package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'NewDocumentsByClaims' 
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByClaimsReportClaim  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long claimId;
	
	private List<NewDocumentsByClaimsReportOperation> operations;
	
	public NewDocumentsByClaimsReportClaim() {
        super();        
    }

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public List<NewDocumentsByClaimsReportOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<NewDocumentsByClaimsReportOperation> operations) {
		this.operations = operations;
	}
}

