package com.vtb.exception;

public class ModelException extends VtbException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * Constructor for ModelException
	 */
	public ModelException() {
		super();
	}

	/**
	 * Constructor for ModelException
	 */
	public ModelException(String arg0) {
		super(arg0);
	}
	
	public ModelException(Exception arg0, String desc) {
		super(arg0,desc);
	}

}

