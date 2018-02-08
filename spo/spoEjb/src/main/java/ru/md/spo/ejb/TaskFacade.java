package ru.md.spo.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.servlet.ServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import com.vtb.domain.Comment;
import com.vtb.domain.MainBorrowerChangeLog;
import com.vtb.domain.Operator;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskCurrency;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskTarget;
import com.vtb.domain.Trance;
import com.vtb.domain.integration.MdTask;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.spo.PunitiveMeasure;
import ru.masterdm.spo.utils.SBeanLocator;

import ru.md.domain.OtherGoal;
import ru.md.persistence.MdTaskMapper;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.AuthorizedPersonJPA;
import ru.md.spo.dbobjects.CdAcredetivSourcePaymentJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverCriteriumJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverPremiumJPA;
import ru.md.spo.dbobjects.CdPremiumTypeJPA;
import ru.md.spo.dbobjects.CdRiskpremiumJPA;
import ru.md.spo.dbobjects.ContractJPA;
import ru.md.spo.dbobjects.DependingLoanJPA;
import ru.md.spo.dbobjects.ExpertTeamJPA;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.FundDownJPA;
import ru.md.spo.dbobjects.GlobalSettingsJPA;
import ru.md.spo.dbobjects.IndConditionJPA;
import ru.md.spo.dbobjects.IndrateMdtaskJPA;
import ru.md.spo.dbobjects.MdTaskNumber;
import ru.md.spo.dbobjects.MdTaskTO;
import ru.md.spo.dbobjects.OperDecisionDescriptionJPA;
import ru.md.spo.dbobjects.OperDecisionJPA;
import ru.md.spo.dbobjects.PipelineJPA;
import ru.md.spo.dbobjects.PremiumJPA;
import ru.md.spo.dbobjects.ProductGroupJPA;
import ru.md.spo.dbobjects.ProductTypeJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.PromissoryNoteJPA;
import ru.md.spo.dbobjects.PunitiveMeasureJPA;
import ru.md.spo.dbobjects.RequestLogJPA;
import ru.md.spo.dbobjects.StavspredJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.dbobjects.TaskVersionJPA;
import ru.md.spo.dbobjects.TranceJPA;
import ru.md.spo.loader.TaskLine;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TaskFacade implements TaskFacadeLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskFacade.class.getName());
	
	@Autowired
	private MdTaskMapper mdTaskMapper;

    @PersistenceUnit(unitName = "flexWorkflowEJBJPA")
    private EntityManagerFactory factory;
    
    @EJB
    private PupFacadeLocal pupFacade;
    
    @EJB
    private DictionaryFacadeLocal dictFacade;
    
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createVersion(TaskVersionJPA version) {
        EntityManager em = factory.createEntityManager();
        em.persist(version);
        LOGGER.info("saved version for mdtask " + version.getId_mdtask());
    }

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public MdTaskTO getTaskFull(Long mdtaskid) {
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		Task jdbc = null;
		try {
			jdbc = processor.getTask(new Task(mdtaskid));
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return new MdTaskTO(mdtaskid, getTask(mdtaskid),jdbc,
					SBeanLocator.singleton().mdTaskMapper().getById(mdtaskid));
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TaskJPA getTask(Long mdtaskid) {
    	long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        TaskJPA res = em.find(TaskJPA.class, mdtaskid);
        Long loadTime = System.currentTimeMillis()-tstart;
        LOGGER.warn("*** TaskFacade.getTask(" + mdtaskid + ") time " + loadTime);
        return res;
    }

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public TaskJPA getTaskByPupID(Long pupid) {
		try {
			EntityManager em = factory.createEntityManager();
			Query query = em
					.createNativeQuery("select t.id_mdtask from mdtask t where t.id_pup_process=?");
			query.setParameter(1, pupid);
			Object obj = query.getSingleResult();
			return getTask(((BigDecimal) obj).longValue());
		}
		catch (Exception e) {
			LOGGER.error("Не могу найти mdtask по pupid = " + pupid + "\n" + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public ArrayList<TaskLine> loadTaskLines(ArrayList<TaskLine> taskLineList, Long currentUserId) throws Exception {
		if(taskLineList == null || taskLineList.isEmpty())
			return new ArrayList<TaskLine>();
		long tstart = System.currentTimeMillis();
		EntityManager em = factory.createEntityManager();
		ArrayList<TaskLine> res = new ArrayList<TaskLine>();
		
		for(TaskLine taskLine : taskLineList){
			if (taskLine.getIdTask() > 0) {// значит это задача, а не только заявка. Проверяем deadline
				Query query = em.createNativeQuery("select t.DT_PLAN_COMPLETION from TASKS t where t.ID_TASK=?");
				query.setParameter(1, taskLine.getIdTask());
				Date deadline = (Date) query.getSingleResult();
				if (deadline != null && deadline.before(new Date()))
					taskLine.setTrClass("expired");// подсветить просроченные
			}
			res.add(taskLine);
		}
		LOGGER.warn("*** TaskFacade.loadTaskLine() deadline time "+(System.currentTimeMillis()-tstart));
		ArrayList<Long> pupidList = new ArrayList<Long>();
		for(TaskLine taskLine : res)
			pupidList.add(taskLine.getIdProcess());
		Query query = em
				.createNativeQuery("select t.id_mdtask,t.is_debt_sum,t.is_limit_sum,t.debt_limit_sum,t.limit_issue_sum,"
						+ "t.mdtask_sum,t.currency,t.mdtask_number,t.crmcode,tp.description_process,d.shortname,sr.status_type,t.version,"
						+ "(select count(p2.id_process) from mdtask m2 "
						+ "left join processes p2 on p2.id_process = m2.id_pup_process and p2.id_status <> 4 "
						+ "where t.mdtask_number = m2.mdtask_number and t.id_mdtask <> m2.id_mdtask ) incomplete, p.id_type_process,t.id_pup_process," +
						"t.is_imported, t.IS_IMPORTED_BM, "
						+ "nvl(mf.mdtask_id, 0),st_map.spo_status map_status "
						+ "from mdtask t inner join processes p on t.id_pup_process=p.id_process "
						+ "inner join type_process tp on tp.id_type_process=p.id_type_process "
						+ "left outer join departments d on initdepartment=d.id_department "
						+ "left outer join crm_status_return sr on sr.fb_spo_return_id=statusreturn "
						+ "left outer join spo_cc_status_map st_map on t.ID_DISPLAY_STATUS=st_map.cc_status_id "
						+ "left join mdtask_favorite mf on mf.mdtask_id = t.id_mdtask and mf.user_id = " + currentUserId
						+ "where t.id_pup_process in(" + StringUtils.join(pupidList, ",") + ")");
		ArrayList<Object[]> resultTable = (ArrayList<Object[]>) query.getResultList();
		for (Object[] r : resultTable)
			for(TaskLine taskLine : res)
				if(taskLine.getIdProcess().longValue()  ==  ((BigDecimal)r[15]).longValue()){
					taskLine.setIdMDTask(((BigDecimal) r[0]).longValue());
					taskLine.setSum((BigDecimal) r[5]);
					taskLine.setCurrency((String) r[6]);
					// Если Продукт «Кредитная линия с лимитом выдачи и лимитом задолженности», то
					// «Сумма лимита выдачи», должна отображаться в графе «сумма» при выведении списка операций.
					String chk = (String) r[1];
					if (chk == null)
						chk = "";
					if (chk.equalsIgnoreCase("Y") && r[3] != null)
						taskLine.setSum((BigDecimal) r[3]);
					chk = (String) r[2];
					if (chk == null)
						chk = "";
					if (chk.equalsIgnoreCase("Y") && r[4] != null)
						taskLine.setSum((BigDecimal) r[4]);
					taskLine.setNumberpup(r[7].toString());
					String number = (String) r[8];
					if (number == null || number.equals(taskLine.getNumberpup()))
						number = "";
					taskLine.setNumberZ(number.isEmpty() ? taskLine.getNumberpup() : number + " ("
							+ taskLine.getNumberpup() + ")");
					taskLine.setDescriptionProcess((String) r[9]);
					taskLine.setDepartment((String) r[10]);
					taskLine.setStatusReturnType((String) r[11]);
					taskLine.setVersion(r[12].toString());
					taskLine.setIdTypeProcess(((BigDecimal) r[14]).longValue());
					taskLine.setIncomplete(((BigDecimal) r[13]).longValue());
					BigDecimal is_imported = (BigDecimal) r[16];
					String is_imported_bm = (String) r[17];
					taskLine.setImportedAccess(is_imported!=null && is_imported.longValue()>0);
					taskLine.setImportedBm(is_imported_bm!=null && !is_imported_bm.isEmpty());

					BigDecimal isFavorite = (BigDecimal) r[18];
                    taskLine.setFavorite(isFavorite !=null && isFavorite.longValue() > 0);
					taskLine.setStatus((String)r[19]);
				}
		LOGGER.warn("*** TaskFacade.loadTaskLine() main time "+(System.currentTimeMillis()-tstart));
		ArrayList<Long> mdtaskidList = new ArrayList<Long>();
		for(TaskLine taskLine : res)
			mdtaskidList.add(taskLine.getIdMDTask());
		query = em.createNativeQuery("select distinct nvl(ek.name,o.organization_name),id_mdtask from r_org_mdtask r "
				+ "inner join crm_organization o on o.id_org=r.id_crmorg "
				+ "inner join CRM_FINANCE_ORG f on f.ID_ORG=r.id_crmorg "
				+ "left outer join crm_ek ek on ek.id=f.ID_UNITED_CLIENT "
				+ "where id_mdtask in (" + StringUtils.join(mdtaskidList, ",") + ")");
		resultTable = (ArrayList<Object[]>) query.getResultList();
		for (Object[] r : resultTable)
			for(TaskLine taskLine : res)
				if(taskLine.getIdMDTask().longValue()  ==  ((BigDecimal)r[1]).longValue())
					taskLine.setContractors(taskLine.getContractors() + r[0] + "<br />");
		query = em.createNativeQuery("select distinct ek.GROUPNAME,id_mdtask "
				+ "from r_org_mdtask r "
				+ "inner join CRM_FINANCE_ORG f on f.ID_ORG=r.id_crmorg "
				+ "inner join crm_ek ek on ek.id=f.ID_UNITED_CLIENT "
				+ "where id_mdtask in(" + StringUtils.join(mdtaskidList, ",") + ") and ek.GROUPNAME is not null and r.ORDER_DISP=0");
		resultTable = (ArrayList<Object[]>) query.getResultList();
		for (Object[] r : resultTable)
			for(TaskLine taskLine : res)
				if(taskLine.getIdMDTask().longValue()  ==  ((BigDecimal)r[1]).longValue())
					taskLine.setGroup(taskLine.getGroup() + r[0] + " ");
		LOGGER.warn("*** TaskFacade.loadTaskLine() orgname time "+(System.currentTimeMillis()-tstart));
		query = em.createNativeQuery("select a.value_var,v.name_var,a.id_process from variables v "
				+ "inner join attributes a on a.id_var=v.id_var "
				+ "where v.name_var in ('Приоритет','Статус','Тип кредитной заявки') and a.value_var is not null "
				+ "and a.id_process in (" + StringUtils.join(pupidList, ",") + ")");
		resultTable = (ArrayList<Object[]>) query.getResultList();
		for (Object[] r : resultTable)
			for(TaskLine taskLine : res)
				if(taskLine.getIdProcess().longValue()  ==  ((BigDecimal)r[2]).longValue()){
					if(r[1].toString().equals("Приоритет"))
						taskLine.setPriority(r[0].toString());
					if(r[1].toString().equals("Статус") && ru.masterdm.spo.utils.Formatter.str(taskLine.getStatus()).isEmpty())
						taskLine.setStatus(r[0].toString());
					if(r[1].toString().equals("Тип кредитной заявки"))
						taskLine.setProcessType(r[0].toString());
				}
		LOGGER.warn("*** TaskFacade.loadTaskLine() PUPAttribute time "+(System.currentTimeMillis()-tstart));
		for(TaskLine taskLine : res){
			taskLine.setShowEditConditionLink(taskLine.getIncomplete().equals(0L)
				&&Formatter.str(taskLine.getStatusReturnType()).equals("1"));
			if (taskLine.isShowEditConditionLink()) {
				// проверка, что сделка является последней из одобренных
				query = em.createNativeQuery("select max(m.id_mdtask) from mdtask m "
						+ "inner join crm_status_return r on r.fb_spo_return_id=m.statusreturn "
						+ "where m.mdtask_number = ? and r.STATUS_TYPE=1");
				Long mdtaskNumber = Long.parseLong(taskLine.getNumberpup());
				query.setParameter(1, mdtaskNumber);
				BigDecimal maxApprovedId = (BigDecimal) query.getSingleResult();
				if (maxApprovedId == null || !(maxApprovedId.longValue() == taskLine.getIdMDTask().longValue()))
					taskLine.setShowEditConditionLink(false);
			}
		}
		LOGGER.warn("*** TaskFacade.loadTaskLine() Incomplete time "+(System.currentTimeMillis()-tstart));
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public TaskJPA getTaskByNumber(Long mdtask_number) throws Exception {
		if (mdtask_number == null)
			throw new Exception("Необходимо указать номер заявки");
		EntityManager em = factory.createEntityManager();
		Query query = em
				.createNativeQuery("select t.id_mdtask from mdtask t "
						+ "left outer join mdtask t2 on (t.mdtask_number = t2.mdtask_number AND t.version < t2.version) "
						+ "where t.mdtask_number=? and t.id_pup_process is not null and t2.id_mdtask is null");
		query.setParameter(1, mdtask_number);
		try {
			Object obj = query.getSingleResult();
			return getTask(((BigDecimal) obj).longValue());
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new Exception("Необходимо указать номер заявки");
		}
	}

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void refreshTask(Long id) {
        EntityManager em = factory.createEntityManager();
        TaskJPA task = em.find(TaskJPA.class, id);
        em.refresh(task);
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getGlobalSetting(String key) {
    	try {
    		return factory.createEntityManager().find(GlobalSettingsJPA.class, key).getValue();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    	return "";
    }

    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void merge(Object entity) {
        EntityManager em = factory.createEntityManager();
        try {
        	em.merge(entity);
        	em.flush();
		} catch (Exception e) {
			LOGGER.error( e.getMessage(), e);
		}
    }

    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeProjectTeamJPA(Long id) {
        EntityManager em = factory.createEntityManager();
        ProjectTeamJPA p = em.find(ProjectTeamJPA.class, id);
        em.createNativeQuery("insert into EX_PROJECT_TEAM(ID_MDTASK, ID_USER) values("+p.getTask().getId()+", "+p.getUser().getIdStr()+")").executeUpdate();
        em.remove(p);
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isOpportunityLoaded(String crmOpportunityId) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("select count(*) from mdtask t where t.opportunityid=?");
        query.setParameter(1, crmOpportunityId);
        BigDecimal count = (BigDecimal) query.getSingleResult();
        return count.longValue() > 0;
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getProductTypeIdByName(String name) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("select t.productid from crm_product t where t.name=?");
        query.setParameter(1, name);
        @SuppressWarnings("unchecked")
        List<String> list = query.getResultList(); 
        if (list.size()>0) {
            return list.get(0);
        }
        return null;
    }

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<AuthorizedPersonJPA> getAuthorizedPersonJPAList(
			Long processTypeId) {
		EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM AuthorizedPersonJPA u where u.processType.idTypeProcess = :processTypeId");
        query.setParameter("processTypeId", processTypeId);
        @SuppressWarnings("unchecked")
        List<AuthorizedPersonJPA> list = query.getResultList();
        return list;
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void setAuthorizedPerson(Long mdtaskId, Long authorizedPersonId) {
		EntityManager em = factory.createEntityManager();
		em.createNativeQuery("update mdtask set id_authorized_person="+authorizedPersonId+" where id_mdtask="+mdtaskId).executeUpdate();
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Long> findLimitByOrg(String orgid, String inn) {
	    ArrayList<Long> res = new ArrayList<Long>();
	    if(orgid==null || orgid.isEmpty())
	    	return res;
	    String sql ="select distinct r.ID_MDTASK from r_org_mdtask r " +
				"inner join mdtask t on r.ID_MDTASK=t.ID_MDTASK " +
				"where t.tasktype='l'  and (t.statusreturn is null or  t.statusreturn in (select s.fb_spo_return_id from crm_status_return s where s.status_type='1')) and " +
				"r.id_crmorg in (select ? from dual union all select f.ID_ORG from CRM_FINANCE_ORG f where f.ID_UNITED_CLIENT=?)";
		if(inn!=null && !inn.trim().isEmpty())
			sql += " and exists (select inn from mdtask s "+ 
					"inner join r_org_mdtask r on r.id_mdtask=s.id_mdtask "+
					"inner join V_ORGANISATION o on o.crmid=r.id_crmorg "+ 
					"where s.deleted='N' and (s.id_pup_process is null or s.parentid is null) and inn=? "+
					"CONNECT BY PRIOR s.id_mdtask=s.parentid start with s.id_mdtask=t.id_mdtask) ";
		sql += " order by r.id_mdtask desc";
		LOGGER.info(sql);
		Query q = factory.createEntityManager().createNativeQuery(sql);
		int i=1;
		if(orgid!=null && !orgid.isEmpty()){
			q.setParameter(i++, orgid);
			q.setParameter(i++, orgid);
		}
		if(inn!=null && !inn.trim().isEmpty())
			q.setParameter(i++, inn);
		@SuppressWarnings("unchecked")
        List<BigDecimal> list = q.getResultList();
		for (BigDecimal b : list) {
		    res.add(b.longValue());
		}
		return res;
	}


    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persist(Object entity) {
        EntityManager em = factory.createEntityManager();
        em.persist(entity);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(java.lang.Class cl, java.lang.Object key) {
        EntityManager em = factory.createEntityManager();
        em.remove(em.find(cl, key));
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdCreditTurnoverPremiumJPA> findCdCreditTurnoverPremium() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM CdCreditTurnoverPremiumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdRiskpremiumJPA> findCdRiskpremium() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM CdRiskpremiumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdPremiumTypeJPA> findRiskpremiumType(CdPremiumTypeJPA.Type type) {
        EntityManager em = factory.createEntityManager();
        if(type==null)
            return em.createQuery("SELECT u FROM CdPremiumTypeJPA u").getResultList();
        Query query = em.createQuery("SELECT u FROM CdPremiumTypeJPA u where u.trade_type = :type" +
				" or u.trade_type ='" + CdPremiumTypeJPA.Type.COMMON.getName() + "'");
        query.setParameter("type", type.getName());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdCreditTurnoverCriteriumJPA> findCdCreditTurnoverCriterium() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM CdCreditTurnoverCriteriumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StavspredJPA> findStavspredJPA(String cur, Long period) {
        ArrayList<StavspredJPA> res = new ArrayList<StavspredJPA>();
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM StavspredJPA u where u.unit = :unit");
        query.setParameter("unit", cur);
        res = (ArrayList<StavspredJPA>) query.getResultList();
        if(period==null)
            return res;
        
        ArrayList<StavspredJPA> periodFilter = new ArrayList<StavspredJPA>();
        for (StavspredJPA s : res){
            if(s.getDays_from()==null || s.getDays_from()<=period){
                if(s.getDays_to()==null || s.getDays_to()>=period){
                    periodFilter.add(s);
                }
            }
        }
        return periodFilter;
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PunitiveMeasureJPA> findPunitiveMeasure(String sanction_type) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM PunitiveMeasureJPA u where u.is_active=1 and (u.sanction_type = :sanction_type " +
				" or u.sanction_type ='" + PunitiveMeasure.SanctionType.COMMONSANCTION.getDescription() + "')");
        query.setParameter("sanction_type", sanction_type);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DependingLoanJPA> findDependingLoan(String cur, Long period) {
        ArrayList<DependingLoanJPA> res = new ArrayList<DependingLoanJPA>();
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM DependingLoanJPA u where u.id_currency = :unit");
        query.setParameter("unit", cur);
        res = (ArrayList<DependingLoanJPA>) query.getResultList();
        if(period==null)
            return res;
        
        ArrayList<DependingLoanJPA> periodFilter = new ArrayList<DependingLoanJPA>();
        for (DependingLoanJPA s : res){
            if(s.getDays_from()==null || s.getDays_from()<=period){
                if(s.getDays_to()==null || s.getDays_to()>=period){
                    periodFilter.add(s);
                }
            }
        }
        return periodFilter;
    }

    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logRequest(RequestLogJPA log) {
        EntityManager em = factory.createEntityManager();
        em.persist(log);
        LOGGER.info("logged reauest send " + log.getTask().getId());
    }

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<RequestLogJPA> getRequestLogList(Long idTask) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT c FROM RequestLogJPA c join c.task t where t.id = :idMdTask order by c.id desc");
        query.setParameter("idMdTask", idTask);
        return query.getResultList();
	}

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<CdAcredetivSourcePaymentJPA> findAcredetivSourcePayment() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM CdAcredetivSourcePaymentJPA u").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<FundDownJPA> findFundDown() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM FundDownJPA u").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<ProductTypeJPA> findProductType() {
        return factory.createEntityManager().
            createQuery("SELECT u FROM ProductTypeJPA u where u.is_active=1").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String findParentHash(TaskJPA taskJPA, Task copyTo) {
    	if (taskJPA.isLimit() || taskJPA.isSublimit()) return "['dummy']";
        try {
        	// МК : пока не используем
        	//parentJPA = getTask(parentJPA.getId());
        	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        	//parent = processor.getTask(new Task(new Long(parentJPA.getId())));//долгая функция

            ArrayList<TaskTarget> targetList = new ArrayList<TaskTarget>(); 
            ArrayList<TaskCurrency> currencyList = new ArrayList<TaskCurrency>();
            ArrayList<TaskProduct> productList = new ArrayList<TaskProduct>();    // виды сделки
            ArrayList<OtherCondition> dilatory = new ArrayList<OtherCondition>(); // 1L Отлагательные условия заключения сделки
            ArrayList<OtherCondition> akkredetive = new ArrayList<OtherCondition>(); // 7L Отлагательные условия открытия аккредетива / выдачи гарантии (документарная сделка),
            ArrayList<OtherCondition> credit = new ArrayList<OtherCondition>(); // 2L Отлагательные условия использования кредитных средств
            ArrayList<OtherCondition> additional = new ArrayList<OtherCondition>(); // 3L Дополнительные условия сделки 
            boolean projectFin = false;

            boolean allFilled = false;
            
            // получаем данные циклично, пока не соберем все заполненные данные либо пока не дойдем до самого верхнего уровня . 
        	Task task = processor.getTask(new Task(taskJPA.getId())); 
        	if (!(task.isLimit() || task.isSubLimit())) {
        		Task loopTask = task;
        		//Найдем виды сделок для вышележащего лимита или сублимита
                while ((!allFilled) && (loopTask.getParent() != null) && (!loopTask.getParent().equals(0L))) {

                	// установим флажки, нужно ли заполнять требуемые поля. Если они уже заполнены, то НЕ НУЖНО их заполнять.
                	boolean fillTarget = targetList.isEmpty();
                	boolean fillCurrency = currencyList.isEmpty();
                	boolean fillTaskProduct = productList.isEmpty();
                	boolean fillProjectFin = !projectFin;

                	// заполним требуемые поля
                	Task parent = processor.getTask(new Task(loopTask.getParent())); //долгая функция 
                	//if ((!parent.getTarget().isEmpty()) && fillTarget) targetList = parent.getTarget();
                	if ((!parent.getCurrencyList().isEmpty()) && fillCurrency) currencyList = parent.getCurrencyList();
                	if ((!parent.getHeader().getOpportunityTypes().isEmpty()) && fillTaskProduct) productList = parent.getHeader().getOpportunityTypes();
                	if ((parent.getMain().isProjectFin()) && fillProjectFin) projectFin = true;
                	
                	// условия окончания поиска по иерархии -- все поля заполнены!
                	allFilled = (!targetList.isEmpty())
                		&& (!currencyList.isEmpty())
                		&& (!productList.isEmpty())
                		&& (!dilatory.isEmpty())
                		&& (!akkredetive.isEmpty())
                		&& (!credit.isEmpty())
                		&& (!additional.isEmpty())
                		&& projectFin;

                	// цикл итерации: идем выше по иерархии.
                	loopTask = parent;
                }
    		}
        	Task parent = task;
        	while(parent.getParent()!=null && !parent.getParent().equals(0L)){
        		parent = processor.getTask(new Task(parent.getParent())); 
        		
        		// раскидаем дополнительные условия по массивам
        		if (!parent.getOtherCondition().isEmpty()) {
        			for (OtherCondition cond : parent.getOtherCondition()) {
        				if (cond.getType().equals(1L)) dilatory.add(cond);
        				if (cond.getType().equals(7L)) akkredetive.add(cond);
        				if (cond.getType().equals(2L)) credit.add(cond);
        				if (cond.getType().equals(3L)) additional.add(cond);
        			}
        		}
        	}

        	// Скопируем полученные данные в copyTo для дальнейшего использования.
        	if (copyTo != null) {
        		copyTo.setCurrencyList(currencyList);
        		copyTo.getMain().setProjectFin(projectFin);
        		copyTo.getOtherCondition().clear();
        		copyTo.getOtherCondition().addAll(dilatory);
        		copyTo.getOtherCondition().addAll(akkredetive);
        		copyTo.getOtherCondition().addAll(credit);
        		copyTo.getOtherCondition().addAll(additional);
        		
        		// положим вид продукта из выбранных значений видов сделок
        		if (copyTo.getHeader().getOpportunityTypes() == null) copyTo.getHeader().setOpportunityTypes(new ArrayList<TaskProduct>());
        		if (productList.size() == 1) {
        			copyTo.getHeader().getOpportunityTypes().add(productList.get(0));
        		} // в противном случае (когда их много или ни одного) ничего не добавляем.
        	}
        	
        	// Соберем полученные данные вместе.
        	// ['target_k6UJ9A00089Q', 'target_k6UJ9A00080L', 'main_currencyList_RUR', 'main_currencyList_EUR', 'main_currencyList_USD', 
        	// 'main_projectFin_chk_true', 'Категория качества ссуды_II', 'Штрафные санкции_за просроченную задолженность по начисленным и неуплаченным процентам (комиссиям)', 'Штрафные санкцииa0e7474d-5bb8-444c-8599-591bdbcb3519_1']	
            StringBuilder sb = new StringBuilder();

        	boolean first = true;

        	// добавим цели кредитования
        	boolean emptyTarget = true;
        	for (TaskTarget target : targetList)
        		if (target.isFlag()) {
        			first = addToParentList(first, sb, "target_" + target.getTargetType().getId());
        			emptyTarget = false;
        		}
        	if (emptyTarget) first = addToParentList(first, sb, "target_ANY");
        	
        	// добавим допустимые валюты
        	boolean emptyCurrency = true;
        	for (TaskCurrency currency: currencyList)
        		if (currency.isFlag()) {
        			first = addToParentList(first, sb, "main_currencyList_" + currency.getCurrency().getCode());
        			emptyCurrency = false;
        		}
        	if (emptyCurrency) first = addToParentList(first, sb, "main_currencyList_ANY");
        	
        	// добавим допустимые виды сделок
        	for (TaskProduct product : productList)
        		first = addToParentList(first, sb, "Вид кредитной сделки_" + product.getId());
        	if(productList.isEmpty()) first = addToParentList(first, sb, "Вид кредитной сделки_ANY");
        	
        	// Добавим проектное финансирование
        	if (projectFin) first = addToParentList(first, sb, "main_projectFin_chk_true");
        	else first = addToParentList(first, sb, "main_projectFin_chk_false");
        	
        	LOGGER.info("parentHash "+ "[" +  sb.toString() + "];");	
        	return "[" +  sb.toString() + "];";
        	
        } catch (Exception e) {
        	LOGGER.info(e.getMessage());
        	e.printStackTrace();
        	return "['dummy']";
        }
    }
    
    private boolean addToParentList(boolean first, StringBuilder sb, String value) {
        if (first) {
            sb.append("'" + value  + "'");
            first = false;
        } else  
            sb.append(", '" + value + "'");
        return first;
    }
    
    /**
     * Removes newline, carriage return and tab characters from a string.
     *
     * @param toBeEscaped string to escape
     * @return the escaped string
     */
    public static String removeFormattingCharacters(final String toBeEscaped) {
        StringBuffer escapedBuffer = new StringBuffer();
        for (int i = 0; i < toBeEscaped.length(); i++) {
            if ((toBeEscaped.charAt(i) != '\n') && (toBeEscaped.charAt(i) != '\r') && (toBeEscaped.charAt(i) != '\t')) {
                escapedBuffer.append(toBeEscaped.charAt(i));
            }
        }
        String s = escapedBuffer.toString();
        return s;//
        // Strings.replaceSubString(s, "\"", "")
    }
    
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getNumberDisplayWithRoot(Long idTask) {
    	TaskJPA task = getTask(idTask);
    	if (task != null) return task.getNumberDisplayWithRoot();
    	else return "";
    }

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void savePeriodObKind(HashMap<Long, HashMap<Long, Double>> map) {
		EntityManager em = factory.createEntityManager();
		for(Long idperiod : map.keySet()){
			em.createNativeQuery("delete from r_period_obkind where id_factpercent="+idperiod.toString()).executeUpdate();
			for(Long idObKind : map.get(idperiod).keySet()) {
				LOGGER.info("savePeriodObKind: idperiod=" + idperiod.toString());
				em.createNativeQuery("insert into r_period_obkind (id, id_factpercent ,id_ob_kind,supplyvalue) " +
						"values (r_period_obkind_seq.nextval,"+idperiod.toString()+","+idObKind.toString()+","+
						map.get(idperiod).get(idObKind).toString()+")").executeUpdate();
			}
		}
	}
	
	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TaskJPA updateDataJPA(ServletRequest request, Long updatedTask) throws FactoryException {
		long tstart = System.currentTimeMillis();
		//грубо нарушаю MVC для того чтобы find & merge были от одного entityManager
		EntityManager em = factory.createEntityManager();
		TaskJPA task = getTask(updatedTask);

		task.setIndcondition(request.getParameter("indcondition") != null
				&& request.getParameter("indcondition").equals("y"));

		//обновить по первому периоду
		if (request.getParameter("updatePmSection")!=null
				&& task.getPeriods().size()>1){
			Long firstPeriodId =task.getPeriods().get(0).getId();
			Query q = em.createNativeQuery("update factpercent set interest_rate_fixed=? where id=?");
			q.setParameter(1,request.getParameter("mdTask_fixedRate")!=null?1L:0L);
			q.setParameter(2,firstPeriodId);
			q.executeUpdate();
			q = em.createNativeQuery("update factpercent set interest_rate_derivative=? where id=?");
			q.setParameter(1,request.getParameter("mdTask_floatRate")!=null?1L:0L);
			q.setParameter(2,firstPeriodId);
			q.executeUpdate();
		}
		String asource = request.getParameter("acredetiv_source");
		if (asource != null) {
			task.setAcredetivSourcePayment(null);
			for (ru.md.spo.dbobjects.CdAcredetivSourcePaymentJPA ac : findAcredetivSourcePayment()) {
				if (ac.getId().toString().equals(asource)) {
					task.setAcredetivSourcePayment(ac);
				}
			}
		}
		if (request.getParameter("fineList_section") != null) {
			task.setRate2(Formatter.parseDouble(request.getParameter("rate2")));
			task.setRate2Note(request.getParameter("rate2Note"));
		}
		if (request.getParameter("title") != null)
			task.setTitle(request.getParameter("title"));

		if (request.getParameter("exchangedate") != null) {
			task.setExchangedate(Formatter.parseDate(request.getParameter("exchangedate")));
		}
		if (request.getParameter("Секция_основные параметры") != null && !task.isProduct()) {
			for (IndConditionJPA c : task.getIndConditions()) {
				c.setTask(null);
				em.remove(em.find(IndConditionJPA.class, c.getId()));
			}
			task.setIndConditions(new ArrayList<IndConditionJPA>());
			if (request.getParameter("main_indConditions") != null) {
				for (String indConditions : request.getParameterValues("main_indConditions")) {
					if (indConditions == null || indConditions.isEmpty())
						continue;
					IndConditionJPA c = new IndConditionJPA();
					c.setCondition(indConditions);
					c.setTask(task);
					task.getIndConditions().add(c);
					em.persist(c);
				}
			}

			for (OperDecisionJPA od : task.getOperDecision()) {
				od.setTask(null);
				em.remove(em.find(OperDecisionJPA.class, od.getId()));
			}
			task.setOperDecision(new ArrayList<OperDecisionJPA>());
			if (request.getParameter("main_operationDecisionList_accepted") != null) {
				for (int i = 0; i < request.getParameterValues("main_operationDecisionList_accepted").length; i++) {
					String accepted = request.getParameterValues("main_operationDecisionList_accepted")[i];
					OperDecisionJPA c = new OperDecisionJPA();
					c.setTask(task);
					c.setAccepted(accepted);
					c.setSpecials(request.getParameterValues("main_operationDecisionList_specials")[i]);
					task.getOperDecision().add(c);
					em.persist(c);
					c.setDescriptions(new ArrayList<OperDecisionDescriptionJPA>());
					String main_operationDecisionId = request.getParameterValues("main_operationDecisionId")[i];
					if (request.getParameter("main_operationDecisionList_desc" + main_operationDecisionId) != null)
						for (String desc : request.getParameterValues("main_operationDecisionList_desc"
								+ main_operationDecisionId)) {
							OperDecisionDescriptionJPA operDesc = new OperDecisionDescriptionJPA(desc, c);
							c.getDescriptions().add(operDesc);
							em.persist(operDesc);
						}
				}
			}
			for (ProductGroupJPA od : task.getProductGroupList()) {
				od.setTask(null);
				em.remove(em.find(ProductGroupJPA.class, od.getId()));
			}
			task.setProductGroupList(new ArrayList<ProductGroupJPA>());
			if (request.getParameter("productGroup") != null) {
				for (int i = 0; i < request.getParameterValues("productGroup").length; i++) {
					ProductGroupJPA pg = new ProductGroupJPA();
					pg.setTask(task);
					pg.setName(request.getParameterValues("productGroup")[i]);
					try {
						pg.setCmnt(request.getParameter("productTypeCmnt"
								+ request.getParameterValues("productGroupId")[i]));
						pg.setPeriod(Formatter.parseLong(request.getParameter("productTypePeriod"
								+ request.getParameterValues("productGroupId")[i])));
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
					task.getProductGroupList().add(pg);
					em.persist(pg);
				}
			}
		}
		if (request.getParameter("clearInLimit") != null
				&& request.getParameter("clearInLimit").equals("y")) {
			task.setParent(null);
		}
		if (request.getParameter("inlimitID") != null && !request.getParameter("inlimitID").isEmpty()) {
			task.setParent(getTask(Long.valueOf(request.getParameter("inlimitID"))));
		}
		if (request.getParameter("graph_section") != null) {
			task.setAmortized_loan(request.getParameter("amortized_loan") != null);
			task.setPmnOrder(request.getParameter("pmn_order"));
		}
		task.setWith_sublimit("n");
		if (request.getParameter("with_sublimit") != null)
			task.setWith_sublimit(request.getParameter("with_sublimit"));
		if (request.getParameter("limit_riskpremium") != null) {
			for (CdRiskpremiumJPA to : findCdRiskpremium()) {
				if (to.getId().toString().equals(request.getParameter("limit_riskpremium")))
					task.setRiskpremium(to);
			}
			task.setRiskpremium_change(Formatter.parseDouble(request.getParameter("riskpremium_change")));
		}
		if (request.getParameter("limit_turnover_premium") != null) {
			for (CdCreditTurnoverPremiumJPA to : findCdCreditTurnoverPremium()) {
				if (to.getId().toString().equals(request.getParameter("limit_turnover_premium")))
					task.setTurnoverPremium(to);
			}
		}
		if (request.getParameter("limit_turnover") != null) {
			task.setTurnover(Formatter.parseDouble(request.getParameter("limit_turnover")));
		}
		if (request.getParameter("definition") != null) {
			task.setDefinition(request.getParameter("definition"));
		}
		if (request.getParameter("generalcondition") != null) {
			task.setGeneralcondition(request.getParameter("generalcondition"));
		}
		if (request.getParameter("contract") != null) {
			for (ContractJPA c : task.getContracts()) {
				c.setTask(null);
				em.remove(em.find(ContractJPA.class, c.getId()));
			}
			task.setContracts(new ArrayList<ContractJPA>());
			for (String contract : request.getParameterValues("contract")) {
				if (contract == null || contract.isEmpty())
					continue;
				ContractJPA c = new ContractJPA(task, contract);
				task.getContracts().add(c);
				em.persist(c);
			}
		}
		if (request.getParameter("show_section_promissory_note") != null) {
			for (PromissoryNoteJPA pn : task.getPromissoryNotes()) {
				pn.setTask(null);
				em.remove(em.find(PromissoryNoteJPA.class, pn.getId()));
			}
			task.setPromissoryNotes(new ArrayList<PromissoryNoteJPA>());
			if (request.getParameter("promissory_note_holder") != null)
				for (int i = 0; i < request.getParameterValues("promissory_note_holder").length; i++) {
					PromissoryNoteJPA pn = new PromissoryNoteJPA();
					pn.setTask(task);
					pn.setHolder(request.getParameterValues("promissory_note_holder")[i]);
					pn.setCurrency(request.getParameterValues("currency_promissory_note")[i]);
					pn.setMaxdate(Formatter.parseDate(request.getParameterValues("promissory_note_date")[i]));
					pn.setPerc(Formatter.parseDouble(request.getParameterValues("promissory_note_per")[i]));
					pn.setPlace(request.getParameterValues("promissory_note_place")[i]);
					pn.setVal(Formatter.parseDouble(request.getParameterValues("promissory_note_val")[i]));

					task.getPromissoryNotes().add(pn);
					em.persist(pn);
				}
		}
		if (request.getParameter("percentStavka_section") != null) {
			if(request.getParameter("monitoringmode")!=null){//сохраняем во временную копию
				//найти копию и обновить
				if(task.getMonitoringMdtask()!=null){
					TaskJPA mon_copy = getTask(task.getMonitoringMdtask());

					for (PremiumJPA p : mon_copy.getPremiumList()) {
						p.setTask(null);
						em.remove(em.find(PremiumJPA.class, p.getId()));
					}
					mon_copy.setPremiumList(new ArrayList<PremiumJPA>());
					if (request.getParameter("premiumtype") != null)
						for (int i = 0; i < request.getParameterValues("premiumtype").length; i++) {
							for (CdPremiumTypeJPA pt : findRiskpremiumType(null)) {
								if (pt.getId().toString().equals(request.getParameterValues("premiumtype")[i])) {
									PremiumJPA p = new PremiumJPA();
									p.setTask(mon_copy);
									p.setPremiumType(pt);
									String value = pt.getValue();
									if ("Валюта".equals(value)) {
										p.setCurr(request.getParameterValues("premiumcurr")[i]);
										p.setVal(Formatter.parseDouble(request.getParameterValues("premiumvalue")[i]));
									}
									if ("Валюта/ %".equals(value)) {
										p.setCurr(request.getParameterValues("premiumcurrpercent")[i]);
										p.setVal(Formatter.parseDouble(request.getParameterValues("premiumvalue")[i]));
									}
									if ("Формула".equals(value)) {
										p.setText(request.getParameterValues("premiumtext")[i]);
									}
									em.persist(p);
								}
							}
						}
					for (IndrateMdtaskJPA p : mon_copy.getIndrates()) {
						p.setTask(null);
						em.remove(em.find(IndrateMdtaskJPA.class, p.getId()));
					}
					if (request.getParameter("indRate") != null)
						for (int i = 0; i < request.getParameterValues("indRate").length; i++) {
							String indRate = request.getParameterValues("indRate")[i];
							if(indRate==null || indRate.isEmpty() || indRate.equalsIgnoreCase("null")) continue;
							IndrateMdtaskJPA p = new IndrateMdtaskJPA();
							p.setTask(mon_copy);
							p.setIndrate(indRate);
							p.setReason(request.getParameterValues("indRateReason")[i]);
							p.setUsefrom(ru.masterdm.spo.utils.Formatter.parseDate(request.getParameterValues("indRateReason_usefrom")[i]));
							p.setValue(ru.masterdm.spo.utils.Formatter.parseBigDecimal(request.getParameterValues("indRate_value")[i]));
							p.setRate(Formatter.parseBigDecimal(request.getParameterValues("rate_ind_rate")[i]));
							em.persist(p);
						}

					mon_copy.setFixrate(request.getParameter("fixrate_percent") != null);
					mon_copy.setInterestRateDerivative(request.getParameter("interest_rate_derivative") != null);
					mon_copy.setInterestRateFixed(request.getParameter("interest_rate_fixed") != null);
					if (request.getParameter("fundDown") != null) {
						mon_copy.setFundDown(null);
						for (ru.md.spo.dbobjects.FundDownJPA fundDown : findFundDown()) {
							if (fundDown.getId().equals(request.getParameter("fundDown")))
								mon_copy.setFundDown(fundDown);
						}
					}
					mon_copy.setRate5((Formatter.parseDouble(request.getParameter("rate5"))));
					mon_copy.setRate6((Formatter.parseDouble(request.getParameter("rate6"))));
					mon_copy.setRate7((Formatter.parseDouble(request.getParameter("rate7"))));
					mon_copy.setRate8((Formatter.parseDouble(request.getParameter("rate8"))));
					merge(mon_copy);

					percentFact(request, em, mon_copy);
				}
			} else {//обычное сохранение заявки
				for (PremiumJPA p : task.getPremiumList()) {
					p.setTask(null);
					em.remove(em.find(PremiumJPA.class, p.getId()));
				}
				task.setPremiumList(new ArrayList<PremiumJPA>());
				if (request.getParameter("premiumtype") != null)
					for (int i = 0; i < request.getParameterValues("premiumtype").length; i++) {
						for (CdPremiumTypeJPA pt : findRiskpremiumType(null)) {
							if (pt.getId().toString().equals(request.getParameterValues("premiumtype")[i])) {
								PremiumJPA p = new PremiumJPA();
								p.setTask(task);
								p.setPremiumType(pt);
								String value = pt.getValue();
								if ("Валюта".equals(value)) {
									p.setCurr(request.getParameterValues("premiumcurr")[i]);
									p.setVal(Formatter.parseDouble(request.getParameterValues("premiumvalue")[i]));
								}
								if ("Валюта/ %".equals(value)) {
									p.setCurr(request.getParameterValues("premiumcurrpercent")[i]);
									p.setVal(Formatter.parseDouble(request.getParameterValues("premiumvalue")[i]));
								}
								if ("Формула".equals(value)) {
									p.setText(request.getParameterValues("premiumtext")[i]);
								}
								em.persist(p);
							}
						}
					}
				for (IndrateMdtaskJPA p : task.getIndrates()) {
					p.setTask(null);
					em.remove(em.find(IndrateMdtaskJPA.class, p.getId()));
				}
				if (request.getParameter("indRate") != null)
					for (int i = 0; i < request.getParameterValues("indRate").length; i++) {
						String indRate = request.getParameterValues("indRate")[i];
						if(indRate==null || indRate.isEmpty() || indRate.equalsIgnoreCase("null")) continue;
						IndrateMdtaskJPA p = new IndrateMdtaskJPA();
						p.setTask(task);
						p.setIndrate(indRate);
						p.setReason(request.getParameterValues("indRateReason")[i]);
						p.setUsefrom(ru.masterdm.spo.utils.Formatter.parseDate(request.getParameterValues("indRateReason_usefrom")[i]));
						p.setValue(ru.masterdm.spo.utils.Formatter.parseBigDecimal(request.getParameterValues("indRate_value")[i]));
						p.setRate(Formatter.parseBigDecimal(request.getParameterValues("rate_ind_rate")[i]));
						em.persist(p);
					}
				if (!task.isFixrate() && request.getParameter("fixrate_percent") != null) {// только что установили
					task.setFixratedate(new Date());
				}
				task.setFixrate(request.getParameter("fixrate_percent") != null);
				task.setIs_fixed(request.getParameter("RateTypeFixed"));
				task.setInterestRateDerivative(request.getParameter("interest_rate_derivative")!=null);
				task.setInterestRateFixed(request.getParameter("interest_rate_fixed")!=null);
				//task.setIs_fixed(task.isInterestRateFixed()?"y":"n");//для обратной совместимости

				if (request.getParameter("fundDown") != null) {
					task.setFundDown(null);
					for (ru.md.spo.dbobjects.FundDownJPA fundDown : findFundDown()) {
						if (fundDown.getId().equals(request.getParameter("fundDown")))
							task.setFundDown(fundDown);
					}
				}
				task.setInd_rate(request.getParameter("indRate"));
				task.setRate5((Formatter.parseDouble(request.getParameter("rate5"))));
				task.setRate6((Formatter.parseDouble(request.getParameter("rate6"))));
				task.setRate7((Formatter.parseDouble(request.getParameter("rate7"))));
				task.setRate8((Formatter.parseDouble(request.getParameter("rate8"))));

				percentFact(request, em, task);
			}
		}
		if (request.getParameter("Секция_основные параметры") != null) {
			if (task.isProduct())
				task.setTargetTypeControlNote(request.getParameter("targetTypeControlNote"));
			task.setTrance_graph(request.getParameter("trance_graph") != null);
			task.setTrance_hard_graph(request.getParameter("trance_hard_graph") != null);
			task.setTrance_limit_excess(request.getParameter("trance_limit_excess") != null);
			task.setTrance_limit_use(request.getParameter("trance_limit_use") != null);
			task.setTrance_period_format(request.getParameter("trance_period_format"));
		}
		if (request.getParameter("earlyPaymentTemplate") != null) {
			task.setEarly_payment_prohibition(request.getParameter("early_payment_prohibition") != null);
			if (request.getParameter("early_payment_prohibition_period") == null
					|| request.getParameter("early_payment_prohibition_period").isEmpty()) {
				task.setEarly_payment_proh_per(null);
			}
			else {
				try {
					task.setEarly_payment_proh_per(Long.valueOf(request
							.getParameter("early_payment_prohibition_period")));
				}
				catch (Exception e) {
					task.setEarly_payment_proh_per(null);
					LOGGER.warn(e.getMessage(), e);
					LOGGER.warn("early_payment_prohibition_period="
							+ request.getParameter("early_payment_prohibition_period"));
				}
			}
		}
		if (request.getParameter("active_decision") != null)
			task.setActive_decision(request.getParameter("active_decision"));
		if(request.getParameter("section_decision")!=null){
			task.setProductMonitoring(request.getParameter("product_monitoring_decision")==null?0L:1L);
			task.setAdditionalContract(request.getParameter("additional_contract_decision")==null?0L:1L);
		}
		if(request.getParameter("cross_sell_type")!=null)
			task.setCrossSellType(Formatter.parseLong(request.getParameter("cross_sell_type")));
		em.merge(task);

		Long loadTime = System.currentTimeMillis() - tstart;
		LOGGER.warn("*** updateDataJPA() time " + loadTime);
		return task;
	}

	private void percentFact(ServletRequest request, EntityManager em, TaskJPA task) {
		TaskFacadeLocal taskFacade = null;
		try{
			taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		} catch (Exception e){}


		if (request.getParameter("percentFact_section") != null) {
			List<FactPercentJPA> oldFactPercents = new ArrayList<FactPercentJPA>();
			List<FactPercentJPA> oldFactPercentsTrance = new ArrayList<FactPercentJPA>();
			if (request.getParameter("projectTeam") != null && request.getParameter("projectTeam").equalsIgnoreCase("true")) {
				List oldMainObjects = em.createQuery(
						"SELECT f FROM FactPercentJPA f WHERE f.task.id = " + task.getId().toString()
								+ " AND f.trance.id IS NULL").getResultList();
				for (Object obj : oldMainObjects)
					oldFactPercents.add((FactPercentJPA) obj);
				List oldObjects = em.createQuery(
						"SELECT f FROM FactPercentJPA f WHERE f.task.id = " + task.getId().toString()
								+ " AND f.trance.id IS NOT NULL").getResultList();
				for (Object obj : oldObjects)
					oldFactPercentsTrance.add((FactPercentJPA) obj);
			}
			em.createNativeQuery("delete from factpercent where id_mdtask=" + task.getId().toString())
					.executeUpdate();

			if (request.getParameter("trfondrate") != null) {
				for (int i = 0; i < request.getParameterValues("trfondrate").length; i++) {
					String trid = request.getParameterValues("trid")[i];
					/*if (request.getParameterValues("trance_id") != null)
						for (String id : request.getParameterValues("trance_id")) {
							if (request.getParameterValues("trid")[i].equals(id))
								trid = id;
						}*/
					if (trid == null)
						continue;
					BigDecimal cnt = (BigDecimal) em.createNativeQuery(
							"select count(*) from trance t where id=" + trid).getSingleResult();
					if (cnt.longValue() == 0L)
						continue;
					FactPercentJPA oldFactTrance = null;
					for (FactPercentJPA trance : oldFactPercentsTrance)
						if (trance.getTranceId().toString().equals(trid))
							oldFactTrance = trance;
					String riskStepupFactor = request.getParameterValues("trriskStepupFactor")[i];
					if (riskStepupFactor.isEmpty()) {
						riskStepupFactor = "NULL";
					}
					else {
						riskStepupFactor = "'" + riskStepupFactor + "'";
					}
					String rating_riskStepupFactor = "";// request.getParameterValues("trrating_riskStepupFactor")[i];
					if (rating_riskStepupFactor.isEmpty()) {
						rating_riskStepupFactor = "NULL";
					}
					else {
						rating_riskStepupFactor = "'" + rating_riskStepupFactor + "'";
					}
					String sql = "insert into factpercent(id,fondrate,riskpremium,riskpremiumtype,riskpremium_change,"
							+ "rate2,rate3,rate4,rate11,tranceId,rate5,rate6,rate9,rate10,rating_fondrate,rating_rate3,"
							+ "rating_calc,rating_riskpremium,rating_c1,rating_c2,id_mdtask,riskStepupFactor,"
							+ "rating_riskStepupFactor,manual_fondrate,RATE4DESC) "
							+ "values(factpercent_seq.nextval,"
							+ parseSqlParam(request.getParameterValues("trfondrate")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("trriskpremium")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("trriskpremiumtype")[i]) + ", "
							+ parseSqlParam(oldFactTrance != null ? (oldFactTrance.getRiskpremium_change() != null ? oldFactTrance
									.getRiskpremium_change().toString() : "")
									: (request.getParameterValues("trriskpremium_change") == null
											|| request.getParameterValues("trriskpremium_change").length == 0 ? ""
											: request.getParameterValues("trriskpremium_change")[i])) + ", "
							+ parseSqlParam("0") + ", "
							+ parseSqlParam(request.getParameterValues("trrate3")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("trrate4")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("trrate11")[i]) + ", "
							+ trid + ", "
							+ parseSqlParam(request.getParameterValues("trrate5")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("trrate6")[i]) + ", "
							// если редактирование из представления проектная команда,
							// то значения будут взяты старые
							+ parseSqlParam(oldFactTrance != null ? (oldFactTrance.getRate9() != null ?
									oldFactTrance.getRate9().toString() : "")
									: (request.getParameterValues("trrate9") == null
													|| request.getParameterValues("trrate9").length == 0 ? "" 
											: request.getParameterValues("trrate9")[i])) + ", "
							+ parseSqlParam(oldFactTrance != null ? (oldFactTrance.getRate10() != null ?
									oldFactTrance.getRate10().toString() : "")
									: (request.getParameterValues("trrate10") == null
													|| request.getParameterValues("trrate10").length == 0 ? "" 
											: request.getParameterValues("trrate10")[i])) + ", "
							/*
							 * parseSqlParam(request.getParameterValues("trrating_fondrate")[i])+", "+
							 * parseSqlParam(request.getParameterValues("trrating_rate3")[i])+", "+
							 * parseSqlParam(request.getParameterValues("trrating_calc")[i])+", "+
							 * parseSqlParam(request.getParameterValues("trrating_riskpremium")[i])+", "+
							 * parseSqlParam(request.getParameterValues("trrating_c1")[i])+", "+
							 * parseSqlParam(request.getParameterValues("trrating_c2")[i])+", "+
							 */
							+ "NULL,NULL,NULL,NULL,NULL,NULL,?,"
							+ riskStepupFactor + "," + rating_riskStepupFactor
							+ ",'" + request.getParameterValues("trance_fondrate_manual")[i] + "',?)";
					LOGGER.info(sql);
					Query q = em.createNativeQuery(sql);
					q.setParameter(1, task.getId());
					q.setParameter(2, oldFactTrance != null ? Formatter.str(oldFactTrance.getRate4Desc())
							: (request.getParameterValues("trrate4desc") == null
											|| request.getParameterValues("trrate4desc").length == 0 ? "" : 
									request.getParameterValues("trrate4desc")[i]));
					q.executeUpdate();
				}
			}

			if (request.getParameter("fondrate") != null) {
				for (int i = 0; i < request.getParameterValues("fondrate").length; i++) {
					String percentFactID = null;
					if (request.getParameterValues("percent_fact_id") != null && request.getParameterValues("percent_fact_id").length > i)
						percentFactID = request.getParameterValues("percent_fact_id")[i];
					boolean interest_rate_fixed = request.getParameter("interest_rate_fixed"+percentFactID) != null;
					boolean interest_rate_derivative = request.getParameter("interest_rate_derivative"+percentFactID) != null;
					FactPercentJPA oldFactPercent = null;
					if (oldFactPercents.size() > i)
						oldFactPercent = oldFactPercents.get(i);
					String premiumType = "NULL";
					String premiumvalue = "NULL";
					String premiumcurr = "NULL";
					String premiumtext = "NULL";
					String riskStepupFactor = request.getParameterValues("riskStepupFactor")[i];
					if (riskStepupFactor.isEmpty()) {
						riskStepupFactor = "NULL";
					}
					else {
						riskStepupFactor = "'" + riskStepupFactor + "'";
					}
					String rating_riskStepupFactor = "";// request.getParameterValues("rating_riskStepupFactor")[i];
					if (rating_riskStepupFactor.isEmpty()) {
						rating_riskStepupFactor = "NULL";
					}
					else {
						rating_riskStepupFactor = "'" + rating_riskStepupFactor + "'";
					}
					if (task.isDocumentary())
						for (CdPremiumTypeJPA pt : findRiskpremiumType(null)) {
							String type = request.getParameterValues("premiumtype")[i];
							if (pt.getId().toString().equals(type)) {
								premiumType = pt.getId().toString();
								String value = pt.getValue();
								if ("Валюта".equals(value)) {
									premiumcurr = "'" + request.getParameterValues("premiumcurr")[i] + "'";
									premiumvalue = parseSqlParam(request.getParameterValues("premiumvalue")[i]);
								}
								if ("Валюта/ %".equals(value)) {
									premiumcurr = "'" + request.getParameterValues("premiumcurrpercent")[i] + "'";
									premiumvalue = parseSqlParam(request.getParameterValues("premiumvalue")[i]);
								}
								if ("Формула".equals(value)) {
									premiumtext = "'" + request.getParameterValues("premiumtext")[i] + "'";
								}
							}
						}
					String sql = "insert into factpercent(id,fondrate,riskpremium,riskpremiumtype,riskpremium_change,"
							+ "rate2,rate3,rate4,rate11,rating_fondrate,rating_rate3,"
							+ "rating_calc,rating_riskpremium,rating_c1,rating_c2,"
							+ "premiumType,premiumvalue,premiumcurr,premiumtext,"
							+ "id_mdtask,supply,start_date,end_date,riskStepupFactor,rating_riskStepupFactor," +
							"interest_rate_fixed,interest_rate_derivative,"
							+ "manual_fondrate,INDCONDITION,RATE4DESC,usefrom,reason) "
							+ "values(factpercent_seq.nextval,"
							+ parseSqlParam(request.getParameterValues("fondrate")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("riskpremium")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("riskpremiumtype")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("riskpremium_change")[i]) + ", "
							+ parseSqlParam("0") + ", "
							+ parseSqlParam(request.getParameterValues("rate3")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("rate4")[i]) + ", "
							+ parseSqlParam(request.getParameterValues("rate11")[i]) + ", "
							/*
							 * parseSqlParam(request.getParameterValues("rating_fondrate")[i])+", "+
							 * parseSqlParam(request.getParameterValues("rating_rate3")[i])+", "+
							 * parseSqlParam(request.getParameterValues("rating_calc")[i])+", "+
							 * parseSqlParam(request.getParameterValues("rating_riskpremium")[i])+", "+
							 * parseSqlParam(request.getParameterValues("rating_с1")[i])+", "+
							 * parseSqlParam(request.getParameterValues("rating_с2")[i])+", "+
							 */
							+ "NULL,NULL,NULL,NULL,NULL,NULL,"
							+ premiumType + ", "
							+ premiumvalue + ", "
							+ premiumcurr + ", "
							+ premiumtext + ",?,?,?,?,"
							+ riskStepupFactor + ","
							+ rating_riskStepupFactor + ","
							+  (interest_rate_fixed?"1":"0") +","
							+  (interest_rate_derivative?"1":"0") +",'"
							+ request.getParameterValues("period_fondrate_manual")[i] + "',?,?,?,?)";
					LOGGER.info(sql);
					Query q = em.createNativeQuery(sql);
					q.setParameter(1, task.getId());
					q.setParameter(2, oldFactPercent != null ? Formatter.str(oldFactPercent.getSupply())
							: request.getParameterValues("supply") == null ? "" : 
							request.getParameterValues("supply")[i]);
					Date d1 = oldFactPercent != null ? oldFactPercent.getStart_date() : 
							request.getParameterValues("percentFactDate1") == null ? null : 
							Formatter.parseDate(request.getParameterValues("percentFactDate1")[i]);
					Date d2 = oldFactPercent != null ? oldFactPercent.getEnd_date() : 
							request.getParameterValues("percentFactDate2") == null ? null :
							Formatter.parseDate(request.getParameterValues("percentFactDate2")[i]);
					if (request.getParameterValues("fondrate").length < 2) {
						d1 = task.getProposed_dt_signing();
						d2 = task.getValidto();
						if (d2 == null)
							d2 = d1;
					}
					else {
						if (d1 == null)
							d1 = task.getValidto();
						if (d1 == null)
							d1 = task.getProposed_dt_signing();
						if (d2 == null)
							d2 = task.getValidto();
						if (d2 == null)
							d2 = task.getProposed_dt_signing();
					}
					if (d1 == null)
						d1 = new java.util.Date();
					if (d2 == null)
						d2 = new java.util.Date();
					q.setParameter(3, d1);
					q.setParameter(4, d2);
					q.setParameter(5, oldFactPercent != null ? Formatter.str(oldFactPercent.getIndcondition()) 
							: request.getParameterValues("factPercentIndCondition") == null ? ""
							: request.getParameterValues("factPercentIndCondition")[i]);
					q.setParameter(6, oldFactPercent != null ? Formatter.str(oldFactPercent.getRate4Desc())
							: request.getParameterValues("rate4desc") == null ? ""
							: request.getParameterValues("rate4desc")[i]);
					q.setParameter(7, Formatter.parseDateRobust(request.getParameterValues("rate4_usefrom")[i]), TemporalType.DATE);
					q.setParameter(8,request.getParameterValues("rate4_reason")[i]);
					q.executeUpdate();
					//indRate не знаю как справится с ORA-02287: sequence number not allowed here
					long currval = ((BigDecimal)em.createNativeQuery("select max(id) from factpercent").getSingleResult()).longValue();
					String[] rate_ind_rate_list = request.getParameterValues("rate_ind_rate_" + percentFactID);
					if(rate_ind_rate_list!=null)
						LOGGER.info(percentFactID+". rate_ind_rate_list size "+rate_ind_rate_list.length);
					if (request.getParameter("indRate"+percentFactID) != null)
						for (int j = 0; j < request.getParameterValues("indRate"+percentFactID).length; j++) {
							String indRate = request.getParameterValues("indRate"+percentFactID)[j];
							if(indRate==null || indRate.isEmpty() || indRate.equalsIgnoreCase("null")) continue;
							IndrateMdtaskJPA p = new IndrateMdtaskJPA();
							p.setTask(task);
							p.setIndrate(indRate);
							p.setIdFactpercent(Long.valueOf(currval));
							p.setRate(Formatter.parseBigDecimal(rate_ind_rate_list[j]));
							p.setReason(request.getParameterValues("indRateReason" + percentFactID)[j]);
							p.setUsefrom(ru.masterdm.spo.utils.Formatter.parseDate(request.getParameterValues("indRateReason_usefrom"+percentFactID)[j]));
							p.setValue(ru.masterdm.spo.utils.Formatter.parseBigDecimal(request.getParameterValues("indRate_value"+percentFactID)[j]));
							em.persist(p);
						}
				}
			}
		}
	}

	private String parseSqlParam(String value){
		if(value==null)
			return "NULL";
		if(value.isEmpty() || value.equals("NaN"))
			return "NULL";
		value = Formatter.trimSpace(value.replaceAll(",", "."));
		try {
	        Double.parseDouble(value.trim());
	        return value;
	    } catch (Exception e) {
	    	return "NULL";
	    }
	}

    @Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeExpertTeamJPA(Long id) {
        EntityManager em = factory.createEntityManager();
        em.remove(em.find(ExpertTeamJPA.class, id));
    }

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public PipelineJPA getPipeline(Long mdtaskid) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT u FROM PipelineJPA u where u.id_mdtask = :id");
        query.setParameter("id", mdtaskid);
        @SuppressWarnings("unchecked")
		List<PipelineJPA> list = query.getResultList();
		return list.size()>0?list.get(0):new PipelineJPA(mdtaskid);
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updatePipeline(PipelineJPA pipeline, String[] pipeline_fin_target) {
		//проверить существует ли старое значение
		boolean oldHideInReport = false;
		//история сохранения галочки не показывать в отчёте 
		try {
			oldHideInReport = factory.createEntityManager().find(PipelineJPA.class, pipeline.getId_mdtask()).getHideinreport().equalsIgnoreCase("y");
		} catch (Exception e) {
			//LOGGER.error(e.getMessage(), e);
		}
		EntityManager em = factory.createEntityManager();
		if(oldHideInReport!=(pipeline.getHideinreport().equalsIgnoreCase("y"))){
			Query query = em.createNativeQuery("insert into PIPELINE_HIDEINREPORT(id_mdtask,DATE_EVENT,flag) values(?,?,?)");
			query.setParameter(1, pipeline.getId_mdtask());
			query.setParameter(2, new Date());
			query.setParameter(3, pipeline.getHideinreport());
			query.executeUpdate();
		}
		Query query = em.createQuery("delete FROM PipelineJPA u where u.id_mdtask = :id");
        query.setParameter("id", pipeline.getId_mdtask());
        query.executeUpdate();
        em.persist(pipeline);
        query = em.createNativeQuery("delete from PIPELINE_FIN_TARGET where id_mdtask=?");
        query.setParameter(1, pipeline.getId_mdtask());
        query.executeUpdate();
        if(pipeline_fin_target==null)
        	return;
        query = em.createNativeQuery("insert into PIPELINE_FIN_TARGET(id_mdtask,val) values(?,?)");
        for(String ft : pipeline_fin_target){
        	query.setParameter(1, pipeline.getId_mdtask());
        	query.setParameter(2, ft);
        	query.executeUpdate();
        }
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> getPipelineFinTarget(Long mdtaskid) {
		EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("select val from PIPELINE_FIN_TARGET where id_mdtask=? and val is not null");
        query.setParameter(1, mdtaskid);
        return query.getResultList();
	}

	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Date getEdcLastUpdate(String ownerType, String ownerId) {
		Long id_entity=3L;
		if(ownerType.equals("1")) id_entity=1L;
		if(ownerType.equals("2")) id_entity=2L;
		EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("select max(e.SEARCH_DATE) from edc_entity_doc e where e.id_entity=? and e.id_object=?");
        query.setParameter(1, id_entity);
        query.setParameter(2, ownerId);
		return (Date) query.getSingleResult();
	}

	@Override  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void spoContractorSync(Long idMdtask) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("call SPO_CONTRACTOR_SYNC(?)");
		query.setParameter(1, idMdtask);
		query.executeUpdate();
		query = em.createNativeQuery("call SPO_DEPOSIT_SYNC()");
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<MdTaskNumber> getListOpportunityNumber(String organizationid) {
		HashMap<Long,MdTaskNumber> res = new LinkedHashMap<Long,MdTaskNumber>();
		EntityManager em = factory.createEntityManager();
		String filterSQL = isInteger(organizationid) ? "id_person=?" : "id_crmorg=?";
        Query query = em.createNativeQuery("select q.id_mdtask,t.mdtask_number,t.version,role " +
				"from (select id_mdtask,id_crmorg,null as id_person,ct.NAME_CONTRACTOR_TYPE as role " +
				"from r_org_mdtask r inner join R_CONTRACTOR_TYPE_MDTASK rct on rct.ID_R=r.ID_R " +
				"inner join CONTRACTOR_TYPE ct on ct.ID_CONTRACTOR_TYPE=rct.ID_CONTRACTOR_TYPE " +
				"union all select id_mdtask,org as id_crmorg,id_person,'Гарант' as role from garant " +
				"union all select id_mdtask,id_crmorg,id_person,'Залогодатель' as role from DEPOSIT " +
				"union all select id_mdtask,org,id_person,'Поручитель' as role from WARRANTY) q " +
				"inner join mdtask t on t.id_mdtask=q.id_mdtask where t.TASKTYPE='p' and " +
				filterSQL +
				" order by t.id_mdtask desc");
        query.setParameter(1, organizationid);
		ArrayList<Object> resultTable = (ArrayList<Object>) query.getResultList();
		for(Object row : resultTable){
			Object[] r = (Object[])row;
			MdTaskNumber n = new MdTaskNumber(((BigDecimal)r[0]).longValue(),r[1].toString() + " версия "+r[2].toString());
			if(!res.containsKey(n.getId()))
				res.put(n.getId(),n);
			res.get(n.getId()).getOrgRoleHash().add(r[3].toString());
		}
		List<MdTaskNumber> list = new ArrayList<MdTaskNumber>();
		for(Long id : res.keySet())
			list.add(res.get(id));
        return list;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public MdTask getOpportunityAttr(Long id) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("SELECT * FROM ( "+
				"select t.mdtask_number,t.product_name,t.currency, "+
				"CASE WHEN (t.is_limit_sum = 'y' AND is_debt_sum = 'y') THEN t.limit_issue_sum  ELSE mdtask_sum END, "+
				"(select min(e.date_event) from process_events e where e.id_process=t.id_pup_process) cr_date, "+
				"MAIN_CED_OFFICIAL_DATE, MAIN_CED_OFFICIAL_NUMBER, "+
				"WHO,ACCEPTED,NOTES,s.status,sr.status_return,t.period,t.perioddimension,t.validto "+
				"from mdtask t  "+
				"left outer join V_CED_CREDIT_DEAL_NUMBER ced on ced.id_mdtask=t.id_mdtask "+
				"left outer join KM_DEAL_STATUS_CHANGE km on km.ID_DEAL=t.id_mdtask "+
				"left outer join CD_DEAL_STATUS s on s.ID_DEAL_STATUS=km.ID_DEAL_STATUS "+
				"left outer join crm_status_return sr on sr.fb_spo_return_id=t.statusreturn "+
				"where t.id_mdtask=? order by km.id desc   "+
				") WHERE ROWNUM = 1 ");
		query.setParameter(1, id);
		@SuppressWarnings("unchecked")
		ArrayList<Object> resultTable = (ArrayList<Object>) query.getResultList();
		if(resultTable == null || resultTable.size()==0){
			LOGGER.warn("no mdtask id=" + id);
			return null;
		}
        Object[] r = (Object[]) resultTable.get(0);
        MdTask task = new MdTask(id, "");
        task.setKind((String) r[1]);
        task.setCur((String) r[2]);
        task.setSum((BigDecimal)r[3]);
        task.setDateCreate((Date) r[4]);
        task.setDateSogl((Date) r[5]);
        task.setNumberSogl((String) r[6]);
        task.setStatusWho((String) r[7]);
        task.setStatusAccepted((Date) r[8]);
        task.setStatusNotes((String) r[9]);
        task.setStatus((String) r[10]);
        task.setNumber(getTask(id).getNumberDisplay());
        task.setTaskUrl("");
        try {
        	NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        	task.setTaskUrl(notifyFacade.getBaseURL(null)+"/form.jsp?mdtaskid="+String.valueOf(id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
        task.setDecision((String)r[11]);
        task.setEndPeriod(r[12]+" "+(String)r[13]);
        task.setEndDate((Date) r[14]);
        
		return task;
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Deprecated
	public Long createTaskVersion(Long mdtaskid, Integer idTypeProcess, Long idUser, String oldRole,
			String newRole, UserJPA newUser, String comment, boolean asNextVersion) throws Exception {
		long exectime = System.currentTimeMillis();
		Task task = null;
		try {
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
					.getActionProcessor("Task");
			task = processor.getTask(new Task(mdtaskid));
		}
		catch (MappingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		boolean needAssign = false;
		Task newTask = createDomainTaskVersion(task, idTypeProcess, idUser, comment, needAssign, null, null);
		spoContractorSync(newTask.getId_task());
		TaskJPA newTaskJPA = copyJpaFieldsToNewVersion(mdtaskid, newTask.getId_task(), idUser, oldRole,
				newRole, newUser, newTask.getTranceList());
		if (newTaskJPA.getIdProcess() != null)
			pupFacade.setStandardPeriodVersion(newTaskJPA.getIdProcess());
		exectime = System.currentTimeMillis() - exectime;
		LOGGER.warn("*** createTaskVersion() time " + exectime);
		return newTaskJPA.getId();
	}

	@Override
	public Task createDomainTaskVersion(Task task, Integer idTypeProcess, Long idUser,
			String comment, boolean asNextVersion, boolean needAssign) throws Exception {
		return createDomainTaskVersion(task, idTypeProcess, idUser, comment, needAssign,
				null, asNextVersion ? null : 0L);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createDomainTaskVersion(Task task, Integer idTypeProcess, Long idUser,
			String comment, boolean needAssign, Long idParent, Long version) throws Exception {
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
				.getActionProcessor("Task");
		Long newPupId = null;
		ArrayList<Long> oldTrancesId = new ArrayList<Long>();
		// если версия для КОД - создание процесса не требуется
		if (idTypeProcess != null) {
			newPupId = pupFacade.createProcessWithAccept(idTypeProcess.longValue(), idUser, needAssign);
		}
		task.setId_pup_process(newPupId);
		task.setId_pup_process_type(idTypeProcess);
		if (task.getParent() == null || task.getParent().equals(0L))
			task.setParent(null);
		if (idParent!= null &&  !idParent.equals(0L))
			task.setParent(idParent);
		for (TaskContractor taskContractor : task.getContractors())
			taskContractor.setId(null);
		for (OtherCondition c : task.getOtherCondition())
			c.setId(null);
		for(OtherGoal g : task.getMain().getOtherGoals())
			g.setIdTarget(null);
		for (Trance trance : task.getTranceList()) {
			oldTrancesId.add(trance.getId());
			trance.setId(null);
		}
		if (comment == null || comment.isEmpty())
			comment = "пользователь не оставил комментарий";
		// Добавление комментария к процессу
		task.getComment().clear();
		Operator operator = idUser==null ? null :  new Operator(idUser.intValue());
		String cmnt = "Обоснование изменения параметров: " + comment;
		task.getComment().add(
				new Comment(null, cmnt, cmnt, operator, null, new Timestamp(System.currentTimeMillis())));

		if (version == null || !version.equals(0L))
			task.getHeader().setVersion(version);
		task.getTaskStatusReturn().setStatusReturn(null);
		task = processor.createTask(task);
		for (PaymentSchedule ps : task.getPaymentScheduleList()) {
			if (ps.getTranceId() != null)
				for(int j = 0; j < oldTrancesId.size(); j++) {
					if (ps.getTranceId().equals(oldTrancesId.get(j)))
						ps.setTranceId(task.getTranceList().get(j).getId());
				}
		}
		processor.updateTask(task);
		if (idTypeProcess != null) {
			pupFacade.updatePUPAttribute(newPupId, "Заявка №", task.getNumberDisplay());
			pupFacade.updatePUPAttribute(newPupId, "Тип кредитной заявки", task.getHeader()
					.getProcessType());
			pupFacade.updatePUPAttribute(newPupId, "Статус", "Начало работы по заявке");
		}
		return task;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public TaskJPA copyJpaFieldsToNewVersion(Long oldTaskId, Long newTaskId, Long idUser,
			String oldRole, String newRoleStr, UserJPA newUser, ArrayList<Trance> trances) {
		LOGGER.info("copyJpaFieldsToNewVersion idUser=" + idUser);
		EntityManager em = factory.createEntityManager();
		TaskJPA oldTaskJPA = em.find(TaskJPA.class, oldTaskId);
		TaskJPA taskJPA = em.find(TaskJPA.class, newTaskId);
		RoleJPA newRole = null;
		if (newRoleStr != null && !newRoleStr.isEmpty())
			newRole = pupFacade.getRole(newRoleStr, taskJPA.getIdTypeProcess());
		// Создание проектной команды
		boolean newUserInTeam = false;
		// для версии КОД назначение на роли невозможно, поскольку отсутствует процесс
		if (taskJPA.getProcess() != null) {
			for (ProjectTeamJPA opt : oldTaskJPA.getProjectTeam()) {
				if (newUser != null && newUser.getIdUser().equals(opt.getUser().getIdUser()))
					newUserInTeam = true;
				ProjectTeamJPA pt = new ProjectTeamJPA();
				pt.setTask(taskJPA);
				pt.setTeamType(opt.getTeamType());
				pt.setUser(opt.getUser());
				em.persist(pt);

				// назначение для проектных команд не мидл-офиса
				if (opt.getTeamType().equals("p"))
					for (RoleJPA optRole : opt.getUser().getRoles()) {
						if (pupFacade.isAssigned(opt.getUser().getIdUser(), optRole.getIdRole(), oldTaskJPA
								.getIdProcess())
								&& dictFacade.findProjectTeamRoles().contains(optRole.getNameRole()))
							try {
								// поиск аналогичной роли в новом процессе
								RoleJPA newRoleTmp = pupFacade.getRole(optRole.getNameRole(), taskJPA.getIdTypeProcess());
								// наличие аналогичной роли и отсутствие назначенных пользователей
								if (newRoleTmp != null && !pupFacade.isAssigned(newRoleTmp.getIdRole(),
										taskJPA.getIdProcess())) {
									// не требуется назначение по старой роли, если определена новая
									if (newRole == null || (newRole != null && !optRole.getNameRole().equals(oldRole)))
										if(idUser != null)
											pupFacade.assign(pt.getUser().getIdUser(), newRoleTmp.getIdRole(), taskJPA
												.getIdProcess(), idUser);
								}
								else
									throw new Exception("Роль '" + optRole.getNameRole() + "' не найдена для процесса '"
											+ taskJPA.getProcessTypeName() + "'");
							}
							catch (Exception e) {
								LOGGER.error(e.getMessage(), e);
							}
					}
			}
			// назначение нового пользователя исполнителем
			if (newUser != null) {
				try {
					if(idUser != null)
						pupFacade.assign(newUser.getIdUser(), newRole.getIdRole(), taskJPA.getIdProcess(), idUser);
				}
				catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				// добавим его в проектную команду, если не был добавлен ранее
				if (!newUserInTeam) {
					ProjectTeamJPA pt = new ProjectTeamJPA();
					pt.setTask(taskJPA);
					pt.setUser(newUser);
					pt.setTeamType("p");
					em.persist(pt);
				}
			}
		}
		taskJPA.setIndcondition(oldTaskJPA.isIndcondition());
		taskJPA.setAcredetivSourcePayment(oldTaskJPA.getAcredetivSourcePayment());
		taskJPA.setRate2(oldTaskJPA.getRate2());
		taskJPA.setRate2Note(oldTaskJPA.getRate2Note());
		taskJPA.setTitle(oldTaskJPA.getTitle());
		taskJPA.setExchangedate(taskJPA.getExchangedate());
		taskJPA.setOperDecision(new ArrayList<OperDecisionJPA>());
		for (OperDecisionJPA od : oldTaskJPA.getOperDecision()) {
			OperDecisionJPA c = new OperDecisionJPA();
			c.setTask(taskJPA);
			c.setAccepted(od.getAccepted());
			c.setSpecials(od.getSpecials());
			taskJPA.getOperDecision().add(c);
			em.persist(c);
			c.setDescriptions(new ArrayList<OperDecisionDescriptionJPA>());
			for (OperDecisionDescriptionJPA desc : od.getDescriptions()) {
				OperDecisionDescriptionJPA operDesc = new OperDecisionDescriptionJPA(desc.getDescr(), c);
				c.getDescriptions().add(operDesc);
				em.persist(operDesc);
			}
		}
		taskJPA.setProductGroupList(new ArrayList<ProductGroupJPA>());
		for (ProductGroupJPA opg : oldTaskJPA.getProductGroupList()) {
			ProductGroupJPA pg = new ProductGroupJPA();
			pg.setTask(taskJPA);
			pg.setName(opg.getName());
			pg.setCmnt(opg.getCmnt());
			pg.setPeriod(opg.getPeriod());
			taskJPA.getProductGroupList().add(pg);
			em.persist(pg);
		}
		taskJPA.setAmortized_loan(oldTaskJPA.isAmortized_loan());
		taskJPA.setWith_sublimit(oldTaskJPA.getWith_sublimit());
		taskJPA.setWith_sublimit(oldTaskJPA.getWith_sublimit());
		taskJPA.setRiskpremium(oldTaskJPA.getRiskpremium());
		taskJPA.setRiskpremium_change(oldTaskJPA.getRiskpremium_change());
		taskJPA.setTurnoverPremium(oldTaskJPA.getTurnoverPremium());
		taskJPA.setTurnover(oldTaskJPA.getTurnover());
		taskJPA.setDefinition(oldTaskJPA.getDefinition());
		taskJPA.setGeneralcondition(oldTaskJPA.getGeneralcondition());
		taskJPA.setContracts(new ArrayList<ContractJPA>());
		for (ContractJPA con : oldTaskJPA.getContracts()) {
			ContractJPA c = new ContractJPA(taskJPA, con.getContract());
			taskJPA.getContracts().add(c);
			em.persist(c);
		}
		taskJPA.setPromissoryNotes(new ArrayList<PromissoryNoteJPA>());
		for (PromissoryNoteJPA opn : oldTaskJPA.getPromissoryNotes()) {
			PromissoryNoteJPA pn = new PromissoryNoteJPA();
			pn.setTask(taskJPA);
			pn.setHolder(opn.getHolder());
			pn.setCurrency(opn.getCurrency());
			pn.setMaxdate(opn.getMaxdate());
			pn.setPerc(opn.getPerc());
			pn.setPlace(opn.getPlace());
			pn.setVal(opn.getVal());
			taskJPA.getPromissoryNotes().add(pn);
			em.persist(pn);
		}
		taskJPA.setPremiumList(new ArrayList<PremiumJPA>());
		for (PremiumJPA op : oldTaskJPA.getPremiumList()) {
			PremiumJPA p = new PremiumJPA();
			p.setTask(taskJPA);
			p.setPremiumType(op.getPremiumType());
			p.setCurr(op.getCurr());
			p.setVal(op.getVal());
			p.setText(op.getText());
			em.persist(p);
		}
		taskJPA.setFixratedate(oldTaskJPA.getFixratedate());
		taskJPA.setFixrate(oldTaskJPA.isFixrate());
		taskJPA.setInterestRateDerivative(oldTaskJPA.isInterestRateDerivative());
		taskJPA.setInterestRateFixed(oldTaskJPA.isInterestRateFixed());
		taskJPA.setFundDown(oldTaskJPA.getFundDown());
		taskJPA.setInd_rate(oldTaskJPA.getInd_rate());
		taskJPA.setRate5(oldTaskJPA.getRate5());
		taskJPA.setRate6(oldTaskJPA.getRate6());
		taskJPA.setRate7(oldTaskJPA.getRate7());
		taskJPA.setRate8(oldTaskJPA.getRate8());
		taskJPA.setRate9(oldTaskJPA.getRate9());
		taskJPA.setRate10(oldTaskJPA.getRate10());

		taskJPA.setTrance_graph(oldTaskJPA.isTrance_graph());
		taskJPA.setTrance_hard_graph(oldTaskJPA.isTrance_hard_graph());
		taskJPA.setTrance_limit_excess(oldTaskJPA.isTrance_limit_excess());
		taskJPA.setTrance_limit_use(oldTaskJPA.isTrance_limit_use());
		taskJPA.setTrance_period_format(oldTaskJPA.getTrance_period_format());
		taskJPA.setEarly_payment_prohibition(oldTaskJPA.isEarly_payment_prohibition());
		taskJPA.setEarly_payment_proh_per(oldTaskJPA.getEarly_payment_proh_per());
		taskJPA.setActive_decision(oldTaskJPA.getActive_decision());
		for(IndrateMdtaskJPA oind : oldTaskJPA.getIndrates()){
			if(oind.getIdFactpercent()!=null)
				continue;
			IndrateMdtaskJPA ind = new IndrateMdtaskJPA();
			ind.setTask(taskJPA);
			ind.setIndrate(oind.getIndrate());
			ind.setRate(oind.getRate());
			ind.setReason(oind.getReason());
			ind.setUsefrom(oind.getUsefrom());
			ind.setValue(oind.getValue());
			em.persist(ind);
		}
		int i = 0;
		if (!taskJPA.isLimit())
			for (FactPercentJPA ofp : oldTaskJPA.getFactPercents()) {
				FactPercentJPA fp = new FactPercentJPA();
				fp.setTask(taskJPA);
				fp.setEffrate(ofp.getEffrate());
				fp.setEnd_date(ofp.getEnd_date());
				fp.setFondrate(ofp.getFondrate());
				fp.setIndcondition(ofp.getIndcondition());
				fp.setInterestRateDerivative(ofp.isInterestRateDerivative());
				fp.setInterestRateFixed(ofp.isInterestRateFixed());
				fp.setPremiumcurr(ofp.getPremiumcurr());
				fp.setPremiumtext(ofp.getPremiumtext());
				fp.setPremiumType(ofp.getPremiumType());
				fp.setPremiumvalue(ofp.getPremiumvalue());
				fp.setRate10(ofp.getRate10());
				fp.setRate11(ofp.getRate11());
				fp.setRate3(ofp.getRate3());
				fp.setRate4(ofp.getRate4());
				fp.setRate4Desc(ofp.getRate4Desc());
				fp.setUsefrom(ofp.getUsefrom());
				fp.setReason(ofp.getReason());
				fp.setRate5(ofp.getRate5());
				fp.setRate6(ofp.getRate6());
				fp.setRate9(ofp.getRate9());
				fp.setRating_c1(ofp.getRating_c1());
				fp.setRating_c2(ofp.getRating_c2());
				fp.setRating_calc(ofp.getRating_calc());
				fp.setRating_fondrate(ofp.getRating_fondrate());
				fp.setRating_ktr(ofp.getRating_ktr());
				fp.setRating_rate3(ofp.getRating_rate3());
				fp.setRating_riskpremium(ofp.getRating_riskpremium());
				fp.setRiskpremium(ofp.getRiskpremium());
				fp.setRiskpremium_change(ofp.getRiskpremium_change());
				fp.setRiskpremiumtype(ofp.getRiskpremiumtype());
				fp.setRiskStepupFactor(ofp.getRiskStepupFactor());
				fp.setRiskStepupFactorValue(ofp.getRiskStepupFactorValue());
				fp.setStart_date(ofp.getStart_date());
				fp.setSupply(ofp.getSupply());
				fp.setTrance(null);
				// Новый транш
				if (ofp.getTrance() != null && trances != null) {
					TranceJPA tr = em.find(TranceJPA.class, trances.get(i++).getId());
					fp.setTrance(tr);
				}
				em.persist(fp);
				for(IndrateMdtaskJPA oind : oldTaskJPA.getIndrates()){
					if(oind.getIdFactpercent()==null || !oind.getIdFactpercent().equals(ofp.getId()))
						continue;
					IndrateMdtaskJPA ind = new IndrateMdtaskJPA();
					ind.setTask(taskJPA);
					ind.setIndrate(oind.getIndrate());
					ind.setRate(oind.getRate());
					ind.setReason(oind.getReason());
					ind.setUsefrom(oind.getUsefrom());
					ind.setValue(oind.getValue());
					ind.setIdFactpercent(fp.getId());
					em.persist(ind);
				}
			}

		em.merge(taskJPA);

		copyPipeline(oldTaskJPA.getId(), taskJPA.getId());

		return taskJPA;
	}

	private void copyPipeline(Long oldTaskId, Long newTaskId) {
		List<String> pipeline_fin_target = getPipelineFinTarget((oldTaskId));

		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("delete FROM PipelineJPA u where u.id_mdtask = :id");
		query.setParameter("id", newTaskId);
		query.executeUpdate();
		query = em.createNativeQuery("delete from PIPELINE_FIN_TARGET where id_mdtask=?");
		query.setParameter(1, newTaskId);
		query.executeUpdate();

		query = em.createNativeQuery("insert into PIPELINE_FIN_TARGET(id_mdtask,val) values(?,?)");
		for(String ft : pipeline_fin_target){
			query.setParameter(1, newTaskId);
			query.setParameter(2, ft);
			query.executeUpdate();
		}
		query = em.createNativeQuery("insert into pipeline(id_mdtask,plan_date,status,close_probability,close_probability_is_manual, law, " +
				"geography,supply, description, cmnt, addition_business, syndication, syndication_cmnt, wal, " +
				"hurdle_rate, markup, pc_cash, pc_res, pc_der, pc_total,line_count, pub, priority, new_client, " +
				"flow_investment, rating, factor_product_type, factor_period, contractor, vtb_contractor, trade_desc, " +
				"prolongation, hideinreport) " +
				"select ?,plan_date,status,close_probability,close_probability_is_manual, law, " +
				"geography,supply, description, cmnt, addition_business, syndication, syndication_cmnt, wal, " +
				"hurdle_rate, markup, pc_cash, pc_res, pc_der, pc_total,line_count, pub, priority, new_client, " +
				"flow_investment, rating, factor_product_type, factor_period, contractor, vtb_contractor, trade_desc, " +
				"prolongation, hideinreport " +
				"from pipeline where id_mdtask=?");
		query.setParameter(1, newTaskId);
		query.setParameter(2, oldTaskId);
		query.executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long findLastApprovedVersion(Long mdtaskNumber) {
		String sql = "SELECT max(m.id_mdtask)"
				+ " FROM mdtask m"
				+ " inner join attributes a on m.ID_PUP_PROCESS = a.ID_PROCESS  and LOWER( a.VALUE_VAR ) like 'одобрен%'"
				+ " inner join variables v on a.ID_VAR = v.ID_VAR and v.NAME_VAR like 'Статус'"
				+ " where m.MDTASK_NUMBER = ?";
		LOGGER.info(sql);
		Query q = factory.createEntityManager().createNativeQuery(sql);
		q.setParameter(1, mdtaskNumber);
		Long res = null;
		List<Object> resList = q.getResultList();
		if (resList != null && resList.size() > 0) {
			BigDecimal resBD = (BigDecimal) resList.get(0);
			if (resBD != null)
				res = resBD.longValue();
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long findSublimit(Long parentId, Long mdtaskNumber) {
		String sql = "SELECT m.id_mdtask FROM mdtask m"
				+ " where m.PARENTID = ? and m.MDTASK_NUMBER = ?";
		LOGGER.info(sql);
		Query q = factory.createEntityManager().createNativeQuery(sql);
		q.setParameter(1, parentId);
		q.setParameter(2, mdtaskNumber);
		Long res = null;
		List<Object> resList = q.getResultList();
		if (resList != null && resList.size() > 0) {
			BigDecimal resBD = (BigDecimal) resList.get(0);
			if (resBD != null)
				res = resBD.longValue();
		}
		return res;
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void logMainBorrowerChanged(Long idMdtask, Long userid,
			String oldorg, String neworg) {
		LOGGER.info("logMainBorrowerChanged idMdtask=" + idMdtask);
		EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("insert into main_borrower_change_log (id_mdtask, old_org, new_org, userid, log_date) values (?1, ?2, ?3, ?4, ?5)");
        q.setParameter(1, idMdtask);
        q.setParameter(2, oldorg);
        q.setParameter(3, neworg);
        q.setParameter(4, userid);
		q.setParameter(5, new Date());
		q.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<MainBorrowerChangeLog> getMainBorrowerChangeLog(Long idMdtask) {
		ArrayList<MainBorrowerChangeLog> res = new ArrayList<MainBorrowerChangeLog>();
		EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("select l.log_date,u.surname,u.name,u.login,oldorg.organization_name old, neworg.organization_name neworg,(select co.organization_name from crm_organization co where co.id_org=vo.ID_UNITED_CLIENT) united_client "
        		+ "from main_borrower_change_log l "
        		+ "inner join users u on u.id_user=l.userid "
        		+ "inner join crm_organization oldorg on oldorg.id_org=l.old_org "
        		+ "inner join crm_organization neworg on neworg.id_org=l.new_org "
        		+ "inner join v_organisation vo on l.new_org=vo.crmid "
        		+ "where l.id_mdtask = ?1 order by l.log_date");
        q.setParameter(1, idMdtask);
        ArrayList<Object> resultTable = (ArrayList<Object>) q.getResultList();
        for(Object row : resultTable){
        	Object[] r = (Object[]) row;
			java.sql.Timestamp logDate = (java.sql.Timestamp)r[0];
			if(res.size()==0 || !r[5].equals(res.get(res.size()-1).getNewOrg()))
        		res.add(new MainBorrowerChangeLog(logDate, (String)r[4], (String)r[5], r[1].toString()+" "+r[2].toString()+" ("+r[3].toString()+")",(String)r[6]));
        }
        return res;
	}

	@Override
	public List<Long> getVersionIds(String mdtaskNumber) {
		ArrayList<Long> res = new ArrayList<Long>();
		EntityManager em = factory.createEntityManager();
		Query q = em.createNativeQuery("select t.id_mdtask id from mdtask t "
				+ " where t.mdtask_number=? order by t.version desc ");
		q.setParameter(1, mdtaskNumber);
		List<Object> resList = q.getResultList();
		if (resList != null && resList.size() > 0) {
			for (Object object : resList) {
				BigDecimal resBD = (BigDecimal) object;
				if (resBD != null)
					res.add(resBD.longValue());
			}
		}
		return res;
	}

	@Override
	public List<TaskJPA> getVersions(String mdtaskNumber) {
		ArrayList<TaskJPA> res = new ArrayList<TaskJPA>();
		try {
			for (Long id : getVersionIds(mdtaskNumber)) {
				res.add(getTask(id));
			}
		}
		catch (Exception e) {}
		return res;
	}

	@Override
	public TaskJPA getFirstVersion(String mdtaskNumber) {
		try {
			EntityManager em = factory.createEntityManager();
			Query q = em.createNativeQuery("select min(t.id_mdtask) from mdtask t "
					+ " where t.mdtask_number=? and t.id_pup_process is not null");
			q.setParameter(1, mdtaskNumber);
			List<Object> resList = q.getResultList();
			if (resList != null && resList.size() > 0) {
				for (Object object : resList) {
					BigDecimal resBD = (BigDecimal) object;
					if (resBD != null)
						return getTask(resBD.longValue());
				}
			}
			return null;
		}
		catch (Exception e) {}
		return null;
	}
	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public long getNextVal(String seqName){
		EntityManager em = factory.createEntityManager();
		return ((BigDecimal)em.createNativeQuery("select "+seqName+".nextval from dual").getSingleResult()).longValue();
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void clearKmDealPercentState(Long idMdtask) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("delete from KM_DEAL_PERCENT_STATE where ID_MDTASK=?");
		query.setParameter(1, idMdtask);
		query.executeUpdate();
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateKmDealPercentState(Long idMdtask, String state) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("update KM_DEAL_PERCENT_STATE set PERCENT_STATE_NAME=? where ID_MDTASK=?");
		query.setParameter(1, state);
		query.setParameter(2, idMdtask);
		query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long createPriceConditionVersion(Long mdtaskid) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("select mdtask_seq.nextval from dual");
		Long idNewCreditDeal = Long.valueOf(((BigDecimal) query.getResultList().get(0)).toString());

		//update monitoring_mdtask
		query = em.createNativeQuery("update mdtask set monitoring_mdtask=? where ID_MDTASK=?");
		query.setParameter(1, idNewCreditDeal);
		query.setParameter(2, mdtaskid);
		query.executeUpdate();

		mdTaskMapper.createKmPercentVersion(idNewCreditDeal, mdtaskid);

		return idNewCreditDeal;
	}

    /**
     * По нажатию "Акцепт" бизнес-процесса "Изменение процентной ставки" перенос данных в данные связанной заявки, запись в хронологию изменения процентной ставки
     *
     * @param mdtaskid {@link Long id} заявки
     * @return {@link Long id} заявки
     */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Long approvePriceConditionVersionMonitoring(Long mdtaskid) {
		EntityManager em = factory.createEntityManager();
		Long tmpId = getTask(mdtaskid).getMonitoringMdtask();
		if(tmpId == null)//в этой ситуации версия стоимостных условия не создавалась.
			return null;//Так что данные равны значениям в заявке. Ничего никуда переность не нужно

		//update mdtask
		String sql = "update mdtask t set " +
				" t.interest_rate_fixed=(select interest_rate_fixed from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.interest_rate_derivative=(select interest_rate_derivative from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.fixrate=(select fixrate from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.fund_down=(select fund_down from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.rate5=(select rate5 from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.rate6=(select rate6 from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.rate7=(select rate7 from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.rate8=(select rate8 from mdtask where ID_MDTASK=" + tmpId + "), " +
				" t.MONITORING_MDTASK=null" +
				" where ID_MDTASK=?";
		LOGGER.info("approvePriceConditionVersionMonitoring: " + sql);
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, mdtaskid);
		query.executeUpdate();

		//insert индикативная ставка
		query = em.createNativeQuery("delete from INDRATE_MDTASK  where ID_MDTASK=?");
		query.setParameter(1, mdtaskid);
		query.executeUpdate();

		query = em.createNativeQuery("update INDRATE_MDTASK set ID_MDTASK=? where ID_MDTASK=?");
		query.setParameter(2, tmpId);
		query.setParameter(1, mdtaskid);
		query.executeUpdate();

		//периоды
		query = em.createNativeQuery("delete from FACTPERCENT  where ID_MDTASK=?");
		query.setParameter(1, mdtaskid);
		query.executeUpdate();

		query = em.createNativeQuery("update FACTPERCENT set ID_MDTASK=? where ID_MDTASK=?");
		query.setParameter(2, tmpId);
		query.setParameter(1, mdtaskid);
		query.executeUpdate();

		return tmpId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long approvePriceConditionVersionMonitoringAndCreatePercentHistory(Long mdtaskid) {
		Long result = approvePriceConditionVersionMonitoring(mdtaskid);

		Long idPerformer = pupFacade.getCurrentUser().getIdUser();

		mdTaskMapper.createDealPercentHistoryValue(mdtaskid, idPerformer);

		return result;
	}
}
