package com.vtb.exception;

public class NoSuchReportException extends NoSuchObjectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchReportException() {
		super();
	}

	public NoSuchReportException(String arg0) {
		super(arg0);
	}

	public NoSuchReportException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
