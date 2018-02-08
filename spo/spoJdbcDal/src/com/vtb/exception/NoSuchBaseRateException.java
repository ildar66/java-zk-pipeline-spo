package com.vtb.exception;

public class NoSuchBaseRateException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchBaseRateException() {
		super();
	}

	public NoSuchBaseRateException(String arg0) {
		super(arg0);
	}

	public NoSuchBaseRateException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
