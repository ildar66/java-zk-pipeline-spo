package com.vtb.domain;

import java.util.Date;

public class TaskVersion extends VtbObject{
	private static final long serialVersionUID = 5395812271250335740L;
	private String role; 
	private String userName;
	private Long version;
	private Date date;
	private String stage;
	private String report;
	
	public TaskVersion(String role, String userName, Long version, Date date,
			String stage) {
		super();
		this.role = role;
		this.userName = userName;
		this.version = version;
		this.date = date;
		this.stage = stage;
	}
	/**
	 * @return Роль пользователя, создавшего версию
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role Роль пользователя, создавшего версию
	 */
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * @return ФИО пользователя, создавшего версию
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName ФИО пользователя, создавшего версию
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return № версии
	 */
	public Long getVersion() {
		return version;
	}
	/**
	 * @param version № версии
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	/**
	 * @return Дата создания версии
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date Дата создания версии
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return Наименование операции
	 */
	public String getStage() {
		return stage;
	}
	/**
	 * @param stage Наименование операции
	 */
	public void setStage(String stage) {
		this.stage = stage;
	}
	/**
	 * @return отчет
	 */
	public String getReport() {
		return report;
	}
	/**
	 * @param report отчет
	 */
	public void setReport(String report) {
		this.report = report;
	}
	
}
