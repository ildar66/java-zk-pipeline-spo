package com.vtb.exception;

public class NoSuchOpportunityTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchOpportunityTypeException() {
	}

	public NoSuchOpportunityTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchOpportunityTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
