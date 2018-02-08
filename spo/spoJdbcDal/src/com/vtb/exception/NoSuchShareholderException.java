package com.vtb.exception;

public class NoSuchShareholderException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchShareholderException() {
		super();
	}

	public NoSuchShareholderException(String arg0) {
		super(arg0);
	}

	public NoSuchShareholderException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
