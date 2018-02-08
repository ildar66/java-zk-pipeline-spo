package com.vtb.util;

import java.util.Hashtable;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
/**
 * Utilty class providing caching services for JNDI InitialContext and 
 * EJB Home objects
 */
public class EJBClientHelper {

	private static InitialContext context = null;
	private static final Hashtable<String, EJBHome> homesCache = new Hashtable<String, EJBHome>();
	private static final Hashtable<String, EJBLocalHome> homesLocalCache = new Hashtable<String, EJBLocalHome>();
	private static final String REFERENCE_NAME_PREFIX = "java:comp/env/ejb/";

	/**
	 * Access to a cached reference to local JNDI base Naming context
	 * @returns valid InitialContext.
	 */
	public static InitialContext getInitialContext() throws NamingException {
		if (context == null) {
			context = new InitialContext();
		}

		return context;
	}


	/**
	 * Locate EJB home (from Home Cache).  Uses local EJB References to 
	 * locate EJB Home.  The returned EJBHome object has been narrowed.
	 * @param refName the local EJB Reference.  This String will be prepended with 
	 *       "java:comp/env/ejb/" to form the full EJB Reference.
	 * @param homeClass the EJB Home class, used to narrow the home object returned 
	 *        by JNDI
	 * @returns The EJBHome
	 */
	public static EJBHome getHome(String refName, Class homeClass) throws NamingException {
		EJBHome home = null;
		home = homesCache.get(refName);
		if (home == null) {
			String ejbReference = REFERENCE_NAME_PREFIX + refName;
			home = (EJBHome) PortableRemoteObject.narrow(getInitialContext().lookup(ejbReference), homeClass);
            homesCache.put(refName, home);
		}
		return home;
	}

	/**
	 * Locate EJB home (from Home Cache).  Uses local EJB References to 
	 * locate EJB Home.  The returned EJBHome object has been narrowed.
	 * @param refName the local EJB Reference.  This String will be prepended with 
	 *       "java:comp/env/ejb/" to form the full EJB Reference.
	 * @param homeClass the EJB Home class, used to narrow the home object returned 
	 *        by JNDI
	 * @returns The EJBHome
	 */
	public static EJBLocalHome getLocalHome(String refName) throws NamingException {
		EJBLocalHome home = null;
		home = homesLocalCache.get(refName);
		if (home == null) {
			String ejbReference = REFERENCE_NAME_PREFIX + refName;
			home = (EJBLocalHome) getInitialContext().lookup(ejbReference);
            homesLocalCache.put(refName, home);
		}
		return home;
	}

}