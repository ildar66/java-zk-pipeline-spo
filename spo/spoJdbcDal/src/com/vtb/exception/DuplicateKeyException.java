package com.vtb.exception;
public class DuplicateKeyException extends MappingException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * Constructor for DuplicateKeyException
	 */
	public DuplicateKeyException() {
		super();
	}

	/**
	 * Constructor for DuplicateKeyException
	 */
	public DuplicateKeyException(String arg0) {
		super(arg0);
	}
	
	public DuplicateKeyException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}

