package com.vtb.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MappingException extends VtbException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for MappingException
     */
    public MappingException() {
        super();
    }

    /**
     * Constructor for MappingException
     */
    public MappingException(String msg) {
        super(msg);
    }

    /**
     * Constructor MappingException.
     * @param arg0
     */
    public MappingException(Exception e, String desc) {
        super(e, desc);
    }

}
