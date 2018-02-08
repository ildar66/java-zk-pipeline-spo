package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="FB_SPO_RATING_NEW",schema="sysdba")
public class FbSpoRatingNewJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ACCOUNTID;
    private String COUNT_RATING_VAL;
    private BigDecimal COUNT_RATING;
    private Date COUNT_RATING_DATE;
    private Date COUNT_RATING_QUARTER;
    private String EXPERT_RATING_VAL;
    private BigDecimal EXPERT_RATING;
    private Date EXPERT_RATING_DATE;
    private String СС_RATING_VAL;
    private Long CC_RATING;
    private Date CC_RATING_DATE;
    @Id
    @Column(name="opportunityid")
    private String opportunityid;
    /**
     * @return accountid
     */
    public String getAccountid() {
        return ACCOUNTID;
    }
    /**
     * @param accountid accountid
     */
    public void setAccountid(String accountid) {
        this.ACCOUNTID = accountid;
    }
    /**
     * @return count_rating_val
     */
    public String getCount_rating_val() {
        return COUNT_RATING_VAL;
    }
    /**
     * @param count_rating_val count_rating_val
     */
    public void setCount_rating_val(String count_rating_val) {
        this.COUNT_RATING_VAL = count_rating_val;
    }
    /**
     * @return count_rating
     */
    public BigDecimal getCount_rating() {
        return COUNT_RATING;
    }
    /**
     * @param count_rating count_rating
     */
    public void setCount_rating(BigDecimal count_rating) {
        this.COUNT_RATING = count_rating;
    }
    /**
     * @return count_rating_date
     */
    public Date getCount_rating_date() {
        return COUNT_RATING_DATE;
    }
    /**
     * @param count_rating_date count_rating_date
     */
    public void setCount_rating_date(Date count_rating_date) {
        this.COUNT_RATING_DATE = count_rating_date;
    }
    /**
     * @return count_rating_quarter
     */
    public Date getCount_rating_quarter() {
        return COUNT_RATING_QUARTER;
    }
    /**
     * @param count_rating_quarter count_rating_quarter
     */
    public void setCount_rating_quarter(Date count_rating_quarter) {
        this.COUNT_RATING_QUARTER = count_rating_quarter;
    }
    /**
     * @return expert_rating_val
     */
    public String getExpert_rating_val() {
        return EXPERT_RATING_VAL;
    }
    /**
     * @param expert_rating_val expert_rating_val
     */
    public void setExpert_rating_val(String expert_rating_val) {
        this.EXPERT_RATING_VAL = expert_rating_val;
    }
    /**
     * @return expert_rating
     */
    public BigDecimal getExpert_rating() {
        return EXPERT_RATING;
    }
    /**
     * @param expert_rating expert_rating
     */
    public void setExpert_rating(BigDecimal expert_rating) {
        this.EXPERT_RATING = expert_rating;
    }
    /**
     * @return expert_rating_date
     */
    public Date getExpert_rating_date() {
        return EXPERT_RATING_DATE;
    }
    /**
     * @param expert_rating_date expert_rating_date
     */
    public void setExpert_rating_date(Date expert_rating_date) {
        this.EXPERT_RATING_DATE = expert_rating_date;
    }

    /**
     * @return cc_rating_val
     */
    public String getCc_rating_val() {
        return СС_RATING_VAL;
    }
    /**
     * @param cc_rating_val cc_rating_val
     */
    public void setCc_rating_val(String cc_rating_val) {
        this.СС_RATING_VAL = cc_rating_val;
    }
    /**
     * @return cc_rating
     */
    public Long getCc_rating() {
        return CC_RATING;
    }
    /**
     * @param cc_rating cc_rating
     */
    public void setCc_rating(Long cc_rating) {
        this.CC_RATING = cc_rating;
    }
    /**
     * @return cc_rating_date
     */
    public Date getCc_rating_date() {
        return CC_RATING_DATE;
    }
    /**
     * @param cc_rating_date cc_rating_date
     */
    public void setCc_rating_date(Date cc_rating_date) {
        this.CC_RATING_DATE = cc_rating_date;
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
