package com.vtb.mapping.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.vtb.util.EJBClientHelper;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

/**
 * This is the abstract superclass of all JPA Mappers.
 * @author IShafigullin, Michael Kuznetsov 
 * transfeffed by Michael Kuznetsov
 *
 */
public abstract class DomainJPAMapper {
	private static EntityManager entityMgr = null;
	private static final Logger LOGGER = Logger.getLogger(DomainJPAMapper.class.getName());

	/**
	 * Return EntityManager from InitialContext.
	 * @return EntityManager
	 */
	protected static EntityManager getEntityMgr() {
		if(entityMgr == null){
			try {
				entityMgr = (EntityManager) EJBClientHelper.getInitialContext().lookup("java:comp/env/flexWorkflowJPA");
			} catch (NamingException e) {
				LOGGER.log(Level.SEVERE, "Exception " + e + " in createEntityMgr()", e);
			}			
		}
		return entityMgr;
	}
}
