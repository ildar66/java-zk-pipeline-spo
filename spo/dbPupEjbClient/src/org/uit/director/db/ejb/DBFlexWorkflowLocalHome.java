package org.uit.director.db.ejb;

/**
 * Local Home interface for Enterprise Bean: DBFlexWorkflow
 */
public interface DBFlexWorkflowLocalHome extends javax.ejb.EJBLocalHome {

	/**
	 * Creates a default instance of Session Bean: DBFlexWorkflow
	 */
	public org.uit.director.db.ejb.DBFlexWorkflowLocal create()
		throws javax.ejb.CreateException;
}
