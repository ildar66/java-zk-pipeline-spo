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
 * Проектная команда. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "project_team")
public class ProjectTeamJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ProjectTeamSequenceGenerator", sequenceName = "project_team_seq", allocationSize = 1)
    @GeneratedValue(generator = "ProjectTeamSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_USER")
    private UserJPA user;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    private String teamType;

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
    public ProjectTeamJPA() {
        super();
        teamType = "p";
    }

    public String getTeamType() {
        return teamType==null?"p":teamType;
    }

    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }
    
    
}
