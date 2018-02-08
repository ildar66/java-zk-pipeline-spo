package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'AttributeTree' 
 * @author Michael Kuznetsov 
 */
public class CRMClaimsReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private CRMClaimsReportHeader headers;
	private List<CRMClaimsReportOperation> operations;
	
	public CRMClaimsReport() {
        super();        
    }
 
	public CRMClaimsReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(CRMClaimsReportHeader headers) {
		this.headers = headers;
	}

	public void setOperations(List<CRMClaimsReportOperation> operations) {
		this.operations = operations;
	}
	
	public List<CRMClaimsReportOperation> getOperations() {
		return operations;
	}
}
