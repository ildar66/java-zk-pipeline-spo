package com.vtb.domain;
/**
 * Тип статуса в очереди CRM.
 * @author Andrey Pavlenko
 *
 */
public enum SPOAcceptType {
    ACCEPT("1"),ERROR("2"),NOTACCEPT("0");
    SPOAcceptType(String id){
        this.id=id;
    }
    private String id;
    public String getId() {
        return id;
    }
}
