package com.vtb.domain;

import java.sql.Date;

/**
 * 
 * This class describes the value of stop factors for contractor in the system
 * 
 * @author Администратор
 */
public class StopFactorValue extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    long id_stop_factor;
	long id_contractor;
	int value;
	String description;
	Date date_of_detection;
	Date date_of_expiration;
	
	public Date getDate_of_detection() {
		return date_of_detection;
	}
	public void setDate_of_detection(Date date_of_detection) {
		this.date_of_detection = date_of_detection;
	}
	public Date getDate_of_expiration() {
		return date_of_expiration;
	}
	public void setDate_of_expiration(Date date_of_expiration) {
		this.date_of_expiration = date_of_expiration;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getId_contractor() {
		return id_contractor;
	}
	public void setId_contractor(long id_contractor) {
		this.id_contractor = id_contractor;
	}
	public long getId_stop_factor() {
		return id_stop_factor;
	}
	public void setId_stop_factor(long id_stop_factor) {
		this.id_stop_factor = id_stop_factor;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	
}
