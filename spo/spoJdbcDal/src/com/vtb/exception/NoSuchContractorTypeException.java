package com.vtb.exception;

public class NoSuchContractorTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchContractorTypeException() {
		super();
	}

	public NoSuchContractorTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchContractorTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
