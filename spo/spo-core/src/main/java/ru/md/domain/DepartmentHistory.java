package ru.md.domain;

import java.util.Date;

/**
 * История инициирующего подразделения сделки
 * @author Sergey Lysenkov
 */
public class DepartmentHistory {
	private Long idmdtask;
	private Long idOldDepartment;
	private String oldDepartmentName;
	private Long idNewDepartment;
	private String newDepartmentName;
	private Long idPerformer;
	private String performerLogin;
	private String performerName;
	private Date changeDate;
	private String version;
	public Long getIdmdtask() {
		return idmdtask;
	}
	public void setIdmdtask(Long idmdtask) {
		this.idmdtask = idmdtask;
	}
	public Long getIdOldDepartment() {
		return idOldDepartment;
	}
	public void setIdOldDepartment(Long idOldDepartment) {
		this.idOldDepartment = idOldDepartment;
	}
	public String getOldDepartmentName() {
		if (oldDepartmentName == null || oldDepartmentName.isEmpty())
			return "не определено";
		return oldDepartmentName;
	}
	public void setOldDepartmentName(String oldDepartmentName) {
		this.oldDepartmentName = oldDepartmentName;
	}
	public Long getIdNewDepartment() {
		return idNewDepartment;
	}
	public void setIdNewDepartment(Long idNewDepartment) {
		this.idNewDepartment = idNewDepartment;
	}
	public String getNewDepartmentName() {
		return newDepartmentName;
	}
	public void setNewDepartmentName(String newDepartmentName) {
		this.newDepartmentName = newDepartmentName;
	}
	public Long getIdPerformer() {
		return idPerformer;
	}
	public void setIdPerformer(Long idPerformer) {
		this.idPerformer = idPerformer;
	}
	public String getPerformerLogin() {
		return performerLogin;
	}
	public void setPerformerLogin(String performerLogin) {
		this.performerLogin = performerLogin;
	}
	public String getPerformerName() {
		return performerName;
	}
	public void setPerformerName(String performerName) {
		this.performerName = performerName;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
