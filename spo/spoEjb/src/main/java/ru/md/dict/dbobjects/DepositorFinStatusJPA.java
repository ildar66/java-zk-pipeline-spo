package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "depositor_fin_status")
public class DepositorFinStatusJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String status;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
