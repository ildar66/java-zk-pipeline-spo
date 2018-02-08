package com.vtb.exception;

public class NoSuchOwnershipFormTypeException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2726258253851517946L;

	public NoSuchOwnershipFormTypeException() {
		super();
	}

	public NoSuchOwnershipFormTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchOwnershipFormTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}
}
