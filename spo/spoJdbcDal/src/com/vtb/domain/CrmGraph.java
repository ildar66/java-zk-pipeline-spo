package com.vtb.domain;

import java.sql.Date;

/**
 * График погашения из CRM.
 * @author Andrey Pavlenko
 */
public class CrmGraph {
    private static final long serialVersionUID = 1L;
    private Date firstPayDate; 
    private Date finalPayDate; 
    private Double amount;
    private String unit;

    /**
     * @return the firstPayDate
     */
    public Date getFirstPayDate() {
        return firstPayDate;
    }

    /**
     * @param firstPayDate the firstPayDate to set
     */
    public void setFirstPayDate(Date firstPayDate) {
        this.firstPayDate = firstPayDate;
    }

    /**
     * @return the finalPayDate
     */
    public Date getFinalPayDate() {
        return finalPayDate;
    }

    /**
     * @param finalPayDate the finalPayDate to set
     */
    public void setFinalPayDate(Date finalPayDate) {
        this.finalPayDate = finalPayDate;
    }

    /**
     * @return the amount
     */
    public Double getAmount() {
        return amount;
    }
    /**
     * @param amount the amount to set
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

}
