package com.vtb.exception;

import java.io.Serializable;

public class VtbException extends WrappedException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
	 * Constructor for VtbException
	 */
	public VtbException() {
		super();
	}

	/**
	 * Constructor for VtbException
	 */
	public VtbException(String msg) {
		super(msg);
	}

	/**
	 * Constructor VtbException.
	 * @param e ошибка
     * @param desc описание ошибки
	 */
	public VtbException(Exception e, String desc) {
		super(e, desc);
	}


}

