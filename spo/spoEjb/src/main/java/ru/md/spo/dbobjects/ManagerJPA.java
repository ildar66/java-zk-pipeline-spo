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

import ru.md.pup.dbobjects.UserJPA;

/**
 * Менеджер заявки. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "manager")
public class ManagerJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_manager")
    @SequenceGenerator(name = "manager_seq", sequenceName = "manager_seq", allocationSize = 1)
    @GeneratedValue(generator = "manager_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="id_user")
    private UserJPA user;
    
    @ManyToOne @JoinColumn(name="id_mdtask")
    private TaskJPA task;

    @ManyToOne @JoinColumn(name="id_start_department")
    private StartDepartmentJPA startDepartment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserJPA getUser() {
        return user;
    }

    public void setUser(UserJPA user) {
        this.user = user;
    }

    public TaskJPA getTask() {
        return task;
    }

    public void setTask(TaskJPA task) {
        this.task = task;
    }

    public StartDepartmentJPA getStartDepartment() {
        return startDepartment;
    }

    public void setStartDepartment(StartDepartmentJPA startDepartment) {
        this.startDepartment = startDepartment;
    }
    
    
}
