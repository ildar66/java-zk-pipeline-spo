package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity @Table(name="task_events")
public class TaskEventJPA implements Serializable {
    //id_task_type_event e.date_event
    private static final long serialVersionUID = 1L;
    
    @Id @Column(name="id_task_event")
    private Long idTaskEvent;
    
    @ManyToOne @JoinColumn(name="id_task")
    private TaskInfoJPA taskInfo;
    
    @Column(name="id_task_type_event")
    private Long type;
    
    @Column(name="date_event")
    private Date date;

    /**
     * @return idTaskEvent
     */
    public Long getIdTaskEvent() {
        return idTaskEvent;
    }

    /**
     * @param idTaskEvent idTaskEvent
     */
    public void setIdTaskEvent(Long idTaskEvent) {
        this.idTaskEvent = idTaskEvent;
    }

    /**
     * @return taskInfo
     */
    public TaskInfoJPA getTaskInfo() {
        return taskInfo;
    }

    /**
     * @param taskInfo taskInfo
     */
    public void setTaskInfo(TaskInfoJPA taskInfo) {
        this.taskInfo = taskInfo;
    }

    /**
     * @return type
     */
    public Long getType() {
        return type;
    }

    /**
     * @param type type
     */
    public void setType(Long type) {
        this.type = type;
    }

    /**
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
    
}
