package ru.md.spo.report;

import java.util.Date;

public class EventString implements Comparable<EventString> {
	private String message;
	private java.util.Date date;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public EventString(String message, Date date) {
		super();
		this.message = message;
		this.date = date;
	}
	@Override
	public int compareTo(EventString o) {
		return date.compareTo(o.getDate());
	}
	
}
