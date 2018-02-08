package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Источник формирования покрытия для осуществления платежа по аккредитиву.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "cd_acredetiv_source_payment")
public class CdAcredetivSourcePaymentJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "ID_SOURCE")
    private Long id;
    private String name_source;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNameSource() {
        return name_source;
    }
    public void setNameSource(String name_source) {
        this.name_source = name_source;
    }
    
}
