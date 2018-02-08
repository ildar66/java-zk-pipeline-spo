package ru.md.spo.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.domain.spo.PunitiveMeasure;
import ru.masterdm.compendium.exception.MappingException;
import ru.md.dict.dbobjects.ConditionTypeJPA;
import ru.md.dict.dbobjects.DepositorFinStatusJPA;
import ru.md.dict.dbobjects.EarlyRepaymentJPA;
import ru.md.dict.dbobjects.OperationTypeJPA;
import ru.md.dict.dbobjects.PipelineCoeffsJPA;
import ru.md.dict.dbobjects.PipelineFinancialGoalJPA;
import ru.md.dict.dbobjects.PipelineFundingCompanyJPA;
import ru.md.dict.dbobjects.PipelineTradingDeskJPA;
import ru.md.dict.dbobjects.RiskStepupFactorJPA;
import ru.md.dict.dbobjects.StavbaseJPA;
import ru.md.dict.dbobjects.SupplyTypeJPA;
import ru.md.dict.dbobjects.SystemModuleJPA;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.spo.dbobjects.CdAcredetivSourcePaymentJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverCriteriumJPA;
import ru.md.spo.dbobjects.CdCreditTurnoverPremiumJPA;
import ru.md.spo.dbobjects.CdPremiumTypeJPA;
import ru.md.spo.dbobjects.CdRiskpremiumJPA;
import ru.md.spo.dbobjects.DependingLoanJPA;
import ru.md.spo.dbobjects.FundDownJPA;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.dbobjects.ProductTypeJPA;
import ru.md.spo.dbobjects.PunitiveMeasureJPA;
import ru.md.spo.dbobjects.SpoRouteJPA;
import ru.md.spo.dbobjects.SpoRouteVersionJPA;
import ru.md.spo.dbobjects.StatusReturnJPA;
import ru.md.spo.dbobjects.StavspredJPA;
import ru.md.spo.util.Page;
import ru.md.spo.util.QueryHelper;

import com.vtb.domain.ProductGroup;
import com.vtb.domain.StavDefrayalExes;
import com.vtb.domain.StavDefrayalExes.StavDefrayalExesType;
import com.vtb.exception.FactoryException;

