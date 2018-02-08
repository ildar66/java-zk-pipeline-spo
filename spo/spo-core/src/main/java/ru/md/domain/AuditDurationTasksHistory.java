package ru.md.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * данные для отчета Аудит прохождения заявки.
 * Подробно по задачам.
 * @author Andrey Pavlenko
 */
public class AuditDurationTasksHistory {
	private Date st;
	private Date en;
	private Long idTask;
	private Long idStageTo;

	public Date getSt() {
		return st;
	}

	public void setSt(Date st) {
		this.st = st;
	}

	public Date getEn() {
		return en;
	}

	public void setEn(Date en) {
		this.en = en;
	}

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

	@Override
	public String toString() {
		return "AuditDurationTasksHistory{" +
				"idTask=" + idTask +
				", st=" + st +
				", en=" + en +
				'}';
	}

	public AuditDurationTasksHistory(AuditDurationTasksHistory a) {
		this.st = a.getSt();
		this.en = a.getEn();
		this.idTask = a.getIdTask();
		this.idStageTo = a.getIdStageTo();
	}

	public AuditDurationTasksHistory() {
	}

	public AuditDurationTasksHistory(Date st, Date en, Long idTask, Long idStageTo) {
		this.st = st;
		this.en = en;
		this.idTask = idTask;
		this.idStageTo = idStageTo;
	}
}
