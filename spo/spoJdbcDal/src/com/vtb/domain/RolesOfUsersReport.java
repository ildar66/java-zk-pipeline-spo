package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'RolesOfUsers' 
 * @author Michael Kuznetsov 
 */
public class RolesOfUsersReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private RolesOfUsersReportHeader headers;
	private List<RolesOfUsersReportDepartment> departments;

	
	public RolesOfUsersReport() {
        super();        
    }
 
	public RolesOfUsersReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(RolesOfUsersReportHeader headers) {
		this.headers = headers;
	}

	public void setDepartments(List<RolesOfUsersReportDepartment> list) {
		departments = list;
	}
	
	public List<RolesOfUsersReportDepartment> getDepartments() {
		return departments;
	}
}
