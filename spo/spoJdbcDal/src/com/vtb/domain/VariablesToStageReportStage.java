package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'Active Stages' 
 * @author Michael Kuznetsov 
 */
public class VariablesToStageReportStage  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String stage_name;	
	private Long stageId;
	private List<String> variables;
	
	public VariablesToStageReportStage() {
        super();        
    }

	public String getStage_name() {
		return stage_name;
	}

	public void setStage_name(String stage_name) {
		this.stage_name = stage_name;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public Long getStageId() {
		return stageId;
	}

	public void setStageId(Long stageId) {
		this.stageId = stageId;
	}
}

