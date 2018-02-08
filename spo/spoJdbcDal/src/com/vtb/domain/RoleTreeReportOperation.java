package com.vtb.domain;

import java.util.List;

/**
 * Single string of department for report 'RolesOfUsers' 
 * @author Michael Kuznetsov 
 */
public class RoleTreeReportOperation  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long roleId;
	private Long parentRole;
	private Long level;
	private String role_name;
	private Long typeProcessId;
	private String status;
	private String usage;
	private Long sortOrder; 
	private List<String> users;
	
	public RoleTreeReportOperation() {
        super();        
    }

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getParentRole() {
		return parentRole;
	}

	public void setParentRole(Long parentRole) {
		this.parentRole = parentRole;
	}

	public Long getTypeProcessId() {
		return typeProcessId;
	}

	public void setTypeProcessId(Long typeProcessId) {
		this.typeProcessId = typeProcessId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}
}

