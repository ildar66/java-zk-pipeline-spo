package com.vtb.exception;
public class NoSuchObjectException extends MappingException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * Constructor for NoSuchObjectException
	 */
	public NoSuchObjectException() {
		super();
	}

	/**
	 * Constructor for NoSuchObjectException
	 */
	public NoSuchObjectException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor NoSuchObjectException.
	 * @param arg0
	 * @param desc
	 */
	public NoSuchObjectException(Exception arg0, String desc) {
		super (arg0,desc);
	}


}

