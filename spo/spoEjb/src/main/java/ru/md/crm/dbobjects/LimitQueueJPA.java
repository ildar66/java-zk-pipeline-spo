package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="FB_SPO_LIMIT_UPLOAD",schema="sysdba")
public class LimitQueueJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="LIMIT_UPLOADID")
    private String id;
    
    @Column(name="SPOSEND")
    private String send;
    
    @Column(name="SPOSENDDATE")
    private Date sendDate;
    
    @Column(name="SPOACCEPT")
    private String accept;
    
    @Column(name="SPOACCEPTDATE")
    private Date acceptDate;
    
    @Column(name="SPO_Result")
    private String result;
    
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
    /**
     * @return result
     */
    public String getResult() {
        return result==null?"":result;
    }
    /**
     * @param result result
     */
    public void setResult(String result) {
        this.result = result;
    }
    
}
