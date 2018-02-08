package org.uit.director.managers;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.uit.director.db.ejb.DBFlexWorkflow;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.db.ejb.DBFlexWorkflowHome;
import org.uit.director.db.ejb.DBFlexWorkflowLocal;
import org.uit.director.db.ejb.DBFlexWorkflowLocalHome;

import ru.md.spo.util.Config;

/**
 * Created by IntelliJ IDEA. User: PD190384 Date: 15.12.2004 Time: 11:30:56 To
 * change this template use File | Settings | File Templates.
 */
public class DBMgr implements Serializable {

	private DBFlexWorkflowCommon dbFlexWorkflow;

	public DBMgr() {
		//do tothing
	}

	public DBFlexWorkflowCommon getDbFlexDirector() {

		if (dbFlexWorkflow == null) {
			InitialContext ctx;
			try {
				ctx = new InitialContext();
				Object objBean = ctx.lookup("java:comp/env/ejb/DBFlexWorkflowEJBLocal");
				DBFlexWorkflowLocalHome dbFlexWorkflowLocalHome = (DBFlexWorkflowLocalHome) objBean;
				dbFlexWorkflow = dbFlexWorkflowLocalHome.create();
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}
		return dbFlexWorkflow;
	}

	public void closeAllDb() {
		try {
			((DBFlexWorkflowLocal)dbFlexWorkflow).remove();
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (RemoveException e) {
			e.printStackTrace();
		}
	}
}
