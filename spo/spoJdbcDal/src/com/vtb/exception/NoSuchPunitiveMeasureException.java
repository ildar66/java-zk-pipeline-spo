package com.vtb.exception;

public class NoSuchPunitiveMeasureException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchPunitiveMeasureException() {
		super();
	}

	public NoSuchPunitiveMeasureException(String arg0) {
		super(arg0);
	}

	public NoSuchPunitiveMeasureException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
