package com.vtb.exception;

public class NoSuchIndustryException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchIndustryException() {
		super();
	}

	public NoSuchIndustryException(String arg0) {
		super(arg0);
	}

	public NoSuchIndustryException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
