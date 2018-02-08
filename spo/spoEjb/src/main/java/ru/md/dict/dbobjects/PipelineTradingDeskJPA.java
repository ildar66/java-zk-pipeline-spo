package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "CD_PIPELINE_TRADING_DESK")
public class PipelineTradingDeskJPA implements Serializable {
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
