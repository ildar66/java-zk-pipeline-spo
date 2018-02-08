package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'NewDocumentsByClaims' 
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByClaimsReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private NewDocumentsByClaimsReportHeader headers;
	private List<NewDocumentsByClaimsReportClaim> claims;
	
	public NewDocumentsByClaimsReport() {
        super();        
    }
 
	public NewDocumentsByClaimsReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(NewDocumentsByClaimsReportHeader headers) {
		this.headers = headers;
	}

	public void setClaims(List<NewDocumentsByClaimsReportClaim> claims) {
		this.claims = claims;		
	}
	
	public List<NewDocumentsByClaimsReportClaim> getClaims() {
		return claims;
	}
}
