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
 * Назначение на пользователя задачи.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "assign_active")
public class AssignJPA implements Serializable,Comparable<AssignJPA> {
    private static final long serialVersionUID = 3L;
    
    @Id @Column(name="id_assign")
    private Long idAssign;
    
    @ManyToOne @JoinColumn(name="id_user_to")
    private UserJPA userTo;
    
    @ManyToOne @JoinColumn(name="id_role")
    private RoleJPA role;
    
    @ManyToOne @JoinColumn(name="id_user_from")
    private UserJPA userFrom;
    
    private Long id_process;
    
    private Date date_event;
    
    private Long id_type_process;
    
    public String toString(){
        return "id_process="+id_process+", idAssign="+idAssign+", userTo="+userTo.toString();
    }

    /**
     * @return idAssign
     */
    public Long getIdAssign() {
        return idAssign;
    }

    /**
     * @param idAssign idAssign
     */
    public void setIdAssign(Long idAssign) {
        this.idAssign = idAssign;
    }

    /**
     * @return userTo
     */
    public UserJPA getUserTo() {
        return userTo;
    }

    /**
     * @param userTo userTo
     */
    public void setUserTo(UserJPA userTo) {
        this.userTo = userTo;
    }

    /**
     * @return role
     */
    public RoleJPA getRole() {
        return role;
    }

    /**
     * @param role role
     */
    public void setRole(RoleJPA role) {
        this.role = role;
    }

    /**
     * @return userFrom
     */
    public UserJPA getUserFrom() {
        return userFrom;
    }

    /**
     * @param userFrom userFrom
     */
    public void setUserFrom(UserJPA userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * @return id_process
     */
    public Long getId_process() {
        return id_process;
    }

    /**
     * @param id_process id_process
     */
    public void setId_process(Long id_process) {
        this.id_process = id_process;
    }

    /**
     * @return date_event
     */
    public Date getDate_event() {
        return date_event;
    }

    /**
     * @param date_event date_event
     */
    public void setDate_event(Date date_event) {
        this.date_event = date_event;
    }

    /**
     * @return id_type_process
     */
    public Long getId_type_process() {
        return id_type_process;
    }

    /**
     * @param id_type_process id_type_process
     */
    public void setId_type_process(Long id_type_process) {
        this.id_type_process = id_type_process;
    }

    @Override
    public int compareTo(AssignJPA o) {
        return -1*this.getId_process().compareTo(o.getId_process());
    }
    
}
