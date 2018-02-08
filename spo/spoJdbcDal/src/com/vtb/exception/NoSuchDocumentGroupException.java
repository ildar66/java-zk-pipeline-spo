package com.vtb.exception;

public class NoSuchDocumentGroupException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5795358409353775150L;

	public NoSuchDocumentGroupException() {
		super();
	}

	public NoSuchDocumentGroupException(String arg0) {
		super(arg0);
	}

	public NoSuchDocumentGroupException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
