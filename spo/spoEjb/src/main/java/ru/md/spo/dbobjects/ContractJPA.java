package ru.md.spo.dbobjects;

import java.io.Serializable;

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
 * Договор. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "contract")
public class ContractJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ContractSequenceGenerator", sequenceName = "contract_seq", allocationSize = 1)
    @GeneratedValue(generator = "ContractSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    private String contract;

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
     * @return contract
     */
    public String getContract() {
        return contract;
    }

    /**
     * @param contract contract
     */
    public void setContract(String contract) {
        this.contract = contract;
    }

    public ContractJPA(TaskJPA task, String contract) {
        super();
        this.task = task;
        this.contract = contract;
    }

    public ContractJPA() {
        super();
    }
    
    
}
