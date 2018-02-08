package ru.md.crm.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Представление содержит список Продуктов.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name="v_spo_product",schema="sysdba")
public class SpoProductJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="PRODUCTID")
    private String id;

    private String NAME;//Название

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }
    
}
