package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;

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
 * Вексель. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "PROMISSORY_NOTE")
public class PromissoryNoteJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "PromissoryNoteSequenceGenerator", sequenceName = "PROMISSORY_NOTE_seq", allocationSize = 1)
    @GeneratedValue(generator = "PromissoryNoteSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    private String holder;//векселедержатель
    
    private Double val;
    private String currency;
    private Double perc;
    private String place;
    private Date maxdate;
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
    public String getHolder() {
        return holder==null?"":holder;
    }
    public void setHolder(String holder) {
        this.holder = holder;
    }
    public Double getVal() {
        return val;
    }
    public void setVal(Double val) {
        this.val = val;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public Double getPerc() {
        return perc;
    }
    public void setPerc(Double perc) {
        this.perc = perc;
    }
    public String getPlace() {
        if(place==null)
            return "";
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public Date getMaxdate() {
        return maxdate;
    }
    public void setMaxdate(Date maxdate) {
        this.maxdate = maxdate;
    }
    
    
}
