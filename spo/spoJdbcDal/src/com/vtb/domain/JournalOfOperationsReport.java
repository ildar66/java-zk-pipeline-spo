package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'Journal of Operations' 
 * @author Michael Kuznetsov 
 */
public class JournalOfOperationsReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private JournalOfOperationsReportHeader headers;
	private List<JournalOfOperationsReportOperation> operations;

	
	public JournalOfOperationsReport() {
        super();        
    }
 
	public JournalOfOperationsReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(JournalOfOperationsReportHeader headers) {
		this.headers = headers;
	}

	public void setOperations (List<JournalOfOperationsReportOperation> list) {
		operations = list;
	}
	
	public List<JournalOfOperationsReportOperation> getOperations () {
		return operations;
	}
}
