package com.vtb.domain;

public class GeneralCondition extends VtbObject {
    private static final long serialVersionUID = 1L;
    private String quality_category;
    private String quality_category_desc;
    private String loan_class;
    private String discharge;
    
    public String getQuality_category() {
        return quality_category;
    }

    public void setQuality_category(String quality_category) {
        this.quality_category = quality_category;
    }

    public String getQuality_category_desc() {
        return quality_category_desc;
    }

    public void setQuality_category_desc(String quality_category_desc) {
        this.quality_category_desc = quality_category_desc;
    }

    public String getLoan_class() {
        return loan_class;
    }

    public void setLoan_class(String loan_class) {
        this.loan_class = loan_class;
    }

    public String getDischarge() {
        return discharge;
    }

    public void setDischarge(String discharge) {
        this.discharge = discharge;
    }
}
