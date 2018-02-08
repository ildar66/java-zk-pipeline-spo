package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Вид обеспечения.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "CR_OB_TYPE")
public class SupplyTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String name;
    private String deleted;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDeleted() {
        return deleted;
    }
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    
}
