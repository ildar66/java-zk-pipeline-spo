package com.vtb.domain;

import java.util.List;

/**
 * Single string of department for report 'RolesOfUsers' 
 * @author Michael Kuznetsov 
 */
public class RoleTreeReportDepartment  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String department_name;	
	private Long departmentId;
	private List<RoleTreeReportOperation> operations;
	
	public RoleTreeReportDepartment() {
        super();        
    }

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	
    public List<RoleTreeReportOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<RoleTreeReportOperation> operations) {
        this.operations = operations;
    }
}

