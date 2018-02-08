package ru.md.pup.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * Переменная ПУП. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="variables")
public class VariableJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_var")
    private Long id;

    @Column(name="name_var")
    private String name;

    @ManyToOne
    @JoinColumn(name="ID_TYPE_PROCESS")
    private ProcessTypeJPA processType;

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

	public ProcessTypeJPA getProcessType() {
		return processType;
	}

	public void setProcessType(ProcessTypeJPA processType) {
		this.processType = processType;
	}

}
