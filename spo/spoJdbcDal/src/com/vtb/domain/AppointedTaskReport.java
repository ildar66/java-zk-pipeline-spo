package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'Appointed Task' 
 * @author Michael Kuznetsov 
 */
public class AppointedTaskReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private String headers;
	private List<AppointedTaskReportRecord> records;

	
	public AppointedTaskReport() {
        super();        
    }
 
	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public void setRecords(List<AppointedTaskReportRecord> records) {
		this.records = records;
	}
	
	public List<AppointedTaskReportRecord> getRecords() {
		return records;
	}
}
