/**
 * 
 */
package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Andrey Pavlenko
 * Таблица, в которую мы отдаем данные по заявке после того, как сделка завершится.
 */
@Entity
@Table(name="FB_SPO_OPPORTUNITY_PRODUCT_NEW",schema="sysdba")
public class FbSpoOpportunityProductNewJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private FbSpoOpportunityProductNewPK pk = new FbSpoOpportunityProductNewPK();
    private String UNIT;
    private BigDecimal QUANTITY;
    private BigDecimal QUANTITYVYDACHI;
    private BigDecimal QUANTITY_ZAD; 
    private String LV;
    private String LZ;
    private Date ACTIVEBEGIN;
    private Date ACTIVEEND;
    private Integer DAYS;
    private Double STAVRAZWRK;
    private Integer PERIODISP;
    private String POGAS;
    private Date DATEKK;
    private Double STAVFLOATFIXEDWRK;
    /**
     * @return uNIT
     */
    public String getUNIT() {
        return UNIT;
    }
    /**
     * @param unit uNIT
     */
    public void setUNIT(String unit) {
        UNIT = unit;
    }
    /**
     * @return qUANTITY
     */
    public BigDecimal getQUANTITY() {
        return QUANTITY;
    }
    /**
     * @param quantity qUANTITY
     */
    public void setQUANTITY(BigDecimal quantity) {
        QUANTITY = quantity;
    }
    /**
     * @return qUANTITYVYDACHI
     */
    public BigDecimal getQUANTITYVYDACHI() {
        return QUANTITYVYDACHI;
    }
    /**
     * @param quantityvydachi qUANTITYVYDACHI
     */
    public void setQUANTITYVYDACHI(BigDecimal quantityvydachi) {
        QUANTITYVYDACHI = quantityvydachi;
    }
    /**
     * @return qUANTITY_ZAD
     */
    public BigDecimal getQUANTITY_ZAD() {
        return QUANTITY_ZAD;
    }
    /**
     * @param quantity_zad qUANTITY_ZAD
     */
    public void setQUANTITY_ZAD(BigDecimal quantity_zad) {
        QUANTITY_ZAD = quantity_zad;
    }
    /**
     * @return lV
     */
    public String getLV() {
        return LV;
    }
    /**
     * @param lv lV
     */
    public void setLV(String lv) {
        LV = lv;
    }
    /**
     * @return lZ
     */
    public String getLZ() {
        return LZ;
    }
    /**
     * @param lz lZ
     */
    public void setLZ(String lz) {
        LZ = lz;
    }
    /**
     * @return aCTIVEBEGIN
     */
    public Date getACTIVEBEGIN() {
        return ACTIVEBEGIN;
    }
    /**
     * @param activebegin aCTIVEBEGIN
     */
    public void setACTIVEBEGIN(Date activebegin) {
        ACTIVEBEGIN = activebegin;
    }
    /**
     * @return aCTIVEEND
     */
    public Date getACTIVEEND() {
        return ACTIVEEND;
    }
    /**
     * @param activeend aCTIVEEND
     */
    public void setACTIVEEND(Date activeend) {
        ACTIVEEND = activeend;
    }
    /**
     * @return dAYS
     */
    public Integer getDAYS() {
        return DAYS;
    }
    /**
     * @param days dAYS
     */
    public void setDAYS(Integer days) {
        DAYS = days;
    }
    /**
     * @return sTAVRAZWRK
     */
    public Double getSTAVRAZWRK() {
        return STAVRAZWRK;
    }
    /**
     * @param stavrazwrk sTAVRAZWRK
     */
    public void setSTAVRAZWRK(Double stavrazwrk) {
        STAVRAZWRK = stavrazwrk;
    }
    /**
     * @return pERIODISP
     */
    public Integer getPERIODISP() {
        return PERIODISP;
    }
    /**
     * @param periodisp pERIODISP
     */
    public void setPERIODISP(Integer periodisp) {
        PERIODISP = periodisp;
    }
    /**
     * @return pOGAS
     */
    public String getPOGAS() {
        return POGAS;
    }
    /**
     * @param pogas pOGAS
     */
    public void setPOGAS(String pogas) {
        POGAS = pogas;
    }
    /**
     * @return dATEKK
     */
    public Date getDATEKK() {
        return DATEKK;
    }
    /**
     * @param datekk dATEKK
     */
    public void setDATEKK(Date datekk) {
        DATEKK = datekk;
    }
    /**
     * @return sTAVFLOATFIXEDWRK
     */
    public Double getSTAVFLOATFIXEDWRK() {
        return STAVFLOATFIXEDWRK;
    }
    /**
     * @param stavfloatfixedwrk sTAVFLOATFIXEDWRK
     */
    public void setSTAVFLOATFIXEDWRK(Double stavfloatfixedwrk) {
        STAVFLOATFIXEDWRK = stavfloatfixedwrk;
    }
    public FbSpoOpportunityProductNewPK getPk() {
        return pk;
    }
    public void setPk(FbSpoOpportunityProductNewPK pk) {
        this.pk = pk;
    }
    
}
