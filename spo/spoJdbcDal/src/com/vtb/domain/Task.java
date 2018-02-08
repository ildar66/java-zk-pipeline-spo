package com.vtb.domain;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vtb.util.ApplProperties;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.domain.spo.CCStatus;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.md.domain.Withdraw;

/**
 * Заявка. Может быть лимитом, сублимитом или сделкой.
 *
 * @author Andrey Pavlenko
 */
public class Task extends VtbObject {
	private static final Logger LOGGER = Logger.getLogger(Task.class.getName());
    private static final long serialVersionUID = 1L;
    private String displayNumber;
    private Long id_task;           // [mdtask.id_mdtask] код заявки (уникальный id)
    private Long id_pup_process;    // [mdtask.id_pup_process ref. processes.id_process] id процесса (связь с уникальным процессом, созданным для заявки)
    private Integer id_pup_process_type;  // [processes.id_type_process] id типа процесса
    private Long parent;                  // [mdtask.parentid ref. mdtask.id_mdtask] ссылка на заявку-родителя в иерархии заявок!!!

    @Deprecated
    private String inLimit;               // [mdtask.CRMINLIMIT char(12) ref. mdtask.CRMID] проводится ли в рамках лимита. Номер заявки в системе CRM.
                                          // Если null, то не создан в CRM. Только для СДЕЛКИ!!!

    private boolean inLimitForPrintForm = false;  // can't check for empty. In print form, always adds SPACE symbols
    private TaskHeader header;     // ряд параметров вынесен в отдельный класс
    private ParentData parentData;
    private TaskStatusReturn taskStatusReturn;
    private ArrayList<TaskContractor> contractors;
    private TaskContractor mainBorrower; // reference to contractors[0] to use vain borrower in print forms!! Don't use it in form processing!
    private ArrayList<Trance> tranceList;
    private String tranceComment;
    private boolean faces3;//распространяется на 3 лица
    private String description="";
    private QuestionType ccQuestionType=null;
    private String ccPreambulo="";
    private CCStatus ccStatus;
    private String OPPORTUNITYID="";//номер заявки CRM
    private boolean deleted=false;//удалена
    private Long documents_count = 0L;//количество прикрепленных документов
    private String file4CClist="";//список guid документов в одинарных ковычках через запятую,
    //которые можно показывать в КК

    private ArrayList<TaskStopFactor> taskClientStopFactorList;
    private ArrayList<TaskStopFactor> taskSecurityStopFactorList;
    private ArrayList<TaskStopFactor> taskStopFactor3List;
    private ArrayList<Commission> CommissionList;
    private ArrayList<CommissionDeal> CommissionListDeal;
    private ArrayList<Fine> FineList = new ArrayList<Fine>();
    private ArrayList<TaskCurrency> currencyList;  // Лимит(Сублимит): список 'Валюты операций в рамках Лимита/ Сублимита'

    private InterestPay interestPay = null;
    private PrincipalPay principalPay = null;
    private ArrayList<PaymentSchedule> paymentScheduleList;

    private String operationDecisionList="DEBUG";  // Порядок принятия решений о проведении операций

    private ArrayList<String> simpleOperationDecisionList;  // Порядок принятия решений о проведении операций

    private List<Currency> parentCurrencyList;
    private String creditDecisionProject;             // выбранный проект кредитноьго решения для передачи в СКК (ссылка на id таблицы appfiles)


    private ArrayList<Premium> premiumList; // таблица вознаграждения для процентной ставки. ТОЛЬКО ДЛЯ ФОРМИРОВАНИЯ ОТЧЕТОВ. ЗАПОЛНЯЕТСЯ ИЗ TaskJPA

    /**
     * Большие текстовые поля
     */
    private ArrayList<ExtendText> extendTexts = new ArrayList<ExtendText>();
    private ArrayList<DepartmentAgreement> departmentAgreements;

    private TaskSupply supply;
    private TaskProcent procent;
    private ArrayList<FactPercent> factPercentList=new ArrayList<FactPercent>();
    private Decision decision;
    private Temp temp;
    private Main main;
    private GeneralCondition generalCondition;
    private Integer authorizedBody;
    private String go;//аттрибут ПУП ГО
    private String collegial;//аттрибут ПУП Коллегиальный
    private ArrayList<Comment> commentList;
    private ArrayList<OtherCondition> otherCondition;
    private ArrayList<SpecialCondition> specialCondition;
    private ArrayList<Agreement> agreementList;
    private ArrayList<EarlyPayment> earlyPaymentList;

