package com.vtb.exception;

public class NoSuchAuthorizedBodyException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchAuthorizedBodyException() {
		super();
	}

	public NoSuchAuthorizedBodyException(String arg0) {
		super(arg0);
	}

	public NoSuchAuthorizedBodyException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
