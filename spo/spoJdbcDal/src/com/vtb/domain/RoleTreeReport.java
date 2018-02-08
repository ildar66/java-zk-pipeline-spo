package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'RolesOfUsers' 
 * @author Michael Kuznetsov 
 */
public class RoleTreeReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private RoleTreeReportHeader headers;
	private List<RoleTreeReportDepartment> departments;
	
	public RoleTreeReport() {
        super();        
    }
 
	public RoleTreeReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(RoleTreeReportHeader headers) {
		this.headers = headers;
	}

    public List<RoleTreeReportDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<RoleTreeReportDepartment> departments) {
        this.departments = departments;
    }
}
