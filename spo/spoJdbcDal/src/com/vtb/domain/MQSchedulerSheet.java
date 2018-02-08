package com.vtb.domain;

import java.sql.Time;

public class MQSchedulerSheet extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static long CONST_ID_UNKNOWN = -1;
	
	public static final int STATUS_ACTIVATED = 1;
	public static final int STATUS_NOT_ACTIVATED = 2;
	 
	Long id;
	Time startTime;
	Time endTime;
	Long id_department;
	
	Integer daysOfWeek;
	Integer status;
	
	public MQSchedulerSheet() {
		// TODO Auto-generated constructor stub
		this.id = Long.valueOf(CONST_ID_UNKNOWN);
	}
	
	public MQSchedulerSheet(long id) {
		// TODO Auto-generated constructor stub
		this.id = Long.valueOf(id);
	}
	
	public MQSchedulerSheet(long id, long id_department, int status) {
		this.id= Long.valueOf(id);
		this.id_department = Long.valueOf(id_department);
		this.status = Integer.valueOf(status);
	}
	
	public Integer getDaysOfWeek() {
		return daysOfWeek;
	}
	public void setDaysOfWeek(Integer daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}
	public void setDaysOfWeek(int[] daysOfWeek) {
		if (daysOfWeek != null) {
			int newValue = 0;
			for (int i=0; i < daysOfWeek.length; i++)
				newValue = newValue + (1<<(daysOfWeek[i]-1));
			this.daysOfWeek = newValue;
				
		}
		
	}
	public Time getEndTime() {
		return endTime;
	}
	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setId(long id) {
		this.id = Long.valueOf(id);
	}
	
	public Long getId_department() {
		return id_department;
	}
	public void setId_department(Long id_department) {
		this.id_department = id_department;
	}
	public void setId_department(long id_department) {
		this.id_department = Long.valueOf(id_department);
	}
	
	public Time getStartTime() {
		return startTime;
	}
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;		
	}
	public void setStatus(int status) {
		this.status = Integer.valueOf(status);		
	}

	
	
}
