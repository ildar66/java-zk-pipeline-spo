package com.vtb.domain;

/**
 * Header data for a report 'CRMClaims' ('orderRporr')
 * @author Michael Kuznetsov 
 */
public class CRMClaimsReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;
	
	private String sendLeftDate;
	private String sendRightDate;
	private String acceptLeftDate;
	private String acceptRightDate;
	private String departmentId;
	
	public CRMClaimsReportHeader() {
        super();        
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

	public String getAcceptLeftDate() {
		return acceptLeftDate;
	}

	public void setAcceptLeftDate(String acceptLeftDate) {
		this.acceptLeftDate = acceptLeftDate;
	}

	public String getAcceptRightDate() {
		return acceptRightDate;
	}

	public void setAcceptRightDate(String acceptRightDate) {
		this.acceptRightDate = acceptRightDate;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}

