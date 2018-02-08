package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'New Task' 
 * @author Michael Kuznetsov 
 */
public class NewTaskReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private String headers;
	private List<NewTaskReportRecord> records;

	
	public NewTaskReport() {
        super();        
    }
 
	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public void setRecords(List<NewTaskReportRecord> records) {
		this.records = records;
	}
	
	public List<NewTaskReportRecord> getRecords() {
		return records;
	}
}
