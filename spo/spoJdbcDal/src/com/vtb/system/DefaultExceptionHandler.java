package com.vtb.system;

/**
 * Default exception handler, simply outputs exception info to console\
 *
 */
public class DefaultExceptionHandler implements ExceptionHandler {
/**
 * DefaultExceptionHandler constructor comment.
 */
public DefaultExceptionHandler() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param e java.lang.Throwable
 */
public void handle(ExceptionEvent event) {

	// Events know how to output themselves
	System.out.println(event.info());
	
}
/**
 * This method was created in VisualAge.
 * @param e java.lang.Throwable
 */
public void handle(Throwable e) {

	e.printStackTrace(System.out);
	
}
}
