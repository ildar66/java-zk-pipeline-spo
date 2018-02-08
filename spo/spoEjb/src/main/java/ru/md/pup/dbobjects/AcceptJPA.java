package ru.md.pup.dbobjects;

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

/**
 * JPA object "Акцепт операции"
 * 
 * @author imatushak@masterdm.ru
 * 
 */
@Entity
@Table(name = "ACCEPT")
public class AcceptJPA implements Serializable {
    private static final long serialVersionUID = 3L;

    @Id
    @Column(name = "ID_ACCEPT")
    @SequenceGenerator(name = "AcceptSequenceGenerator", sequenceName = "ACCEPT_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "AcceptSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long acceptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TASK")
    private TaskInfoJPA taskInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER")
    private UserJPA user;

    @Column(name = "INIT_DATE")
    private Date initDate;

    @Column(name = "ACCEPT_DATE")
    private Date acceptDate;

    /**
     * @return the acceptId
     */
    public Long getAcceptId() {
        return acceptId;
    }

    /**
     * @param acceptId
     *            the acceptId to set
     */
    public void setAcceptId(Long acceptId) {
        this.acceptId = acceptId;
    }

    /**
     * @return the taskInfo
     */
    public TaskInfoJPA getTaskInfo() {
        return taskInfo;
    }

    /**
     * @param taskInfo
     *            the taskInfo to set
     */
    public void setTaskInfo(TaskInfoJPA taskInfo) {
        this.taskInfo = taskInfo;
    }

    /**
     * @return the user
     */
    public UserJPA getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(UserJPA user) {
        this.user = user;
    }

    /**
     * @return the initDate
     */
    public Date getInitDate() {
        return initDate;
    }

    /**
     * @param initDate
     *            the initDate to set
     */
    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    /**
     * @return the acceptDate
     */
    public Date getAcceptDate() {
        return acceptDate;
    }

    /**
     * @param acceptDate
     *            the acceptDate to set
     */
    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

}
