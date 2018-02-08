package com.vtb.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.vtb.util.Formatter;

/**
 * Class representing Limit and Sublimit Tree (used to show in Opportunity and, maybe Limit data).
 * @author Michael Kuznetsov
 */
public class LimitTree extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id_task;
	private boolean marked = false;            // if element is referenced by sublimit, limit or opportunity (deal).
	private String name;
	private String referenceId;                // ID_MDTASK field
	private Long number;
	private String companiesGroup;
	private String organization;
	private String limitVid;
	private BigDecimal sum;
	private String currency;
	private Date validTo;
	private Integer period = null;
	private String crmstatus;
	private String period_validTo;      // computed field. 
	
	public LimitTree() {
        super();
    }
	
	public LimitTree(Long id_task) {
        super();
        this.id_task = id_task;
    }
	
    /** id заявки. Уникален. Отличается от номера. */
    public Long getId_task() {
        return id_task;
    }

    public void setId_task(Long id_task) {
        this.id_task = id_task;
    }
	
    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCompaniesGroup() {
        return companiesGroup;
    }

    public void setCompaniesGroup(String companiesGroup) {
        this.companiesGroup = companiesGroup;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLimitVid() {
        return limitVid;
    }

    public void setLimitVid(String limitVid) {
        this.limitVid = limitVid;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
        this.period_validTo = getPeriodValidTo(period, validTo);
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
        this.period_validTo = getPeriodValidTo(period, validTo);
    }

    public String getCrmstatus() {
        return crmstatus;
    }

    public void setCrmstatus(String crmstatus) {
        this.crmstatus = crmstatus;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getPeriod_validTo() {
        return period_validTo;
    }

    public void setPeriod_validTo(String period_validTo) {
        this.period_validTo = period_validTo;
    }

    /**
     * Generates standard period / validTo data
     */
    private String getPeriodValidTo(Integer period, Date validTo) {
        if ((period != null) && !(period.intValue() == 0)) return Formatter.toMoneyFormat(period) + " дн.";
        if (validTo != null) return Formatter.format(validTo);
        return "";
    }
}