    private ArrayList<ProjectTeamMember> projectTeamStructurerList= new ArrayList<ProjectTeamMember>();
    private ArrayList<ProjectTeamMember> projectTeamClientManagerList= new ArrayList<ProjectTeamMember>();
    private ArrayList<ProjectTeamMember> projectTeamSPKZList= new ArrayList<ProjectTeamMember>();
    private ArrayList<ProjectTeamMember> projectTeamStrucurerManagerList= new ArrayList<ProjectTeamMember>();
    private ArrayList<ProjectTeamMember> projectTeamCreditAnalyticList= new ArrayList<ProjectTeamMember>();
    private ArrayList<ProjectTeamMember> projectTeamProductManagerList= new ArrayList<ProjectTeamMember>();

    private ArrayList<Contract> contractList = new ArrayList<Contract>();
    private ArrayList<PromissoryNote> promissoryNoteList = new ArrayList<PromissoryNote>();

    private String ind_rate;  // id индикативной ставки (подмешиваем только при генерации отчетов. В других местах не заполняется и не используется)

    private List<Withdraw> withdraws=new ArrayList<Withdraw>();

    public Task() {
        this.header = new TaskHeader();
        parentData = new ParentData();
        contractors = new ArrayList<TaskContractor>();
        tranceList = new ArrayList<Trance>();
        decision = new Decision();
        main = new Main();
        temp = new Temp();
        generalCondition = new GeneralCondition();
        commentList = new ArrayList<Comment>();
        earlyPaymentList = new ArrayList<EarlyPayment>();
        agreementList = new ArrayList<Agreement>();
        currencyList = new ArrayList<TaskCurrency>();

        FineList = new ArrayList<Fine>();
        CommissionList = new ArrayList<Commission>();
        CommissionListDeal = new ArrayList<CommissionDeal>();
        procent = new TaskProcent(null);
        taskClientStopFactorList = new ArrayList<TaskStopFactor>();
        taskStopFactor3List = new ArrayList<TaskStopFactor>();
        taskSecurityStopFactorList = new ArrayList<TaskStopFactor>();
        otherCondition = new ArrayList<OtherCondition>();
        specialCondition = new ArrayList<SpecialCondition>();
        supply = new TaskSupply();
        departmentAgreements = new ArrayList<DepartmentAgreement>();
        extendTexts = new ArrayList<ExtendText>();
        taskStatusReturn = new TaskStatusReturn(null,"");
        ccQuestionType = new QuestionType(null);
        principalPay = new PrincipalPay(null);
        interestPay = new InterestPay(null);
        paymentScheduleList = new ArrayList<PaymentSchedule>();
        operationDecisionList = "";
        parentCurrencyList = new ArrayList<Currency>();
    }



    /**
     * создает новую заявку с id.
     *
     * @param id_task
     *            - новый уникальный айдишник
     */
    public Task(Long id_task) {
        this();
        this.id_task = id_task;
    }

    @Override
    public String toString(){
        return "num: "+this.getHeader().getCombinedNumber()+" mdtaskid:"+id_task;
    }

    /**
     * возвращает отчет "Проект решения" в виде html куска (без тегов header, body)
     * @return HTML отчет
     */
    public String toHTMLString(String xslt) {
        try{
            LOGGER.info(xslt);
            StreamSource xsltSource =
                new StreamSource(new ByteArrayInputStream(xslt.getBytes("UTF-8")));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true); //нужно, если template в DOMSource
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document docData = documentBuilder.newDocument();
            Element number = docData.createElement("number");
            number.appendChild(docData.createTextNode("12345"));
            docData.appendChild(number);
            LOGGER.info(docData.toString());
            //docData.appendChild(this.toXML(docData,"task"));
            // получаем StreamResult
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            Transformer t = TransformerFactory.newInstance().newTransformer(
                    xsltSource);
            //применение стилей xls-документа
            t.setOutputProperty(OutputKeys.METHOD, "html"); //тип "html" || "xml" || "text"
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource ds = new DOMSource(docData);
            t.transform(ds, result);
            String res = result.getWriter().toString();
            LOGGER.info(res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "xslt error "+e.getMessage();
        }
    }

    /** Контрагент в заявке */
    public ArrayList<TaskContractor> getContractors() {
        return contractors;
    }

    /** заголовок */
    public TaskHeader getHeader() {
        return header;
    }

    /** id заявки. Уникален. Отличается от номера. */
    public Long getId_task() {
        return id_task;
    }


