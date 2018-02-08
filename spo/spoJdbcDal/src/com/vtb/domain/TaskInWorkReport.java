package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'TaskInWork' 
 * @author Michael Kuznetsov 
 */
public class TaskInWorkReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private String headers;
	private List<TaskInWorkReportRecord> records;

	
	public TaskInWorkReport() {
        super();        
    }
 
	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public void setRecords(List<TaskInWorkReportRecord> records) {
		this.records = records;
	}
	
	public List<TaskInWorkReportRecord> getRecords() {
		return records;
	}
}
