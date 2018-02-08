package com.vtb.domain;


/**
 * Single string data for report 'Active Stages' 
 * @author Michael Kuznetsov 
 */
public class JournalOfOperationsReportOperation  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private String claim_name;	
	private String claim_name_internal;
	private String claim_name_CRM;
	
	private String comment_field;

	private String date_from_sort; //DECIMAL
	private String date_from; //Date-time
	private String date_claimed; //Date-time
	private String date_to; //Date-time

	private String department_name;	 
	private String description_stage;
	private String description_status; 

	private String id_stage;  //DECIMAL
	private String id_process; //DECIMAL
	private String id_task; //DECIMAL
	
	private String plan_period; //DECIMAL
	private String limit_type; 
	private String fact_period; //DECIMAL 
	private String delinquency; //DECIMAL
	private String complation_description;	
	private String user_name;
		
	public JournalOfOperationsReportOperation() {
        super();        
    }
    
	public String getClaim_name() {
		return claim_name;
	}
    
	public void setClaim_name(String claim_name) {
		this.claim_name = claim_name;
	}

	public String getComment_field() {
		return comment_field;
	}

	public void setComment_field(String comment_field) {
		this.comment_field = comment_field;
	}

	public String getDate_from_sort() {
		return date_from_sort;
	}

	public void setDate_from_sort(String date_from_sort) {
		this.date_from_sort = date_from_sort;
	}

	public String getDate_from() {
		return date_from;
	}

	public void setDate_from(String date_from) {
		this.date_from = date_from;
	}

	public String getDate_claimed() {
		return date_claimed;
	}

	public void setDate_claimed(String date_claimed) {
		this.date_claimed = date_claimed;
	}

	public String getDate_to() {
		return date_to;
	}

	public void setDate_to(String date_to) {
		this.date_to = date_to;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public String getDescription_stage() {
		return description_stage;
	}

	public void setDescription_stage(String description_stage) {
		this.description_stage = description_stage;
	}

	public String getDescription_status() {
		return description_status;
	}

	public void setDescription_status(String description_status) {
		this.description_status = description_status;
	}

	public String getId_stage() {
		return id_stage;
	}

	public void setId_stage(String id_stage) {
		this.id_stage = id_stage;
	}

	public String getId_process() {
		return id_process;
	}

	public void setId_process(String id_process) {
		this.id_process = id_process;
	}


	public String getId_task() {
		return id_task;
	}

	public void setId_task(String id_task) {
		this.id_task = id_task;
	}


	public String getPlan_period() {
		return plan_period;
	}

	public void setPlan_period(String plan_period) {
		this.plan_period = plan_period;
	}

	public String getLimit_type() {
		return limit_type;
	}

	public void setLimit_type(String limit_type) {
		this.limit_type = limit_type;
	}

	public String getFact_period() {
		return fact_period;
	}

	public void setFact_period(String fact_period) {
		this.fact_period = fact_period;
	}

	public String getDelinquency() {
		return delinquency;
	}

	public void setDelinquency(String delinquency) {
		this.delinquency = delinquency;
	}

	public String getComplation_description() {
		return complation_description;
	}

	public void setComplation_description(String complation_description) {
		this.complation_description = complation_description;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getClaim_name_internal() {
        return claim_name_internal;
    }

    public void setClaim_name_internal(String claim_name_internal) {
        this.claim_name_internal = claim_name_internal;
    }

    public String getClaim_name_CRM() {
        return claim_name_CRM;
    }

    public void setClaim_name_CRM(String claim_name_CRM) {
        this.claim_name_CRM = claim_name_CRM;
    }
    
    public void generateClaimName() {
        if ((claim_name_CRM != null) && (!claim_name_CRM.equals("")) && (!claim_name_internal.equals(claim_name_CRM))) 
            claim_name = claim_name_CRM + " (" + claim_name_internal + ")";
        else claim_name = claim_name_internal; 
    }
}

