package com.vtb.domain;


/**
 * Header data for a report 'Active Stages'
 * @author Michael Kuznetsov 
 */
public class ActiveStagesReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;
	
	private String param_process_type;
	private String param_user_name;
	private String param_claim_name;
	private String internal_claim_name;
	private String CRM_claim_name;
	private String param_delinquency_descr;
	private String param_department;
	private String param_corresponding_deps;

	public ActiveStagesReportHeader() {
        super();        
    }

	public String getParam_process_type() {
		return param_process_type;
	}

	public void setParam_process_type(String param_process_type) {
		this.param_process_type = param_process_type;
	}

	public String getParam_user_name() {
		return param_user_name;
	}

	public void setParam_user_name(String param_user_name) {
		this.param_user_name = param_user_name;
	}

	public String getParam_claim_name() {
		return param_claim_name;
	}

	public void setParam_claim_name(String param_claim_name) {
		this.param_claim_name = param_claim_name;
	}

	public String getParam_delinquency_descr() {
		return param_delinquency_descr;
	}

	public void setParam_delinquency_descr(String param_delinquency_descr) {
		this.param_delinquency_descr = param_delinquency_descr;
	}

	public String getParam_department() {
		return param_department;
	}

	public void setParam_department(String param_department) {
		this.param_department = param_department;
	}

	public String getParam_corresponding_deps() {
		return param_corresponding_deps;
	}

	public void setParam_corresponding_deps(String param_corresponding_deps) {
		this.param_corresponding_deps = param_corresponding_deps;
	}

    public String getInternal_claim_name() {
        return internal_claim_name;
    }

    public void setInternal_claim_name(String internal_claim_name) {
        this.internal_claim_name = internal_claim_name;
    }

    public String getCRM_claim_name() {
        return CRM_claim_name;
    }

    public void setCRM_claim_name(String crm_claim_name) {
        CRM_claim_name = crm_claim_name;
    }
    
    public void generateClaimName() {
        if ((CRM_claim_name != null) && (!CRM_claim_name.equals("")) && (!CRM_claim_name.equals(internal_claim_name))) 
            param_claim_name = CRM_claim_name + " (" + internal_claim_name + ")";
        else param_claim_name = internal_claim_name; 
    }
}

