package com.vtb.exception;

public class NoSuchAddressException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchAddressException() {
		super();
	}

	public NoSuchAddressException(String arg0) {
		super(arg0);
	}

	public NoSuchAddressException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
