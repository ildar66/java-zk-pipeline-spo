package com.vtb.exception;

public class NoSuchManagerException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchManagerException() {
		super();
	}

	public NoSuchManagerException(String arg0) {
		super(arg0);
	}

	public NoSuchManagerException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