    /** обеспечение */
    public TaskSupply getSupply() {
        return supply;
    }

    /** решение по заявке */
    public Decision getDecision() {
        return decision;
    }

    /** основные параметры */
    public Main getMain() {
        return main;
    }

    /** общие условия */
    public GeneralCondition getGeneralCondition() {
        return generalCondition;
    }

    /** уполномоченный орган */
    public void setAuthorizedBody(Integer authorizedBody) {
        this.authorizedBody = authorizedBody;
    }

    /** уполномоченный орган */
    public Integer getAuthorizedBody() {
        return authorizedBody;
    }

    public Temp getTemp() {
        return temp;
    }


    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    /** комментарии */
    public ArrayList<Comment> getComment() {
        return commentList;
    }

    /** прочие условия */
    public ArrayList<OtherCondition> getOtherCondition() {
        return otherCondition;
    }


    /**
	 * @param otherCondition the otherCondition to set
	 */
	public void setOtherCondition(ArrayList<OtherCondition> otherCondition) {
		this.otherCondition = otherCondition;
	}



	/** стоп факторы */
    public ArrayList<TaskStopFactor> getTaskClientStopFactorList() {
        return taskClientStopFactorList;
    }

    public ArrayList<TaskStopFactor> getTaskSecurityStopFactorList() {
        return taskSecurityStopFactorList;
    }

    public ArrayList<TaskStopFactor> getTaskStopFactor3List() {
        return taskStopFactor3List;
    }



    public void setTaskStopFactor3List(ArrayList<TaskStopFactor> taskStopFactor3List) {
        this.taskStopFactor3List = taskStopFactor3List;
    }



    /** процентная ставка */
    public TaskProcent getTaskProcent() {
        return procent;
    }

    public void setTaskProcent(TaskProcent procent) {
        this.procent = procent;
    }

    /** комиссии */
    public ArrayList<Commission> getCommissionList() {
        return CommissionList;
    }

    /** комиссии для сделки*/
    public ArrayList<CommissionDeal> getCommissionDealList() {
        return CommissionListDeal;
    }

    /** штрафы */
    public ArrayList<Fine> getFineList() {
        return FineList;
    }

    public ArrayList<Agreement> getAgreementList() {
        return agreementList;
    }

    public void setId_task(Long id_task) {
        this.id_task = id_task;
    }

    public Long getId_pup_process() {
        return id_pup_process;
    }

