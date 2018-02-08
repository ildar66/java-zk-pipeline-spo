package com.vtb.exception;

public class NoSuchRegionException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchRegionException() {
		super();
	}

	public NoSuchRegionException(String arg0) {
		super(arg0);
	}

	public NoSuchRegionException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
