package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'Сроки прохождения этапов' 
 * @author Michael Kuznetsov 
 */
public class DurationStagesReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	//private JournalOfOperationsReportHeader headers;
	private List<StandardPeriod> operations;

	
	public DurationStagesReport() {
        super();        
    }
 
	public void setOperations (List<StandardPeriod> list) {
		operations = list;
	}
	
	public List<StandardPeriod> getOperations () {
		return operations;
	}
}
