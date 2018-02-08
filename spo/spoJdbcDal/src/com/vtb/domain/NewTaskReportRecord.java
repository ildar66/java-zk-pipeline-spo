package com.vtb.domain;

public class NewTaskReportRecord  extends VtbObject{
	private static final long serialVersionUID = 1L;
    private String claim_name;
	private String claim_name_internal;
	private String claim_name_CRM;

	private String descriptionProcess;
	private String operationDescription;
	
	public NewTaskReportRecord() {
        super();        
    }

   public String getClaim_name() {
        return claim_name;
    }

    public void setClaim_name(String claim_name) {
        this.claim_name = claim_name;
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

	public String getDescriptionProcess() {
		return descriptionProcess;
	}
    
	public void setDescriptionProcess(String descriptionProcess) {
		this.descriptionProcess = descriptionProcess;
	}	
	
	public String getOperationDescription() {
		return operationDescription;
	}
    
	public void setOperationDescription(String operationDescription) {
		this.operationDescription = operationDescription;
	}
	
    public void generateClaimName() {
        if ((claim_name_CRM != null) && (!claim_name_CRM.equals("")) && (!claim_name_internal.equals(claim_name_CRM))) 
            claim_name = claim_name_CRM + " (" + claim_name_internal + ")";
        else claim_name = claim_name_internal; 
    }
}
