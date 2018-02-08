package ru.masterdm.integration;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Статус заявки в КК.
 * 
 * @author Andrey Pavlenko
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CCStatus implements Serializable {
    private static final long serialVersionUID = 2L;
    private Long questionId = null;  // номер вопроса (заявки). То, что id_mdtask
    private Long ccResolutionStatusId = null;// статус решения ID
    private Date meetingDate;// дата заседания
    private String protocol = null;// номер протокола кредитного комитета
    private Long id_report = null;// ID проекта решения в таблице cc_report

    /**
     * @return статус решения ID
     */
    public Long getStatus() {
        return ccResolutionStatusId;
    }

    /**
     * @param status
     *            статус решения ID
     */
    public void setStatus(Long status) {
        this.ccResolutionStatusId = status;
    }

    /**
     * @return дата заседания
     */
    public Date getMeetingDate() {
        return meetingDate;
    }

    /**
     * @param meetingDate
     *            дата заседания
     */
    public void setMeetingDate(java.util.Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    /**
     * @return номер протокола кредитного комитета
     */
    public String getProtocol() {
        return protocol == null ? "не присвоен" : protocol;
    }

    /**
     * @param protocol
     *            номер протокола кредитного комитета
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return ID проекта решения в таблице cc_report
     */
    public Long getId_report() {
        return id_report;
    }

    /**
     * @param id_report ID проекта решения в таблице cc_report
     */
    public void setId_report(Long id_report) {
        this.id_report = id_report;
    }

    /**
     * Возвращает номер вопроса
     * @return номер вопроса
     */
    public Long getQuestionId() {
        return questionId;
    }

    /**
     * Устанавливает номер вопроса
     * @param questionId номер вопроса
     */
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
