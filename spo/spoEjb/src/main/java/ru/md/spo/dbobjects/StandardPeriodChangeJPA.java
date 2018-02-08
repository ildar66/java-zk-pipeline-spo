package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;

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

import com.vtb.util.Formatter;

/**
 * Значение нормативных сроков для конкретной заявки. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "STANDARD_PERIOD_change_mdtask")
public class StandardPeriodChangeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "StandardPeriodChangeSequenceGenerator", sequenceName = "ST_PER_change_mdtask_seq", allocationSize = 1)
    @GeneratedValue(generator = "StandardPeriodChangeSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name="id_mdtask")
    private TaskJPA task;
    
    @Column(name="change_comment")
    private String changeComment;

    @ManyToOne @JoinColumn(name="id_standard_period_value")
    private StandardPeriodValueJPA value;
    
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="who_change")
    private UserJPA whoChange;
    
    @Column(name ="when_change")
    private Date whenChange;

    private Long days;//число рабочих дней для ручного ввода срока
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_SPG")
    private StandardPeriodGroupJPA group;
    
    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return task
     */
    public TaskJPA getTask() {
        return task;
    }

    /**
     * @param task task
     */
    public void setTask(TaskJPA task) {
        this.task = task;
    }

    /**
     * @return changeComment
     */
    public String getChangeComment() {
        return changeComment;
    }

    /**
     * @param changeComment changeComment
     */
    public void setChangeComment(String changeComment) {
        this.changeComment = changeComment;
    }

    /**
     * @return value
     */
    public StandardPeriodValueJPA getValue() {
        return value;
    }

    /**
     * @param value value
     */
    public void setValue(StandardPeriodValueJPA value) {
        this.value = value;
    }

    /**
     * @return whoChange
     */
    public UserJPA getWhoChange() {
        return whoChange;
    }

    /**
     * @param whoChange whoChange
     */
    public void setWhoChange(UserJPA whoChange) {
        this.whoChange = whoChange;
    }

    /**
     * @return whenChange
     */
    public Date getWhenChange() {
        return whenChange;
    }

    /**
     * @param whenChange whenChange
     */
    public void setWhenChange(Date whenChange) {
        this.whenChange = whenChange;
    }
    public String getWhoChangeFormated(){
        if(whoChange==null) return "";
        String s = "\n\nНормативный срок "+getPeriod()+" раб.дн. ";
        if(getValue()!=null)
        	s+= "("+getValue().getName()+")";
        s+= " изменил пользователь "+whoChange.getFullName()+" "
                + Formatter.formatDateTime(whenChange)+
                " \nКомментарий: "+changeComment;
        return s;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public StandardPeriodGroupJPA getGroup() {
        if(value!=null)
            return value.getGroup();
        return group;
    }

    public void setGroup(StandardPeriodGroupJPA group) {
        this.group = group;
    }
    public Long getPeriod() {
        if(days!=null) return days;
        if(value==null)
            return 1L;
        return value.getPeriod();
    }

    public String getCriteria(){
        if(value==null)
            return "";
        return value.getName();
    }

	@Override
	public String toString() {
		return "StandardPeriodChangeJPA [id=" + id + ", changeComment="
				+ changeComment + "]";
	}
    
}
