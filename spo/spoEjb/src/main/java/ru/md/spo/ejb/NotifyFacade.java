package ru.md.spo.ejb;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.ApplProperties;
import com.vtb.util.CollectionUtils;
import com.vtb.util.Formatter;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.ced.CedService;
import ru.masterdm.integration.ced.ws.CedUser;
import ru.masterdm.integration.compendium.CompendiumService;
import ru.masterdm.integration.mailer.MailerService;
import ru.masterdm.integration.mailer.domain.Message;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.MdTask;
import ru.md.domain.Org;
import ru.md.domain.TaskKz;
import ru.md.domain.dict.InterestRateChange;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.ReportMapper;
import ru.md.persistence.UserMapper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.PauseParamJPA;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.StageJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.MdTaskTO;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.dbobjects.StandardPeriodVersionJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.to.UserMailQueue;
import ru.md.spo.util.Config;
import ru.md.spo.util.NotifyMessageFormat;
import ru.md.spo.util.ResourceLoader;

/**
 * Рассылка уведомлений. Производительность этого бина не критична.
 * @author Andrey Pavlenko
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotifyFacade implements NotifyFacadeLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotifyFacade.class.getName());

	private static final String TIMER_DELINQUENCY_CHECK = "on.delinquency.send.messages";
	@PersistenceUnit(unitName = "flexWorkflowEJBJPA")
	private EntityManagerFactory factory;
	@Resource
	TimerService timerService;

	@EJB
	private PupFacadeLocal pupFacade;

	@EJB
	private TaskFacadeLocal taskFacade;

	@Autowired
    private Configuration templateConf;
    @Autowired
    private MdTaskMapper mdTaskMapper;
    @Autowired
    private UserMapper userMapper;

	@Override
	public void startTimer() {
		try {
			// clear old running timers
			for (Object obj : timerService.getTimers()) {
				Timer timer = (Timer) obj;
				String typeOfTimer = (String) timer.getInfo();
				if (typeOfTimer != null && typeOfTimer.equals(TIMER_DELINQUENCY_CHECK)) {
					LOGGER.info("Таймер " + typeOfTimer + " отключен!");
					timer.cancel();
				}
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("таймер " + TIMER_DELINQUENCY_CHECK + " запускается");

		// запускаем по расписанию
		try {
			final long hours24 = 24 * 60 * 60 * 1000; // 24 hours in milliseconds.
			// по ТЗ рассылка должна происходить в 23:59
			// (за один рабочий день до, через один рабочий день после.
			// Рабочий день по ТЗ начинается в полночь MSK вне зависимости от региона
			Calendar scheduledDateTime = Calendar.getInstance();
			scheduledDateTime.set(Calendar.HOUR_OF_DAY, 23);
			scheduledDateTime.set(Calendar.MINUTE, 59);
			scheduledDateTime.set(Calendar.SECOND, 0);
			/*
			 * if (!scheduledDateTime.after(Calendar.getInstance())) { // today it's too late to start
			 * timer. Start it tomotrrow. scheduledDateTime.add(Calendar.DATE, 1); }
			 */
			timerService.createTimer(scheduledDateTime.getTime(), hours24, TIMER_DELINQUENCY_CHECK);
		}
		catch (Exception e) {
			LOGGER.error("Couldn't start delinquency timer " + e.getMessage());
		}
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onTimeout(Timer timer) {
		try {
			String typeOfTimer = (String) timer.getInfo();
			if (TIMER_DELINQUENCY_CHECK.equals(typeOfTimer))
				doDelinquencyCheck();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void doNotifyNow() {
		try {
			doDelinquencyCheck();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * check and send it as timeout.
	 * @throws FactoryException
	 */
	@SuppressWarnings("unchecked")
	private void doDelinquencyCheck() throws Exception {
		LOGGER.info("timer: start delinquency check ");

		EntityManager em = factory.createEntityManager();
		//Если с даты создания заявки кнопка «Подтверждено Трейдером» не нажималась в течение 30 календарных дней,
		//то Кредитному аналитику, включенному в «Проектную команду» с признаком «Выполнения операции» необходимо
		//раз в деньнаправлять уведомление.
		if(taskFacade.getGlobalSetting("traderApproveEnable").equalsIgnoreCase("true")){
			Query q = em.createNativeQuery(ResourceLoader.getSQL("trader_need_approve"));
			List<Object> list = q.getResultList();
			for(Object obj : list){
				Object[] arr = (Object[]) obj;
				Long id_mdtask = ((BigDecimal) arr[0]).longValue();
				Long id_trader = ((BigDecimal) arr[1]).longValue();
				Long id_status = ((BigDecimal) arr[2]).longValue();//разные уведомления в зависимости от статуса
				//Long version = ((BigDecimal) arr[3]).longValue();
				TaskJPA task = taskFacade.getTask(id_mdtask);
				LOGGER.info("trader_need_approve id_mdtask="+id_mdtask+", id_trader="+id_trader + ", mdtask_number="+task.getNumberDisplay());
				String link = getBaseURL(id_trader)+ "/showTaskList.do?typeList=all&searchNumber="
						+ task.getMdtask_number().toString() + "&projectteam=true&searchHideApproved=n";
				if(id_status.longValue()==4)
					link = getBaseURL(id_trader)+ "/showTaskList.do?typeList=all&searchNumber="
							+ task.getMdtask_number().toString() + "&closed=true";
				String body = "Вы являетесь Кредитным аналитиком по "+getTypeNamePraepositionalis(task.getId())+
                        getAllContractors(task.getId())+" № <a href=\"" +
						link+"\">"+task.getNumberAndVersion()+"</a>. "
						+ "Требуется подтверждение стоимостных параметров заявки ";
				if(id_status.longValue()==1)
					body += "(см. представление «работа проектной команды»).";
				else
					body += "(см. представление «завершенные заявки»).";
				send(id_trader,id_trader,"Подтверждение параметров " + getNameGenitive(task.getId()), body
                        + getDescriptionTask(task.getId()));
			}
		}


		Date now = new Date();
		Calendar tomorrowCal = Calendar.getInstance();
		tomorrowCal.setTime(new Date());
		tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
		Date nextWorkDayBegin = getNextWorkDayBegin();

		if (!isWorkingDay(tomorrowCal.getTime())) {// в выходной не отправляем уведомление
			LOGGER.warn("weekend. Skip notifycation.");
			return;// то есть если сейчас 23:59 пятницы, то наше уведомление никому не нужно
			// а если сейчас 23:59 воскресенья, то отправим уведомления
		}

		// При привышении срока восстановления работ
		try {
			notifyResumeRequest();
		}
		catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}

		// За один рабочий день до даты истечения нормативного срока исполнителю по
		// текущей операции этапа заявки должно направляться уведомление
		notifyExpiredSoon(nextWorkDayBegin);

		// Начиная с рабочего дня, следующего за днем истечения нормативного срока исполнителю
		// по текущей операции этапа заявки и его руководителям направляется уведомление
		notifyExpired(now);
	}

	private boolean isWorkingDay(Date d){
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
				.getActionProcessor("Compendium");
		return !compenduim.isWeekend(d);
	}

	/**
	 * уведомление При привышении срока восстановления работ
	 * @throws MalformedURLException
	 */
	private void notifyResumeRequest() throws Exception {
		Date now = new Date();
		EntityManager em = factory.createEntityManager();

		String sql = "select p.id from pauseParam p where p.id in ( "
				+ "select max(p.id) id from pauseParam p inner join process_events e on e.id_process_event=p.id_process_event "
				+ "inner join processes pr on pr.id_process=e.id_process "
				+ "where e.id_process_type_event=2 and pr.id_status=2 " + "group by e.id_process "
				+ ") and p.dateresume<?";
		Query q = em.createNativeQuery(sql);
		q.setParameter(1, now);
		for (Object objid : q.getResultList()) {
			notifyResumeRequestSendMail(((BigDecimal) objid).longValue(), "notifyResume");
		}

		Calendar theDayAfterTomorrow = Calendar.getInstance();
		theDayAfterTomorrow.setTime(new Date());
		while (!isWorkingDay(theDayAfterTomorrow.getTime()))
			theDayAfterTomorrow.add(Calendar.DAY_OF_YEAR, 1);
		theDayAfterTomorrow.add(Calendar.DAY_OF_YEAR, 2);
		q = em.createNativeQuery(sql + " and p.dateresume>?");
		q.setParameter(1, theDayAfterTomorrow.getTime());
		q.setParameter(2, now);
		for (Object objid : q.getResultList()) {
			notifyResumeRequestSendMail(((BigDecimal) objid).longValue(), "notifyResumeSoon");
		}
	}

	private void notifyResumeRequestSendMail(long pauseid, String messageFormat)
			throws MalformedURLException, FactoryException {
		// если в секции "Проектная команда"
		// по данной заявке признак "выполнение операций" выставлен у:
		EntityManager em = factory.createEntityManager();
		PauseParamJPA p = em.find(PauseParamJPA.class, pauseid);
		TaskJPA task = taskFacade.getTaskByPupID(p.getEvent().getProcess().getId());
		RoleJPA structBoss = pupFacade.getRole("Руководитель структуратора", task.getProcess()
				.getProcessType().getIdTypeProcess());
		Map<String, Object> data = task.toMap();
        data.put("taskname",getNameGenitive(task.getId()));
		data.put("tasktype", task.isProduct()?"сделки":"лимита");
		data.put("expireData", Formatter.formatDateTime(p.getDateresume()));
		Set<UserJPA> toSet = new HashSet<UserJPA>();
		for (ProjectTeamJPA pteam : task.getProjectTeam()) {
			// 2.1. Пользователя с ролью "Структуратор" - уведомления должны направляться данному
			// Структуратору ,
			if (pupFacade.isAssigned(pteam.getUser().getIdUser(), pupFacade.getRole("Структуратор",
					task.getProcess().getProcessType().getIdTypeProcess()).getIdRole(), task.getProcess()
					.getId())) {
				toSet.add(pteam.getUser());
				// и всем Пользователям с ролью "Руководитель структуратора", которые зарегистрированы в
				// подразделении данного Структуратора и в вышестоящих подразделениях.
				DepartmentJPA dep = pteam.getUser().getDepartment();
				while (dep != null) {
					for (Long bossId : pupFacade.findDepartmentUsersInRoles(structBoss.getIdRole(), dep
							.getIdDepartment())) {
						toSet.add(em.find(UserJPA.class, bossId));
					}
					if (dep.getParentDepartmentList().size() == 0) {
						dep = null;
					}
					else {
						dep = dep.getParentDepartmentList().get(0);
					}
				}
			}
			// 2.2. Пользователя с ролью "Руководитель структуратора" - уведомления должны направляться
			// данному Руководителю структуратора
			if (pupFacade.isAssigned(pteam.getUser().getIdUser(), pupFacade.getRole(
					"Руководитель структуратора", task.getProcess().getProcessType().getIdTypeProcess())
					.getIdRole(), task.getProcess().getId())) {
				toSet.add(pteam.getUser());
			}
		}
		for (UserJPA user : toSet) {
			data.put("baseurl", getBaseURL(user.getIdUser()));
			sendMail(data, user, messageFormat, task.getId());
		}
	}

	private void sendMail(Map<String, Object> data, UserJPA to, String format, Long idMdTask) {
		try {
			StringWriter subj = new StringWriter();
			templateConf.getTemplate(format + ".subj.ftl", "utf-8").process(data, subj);
			StringWriter body = new StringWriter();
			templateConf.getTemplate(format + ".body.ftl", "utf-8").process(data, body);
			send(to.getIdUser(), to.getIdUser(), subj.toString(),
					body.toString() + getDescriptionTask(idMdTask));
		}
		catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}

	/**
	 * Уведомить о необходимости назначить
	 * @param taskInfo
	 */
	private void notifyNeed4assign(TaskInfoJPA taskInfo, String fact) throws Exception {
		TaskJPA taskJPA = taskFacade.getTaskByPupID(taskInfo.getProcess().getId());
		Map<String, Object> data = taskJPA.toMap();
        data.put("taskname",getNameGenitive(taskJPA.getId()));
		data.put("tasktype", taskJPA.isProduct()?"сделки":"лимита");
		data.put("fact", fact);
		data.put("expireData", Formatter.formatDateTime(taskInfo.getPlanCompletionDate()));
		data.put("stage", taskInfo.getStage().getDescription());
		data.put("StandardPeriodStage", findStandardPeriodStageName4Stage(taskInfo.getStage(), taskJPA
				.getActiveStandardPeriodVersion()));

		for (UserJPA boss : notifyNeed4assignBossList(taskInfo)) {
			data.put("baseurl", getBaseURL(boss.getIdUser()));
			sendMail(data, boss, "need2assign", taskJPA.getId());
		}
	}
	private Set<UserJPA> notifyNeed4assignBossList(TaskInfoJPA taskInfo) {
		Set<UserJPA> bossList = new HashSet<UserJPA>();
		EntityManager em = factory.createEntityManager();
		if (taskInfo.getIdDepartament() == null)// такого не бывает
			return new HashSet<UserJPA>();
		DepartmentJPA taskDep = em.find(DepartmentJPA.class, taskInfo.getIdDepartament());
		LOGGER.info("notifyNeed4assign: taskDep=" + taskDep.toString() + " stage = "
							+ taskInfo.getStage().toString());
		for (RoleJPA role : taskInfo.getStage().getRoles()) {
			for (UserJPA possibleExecutor : role.getUsers()) {
				if (taskDep.isAncestor(possibleExecutor.getDepartment())) {// подходят права по иерархии
					// подразделений
					for (RoleJPA parentRole : role.getParentRoles()) {
						if (taskInfo.getStage().getRoles().contains(parentRole)) {
							for (UserJPA boss : parentRole.getUsers()) {
								if (possibleExecutor.getDepartment().isAncestor(boss.getDepartment())) {
									// уф, можно уведомлять
									bossList.add(boss);
								}
							}
						}
					}
				}
			}
		}
		return bossList;
	}

	private void sendExpiredMessage(TaskInfoJPA taskInfo, NotifyMessageFormat format,
			TaskJPA taskJPA, UserJPA to) throws MappingException, MalformedURLException {
		sendExpiredMessage(taskInfo, format, taskJPA, to, null);
	}

	private void sendExpiredMessage(TaskInfoJPA taskInfo, NotifyMessageFormat format,
			TaskJPA taskJPA, UserJPA to, UserJPA assigned) throws MappingException, MalformedURLException {
		String stageNameStandardPeriod = findStandardPeriodStageName4Stage(taskInfo.getStage(), taskJPA
				.getActiveStandardPeriodVersion());
		String link = getBaseURL(to.getIdUser()) + "/showTaskList.do?typeList=" + format.getListType()
				+ "&searchNumber=" + taskJPA.getMdtask_number().toString();
		String slave = taskInfo.getExecutor() == null ? "" : taskInfo.getExecutor().getFullName();
		if (assigned != null)
			slave = assigned.getFullName();
		send(to.getIdUser(), to.getIdUser(),
                MessageFormat.format(format.getSubjectFormat(), getNameGenitive(taskJPA.getId()),
                        Formatter.formatDateTime(taskInfo.getPlanCompletionDate())),
                MessageFormat.format(format.getBodyFormat(), taskJPA.getNumberAndVersion(), link,
                        taskJPA.getOrganisation(), Formatter.formatDateTime(taskInfo.getPlanCompletionDate()),
                        taskInfo.getStage().getDescription(), stageNameStandardPeriod, slave,
                        getTypeNamePraepositionalis(taskJPA.getId()),getTypeNameGenitive(taskJPA.getId()))
                        + getDescriptionTask(taskJPA.getId()));
	}
	@Override
	public Date getNextWorkDayBegin() {
		Calendar day = Calendar.getInstance();
		day.set(Calendar.HOUR_OF_DAY, 23);
		day.set(Calendar.MINUTE, 59);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);
		day.add(Calendar.DATE, 1);
		while (!isWorkingDay(day.getTime()))
			day.add(Calendar.DATE, 1);
		return day.getTime();
	}

	private String findStandardPeriodStageName4Stage(StageJPA stage, StandardPeriodVersionJPA version) {
		String stageNameStandardPeriod = "";
		if (version != null)
			for (StandardPeriodGroupJPA group : version.getStandardPeriodGroups()) {
				if (group.getStages().contains(stage)) {
					stageNameStandardPeriod = group.getName();
				}
			}
		return stageNameStandardPeriod;
	}

	/**
	 * @return базовый URL для FlexWorkFlow. Учитывает разные адреса для пользователей в ГО и филиалах
	 * @throws MalformedURLException
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getBaseURL(Long userid) throws MalformedURLException {
		String baseUrl = "";
		if (userid == null) {
			baseUrl = taskFacade.getGlobalSetting("serverSchemeAddressPort");
		}
		else {
			Long depid = pupFacade.getUser(userid).getDepartment().getIdDepartment();
			Long mqFileHostType = getMqFileHostTypeByDepId(depid);
			if (Config.getProperty("USE_SA").equals("true")// включена функция использовать SA
					&& mqFileHostType!=null && mqFileHostType.equals(1L)) {// филиал
				baseUrl = taskFacade.getGlobalSetting("proxyServerSchemeAddressPort");
			}
			else {
				baseUrl = taskFacade.getGlobalSetting("serverSchemeAddressPort");
			}
		}
		return baseUrl.concat("/").concat(ApplProperties.getwebcontextFWF());
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long getMqFileHostTypeByDepId(Long depid) {
		Long mqFileHostType = SBeanLocator.singleton().getCompendiumMapper().getMqFileHostTypeByDepId(depid);
		return mqFileHostType;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onEditPriceCondition(TaskJPA task, Long whoChange) {
		for (ProjectTeamJPA p : task.getProjectTeam("m"))
			try {
				StringWriter subj = new StringWriter();
				Map<String, Object> data = task.toMap();
                data.put("taskname",getNameGenitive(task.getId()));
				data.put("baseurl", getBaseURL(p.getUser().getIdUser()));
				data.put("fio", pupFacade.getUser(whoChange).getFullName());
				data.put("tasktype", task.isProduct()?"сделке":"лимиту");
				templateConf.getTemplate("onEditPriceCondition.subj.ftl", "utf-8").process(data, subj);
				StringWriter body = new StringWriter();
				templateConf.getTemplate("onEditPriceCondition.body.ftl", "utf-8").process(data, body);
				send(p.getUser().getIdUser(), p.getUser().getIdUser(), subj.toString(),
                        body.toString() + getDescriptionTask(task.getId()));
			}
			catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
			}
	}

	private HashSet<Long> getAssignedUser(Long idTask) {
		HashSet<Long> res = new HashSet<Long>();
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery(MessageFormat.format(ResourceLoader.getSQL("whoAssigned"), idTask.toString()));
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.getResultList();
		for (Object[] obj : list)
			res.add(((BigDecimal) obj[0]).longValue());
		return res;
	}
    @Override
	public String notifyTaskTableTest() throws Exception {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT t FROM TaskInfoJPA t where t.idTask in (61569,61445,61608)");
	    return notifyTaskTable(2, 27L, (List<TaskInfoJPA>) query.getResultList(), true);
    }
    private String getStandardperiodname(Long idStage, StandardPeriodVersionJPA versionJPA){
	    for(StandardPeriodGroupJPA g : versionJPA.getStandardPeriodGroups())
	        for(StageJPA s : g.getStages())
	            if (s.getIdStage().equals(idStage))
	                return g.getName();
	    return "";
    }
    private String getURL(Long userid, Long mdtaskNumber, String type) throws Exception{
        return getBaseURL(userid)+"/showTaskList.do?typeList="+type+"&searchNumber="+mdtaskNumber;
    }
    private String getOverrun(Date planCompletionDate){
    	try{
			return ru.masterdm.spo.utils.Formatter.format(ServiceFactory.getService(CompendiumService.class).getInterval(planCompletionDate, new Date()));
		} catch (Exception e) {
    		LOGGER.warn(e.getMessage(), e);
		}
    	return "";
	}
	private String notifyTaskTable(int tableFormat, Long userid, List<TaskInfoJPA> infotasks, boolean boss) throws Exception {
        ReportMapper reportMapper = (ReportMapper) SBeanLocator.singleton().getBean("reportMapper");
		StringWriter body = new StringWriter();
		Map<String, Object> data = new HashMap<String, Object>();
        List<HashMap<String, Object>> tasks = new ArrayList<HashMap<String, Object>>();
		for (TaskInfoJPA ti : infotasks) {
		    if (ti.getProcess()==null)
		        continue;
            HashMap<String, Object> task = new HashMap<String, Object>();
            TaskJPA taskJPA = taskFacade.getTaskByPupID(ti.getProcess().getId());
            if (taskJPA == null)
                continue;
            task.put("number", taskJPA.getNumberAndVersion());
            task.put("executor", getExecutorName(ti));
            task.put("url", getURL(userid, taskJPA.getMdtask_number(),
                   boss?"noAccept":(ti.getIdStatus().intValue() == 2?"accept":"perform")));
            if (tableFormat > 1)
                task.put("overrun", getOverrun(ti.getPlanCompletionDate()));
            task.put("type", taskJPA.getType());
            task.put("org", taskJPA.getOrganisationAndGroup());
            task.put("sum", taskJPA.getSumWithCurrency());
            task.put("period", taskJPA.getPeriodFormated());
            task.put("stagename", ti.getStage().getDescription());
            task.put("standardperiodname", getStandardperiodname(ti.getStage().getIdStage(), taskJPA.getActiveStandardPeriodVersion()));
            task.put("comment", reportMapper.getLastComment(taskJPA.getId()));
            tasks.add(task);
		}
		data.put("tasks", tasks);
		data.put("tableFormat", tableFormat);
		templateConf.getTemplate("taskTable1.ftl", "utf-8").process(data, body);
		return body.toString();
	}
	private String getExecutorName(TaskInfoJPA ti) throws Exception {
        if (ti.getIdStatus().intValue() == 2) // в работе
            return ti.getExecutor()==null?"Исполнитель не выбран":ti.getExecutor().getFullName();
        List<String> assigned = new ArrayList<String>();
        for (Long idUser : getAssignedUser(ti.getIdTask()))
            assigned.add(pupFacade.getUser(idUser).getFullName());
        if (assigned.isEmpty())
            return "Исполнитель не назначен";
        return "Исполнитель назначен, заявка не в работе: " + CollectionUtils.listJoin(assigned);
    }
	private void notifyExpiredSoon(Date nextWorkDayBegin) {
		UserMailQueue notifyBoss = new UserMailQueue();//уведомления для начальников
		UserMailQueue notifySlaves = new UserMailQueue();//уведомления для сотрудников
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT t FROM TaskInfoJPA t where t.planCompletionDate = :deadline and idStatus<3");
		query.setParameter("deadline", nextWorkDayBegin);
		for (TaskInfoJPA taskInfo : (List<TaskInfoJPA>) query.getResultList())
			if (taskInfo.getIdStatus().intValue() == 2) // в работе
				notifySlaves.put(taskInfo.getExecutor().getIdUser(), taskInfo);
			else {// ожидает обработки
				HashSet<Long> assigned = getAssignedUser(taskInfo.getIdTask());
				if (assigned.size() == 0) // никто не назначен
					for (UserJPA boss : notifyNeed4assignBossList(taskInfo))
                        notifyBoss.put(boss.getIdUser(), taskInfo);
				else // есть назначенные
					for (Long userid : assigned)
						notifySlaves.put(userid, taskInfo);
			}
		LOGGER.info("notifyExpiredSoon");
		LOGGER.info("notifyBosess size " + notifyBoss.queueSize());
		LOGGER.info("notifySlaves size " + notifySlaves.queueSize());
        sendUserMailQueue(notifyBoss, "Срок обработки заявок истекает. Исполнитель по ним не назначен",
                          "Срок обработки следующих заявок истекает "+ ru.masterdm.spo.utils.Formatter.format(nextWorkDayBegin) +
                                  " (исполнитель выполнения операций не назначен):", 1, true);
        sendUserMailQueue(notifySlaves, "Срок обработки заявок истекает. Вы являетесь исполнителем по заявке",
                          "Срок обработки следующих заявок истекает "+ ru.masterdm.spo.utils.Formatter.format(nextWorkDayBegin) +
                                  ". Вы являетесь исполнителем по заявке", 1, false);
	}
	private void sendUserMailQueue(UserMailQueue queue, String subject, String body, int tableFormat, boolean boss){
        for (Long userid : queue.getQueue().keySet())
            try{
                send(userid, userid, subject,
                     body + notifyTaskTable(tableFormat, userid, queue.getQueue().get(userid), boss));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
    }

	/**
	 * Алгоритм поиска руководителя:
	 * в текущем подразделении структуратора Б
	 * если нет в текущем - искать на уровень выше
	 * @return руководители структуратора
     */
	private Set<UserJPA> getStructuratorBosses(UserJPA executor, Long idTypeProcess) {
		EntityManager em = factory.createEntityManager();
		RoleJPA strBossRole = pupFacade.getRole("Руководитель структуратора", idTypeProcess);
		DepartmentJPA dep = executor.getDepartment();
		Set<UserJPA> toSet = new HashSet<UserJPA>();
		while (dep != null && toSet.isEmpty()) {
			for (Long bossId : pupFacade.findDepartmentUsersInRoles(strBossRole.getIdRole(), dep.getIdDepartment()))
				if (!bossId.equals(executor.getIdUser()))
					toSet.add(em.find(UserJPA.class, bossId));
			if (dep.getParentDepartmentList().size() == 0)
				dep = null;
			else
				dep = dep.getParentDepartmentList().get(0);
		}
		return toSet;
	}
	private void notifyExpired(Date now) throws FactoryException, MappingException,
			MalformedURLException {
        UserMailQueue notifyBossNeedAssign = new UserMailQueue();//уведомления для начальников. Нужно назначить
        UserMailQueue notifyBoss = new UserMailQueue();//уведомления для начальников. Есть назначенные
        UserMailQueue notifySlaves = new UserMailQueue();//уведомления для сотрудников
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT t FROM TaskInfoJPA t where t.planCompletionDate < :now and idStatus<3");
		query.setParameter("now", now);
		for (TaskInfoJPA taskInfo : (List<TaskInfoJPA>) query.getResultList())
            if (taskInfo.getIdStatus().intValue() == 2) {// в работе
                notifySlaves.put(taskInfo.getExecutor().getIdUser(), taskInfo);
                if(taskInfo.getStage().isStructuratorStage())
                    for (UserJPA strBoss : getStructuratorBosses(taskInfo.getExecutor(), taskInfo.getProcessType().getIdTypeProcess()))
                        notifyBoss.put(strBoss.getIdUser(), taskInfo);
                else {
                    query = em.createNativeQuery(MessageFormat.format(ResourceLoader.getSQL("whoAssigned")
                           + " and a.id_user_to=" + taskInfo.getExecutor().getIdUser().toString(), taskInfo.getIdTask().toString()));
                    @SuppressWarnings("unchecked")
                    List<Object[]> list = query.getResultList();
                    for (Object[] obj : list) {
                        UserJPA boss = em.find(UserJPA.class, ((BigDecimal) obj[2]).longValue());
                        if (!boss.equals(taskInfo.getExecutor()))
                            notifyBoss.put(boss.getIdUser(), taskInfo);
                    }
                }
            } else {
                query = em.createNativeQuery(MessageFormat.format(ResourceLoader.getSQL("whoAssigned"), taskInfo.getIdTask().toString()));
                @SuppressWarnings("unchecked")
                List<Object[]> list = query.getResultList();
                if (list.size() == 0) // никто не назначен
                    for (UserJPA boss : notifyNeed4assignBossList(taskInfo))
                        notifyBossNeedAssign.put(boss.getIdUser(), taskInfo);
                else {// есть назначенные
                    for (Object[] obj : list) {
                        UserJPA user = em.find(UserJPA.class, ((BigDecimal) obj[0]).longValue());
                        notifySlaves.put(user.getIdUser(), taskInfo);
                        if(taskInfo.getStage().isStructuratorStage()){
                            for (UserJPA strBoss : getStructuratorBosses(user, taskInfo.getProcessType().getIdTypeProcess()))
                                notifyBoss.put(strBoss.getIdUser(), taskInfo);
                        } else {
                            UserJPA boss = em.find(UserJPA.class, ((BigDecimal) obj[2]).longValue());
                            if (!boss.equals(user))
                                notifyBoss.put(boss.getIdUser(), taskInfo);
                        }
                    }
                }
            }
        sendUserMailQueue(notifySlaves, "Заявки с нарушением нормативного срока",
                          "Срок обработки следующих заявок истек. Вы являетесь исполнителем по заявке (см. представление операции в работе)",
                          2, false);
        sendUserMailQueue(notifyBoss, "Срок обработки заявок истек. Вы являетесь руководителем исполнителя по заявке",
                          "Срок обработки следующих заявок истек. Вы являетесь руководителем исполнителя по заявке (см. представление «все заявки»).",
                          2, true);
        sendUserMailQueue(notifyBossNeedAssign, "Срок обработки заявок истек. Исполнитель по ним не назначен",
                          "Срок обработки следующих заявок истек (исполнитель выполнения операций не назначен):", 2, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void notifySecretaryNewStage(Long idProcess, User from, Long idNewStage,
			Long idCurrentStage, Long idDepartament) throws Exception {
		TaskJPA taskJPA = taskFacade.getTaskByPupID(idProcess);
		StandardPeriodGroupJPA currentGroup = pupFacade.getGroup4Stage(idCurrentStage, idProcess);
		TaskActionProcessor taskprocessor = (TaskActionProcessor) ActionProcessorFactory
				.getActionProcessor("Task");
		HashMap<Long, String> assignUsers = taskprocessor.findAssignUser(idNewStage, idProcess);
		// оповещение секретарей о начале нового этапа
		boolean isNewGroup = true;
		if (currentGroup != null) {
			for (StageJPA stage : currentGroup.getStages())
				if (stage.getIdStage().equals(idNewStage))
					isNewGroup = false;
		}
		if (isNewGroup) {
			StandardPeriodGroupJPA newGroup = pupFacade.getGroup4Stage(idNewStage, idProcess);
			if (newGroup != null) {
				LOGGER.info("secretary: этап начался " + newGroup.getName() + ". idDepartament="
						+ idDepartament);
				for (Long idSecretary : getSecretaryIds(taskJPA.getProcess().getProcessType()
						.getIdTypeProcess(), idDepartament)) {
					UserJPA secretary = pupFacade.getUser(idSecretary);
                    //Шаблон 4,5
					String body = taskJPA.getType()+ " с " + taskJPA.getOrganisation()
                            +" № <a href=\"" + pupFacade.getBaseURL(secretary.getIdUser())
							+ "/showTaskList.do?typeList=all&searchNumber="
							+ taskJPA.getMdtask_number().toString() + "\">" + taskJPA.getNumberAndVersion()
							+ "</a> " + " поступил"+
							(taskJPA.isProduct()?"а":"")
							+" на этап \""
							+ newGroup.getName()
							+ "\" (см. список заявок \"Все заявки подразделения\")<br />в подразделение: "
							+ pupFacade.getDepartmentById(idDepartament).getShortName() + ".<br />";
					if (assignUsers != null && assignUsers.size() != 0) {
						body += "Исполнителем назначен: ";
						for (Long idExecutor : assignUsers.keySet())
							body += pupFacade.getUser(idExecutor).getFullName() + "<br />";
					}
					else {
						body += "Исполнитель не назначен.";
					}
					send(from.getId(), secretary.getIdUser(),
							"Начало обработки " + getNameGenitive(taskJPA.getId()),
                            body + getDescriptionTask(taskJPA.getId()));
				}
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void notifyStartEditProcess(Long taskId, User from) throws Exception {
		TaskJPA task = taskFacade.getTask(taskId);
		String body = null;
		for (ProjectTeamJPA prTeam : task.getProjectTeam()) {
			if (prTeam.getTeamType().equals("p")) {
				body = "Пользователь " + from.getName().getFIO() + " запустил бизнес-процесс «"
							+ task.getProcessTypeName() + "» по "+getTypeNamePraepositionalis(task.getId())
                        + getAllContractors(task.getId())+" № <a href=\""
						+ pupFacade.getBaseURL(prTeam.getUser().getIdUser())
						+ "/showTaskList.do?typeList=all&searchNumber=" + task.getMdtask_number().toString()
						+ "&projectteam=true\">" + task.getNumberAndVersion() + "</a> (" + task.getOrganisation()
						+ "). Вы являетесь членом Проектной команды по "+(task.isProduct()?"этой сделке":"этому лимиту")
						+ "(см. список заявок «работа проектной команды»).";
				send(from.getId(), prTeam.getUser().getIdUser(), "Изменение условий по " +getNamePraepositionalis(taskId),
                        body + getDescriptionTask(taskId));
				LOGGER.info("отправлено уведомление об участии в Проектной команде "
						+ prTeam.getUser().getFullName() + " на " + prTeam.getUser().getAllEmails());
			}
		}
		RoleJPA role = pupFacade.getRole("Руководитель мидл-офиса", task.getIdTypeProcess());
		if (role != null) {
			for (UserJPA rukMO : role.getUsers()) {
				body = "Пользователь " + from.getName().getFIO() + " запустил бизнес-процесс «"
						+ task.getProcessTypeName() + "» по "+getTypeNamePraepositionalis(task.getId())
                        + getAllContractors(task.getId())+" № <a href=\""
						+ pupFacade.getBaseURL(rukMO.getIdUser())
						+ "/showTaskList.do?typeList=all&searchNumber=" + task.getMdtask_number().toString()
						+ "\">" + task.getNumberAndVersion() + "</a> (" + task.getOrganisation()
						+ "). Вы являетесь Руководителем мидл-офиса в этом "
						+ "бизнес-процессе и можете назначить работника мидл-офиса.";
				send(from.getId(), rukMO.getIdUser(), "Изменение условий по "+getNamePraepositionalis(taskId),
                        body + getDescriptionTask(taskId));
				LOGGER.info("отправлено уведомление о назначении рук-лем МО " + rukMO.getFullName()
						+ " на " + rukMO.getAllEmails());
			}
		}
		//шаблон 11
        for (ProjectTeamJPA prTeam : task.getProjectTeam())
            if (pupFacade.userAssignedAs(prTeam.getUser().getIdUser(),"Структуратор",task.getIdProcess())
                    || pupFacade.userAssignedAs(prTeam.getUser().getIdUser(),"Руководитель структуратора",task.getIdProcess())) {
				String roleName = pupFacade.userAssignedAs(prTeam.getUser().getIdUser(),"Структуратор",task.getIdProcess())?
                        "Структуратор":"Руководитель структуратора";
                send(from.getId(), prTeam.getUser().getIdUser(), "Изменение условий по " +getNamePraepositionalis(taskId),
                    "Вы назначены исполнителем операции «Изменение параметров заявки» по "+
                            getTypeNamePraepositionalis(task.getId())+
                            getAllContractors(task.getId())+" № <a href=\""
                            + pupFacade.getBaseURL(prTeam.getUser().getIdUser())
                            + "/showTaskList.do?typeList=perform&searchNumber=" + task.getMdtask_number().toString()
                            + "\">" + task.getNumberAndVersion() + "</a> " +
                            "процесса «"+task.getProcessTypeName()+ "» " +
                            "для роли " + roleName + "." + getDescriptionTask(taskId));
            }
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Set<Long> getSecretaryIds(Long idTypeProcess, Long idDepartment) {
		Set<Long> secretaryIds = new HashSet<Long>();
		RoleJPA role = pupFacade.getRole("Секретарь", idTypeProcess);
		Set<DepartmentJPA> deps = pupFacade.getDepartmentById(idDepartment).getAllParent();
		deps.add(pupFacade.getDepartmentById(idDepartment));
		for (DepartmentJPA dep : deps)
			secretaryIds.addAll(pupFacade.findDepartmentUsersInRoles(role.getIdRole(), dep
					.getIdDepartment()));
		return secretaryIds;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String[] notifyEditProcess(Long numberDisplay, Long idTaskInfo, Long version, User from, Long idUserTo,
			String contractor, String modules) throws Exception{
		String[] notify = new String[2];
		String link = getBaseURL(idUserTo)+ "/form.jsp?mdtaskid=" + idTaskInfo;
		String subject = modules + " Одобрены новые условия по сделке № " + numberDisplay + " (" + contractor + ")";
		String body = "По сделке № <a href=\"" + link + "\">" + numberDisplay + " </a>(" + contractor + ") одобрены новые условия - версия № " + version;
		notify[0] = subject;
		notify[1] = body;
		LOGGER.info("=====формирование сообщения=====");
		LOGGER.info(subject);
		LOGGER.info(body);
		return notify;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onWorkCompletes(Long fromUserId, Long mdTaskNumber, Long mdTaskVersion, String mainBorrowerName) throws TemplateException, IOException {
        if (mdTaskNumber == null
                || mdTaskVersion == null
                || StringUtils.isEmpty(mainBorrowerName))
            return;

        MdTask mdTask = mdTaskMapper.getForOnWorkCompletesNotifications(mdTaskNumber, mdTaskVersion);
        if (mdTask == null) {
            LOGGER.warn("mdTask is null by number '" + mdTaskNumber + "' and version '" + mdTaskVersion + "'");
            return;
        }
        Org contractor = new Org();
        contractor.setName(mainBorrowerName);
        mdTask.setMainOrganization(contractor);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("mdTask", mdTask);

        StringWriter subject = new StringWriter();
        templateConf.getTemplate("notifyOnWorkCompletes.subj.ftl").process(data, subject);
        
        ru.md.domain.MdTask lastCedConfirmedCreditDeal = SBeanLocator.singleton().mdTaskMapper().getLastCedConfirmedCreditDeal(mdTaskNumber, mdTaskVersion);
        if (lastCedConfirmedCreditDeal == null) {
        	LOGGER.warn("lastCedConfirmedCreditDeal are empty by mdTaskNumber '" + mdTaskNumber + "', less then mdTaskVersion '" + mdTaskVersion + "'");
            return;
        }
        else {
        	Long lastConfirmedCedDealId = lastCedConfirmedCreditDeal.getIdMdtask(); 
        	Long lastConfirmedDealVersionWithCed = lastCedConfirmedCreditDeal.getVersion();
        	
        	LOGGER.debug("lastConfirmedDealVersionWithCed '" + mdTaskNumber + "', mdTaskVersion '" + lastConfirmedDealVersionWithCed + "', lastConfirmedCedDealId '" + lastConfirmedCedDealId + "'");
        	
	        List<CedUser> members = ServiceFactory.getService(CedService.class).getAssisgnedMembersByCreditDealVersion(mdTaskNumber, lastConfirmedDealVersionWithCed);
	        if (members == null || members.size() == 0) {
	            LOGGER.warn("members are empty by mdTaskNumber '" + mdTaskNumber + "', mdTaskVersion '" + lastConfirmedDealVersionWithCed + "'");
	            return;
	        }
	        for (CedUser member : members) {
	            data.put("baseUrl", getBaseURL(member.getIdUser()));

	            StringWriter body = new StringWriter();
	            templateConf.getTemplate("notifyOnWorkCompletes.body.ftl").process(data, body);

	            ServiceFactory.getService(MailerService.class).send(subject.toString(), body.toString(), fromUserId, member.getIdUser());
	        }	        
        }
    }

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void send(Long fromUserId, Long recipientId, String subject,
			String body) throws MappingException {
		try {
			LOGGER.info("Mail sender called. Parameters:\r\n" + "fromUserId='" + fromUserId + "',\r\n" + "recipientId='" + recipientId + "',\r\n"
                    + "subject='" + subject + "',\r\n" + "body='" + body + "'");
			if(recipientId==null)
				return;
			ru.masterdm.integration.ServiceFactory.getService(MailerService.class).send("[СПО] " + subject, body, fromUserId, recipientId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	/**
	 * Возвращает название для уведомлений
	 * @return
	 */
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getName(Long mdTaskId) {
		//Сделка №204384 версия 1 с "Ромашка", ПАО и т.д.
        return getName(mdTaskId, CASES.NAME);
	}
	/**
	 * Возвращает название для уведомлений в родительном падеже
	 * @return
	 */
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getNameGenitive(Long mdTaskId) {
		//сделки 204427 версия 1 с "Ромашка", ПАО и т.д.
        return getName(mdTaskId, CASES.GENITIVE);
	}
	/**
	 * Возвращает название для уведомлений в дательном падеже
	 * @return
	 */
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getNamePraepositionalis(Long mdTaskId) {
		//по сделке №ХХХХ версия 1 с "Ромашка", ПАО
		return getName(mdTaskId, CASES.PRAEPOSITIONALIS);
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getNameContractors(Long mdTaskId) {
		//с "Ромашка", ПАО и т.д.
		List<TaskKz> kzList = SBeanLocator.singleton().compendium().getTaskKzByMdtask(mdTaskId);
		if (kzList.size() > 0)
			for (TaskKz taskKz : kzList)
				if (taskKz.isMainOrg())
					return " с " + SBeanLocator.singleton().getDictService().getEkNameByOrgId(taskKz.getKzid())/* +
							(kzList.size()>1?" и т.д.":"")*/;
		return "";
	}
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getAllContractors(Long mdTaskId) {
        return getNameContractors(mdTaskId);
		/*List<TaskKz> kzList = SBeanLocator.singleton().compendium().getTaskKzByMdtask(mdTaskId);
        ArrayList<String> res = new ArrayList<String>();
		for (TaskKz taskKz : kzList)
            res.add(SBeanLocator.singleton().compendium().getEkNameByOrgId(taskKz.getKzid()));
        if (res.isEmpty())
            return "";
		return " с " + CollectionUtils.listJoin(res) + " ";*/
	}

	private String getName(Long mdTaskId, CASES cases) {
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(mdTaskId);
        return getTypeName(task, cases) + getNameContractors(mdTaskId) + " № " + task.getNumberAndVersion();
	}

    public String getTypeNamePraepositionalis(Long mdTaskId) {
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(mdTaskId);
        return getTypeName(task, CASES.PRAEPOSITIONALIS);
    }

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void notifyTaskChange(String[] changedSection, MdTaskTO task) throws Exception {
        if (changedSection.length == 0) {
			LOGGER.info("task is not changed");
			return;
		}
		//для завершенных не рассылать уведомления
		if (task.mybatis.isLimit() || task.jpa.getProcess()!=null && task.jpa.getProcess().getIdStatus() > 2
				|| task.jpa.getIdProcess() == null)
			return;
        //сформировать письмо, отправить адресатам
        Map<String, Object> data = task.jpa.toMap();
        data.put("changedSection", changedSection);
        for(Long userid : getCedMiddleOffice(task.mybatis.getMdtaskNumber())) {
            data.put("baseurl", getBaseURL(userid));
            sendMail(data, pupFacade.getUser(userid), "notifyTaskChange", task.id);
        }
	}

	@Override
	public void notifyDeleteDoc(Long mdTaskId, String reason, String unid, String orgid) throws Exception {
		if (mdTaskId == null || ru.masterdm.spo.utils.Formatter.str(unid).isEmpty())
			return;
        Long currentUserId = pupFacade.getCurrentUser().getIdUser();
        AttachJPA attach = pupFacade.getAttachemnt(unid);
        TaskJPA task = taskFacade.getTask(mdTaskId);
        for(Long to : getDocStakeholder(task, orgid)){
            String subj = "Удалён документ по "+attach.getTitle()+" по "+getNamePraepositionalis(mdTaskId);
            LOGGER.info(subj);
            send(currentUserId, to, subj,
                 "Пользователь "+pupFacade.getCurrentUser().getFullName()+" удалил документ "
                    +attach.getTitle()+ " по "+getTypeNamePraepositionalis(mdTaskId)+" с "
                         + task.getOrganisation()+
                         "  № <a href=\"" + pupFacade.getBaseURL(to) + "/form.jsp?mdtaskid="+mdTaskId
                         + "\">" + task.getNumberAndVersion() + "</a> "
                         +getDescriptionTask(mdTaskId));
        }
	}
    private HashSet<Long> getDocStakeholder(TaskJPA task, String orgid) throws Exception {
		MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        HashSet<Long> users = new HashSet<Long>();
		if(orgid != null) {
			for (Long idMdtask : mapper.getIdMdtaskByOrgId(orgid))
					users.addAll(getDocStakeholder(taskFacade.getTask(idMdtask), null));
			return users;
		}
        //если заявка не передана на КК, то не отправляем уведомления
        MdTask mdtask = mapper.getById(task.getId());
        if(mdtask.getIdInstance() == null || task.getProcess() == null || !task.getProcess().getIdStatus().equals(1L))
            return users;
        DictionaryFacadeLocal dict =  com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
        for(ProjectTeamJPA pt : task.getProjectTeam("p"))
            for (String roleName : dict.findProjectTeamRoles())
                if(task.getIdProcess()!= null && pupFacade.userAssignedAs(pt.getUser().getIdUser(), roleName, task.getIdProcess()))
                    users.add(pt.getUser().getIdUser());
        return users;
    }

    @Override
    public void notifyAcceptDoc(Long mdTaskId, String unid, String orgid) throws Exception {
        if (mdTaskId == null || ru.masterdm.spo.utils.Formatter.str(unid).isEmpty())
            return;
        Long currentUserId = pupFacade.getCurrentUser().getIdUser();
        AttachJPA attach = pupFacade.getAttachemnt(unid);
        TaskJPA task = taskFacade.getTask(mdTaskId);
        for(Long to : getDocStakeholder(task, orgid)){
            send(currentUserId, to, "Добавлен документ по "+attach.getTitle()+" по "+getNamePraepositionalis(mdTaskId),
                 "Пользователь "+pupFacade.getCurrentUser().getFullName()+" добавил документ "
                         +attach.getTitle()+ " по "+getTypeNamePraepositionalis(mdTaskId)+" с "
                         + task.getOrganisation()+
                         "  № <a href=\"" + pupFacade.getBaseURL(to) + "/form.jsp?mdtaskid="+mdTaskId
                         + "\">" + task.getNumberAndVersion() + "</a> "
                         +getDescriptionTask(mdTaskId));
        }
    }

    //Работнику мидл-офиса (КОД), включенному в секцию «Участники» активного запроса КОД по этой сделке, необходимо направлять уведомление
    private Set<Long> getCedMiddleOffice(Long number) {
        Set<Long> res = new HashSet<Long>();
		List<String> roleKeys = new ArrayList<String>();
		roleKeys.add("MIDDLE_OFFICE_STAFF_CED");
		for (CedUser cedUser : ServiceFactory.getService(CedService.class).getAssignedMembers(number, null, roleKeys, false, true))
			res.add(cedUser.getIdUser());
        //res.add(28L);
        return res;
    }

	public String getTypeNameGenitive(Long mdTaskId) {
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(mdTaskId);
        return getTypeName(task, CASES.GENITIVE);
    }
    private String getTypeName(MdTask task, CASES cases) {
        String res = "";
        if (task.isProduct())
            res = cases.getProductName();
        if (task.isLimit())
            res = cases.getLimitName();
        if (task.isCrossSell())
            res = cases.getCrossSellName();
        return res;
	}

	public enum CASES {
		NAME("сделка","лимит","кросс-селл"),
		GENITIVE("сделки","лимита","кросс-селла"),
		PRAEPOSITIONALIS("сделке","лимиту","кросс-селлу");

		String productName;
		String limitName;
		String crossSellName;

		CASES(String productName, String limitName, String crossSellName) {
			this.productName = productName;
			this.limitName = limitName;
			this.crossSellName = crossSellName;
		}

		public String getProductName() {
			return productName;
		}

		public String getLimitName() {
			return limitName;
		}

		public String getCrossSellName() {
			return crossSellName;
		}
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String getDescriptionTask(Long mdTaskId) {
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(mdTaskId);
        StringBuilder res = new StringBuilder("<br /><b>Описание заявки:</b><br />");
        List<TaskKz> kzList = SBeanLocator.singleton().compendium().getTaskKzByMdtask(task.getIdMdtask());
        int i=1;
        for (TaskKz taskKz : kzList){
            if (taskKz.isMainOrg()) {
                String groupName = SBeanLocator.singleton().compendium().getGroupNameByOrgId(taskKz.getKzid());
                res.append(
                    (taskKz.isMainOrg()?"Основной ":"")
                    + "Заемщик " + (i++) + ": "
                    + SBeanLocator.singleton().getDictService().getEkNameByOrgId(taskKz.getKzid())
                    + ((groupName!=null && !groupName.isEmpty())?" (входит в Группу компаний: «" + groupName + "»)":"" )
                    +"<br />");
            }
        }
        res.append("Тип заявки: " + task.getType() +".<br />");
        res.append("Сумма " + getTypeName(task, CASES.GENITIVE) + ": " + task.getSumWithCurrency() + ".<br />");
        String period = task.getPeriodFormated();
        if (period.isEmpty())
            period = task.getValidtoDisplay();
        res.append("Срок " + getTypeName(task, CASES.GENITIVE) + ": " + period + "<br />");
        res.append("Номер версии: " + task.getNumberAndVersion() + ".<br />");
        res.append("Дата создания заявки: " + Formatter.format(
                SBeanLocator.singleton().mdTaskMapper().getStartDate(mdTaskId))+"<br />");
		return res.toString();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void interestRateChangeNotification(Long mdTaskId, InterestRateChange interestRateChange) {
        if (mdTaskId == null || interestRateChange == null) {
            LOGGER.warn("input parameters are null");
            return;
        }

        UserJPA currentUser = pupFacade.getCurrentUser();
        MdTask mdTask = mdTaskMapper.getById(mdTaskId);
        String baseUrl = null;
        try {
            baseUrl = getBaseURL(currentUser.getIdUser());
        } catch (MalformedURLException e) {
            LOGGER.warn("cannot get base url", e);
            return;
        }
        if (StringUtils.isEmpty(baseUrl)) {
            LOGGER.warn("base url is empty");
            return;
        }

        String bodyTemplate = null;
        String subjTemplate = null;
        List<Long> recipients = null;

        switch (interestRateChange) {
            case ACCEPTED:
                bodyTemplate = "interestRateAccepted.body.ftl";
                subjTemplate = "interestRateAccepted.subj.ftl";
                recipients = userMapper.getRecipientsOnInterestRateAccepted(currentUser.getIdUser(), mdTask.getIdMdtask());
                break;
            case TO_ACCEPT:
                bodyTemplate = "interestRateToAccept.body.ftl";
                subjTemplate = "interestRateToAccept.subj.ftl";
                recipients = userMapper.getRecipientsOnInterestRateToAccept(currentUser.getIdUser(), mdTask.getIdMdtask());
                break;
            case RETURN:
                bodyTemplate = "interestRateReturn.body.ftl";
                subjTemplate = "interestRateReturn.subj.ftl";
                recipients = userMapper.getRecipientsOnInterestRateReturn(currentUser.getIdUser(), mdTask.getIdMdtask());
        }

        if (StringUtils.isEmpty(bodyTemplate) || StringUtils.isEmpty(subjTemplate)) {
            LOGGER.warn("cannot get templates");
            return;
        }
        if (recipients == null) {
            LOGGER.warn("recipients are not defined");
            return;
        }

        Map<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("baseUrl", baseUrl);
        templateData.put("mdTask", mdTask);
        templateData.put("officialNumber", mdTaskMapper.getOficcialNumber(mdTaskId));

        StringWriter body = new StringWriter();
        StringWriter subj = new StringWriter();
        try {
            templateConf.getTemplate(bodyTemplate).process(templateData, body);
            templateConf.getTemplate(subjTemplate).process(templateData, subj);
        } catch (Exception e) {
            LOGGER.warn("cannot process templates");
            return;
        }

        Message message = new Message();
        message.setBody(body.toString());
        message.setSubject(subj.toString());
        message.setFromUserId(currentUser.getIdUser());
        message.setToUserIds(recipients);
        ServiceFactory.getService(MailerService.class).send(message);
    }
}
