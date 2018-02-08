package org.uit.director.db.dbobjects;

import java.util.ArrayList;
import java.util.List;

public class WorkflowVariables extends WorkflowObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String description;

	VariablesType typeVar;

	String addition;

	boolean isId;

	boolean isMain;

	int idTypeProcess;

	boolean isActive;

	int orderVar;
	
	ExtDataSource extDataSource;
	
	/**
	 * Возможные принимаемые значения
	 */
	List<String> options;

	public WorkflowVariables(long id, String name, String description,
			VariablesType typeVar, String addition, boolean is_id, boolean isMain,
			int idTypeProcess, boolean isActive, int order_var, ExtDataSource dataSource,  
			List<String> options) {
		super(id, name);
		this.description = description;
		this.typeVar = typeVar;
		this.addition = addition;
		isId = is_id;
		this.isMain = isMain;
		this.idTypeProcess = idTypeProcess;
		this.isActive = isActive;
		orderVar = order_var;
		extDataSource = dataSource;
		this.options = options;

	}

	/**
	 * @param id
	 * @param name
	 */
	public WorkflowVariables(WorkflowVariables var) {
		super(var.getId(), var.getName());
		description = var.description;
		typeVar = var.typeVar;
		addition = var.addition;
		isId = var.isId();
		isMain = var.isMain;
		idTypeProcess = var.idTypeProcess;
		isActive = var.isActive;
		orderVar = var.orderVar;
		extDataSource = var.extDataSource;
		options = var.options;
	}

	/**
	 * @param id
	 * @param name
	 */
	public WorkflowVariables() {
		super((long) 0, "");
		options = new ArrayList<String>();
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

	public VariablesType getTypeVar() {
		return typeVar;
	}

	public void setTypeVar(VariablesType typeVar) {
		this.typeVar = typeVar;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}
	
	public ExtDataSource getDataSource() {
		return extDataSource;
	}
	
	public void setDataSource(ExtDataSource dataSource) {
		extDataSource = dataSource;
	}

	@Override
	public Object getData(String field) {

		if (field.equals(Cnst.TVar.id)) {
			return id;
		}
		if (field.equals(Cnst.TVar.name)) {
			return name;
		}
		if (field.equals(Cnst.TVar.description)) {
			return description;
		}
		if (field.equals(Cnst.TVar.typeVar)) {
			return typeVar;
		}
		if (field.equals(Cnst.TVar.addition)) {
			return addition;
		}
		if (field.equals(Cnst.TVar.isId)) {
			return new Boolean(isId);
		}
		if (field.equals(Cnst.TVar.isMain)) {
			return new Boolean(isMain);
		}
		if (field.equals(Cnst.TVar.orderVar)) {
			return orderVar;
		}
		if (field.equals(Cnst.TVar.id_ds)) {
			return extDataSource;
		}

		return null;
	}

	public Long getIdVariable() {
		return id;
	}

	public String getNameVariable() {
		return name;
	}

	/**
	 * @return Returns the idTypeProcess.
	 */
	public Integer getIdTypeProcess() {
		return idTypeProcess;
	}

	/**
	 * @param idTypeProcess
	 *            The idTypeProcess to set.
	 */
	public void setIdTypeProcess(int idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	/**
	 * @return Returns the isId.
	 */
	public boolean isId() {
		return isId;
	}

	/**
	 * @param isId
	 *            The isId to set.
	 */
	public void setId(boolean isId) {
		this.isId = isId;
	}

	/**
	 * @return Returns the isMain.
	 */
	public boolean isMain() {
		return isMain;
	}

	/**
	 * @param isMain
	 *            The isMain to set.
	 */
	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public int getOrderVar() {
		return orderVar;
	}

	public void setOrderVar(int orderVar) {
		this.orderVar = orderVar;
	}
		
	
	

}
