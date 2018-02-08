package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * События процесса. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="pauseParam")
public class PauseParamJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @Column(name="id")
    private Long id;
    @ManyToOne @JoinColumn(name="ID_PROCESS_EVENT")
    private ProcessEventJPA event;
    private String cmnt;
    private Date dateresume;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ProcessEventJPA getEvent() {
        return event;
    }
    public void setEvent(ProcessEventJPA event) {
        this.event = event;
    }
    public String getCmnt() {
        return cmnt;
    }
    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }
    public Date getDateresume() {
        return dateresume;
    }
    public void setDateresume(Date dateresume) {
        this.dateresume = dateresume;
    }
    @Override
    public String toString() {
        return "PauseParamJPA [cmnt=" + cmnt + ", dateresume=" + dateresume;
    }
    
}
