package org.uit.director.db.dbobjects;



public class WorkflowRoles extends WorkflowObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int idTypeProcess;
	boolean isActive;
	boolean isAdmin;
	
	
    public WorkflowRoles(long id, String name, int idTypeProcess_, boolean isActive, boolean isAdmin) {
        super(id, name);
        idTypeProcess = idTypeProcess_;
        this.isActive = isActive;
        this.isAdmin = isAdmin; 
    }
    
    

    /**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}



	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}



	public long getIdRole() {
        return id;
    }

    public String getNameRole() {
        return name;
    }
    

	/**
	 * @return Returns the idTypeProcess.
	 */
	public int getIdTypeProcess() {
		return idTypeProcess;
	}
	/**
	 * @param idTypeProcess The idTypeProcess to set.
	 */
	public void setIdTypeProcess(int idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}
    @Override
	public Object getData(String field) {

        if (field.equals(Cnst.TRoles.id)) {
			return id;
		}
        if (field.equals(Cnst.TRoles.name)) {
			return name;
		}
        if (field.equals(Cnst.TRoles.idTypeProcess)) {
			return idTypeProcess;
		}
        return null;
    }



	public boolean isAdmin() {
		return isAdmin;
	}



	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
    
}
