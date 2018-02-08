package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cd_credit_turnover_premium")
public class CdCreditTurnoverPremiumJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "id")
    private Long id;
    private String description;
    private String sublimit_type;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSublimit_type() {
        return sublimit_type;
    }
    public void setSublimit_type(String sublimit_type) {
        this.sublimit_type = sublimit_type;
    }

    
    
}
