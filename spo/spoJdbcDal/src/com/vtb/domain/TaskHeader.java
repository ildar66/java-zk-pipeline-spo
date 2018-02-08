package com.vtb.domain;

import java.util.ArrayList;

import ru.masterdm.compendium.domain.Department;

public class TaskHeader  extends VtbObject{
	
    private static final long serialVersionUID = 5395812271250335740L;
	private Long number; // [mdtask.MDTASK_NUMBER -- отображаемый пользователю номер заявки (внутренний, не CRM)]. Полный номер -- в поле combinedNumber
	private boolean typeLimit = false;
	private boolean typeSublimit = true;     //set true, because type is read from processType. 
	                                         //If null, just setProcessType() isn'r called, and trigger (look beneath) is never called. 
	private boolean typeOpportunity = false;	
	private String title;        // наименование лимита\сублимита\сделки. Дополнительная информация...
	private String priority;
	private String status;
	private String processType;
	private String tasktype;
	private Department startDepartment = null;
	private Long subplace = null;
	private ArrayList<TaskManager> managers;//менеджеры основного инициирующего
	private ArrayList<TaskDepartment> otherDepartments;//другие инициирующие
	private ArrayList<Department> places;
	private String manager;
	private Department place=null;
	private ArrayList<TaskProduct> opportunityTypes;
	private ArrayList<TaskProduct> parentOpportunityTypes;
	private Integer idLimitType;
	private String limitTypeName;
	private OperationType operationtype;//Тип операции
	private String crmid;//ID в CRM
	private String crmQueueId;//номер очереди загрузки в таблице обмена CRM
	private String crmlimitname;//название лимита
	private String crmcurrencylist;//список кодов допустимых валют через запятую
	private String crmcode;//отображаемый номер
	private String crmstatus;// в представлении V_SPO_FB_LIMIT есть поле STATUS, его возможные значения "Открытый", "Утвержденный"
	private String combinedNumber;  // строка вида 'CRM_code (number)' для отображения в отчетах и прочем...  
	private Long version; // номер версии сделки
	
	public TaskHeader() {
        super();
        opportunityTypes = new ArrayList<TaskProduct>();
        parentOpportunityTypes = new ArrayList<TaskProduct>();
        otherDepartments = new ArrayList<TaskDepartment>();
        operationtype= new OperationType(null,null);
        setPlaces(new ArrayList<Department>());
        managers=new ArrayList<TaskManager>();
    }
	
	public boolean isTypeLimit() { return typeLimit; }
    public void setTypeLimit(boolean typeLimit) { this.typeLimit = typeLimit;}
    public boolean isTypeSublimit() {return typeSublimit; }
    public void setTypeSublimit(boolean typeSublimit) { this.typeSublimit = typeSublimit;}
    public boolean isTypeOpportunity() {return typeOpportunity;}
    public void setTypeOpportunity(boolean typeOpportunity) { this.typeOpportunity = typeOpportunity;}

	
	public boolean isLimit(){
		return getTasktype().equals("l");
	}
	
	public boolean isOpportunity(){
		return getTasktype().equals("p");
	}
	
	public boolean isSubLimit(){
        return getTasktype().isEmpty();
    }
	
