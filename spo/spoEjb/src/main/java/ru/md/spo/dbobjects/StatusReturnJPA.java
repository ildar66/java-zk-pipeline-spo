package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "crm_status_return")
public class StatusReturnJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "fb_spo_return_id")
    private String id;
    private String status_type;
    private String status_return;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getStatus_type() {
        return status_type;
    }
    public void setStatus_type(String status_type) {
        this.status_type = status_type;
    }
    public String getStatus_return() {
        return status_return;
    }
    public void setStatus_return(String status_return) {
        this.status_return = status_return;
    }
    
    
}
