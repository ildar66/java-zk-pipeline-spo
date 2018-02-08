package ru.md.domain.dashboard;

import ru.masterdm.spo.list.EDashStatus;

/**
 * Доменный обьект статусов записей в аналитической таблице. Статусы предстваленны в таблице SPO_DASHBOARD_STATUS
 * @author pmasalov
 */
public class TaskTypeStatus {
    /** Уникальный идентификатор статуса */
    int idStatus;
    /** Наименование статуса */
    String status;
    /** приналдлежность статуса к типу заявок */
    String taskType;
    /** Порядок отображения информации по дангому статусу в разреде типа заявок */
    Integer orderdisp;

    /**
     * Returns .
     * @return
     */
    public int getIdStatus() {
        return idStatus;
    }

    /**
     * Sets .
     * @param idStatus
     */
    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    /**
     * Returns .
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets .
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns .
     * @return
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets .
     * @param taskType
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * Returns .
     * @return
     */
    public Integer getOrderdisp() {
        return orderdisp;
    }

    /**
     * Sets .
     * @param orderdisp
     */
    public void setOrderdisp(Integer orderdisp) {
        this.orderdisp = orderdisp;
    }

    public EDashStatus obtainEnum() {
        return EDashStatus.find(getIdStatus());
    }
}
