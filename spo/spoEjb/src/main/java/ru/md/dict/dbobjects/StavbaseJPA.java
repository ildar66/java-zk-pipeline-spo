package ru.md.dict.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Ставки фондирования
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "crm_stavbase")
public class StavbaseJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String FB_STAVBASEID;//SLXID записи
    private String unit;//Код валюты
    private String diapname;//Название диапазона сроков. Например_ "до недели", "до месяца"…
    private Long diapdaymin;//Начало диапазона (число дней), которое не перекрывается с предыдущим диапазоном.
    private Long diapdaymax;//Конец диапазона (число дней). 
    private Double stavvalue;//Значение ставки 
    private Date activedate;//Дата, с которой ставка активна. До этой даты действует предыдущая ставка
	public String getFB_STAVBASEID() {
		return FB_STAVBASEID;
	}
	public void setFB_STAVBASEID(String fB_STAVBASEID) {
		FB_STAVBASEID = fB_STAVBASEID;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDiapname() {
		return diapname;
	}
	public void setDiapname(String diapname) {
		this.diapname = diapname;
	}
	public Long getDiapdaymin() {
		return diapdaymin;
	}
	public void setDiapdaymin(Long diapdaymin) {
		this.diapdaymin = diapdaymin;
	}
	public Long getDiapdaymax() {
		return diapdaymax;
	}
	public void setDiapdaymax(Long diapdaymax) {
		this.diapdaymax = diapdaymax;
	}
	public Double getStavvalue() {
		return stavvalue;
	}
	public void setStavvalue(Double stavvalue) {
		this.stavvalue = stavvalue;
	}
	public Date getActivedate() {
		return activedate;
	}
	public void setActivedate(Date activedate) {
		this.activedate = activedate;
	}
    
}
