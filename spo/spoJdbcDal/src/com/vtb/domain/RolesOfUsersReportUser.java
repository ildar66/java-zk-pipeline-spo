package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'Active Stages' 
 * @author Michael Kuznetsov 
 */
public class RolesOfUsersReportUser  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String user_fio;
	private Long userId;
	private String user_login;
	
	// Let be there a copy 
	private String department_name;
	
	private List<String> roles;
	
	public RolesOfUsersReportUser() {
        super();        
    }

	public String getUser_fio() {
		return user_fio;
	}

	public void setUser_fio(String user_fio) {
		this.user_fio = user_fio;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getUser_login() {
		return user_login;
	}

	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}

}

