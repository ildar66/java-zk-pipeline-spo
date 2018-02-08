package ru.md.spo.dbobjects;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vtb.util.Formatter;

/**
 * Компенсирующий спрэд за фиксацию ставки
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "crm_fb_stavspred")
public class StavspredJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "FB_STAVSPREDID")
    private String id;

    private String unit;
    private Double stav_spred;
    private String additional_param;
    private Date activedate;
    private Long is_active;
    private Long days_from;
    private Long days_to;
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getStavSpredFormatedInPercent() {
        return Formatter.format(100*stav_spred);
    }
    public Double getStav_spred() {
        return stav_spred;
    }
    public void setStav_spred(Double stav_spred) {
        this.stav_spred = stav_spred;
    }
    public String getAdditional_param() {
        return additional_param;
    }
    public void setAdditional_param(String additional_param) {
        this.additional_param = additional_param;
    }
    public Date getActivedate() {
        return activedate;
    }
    public void setActivedate(Date activedate) {
        this.activedate = activedate;
    }
    public Long getIs_active() {
        return is_active;
    }
    public void setIs_active(Long is_active) {
        this.is_active = is_active;
    }
    public Long getDays_from() {
        return days_from;
    }
    public void setDays_from(Long days_from) {
        this.days_from = days_from;
    }
    public Long getDays_to() {
        return days_to;
    }
    public void setDays_to(Long days_to) {
        this.days_to = days_to;
    }
    
    
}
