package com.vtb.domain;


/**
 * Single string of department for report 'NewDocumentsByClaims' 
 * @author Michael Kuznetsov 
 */
public class NewDocumentsByClaimsReportOperation  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private Long claimId;
	private String filename;
	private String filetype;
	private String department;
	private String dateOfAddition;
	private String status;
	
	public NewDocumentsByClaimsReportOperation() {
        super();        
    }

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDateOfAddition() {
		return dateOfAddition;
	}

	public void setDateOfAddition(String dateOfAddition) {
		this.dateOfAddition = dateOfAddition;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