    public void setId_pup_process(Long id_pup_process) {
        this.id_pup_process = id_pup_process;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public List<ExtendText> getExtendTexts() {
        return extendTexts;
    }

    public ArrayList<EarlyPayment> getEarlyPaymentList() {
        return earlyPaymentList;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOPPORTUNITYID() {
        return OPPORTUNITYID;
    }


    public void setOPPORTUNITYID(String opportunityid) {
        OPPORTUNITYID = opportunityid;
    }


    public Integer getId_pup_process_type() {
        return id_pup_process_type;
    }


    public void setId_pup_process_type(Integer id_pup_process_type) {
        this.id_pup_process_type = id_pup_process_type;
    }

    public ArrayList<DepartmentAgreement> getDepartmentAgreements() {
        return departmentAgreements;
    }


    public boolean isDeleted() {
        return deleted;
    }


    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    public Long getDocuments_count() {
        return documents_count;
    }


    public void setDocuments_count(Long documents_count) {
        this.documents_count = documents_count;
    }


	public String getFile4CClist() {
		return file4CClist;
	}


	public void setFile4CClist(String file4CClist) {
		this.file4CClist = file4CClist;
	}


    /**
	 * @return the inLimit
	 */
	public String getInLimit() {
		return inLimit;
	}


	/**
	 * @param inLimit the inLimit to set
	 */
	public void setInLimit(String inLimit) {
		this.inLimit = inLimit;
		if ((inLimit == null) || ("".equals(inLimit))) setInLimitForPrintForm(false);
		else setInLimitForPrintForm(true);
	}


	/**
	 * @return the taskStatusReturn
	 */
	public TaskStatusReturn getTaskStatusReturn() {
		return taskStatusReturn;
	}


	/**
	 * @return the specialCondition
	 */
	public ArrayList<SpecialCondition> getSpecialCondition() {
		return specialCondition;
	}


	/**
	 * @return the ccQuestionType
	 */
	public QuestionType getCcQuestionType() {
		return ccQuestionType;
	}


	/**
	 * @param ccQuestionType the ccQuestionType to set
	 */
	public void setCcQuestionType(QuestionType ccQuestionType) {
		this.ccQuestionType = ccQuestionType;
	}


	/**
	 * @return the ccPreambulo
	 */
	public String getCcPreambulo() {
		return ccPreambulo==null?"":ccPreambulo;
	}


	/**
	 * @param ccPreambulo the ccPreambulo to set
	 */
	public void setCcPreambulo(String ccPreambulo) {
		this.ccPreambulo = ccPreambulo;
	}


	/**
	 * @return распространяется на 3 лица
	 */
	public boolean isFaces3() {
		return faces3;
	}


	/**
	 * @param faces3 распространяется на 3 лица
	 */
	public void setFaces3(boolean faces3) {
		this.faces3 = faces3;
	}

	public String getNumberDisplay(){
        if(header.getNumber().longValue()==0)return "Новый";
        if(header.getCombinedNumber() != null) return header.getCombinedNumber();
	    if(header.getCrmcode()!=null)return header.getCrmcode();
    	return header.getNumber().toString();
    }

	/**
	 * tells whether task is limit
	 * @return
	 */
	public boolean isLimit() {
	    return header.isLimit();
	}

	/**
     * tells whether task is subLimit
     * @return
     */
    public boolean isSubLimit() {
        return header.isSubLimit();
    }

    /**
     * tells whether task is deal (SDELKA in russian)
     * @return
     */
    public boolean isOpportunity() {
        return header.isOpportunity();
    }
    public boolean isProduct() {
        return header.isOpportunity();
    }

    public boolean isCrossSell(){
        return header.getTasktype().equals("c");
    }

	/**
	 * возвращает список названий организаций через запятую
	 * @return
	 */
	public String getOrganisation(){
		String res="";
		for (int i=0;i<contractors.size();i++){
			if(i>0)res += ", ";
			res += contractors.get(i).getOrg().getAccount_name();
		}
		return res;
	}

    public InterestPay getInterestPay() {
        return interestPay;
    }

    public void setInterestPay(InterestPay interestPay) {
        this.interestPay = interestPay;
    }

    public PrincipalPay getPrincipalPay() {
        return principalPay;
    }

    public void setPrincipalPay(PrincipalPay principalPay) {
        this.principalPay = principalPay;
    }

    public ArrayList<PaymentSchedule> getPaymentScheduleList() {
        return paymentScheduleList;
    }

    public void setPaymentScheduleList(
            ArrayList<PaymentSchedule> paymentScheduleList) {
        this.paymentScheduleList = paymentScheduleList;
    }

    public ArrayList<TaskCurrency> getCurrencyList() {
        return currencyList;
    }
    public boolean haveCurrency(String cur){
        if(cur==null)
            return false;
        for(TaskCurrency taskCurrency: currencyList)
            if(taskCurrency.isFlag() && taskCurrency.getCurrency()!=null && taskCurrency.getCurrency().getCode().equalsIgnoreCase(cur))
                return true;
        return false;
    }

    public void setCurrencyList(ArrayList<TaskCurrency> currencyList) {
        this.currencyList = currencyList;
    }

    public void setOperationDecisionList(String operationDecisionList) {
        this.operationDecisionList = operationDecisionList;
    }

    public String getOperationDecisionList() {
        return operationDecisionList;
    }


    public List<Currency> getParentCurrencyList() {
        return parentCurrencyList;
    }


    public void setParentCurrencyList(List<Currency> parentCurrencyList) {
        this.parentCurrencyList = parentCurrencyList;
    }
    /**
     * @return the tranceList
     */
    public ArrayList<Trance> getTranceList() {
        return tranceList;
    }
    /**
     * @return Комментарий по графику использования
     */
    public String getTranceComment() {
        return tranceComment==null?"":tranceComment;
    }
    /**
     * @param tranceComment Комментарий по графику использования
     */
    public void setTranceComment(String tranceComment) {
        this.tranceComment = tranceComment;
    }

    /**
     * Этот вид продукта -- кредитная линия?
     * @return
     */
    public boolean isCreditLine() {
        if (main == null) return false;
        return (main.isDebtLimit() || main.isLimitIssue());
    }
    public void setInLimitForPrintForm(boolean inLimitForPrintForm) {
        this.inLimitForPrintForm = inLimitForPrintForm;
    }
    public boolean isInLimitForPrintForm() {
        return inLimitForPrintForm;
    }
    /**
     * @return Статус заявки в КК.
     */
    public CCStatus getCcStatus() {
        if (ccStatus == null) ccStatus = new CCStatus();
        return ccStatus;
    }
    /**
     * @param ccStatus Статус заявки в КК.
     */
    public void setCcStatus(CCStatus ccStatus) {
        this.ccStatus = ccStatus;
    }

	public String getActiveStageUrl() {
		String report = "file:///" + ApplProperties.getReportsPath() + "Audit/active_stages.rptdesign";
		return "reportPrintFormRenderAction.do?__format=html&notused=off&__report=" + report
				+ "&isDelinquency=-1&correspondingDeps=on" + "&p_idDepartment=-1&id_ClaimFromList="
				+ header.getNumber() + "&mdtaskId=" + getId_task();
	}

    /**
     * @return go
     */
    public String getGo() {
        return go;
    }
    /**
     * @param go go
     */
    public void setGo(String go) {
        this.go = go;
    }
    /**
     * @return collegial
     */
    public String getCollegial() {
        return collegial;
    }
    /**
     * @param collegial collegial
     */
    public void setCollegial(String collegial) {
        this.collegial = collegial;
    }
    public boolean isSectionPriceConditionEmpty(){
        return ((getTaskProcent().getProcent() == null) || ((getTaskProcent().getProcent() != null) && (getTaskProcent().getProcent().equals(new Double("0.0")))))
        && ((getTaskProcent().getRiskpremium() == null) || ((getTaskProcent().getRiskpremium() != null) && (getTaskProcent().getRiskpremium().equals(new Double("0.0")))))
        && ((getTaskProcent().getDescription()== null) || ((getTaskProcent().getDescription()!= null) && (getTaskProcent().getDescription().equals(""))))
        && ((getTaskProcent().getRiskDescription()== null) || ((getTaskProcent().getRiskDescription()!= null) && (getTaskProcent().getRiskDescription().equals(""))))
        && ((getPrincipalPay() == null) || ((getPrincipalPay() != null) && (getPrincipalPay().getAmount() == null)))
        && ((getInterestPay() == null) || ((getInterestPay() != null) && (getInterestPay().getNumDay() == null)))
        && (paymentScheduleList.size() == 0) && (getFineList().size()==0) && (getCommissionDealList().size()==0);
    }
    public boolean isSectionConclusionEmpty(){
        return getGo().isEmpty()&&getCollegial().isEmpty()&&getTemp().getPlanMeetingDate()==null;
    }

    public ArrayList<FactPercent> getFactPercentList() {
        return factPercentList;
    }

    public void setFactPercentList(ArrayList<FactPercent> factPercentList) {
        this.factPercentList = factPercentList;
    }



    public TaskContractor getMainBorrower() {
        return mainBorrower;
    }

    public void setMainBorrower(TaskContractor mainBorrower) {
        this.mainBorrower = mainBorrower;
    }

	public ArrayList<ProjectTeamMember> getProjectTeamStructurerList() {
		return projectTeamStructurerList;
	}

	public void setProjectTeamStructurerList(ArrayList<ProjectTeamMember> projectTeamStructurerList) {
		this.projectTeamStructurerList = projectTeamStructurerList;
	}

	public ArrayList<ProjectTeamMember> getProjectTeamClientManagerList() {
		return projectTeamClientManagerList;
	}

	public void setProjectTeamClientManagerList(ArrayList<ProjectTeamMember> projectTeamClientManagerList) {
		this.projectTeamClientManagerList = projectTeamClientManagerList;
	}

	public ArrayList<ProjectTeamMember> getProjectTeamSPKZList() {
		return projectTeamSPKZList;
	}

	public void setProjectTeamSPKZList(ArrayList<ProjectTeamMember> projectTeamSPKZList) {
		this.projectTeamSPKZList = projectTeamSPKZList;
	}

	public ArrayList<ProjectTeamMember> getProjectTeamStrucurerManagerList() {
		return projectTeamStrucurerManagerList;
	}

	public void setProjectTeamStrucurerManagerList(ArrayList<ProjectTeamMember> projectTeamStrucurerManagerList) {
		this.projectTeamStrucurerManagerList = projectTeamStrucurerManagerList;
	}

	public ArrayList<ProjectTeamMember> getProjectTeamCreditAnalyticList() {
		return projectTeamCreditAnalyticList;
	}

	public void setProjectTeamCreditAnalyticList(ArrayList<ProjectTeamMember> projectTeamCreditAnalyticList) {
		this.projectTeamCreditAnalyticList = projectTeamCreditAnalyticList;
	}

	public ArrayList<ProjectTeamMember> getProjectTeamProductManagerList() {
		return projectTeamProductManagerList;
	}

	public void setProjectTeamProductManagerList(ArrayList<ProjectTeamMember> projectTeamProductManagerList) {
		this.projectTeamProductManagerList = projectTeamProductManagerList;
	}

	public ArrayList<Contract> getContractList() {
		return contractList;
	}

	public void setContractList(ArrayList<Contract> contractList) {
		this.contractList = contractList;
	}

	public ArrayList<PromissoryNote> getPromissoryNoteList() {
		return promissoryNoteList;
	}

	public void setPromissoryNoteList(ArrayList<PromissoryNote> promissoryNoteList) {
		this.promissoryNoteList = promissoryNoteList;
	}

    public String getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(String displayNumber) {
        this.displayNumber = displayNumber;
    }

	public String getCreditDecisionProject() {
		return creditDecisionProject;
	}

	public void setCreditDecisionProject(String creditDecisionProject) {
		this.creditDecisionProject = creditDecisionProject;
	}

	public ParentData getParentData() {
		return parentData;
	}

	public void setParentData(ParentData parentData) {
		this.parentData = parentData;
	}

	public String getInd_rate() {
        return ind_rate;
    }
    public void setInd_rate(String ind_rate) {
        this.ind_rate = ind_rate;
    }

	public ArrayList<Premium> getPremiumList() {
		return premiumList;
	}

	public void setPremiumList(ArrayList<Premium> premiumList) {
		this.premiumList = premiumList;
	}
	/* исправление косяка с траншами. Костыли. Нужно чтобы работало уже сегодня, сегодна будут тестировать. */
	public boolean needRefreshFrame(){
		for(Trance t : getTranceList()){
			if(t.getId()==null)
				return true;
		}
		return false;
	}



	public ArrayList<String> getSimpleOperationDecisionList() {
		return simpleOperationDecisionList;
	}



	public void setSimpleOperationDecisionList(
			ArrayList<String> simpleOperationDecisionList) {
		this.simpleOperationDecisionList = simpleOperationDecisionList;
	}



	public List<Withdraw> getWithdraws() {
		return withdraws;
	}



	public void setWithdraws(List<Withdraw> withdraws) {
		this.withdraws = withdraws;
	}
	public String getContractorRole(String contractorId) {
		HashSet<String> res = new HashSet<String>();
		for(TaskContractor org : getContractors())
			if(org.getOrg().getAccountid().equals(contractorId))
				for(ContractorType type : org.getOrgType())
				    res.add(type.getDescription());
		for(Deposit supply : getSupply().getDeposit())
			if(supply.getPerson()!=null && supply.getPerson().getId()!= null && supply.getPerson().getId().toString().equals(contractorId)
			|| supply.getOrg()!=null && supply.getOrg().getId()!=null && supply.getOrg().getId().equals(contractorId))
				res.add("Залогодатель");
		for(Guarantee supply : getSupply().getGuarantee())
			if(supply.getPerson()!=null && supply.getPerson().getId()!= null && supply.getPerson().getId().toString().equals(contractorId)
			|| supply.getOrg()!=null && supply.getOrg().getId()!=null && supply.getOrg().getId().equals(contractorId))
				res.add("Гарант");
		for(Warranty supply : getSupply().getWarranty())
			if(supply.getPerson()!=null && supply.getPerson().getId()!= null && supply.getPerson().getId().toString().equals(contractorId)
			    || supply.getOrg()!=null && supply.getOrg().getId()!=null && supply.getOrg().getId().equals(contractorId))
				res.add("Поручитель");
		return compileString(res);
	}
	private String compileString(HashSet<String> set){
		String res = "";
		for(String s : set){
			if(!res.isEmpty())
				res += ", ";
			res += s;
		}
		return res;
	}
	/**
	 * Возвращает предупреждения о незаполненых полях условий досрочного погашения
	 * @return
	 */
    public String conditionWarning(){
    	if(getEarlyPaymentList()==null || getEarlyPaymentList().isEmpty())
    		return null;
    	for(EarlyPayment e : getEarlyPaymentList()){
    		if( (e.getPermission()==null || e.getPermission().isEmpty()) && e.getCondition().isEmpty())
                return "В подсекции «Условия досрочного погашения» необходимо заполнить поля «Условие досрочного погашения» или «Комментарий»";
    	}
    	return null;
    }
}
