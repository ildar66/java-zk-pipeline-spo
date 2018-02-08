package ru.md.crm.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FbSpoOpportunityProductNewPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name="FB_SPO_OPPORTUNITYID")
    private String id;//id очереди
    private String PRODUCTID;
    private Long fb_trancheid;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPRODUCTID() {
        return PRODUCTID;
    }
    public void setPRODUCTID(String pRODUCTID) {
        PRODUCTID = pRODUCTID;
    }
    public Long getFB_TRANCHID() {
        return fb_trancheid;
    }
    public void setFB_TRANCHID(Long fB_TRANCHID) {
        fb_trancheid = fB_TRANCHID;
    }

}
