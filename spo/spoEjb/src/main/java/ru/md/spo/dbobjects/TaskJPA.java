package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vtb.exception.FactoryException;
import com.vtb.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.spo.integration.FilialTask;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.ProcessJPA;

import com.vtb.domain.AbstractSupply;
import com.vtb.domain.Task;
import com.vtb.util.ApplProperties;
import com.vtb.util.Formatter;
import ru.md.spo.ejb.NotifyFacadeLocal;

/**
 * JPA for task. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "MDTASK") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class TaskJPA implements Serializable {
    @Override
    public String toString() {
        return "TaskJPA [id=" + id + ", mdtask_number=" + mdtask_number + "]";
    }
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id_mdtask")
    @SequenceGenerator(name = "TaskSequenceGenerator", sequenceName = "mdtask_seq", allocationSize = 1)
    @GeneratedValue(generator = "TaskSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "crm_queue_id")
    private String crmQueue;
    
    private String tasktype;
    private String title;
    @Column(name = "cross_sell_type")
    private Long crossSellType;

    @ManyToOne @JoinColumn(name="id_pup_process")
    private ProcessJPA process;
    
    @ManyToOne @JoinColumn(name="id_authorized_person")
    private AuthorizedPersonJPA authorizedPerson;

    @ManyToOne @JoinColumn(name="ID_LIMIT_TYPE")
    private LimitTypeJPA limitType;

    @ManyToOne @JoinColumn(name="acredetiv_source")
    private CdAcredetivSourcePaymentJPA acredetivSourcePayment; //Источник формирования покрытия для осуществления платежа по аккредитиву
    
    @ManyToOne @JoinColumn(name="initdepartment")
    private DepartmentJPA initDepartment;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ManagerJPA> managers;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ProductGroupJPA> productGroupList;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY) @OrderBy(value="id")
    private List<FactPercentJPA> factPercents;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ProjectTeamJPA> projectTeam;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ExpertTeamJPA> expertTeam;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ContractJPA> contracts;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<IndConditionJPA> indConditions;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<IndrateMdtaskJPA> indrates;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<OperDecisionJPA> operDecision;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<PromissoryNoteJPA> promissoryNotes;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<StartDepartmentJPA> startDepartmentList;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<PremiumJPA> premiumList;

    @ManyToOne @JoinColumn(name="place")
    private DepartmentJPA place;

    @ManyToOne @JoinColumn(name="statusreturn")
    private StatusReturnJPA statusReturn;
    
    @ManyToOne @JoinColumn(name="riskpremium")
    private CdRiskpremiumJPA riskpremium;
    
    private Double riskpremium_change;
    
    @ManyToOne @JoinColumn(name="turnover_premium")
    private CdCreditTurnoverPremiumJPA turnoverPremium;
    private Double turnover;
    
    @ManyToMany(fetch = FetchType.LAZY) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "r_org_mdtask", joinColumns = @JoinColumn(name = "id_mdtask"), inverseJoinColumns = @JoinColumn(name = "id_crmorg"))
    private List<OrgJPA> orgList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "place", joinColumns = @JoinColumn(name = "id_mdtask"), inverseJoinColumns = @JoinColumn(name = "id_department"))
    private List<DepartmentJPA> placeList;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "R_MDTASK_OPP_TYPE", joinColumns = @JoinColumn(name = "id_mdtask"), inverseJoinColumns = @JoinColumn(name = "ID_OPP_TYPE"))
    private List<ProductTypeJPA> productTypes;
    
    private BigDecimal debt_limit_sum;
    private BigDecimal limit_issue_sum;
    private BigDecimal mdtask_sum;
    private String is_debt_sum;
    private String is_limit_sum;
    private String crmcode;
    private Long mdtask_number;
    private Long period_days;
    private Long period;
    private String periodDimension;
    private String currency;
    private String deleted;
    @Column(name = "main_org_changeble")
    private String mainOrgChangeble;
    @Column(name = "MAIN_ORG_GROUP")
    private String mainOrgGroup;
    @Column(name = "pmn_order")
    private String pmnOrder;
    private String with_sublimit;
    private String generalcondition;
    private String definition;
    
    private String status_backup;
    
    private String trance_graph;//График использования траншей
    private String trance_limit_use;//Допускается использование недоиспользованного лимита
    private String trance_limit_excess;//Допускается превышение лимита по графику
    private String trance_hard_graph;//Жесткий график
    private String trance_period_format;//Формат периода предоставления
    
    private String fixrate;//Ставка зафиксирована
    private Date fixratedate;
    private Date exchangedate;
    private Date proposed_dt_signing;
    private Date validto;
    
    @ManyToOne @JoinColumn(name="fund_down")
    private FundDownJPA fundDown;//Фондирование по пониженной ставке
    private String ind_rate;
    private String indcondition;//индивидуальные условия
    private Double rate2; //Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера
    @Column(name = "RATE2_NOTE")
    private String rate2Note; //Комментарий к "Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера"
    private Double rate5; //Компенсирующий спрэд за фиксацию процентной ставки
    private Double rate6; //Компенсирующий спрэд за досрочное погашение
    private Double rate7; //Покрытие прямых расходов
    private Double rate8; //Покрытие общебанковских расходов
    private Double rate9; //Комиссия за выдачу
    private Double rate10; //Комиссия за сопровождение

    private String trader_approve;//Подтверждено Трейдером
    private Date trader_approve_date;//Подтверждено Трейдером дата
    private Long trader_approve_user;//Подтверждено Трейдером кто нажимал
    private String ced_approve_login;
    private Date ced_approve_date;
    
    
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="PARENTID")
    private TaskJPA parent;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<StandardPeriodChangeJPA> standardPeriodDefined;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskStopFactorJPA> taskStopFactors;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY) @OrderBy("id asc")
    private List<TaskJPA> childs;
    
    private Long early_payment_proh_per;
    private String early_payment_prohibition;
    private String is_fixed;
    @Column(name = "interest_rate_fixed")
    private Boolean interestRateFixed;
    @Column(name = "interest_rate_derivative")
    private Boolean interestRateDerivative;
    private String amortized_loan;
    private String active_decision;
    @Column(name = "version")
    private Long version;

    private Long is_imported;
    @Column(name = "additional_contract")
    private Long additionalContract;
    @Column(name = "product_monitoring")
    private Long productMonitoring;    
    
    @Lob
   	@Column(name = "TARGET_TYPE_COMMENT")
    private String targetTypeControlNote;

    @Column(name = "monitoring_mode")
    private String monitoringMode;//Режим редактирования сделки для МИУ-2
    @Column(name = "monitoring_user_work")
    private Long monitoringUserWorkId;//Какой пользователь сейчас работает над заявкий по МИУ-2
    @Column(name = "monitoring_price_user")
    private Long monitoringPriceUserId;//Какой пользователь редактировал ставку для МИУ-2
    @Column(name = "monitoring_mdtask")
    private Long monitoringMdtask;//Временная версия стоимостных условий для сделки

    /**
     * @return Прочие подразделения и менеджеры
     */
    public String getManager() {
        return manager;
    }
    /**
     * @param manager Прочие подразделения и менеджеры
     */
    public void setManager(String manager) {
        this.manager = manager;
    }
    private String manager;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCrmQueue() {
        return crmQueue;
    }
    public void setCrmQueue(String crmQueue) {
        this.crmQueue = crmQueue;
    }
    public BigDecimal getSum() {
        return mdtask_sum;
    }
    public void setSum(BigDecimal mdtask_sum) {
        this.mdtask_sum = mdtask_sum;
    }
    public Long getMdtask_number() {
        return mdtask_number;
    }
    public void setMdtask_number(Long mdtask_number) {
        this.mdtask_number = mdtask_number;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public BigDecimal getDebtLimitSum() {
        return debt_limit_sum;
    }
    public void setDebtLimitSum(BigDecimal debt_limit_sum) {
        this.debt_limit_sum = debt_limit_sum;
    }
    public BigDecimal getLimitIssueSum() {
        return limit_issue_sum;
    }
    public void setLimitIssueSum(BigDecimal limit_issue_sum) {
        this.limit_issue_sum = limit_issue_sum;
    }
    public boolean isDebtLimit() {
        return is_debt_sum.equalsIgnoreCase("Y");
    }
    public void setIsDebtLimit(boolean is_debt_sum) {
        this.is_debt_sum = is_debt_sum?"y":"n";
    }
    public boolean isLimitIssue() {
        return is_limit_sum.equalsIgnoreCase("Y");
    }
    public void setIsLimitIssue(boolean is_limit_sum) {
        this.is_limit_sum = is_limit_sum?"y":"n";
    }
    public Long getProcessId() {
    	if(getProcess() == null)
    		return null;
    	return getProcess().getId();
    }
    public ProcessJPA getProcess() {
        return process;
    }
    public String getProcessTypeName(){
        if(process!=null)
            return process.getProcessType().getDescriptionProcess();
        if(parent==null)
        	return "";
        return parent.getProcessTypeName();
    }
    public Long getIdTypeProcess(){
    	if(getProcess()==null)
    		return getParent().getIdTypeProcess();
    	return getProcess().getProcessType().getIdTypeProcess();
    }
    public Long getIdProcess(){
    	if(getProcess()!=null)
    		return getProcess().getId();
        if(getParent()!=null)
            return getParent().getIdProcess();
        return null;
    }

    public boolean isHasProcess() {
        return getProcess()!=null;
    }

    public void setProcess(ProcessJPA process) {
        this.process = process;
    }
    public DepartmentJPA getInitDepartment() {
        return initDepartment;
    }
    public void setInitDepartment(DepartmentJPA initDepartment) {
        this.initDepartment = initDepartment;
    }
    public String getCrmcode() {
        return crmcode;
    }
    public void setCrmcode(String crmcode) {
        this.crmcode = crmcode;
    }
    public String getNumberDisplayWithRoot() {
        return getNumberDisplay(true);
    }
    public String getNumberAndVersion() {
        return getNumberDisplay() + " версия " + getVersion();
    }
    public String getNumberDisplay() {
        return getNumberDisplay(false);
    }
    /**
     * возвращает отображаемый номер.
     * @param withRoot - если false, то показываю только номер сущности без родителей.
     * Если true, то полный номер вида 123-33-1-2
     * @return
     */
    private String getNumberDisplay(boolean withRoot) {
        //для сублимитов
        if(getParent()!=null && !isProduct()){
            int i=0;
            for(TaskJPA frere : getParent().getChilds()){
                if(frere.isDeleted() || frere.isProduct())
                    continue;
                i++;
                if(frere.getId().equals(getId())){
                    if(!withRoot && getParent().isLimit())
                        return String.valueOf(i);
                    return getParent().getNumberDisplay(withRoot) + (getParent().isLimit()?".":"-") + String.valueOf(i);
                }
            }
        }
        //Это сделка или лимит
        if (getMdtask_number() == null) return null;
        if (getCrmcode() != null && !getCrmcode().equals("") && !getMdtask_number().toString().equals(getCrmcode())) 
            return getCrmcode() + "(" + getMdtask_number() + ")";
        else return getMdtask_number().toString();
    }
    public boolean isProduct(){
    	return getTasktype()!=null && getTasktype().equals("p");
    }
    public boolean isLimit(){
    	return getTasktype()!=null && getTasktype().equals("l");
    }
    /**
     * @return true, если заявка сделка а также если заявка лимит или сублимит, которые имеют в структуре ниже сделку 
     */
    public boolean hasProductInHerarhy(){
        if (isProduct()) 
            return true;
        for(TaskJPA child : childs){
            if(child.hasProductInHerarhy()) 
                return true;
        }
        return false;
    }
    public boolean isSublimit(){
        return process==null && getTasktype()==null;
    }
    /**Тип кредитной заявки*/
    public String getType(){
    	if(getTasktype()==null) return "Сублимит";
    	if(getTasktype().equals("l")) return "Лимит";
    	if(getTasktype().equals("p")) return "Сделка";
    	if(getTasktype().equals("c")) return "Кросс-селл";
    	return "Сублимит";
    }
    public List<OrgJPA> getOrgList() {
        return orgList;
    }
    /**
     * возвращает основного заемщика
     * @return
     */
    public String getOrganisation(){
        if (orgList.size() > 0){
        	String name = SBeanLocator.singleton().getDictService().getEkNameByOrgId(orgList.get(0).getId());
            if(!orgList.get(0).getInn().isEmpty())
            	name += " (инн "+orgList.get(0).getInn()+")";
            return name;
        }
        return "";
    }
    public String getOrganisationAndGroup(){
        //<Название контрагента осн.заемщика> (<ИНН>) (входит в Группу компаний: <название группы компаний>)
        if (orgList.size() > 0){
        	String name = SBeanLocator.singleton().getDictService().getEkNameByOrgId(orgList.get(0).getId());
            if(!orgList.get(0).getInn().isEmpty())
            	name += " (инн "+orgList.get(0).getInn()+")";
            String group = SBeanLocator.singleton().getCompendiumMapper().getGroupNameByOrgId(orgList.get(0).getId());
            if (!Formatter.str(group).isEmpty())
                name += " (входит в Группу компаний: "+group+")";
            return name;
        }
        return "";
    }
    public void setOrgList(List<OrgJPA> orgList) {
        this.orgList = orgList;
    }
    public StatusReturnJPA getStatusReturn() {
        return statusReturn;
    }
    public void setStatusReturn(StatusReturnJPA statusReturn) {
        this.statusReturn = statusReturn;
    }

	public String getActiveStageUrl() {
		String report = "file:///" + ApplProperties.getReportsPath() + "Audit/active_stages.rptdesign";
		return "reportPrintFormRenderAction.do?__format=html&notused=off&__report=" + report
				+ "&isDelinquency=-1&correspondingDeps=on" + "&p_idDepartment=-1&id_ClaimFromList="
				+ mdtask_number + "&mdtaskId=" + getId();
	}

    public String getSumWithCurrency() {
        return Formatter.format(mdtask_sum) + " " + currency;
    }
    /**
     * @return Место проведения сделки
     */
    public DepartmentJPA getPlace() {
        return place;
    }
    public Long getPlaceId() {
        if (place == null) return null;
        return place.getIdDepartment();
    }
    /**
     * @param place Место проведения сделки
     */
    public void setPlace(DepartmentJPA place) {
        this.place = place;
    }
    /**
     * @return Место
     */
    public List<DepartmentJPA> getPlaceList() {
        return placeList;
    }
    /**
     * @param placeList Место
     */
    public void setPlaceList(List<DepartmentJPA> placeList) {
        this.placeList = placeList;
    }
    public List<ManagerJPA> getManagers() {
        return managers;
    }
    public void setManagers(List<ManagerJPA> managers) {
        this.managers = managers;
    }
    public List<StartDepartmentJPA> getStartDepartmentList() {
        return startDepartmentList;
    }
    public void setStartDepartmentList(List<StartDepartmentJPA> startDepartmentList) {
        this.startDepartmentList = startDepartmentList;
    }
    /**
     * фильтрует по типу проектной команды.
     * @param type
     * m - мидл офис
     * p - обычная проектная команда
     */
    public List<ProjectTeamJPA> getProjectTeam(String type) {
        List<ProjectTeamJPA> res = new ArrayList<ProjectTeamJPA>();
        for (ProjectTeamJPA p : projectTeam) {
            if (p.getTeamType().equals(type)){
                boolean newUser = true;
                for(ProjectTeamJPA ex : res)
                    if (ex.getUser().equals(p.getUser()))
                        newUser = false;
                if(newUser)
                    res.add(p);
            }
        }
        return res;
    }
    public List<ProjectTeamJPA> getProjectTeam() {
        return projectTeam;
    }
    public void setProjectTeam(List<ProjectTeamJPA> projectTeam) {
        this.projectTeam = projectTeam;
    }
	/**
	 * @return уполномоченное лицо
	 */
	public AuthorizedPersonJPA getAuthorizedPerson() {
		return authorizedPerson;
	}
	/**
	 * @param authorizedPerson уполномоченное лицо
	 */
	public void setAuthorizedPerson(AuthorizedPersonJPA authorizedPerson) {
		this.authorizedPerson = authorizedPerson;
	}

    /**
     * самый верхний номер заявки.
     */
	public Long getParentMdtaskId() {
        if(getParent()==null)
            return getId();
        return getParent().getParentMdtaskId();
    }
	public TaskJPA getParent() {
		return parent;
	}
	public void setParent(TaskJPA parent) {
		this.parent = parent;
	}
	public List<TaskJPA> getChilds() {
		return childs;
	}
	public void setChilds(List<TaskJPA> childs) {
		this.childs = childs;
	}
	/**
	 * Возвращает значение по-умолчанию поля "период" для нового обеспечения.
	 * @param supplyCode - вид обеспечения (g-гарантия, d- залоги, w - поручительство)
	 * @return
	 */
	public String getPeriod4newSupplyFormated(String supplyCode) {
		if(getPeriod4newSupply(supplyCode)==null)
			return "";
		return String.valueOf(getPeriod4newSupply(supplyCode));
	}
	/**
	 * Возвращает значение по-умолчанию поля "период" для нового обеспечения.
	 * @param supplyCode - вид обеспечения (g-гарантия, d- залоги, w - поручительство)
	 * @return
	 */
	public Long getPeriod4newSupply(String supplyCode) {
		if(periodDimension==null || periodDimension.isEmpty() || period==null)
			return null;
		if(supplyCode!="w")
			return period;
		if(periodDimension.equals("дн."))
			return (period+1095);
		if(periodDimension.equals("мес."))
			return (period+36);
		if(periodDimension.equals("г./лет"))
			return (period+3);
		return null;
	}
	/**
	 * Возвращает значение по-умолчанию поля "дата по" для нового обеспечения.
	 * @param supplyCode - вид обеспечения (g-гарантия, d- залоги, w - поручительство)
	 * @return
	 */
	public Date getToDate4newSupply(String supplyCode) {
		if(periodDimension!=null && !periodDimension.isEmpty() && period!=null)
			return null;
		if(validto!=null){
			if(supplyCode!="w")
				return validto;
			Calendar c = Calendar.getInstance();
			c.setTime(validto);
			c.add(Calendar.YEAR, 3);
			return c.getTime();
		}
		return null;
	}
	/**
	 * Возвращает значение по-умолчанию поля "дата по" для нового обеспечения.
	 * @param supplyCode - вид обеспечения (g-гарантия, d- залоги, w - поручительство)
	 * @return
	 */
	public String getToDate4newSupplyFormated(String supplyCode) {
		return Formatter.format(getToDate4newSupply(supplyCode));
	}
	public String getPeriodFull() {
		/*
		 * (МО.172 – не исправлено) В шаблонах ПКР (сделка и сделка в рамках лимита) в случае, когда на форме указана и срок сделки в днях (месяцах или годах)\, 
		 * и срок сделки до даты необходимо выводить срок сделки в днях (месяцах или годах).
		 */
		if(period==null) return Formatter.format(getValidto());
		return period.toString() + " " + periodDimension;
	}
	public String getPeriodFormated() {
		if(period==null) return "";
		return period.toString() + " " + getPeriodDimension();
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
        calcPeriodDay();
	}
	public boolean isDeleted() {
	    return deleted!=null && deleted.equals("Y");
	}
    public String getDeleted() {
        return deleted;
    }
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    public StandardPeriodVersionJPA getActiveStandardPeriodVersion() {
        if(getProcess() ==  null)
            return null;
        return getProcess().getProcessType().getLastStandardPeriodVersion();
    }
    /**
     * @return standardPeriodDefined
     */
    public List<StandardPeriodChangeJPA> getStandardPeriodDefined() {
        return standardPeriodDefined;
    }
    /**
     * @param standardPeriodDefined standardPeriodDefined
     */
    public void setStandardPeriodDefined(
            List<StandardPeriodChangeJPA> standardPeriodDefined) {
        this.standardPeriodDefined = standardPeriodDefined;
    }
    /**
     * @return taskStopFactors
     */
    public List<TaskStopFactorJPA> getTaskStopFactors() {
        return taskStopFactors;
    }
    /**
     * @param taskStopFactors taskStopFactors
     */
    public void setTaskStopFactors(List<TaskStopFactorJPA> taskStopFactors) {
        this.taskStopFactors = taskStopFactors;
    }
    public String getWith_sublimit() {
        return with_sublimit;
    }
    public boolean isWithSublimit() {
        return with_sublimit==null || with_sublimit.equalsIgnoreCase("y");
    }
    public void setWith_sublimit(String with_sublimit) {
        this.with_sublimit = with_sublimit;
    }
    public List<ContractJPA> getContracts() {
        return contracts;
    }
    public void setContracts(List<ContractJPA> contracts) {
        this.contracts = contracts;
    }
    public CdCreditTurnoverPremiumJPA getTurnoverPremium() {
        return turnoverPremium;
    }
    public String getTurnoverPremiumDisplay() {
        if(turnoverPremium==null)
            return "";
        return turnoverPremium.getDescription();
    }
    public void setTurnoverPremium(CdCreditTurnoverPremiumJPA turnoverPremium) {
        this.turnoverPremium = turnoverPremium;
    }
    public Double getTurnover() {
        return turnover;
    }
    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }
    public String getRiskpremiumID() {
        if(riskpremium==null)
            return "";
        return riskpremium.getId().toString();
    }
    public CdRiskpremiumJPA getRiskpremium() {
        return riskpremium;
    }
    public String getRiskpremiumDisplay() {
        if(riskpremium==null)
            return "не выбрана";
        return riskpremium.getDescription();
    }
    public boolean showRiskpremiumChange(){
        if(riskpremium==null)
            return false;
        String val = riskpremium.getValue();
        return "увеличенная".equals(val) || "уменьшенная".equals(val);
    }
    public void setRiskpremium(CdRiskpremiumJPA riskremium) {
        this.riskpremium = riskremium;
    }
    public String getGeneralcondition() {
        if(generalcondition==null)
            return "";
        return generalcondition;
    }
    public void setGeneralcondition(String generalcondition) {
        this.generalcondition = generalcondition;
    }
    
    public String getDefinition() {
    	if(definition==null)
    		return "";
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public List<PromissoryNoteJPA> getPromissoryNotes() {
        return promissoryNotes;
    }
    public void setPromissoryNotes(List<PromissoryNoteJPA> promissoryNotes) {
        this.promissoryNotes = promissoryNotes;
    }
    public boolean isTrance_graph() {
        if(trance_graph==null)
            return true;
        return trance_graph.equals("y");
    }
    public void setTrance_graph(boolean trance_graph) {
        this.trance_graph = trance_graph?"y":"n";
    }
    public boolean isFixrate() {
        if(fixrate==null)
            return false;
        return fixrate.equals("y");
    }
    public void setFixrate(boolean fixrate) {
        this.fixrate = fixrate?"y":"n";
    }
    public Date getFixratedate() {
        return fixratedate;
    }
    public void setFixratedate(Date fixratedate) {
        this.fixratedate = fixratedate;
    }
    public String getRate_desc_decision() {
        if(fundDown==null)
            return "";
        return fundDown.getText();
    }
    public String getInd_rate() {
        return ind_rate;
    }
    public void setInd_rate(String ind_rate) {
        this.ind_rate = ind_rate;
    }

    /**
     * Возвращает только периоды, без траншей.
     */
    public List<FactPercentJPA> getPeriods() {
        List<FactPercentJPA> res = new ArrayList<FactPercentJPA>();
        for(FactPercentJPA f : getFactPercents())
            if(f.getTranceId()==null)
                res.add(f);
        return res;
    }
    public List<FactPercentJPA> getFactPercents() {
        if(factPercents.size()>0)
            return factPercents;
        List<FactPercentJPA> res = new ArrayList<FactPercentJPA>();
        FactPercentJPA fp = new FactPercentJPA();
        fp.setId(0L);
        res.add(fp);
        return res;
    }
    public void setFactPercents(List<FactPercentJPA> factPercents) {
        this.factPercents = factPercents;
    }
    @Deprecated
    public boolean isFixed() {
        return is_fixed==null || is_fixed.equals("y");
    }
    @Deprecated
    public void setIs_fixed(String is_fixed) {
        this.is_fixed = is_fixed;
        if(this.is_fixed==null) this.is_fixed="n";
    }
    public boolean isEarly_payment_prohibition() {
        return early_payment_prohibition==null || early_payment_prohibition.equals("y");
    }
    public void setEarly_payment_prohibition(boolean early_payment_prohibition) {
        this.early_payment_prohibition = early_payment_prohibition?"y":"n";
    }
    public Long getEarly_payment_proh_per() {
        return early_payment_proh_per;
    }
    public void setEarly_payment_proh_per(Long early_payment_proh_per) {
        this.early_payment_proh_per = early_payment_proh_per;
    }
    public Double getRiskpremium_change() {
        return riskpremium_change;
    }
    public void setRiskpremium_change(Double riskpremium_change) {
        this.riskpremium_change = riskpremium_change;
    }
    public boolean isAmortized_loan() {
        return amortized_loan==null || amortized_loan.equals("y");
    }
    public void setAmortized_loan(boolean amortized_loan) {
        this.amortized_loan = amortized_loan?"y":"n";
    }
    public List<IndConditionJPA> getIndConditions() {
        return indConditions;
    }
    public void setIndConditions(List<IndConditionJPA> indConditions) {
        this.indConditions = indConditions;
    }
    public List<OperDecisionJPA> getOperDecision() {
        return operDecision;
    }
    public void setOperDecision(List<OperDecisionJPA> operDecision) {
        this.operDecision = operDecision;
    }
    public boolean isIndcondition() {
        return indcondition!=null&&indcondition.equals("y");
    }
    public void setIndcondition(boolean indcondition) {
        this.indcondition = indcondition?"y":"n";
    }
    public Double getRate5() {
        return rate5;
    }
    public void setRate5(Double rate5) {
        this.rate5 = rate5;
    }
    public Double getRate6() {
        return rate6;
    }
    public void setRate6(Double rate6) {
        this.rate6 = rate6;
    }
    public Double getRate7() {
        return rate7;
    }
    public void setRate7(Double rate7) {
        this.rate7 = rate7;
    }
    public Double getRate8() {
        return rate8;
    }
    public void setRate8(Double rate8) {
        this.rate8 = rate8;
    }
    public Double getRate9() {
        return rate9;
    }
    public void setRate9(Double rate9) {
        this.rate9 = rate9;
    }
    public Double getRate10() {
        return rate10;
    }
    public void setRate10(Double rate10) {
    	this.rate10 = rate10;
    }
    public Date getExchangedate() {
        return exchangedate;
    }
    public void setExchangedate(Date exchangedate) {
        this.exchangedate = exchangedate;
    }
    public LimitTypeJPA getLimitType() {
        return limitType;
    }
    public void setLimitType(LimitTypeJPA limitType) {
        this.limitType = limitType;
    }
    /**Это документарный лимит, продукт, сублимит?*/
    public boolean isDocumentary(){
        if(isProduct()){
            for(ProductTypeJPA pt : productTypes){
                if(pt.getFamily().equals("Документарные операции"))
                    return true;
            }
            return false;
        } else {
            if(limitType==null)
                return false;
            return limitType.getName().contains("окументарный");
        }
    }
    public List<ProductTypeJPA> getProductTypes() {
        return productTypes;
    }
    public void setProductTypes(List<ProductTypeJPA> productTypes) {
        this.productTypes = productTypes;
    }

    public CdAcredetivSourcePaymentJPA getAcredetivSourcePayment() {
        return acredetivSourcePayment;
    }
    public String getAcredetivSourcePaymentName() {
        return acredetivSourcePayment==null?"не выбран":acredetivSourcePayment.getNameSource();
    }
    public void setAcredetivSourcePayment(
            CdAcredetivSourcePaymentJPA acredetivSourcePayment) {
        this.acredetivSourcePayment = acredetivSourcePayment;
    }
    public FundDownJPA getFundDown() {
        return fundDown;
    }
    public void setFundDown(FundDownJPA fundDown) {
        this.fundDown = fundDown;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        TaskJPA other = (TaskJPA) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    /**
     * преобразует заявку в Map. Используется чтобы строить шаблон для уведомлений
     **/
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("numberDisplay", getNumberAndVersion());
        data.put("mdtask_number", getMdtask_number().toString());
        data.put("id_mdtask", getId());
        data.put("mainOrg", getOrganisation());
        try {
            NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
            data.put("allOrg", notifyFacade.getAllContractors(getId()));
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return data;
    }
    public FilialTask toFilialTask(BigDecimal taskId){
    	FilialTask t = new FilialTask(taskId==null?null:taskId.longValue(), getId());
		t.setNumber(getNumberDisplay());
		t.setSum(getSum()==null?null:getSum().doubleValue());
		t.setCur(getCurrency());
		if(getProductTypes()!=null && getProductTypes().size()>0)
			t.setProductTypeName(getProductTypes().get(0).getName());
		t.setMainContractorName(getOrgList().get(0).getOrganizationName());
		t.setContractors(new ArrayList<String>());
		for(int i=1;i<getOrgList().size();i++){
			OrgJPA org = getOrgList().get(i);
			t.getContractors().add(org.getOrganizationName());
		}
		t.setPeriod(getPeriod());
		return t;
    }
    
    /**
     * по номеру периода хэш в нем хэш по номеру обеспечения степень обеспечения
     * @param task
     * @return
     */
	public HashMap<Long,HashMap<Long,Double>> getPeriodObKind(Task task) {
		HashMap<Long,HashMap<Long,Double>> res = new HashMap<Long, HashMap<Long,Double>>();
		for(FactPercentJPA fp : getFactPercents()){
			if(fp.getId().equals(0L))
				continue;
			Date endPeriod = fp.getTranceId()==null?fp.getEnd_date():fp.getTrance().getUsedateto();
			Date startPeriod = fp.getTranceId()==null?fp.getStart_date():fp.getTrance().getUsedatefrom();
			if(!res.containsKey(fp.getId())) res.put(fp.getId(), new HashMap<Long, Double>());
			for(AbstractSupply s : task.getSupply().getAllSupply()){
				if(s.getOb()==null || s.getOb().getId()==null || s.getOb().getId().equals(-1L)) continue;
				//если у обеспечения не заполнен диапазон дат, то не включаем его в период
				if(getFactPercents().size()>1 && (s.getTodate()==null || s.getFromdate()==null)) continue;
				if(getFactPercents().size()==1 
						|| endPeriod==null ||startPeriod==null
						|| s.getTodate().getTime()<=endPeriod.getTime() && s.getTodate().getTime()>=startPeriod.getTime()
						|| s.getFromdate().getTime()<=endPeriod.getTime() && s.getFromdate().getTime()>=startPeriod.getTime()
						|| endPeriod.getTime()<=s.getTodate().getTime() && endPeriod.getTime()>=s.getFromdate().getTime()
						|| startPeriod.getTime()<=s.getTodate().getTime() && startPeriod.getTime()>=s.getFromdate().getTime() ){
					if(!res.get(fp.getId()).containsKey(s.getOb().getId())) 
						res.get(fp.getId()).put(s.getOb().getId(), 0.0);
					if(s.getSupplyvalue()!=null)
						res.get(fp.getId()).put(s.getOb().getId(), 
								s.getSupplyvalue()+res.get(fp.getId()).get(s.getOb().getId()));
					if(res.get(fp.getId()).get(s.getOb().getId())>100.0)
						res.get(fp.getId()).put(s.getOb().getId(), 100.0);
				}
			}
		}
		return res;
	}
	public List<ExpertTeamJPA> getExpertTeam() {
		return expertTeam;
	}
	public void setExpertTeam(List<ExpertTeamJPA> expertTeam) {
		this.expertTeam = expertTeam;
	}
	public List<PremiumJPA> getPremiumList() {
		return premiumList;
	}
	public void setPremiumList(List<PremiumJPA> premiumList) {
		this.premiumList = premiumList;
	}
	public String getStatus_backup() {
		return status_backup;
	}
	public void setStatus_backup(String status_backup) {
		this.status_backup = status_backup;
	}
	public Date getProposed_dt_signing() {
		return proposed_dt_signing;
	}
	public void setProposed_dt_signing(Date proposed_dt_signing) {
		this.proposed_dt_signing = proposed_dt_signing;
	}
	public Date getValidto() {
		return validto;
	}
	public void setValidto(Date validto) {
		this.validto = validto;
	}
	public String getTitle() {
		return title==null?"":title.replaceAll("\"", "&quot;");
	}
	public String getTitleReport() {
		return title==null?"":title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return размерность срока
	 */
	public String getPeriodDimension() {
		return periodDimension==null?"":periodDimension;
	}
	/**
	 * @param periodDimension размерность срока
	 */
	public void setPeriodDimension(String periodDimension) {
		this.periodDimension = periodDimension;
        calcPeriodDay();
	}
	/**
	 * @return Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера
	 */
	public Double getRate2() {
		return rate2;
	}
	/**
	 * @param rate2 Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера
	 */
	public void setRate2(Double rate2) {
		this.rate2 = rate2;
	}
	/**
	 * Возвращает комментарий к "Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера"
	 *
	 * @return комментарий к "Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера"
	 */
	public String getRate2Note() {
		return rate2Note;
	}
	/**
	 * Устанавливает комментарий к "Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера"
	 *
	 * @param rate2Note комментарий к "Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера"
	 */
	public void setRate2Note(String rate2Note) {
		this.rate2Note = rate2Note;
	}
	/**
	 * @return the productGroupList
	 */
	public List<ProductGroupJPA> getProductGroupList() {
		return productGroupList;
	}
	/**
	 * @param productGroupList the productGroupList to set
	 */
	public void setProductGroupList(List<ProductGroupJPA> productGroupList) {
		this.productGroupList = productGroupList;
	}
	public boolean isTrance_limit_use() {
		if(trance_limit_use==null)
            return true;
        return trance_limit_use.equals("y");
	}
	public void setTrance_limit_use(boolean trance_limit_use) {
		this.trance_limit_use = trance_limit_use?"y":"n";
	}
	public boolean isTrance_limit_excess() {
		if(trance_limit_excess==null)
            return true;
        return trance_limit_excess.equals("y");
	}
	public void setTrance_limit_excess(boolean trance_limit_excess) {
		this.trance_limit_excess = trance_limit_excess?"y":"n";
	}
	public boolean isTrance_hard_graph() {
		if(trance_hard_graph==null)
            return true;
        return trance_hard_graph.equals("y");
	}
	public void setTrance_hard_graph(boolean trance_hard_graph) {
		this.trance_hard_graph = trance_hard_graph?"y":"n";
	}
	public String getTrance_period_format() {
		return trance_period_format==null?"":trance_period_format;
	}
	public void setTrance_period_format(String trance_period_format) {
		this.trance_period_format = trance_period_format;
	}
	public String getActive_decision() {
		return active_decision==null?"":active_decision;
	}
	public void setActive_decision(String active_decision) {
		this.active_decision = active_decision;
	}
	/**
	 * Возвращает {@link Long} номер версии
	 * @return {@link Long} номер версии
	 */
	public Long getVersion() {
		return version;
	}
	/**
	 * Устанавливает {@link Long} номер версии
	 * @param version {@link Long} номер версии
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public boolean isTraderApprove() {
		return trader_approve!=null && trader_approve.equals("y");
	}
	public void setTraderApprove(boolean trader_approve) {
		this.trader_approve = trader_approve?"y":"n";
	}
	public Date getTrader_approve_date() {
		return trader_approve_date;
	}
	public void setTrader_approve_date(Date trader_approve_date) {
		this.trader_approve_date = trader_approve_date;
	}
	public Long getTrader_approve_user() {
		return trader_approve_user;
	}
	public void setTrader_approve_user(Long trader_approve_user) {
		this.trader_approve_user = trader_approve_user;
	}
	public String getCed_approve_login() {
		return ced_approve_login;
	}
	public void setCed_approve_login(String ced_approve_login) {
		this.ced_approve_login = ced_approve_login;
	}
	public Date getCed_approve_date() {
		return ced_approve_date;
	}
	public void setCed_approve_date(Date ced_approve_date) {
		this.ced_approve_date = ced_approve_date;
	}
	public boolean isMainOrgChangeble() {
		return mainOrgChangeble != null && mainOrgChangeble.equalsIgnoreCase("y");
	}
	public String getMainOrgChangeble() {
		return mainOrgChangeble;
	}
	public void setMainOrgChangeble(String mainOrgChangeble) {
		this.mainOrgChangeble = mainOrgChangeble;
	}
	public String getPmnOrder() {
		return pmnOrder==null?" ":pmnOrder;
	}
	public void setPmnOrder(String pmnOrder) {
		this.pmnOrder = pmnOrder;
	}

    public String getMainOrgGroup() {
        return mainOrgGroup;
    }

    public void setMainOrgGroup(String mainOrgGroup) {
        this.mainOrgGroup = mainOrgGroup;
    }

    public boolean isImported(){
        return is_imported!=null && is_imported > 0;
    }

    public Boolean isInterestRateFixed() {
        return interestRateFixed;
    }

    public void setInterestRateFixed(Boolean interestRateFixed) {
        this.interestRateFixed = interestRateFixed;
    }

    public Boolean isInterestRateDerivative() {
        return interestRateDerivative;
    }

    public void setInterestRateDerivative(Boolean interestRateDerivative) {
        this.interestRateDerivative = interestRateDerivative;
    }

    /**
     * Список индикативных ставок через запятую
     */
    public String getIndratesDisplay(Long idPeriod) {
        ArrayList<String> res = new ArrayList<String>();
        CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        FloatPartOfActiveRate[] fpars = compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(), null);
        for(IndrateMdtaskJPA ind : getIndrates()){
            if(idPeriod==null && ind.getIdFactpercent()!=null
                    ||idPeriod!=null&&!idPeriod.equals(ind.getIdFactpercent())){continue;}
            for(FloatPartOfActiveRate fpar : fpars)
                if(ind.getIndrate() != null && ind.getIndrate().equals(fpar.getId()))
                    res.add(fpar.getText());
        }
        return CollectionUtils.listJoin(res);
    }
    public String getIndratesDisplay() {
        return getIndratesDisplay(null);
    }

    /**
     * список индикативных ставок в целом по сделке
     */
    public List<String> getMainIndrates() {
        ArrayList<String> res = new ArrayList<String>();
        for(IndrateMdtaskJPA ind : getIndrates())
            if(ind.getIdFactpercent() == null)
                res.add(ind.getIndrate());
        return res;
    }
    public List<IndrateMdtaskJPA> getIndrates() {
        return indrates;
    }

    public void setIndrates(List<IndrateMdtaskJPA> indrates) {
        this.indrates = indrates;
    }

    public Long getProductMonitoring() {
        return productMonitoring;
    }

    public void setProductMonitoring(Long productMonitoring) {
        this.productMonitoring = productMonitoring;
    }

    public Long getAdditionalContract() {
        return additionalContract;
    }

    public void setAdditionalContract(Long additionalContract) {
        this.additionalContract = additionalContract;
    }

    public String getMonitoringMode() {
        return monitoringMode==null?"Редактирование ставки":monitoringMode;
    }

    public void setMonitoringMode(String monitoringMode) {
        this.monitoringMode = monitoringMode;
    }

    public Long getMonitoringUserWorkId() {
        return monitoringUserWorkId;
    }

    public void setMonitoringUserWorkId(Long monitoringUserWorkId) {
        this.monitoringUserWorkId = monitoringUserWorkId;
    }

    public Long getMonitoringPriceUserId() {
        return monitoringPriceUserId;
    }

    public void setMonitoringPriceUserId(Long monitoringPriceUserId) {
        this.monitoringPriceUserId = monitoringPriceUserId;
    }
    
    /**
     * Возвращает комментарий к контролю целевого использования
     *
     * @return комментарий к контролю целевого использования
     */
    public String getTargetTypeControlNote() {
		return targetTypeControlNote;
	}
    
	/**
	 * Устанавливает комментарий к контролю целевого использования
	 *
	 * @param targetTypeControlNote комментарий к контролю целевого использования
	 */
	public void setTargetTypeControlNote(String targetTypeControlNote) {
		this.targetTypeControlNote = targetTypeControlNote;
	}

    public Long getMonitoringMdtask() {
        return monitoringMdtask;
    }

    public void setMonitoringMdtask(Long monitoringMdtask) {
        this.monitoringMdtask = monitoringMdtask;
    }

    public Long getCrossSellType() {
        return crossSellType;
    }

    public void setCrossSellType(Long crossSellType) {
        this.crossSellType = crossSellType;
    }

    public Long getPeriod_days() {
        return period_days;
    }
    public void setPeriod_days(Long period_days) {
        this.period_days = period_days;
    }
    private void calcPeriodDay() {
        if (getPeriod() == null || getPeriodDimension() == null) {
            setPeriod_days(null);
            return;
        }
        if (getPeriodDimension().equals("дн."))
            setPeriod_days(getPeriod());
        if (getPeriodDimension().equals("мес."))
            setPeriod_days(getPeriod()*30);
        if (getPeriodDimension().equals("г./лет"))
            setPeriod_days(getPeriod()*365);
    }
}
