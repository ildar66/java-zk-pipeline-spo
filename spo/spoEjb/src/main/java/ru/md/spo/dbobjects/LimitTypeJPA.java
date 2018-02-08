package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Вид лимита.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "LIMIT_TYPE")
public class LimitTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @Column(name = "ID_LIMIT_TYPE")
    private Long id;
    private String name_limit_type;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name_limit_type;
    }
    public void setName(String name) {
        this.name_limit_type = name;
    }
}
