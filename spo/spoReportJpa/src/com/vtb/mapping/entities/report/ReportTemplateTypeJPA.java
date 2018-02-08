package com.vtb.mapping.entities.report;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents report template type entity.
 * @author Michael Kuznetsov
 */
@Entity
@Table(name = "REPORT_TEMPLATE_TYPE")

public class ReportTemplateTypeJPA implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TEMPLATE_TYPE")
    private String id;

    @Column(name = "TEMPLATE_TYPE_NAME")
    private String name;

    /**
     * Returns template type identifier.
     * @return template type identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets template type identifier.
     * @param id template type identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns template type name.
     * @return template type name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets template type name.
     * @param name template type name
     */
    public void setName(String name) {
        this.name = name;
    }
}
