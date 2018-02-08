package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="V_SPO_fb_network_wager",schema="sysdba")
public class NetworkWagerJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    //V_SPO_FB_TRANCHE.BASE_PREM ;//Премия за кредитный риск
    //Расчетная фиксированная ставка. Поля с таким названием нет ни в одной таблице/представлении в ТЗ на интеграцию CRM и СПО

    @Id
    private String fb_network_wagerid;
    private String oppproductid;
    private String WRKLIBORSROK;//Индикативная ставка
    private BigDecimal STAVFLOATFIXEDWRK;//ставка Float Fixed
    private BigDecimal STAVRAZWRK;//ставка размещения
    private BigDecimal STPLAVWRK;//Результрующая ставка
    private Date END_DATE;
    private Date START_DATE;
    private String PERIOD_NAME;//название периода
    private String VIDOBESP;//Обеспечение по периоду
    private BigDecimal EFF_STAVKA;//Эффективная ставка
    private BigDecimal FUND_RAS_STAV;//Расчетная ставка она же Ставка фондирования
    private BigDecimal FUND_RAS_STAV_PROTECTED;//Расчетная (защищенная) ставка
    private BigDecimal FUND_PAY_ECONOMIC_CAPITAL;//Плата за экономический капитал
    private BigDecimal FUND_KUTR ;//КУТР
    private BigDecimal FUND_C1;//Коэффициент С1
    private BigDecimal FUND_C2;//Коэффициент С2
    private BigDecimal POKRITIE;//Покрытие
    public String getID() {
        return fb_network_wagerid;
    }
    public void setId(String id) {
        fb_network_wagerid = id;
    }
    public String getOppproductid() {
        return oppproductid;
    }
    public void setOppproductid(String oppproductid) {
        this.oppproductid = oppproductid;
    }
    public String getWRKLIBORSROK() {
        return WRKLIBORSROK;
    }
    public void setWRKLIBORSROK(String wRKLIBORSROK) {
        WRKLIBORSROK = wRKLIBORSROK;
    }
    public BigDecimal getSTAVFLOATFIXEDWRK() {
        return STAVFLOATFIXEDWRK;
    }
    public void setSTAVFLOATFIXEDWRK(BigDecimal sTAVFLOATFIXEDWRK) {
        STAVFLOATFIXEDWRK = sTAVFLOATFIXEDWRK;
    }
    public BigDecimal getSTAVRAZWRK() {
        return STAVRAZWRK;
    }
    public void setSTAVRAZWRK(BigDecimal sTAVRAZWRK) {
        STAVRAZWRK = sTAVRAZWRK;
    }
    public BigDecimal getSTPLAVWRK() {
        return STPLAVWRK;
    }
    public void setSTPLAVWRK(BigDecimal sTPLAVWRK) {
        STPLAVWRK = sTPLAVWRK;
    }
    public Date getEND_DATE() {
        return END_DATE;
    }
    public void setEND_DATE(Date eND_DATE) {
        END_DATE = eND_DATE;
    }
    public Date getSTART_DATE() {
        return START_DATE;
    }
    public void setSTART_DATE(Date sTART_DATE) {
        START_DATE = sTART_DATE;
    }
    public String getPERIOD_NAME() {
        return PERIOD_NAME;
    }
    public void setPERIOD_NAME(String pERIOD_NAME) {
        PERIOD_NAME = pERIOD_NAME;
    }
    public String getVIDOBESP() {
        return VIDOBESP;
    }
    public void setVIDOBESP(String vIDOBESP) {
        VIDOBESP = vIDOBESP;
    }
    public BigDecimal getEFF_STAVKA() {
        return EFF_STAVKA;
    }
    public void setEFF_STAVKA(BigDecimal eFF_STAVKA) {
        EFF_STAVKA = eFF_STAVKA;
    }
    public BigDecimal getFUND_RAS_STAV() {
        return FUND_RAS_STAV;
    }
    public void setFUND_RAS_STAV(BigDecimal fUND_RAS_STAV) {
        FUND_RAS_STAV = fUND_RAS_STAV;
    }
    public BigDecimal getFUND_RAS_STAV_PROTECTED() {
        return FUND_RAS_STAV_PROTECTED;
    }
    public void setFUND_RAS_STAV_PROTECTED(BigDecimal fUND_RAS_STAV_PROTECTED) {
        FUND_RAS_STAV_PROTECTED = fUND_RAS_STAV_PROTECTED;
    }
    public BigDecimal getFUND_PAY_ECONOMIC_CAPITAL() {
        return FUND_PAY_ECONOMIC_CAPITAL;
    }
    public void setFUND_PAY_ECONOMIC_CAPITAL(BigDecimal fUND_PAY_ECONOMIC_CAPITAL) {
        FUND_PAY_ECONOMIC_CAPITAL = fUND_PAY_ECONOMIC_CAPITAL;
    }
    public BigDecimal getFUND_KUTR() {
        return FUND_KUTR;
    }
    public void setFUND_KUTR(BigDecimal fUND_KUTR) {
        FUND_KUTR = fUND_KUTR;
    }
    public BigDecimal getFUND_C1() {
        return FUND_C1;
    }
    public void setFUND_C1(BigDecimal fUND_C1) {
        FUND_C1 = fUND_C1;
    }
    public BigDecimal getFUND_C2() {
        return FUND_C2;
    }
    public void setFUND_C2(BigDecimal fUND_C2) {
        FUND_C2 = fUND_C2;
    }
    public BigDecimal getPOKRITIE() {
        return POKRITIE;
    }
    public void setPOKRITIE(BigDecimal pOKRITIE) {
        POKRITIE = pOKRITIE;
    }
}
