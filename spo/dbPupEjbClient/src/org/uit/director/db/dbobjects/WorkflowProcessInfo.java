/*
 * Created on 25.09.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.util.List;


public class WorkflowProcessInfo extends WorkflowObject{
	
	private static final long serialVersionUID = 1L;	

	// Дата инициализации процесса
	protected String dateInitProcess;

	// Дата завершения процесса
	protected String dateCompleteProcess;

	// Статус процесса
	protected StatusProcess statusProcess;

	// Тип процесса
	protected Integer idTypeProcess;

	// Срок выполнения процесса
	protected int countExecute;

	// Число активных этапов
	protected int countActiveStages;

	// Активные этапы
	protected List<Long> activeStages;
	
	protected Long idParentProcess;	
	
    public WorkflowProcessInfo(Long id) {
        super(id, "");      
    }
	
	public Long getIdParentProcess() {
		return idParentProcess;
	}

	public void setIdParentProcess(Long idParentProcess) {
		this.idParentProcess = idParentProcess;
	}

	public List<Long> getActiveStages() {
		return activeStages;
	}

	public int getCountActiveStages() {
		return countActiveStages;
	}

	public int getCountExecute() {
		return countExecute;
	}

	public String getDateCompleteProcess() {
		return dateCompleteProcess;
	}

	public String getDateInitProcess() {
		return dateInitProcess;
	}

	public Long getIdProcess() {
		return getId();
	}

	public Integer getIdTypeProcess() {
		return idTypeProcess;
	}

	public StatusProcess getStatusProcess() {
		return statusProcess;
	}

	public void setActiveStages(List<Long> activeStages) {
		this.activeStages = activeStages;
	}

	public void setCountActiveStages(int countActiveStages) {
		this.countActiveStages = countActiveStages;
	}

	public void setCountExecute(int countExecute) {
		this.countExecute = countExecute;
	}

	public void setDateCompleteProcess(String dateCompleteProcess) {
		this.dateCompleteProcess = dateCompleteProcess;
	}

	public void setDateInitProcess(String dateInitProcess) {
		this.dateInitProcess = dateInitProcess;
	}

	public void setIdProcess(Long idProcess) {
		setId(idProcess);
	}

	public void setIdTypeProcess(Integer idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	public void setStatusProcess(StatusProcess statusProcess) {
		this.statusProcess = statusProcess;
	}

	@Override
	public Object getData(String field) {
		return null;
	}
	
	

}
