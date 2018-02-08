package com.vtb.domain;

import java.sql.Date;

public class Decision  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Date meet_date;
	private Date date;
	private String decision;
	private String order;
	public Date getMeet_date() {
		return meet_date;
	}
	public void setMeet_date(Date meet_date) {
		this.meet_date = meet_date;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDecision() {
		return decision;
	}
	public void setDecision(String decision) {
		this.decision = decision;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}

}