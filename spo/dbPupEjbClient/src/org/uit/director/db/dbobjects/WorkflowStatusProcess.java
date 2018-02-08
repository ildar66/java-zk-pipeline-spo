package org.uit.director.db.dbobjects;


/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 27.12.2005
 * Time: 9:07:14
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowStatusProcess extends WorkflowObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkflowStatusProcess(long id, String name) {
        super(id, name);
    }

    public long getIdStatus() {
        return id;
    }

    public String getNameStatus() {
        return name;
    }

    @Override
	public Object getData(String field) {
        if (field.equals(Cnst.TStatProc.id)) {
			return id;
		}
        if (field.equals(Cnst.TStatProc.name)) {
			return name;
		}
        return null;
    }
}
