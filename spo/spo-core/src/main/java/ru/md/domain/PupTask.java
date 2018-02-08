package ru.md.domain;

/**
 * Задача из таблицы tasks.
 * @author Andrey Pavlenko
 */
public class PupTask {
	private Long idTask;
	private Long idStageTo;
	private Long idUser;
	private Long idDepartment;
	private Long idStatus;
	private Long idTypeProcess;

	public Long getIdTask() {
		return idTask;
	}

	public void setIdTask(Long idTask) {
		this.idTask = idTask;
	}

	public Long getIdStageTo() {
		return idStageTo;
	}

	public void setIdStageTo(Long idStageTo) {
		this.idStageTo = idStageTo;
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

	public Long getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Long idStatus) {
		this.idStatus = idStatus;
	}

	public Long getIdTypeProcess() {
		return idTypeProcess;
	}

	public void setIdTypeProcess(Long idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}
}
