package com.vtb.util;

import java.util.HashMap;

import javax.ejb.Local;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.vtb.exception.FactoryException;

/**
 * Ejb references locator class(Локатор сервисов).
 * 
 * @author IShafigullin
 */
public final class EjbLocator {

    private static final String WAS_LOCAL_EJB_PREFIX = "ejblocal:";

    private static EjbLocator locator;

    private HashMap<String, Object> referenceMap = new HashMap<String, Object>();
    private Context context;

    /**
     * Disable constructor for utility class.
     */
    private EjbLocator() {
    }
    /** очищает кеш ссылок на бины */
    public void clearReferences() {
        referenceMap.clear();
    }

    /**
     * Singleton getInstance method.
     * @return this
     */
    public static EjbLocator getInstance() {
        if (locator == null) {
            locator = new EjbLocator();
        }
        return locator;
    }

    /**
     * Looks up for ejb reference.
     * @param clazz ejb interface (local or remote)
     * @param <T> reference type
     * @return T ejb reference
     * @throws FactoryException 
     */
    @SuppressWarnings("unchecked")
    public <T> T getReference(Class<T> clazz) throws FactoryException {
    	/*
    	 * com.ibm.websphere.naming.CannotInstantiateObjectException: 
    	 * Exception occurred while the JNDI NamingManager was processing a javax.naming.Reference object. 
    	 * [Root exception is com.ibm.websphere.ejbcontainer.AmbiguousEJBReferenceException: 
    	 * The short-form default binding 'com.vtb.ejb.TaskActionProcessorFacadeLocal' is ambiguous because multiple beans implement the interface : 
    	 * [flexWorkflowEAR#spoProcessor-14.12-SNAPSHOT.jar#TaskActionProcessorFacade, 
    	 *  flexWorkflowEAR#spoProcessor-14.12-SNAPSHOT.jar#TaskActionProcessorFacadeBean]. 
    	 *  Provide an interface specific binding or use the long-form default binding on lookup.]
    	 */
        String jndiPrefix = "";
        if (clazz.getAnnotation(Local.class) != null) {
            jndiPrefix = WAS_LOCAL_EJB_PREFIX;
        }
        T reference = (T) referenceMap.get(clazz.getName());
        if (reference == null) {
            try {
                reference = (T) getContext().lookup(jndiPrefix + clazz.getName());
                if (reference == null) {
                    throw new FactoryException(
                        "Cannot find remote reference for '" + clazz.getName() + "'"
                    );
                }
                referenceMap.put(clazz.getName(), reference);
            } catch (NamingException e) {
                throw new FactoryException(e.getMessage());
            }
        }
        return reference;
    }

    /**
     * Initializes and holds initial context.
     * @return {@link Context} instance
     * @throws FactoryException 
     */
    private Context getContext() throws FactoryException {
        try {
            if (context == null) {
                context = new InitialContext();
            }
        } catch (NamingException e) {
            throw new FactoryException(e.getMessage());
        }
        return context;
    }
}
