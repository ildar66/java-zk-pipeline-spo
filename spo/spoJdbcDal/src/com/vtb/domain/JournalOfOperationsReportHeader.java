package com.vtb.domain;


/**
 * Header data for a report 'Active Stages'
 * @author Michael Kuznetsov 
 */
public class JournalOfOperationsReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private String mdtask;
	private String internal_claim_name;
    private String CRM_claim_name;
	private String delinquency_descr;
	private String description_status;

	
	public JournalOfOperationsReportHeader() {
        super();        
    }

	public String getMdtask() {
		return mdtask;
	}

	public void setMdtask(String mdtask) {
		this.mdtask = mdtask;
	}

	public String getDelinquency_descr() {
		return delinquency_descr;
	}

	public void setDelinquency_descr(String delinquency_descr) {
		this.delinquency_descr = delinquency_descr;
	}

	public String getDescription_status() {
		return description_status;
	}

	public void setDescription_status(String description_status) {
		this.description_status = description_status;
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
        if ((CRM_claim_name != null) && (!CRM_claim_name.equals("")) && (!internal_claim_name.equals(CRM_claim_name))) 
            mdtask = CRM_claim_name + " (" + internal_claim_name + ")";
        else mdtask = internal_claim_name; 
    }
}

