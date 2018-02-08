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
 * индивидуальные условия, которые выводим в основных параметрах. 
 * Есть еще индивидуальные условия в условиях. Они по смыслу тоже самое
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "indCondition")
public class IndConditionJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "IndConditionSequenceGenerator", sequenceName = "indcondition_seq", allocationSize = 1)
    @GeneratedValue(generator = "IndConditionSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    private String condition;

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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    
}
