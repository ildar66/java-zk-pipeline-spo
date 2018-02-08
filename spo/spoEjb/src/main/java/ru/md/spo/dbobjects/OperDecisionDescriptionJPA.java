package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Порядок принятия решения о проведении операций. Описание.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "OPER_DECISION_DESCRIPTION")
public class OperDecisionDescriptionJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "OperDecisionDescriptionSequenceGenerator", sequenceName = "OPER_DECISION_DESCRIPTION_seq", allocationSize = 1)
    @GeneratedValue(generator = "OperDecisionDescriptionSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "DESCRIPTION")
    private String descr;//Решения о/об
    
    @ManyToOne @JoinColumn(name="ID_OPER_DECISION")
    private OperDecisionJPA decision;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	public OperDecisionDescriptionJPA(String descr, OperDecisionJPA decision) {
		super();
		this.descr = descr;
		this.decision = decision;
	}

	public OperDecisionDescriptionJPA() {
		super();
	}

}
