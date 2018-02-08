package com.vtb.domain;
/**
 * Комиссии из CRM
 * @author Andrey Pavlenko
 *
 */
public class CrmComiss extends VtbObject {
    private static final long serialVersionUID = 1L;
    private String comiss_code;
    private Double comiss_value;
    private String comiss_unit;
    private String comiss_base;
    private String comiss_periodichnost;
    private String notes;
    /**
     * @return тип комиссии
     */
    public String getComiss_code() {
        return comiss_code;
    }
    /**
     * @param comiss_code тип комиссии
     */
    public void setComiss_code(String comiss_code) {
        this.comiss_code = comiss_code;
    }
    /**
     * @return Отражает числовое значение комиссии (сумма денежных средств или процент), введенное клиентским менеджером в рамках сделки.
     */
    public Double getComiss_value() {
        return comiss_value;
    }
    /**
     * @param comiss_value Отражает числовое значение комиссии (сумма денежных средств или процент), введенное клиентским менеджером в рамках сделки.
     */
    public void setComiss_value(Double comiss_value) {
        this.comiss_value = comiss_value;
    }
    /**
     * @return Отражает единицы измерения суммы комиссии (валюта, процент)
     */
    public String getComiss_unit() {
        return comiss_unit;
    }
    /**
     * @param comiss_unit Отражает единицы измерения суммы комиссии (валюта, процент)
     */
    public void setComiss_unit(String comiss_unit) {
        this.comiss_unit = comiss_unit;
    }
    /**
     * @return Отражает базу для расчета комиссии.
     */
    public String getComiss_base() {
        return comiss_base;
    }
    /**
     * @param comiss_base Отражает базу для расчета комиссии.
     */
    public void setComiss_base(String comiss_base) {
        this.comiss_base = comiss_base;
    }
    /**
     * @return Отражает установленную клиентским менеджером периодичность взимания комиссии
     */
    public String getComiss_periodichnost() {
        return comiss_periodichnost;
    }
    /**
     * @param comiss_periodichnost Отражает установленную клиентским менеджером периодичность взимания комиссии
     */
    public void setComiss_periodichnost(String comiss_periodichnost) {
        this.comiss_periodichnost = comiss_periodichnost;
    }
    /**
     * @return Отражает введенные клиентским менеджером комментарии.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * @param notes Отражает введенные клиентским менеджером комментарии.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
}
