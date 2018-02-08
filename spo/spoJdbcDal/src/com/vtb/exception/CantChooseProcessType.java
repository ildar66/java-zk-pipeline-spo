package com.vtb.exception;

public class CantChooseProcessType extends VtbException {
    private static final long serialVersionUID = 1L;

    public CantChooseProcessType() {
        super();
    }

    public CantChooseProcessType(Exception e, String desc) {
        super(e, desc);
    }

    public CantChooseProcessType(String msg) {
        super(msg);
    }

}
