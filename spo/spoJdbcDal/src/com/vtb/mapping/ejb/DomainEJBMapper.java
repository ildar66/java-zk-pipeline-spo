package com.vtb.mapping.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBLocalObject;
import javax.naming.InitialContext;

import com.vtb.domain.VtbObject;
import com.vtb.exception.MappingException;
import com.vtb.mapping.Mapper;
import com.vtb.system.AppService;
import com.vtb.system.TraceCapable;
/**
 * This is the abstract superclass of all DomainFactories.
 *
 * Creation date: (2/26/00 3:48:50 PM)
 * @author: Administrator
 */
public abstract class DomainEJBMapper<T> implements Mapper<T> {
	InitialContext initContext;
/**
 * DomainFactory constructor.
 */
public DomainEJBMapper() {
	super();
}
/**
 * Return the initialContext for his domain factory.
 * This is hardcoded for WebSphere Advanced Edition and the local admin server.
 * Really those should come from a properties file, but hardcoding makes the example easier to
 * understand for someone new to the technology.
 *
 * Creation date: (2/19/00 2:57:48 PM)
 */
protected void createInitialContext() {
	if (initContext == null) {
		try {
			initContext = new InitialContext();
		} catch (Exception e) { // Error getting the initial context
			AppService.log(TraceCapable.ERROR_LEVEL,"Exception " + e + " in createInitialContext()");
		}
	}
}
/**
 * Return a ArrayList of all Domain objects of the type created by this factory.
 * Creation date: (3/19/00 7:59:51 PM)
 * @return java.util.ArrayList
 */
public ArrayList findAll() throws MappingException {
	Iterator allEJBs = findAllEJBs().iterator();
	ArrayList list = new ArrayList();
	while (allEJBs.hasNext()) {
		Object next = allEJBs.next();
		Object mapped = map((EJBLocalObject) next);
		list.add(mapped);
	}
	return list;
}
/**
 * Return an Enumeration that returns all of the EJB's for this type
 * Creation date: (3/19/00 7:59:31 PM)
 * @return java.util.Enumeration
 */
protected Collection findAllEJBs() throws MappingException {
	throw new MappingException("FindAll not valid for this type");
}
/**
 * Return the Object that results from locating this domain object in the persistent store.
 * Creation date: (4/2/00 5:00:28 PM)
 * @return java.lang.Object
 * @param domainObjectWithKeyValues java.lang.Object
 */
public T findByPrimaryKey(T domainObjectWithKeyValues) throws MappingException {
	EJBLocalObject ejb = findEJBObjectMatching(domainObjectWithKeyValues);
	return map(ejb);
}
/**
 * Return the EJBOBject matching this domain object
 *
 * Creation date: (3/20/00 11:57:12 AM)
 * @return javax.ejb.EJBObject
 * @param domainObject java.lang.Object
 */
protected abstract EJBLocalObject findEJBObjectMatching(T domainObject) throws MappingException;
/**
 * Return the result of mapping this EJBObject into a domain object.
 *
 * Creation date: (3/19/00 7:38:07 PM)
 * @return java.lang.Object
 * @param input javax.ejb.EJBObject
 */
public abstract T map(EJBLocalObject input) throws MappingException;
/**
 * Remove the domain object from the persistent store.
 *
 * Creation date: (3/20/00 11:55:18 AM)
 * @param domainObject java.lang.Object
 */
public void remove(T domainObject) throws MappingException {
	EJBLocalObject ejb = findEJBObjectMatching(domainObject);
	try {
		ejb.remove();
	} catch (Exception e) {
		throw new MappingException(e, ("Wrapped Exception in DomainFactory.delete():" + e));
	}
}
}
