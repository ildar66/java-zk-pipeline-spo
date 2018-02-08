package com.vtb.exception;

public class NoSuchDocumentsTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchDocumentsTypeException() {
		super();
	}

	public NoSuchDocumentsTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchDocumentsTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}
}
