package ru.md.pup.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * Атрибут ПУП. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="attributes")
public class AttributeJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_attr")
    private Long id;

    @Column(name="value_var")
    private String value;

    @ManyToOne
    @JoinColumn(name="ID_PROCESS")
    private ProcessJPA process;
    
    @ManyToOne
    @JoinColumn(name="ID_VAR")
    private VariableJPA variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ProcessJPA getProcess() {
		return process;
	}

	public void setProcess(ProcessJPA process) {
		this.process = process;
	}

	public VariableJPA getVariable() {
		return variable;
	}

	public void setVariable(VariableJPA variable) {
		this.variable = variable;
	}

}
