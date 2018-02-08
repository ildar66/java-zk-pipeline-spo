package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'RolesToStage' 
 * @author Michael Kuznetsov 
 */
public class RolesToStageReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private RolesToStageReportHeader headers;
	private List<RolesToStageReportStage> operations;

	
	public RolesToStageReport() {
        super();        
    }
 
	public RolesToStageReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(RolesToStageReportHeader headers) {
		this.headers = headers;
	}
	
	public void setOperations(List<RolesToStageReportStage> list) {
		operations = list;
	}
	
	public List<RolesToStageReportStage> getOperations() {
		return operations;
	}
}
