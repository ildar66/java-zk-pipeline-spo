package ru.md.domain.dashboard;

import java.util.Date;

import ru.masterdm.spo.list.EDashStatus;
import ru.masterdm.spo.utils.Formatter;

/**
 * Created by Admin on 17.08.2016.
 */
public class DashboardEvent {
    private Long idMdtask;
    private Date eventDate;
    private EDashStatus status;

    public DashboardEvent(Long idMdtask, Date eventDate, EDashStatus status) {
        this.idMdtask = idMdtask;
        this.eventDate = eventDate;
        this.status = status;
    }

    public DashboardEvent() {
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdMdtask() {
        return idMdtask;
    }

    /**
     * Sets .
     * @param idMdtask
     */
    public void setIdMdtask(Long idMdtask) {
        this.idMdtask = idMdtask;
    }

    /**
     * Returns .
     * @return
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Sets .
     * @param eventDate
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Returns .
     * @return
     */
    public EDashStatus getStatus() {
        return status;
    }

    /**
     * Sets .
     * @param status
     */
    public void setStatus(EDashStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        if (status == null)
            return "";
        return status.getName() + " " + Formatter.formatDateTime(eventDate);
    }
}
