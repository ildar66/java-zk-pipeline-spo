package ru.masterdm.spo.dashboard.domain;

import java.util.Date;

/**
 * Created by drone on 22.07.16.
 */
public class MdTask {

    private Long number;
    private Long version;
    private String org;
    private String stage;
    private String currency;
    private Double sum;
    private Date planDate;

    public MdTask(Long number, Long version, String org, String stage, String currency, Double sum) {
        this.number = number;
        this.version = version;
        this.org = org;
        this.stage = stage;
        this.currency = currency;
        this.sum = sum;
    }

    public MdTask(Long number, Long version, String org, String stage, String currency, Double sum, Date planDate) {
        this.number = number;
        this.version = version;
        this.org = org;
        this.stage = stage;
        this.currency = currency;
        this.sum = sum;
        this.planDate = planDate;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }
}
