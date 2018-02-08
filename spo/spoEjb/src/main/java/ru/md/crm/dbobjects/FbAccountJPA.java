package ru.md.crm.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="v_Spo_fb_Account",schema="sysdba")
public class FbAccountJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ACCOUNTID")
    private String id;
    private String category;
    private String corp_block;
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
     * @return category
     */
    public String getCategory() {
        return category;
    }
    /**
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    /**
     * @return corp_block
     */
    public String getCorp_block() {
        return corp_block;
    }
    /**
     * @param corp_block corp_block
     */
    public void setCorp_block(String corp_block) {
        this.corp_block = corp_block;
    }    
    
}
