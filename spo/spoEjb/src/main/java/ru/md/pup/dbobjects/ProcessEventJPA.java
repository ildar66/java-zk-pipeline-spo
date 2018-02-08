package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * События процесса. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="process_events")
public class ProcessEventJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name="ID_PROCESS_EVENT")
    private Long id;
    @ManyToOne @JoinColumn(name="ID_PROCESS")
    private ProcessJPA process;
    private Long id_process_type_event;
    private Date date_event;
    
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_user")
    private UserJPA user;
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<PauseParamJPA> pauseParams;//на самом деле здесь будет только один элемент или ни одного

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ProcessJPA getProcess() {
        return process;
    }
    public void setProcess(ProcessJPA process) {
        this.process = process;
    }
    public Long getId_process_type_event() {
        return id_process_type_event;
    }
    public void setId_process_type_event(Long id_process_type_event) {
        this.id_process_type_event = id_process_type_event;
    }
    public Date getDate_event() {
        return date_event;
    }
    public void setDate_event(Date date_event) {
        this.date_event = date_event;
    }
    public UserJPA getUser() {
        return user;
    }
    public void setUser(UserJPA user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "ProcessEventJPA [id_process_type_event="
                + id_process_type_event + ", date_event=" + date_event + "]";
    }
    public List<PauseParamJPA> getPauseParams() {
        return pauseParams;
    }
    public void setPauseParams(List<PauseParamJPA> pauseParams) {
        this.pauseParams = pauseParams;
    }
    public Date getPauseDate(){
        if(pauseParams.size()==0)
            return null;
        return pauseParams.get(0).getDateresume();
    }
    public String getPauseResumeCmnt(){
        if(pauseParams.size()==0)
            return "";
        return pauseParams.get(0).getCmnt();
    }
}
