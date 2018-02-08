package com.vtb.domain;

/**
 * Header data for a report 'NewDocumentsByOrgs'
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByOrgsReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String department_name;
	private String sendLeftDate;
	private String sendRightDate;

	
	public NewDocumentsByOrgsReportHeader() {
        super();        
    }

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public String getSendLeftDate() {
		return sendLeftDate;
	}

	public void setSendLeftDate(String sendLeftDate) {
		this.sendLeftDate = sendLeftDate;
	}

	public String getSendRightDate() {
		return sendRightDate;
	}

	public void setSendRightDate(String sendRightDate) {
		this.sendRightDate = sendRightDate;
	}
}

