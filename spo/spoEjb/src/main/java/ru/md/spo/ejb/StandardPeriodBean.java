package ru.md.spo.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import com.vtb.util.CollectionUtils;
import org.apache.crimson.tree.XmlDocument;
import org.hibernate.lob.SerializableBlob;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vtb.domain.StandardPeriod;
import com.vtb.exception.FactoryException;
import com.vtb.util.Formatter;

import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.compendium.CompendiumService;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IStandardPeriodService;
import ru.masterdm.spo.utils.CollectStages;
import ru.masterdm.spo.utils.SBeanLocator;

import ru.md.domain.AuditDurationStage;
import ru.md.domain.AuditDurationTasksHistory;
import ru.md.domain.TaskComment;
import ru.md.domain.User;
import ru.md.persistence.ReportMapper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.pup.dbobjects.StageJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.RequestLogJPA;
import ru.md.spo.dbobjects.StandardPeriodChangeJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.dbobjects.StandardPeriodValueJPA;
import ru.md.spo.dbobjects.StandardPeriodVersionJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.report.EventString;

@Stateless
public class StandardPeriodBean implements StandardPeriodBeanLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(StandardPeriodBean.class.getName());
	@PersistenceUnit(unitName = "flexWorkflowEJBJPA")
	private EntityManagerFactory factory;

	@EJB
	private PupFacadeLocal pupFacade;

	@EJB
	private TaskFacadeLocal taskFacade;

	@Resource
	TimerService timerService;

	@Resource
	SessionContext cnx;

	private static final String TIMER = "timer.recalculateDeadline";
	private static final String TIMER_AUDITCLIENTREPORT = "timer.AuditClientReport";

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ArrayList<StandardPeriod> getStandartPeriodReport(Long mdtaskid)
			throws Exception {
		TaskJPA task = taskFacade.getTask(mdtaskid);
		return getStandartPeriodReport(task, false);
	}

	/**
	 * по дате старта и количеству дней срока возвратит до какого дата\время
	 * нужно уложиться. То есть вернет 9:00 того рабочего дня, когда заявка
	 * будет просрочена
	 *
	 * @param from
	 *            date from
	 * @param interval
	 *            промежуток времени
	 * @return требуемую дату. Null, если неверно.
	 */
	private Date getDeadLineDate(Date from, Integer interval) {
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
				.getActionProcessor("Compendium");
		return compenduim.findDeadlineDate(true, from, interval);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ArrayList<StandardPeriod> getStandartPeriodReportByNumber(Long mdtaskNumber, boolean onlyExpertise)
			throws Exception {
		TaskJPA task = taskFacade.getTaskByNumber(mdtaskNumber);
		return getStandartPeriodReport(task, onlyExpertise);
	}

	private ArrayList<StandardPeriod> getStandartPeriodReport(TaskJPA task, boolean onlyExpertise) throws Exception {
		long tstart = System.currentTimeMillis();
        IStandardPeriodService standardPeriodService = SBeanLocator.singleton().getStandardPeriodService();

		if (task.getProcess() == null) {
			String msg = "к этой заявке не применим контроль сроков. Она идёт вне бизнесс процесса.";
			LOGGER.warn(msg);
			throw new Exception(msg);
		}

        StandardPeriodVersionJPA version = task.getActiveStandardPeriodVersion();

		ArrayList<StandardPeriod> res = new ArrayList<StandardPeriod>();
		for (StandardPeriodGroupJPA group : version.getStandardPeriodGroups()) {
			// для экспертиз пропускать лишние
			if (onlyExpertise && !group.isExpertGroup())
				continue;
			// рассчитать итерации
			ArrayList<ArrayList<TaskInfoJPA>> iter = getIterations(task, group);
			if (iter.size() == 0) {
				res.add(new StandardPeriod(group.getId(), group.getName(),
                        standardPeriodService.getStandardPeriodValue(task.getId(),group.getName(), null, null),
                        getStandardPeriodCriteria(task, group, null), task.getId()));
				continue;
			}

			for (int i = 0; i < iter.size(); i++) {
				ArrayList<TaskInfoJPA> taskList = iter.get(i);
				String stageName = group.getName();
				if (iter.size() > 1)
					stageName += " - итерация " + String.valueOf(i + 1);
				StandardPeriod sp = new StandardPeriod(group.getId(), stageName,
                        standardPeriodService.getStandardPeriodValue(task.getId(),group.getName(),
                                getStartDate(taskList),getEndDate(taskList)),
                        getStandardPeriodCriteria(task, group, taskList), task.getId());
				sp.setOperationName(new ArrayList<String>());
				for (StageJPA stage : group.getStages()) {
					sp.getOperationName().add(stage.getDescription());
				}
				sp.setStart(getStartDate(taskList));
				if (sp.getStart() == null) {
					res.add(sp);
					continue;
				}

				if (sp.getStart() != null) {
					sp.setFinish(getEndDate(taskList));
					Long fact = sp.getFinish() == null ? null : ServiceFactory.getService(CompendiumService.class).getInterval(sp.getStart(), sp.getFinish());
					if (fact != null) {
						if (fact < 1)
							fact = 0L;
						sp.setFactPeriod(fact);
					}
					sp.setUser(getActiveUsers4StPerStageWithRoles(taskList));
				}
				sp.setCanEditPeriod(hasActiveStage(taskList) && sp.getStart() != null);
				if (sp.isCanEditPeriod()) {
					sp.setCanEditPeriod(!isSelectedStandardPeriodValueReadonly(task, group));
				}
				if (sp.getStandardPeriod() != null && sp.getStart() != null) {
					sp.setDeadline(getDeadLineDate(sp.getStart(), sp.getStandardPeriod().intValue()));
				}

                if (onlyExpertise)
				    sp.setEventsHistory(getEventsHistory(task, getActiveUsers4StPerStage(task, group), sp
							.getStart(), sp.getFinish()));

				ArrayList<String> changeHistory = new ArrayList<String>();
                String groupName = group.getName();
				StandardPeriodChangeJPA prev = null;
				for (StandardPeriodChangeJPA defValue : task.getStandardPeriodDefined())
					try {
						if (defValue.getGroup().getName().equals(groupName)
								&& defValue.getWhoChange() != null
								&& (defValue.getWhenChange() == null || defValue.getWhenChange().before(sp.getStart()))
								&& (prev == null || defValue.getWhenChange() == null
										|| prev.getWhenChange() == null || defValue.getWhenChange().after(prev.getWhenChange())))
							prev = defValue;
					}
					catch (Exception e) {
						// LOGGER.warn(e.getMessage(), e);
					}
				for (StandardPeriodChangeJPA defValue : task.getStandardPeriodDefined()) {
					try {
						if (defValue.getGroup().getName().equals(groupName)
								&& defValue.getWhoChange() != null
								&& (defValue.getWhenChange() == null || defValue.getWhenChange().after(sp.getStart()))
								&& (defValue.getWhenChange() == null || sp.getFinish() == null || defValue.getWhenChange().before(sp.getFinish()))) {
							String s = "\n\nНормативный срок ";
							if (prev != null)
								s += prev.getPeriod() + " раб.дн. ";
							if (prev != null && prev.getValue() != null && prev.getValue().getName() != null
									&& !prev.getValue().getName().isEmpty())
								s += "(" + prev.getValue().getName() + ")";
							s += " изменил пользователь " + defValue.getWhoChange().getFullName() + " "
									+ Formatter.formatDateTime(defValue.getWhenChange()) + " \nКомментарий: "
									+ defValue.getChangeComment();
							changeHistory.add(s);
							prev = defValue;
						}
					}
					catch (Exception e) {
						LOGGER.warn(e.getMessage(), e);
					}
				}
				sp.setChangeHistory(changeHistory);

                //Это нужно только для отчёта экспертиз
				/*TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
						.getActionProcessor("Task");
				Task taskJDBC = processor.getTask(new Task(task.getId()));
				ArrayList<String> comments = new ArrayList<String>();
				for (Comment cmnt : taskJDBC.getComment()) {
					if (cmnt.getStageid() == null || sp.getStart() == null)
						continue;
					for (StageJPA stage : group.getStages()) {
						if (stage.getIdStage().longValue() == cmnt.getStageid().longValue()
								&& (cmnt.getWhen() == null || cmnt.getWhen().after(sp.getStart()))
								&& (cmnt.getWhen() == null || sp.getFinish() == null || cmnt.getWhen().before(
										sp.getFinish()))) {
							comments.add(cmnt.getAuthor().getName() + "\n" + cmnt.getBody() + "\n"
									+ Formatter.formatDateTime(cmnt.getWhen()));
							sp.setLastCommentText(sp.getLastCommentText() + cmnt.getBody() + "\n\n");
							try {
								UserJPA user = pupFacade.getUser(cmnt.getAuthor().getId().longValue());
								sp.setLastCommentAuthor(sp.getLastCommentAuthor() + user.getFullName() + "\n\n");
								sp.setLastCommentAuthorDepartment(sp.getLastCommentAuthorDepartment()
										+ user.getDepartment().getShortName() + "\n\n");
							}
							catch (Exception e) {
								LOGGER.error(e.getMessage(), e);
							}
						}
					}
				}
				sp.setComments(comments);*/

				res.add(sp);
			}
		}
		LOGGER.warn("*** getAuditStandartPeriodReport.getStandartPeriodReport time " + (System.currentTimeMillis() - tstart));
		return res;
	}

	/**
	 * Fills request field of the report
	 * @param task
	 * @return
	 */
	private ArrayList<String> getEventsHistory(TaskJPA task, Set<UserJPA> authors, Date startDate,
			Date endDate) {
		ArrayList<EventString> events = new ArrayList<EventString>();
		List<RequestLogJPA> logs = taskFacade.getRequestLogList(task.getId());
		for (RequestLogJPA log : logs) {
			if (startDate != null && startDate.after(log.getDate()) || endDate != null
					&& endDate.before(log.getDate()))
				continue;
			if (authors.contains(log.getFrom())) {
				String event = "Пользователю(-ям): " + recepientsToString(log.getRecepients())
						+ " направлен запрос от пользователя ";
				event += log.getFrom().getFullName() + ". Содержание запроса: " + log.getBody() + " \n"
						+ Formatter.formatDateTime(log.getDate());
				events.add(new EventString(event, log.getDate()));
			}
		}

		/**
		 * Пользователь Князева Марина Алексеевна добавил документ «без группы» к заявке: Документ.doc.
		 * 29.09.2012 14:00
		 */
		// список документов
		List<AttachJPA> documents = pupFacade.findAttachemntByOwnerAndType(task.getProcess().getId()
				.toString(), 0L);
		for (AttachJPA doc : documents) {
			if (startDate != null && startDate.after(doc.getDATE_OF_ADDITION()) || endDate != null
					&& endDate.before(doc.getDATE_OF_ADDITION()))
				continue;
			String event = "";
			if (doc.getWhoAdd() != null)
				event = "Пользователь " + doc.getWhoAdd().getFullName() + " добавил документ '"
						+ doc.getFILETYPE() + "' к заявке: " + doc.getFILENAME() + " "
						+ Formatter.formatDateTime(doc.getDATE_OF_ADDITION());
			events.add(new EventString(event, doc.getDATE_OF_ADDITION()));
		}
		Collections.sort(events);
		ArrayList<String> res = new ArrayList<String>();
		for (EventString es : events)
			res.add(es.getMessage());
		return res;
	}

	/**
	 * Преобразуем список получателей в строку через запятую
	 * @param recepients список получателей
	 * @return список получателей в строку через запятую
	 */
	private String recepientsToString(List<UserJPA> recepients) {
		if (recepients == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < recepients.size(); i++) {
			sb.append(recepients.get(i).getFullName());
			if (i < recepients.size() - 1)
				sb.append(", ");
		}
		return sb.toString();
	}

	private Set<UserJPA> getActiveUsers4StPerStage(TaskJPA task, StandardPeriodGroupJPA group) {
		EntityManager em = factory.createEntityManager();
		Set<UserJPA> users = new HashSet<UserJPA>();
		Query q = em.createNativeQuery("select t.id_user,t.id_stage_to from tasks t "
				+ "inner join r_stage_standardgroup r on r.id_stage=t.id_stage_to "
				+ "where t.id_process=? and r.id_spg=? and t.id_user is not null");
		q.setParameter(1, task.getProcess().getId());
		q.setParameter(2, group.getId());
		@SuppressWarnings("unchecked")
		ArrayList<Object[]> list = (ArrayList<Object[]>) q.getResultList();
		for (Object[] params : list) {
			Long userid = ((BigDecimal) params[0]).longValue();
			users.add(em.find(UserJPA.class, userid));
		}
		return users;
	}

	private ArrayList<String> getActiveUsers4StPerStageWithRoles(ArrayList<TaskInfoJPA> taskList) {
        ru.md.persistence.UserMapper userMapper = (ru.md.persistence.UserMapper)SBeanLocator.singleton().getBean("userMapper");
		Map<Long, Set<Long>> users = new HashMap<Long, Set<Long>>();
		for (TaskInfoJPA ti : taskList) {
			if (ti.getExecutor() == null)
				continue;
			if (!users.containsKey(ti.getExecutor().getIdUser()))
				users.put(ti.getExecutor().getIdUser(), new HashSet<Long>());
			users.get(ti.getExecutor().getIdUser()).add(ti.getStage().getIdStage());
		}
        ArrayList<String> spusers = new ArrayList<String>();
		for (Long userid : users.keySet()) {
            User user = userMapper.getUserById(userid);
			String username = user.getFullName();
            HashSet<String> roles = new HashSet<String>();
			for (Long stageid : users.get(userid)) {
                for(String roleName : userMapper.userRolesStage(userid,stageid))
                    roles.add(roleName);
			}
            String userRoles = CollectionUtils.hashSetJoin(roles);
			spusers.add(username + (userRoles.isEmpty()?"":" (" + userRoles + ") "));
		}
		return spusers;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void changeStandardPeriod(Long mdtaskid, Long userid, String cmnt, Long valueid,
			Long days, Long grid) {
		EntityManager em = factory.createEntityManager();
		TaskJPA task = em.find(TaskJPA.class, mdtaskid);
		StandardPeriodChangeJPA change = new StandardPeriodChangeJPA();
		change.setTask(task);
		if (valueid != null) {
			change.setValue(em.find(StandardPeriodValueJPA.class, valueid));
		}
		change.setChangeComment(cmnt);
		change.setWhenChange(new Date());
		change.setWhoChange(em.find(UserJPA.class, userid));
		change.setDays(days);
		if (grid != null) {
			change.setGroup(em.find(StandardPeriodGroupJPA.class, grid));
		}
		em.persist(change);
		task.getStandardPeriodDefined().add(change);
		em.merge(task);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@SuppressWarnings("unchecked")
	public void recalculateDeadline(Long pupid) throws FactoryException {
		EntityManager em = factory.createEntityManager();
		if (pupid == null) {
			/*
			 * Query query = em.createQuery("SELECT u FROM ProcessJPA u where u.idStatus=1");
			 * for(ProcessJPA process : (List<ProcessJPA>)query.getResultList()){
			 * recalculateDeadline(process.getId()); }
			 */
			return;
		}
		TaskJPA task = taskFacade.getTaskByPupID(pupid);
		if (task == null || task.getActiveStandardPeriodVersion() == null)
			return;
		for (StandardPeriodGroupJPA group : task.getActiveStandardPeriodVersion().getStandardPeriodGroups()) {
			// рассчитать итерации
			ArrayList<ArrayList<TaskInfoJPA>> iter = getIterations(task, group);
			for (int i = 0; i < iter.size(); i++) {
				ArrayList<TaskInfoJPA> taskList = iter.get(i);
				Date startDate = getStartDate(taskList);
                IStandardPeriodService standardPeriodService = SBeanLocator.singleton().getStandardPeriodService();
                Long value = standardPeriodService.getStandardPeriodValue(task.getId(),
                        group.getName(), startDate, null);
                if (startDate != null)
					try {
						Date deadline = ServiceFactory.getService(CompendiumService.class).getDeadLineDate(true, startDate, value == null ? null
								: value.intValue());
						for (TaskInfoJPA ti : taskList) {
							ti.setPlanCompletionDate(deadline);
							em.merge(ti);
						}
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String saveStandardPeriod(HttpServletRequest request) throws FactoryException {
		EntityManager em = factory.createEntityManager();
		ProcessTypeJPA pt = em.find(ProcessTypeJPA.class, Long.valueOf(request
				.getParameter("idProcessType")));
		StandardPeriodVersionJPA v = new StandardPeriodVersionJPA();
		v.setProcessType(pt);
		v.setDate(new Date());
		pt.getStandardPeriodVersions().add(v);
		em.persist(v);
		em.merge(pt);

		v.setStandardPeriodGroups(new ArrayList<StandardPeriodGroupJPA>());
		Set<String> params = request.getParameterMap().keySet();
		ArrayList<Long> groupIds = new ArrayList<Long>();
		for (String grparam : params) {
			if (grparam.startsWith("group_name_")) {
				String groupId = grparam.substring(11);
				groupIds.add(Long.valueOf(groupId));
			}
		}
		Collections.sort(groupIds);

		Pattern patternStage = Pattern.compile("stage_.*_(.*)");
		// ищем все новые группы
		for (Long groupId : groupIds) {
			StandardPeriodGroupJPA group = new StandardPeriodGroupJPA();
			group.setName(request.getParameter("group_name_" + groupId));
			group.setVersion(v);

			group.setDecisionStages(new ArrayList<StageJPA>());
			if (request.getParameter("decision_id_" + groupId) != null)
				for (String sparam : request.getParameterValues("decision_id_" + groupId)) {
					group.getDecisionStages().add(em.find(StageJPA.class, Long.valueOf(sparam)));
				}

			group.setStages(new ArrayList<StageJPA>());
			for (String stparam : params) {
				if (stparam.startsWith("stage_" + groupId + "_")) {
					Matcher m = patternStage.matcher(stparam);
					m.find();
					String stageid = m.group(1);
					group.getStages().add(em.find(StageJPA.class, Long.valueOf(stageid)));
				}
			}
			v.getStandardPeriodGroups().add(group);
			em.persist(group);
			em.merge(v);

			group.setValues(new HashSet<StandardPeriodValueJPA>());
			if (request.getParameter("period_value_" + groupId) != null) {
				for (int i = 0; i < request.getParameterValues("period_value_" + groupId).length; i++) {
					StandardPeriodValueJPA value = new StandardPeriodValueJPA();
					value.setGroup(group);
					value.setName(request.getParameterValues("name_value_" + groupId)[i]);
					value.setPeriod(Long.valueOf(request.getParameterValues("period_value_" + groupId)[i]));
					String valueId = request.getParameterValues("value_" + groupId)[i];
					String readonly = request.getParameter("readonly_" + valueId);
					if (readonly == null)
						readonly = "n";
					value.setReadonly(readonly);
					group.getValues().add(value);
					em.persist(value);
				}
				em.merge(group);
			}
		}
		em.flush();
		return v.getId().toString();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public HashSet<Long> getCollectStages(StageJPA stage) throws Exception {
		EntityManager em = factory.createEntityManager();
		Query q = em.createNativeQuery("select tp.schema_var from type_process tp where tp.id_type_process=?");
		q.setParameter(1, stage.getId_type_process());
		SerializableBlob blob = (SerializableBlob) q.getSingleResult();
		@SuppressWarnings("deprecation")
		Document schema = XmlDocument.createXmlDocument(blob.getBinaryStream(), false);

		Query query = em.createQuery("SELECT u FROM StageJPA u where u.id_type_process = :idTypeProcess and u.description= :name");
		query.setParameter("idTypeProcess", stage.getId_type_process());
		NodeList stagesTagList = schema.getElementsByTagName("state");
		for (int i = 0; i < stagesTagList.getLength(); i++) {
			Node stageNode = stagesTagList.item(i);
			String name = stageNode.getAttributes().getNamedItem("name").getNodeValue().trim();
			if (name.equals(stage.getDescription().trim())) {
				/*
				 * Найдем первое ветвление от этапа По договоренности, на операции сбора все ветвления ждут
				 * одинаковых операций
				 */
				for (int stageNodeChildIndex = 0; stageNodeChildIndex < stageNode.getChildNodes()
						.getLength(); stageNodeChildIndex++) {
					Node tr = stageNode.getChildNodes().item(stageNodeChildIndex);
					if (tr instanceof Element && tr.getNodeName().equals("transition")) {
						HashSet<Long> set = new HashSet<Long>();
						// список этапов которые должны собраться на текущем этапе
						for (int trChildIndex = 0; trChildIndex < tr.getChildNodes().getLength(); trChildIndex++) {
							Node collect = tr.getChildNodes().item(trChildIndex);
							if (collect instanceof Element && collect.getNodeName().equals("collect")) {
								String nameStageCollect = collect.getAttributes().getNamedItem("name")
										.getNodeValue();
								query.setParameter("name", nameStageCollect);
								@SuppressWarnings("unchecked")
								List<StageJPA> stages = query.getResultList();
								for (StageJPA s : stages)
									set.add(s.getIdStage());
							}
						}
						return set;
					}
				}
			}

		}
		return new HashSet<Long>();
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Date calculateStartDateCollect(Long stage_id, Date start, Date end, Long processId) {
		Date res = start;
		if (stage_id == null)
			return start;
		StageJPA firstStage = pupFacade.getStage(stage_id);
		HashSet<Long> collectStages = new HashSet<Long>();
		if (CollectStages.singleton().cache.containsKey(stage_id))
			collectStages = CollectStages.singleton().cache.get(stage_id);
		else
			try {
				collectStages = getCollectStages(firstStage);
				CollectStages.singleton().cache.put(stage_id, collectStages);
			}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (collectStages.size() > 0) {
			// список задач по этапам ожидания
			Set<TaskInfoJPA> collectTasks = new HashSet<TaskInfoJPA>();
			for (TaskInfoJPA ti : pupFacade.getProcessById(processId).getTasks())
				if (collectStages.contains(ti.getStage().getIdStage()))
					collectTasks.add(ti);
			// Если первая операция не завершена, и есть активные операции, которых ждем, то даты старта
			// нет.
			if (end == null)
				for (TaskInfoJPA ti : collectTasks)
					if (ti.getEndDate() == null)
						return null;
			// дата старта - это самая поздняя дата завершения операции, которых ждем. Такая, что эта дата
			// больше даты начала первой операции и меньше даты окончания (если есть дата окончания)
			for (TaskInfoJPA ti : collectTasks)
				if (ti.getEndDate() != null && ti.getEndDate().after(res)
						&& (end == null || end.after(ti.getEndDate()))
						&& start.before(ti.getEndDate()))
					res = ti.getEndDate();
		}
		return res;
	}

	private Date maxDate(Date d1, Date d2){
		if(d1 == null)
			return d2;
		if(d2 == null)
			return d1;
		return d1.after(d2)?d1:d2;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<AuditDurationStage> getAuditDurationStagesReport(Date from, Date to, Long processType) {
		DateTime toReport = new DateTime(to).plusDays(1);
		ReportMapper mapper = (ReportMapper) SBeanLocator.singleton().getBean("reportMapper");

		List<AuditDurationStage> stages = mapper.findAuditDurationStage(processType, from, toReport.toDate());
		List<AuditDurationStage> operations = new ArrayList<AuditDurationStage>();
		for(AuditDurationStage stage : stages) {
			try {
				List<AuditDurationTasksHistory> list = mapper.getAuditDurationTasksHistory(stage.getIdPupProcess(), stage.getIdSpg());
				List<AuditDurationTasksHistory> iterations = new ArrayList<AuditDurationTasksHistory>();
				AuditDurationTasksHistory iter = null;
				List<AuditDurationTasksHistory> iterTasks = null;
				for (AuditDurationTasksHistory h : list) {
					if (iter == null) {
						iter = new AuditDurationTasksHistory(h);
						iterTasks = new ArrayList<AuditDurationTasksHistory>();
						iterTasks.add(h);
					} else {
						//ищем по всем операциям итерации совпадение дат
						boolean current = false;
						for(AuditDurationTasksHistory iterTask : iterTasks)
							if (dataEq(iterTask.getEn(), h.getSt()) || dataEq(iterTask.getSt(),h.getSt()))
								current = true;
						if (!current) {//новая итерация
							iterations.add(iter);
							iter = new AuditDurationTasksHistory(h);
							iterTasks = new ArrayList<AuditDurationTasksHistory>();
							iterTasks.add(h);
						} else {
							if (h.getEn() == null || iter.getEn() == null)
								iter.setEn(null);
							else
								iter.setEn(maxDate(h.getEn(), iter.getEn()));
							iterTasks.add(h);
						}
					}
				}
				iterations.add(iter);
				int i = 1;
				for (AuditDurationTasksHistory it : iterations) {
                    if (it == null)
                        continue;
					AuditDurationStage iterAndStage = new AuditDurationStage(stage);
					iterAndStage.setStageEnd(it.getEn());
					iterAndStage.setStageStart(it.getSt());
					iterAndStage.setStageStart(calculateStartDateCollect(it.getIdStageTo(), it.getSt(),
																				 it.getEn(), stage.getIdPupProcess()));
					if (iterations.size() > 1)
						iterAndStage.setStageIter(" - итерация " + (i++));
					if(iterAndStage.getStageStart()==null || iterAndStage.getStageStart().after(toReport.toDate()))
						continue;
					if(iterAndStage.getStageStart().before(from))
						continue;
					operations.add(iterAndStage);
				}
			} catch (Exception e1) {
				LOGGER.error(e1.getMessage(), e1);
				//operations.add(s);
			}
		}
        for (AuditDurationStage a : operations) {
            //данные проектной команды
            a.setClientManager(pupFacade.whoAssignedAs("Клиентский менеджер", a.getIdPupProcess()));
            a.setProguctManager(pupFacade.whoAssignedAs("Продуктовый менеджер", a.getIdPupProcess()));
            a.setStructurator(pupFacade.whoAssignedAs("Структуратор", a.getIdPupProcess()));
            a.setAnalist(pupFacade.whoAssignedAs("Кредитный аналитик", a.getIdPupProcess()));
            //Норматив
            IStandardPeriodService standardPeriodService = SBeanLocator.singleton().getStandardPeriodService();
            Long period = standardPeriodService.getStandardPeriodValue(a.getIdMdtask(), a.getStageName(), a.getStageStart(), a.getStageEnd());
            a.setPeriod(period);
            //Комментарии
            ReportMapper reportMapper = (ReportMapper) SBeanLocator.singleton().getBean("reportMapper");
            List<TaskComment> tcList = reportMapper.getLastTaskComment(a.getIdSpg(), a.getIdMdtask(), a.getStageStart(), a.getStageEnd());
            //Комментарии по стадии
            ArrayList<String> val = new ArrayList<String>();
            for (TaskComment tc : tcList) val.add(tc.getText());
            a.setCmnt(CollectionUtils.listJoinVbar(val));
            //ФИО разместившего комментарий
            val = new ArrayList<String>();
            for (TaskComment tc : tcList) val.add(tc.getAuthor());
            a.setCmntUser(CollectionUtils.listJoinVbar(val));
            //Подразделение разместившего комментарий
            val = new ArrayList<String>();
            for (TaskComment tc : tcList) val.add(tc.getDepname());
            a.setCmntDep(CollectionUtils.listJoinVbar(val));
        }
		return operations;
	}

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generateAuditClientReport() {
        Date reportStartDate = ru.masterdm.spo.utils.Formatter.parseDate("01.01.2008");
        ReportMapper mapper = (ReportMapper) SBeanLocator.singleton().getBean("reportMapper");
        //должен формироваться по всем заявкам по всем бизнес процессам из списка:
        // КГО, КГОС за МО, PL, ИУ, ИУКГО, ИУКГОС за МО
        Set<String> processes = CollectionUtils.set("Крупный бизнес ГО", "Крупный бизнес ГО (Структуратор за МО)",
                                                    "Изменение условий","Изменение условий Крупный бизнес ГО",
                                                    "Изменение условий Крупный бизнес ГО (Структуратор за МО)");
        for (ProcessTypeJPA pt : pupFacade.findProcessTypeList())
            if (processes.contains(pt.getDescriptionProcess()))
                for (AuditDurationStage a : getAuditDurationStagesReport(reportStartDate, new Date(), pt.getIdTypeProcess()))
				    mapper.insertAuditClientReport(a);
    }

    /** очистить таблицу spo_standard_period_report */
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void clearAuditClientReport() {
        EntityManager em = factory.createEntityManager();

        em.createNativeQuery("delete from spo_standard_period_report").executeUpdate();
    }

    @SuppressWarnings("rawtypes")
	private Date getStartDate(ArrayList<TaskInfoJPA> taskList) {
        if (taskList == null)
            return null;
		TaskInfoJPA firstTask = null;
		for (TaskInfoJPA ti : taskList) {
			if (firstTask == null || ti.getStartDate() != null
					&& ti.getStartDate().before(firstTask.getStartDate()))
				firstTask = ti;
		}

		if (firstTask == null)
			return null;
		// дата, которая считается началом этапа. Приостановка заявки может её сместить
		Date res = calculateStartDateCollect(firstTask.getStage().getIdStage(), firstTask.getStartDate(),
				getEndDate(taskList), firstTask.getProcess().getId());
		if (res == null)
			return null;//тогда не смотрим приостановку, бережём оракл

		// учитывать приостановку заявки
		EntityManager em = factory.createEntityManager();
		Query q = em.createNativeQuery("select e.date_event from process_events e "
				+ "where e.id_process_type_event=9 and e.id_process=?");
		q.setParameter(1, taskList.get(0).getProcess().getId());
		List list = q.getResultList();
		if (list.size() == 0)
			return res;
		Date finish = getEndDate(taskList);
		for (Object resumeObj : list) {
			Date resume = (Date) resumeObj;
			if (resume.after(res) && (finish == null || finish.after(resume))) {
				res = resume;
			}
		}
		return res;
	}

	private Date getEndDate(ArrayList<TaskInfoJPA> taskList) {
		if (taskList == null)
			return null;
		for (TaskInfoJPA ti : taskList) {
			if (ti.getIdStatus() < 3)
				return null;// итерация не завершена
		}
		Date endDate = null;
		for (TaskInfoJPA ti : taskList) {
			if (endDate == null || ti.getEndDate() != null && ti.getEndDate().after(endDate))
				endDate = ti.getEndDate();
		}
		return endDate;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public StandardPeriodGroupJPA getStandardPeriodGroup(Long id) {
		EntityManager em = factory.createEntityManager();
		return em.find(StandardPeriodGroupJPA.class, id);
	}

	/**
	 * проверяет есть ли активные операции по заявке для этого этапа. От этого зависит можно ли
	 * редактировать нормативный срок.
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private boolean hasActiveStage(ArrayList<TaskInfoJPA> taskList) {
		for (TaskInfoJPA ti : taskList) {
			if (ti.getIdStatus() < 3)
				return true;
		}
		return false;
	}

	/**
	 * Возвращает список задач, сгруппированных по итерациям и отсортированных по дате начала
	 * итерации.
	 */
	private ArrayList<ArrayList<TaskInfoJPA>> getIterations(TaskJPA task, StandardPeriodGroupJPA group) {
		long tstart = System.currentTimeMillis();
		ArrayList<ArrayList<TaskInfoJPA>> res = new ArrayList<ArrayList<TaskInfoJPA>>();
		ArrayList<TaskInfoJPA> buf = findTask4group(task, group);// пулл задач, откуда будем брать для
																															// группировки
		while (buf.size() > 0) {
			ArrayList<TaskInfoJPA> iter = new ArrayList<TaskInfoJPA>();
			TaskInfoJPA ti = buf.get(0);
			buf.remove(ti);
			iter.add(ti);
			while (pullNextIterItem(buf, iter)) {}
			res.add(iter);
		}
		//LOGGER.warn("*** getAuditStandartPeriodReport.getIterations " + task.getId()
		//		+ " time " + (System.currentTimeMillis()-tstart));
		return res;
	}

	private ArrayList<TaskInfoJPA> findTask4group(TaskJPA task, StandardPeriodGroupJPA group) {
		ArrayList<TaskInfoJPA> buf = new ArrayList<TaskInfoJPA>();// пулл задач, откуда будем брать для
																															// группировки
		EntityManager em = factory.createEntityManager();
		Query query = em
				.createNativeQuery("select t.ID_TASK from tasks t inner join r_stage_standardgroup g on g.id_stage=t.id_stage_to "
						+ "where t.id_process=:idprocess and g.id_spg=:idgroup order by t.ID_TASK asc");
		query.setParameter("idprocess", task.getProcess().getId());
		query.setParameter("idgroup", group.getId());
		for (Object obj : query.getResultList()) {
			Long id = ((BigDecimal) obj).longValue();
			buf.add(em.find(TaskInfoJPA.class, id));
		}
		return buf;
	}

	private boolean pullNextIterItem(ArrayList<TaskInfoJPA> buf, ArrayList<TaskInfoJPA> iter) {
		for (TaskInfoJPA inIter : iter) {
			for (TaskInfoJPA pret : buf) {
				// ищем задачи, которые начались после завершения одной из задач итерации. Или задача
				// итерации началась по завершении этой.
				if (dataEq(pret.getEndDate(), inIter.getStartDate())
						|| dataEq(inIter.getEndDate(), pret.getStartDate())
						|| dataEq(inIter.getStartDate(), pret.getStartDate())) {
					iter.add(pret);
					buf.remove(pret);
					return true;
				}
				// ищем задачи, которые начались во время выполнения задачи итерации. Или наоборот
				if (dataAfter(pret.getStartDate(), inIter.getStartDate())
						&& (inIter.getEndDate() == null || dataAfter(inIter.getEndDate(), pret.getStartDate()))
						|| dataAfter(inIter.getStartDate(), pret.getStartDate())
						&& (pret.getEndDate() == null || dataAfter(pret.getEndDate(), inIter.getStartDate()))) {
					iter.add(pret);
					buf.remove(pret);
					return true;
				}
			}
		}
		return false;
	}

	private boolean dataAfter(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			return false;
		return d1.after(d2);
	}

	private boolean dataEq(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			return false;
		long delta = d1.getTime() - d2.getTime();
		return delta < 3000L && delta > -3000L;
	}

	/**
	 * Проверяет не выбран ли критерий дифференциации с запретом редактирования.
	 * @param task - заявка
	 * @param group - этап
	 */
	private boolean isSelectedStandardPeriodValueReadonly(TaskJPA task, StandardPeriodGroupJPA group) {
		if (group.getValues().size() == 1 && group.getValues().iterator().next().isReadonly())
			return true;
		boolean value = false;
		Long valueid = 0L;
		for (StandardPeriodChangeJPA defValue : task.getStandardPeriodDefined()) {
			if (defValue.getGroup().equals(group)) {
				if (valueid < defValue.getId()) {
					value = defValue.getValue() != null && defValue.getValue().isReadonly();
					valueid = defValue.getId();
				}
			}
		}
		return value;
	}

	/**
	 * возвращает выбранный критерий дифференциации для этапа и итерации. Или коментарий изменения.
	 * @param task - заявка
	 * @param group - этап
	 */
	private String getStandardPeriodCriteria(TaskJPA task, StandardPeriodGroupJPA group,
			ArrayList<TaskInfoJPA> taskList) {
        String groupName = group.getName();

		Date start = getStartDate(taskList);
		ReportMapper reportMapper = (ReportMapper) SBeanLocator.singleton().getBean("reportMapper");
        if (start==null)
            start = new Date();
		Long versionid = reportMapper.getActualSPVersion(task.getIdTypeProcess(), start);
        if (versionid == null)
            return "";
		EntityManager em = factory.createEntityManager();
        StandardPeriodVersionJPA version = em.find(StandardPeriodVersionJPA.class, versionid);
        for(StandardPeriodGroupJPA gr : version.getStandardPeriodGroups())
            if (gr.getName().equals(groupName) && gr.getValues().size() == 1)
                return gr.getValues().iterator().next().getName();

		String criteria = "";
		Long valueid = 0L;
		for (StandardPeriodChangeJPA defValue : task.getStandardPeriodDefined()) {
			if (defValue.getGroup().getName().equals(groupName) && (defValue.getWhenChange() == null
					|| getEndDate(taskList) == null// итерация не завершена. Тогда берем последнее изменение
			|| defValue.getWhenChange().before(getEndDate(taskList)))) {
				if (valueid < defValue.getId()) {
					criteria = defValue.getCriteria();
					valueid = defValue.getId();
				}
			}
		}
		return criteria;
	}

	@Override
	public void startTimer() {
		try {
			// clear old running timers
			for (Object obj : timerService.getTimers()) {
				Timer timer = (Timer) obj;
				String typeOfTimer = (String) timer.getInfo();
				if (typeOfTimer != null &&
                        (typeOfTimer.equals(TIMER) || typeOfTimer.equals(TIMER_AUDITCLIENTREPORT)) ) {
					LOGGER.info("Таймер " + typeOfTimer + " отключен!");
					timer.cancel();
				}
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("таймер " + TIMER + " запускается");
		LOGGER.info("таймер " + TIMER_AUDITCLIENTREPORT + " запускается");

		// запускаем по расписанию
		try {
			final long hours4 = 4 * 60 * 60 * 1000;
			timerService.createTimer(new Date(), hours4, TIMER);

            final long hours24 = 24 * 60 * 60 * 1000; // 24 hours in milliseconds.
            Calendar scheduledDateTime = Calendar.getInstance();
            scheduledDateTime.set(Calendar.HOUR_OF_DAY, 1);
            scheduledDateTime.set(Calendar.MINUTE, 10);
            scheduledDateTime.set(Calendar.SECOND, 0);
            scheduledDateTime.add(Calendar.DATE, 1);
            timerService.createTimer(scheduledDateTime.getTime(), hours24, TIMER_AUDITCLIENTREPORT);
		}
		catch (Exception e) {
			LOGGER.error("Couldn't start timer " + e.getMessage());
		}
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onTimeout(Timer timer) {
		try {
			String typeOfTimer = (String) timer.getInfo();
			if (TIMER.equals(typeOfTimer))
				recalculateDeadline(null);
            if (TIMER_AUDITCLIENTREPORT.equals(typeOfTimer)) {
                StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
                spLocal.clearAuditClientReport();
                spLocal.generateAuditClientReport();
				IDashboardService dashboardService = (IDashboardService) SBeanLocator.singleton().getBean("dashboardService");
				//dashboardService.clearOldClientReport();
				dashboardService.generatePipelineClientReport();
            }
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean isGroupActive(Long groupId, Long idPupProcess) {
		Query query = factory.createEntityManager().createNativeQuery(
				"select count(*) from r_stage_standardgroup r "
						+ "inner join tasks t on r.id_stage=t.id_stage_to "
						+ "where t.id_status<3 and id_spg=:id_spg and t.id_process=:id_process");
		query.setParameter("id_spg", groupId);
		query.setParameter("id_process", idPupProcess);
		return ((BigDecimal) query.getSingleResult()).longValue() > 0;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long getCurrentValueId(Long groupId, Long idPupProcess) {
		TaskJPA task = taskFacade.getTaskByPupID(idPupProcess);

		Long valueid = null;
		Long changeid = 0L;
		for (StandardPeriodChangeJPA defValue : task.getStandardPeriodDefined()) {
			if (defValue.getGroup().getId().equals(groupId)) {
				if (changeid < defValue.getId()) {
					changeid = defValue.getId();
					valueid = defValue.getValue() == null ? null : defValue.getValue().getId();
				}
			}
		}
		return valueid;
	}

}
