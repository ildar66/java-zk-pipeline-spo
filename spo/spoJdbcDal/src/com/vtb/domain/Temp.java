package com.vtb.domain;

import java.sql.Date;

public class Temp extends VtbObject {//временные поля, чтобы удовлетворить АВ.
    //этот код будет уничтожен после ввода КК в промэксплуатацию
    private static final long serialVersionUID = 1L;
    private Date meetingDate;
    private Date planMeetingDate;
    private String resolution="";
    public Date getMeetingDate() {
        return meetingDate;
    }
    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }
    public Date getPlanMeetingDate() {
        return planMeetingDate;
    }
    public void setPlanMeetingDate(Date planMeetingDate) {
        this.planMeetingDate = planMeetingDate;
    }
    public String getResolution() {
        return (resolution==null)?"":resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public Temp() {
        super();
    }
}
