package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'VariablesToStage' 
 * @author Michael Kuznetsov 
 */
public class VariablesToStageReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private VariablesToStageReportHeader headers;
	private List<VariablesToStageReportStage> stages;

	
	public VariablesToStageReport() {
        super();        
    }
 
	public VariablesToStageReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(VariablesToStageReportHeader headers) {
		this.headers = headers;
	}

	public void setStages(List<VariablesToStageReportStage> list) {
		stages = list;
	}
	
	public List<VariablesToStageReportStage> getStages() {
		return stages;
	}
}
