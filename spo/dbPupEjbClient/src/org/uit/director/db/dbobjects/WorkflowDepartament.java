package org.uit.director.db.dbobjects;

public class WorkflowDepartament extends WorkflowObject{

	public WorkflowDepartament(Long id, String shortName, String fullName) {
		super(id, shortName);
		setFullName(fullName);
		
		
	}

	
	private static final long serialVersionUID = 1L;
	String fullName;
	
	@Override
	public Object getData(String field) {
		return null;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Long getIdDepartament() {
		return id;
	}

	public void setIdDepartament(Long idDepartament) {
		setId(idDepartament);
	}

	public String getShortName() {
		return getName();
	}

	public void setShortName(String shortName) {
		setName(shortName);
	}
	
	
}
