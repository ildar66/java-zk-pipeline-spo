package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.md.pup.dbobjects.UserJPA;

/**
 * Экспертная команда.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "expert_team")
public class ExpertTeamJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ExpertTeamSequenceGenerator", sequenceName = "expert_team_seq", allocationSize = 1)
    @GeneratedValue(generator = "ExpertTeamSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_USER")
    private UserJPA user;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    private String expname;

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
	public String getExpname() {
		return expname;
	}
	public void setExpname(String expname) {
		this.expname = expname;
	}
}
