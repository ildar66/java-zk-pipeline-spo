package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CRMRating implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountid;
    private String count_rating_val;
    private BigDecimal count_rating;
    private Date count_rating_date;
    private Date count_rating_quarter;
    private String expert_rating_val;
    private BigDecimal expert_rating;
    private Date expert_rating_date;
    private String cc_rating_val;
    private Long cc_rating;
    private Date cc_rating_date;
    private String opportunityid;
    /**
     * @return accountid
     */
    public String getAccountid() {
        return accountid;
    }
    /**
     * @param accountid accountid
     */
    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }
    /**
     * @return count_rating_val
     */
    public String getCount_rating_val() {
        return count_rating_val;
    }
    /**
     * @param count_rating_val count_rating_val
     */
    public void setCount_rating_val(String count_rating_val) {
        this.count_rating_val = count_rating_val;
    }
    /**
     * @return count_rating
     */
    public BigDecimal getCount_rating() {
        return count_rating;
    }
    /**
     * @param count_rating count_rating
     */
    public void setCount_rating(BigDecimal count_rating) {
        this.count_rating = count_rating;
    }
    /**
     * @return count_rating_date
     */
    public Date getCount_rating_date() {
        return count_rating_date;
    }
    /**
     * @param count_rating_date count_rating_date
     */
    public void setCount_rating_date(Date count_rating_date) {
        this.count_rating_date = count_rating_date;
    }
    /**
     * @return count_rating_quarter
     */
    public Date getCount_rating_quarter() {
        return count_rating_quarter;
    }
    /**
     * @param count_rating_quarter count_rating_quarter
     */
    public void setCount_rating_quarter(Date count_rating_quarter) {
        this.count_rating_quarter = count_rating_quarter;
    }
    /**
     * @return expert_rating_val
     */
    public String getExpert_rating_val() {
        return expert_rating_val;
    }
    /**
     * @param expert_rating_val expert_rating_val
     */
    public void setExpert_rating_val(String expert_rating_val) {
        this.expert_rating_val = expert_rating_val;
    }
    /**
     * @return expert_rating
     */
    public BigDecimal getExpert_rating() {
        return expert_rating;
    }
    /**
     * @param expert_rating expert_rating
     */
    public void setExpert_rating(BigDecimal expert_rating) {
        this.expert_rating = expert_rating;
    }
    /**
     * @return expert_rating_date
     */
    public Date getExpert_rating_date() {
        return expert_rating_date;
    }
    /**
     * @param expert_rating_date expert_rating_date
     */
    public void setExpert_rating_date(Date expert_rating_date) {
        this.expert_rating_date = expert_rating_date;
    }
    /**
     * @return cc_rating_val
     */
    public String getCc_rating_val() {
        return cc_rating_val;
    }
    /**
     * @param cc_rating_val cc_rating_val
     */
    public void setCc_rating_val(String cc_rating_val) {
        this.cc_rating_val = cc_rating_val;
    }
    /**
     * @return cc_rating
     */
    public Long getCc_rating() {
        return cc_rating;
    }
    /**
     * @param cc_rating cc_rating
     */
    public void setCc_rating(Long cc_rating) {
        this.cc_rating = cc_rating;
    }
    /**
     * @return cc_rating_date
     */
    public Date getCc_rating_date() {
        return cc_rating_date;
    }
    /**
     * @param cc_rating_date cc_rating_date
     */
    public void setCc_rating_date(Date cc_rating_date) {
        this.cc_rating_date = cc_rating_date;
    }
    /**
     * @return opportunityid
     */
    public String getOpportunityid() {
        return opportunityid;
    }
    /**
     * @param opportunityid opportunityid
     */
    public void setOpportunityid(String opportunityid) {
        this.opportunityid = opportunityid;
    }
    
}
