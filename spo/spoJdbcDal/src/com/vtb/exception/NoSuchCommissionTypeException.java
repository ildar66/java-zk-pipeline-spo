package com.vtb.exception;

public class NoSuchCommissionTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchCommissionTypeException() {
		super();
	}

	public NoSuchCommissionTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchCommissionTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
