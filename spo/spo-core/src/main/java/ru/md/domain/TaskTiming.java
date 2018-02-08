package ru.md.domain;

import java.util.Date;

/**
 * Created by Andrey Pavlenko on 23.08.2016.
 */
public class TaskTiming {
    private Date createDate;
    private Date refuseDate;
    private Date acceptDate;
    private Date fixDate;
    private Date tranche;
    private Date struct;
    private Date exper;

    /**
     * Returns .
     * @return
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets .
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Returns .
     * @return
     */
    public Date getRefuseDate() {
        return refuseDate;
    }

    /**
     * Sets .
     * @param refuseDate
     */
    public void setRefuseDate(Date refuseDate) {
        this.refuseDate = refuseDate;
    }

    /**
     * Returns .
     * @return
     */
    public Date getAcceptDate() {
        return acceptDate;
    }

    /**
     * Sets .
     * @param acceptDate
     */
    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

    /**
     * Returns .
     * @return
     */
    public Date getFixDate() {
        return fixDate;
    }

    /**
     * Sets .
     * @param fixDate
     */
    public void setFixDate(Date fixDate) {
        this.fixDate = fixDate;
    }

    /**
     * Returns .
     * @return
     */
    public Date getTranche() {
        return tranche;
    }

    /**
     * Sets .
     * @param tranche
     */
    public void setTranche(Date tranche) {
        this.tranche = tranche;
    }

    /**
     * Returns .
     * @return
     */
    public Date getStruct() {
        return struct;
    }

    /**
     * Sets .
     * @param struct
     */
    public void setStruct(Date struct) {
        this.struct = struct;
    }

    /**
     * Returns .
     * @return
     */
    public Date getExper() {
        return exper;
    }

    /**
     * Sets .
     * @param exper
     */
    public void setExper(Date exper) {
        this.exper = exper;
    }
}
