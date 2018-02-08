package com.vtb.domain;

import java.sql.Timestamp;

/**
 * VtbObject "История прохождения заявки ".
 * В данную таблицу система СПО записывает информацию о прохождении заявки
 * 
 * @author IShafigullin
 * 
 */
public class SpoHistory extends VtbObject {

	private static final long serialVersionUID = 1L;

	private String id = null;// Идентификатор

	/**
	 * ФИО, должность, подразделение(Исполнитель в СПО)
	 */
	private String spoExpert = null;

	/**
	 * Стадия рассмотрения(Справочник стадий из СПО)
	 */
	private String spoOpportunityID = null;

	/**
	 * Статус рассмотрения экспертом (Справочник статусов из СПО)
	 */
	private String spoStep = null;// Статус рассмотрения экспертом
	private Timestamp stepchDate = null;// Дата изменения статуса
	
	private String spoStage = null; //Наименование операции

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoHistory)) {
			return false;
		}
		SpoHistory aSpoHistory = (SpoHistory) anObject;
		return aSpoHistory.getId().equals(getId());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoHistory: ");
		sb.append(getId() + "(StepchDate=" + getStepchDate() + ", SpoExpert=" + getSpoExpert() + ")");

		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	/**
	 * 
	 * @param id
	 * @param spoExpert
	 * @param spoOpportunityID
	 * @param spoStep
	 * @param stepchDate
	 */
	public SpoHistory(String id, String spoExpert, String spoOpportunityID, String spoStep, String spoStage, Timestamp stepchDate) {
		super();
		this.id = id;
		this.spoExpert = spoExpert;
		this.spoOpportunityID = spoOpportunityID;
		this.spoStep = spoStep;
		this.spoStage = spoStage;
		this.stepchDate = stepchDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpoExpert() {
		return spoExpert;
	}

	public void setSpoExpert(String spoExpert) {
		this.spoExpert = spoExpert;
	}

	public String getSpoOpportunityID() {
		return spoOpportunityID;
	}

	public void setSpoOpportunityID(String spoOpportunityID) {
		this.spoOpportunityID = spoOpportunityID;
	}

	public String getSpoStep() {
		return spoStep;
	}

	public void setSpoStep(String sposter) {
		this.spoStep = sposter;
	}

	public Timestamp getStepchDate() {
		return stepchDate;
	}

	public void setStepchDate(Timestamp stepchDate) {
		this.stepchDate = stepchDate;
	}

	public String getSpoStage() {
		return spoStage;
	}

	public void setSpoStage(String spoStage) {
		this.spoStage = spoStage;
	}

}
