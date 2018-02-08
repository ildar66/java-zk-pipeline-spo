package com.vtb.domain;

/**
 * VtbObject "Роль члена проектной команды"
 * 
 * @author MKuznetsov
 * 
 */
public class ProjectTeamMember extends VtbObject {

	private static final long serialVersionUID = 1L;
	
	private String fio; // ФИО
	private String department; // подразделение
	private String role;  // роль

	private Long idUser;     // id  пользователя
	private Long idDepartment;   // и id его подразделения
	
    public ProjectTeamMember() {
       super();
    }

	public ProjectTeamMember(String fio, String department, String role) {
		super();
		this.fio = fio;
		this.department = department;
		this.role = role;
	}

	public ProjectTeamMember(String fio, String department, String role, Long idUser, Long idDepartment) {
		super();
		this.fio = fio;
		this.department = department;
		this.role = role;
		this.idUser = idUser;
		this.idDepartment = idDepartment;
	}
	
	public String getFio() {
		return fio;
	}
	
	public void setFio(String fio) {
		this.fio = fio;
	}
	
	public String getDepartment() {
		return department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getIdDepartment() {
		return idDepartment;
	}

	public void setIdDepartment(Long idDepartment) {
		this.idDepartment = idDepartment;
	}
}
