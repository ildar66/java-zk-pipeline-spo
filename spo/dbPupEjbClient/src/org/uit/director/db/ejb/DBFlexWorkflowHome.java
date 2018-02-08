package org.uit.director.db.ejb;

/**
 * Home interface for Enterprise Bean: DBFlexWorkflow
 */
public interface DBFlexWorkflowHome extends javax.ejb.EJBHome {

	/**
	 * Creates a default instance of Session Bean: DBFlexWorkflow
	 */
	public org.uit.director.db.ejb.DBFlexWorkflow create()
		throws javax.ejb.CreateException,
		java.rmi.RemoteException;
}
