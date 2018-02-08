package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Цель Финансирования
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "CD_PIPELINE_FUNDING_COMPANY")
public class PipelineFundingCompanyJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String name;
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
}