    public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		if (crmcode==null)crmcode=number.toString();
		this.number = number;
	}
	public String getPriority() {
		return priority==null?"":priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public Department getStartDepartment() {
		return startDepartment;
	}
	public void setStartDepartment(Department startDepartment) {
		this.startDepartment = startDepartment;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public Department getPlace() {
		return place;
	}
	public void setPlace(Department place) {
		this.place = place;
	}

    public Integer getIdLimitType() {
        return idLimitType;
    }
    public void setIdLimitType(Integer idLimitType) {
        this.idLimitType = idLimitType;
    }
    public ArrayList<TaskProduct> getOpportunityTypes() {
        return opportunityTypes;
    }
    public boolean isProductTypesSelected(String idProduct){
        if(opportunityTypes==null || idProduct==null)
            return false;
        for(TaskProduct tp : opportunityTypes)
            if(tp.getId().equals(idProduct))
                return true;
        return false;
    }
    public void setOpportunityTypes(ArrayList<TaskProduct> opportunityTypes) {
        this.opportunityTypes = opportunityTypes;
    }

	public OperationType getOperationtype() {
		return operationtype;
	}

	public void setOperationtype(OperationType operationtype) {
		this.operationtype = operationtype;
	}
	public ArrayList<TaskDepartment> getOtherDepartments() {
		return otherDepartments;
	}
	public void setOtherDepartments(ArrayList<TaskDepartment> otherDepartments) {
		this.otherDepartments = otherDepartments;
	}
	public String getStatus() {
		return status==null?"":status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProcessType() {
		return processType==null?"":processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
		if(processType!=null && processType.equals("Сделка"))
			this.tasktype="p";
		if(processType!=null && processType.equals("Лимит"))
			this.tasktype="l";
		if(processType!=null && processType.equals("Кросс-селл"))
			this.tasktype="c";
		setTypeLimit(isLimit());
		setTypeOpportunity(isOpportunity());
		setTypeSublimit(isSubLimit());
	}
	public void setPlaces(ArrayList<Department> places) {
		this.places = places;
	}
	public ArrayList<Department> getPlaces() {
		return places;
	}
	public ArrayList<TaskManager> getManagers() {
		return managers;
	}
	public void setManagers(ArrayList<TaskManager> managers) {
		this.managers = managers;
	}
	/**
	 * @return the crmid
	 */
	public String getCrmid() {
		return crmid;
	}
	/**
	 * @param crmid the crmid to set
	 */
	public void setCrmid(String crmid) {
		this.crmid = crmid;
	}
	/**
	 * @return the crmlimitname
	 */
	public String getCrmlimitname() {
		return crmlimitname;
	}
	/**
	 * @param crmlimitname the crmlimitname to set
	 */
	public void setCrmlimitname(String crmlimitname) {
		this.crmlimitname = crmlimitname;
	}
	/**
	 * @return the crmcurrencylist
	 */
	public String getCrmcurrencylist() {
		return crmcurrencylist;
	}
	/**
	 * @param crmcurrencylist the crmcurrencylist to set
	 */
	public void setCrmcurrencylist(String crmcurrencylist) {
		this.crmcurrencylist = crmcurrencylist;
	}
	/**
	 * @return the crmcode
	 */
	public String getCrmcode() {
		return crmcode;
	}
	/**
	 * @param crmcode the crmcode to set
	 */
	public void setCrmcode(String crmcode) {
		this.crmcode = crmcode;
	}
	/**
	 * @return the crmstatus
	 */
	public String getCrmstatus() {
		return crmstatus==null?"":crmstatus;
	}
	/**
	 * @param crmstatus the crmstatus to set
	 */
	public void setCrmstatus(String crmstatus) {
		this.crmstatus = crmstatus;
	}

	/**
	 * @return the limitTypeName
	 */
	public String getLimitTypeName() {
		return limitTypeName;
	}

	/**
	 * @param limitTypeName the LimitTypeName to set
	 */	
	public void setLimitTypeName(String limitTypeName) {
		this.limitTypeName = limitTypeName;
	}
    public String getCombinedNumber() {
        return combinedNumber;
    }
    public void setCombinedNumber(String combinedNumber) {
        this.combinedNumber = combinedNumber;
    }

	/**
     * Generates combined number to show in the reports and sets combinedNumber field   
     */
    public void generateCombinedNumber() {
        combinedNumber = generateCombinedNumber(crmcode, (number != null) ? String.valueOf(number) : null); 
    }
    
    /**
     * Generates combined number to show in the reports.
     * @param crmcode crmCode if present
     * @param number mdTask number
     */
    static public String generateCombinedNumber(String crmcode, String number) {
        if (number == null) return null;  
        if ((crmcode != null) && (!crmcode.equals("")) && !(number.equals(crmcode))) 
            return crmcode + " (" + number + ")";
        else return number; 
    }
    
    public ArrayList<TaskProduct> getParentOpportunityTypes() {
        return parentOpportunityTypes;
    }
    
    public void setParentOpportunityTypes(ArrayList<TaskProduct> parentOpportunityTypes) {
        this.parentOpportunityTypes = parentOpportunityTypes;
    }

    /**
     * @return номер очереди загрузки в таблице обмена CRM
     */
    public String getCrmQueueId() {
        return crmQueueId;
    }

    /**
     * @param crmQueueId номер очереди загрузки в таблице обмена CRM
     */
    public void setCrmQueueId(String crmQueueId) {
        this.crmQueueId = crmQueueId;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getTasktype() {
		return tasktype==null?"":tasktype;
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public Long getSubplace() {
		return subplace;
	}

	public void setSubplace(Long subplace) {
		this.subplace = subplace;
	}

}