@Stateless
public class DictionaryFacade implements DictionaryFacadeLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryFacade.class);
	
    @PersistenceUnit(unitName = "flexWorkflowEJBJPA")
    private EntityManagerFactory factory;
    
    @EJB
    private TaskFacadeLocal taskFacade;
    
    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdCreditTurnoverPremiumJPA> findCdCreditTurnoverPremium() {
        return (List<CdCreditTurnoverPremiumJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM CdCreditTurnoverPremiumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdRiskpremiumJPA> findCdRiskpremium() {
        return (List<CdRiskpremiumJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM CdRiskpremiumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdPremiumTypeJPA> findRiskpremiumType(CdPremiumTypeJPA.Type type) {
        EntityManager em = factory.createEntityManager();
        if(type==null)
            return (List<CdPremiumTypeJPA>) em.createQuery("SELECT u FROM CdPremiumTypeJPA u").getResultList();
        Query query = em.createQuery("SELECT u FROM CdPremiumTypeJPA u where u.trade_type = :type" +
                " or u.trade_type ='"+CdPremiumTypeJPA.Type.COMMON.getName()+"'");
        query.setParameter("type", type.getName());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CdCreditTurnoverCriteriumJPA> findCdCreditTurnoverCriterium() {
        return (List<CdCreditTurnoverCriteriumJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM CdCreditTurnoverCriteriumJPA u").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StavspredJPA> findStavspred(String cur, Long period) {
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
        Query query = em.createQuery("SELECT u FROM PunitiveMeasureJPA u where u.sanction_type = :sanction_type" +
				" or u.sanction_type ='" + PunitiveMeasure.SanctionType.COMMONSANCTION.getDescription() + "'");
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

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<CdAcredetivSourcePaymentJPA> findAcredetivSourcePayment() {
        return (List<CdAcredetivSourcePaymentJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM CdAcredetivSourcePaymentJPA u").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<FundDownJPA> findFundDown() {
        return (List<FundDownJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM FundDownJPA u").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<ProductTypeJPA> findProductType() {
        return (List<ProductTypeJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM ProductTypeJPA u where u.is_active=1 and u.spo_enable=1 order by u.name").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<SupplyTypeJPA> findSupplyType() {
        return (List<SupplyTypeJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM SupplyTypeJPA u where u.deleted='0' order by u.name").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<OperationTypeJPA> findOperationType() {
        return (List<OperationTypeJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM OperationTypeJPA u where u.deleted='0' order by u.name").getResultList();
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) @SuppressWarnings("unchecked")
    public List<DepositorFinStatusJPA> findDepositorFinStatus() {
        return (List<DepositorFinStatusJPA>) factory.createEntityManager().
            createQuery("SELECT u FROM DepositorFinStatusJPA u order by u.id").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EarlyRepaymentJPA> findEarlyRepayment(String cur, Long period) {
        ArrayList<EarlyRepaymentJPA> res = new ArrayList<EarlyRepaymentJPA>();
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM EarlyRepaymentJPA u where u.currency = :unit");
        query.setParameter("unit", cur);
        res = (ArrayList<EarlyRepaymentJPA>) query.getResultList();
        if(period==null)
            return res;
        
        ArrayList<EarlyRepaymentJPA> periodFilter = new ArrayList<EarlyRepaymentJPA>();
        for (EarlyRepaymentJPA s : res){
            if(s.getDays_from()==null || s.getDays_from()<=period){
                if(s.getDays_to()==null || s.getDays_to()>=period){
                    periodFilter.add(s);
                }
            }
        }
        return periodFilter;
    }

    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StavDefrayalExes> findStavDefrayalExes(StavDefrayalExesType type, String clientCategory) {
        if(clientCategory==null)
            clientCategory="";
        if(clientCategory.length()>3)
            clientCategory=clientCategory.substring(0, 3);
        List<StavDefrayalExes> res = new ArrayList<StavDefrayalExes>();
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("select e.stavvalue,e.activedate,e.bcategory from CRM_STAV_DEFRAYAL_EXES e where e.stavtype like ?");
        q.setParameter(1, type.name());
        @SuppressWarnings("unchecked")
        List<Object[]> list = q.getResultList();
        for(Object[] obj : list){
            StavDefrayalExes stav = new StavDefrayalExes();
            BigDecimal val = (BigDecimal) obj[0];
            stav.setStavvalue(val.doubleValue());
            stav.setBcategory((String) obj[2]);
            if(stav.getBcategory()!=null && stav.getBcategory().toUpperCase().startsWith(clientCategory.toUpperCase())
                    ||clientCategory.isEmpty())
                res.add(stav);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<RiskStepupFactorJPA> findRiskStepupFactor() {
		return (List<RiskStepupFactorJPA>) factory.createEntityManager().
	            createQuery("SELECT u FROM RiskStepupFactorJPA u where u.is_active=1 order by u.text").getResultList();
	}

    @SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Double findStavarsmargin(String curr, Integer interval, String indRate) {
		EntityManager em = factory.createEntityManager();
		String sql = "select s.diapday,s.stavvalue from crm_stavarsmargin s where s.is_active=1 and INSTR('"+indRate+"',UPPER(s.liborsrok)) = 1 and s.unit='"
				+curr+"' and s.activedate<= :now ";
		Query query = em.createNativeQuery(sql+" and s.diapday>="+interval+" order by s.diapday asc");
		query.setParameter("now", new Date());
		ArrayList<Object[]> res = (ArrayList<Object[]>) query.getResultList();
		if(res.size()==0)
			return 0.0;
		Double diap1 = ((BigDecimal) res.get(0)[0]).doubleValue();
		Double stav1 = ((BigDecimal) res.get(0)[1]).doubleValue();
		query = em.createNativeQuery(sql+" and s.diapday<="+interval+" order by s.diapday desc");
		query.setParameter("now", new Date());
		res = (ArrayList<Object[]>) query.getResultList();
		if(res.size()==0)
			return 0.0;
		Double diap2 = ((BigDecimal) res.get(0)[0]).doubleValue();
		Double stav2 = ((BigDecimal) res.get(0)[1]).doubleValue();
		if(diap1.equals(diap2))
			return stav1.doubleValue();
		return (stav1-stav2)*(interval-diap1)/(diap1-diap2)+stav1;
		//return (stav1.subtract(stav2).multiply(BigDecimal.valueOf(interval).subtract(diap1)).divide(diap1.subtract(diap2)).add(stav1)).doubleValue();
	}

	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Double findStavbase(String curr, Integer interval) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT u FROM StavbaseJPA u where u.unit = :unit and diapdaymin<=:int and diapdaymax>=:int and activedate<:now");
		query.setParameter("unit", curr);
		query.setParameter("int", interval.longValue());
		query.setParameter("now", new Date());
		@SuppressWarnings("unchecked")
		ArrayList<StavbaseJPA> res = (ArrayList<StavbaseJPA>) query.getResultList();
		if(res.size()==0)
			return 0.0;
		return res.get(0).getStavvalue();
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PipelineFinancialGoalJPA> findPipelineFinancialGoal() {
		return (List<PipelineFinancialGoalJPA>) factory.createEntityManager().
	            createQuery("SELECT u FROM PipelineFinancialGoalJPA u").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PipelineCoeffsJPA> findPipelineCoeffs(Long type) {
		Query query = factory.createEntityManager().
	            createQuery("SELECT u FROM PipelineCoeffsJPA u where u.id_type= :type");
		query.setParameter("type", type);
		return (List<PipelineCoeffsJPA>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PipelineTradingDeskJPA> findPipelineTradingDesk() {
		return (List<PipelineTradingDeskJPA>) factory.createEntityManager().
	            createQuery("SELECT u FROM PipelineTradingDeskJPA u").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PipelineFundingCompanyJPA> findPipelineFundingCompany() {
		Query query = factory.createEntityManager().createQuery("SELECT u FROM PipelineFundingCompanyJPA u");
		return (List<PipelineFundingCompanyJPA>) query.getResultList(); 
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<ConditionTypeJPA> findConditionTypes() {
		Query query = factory.createEntityManager().createQuery("SELECT u FROM ConditionTypeJPA u order by u.sort_order");
		return (List<ConditionTypeJPA>) query.getResultList(); 
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> findProjectTeamRoles() {
		List<String> res = factory.createEntityManager().createNativeQuery(
				"select T.SPO_ROLE_NAME from CPS_SECTION_ROLE_SPO_MAPPING t INNER JOIN CPS_SECTION_ROLE SR ON SR.SECTION_ROLE_ID = T.SECTION_ROLE_ID " +
						"INNER JOIN CPS_SECTION S ON S.SECTION_ID = SR.SECTION_ID " +
						"WHERE S.KEY = 'PROJECT_TEAM'").getResultList();
		if(!res.contains("Руководитель структуратора (за МО)"))
			res.add("Руководитель структуратора (за МО)");
		if(!res.contains("Структуратор (за МО)"))
			res.add("Структуратор (за МО)");//В базу роли добавить не получается, так как скрипт cps пересоздаёт эти таблицы
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> findMiddleOfficeRoles() {
		return factory.createEntityManager().createNativeQuery(
				"select T.SPO_ROLE_NAME from CPS_SECTION_ROLE_SPO_MAPPING t INNER JOIN CPS_SECTION_ROLE SR ON SR.SECTION_ROLE_ID = T.SECTION_ROLE_ID " +
				"INNER JOIN CPS_SECTION S ON S.SECTION_ID = SR.SECTION_ID " +
				"WHERE S.KEY = 'MIDDLE_OFFICE'").getResultList();
	}

	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public OrgJPA getOrg(String id) {
		return factory.createEntityManager().find(OrgJPA.class, id);
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String saveSpoRoute(HttpServletRequest request)
			throws FactoryException {
		EntityManager em = factory.createEntityManager();
        ProcessTypeJPA pt = em.find(ProcessTypeJPA.class,Long.valueOf(request.getParameter("idProcessType")));
        SpoRouteVersionJPA v = new SpoRouteVersionJPA();
        v.setProcessType(pt);
        v.setDate(new Date());
        pt.getSpoRouteVersion().add(v);
        em.persist(v);
        em.merge(pt);
        
        v.setRoutes(new ArrayList<SpoRouteJPA>());
        if(request.getParameter("defaultDepartment")!=null)
        	for(int i=0;i<request.getParameterValues("defaultDepartment").length;i++){
        		SpoRouteJPA route = new SpoRouteJPA();
        		route.setVersion(v);
        		route.setStageName(request.getParameterValues("stage")[i]);
        		route.setDefaultDepartment(em.find(DepartmentJPA.class, Long.valueOf(request.getParameterValues("defaultDepartment")[i])));
        		v.getRoutes().add(route);
        		em.persist(route);
        		String routeid = request.getParameterValues("routeid")[i];
        		route.setInitDepartments(new ArrayList<DepartmentJPA>());
        		if(request.getParameter("route"+routeid+"_initdep")!=null)
        			for(String initDep : request.getParameterValues("route"+routeid+"_initdep"))
        				route.getInitDepartments().add(em.find(DepartmentJPA.class, Long.valueOf(initDep)));
        		em.merge(route);
        	}
        
        em.flush();
        return v.getId().toString();
	}

	@Override  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long findSpoRoute(String stageName, Long initDepId, Long processTypeId) {
		try {
			EntityManager em = factory.createEntityManager();
			Query q = em.createNativeQuery("select max(id) from spo_route_version where id_type_process=?");
			q.setParameter(1, processTypeId);
			Long versionId = ((BigDecimal)q.getSingleResult()).longValue();
			LOGGER.info("sporoute current version " + versionId);
			SpoRouteVersionJPA versionCurrent = em.find(SpoRouteVersionJPA.class, versionId);
			while(initDepId!=null){
				for(SpoRouteJPA route : versionCurrent.getRoutes())
					if(route.getStageName().equals(stageName))
						for(DepartmentJPA initDep : route.getInitDepartments())
							if(initDep.getIdDepartment().equals(initDepId)){
								LOGGER.info("sporoute " + route.getId());
								return route.getDefaultDepartment().getIdDepartment();
							}
				DepartmentJPA parent = em.find(DepartmentJPA.class, initDepId).getParentDepartment();
				initDepId = parent==null?null:parent.getIdDepartment();
			}
			/*Если в справочнике «Настройка маршрутизации заявки» не описано правило передачи заявки на следующую операцию бизнес-процесса с 
    		 * Инициирующим подразделением, соответствующим Инициирующему подразделению по заявке (или вышестоящему по иерархии), 
    		 * то при передаче заявки на следующую операцию бизнес-процесса, производится поиск правила для следующей операции 
    		 * без указания Инициирующего подразделения. 
    		 * Если такая запись существует, из неё определяется подразделение-исполнитель операции по умолчанию,*/
			for(SpoRouteJPA route : versionCurrent.getRoutes())
				if(route.getStageName().equals(stageName) && route.getInitDepartments().size()==0){
					LOGGER.info("sporoute " + route.getId());
					return route.getDefaultDepartment().getIdDepartment();
				}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> getPeriodDimension() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("дн.");
		list.add("мес.");
		list.add("г./лет");
		return list;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> getPayInt() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Query q = factory.createEntityManager().createNativeQuery("select max(id) from CD_PAY_INT_VERSION");
			BigDecimal currentVersion = (BigDecimal) q.getSingleResult();
			q = factory.createEntityManager().createNativeQuery("select name from CD_PAY_INT where id_ver=?1");
			q.setParameter(1, currentVersion);
			for(Object o : q.getResultList())
				list.add((String) o);
		} catch (Exception e) {
			//LOGGER.error(e.getMessage(), e);
			//пустой справочник - это нормальная штатная ситуация. Не нужно засорять сообщениями лог
		}
		return list;
	}
	
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> getComBase() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Query q = factory.createEntityManager().createNativeQuery("select text from crm_com_base where is_active = 1");
			for(Object o : q.getResultList())
				list.add((String) o);
		} catch (Exception e) {
			//LOGGER.error(e.getMessage(), e);
			//пустой справочник - это нормальная штатная ситуация. Не нужно засорять сообщениями лог
		}
		return list;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<ProductGroup> getProductGroupList() {
		ArrayList<ProductGroup> list = new ArrayList<ProductGroup>();
		EntityManager em = factory.createEntityManager();
		Query query = em.createNativeQuery("select id,name from cd_product_group where is_active=1");
		@SuppressWarnings("unchecked")
		List<Object> listObj = query.getResultList();
		for (Object obj : listObj){
            Object[] arr = (Object[]) obj;
            list.add(new ProductGroup(((BigDecimal)arr[0]).longValue(), (String) arr[1]));
		}
		//связь с лимитом
		query = em.createNativeQuery("select id_limit_type from cd_product_group_limit_type where id_prod_gr_type=?");
		for(ProductGroup pg : list){
			query.setParameter(1, pg.getId());
			for(Object obj : query.getResultList())
				pg.getLimitTypes().add(((BigDecimal)obj).longValue());
		}
		return list;
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DepartmentJPA findDepartmentByShortName(String name) {
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT u FROM DepartmentJPA u where u.shortName=?");
		query.setParameter(1, name);
		return (DepartmentJPA) query.getSingleResult();
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DepartmentJPA findDepartmentByProduct(String productTypeId) {
		EntityManager em = factory.createEntityManager();
		String sql = "select ID_DEP from tech_control where PRODUCT=?";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, productTypeId);
		@SuppressWarnings("unchecked")
		List<BigDecimal> res = query.getResultList();
		for (BigDecimal row : res) {
			return em.find(DepartmentJPA.class, row.longValue());
		}
		//не нашли. Значит, смотрим по умолчанию
		try {
			String defMoId = taskFacade.getGlobalSetting("default_mo_id");
			return em.find(DepartmentJPA.class, Long.valueOf(defMoId));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Page<OrgJPA> findOrganizationPage(OrgJPA org, int start, int count, String orderBy) throws Exception {
		EntityManager em = factory.createEntityManager();
		try {
			if (org == null)
				org = new OrgJPA();
			Page<OrgJPA> returnPage = null;
			Query query = null;
			StringBuilder sbWhereSection = new StringBuilder();

			StringBuilder sqlStr = 
				new StringBuilder("SELECT c FROM OrgJPA c ") ;
			StringBuilder sqlTotalCount = 
				new StringBuilder("SELECT COUNT(c) FROM OrgJPA c ") ;

			QueryHelper.appendWhereSectionIfNotEmpty(org.getId(), sbWhereSection, "lower( c.id ) like :id");
			QueryHelper.appendWhereSectionIfNotEmpty(org.getOrganizationName(), sbWhereSection, "lower( c.organizationName ) like :organizationName");
			QueryHelper.appendWhereSectionIfNotEmpty(org.getInn(), sbWhereSection, "lower( c.inn ) like :inn");			
			QueryHelper.appendWhereSectionIfNotEmpty(org.getOgrn(), sbWhereSection, "lower( c.ogrn ) like :ogrn");			
			QueryHelper.appendWhereSectionIfNotEmpty(org.getClientType(), sbWhereSection, "lower( c.clientType ) = :clientType");
			QueryHelper.appendWhereSectionIfNotEmpty(org.getClientcategory(), sbWhereSection, "lower( c.clientCategory ) like :clientCategory");
			QueryHelper.appendWhereSectionIfNotEmpty(org.getDepartment(), sbWhereSection, "lower( c.department ) like :department");
			QueryHelper.appendWhereSectionIfNotEmpty(org.getIsActive(), sbWhereSection, "c.isActive = :isActive");
			//по единому клиенту
			if(org.getClientUnited().equals("cl"))
			    QueryHelper.appendWhereSectionIfNotEmpty("cl", sbWhereSection, "c.idUnitedClient is not null");
			if(org.getClientUnited().equals("ek"))
				QueryHelper.appendWhereSectionIfNotEmpty("ek", sbWhereSection, "c.idUnitedClient is null");
			if(org.getIdUnitedClient()!=null)
				QueryHelper.appendWhereSectionIfNotEmpty("id", sbWhereSection, "c.idUnitedClient like '"+org.getIdUnitedClient()+"'");

			sqlStr.append(sbWhereSection);
			sqlTotalCount.append(sbWhereSection);
			
			if (orderBy != null && !orderBy.equals(""))
				sqlStr.append(" ORDER BY " + orderBy);
			
			if (count > 0)
				query = em.createQuery(sqlStr.toString()).setMaxResults(count).setFirstResult(start);
			else 
				query = em.createQuery(sqlStr.toString());
			Query queryTC = em.createQuery(sqlTotalCount.toString());

			QueryHelper.setParameterIfNotEmpty(query, "id", org.getId());
			QueryHelper.setParameterIfNotEmpty(query, "organizationName", org.getOrganizationName());
			QueryHelper.setParameterIfNotEmpty(query, "inn", org.getInn());
			QueryHelper.setParameterIfNotEmpty(query, "ogrn", org.getOgrn());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(query, "clientType", org.getClientType());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(query, "clientCategory", org.getClientcategory());
			QueryHelper.setParameterIfNotEmpty(query, "department", org.getDepartment());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(query, "isActive", org.getIsActive());

			QueryHelper.setParameterIfNotEmpty(queryTC, "id", org.getId());
			QueryHelper.setParameterIfNotEmpty(queryTC, "organizationName", org.getOrganizationName());
			QueryHelper.setParameterIfNotEmpty(queryTC, "inn", org.getInn());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(queryTC, "clientType", org.getClientType());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(queryTC, "clientCategory", org.getClientcategory());
			QueryHelper.setParameterIfNotEmpty(queryTC, "department", org.getDepartment());
			QueryHelper.setParameterEqualExpressionIfNotEmpty(queryTC, "isActive", org.getIsActive());
			
			LOGGER.info("sqlStr: " + sqlStr);
			LOGGER.info("sqlTotalCount: " + sqlTotalCount);
			LOGGER.info("count: " + count);
			LOGGER.info("start: " + start);
			LOGGER.info("org.getIdUnitedClient(): " + org.getIdUnitedClient());
			List<OrgJPA> listJPA = query.getResultList();
			int totalCount = ((Long) queryTC.getSingleResult()).intValue();
			LOGGER.info("totalCount: " + totalCount);
			if (listJPA.size() > 0) {
				boolean hasNext = (start + listJPA.size()) < totalCount;
				returnPage = new Page<OrgJPA>(listJPA, start, hasNext);
				returnPage.setTotalCount(totalCount);
			} else {
				returnPage = Page.EMPTY_PAGE;
				returnPage.setTotalCount(totalCount);
			}
			return returnPage;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findOrganizationPage " + e));
		}
	}

	@Override
	public Pair<List<OrgJPA>,List<DepartmentJPA>> findOrganization4EK(String crmDepName, String ek_id, String place2Name) throws Exception {
		LOGGER.info("ek_id="+ek_id);
		LOGGER.info("crmDepName="+crmDepName);
		LOGGER.info("place2Name="+place2Name);
		
		List<OrgJPA> listJPA = new ArrayList<OrgJPA>();
		List<DepartmentJPA> depList = new ArrayList<DepartmentJPA>();
		EntityManager em = factory.createEntityManager();
		String sql = "select o.crmid from v_organisation o "
				+ "where id_united_client = :ek_id and o.is_active = 1 and (department like :crmDepName or division like :crmDepName)";
		Query q = em.createNativeQuery(sql + " order by o.ORGANIZATIONNAME");
		q.setParameter("crmDepName", crmDepName);
		q.setParameter("ek_id", ek_id);
		for(Object orgid: q.getResultList()){
			listJPA.add(em.find(OrgJPA.class, orgid));
		}
		//кз найдены по полю division?
		boolean findByDevision = true;//КЗ найдены по полю division
		boolean emptyDevision = true;//у всех найденных КЗ поле division не заполненно 
		for(OrgJPA org : listJPA){
			if(org.getDivision()!=null && !org.getDivision().isEmpty())
				emptyDevision = false;
			if(org.getDivision()==null || !org.getDivision().equals(crmDepName))
				findByDevision = false;
		}
		if(findByDevision || emptyDevision){
			LOGGER.info("нижняя ветка алгоритма");
			return Pair.of(priorByCLientType(listJPA),depList);
		} else {
			LOGGER.info("правая ветка алгоритма");
			listJPA.clear();
			sql += " and (o.division is null or o.division ='Не определено' or o.division in (select cd.dep_name from crm_fb_department cd where cd.fb_departmentid in "
					+ "(select d.crm_fb_department from departments d where d.is_exec_dep=1)))";
			q = em.createNativeQuery(sql + " order by o.ORGANIZATIONNAME");//Это запрос 2 в терминологии Алдара Гармаева 
			q.setParameter("crmDepName", crmDepName);
			q.setParameter("ek_id", ek_id);
			for(Object orgid: q.getResultList()){
				listJPA.add(em.find(OrgJPA.class, orgid));
			}
			if(listJPA.size()<2)//если одна, то выведем её, если 0, то сообщение 
				return Pair.of(listJPA,depList);
			//если больше 1, то нужно отобразить "уточнение места проведения сделки". Это запрос 3 в терминологии Алдара Гармаева
			sql = "select distinct d.id_department from departments d "
					+ "inner join crm_fb_department cd on cd.fb_departmentid=d.crm_fb_department "
					+ "inner join v_organisation o on o.division=cd.dep_name "
					+ "where o.crmid in ("+sql+")";
			q = em.createNativeQuery(sql);
			q.setParameter("crmDepName", crmDepName);
			q.setParameter("ek_id", ek_id);
			for(Object depid: q.getResultList()){
				depList.add(em.find(DepartmentJPA.class, ((BigDecimal) depid).longValue()));
			}
			boolean showEmptyDep = false;
			for(OrgJPA org : listJPA)
				if(org.getDivision()==null || org.getDivision().equalsIgnoreCase("Не определено"))
					showEmptyDep = true;
			if(showEmptyDep){
				DepartmentJPA emptyDep = new DepartmentJPA();
				emptyDep.setShortName("Не определено");
				emptyDep.setIdDepartment(0L);
				depList.add(emptyDep);
			}
			for (DepartmentJPA dep : depList)
				LOGGER.info(dep.getShortName());
			//уточнение выбрано
			if(place2Name!=null && !place2Name.isEmpty()){
				List<OrgJPA> listJPADivision = new ArrayList<OrgJPA>();
				for(OrgJPA org : listJPA)
					if(org.getDivision()!=null && org.getDivision().equals(place2Name) || 
					    place2Name.equals("Не определено") && (org.getDivision()==null || org.getDivision().equals("Не определено")))
						listJPADivision.add(org);
				return Pair.of(priorByCLientType(listJPADivision),depList);
			}
		}
		return Pair.of(listJPA,depList);
	}
	/** нижняя ветка алгоритма */
	private List<OrgJPA> priorByCLientType(List<OrgJPA> listJPA) {
		if(filterOrgByClientType("Клиент", listJPA).size() > 0)
			if(filterOrgByClientType("Клиент", listJPA).size() == 1)
				return filterOrgByClientType("Клиент", listJPA);
			else
				return listJPA;
		if(filterOrgByClientType("Проспект", listJPA).size() > 0)
			if(filterOrgByClientType("Проспект", listJPA).size() == 1)
				return filterOrgByClientType("Проспект", listJPA);
			else
				return listJPA;
		if(filterOrgByClientType("Бывший клиент", listJPA).size() > 0)
			if(filterOrgByClientType("Бывший клиент", listJPA).size() == 1)
				return filterOrgByClientType("Бывший клиент", listJPA);
			else
				return listJPA;
		return listJPA;
	}
	private List<OrgJPA> filterOrgByClientType(String type, List<OrgJPA> list){
		List<OrgJPA> res = new ArrayList<OrgJPA>();
		for(OrgJPA org : list)
			if(org.getClientType()!=null && org.getClientType().equals(type))
				res.add(org);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DepartmentJPA> getExecDepList() {
		return factory.createEntityManager().createQuery("SELECT u FROM DepartmentJPA u where u.isActive=true and isExecDep=true order by u.shortName").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<DepartmentJPA> getInitialDepList() {
		return factory.createEntityManager().createQuery("SELECT u FROM DepartmentJPA u where u.isActive=true and isInitialDep=true order by u.shortName").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public SystemModuleJPA getModuleInfo(String key) {
		Query q = factory.createEntityManager().createQuery("SELECT u FROM SystemModuleJPA u where u.key=:key");
		q.setParameter("key", key);
		for(SystemModuleJPA module : (List<SystemModuleJPA>)q.getResultList())
			return module;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> findCurrencyList() {
		return factory.createEntityManager().createNativeQuery(
				"select c.code from V_CD_CURRENCY c where c.is_active='y'").getResultList();
	}

	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public StatusReturnJPA getApprovedStatusReturn() {
		List<StatusReturnJPA> list = factory.createEntityManager().
				createQuery("SELECT u FROM StatusReturnJPA u where u.status_return='Принято в СПО без изменений'").getResultList();
		if(list.size()>0)
			return list.get(0);
		return null;
	}
	
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<String> getIllegalLendingTargets() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Query q = factory.createEntityManager().createNativeQuery("select illegal_lend_targets_name " +
					"from cd_illegal_lending_targets where illegal_lend_targets_name is not null");
			for(Object o : q.getResultList())
				list.add((String) o);
		} catch (Exception e) {
			//LOGGER.error(e.getMessage(), e);
			//пустой справочник - это нормальная штатная ситуация. Не нужно засорять сообщениями лог
		}
		return list;
	}
}
