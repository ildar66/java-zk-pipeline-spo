package com.vtb.exception;

public class NoSuchOrganizationGroupException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchOrganizationGroupException() {
		super();
	}

	public NoSuchOrganizationGroupException(String arg0) {
		super(arg0);
	}

	public NoSuchOrganizationGroupException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
