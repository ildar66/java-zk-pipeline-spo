/**
 * 
 */
package ru.md.spo.dbobjects;

import java.io.Serializable;
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

import ru.md.pup.dbobjects.DepartmentJPA;

/**
 * Инициирующий департамент. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "start_department")
public class StartDepartmentJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_start_department")
    @SequenceGenerator(name = "start_department_seq", sequenceName = "start_department_seq", allocationSize = 1)
    @GeneratedValue(generator = "start_department_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="id_department")
    private DepartmentJPA department;
    
    @ManyToOne @JoinColumn(name="id_mdtask")
    private TaskJPA task;
    
    @OneToMany(mappedBy = "startDepartment", fetch = FetchType.LAZY)
    private List<ManagerJPA> managers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DepartmentJPA getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentJPA department) {
        this.department = department;
    }

    public TaskJPA getTask() {
        return task;
    }

    public void setTask(TaskJPA task) {
        this.task = task;
    }

    public List<ManagerJPA> getManagers() {
        return managers;
    }

    public void setManagers(List<ManagerJPA> managers) {
        this.managers = managers;
    }
    
}
