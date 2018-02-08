package com.vtb.domain;

import java.util.Date;

/**Покрытие прямых расходов и Покрытие общебанковских расходов*/
public class StavDefrayalExes {
    public enum StavDefrayalExesType{
        DIRECT,COMMONBANK
    }
    private Date activedate;
    private Double stavvalue;
    private String bcategory;
    private String stavtype;
    public Date getActivedate() {
        return activedate;
    }
    public void setActivedate(Date activedate) {
        this.activedate = activedate;
    }
    public Double getStavvalue() {
        return stavvalue*100;
    }
    public void setStavvalue(Double stavvalue) {
        this.stavvalue = stavvalue;
    }
    public String getBcategory() {
        return bcategory;
    }
    public void setBcategory(String bcategory) {
        this.bcategory = bcategory;
    }
    public String getStavtype() {
        return stavtype;
    }
    public void setStavtype(String stavtype) {
        this.stavtype = stavtype;
    }
    
}
