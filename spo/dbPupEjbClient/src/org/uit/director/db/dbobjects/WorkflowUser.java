package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class WorkflowUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long idUser;
	String family;
	String name;
	String patronymic;
	WorkflowDepartament departament;
	String permission;
	String login;
	String mailUser;
	String ipUser;
	boolean isActive;
	
	
	public WorkflowUser() {
		super();
		 idUser = new Long(0);
		 family = "";
		 name = "";
		 patronymic = "";
		 departament = new WorkflowDepartament(new Long(0), "", "");
		 permission = "";
		 login = "";
		 mailUser = "";
		 ipUser = "";
		 isActive = false;
	}
	
	public WorkflowUser(Long idUser, String family, String name, String patronymic, WorkflowDepartament departament, String permission, String login, String mailUser, String ipUser, boolean isActive) {
		super();
		this.idUser = idUser;
		this.family = family;
		this.name = name;
		this.patronymic = patronymic;
		this.departament = departament;
		this.permission = permission;
		this.login = login.toLowerCase();
		this.mailUser = mailUser;
		this.ipUser = ipUser;
		this.isActive = isActive;
	}
	public WorkflowDepartament getDepartament() {
		return departament;
	}
	public void setDepartament(WorkflowDepartament departament) {
		this.departament = departament;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public Long getIdUser() {
		return idUser;
	}
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPatronymic() {
		return patronymic;
	}
	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}
	
	public String getFIO() {
		return getFamily() + " " + getName() + " " + getPatronymic();
	}
	
	public String getFullNameUser () {
		return getFIO() + " (" + getLogin() + ")";
		
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getIpUser() {
		return ipUser;
	}

	public void setIpUser(String ipUser) {
		this.ipUser = ipUser;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getLogin() {
		return login.toLowerCase();
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}
}
