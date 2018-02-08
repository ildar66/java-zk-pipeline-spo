package com.vtb.exception;

public class NoSuchSpoSettingException extends NoSuchObjectException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoSuchSpoSettingException() {
	}

	public NoSuchSpoSettingException(String arg0) {
		super(arg0);
	}

	public NoSuchSpoSettingException(Exception arg0, String desc) {
		super(arg0, desc);
	}

}
