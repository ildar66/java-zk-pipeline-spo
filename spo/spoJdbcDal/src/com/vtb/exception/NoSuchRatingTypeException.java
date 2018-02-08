package com.vtb.exception;

public class NoSuchRatingTypeException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchRatingTypeException() {
		super();
	}

	public NoSuchRatingTypeException(String arg0) {
		super(arg0);
	}

	public NoSuchRatingTypeException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
