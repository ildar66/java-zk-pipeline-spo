package org.uit.director.db.dbobjects;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 27.12.2005
 * Time: 9:34:27
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowStagesInRole extends WorkflowObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long idStage;
	
    public WorkflowStagesInRole(long id, long idStage) {
        super(id, "");
        this.idStage = idStage;
    }

    public long getIdRole() {
        return id;
    }

    public long getIdStage() {
        return idStage;
    }

    @Override
	public Object getData(String field) {
        if (field.equals(Cnst.TStagesInRole.idRole)) {
			return id;
		}
        if (field.equals(Cnst.TStagesInRole.idStage)) {
			return idStage;
		}

        return null;
    }
}
