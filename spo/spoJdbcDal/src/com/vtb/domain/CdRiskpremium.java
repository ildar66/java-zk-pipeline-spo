package com.vtb.domain;

public class CdRiskpremium extends VtbObject {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String description;
    private String value;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
