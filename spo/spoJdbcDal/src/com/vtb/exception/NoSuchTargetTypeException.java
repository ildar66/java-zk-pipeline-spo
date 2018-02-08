package com.vtb.exception;

public class NoSuchTargetTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchTargetTypeException() {
		super();
	}

	public NoSuchTargetTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchTargetTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
