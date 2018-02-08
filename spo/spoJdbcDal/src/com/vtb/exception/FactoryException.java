package com.vtb.exception;

/**
 * This Exception defines a Factory Exception (e.g. cannot CRUD an EJB)
 * Creation date: (2/20/00 10:00:30 PM)
 * @author: Administrator
 */
public class FactoryException extends WrappedException {
/**
     * 
     */
    private static final long serialVersionUID = 1L;
/**
 * FactoryException constructor comment.
 */
public FactoryException() {
	super();
}
/**
 * FactoryException constructor comment.
 * @param s java.lang.String
 */
public FactoryException(String s) {
	super(s);
}
}
