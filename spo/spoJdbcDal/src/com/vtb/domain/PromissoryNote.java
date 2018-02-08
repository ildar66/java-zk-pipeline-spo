package com.vtb.domain;

import java.util.Date;

/**
 * Вексель. 
 */
public class PromissoryNote extends VtbObject {
    private static final long serialVersionUID = 1L;

    private String org; // векселедержатель
    private Double val;       // номинал
    private String currency;  // валюта
    private Double perc;      // процентная оговорка
    private String place;     // место платежа
    private Date maxdate;     // срок платежа
   
    
    public PromissoryNote() {
    	super();
    }
    
    public PromissoryNote(String org, Double val, String currency, Double perc, String place, Date maxdate) {
		super();
		this.org = org;
		this.val = val;
		this.currency = currency;
		this.perc = perc;
		this.place = place;
		this.maxdate = maxdate;
	}
    
	public Double getVal() {
        return val;
    }
    public void setVal(Double val) {
        this.val = val;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public Double getPerc() {
        return perc;
    }
    public void setPerc(Double perc) {
        this.perc = perc;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public Date getMaxdate() {
        return maxdate;
    }
    public void setMaxdate(Date maxdate) {
        this.maxdate = maxdate;
    }
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
}
