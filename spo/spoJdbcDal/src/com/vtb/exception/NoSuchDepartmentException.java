package com.vtb.exception;

public class NoSuchDepartmentException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchDepartmentException() {
		super();
	}

	public NoSuchDepartmentException(String arg0) {
		super(arg0);
	}

	public NoSuchDepartmentException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
