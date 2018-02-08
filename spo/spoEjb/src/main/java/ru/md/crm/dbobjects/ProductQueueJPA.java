package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name="FB_SPO_OPPORTUNITY",schema="sysdba")
public class ProductQueueJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="FB_SPO_OPPORTUNITYID")
    private String id;
    
    @OneToMany(mappedBy="CRMID",fetch=FetchType.LAZY)
    private List<FbSpoLogStatusJPA> logs;//Лог ошибок импорта
    
    @Column(name="SPOSEND")
    private String send;//Признак отправки в СПО
    
    @Column(name="SPOSENDDATE")
    private Date sendDate;//Дата и время отправки в СПО
    
    @Column(name="SPOACCEPT")
    private String accept;//Признак получения системой СПО
    
    @Column(name="SPOACCEPTDATE")
    private Date acceptDate;//Дата и время получения в СПО
    
    @ManyToOne
    @JoinColumn(name="OPPORTUNITYID")
    private SpoFbOpportunityJPA opportunity;//код сделки
    
    @ManyToOne
    @JoinColumn(name="ACCOUNTID")
    private AccountJPA account;//организация
    
    @Column(name="CALLBACK")
    private String CALLBACK;//ссылка на справочник «статусов возврата заявки
    
    @Column(name="CALLBACKDATE")
    private Date CALLBACKDATE;//Дата и время возврата окончательного статуса заявки
    
    @Column(name="SPODELETE")
    private String SPODELETE;//Признак изменения категории клиента в CRM
    
    @Column(name="USERCODE")
    private String USERCODE;//Логин пользователя в CRM, осуществившего выгрузку заявки
    
    public String toString(){
        return id+" accept="+accept;
    }
    /**
     * @return id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return send
     */
    public String getSend() {
        return send;
    }
    /**
     * @param send send
     */
    public void setSend(String send) {
        this.send = send;
    }
    /**
     * @return sendDate
     */
    public Date getSendDate() {
        return sendDate;
    }
    /**
     * @param sendDate sendDate
     */
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
    /**
     * @return accept
     */
    public String getAccept() {
        return accept;
    }
    /**
     * @param accept accept
     */
    public void setAccept(String accept) {
        this.accept = accept;
    }
    /**
     * @return acceptDate
     */
    public Date getAcceptDate() {
        return acceptDate;
    }
    /**
     * @param acceptDate acceptDate
     */
    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

    public SpoFbOpportunityJPA getOpportunity() {
        return opportunity;
    }
    public void setOpportunity(SpoFbOpportunityJPA opportunity) {
        this.opportunity = opportunity;
    }
    /**
     * @return account
     */
    public AccountJPA getAccount() {
        return account;
    }
    /**
     * @param account account
     */
    public void setAccount(AccountJPA account) {
        this.account = account;
    }
    /**
     * @return cALLBACK
     */
    public String getCALLBACK() {
        return CALLBACK;
    }
    /**
     * @param callback cALLBACK
     */
    public void setCALLBACK(String callback) {
        CALLBACK = callback;
    }
    /**
     * @return cALLBACKDATE
     */
    public Date getCALLBACKDATE() {
        return CALLBACKDATE;
    }
    /**
     * @param callbackdate cALLBACKDATE
     */
    public void setCALLBACKDATE(Date callbackdate) {
        CALLBACKDATE = callbackdate;
    }
    /**
     * @return sPODELETE
     */
    public String getSPODELETE() {
        return SPODELETE;
    }
    /**
     * @param spodelete sPODELETE
     */
    public void setSPODELETE(String spodelete) {
        SPODELETE = spodelete;
    }
    /**
     * @return uSERCODE
     */
    public String getUSERCODE() {
        return USERCODE;
    }
    /**
     * @param usercode uSERCODE
     */
    public void setUSERCODE(String usercode) {
        USERCODE = usercode;
    }
    /**
     * @return logs
     */
    public List<FbSpoLogStatusJPA> getLogs() {
        return logs;
    }
    /**
     * @param logs logs
     */
    public void setLogs(List<FbSpoLogStatusJPA> logs) {
        this.logs = logs;
    }

    
}