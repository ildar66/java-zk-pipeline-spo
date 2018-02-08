package com.vtb.exception;

public class NoSuchRoleException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchRoleException() {
	}

	public NoSuchRoleException(String arg0) {
		super(arg0);
	}

	public NoSuchRoleException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
