package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'NewDocumentsByOrgs' 
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByOrgsReportOrg  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String organizationName;
	private List<NewDocumentsByOrgsReportOperation> operations;
	
	public NewDocumentsByOrgsReportOrg() {
        super();        
    }
	
	public List<NewDocumentsByOrgsReportOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<NewDocumentsByOrgsReportOperation> operations) {
		this.operations = operations;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
}

