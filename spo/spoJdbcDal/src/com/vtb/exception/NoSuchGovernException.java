package com.vtb.exception;

public class NoSuchGovernException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchGovernException() {
		super();
	}

	public NoSuchGovernException(String arg0) {
		super(arg0);
	}

	public NoSuchGovernException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
