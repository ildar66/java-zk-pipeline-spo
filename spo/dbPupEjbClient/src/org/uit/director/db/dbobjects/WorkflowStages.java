package org.uit.director.db.dbobjects;

public class WorkflowStages extends WorkflowObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int limitDay;

	int typeLimitDay;

	int attentionDay;

	String classOnEntry;

	String classOnExit;

	int idTypeProcess;

	boolean isActive;

	public WorkflowStages(long idStage, String nameStage, String orderView,
			String orederExpand, String orderEdit, int limitDay,
			int typeLimitDay, int attentionDay, String classOnEntry,
			String classOnExit, int idTypeProcess, boolean isActive) {

		super(idStage, nameStage);		
		this.limitDay = limitDay;
		this.typeLimitDay = typeLimitDay;
		this.attentionDay = attentionDay;
		this.classOnEntry = classOnEntry;
		this.classOnExit = classOnExit;
		this.idTypeProcess = idTypeProcess;
		this.isActive = isActive;
	}

	

	public int getLimitDay() {
		return limitDay;
	}

	public void setLimitDay(int limitDay) {
		this.limitDay = limitDay;
	}

	public int getTypeLimitDay() {
		return typeLimitDay;
	}

	public void setTypeLimitDay(int typeLimitDay) {
		this.typeLimitDay = typeLimitDay;
	}

	public int getAttentionDay() {
		return attentionDay;
	}

	public void setAttentionDay(int attentionDay) {
		this.attentionDay = attentionDay;
	}

	public String getClassOnEntry() {
		return classOnEntry;
	}

	public void setClassOnEntry(String classOnEntry) {
		this.classOnEntry = classOnEntry;
	}

	public String getClassOnExit() {
		return classOnExit;
	}

	public void setClassOnExit(String classOnExit) {
		this.classOnExit = classOnExit;
	}

	public long getIdStage() {
		return id;
	}

	public String getNameStage() {
		return name;
	}

	/**
	 * @return Returns the idTypeProcess.
	 */
	public int getIdTypeProcess() {
		return idTypeProcess;
	}

	/**
	 * @param idTypeProcess
	 *            The idTypeProcess to set.
	 */
	public void setIdTypeProcess(int idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	@Override
	public Object getData(String field) {

		if (field.equals(Cnst.TStages.id)) {
			return id;
		}
		if (field.equals(Cnst.TStages.attentionDay)) {
			return attentionDay;
		}
		if (field.equals(Cnst.TStages.classEntry)) {
			return classOnEntry;
		}
		if (field.equals(Cnst.TStages.classExit)) {
			return classOnExit;
		}
		if (field.equals(Cnst.TStages.limitDay)) {
			return limitDay;
		}
		if (field.equals(Cnst.TStages.name)) {
			return name;
		}		
		if (field.equals(Cnst.TStages.typeLimitDay)) {
			return typeLimitDay;
		}
		if (field.equals(Cnst.TStages.idTypeProcess)) {
			return idTypeProcess;
		}
		return null;

	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
