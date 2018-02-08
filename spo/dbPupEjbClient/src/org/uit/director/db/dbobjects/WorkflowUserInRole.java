package org.uit.director.db.dbobjects;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 27.12.2005
 * Time: 9:34:27
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowUserInRole extends WorkflowObject {
	private static final long serialVersionUID = 1L;
	private Long idRole;
	private Long idUser;

    public WorkflowUserInRole(long id, Long idUser) {
    	super(id, "");
    	idRole = id;
    	this.idUser = idUser; 
    }

    public long getIdRole() {
        return idRole;
    }

    public Long getIdUser() {
        return idUser;
    }

    @Override
	public Object getData(String field) {
        if (field.equals(Cnst.TUserInRole.id)) {
			return id;
		}
        if (field.equals(Cnst.TUserInRole.name)) {
			return name;
		}

        return null;
    }
}
