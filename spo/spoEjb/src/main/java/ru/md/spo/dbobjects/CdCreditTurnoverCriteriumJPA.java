package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cd_credit_turnover_criterium")
public class CdCreditTurnoverCriteriumJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "id")
    private Long id;
    private String description;
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
}
