package ru.md.crm.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="v_Spo_Account",schema="sysdba")
public class AccountJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ACCOUNTID")
    private String id;
    @Column(name="ACCOUNT")
    private String name;
    private String REGION;
    @ManyToOne @JoinColumn(name = "ACCOUNTID", insertable=false,updatable=false)
    private FbAccountJPA fbAccount;
    
    public String toString() {
        return name;
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
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return rEGION
     */
    public String getREGION() {
        return REGION;
    }

    /**
     * @param rEGION rEGION
     */
    public void setREGION(String rEGION) {
        REGION = rEGION;
    }

    /**
     * @return fbAccount
     */
    public FbAccountJPA getFbAccount() {
        return fbAccount;
    }
    
}
