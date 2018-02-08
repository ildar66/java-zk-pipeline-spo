package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'Active Stages' 
 * @author Michael Kuznetsov 
 */
public class ActiveStagesReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private ActiveStagesReportHeader headers;
	private List<ActiveStagesReportProcessType> processTypes;
	
	public ActiveStagesReport() {
        super();        
    }
 
	public ActiveStagesReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(ActiveStagesReportHeader headers) {
		this.headers = headers;
	}

	public List<ActiveStagesReportProcessType> getProcessTypes() {
		return processTypes;
	}

	public void setProcessTypes(List<ActiveStagesReportProcessType> processTypes) {
		this.processTypes = processTypes;
	}

}
