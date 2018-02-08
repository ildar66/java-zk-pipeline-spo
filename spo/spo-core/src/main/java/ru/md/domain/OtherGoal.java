package ru.md.domain;

import java.io.Serializable;

/**
 * Цель кредитования (R_MDTASK_OTHERGOALS)
 * 
 * @author akirilchev@masterdm.ru
 */
public class OtherGoal implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idTarget;
	private String goal;
	private String crmTargetTypeId;

	/**
	 * Конструктор
	 */
	public OtherGoal() {
		super();
	}

	/**
	 * Конструктор
	 *
	 * @param idTarget id
	 * @param goal наименование 
	 * @param crmTargetTypeId id цели crm
	 */
	public OtherGoal(Long idTarget, String goal, String crmTargetTypeId) {
		super();
		this.idTarget = idTarget;
		this.goal = goal;
		this.crmTargetTypeId = crmTargetTypeId;
	}
	
	/**
	 * Возвращает первичный ключ
	 *
	 * @return первичный ключ
	 */
	public Long getIdTarget() {
		return idTarget;
	}

	/**
	 * Устанавливает первичный ключ
	 *
	 * @param idTarget первичный ключ
	 */
	public void setIdTarget(Long idTarget) {
		this.idTarget = idTarget;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getCrmTargetTypeId() {
		return crmTargetTypeId == null ? "" : crmTargetTypeId;
	}

	public void setCrmTargetTypeId(String crmTargetTypeId) {
		this.crmTargetTypeId = crmTargetTypeId;
	}


}