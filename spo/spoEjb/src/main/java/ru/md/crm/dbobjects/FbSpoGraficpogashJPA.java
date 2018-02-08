package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="FB_SPO_GRAFICPOGASH",schema="sysdba")
public class FbSpoGraficpogashJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="FB_SPO_OPPORTUNITYID")
    private String id;//Связь с таблицей FB_SPO_OPPORTUNITY

    private String OPPPRODUCTID;
    private Date STARTDATE;
    private Date FINISHDATE;
    private Double SUMMA;
    private String UNIT;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOPPPRODUCTID() {
        return OPPPRODUCTID;
    }
    public void setOPPPRODUCTID(String oPPPRODUCTID) {
        OPPPRODUCTID = oPPPRODUCTID;
    }
    public Date getSTARTDATE() {
        return STARTDATE;
    }
    public void setSTARTDATE(Date sTARTDATE) {
        STARTDATE = sTARTDATE;
    }
    public Date getFINISHDATE() {
        return FINISHDATE;
    }
    public void setFINISHDATE(Date fINISHDATE) {
        FINISHDATE = fINISHDATE;
    }
    public Double getSUMMA() {
        return SUMMA;
    }
    public void setSUMMA(Double sUMMA) {
        SUMMA = sUMMA;
    }
    public String getUNIT() {
        return UNIT;
    }
    public void setUNIT(String uNIT) {
        UNIT = uNIT;
    }
    
}
