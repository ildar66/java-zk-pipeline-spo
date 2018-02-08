package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Повышающий коэффициент за риск
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "crm_risk_stepup_factor")
public class RiskStepupFactorJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String item_id;
    private String text;//надбавка
    private String shorttext;//не используется
    private String shortname;//Повыш. коэфф. за риск
    private Long is_active;
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getShorttext() {
		return shorttext;
	}
	public void setShorttext(String shorttext) {
		this.shorttext = shorttext;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public boolean isActive() {
		if(is_active==null)
			return false;
		return is_active.equals(1L);
	}
	public void setIs_active(Long is_active) {
		this.is_active = is_active;
	}
    
}
