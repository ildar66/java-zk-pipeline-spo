package org.uit.director.db.dbobjects;


public class WorkflowAssignInfo extends WorkflowObject {
	
	//пользователь, которому назначено задание на иссполнение
	protected Long idUser;
	
	//роль, на которую назначено задание
	protected Long idRole;
	
	//идентификатор процесса
	protected Long idProcess;
	
	//дата назначения
	protected String dateAssignUser;
	
	//возможность переназначения задания
	protected boolean mayReassign;
	
	//пользователь, назначащий исполнителя
	protected Long idUserFrom;

	//уровень назначечния
	protected Integer levelAssign;

	public WorkflowAssignInfo(Long id) {
		super(id, "AssignTask");
	}

	private static final long serialVersionUID = 1L;

	public String getDateAssignUser() {
		return dateAssignUser;
	}


	public void setDateAssignUser(String dateAssignUser) {
		this.dateAssignUser = dateAssignUser;
	}


	public Long getIdProcess() {
		return idProcess;
	}


	public void setIdProcess(Long idProcess) {
		this.idProcess = idProcess;
	}


	public Long getIdRole() {
		return idRole;
	}


	public void setIdRole(Long idRole) {
		this.idRole = idRole;
	}


	public Long getIdUser() {
		return idUser;
	}


	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}


	public boolean isMayReassign() {
		return mayReassign;
	}


	public void setMayReassign(boolean mayReassign) {
		this.mayReassign = mayReassign;
	}


	public Long getIdUserFrom() {
		return idUserFrom;
	}


	public void setIdUserFrom(Long idUserFrom) {
		this.idUserFrom = idUserFrom;
	}

	public Integer getLevelAssign() {
		return levelAssign;
	}


	public void setLevelAssign(Integer levelAssign) {
		this.levelAssign = levelAssign;
	}


	@Override
	public Object getData(String field) {
		// TODO Auto-generated method stub
		return null;
	}

}
