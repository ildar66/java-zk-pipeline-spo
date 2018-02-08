package com.vtb.exception;

public class NoSuchOkvedException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchOkvedException() {
		super();
	}

	public NoSuchOkvedException(String arg0) {
		super(arg0);
	}

	public NoSuchOkvedException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
