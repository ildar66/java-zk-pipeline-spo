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
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Процесс ПУП. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name="processes")
public class ProcessJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_process")
    private Long id;

    @Column(name="id_status")
    private Long idStatus;

    @ManyToOne
    @JoinColumn(name="ID_TYPE_PROCESS")
    private ProcessTypeJPA processType;
    
    @OneToMany(mappedBy = "process", fetch = FetchType.EAGER)
    private List<AttributeJPA> attributes;//FIXME если поставить LAZY, то не будет работать на was8.5, будет только на 7.0
    
    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY) @OrderBy("date_event desc")
    private List<ProcessEventJPA> events;
    
    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY)
    private List<TaskInfoJPA> tasks;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStatus() {
        return idStatus;
    }

    public boolean isPaused(){
        if(idStatus==null)
            return false;
        return idStatus.longValue()==2;
    }
    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }

    public ProcessTypeJPA getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessTypeJPA process) {
        this.processType = process;
    }
	public List<AttributeJPA> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeJPA> attributes) {
		this.attributes = attributes;
	}
    public List<ProcessEventJPA> getEvents() {
        return events;
    }
    public void setEvents(List<ProcessEventJPA> events) {
        this.events = events;
    }
    /**дата восстановления*/
    public Date getResumeDate(){
        for(ProcessEventJPA event : events){
            if(!event.getId_process_type_event().equals(2L))
                continue;
            if(event.getPauseParams().size()>0)
                return event.getPauseParams().get(0).getDateresume();
        }
        return null;
    }
    /**есть ли у заявки история приостановления*/
    public boolean isHaveBeenPause(){
        for(ProcessEventJPA event : events){
            if(event.getId_process_type_event().equals(2L)||event.getId_process_type_event().equals(9L))
                return true;
        }
        return false;
    }
	public List<TaskInfoJPA> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskInfoJPA> tasks) {
		this.tasks = tasks;
	}
    
}
