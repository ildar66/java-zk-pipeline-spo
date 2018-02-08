package com.vtb.exception;

public class NoSuchLimitTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchLimitTypeException() {
		super();
	}

	public NoSuchLimitTypeException(String arg0) {
		super(arg0);

	}

	public NoSuchLimitTypeException(Exception arg0, String desc) {
		super(arg0, desc);

	}

}
