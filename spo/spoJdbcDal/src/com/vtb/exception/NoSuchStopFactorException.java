package com.vtb.exception;

public class NoSuchStopFactorException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchStopFactorException() {
		super();
	}

	public NoSuchStopFactorException(String arg0) {
		super(arg0);

	}

	public NoSuchStopFactorException(Exception arg0, String desc) {
		super(arg0, desc);

	}

}
