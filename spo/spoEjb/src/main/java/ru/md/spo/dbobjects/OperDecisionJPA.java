package ru.md.spo.dbobjects;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Порядок принятия решения о проведении операций
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "R_MDTASK_OPER_DECISION")
public class OperDecisionJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "OperDecisionSequenceGenerator", sequenceName = "r_mdtask_oper_decision_seq", allocationSize = 1)
    @GeneratedValue(generator = "OperDecisionSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    @OneToMany(mappedBy = "decision", fetch = FetchType.LAZY)
    private List<OperDecisionDescriptionJPA> descriptions;//Решения о/об 
    
    private String accepted;//принимаются
    private String specials;//особенности
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskJPA getTask() {
        return task;
    }

    public void setTask(TaskJPA task) {
        this.task = task;
    }

	/**
	 * @return принимаются
	 */
	public String getAccepted() {
		return accepted==null?"":accepted;
	}

	/**
	 * @param accepted принимаются
	 */
	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

	/**
	 * @return особенности
	 */
	public String getSpecials() {
		return specials==null?"":specials;
	}

	/**
	 * @param specials особенности
	 */
	public void setSpecials(String specials) {
		this.specials = specials;
	}

	/**
	 * @return the descriptions
	 */
	public List<OperDecisionDescriptionJPA> getDescriptions() {
		return descriptions;
	}

	/**
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(List<OperDecisionDescriptionJPA> descriptions) {
		this.descriptions = descriptions;
	}

}
