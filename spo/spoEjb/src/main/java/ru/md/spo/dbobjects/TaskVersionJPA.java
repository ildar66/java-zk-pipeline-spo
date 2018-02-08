package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
/**
 * JPA for task saved version as print form. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "MDTASKVERSION")
public class TaskVersionJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "versionid")
    @SequenceGenerator(name = "TaskVersionSequenceGenerator", sequenceName = "MDTASKVERSION_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "TaskVersionSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String role; 
    private String userName;
    private Date versiondate;
    private String stage;
    private String report;
    private Long id_mdtask;
    
    public TaskVersionJPA() {
    }
    
    public TaskVersionJPA(String role, String userName, Date versiondate,
            String stage, String report, Long id_mdtask) {
        super();
        this.role = role;
        this.userName = userName;
        this.versiondate = versiondate;
        this.stage = stage;
        this.report = report;
        this.id_mdtask = id_mdtask;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Date getVersiondate() {
        return versiondate;
    }
    public void setVersiondate(Date versiondate) {
        this.versiondate = versiondate;
    }
    public String getStage() {
        return stage;
    }
    public void setStage(String stage) {
        this.stage = stage;
    }
    public String getReport() {
        return report;
    }
    public void setReport(String report) {
        this.report = report;
    }
    public Long getId_mdtask() {
        return id_mdtask;
    }
    public void setId_mdtask(Long id_mdtask) {
        this.id_mdtask = id_mdtask;
    }
    
}
