package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'NewDocumentsByOrgs' 
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByOrgsReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private NewDocumentsByOrgsReportHeader headers;
	private List<NewDocumentsByOrgsReportOrg> organizations;
	
	public NewDocumentsByOrgsReport() {
        super();        
    }
 
	public NewDocumentsByOrgsReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(NewDocumentsByOrgsReportHeader headers) {
		this.headers = headers;
	}

	public void setOrganizations(List<NewDocumentsByOrgsReportOrg> organizations) {
		this.organizations = organizations;		
	}
	
	public List<NewDocumentsByOrgsReportOrg> getOrganizations() {
		return organizations;
	}
}
