package com.vtb.exception;

/**
 * @author kbrown
 *
 * This is a trivial example of a class that wraps other exception classes.
 * All of our project exception classes extend this class and allow you to retrieve
 * the root exception of any exception instance by sending getRootException();
 */
public class WrappedException extends Exception {

    private static final long serialVersionUID = 1L;
    private Exception nestedException;

	/**
	 * Constructor for WrappedException.
	 */
	public WrappedException() {
		super();
	}

	/**
	 * Constructor for WrappedException.
	 * @param s
	 */
	public WrappedException(String s) {
		super(s);
	}

	/**
	 * Constructor WrappedException.
	 * @param arg0
	 */
	public WrappedException(Exception nestedException, String desc) {
		super(desc, nestedException);
		this.nestedException = nestedException;
	}


	/**
	 * Returns the nestedException.
	 * @return Exception
	 */
	public Exception getNestedException() {
		return nestedException;
	}

	/**
	 * Sets the nestedException.
	 * @param nestedException The nestedException to set
	 */
	public void setNestedException(Exception nestedException) {
		this.nestedException = nestedException;
	}

	public Exception getRootException() {
		if ((nestedException != null)
			&& (nestedException instanceof WrappedException))
			return ((WrappedException) nestedException).getRootException();
		else
			return nestedException;

	}

}
