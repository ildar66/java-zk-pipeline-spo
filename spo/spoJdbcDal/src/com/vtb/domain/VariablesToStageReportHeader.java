package com.vtb.domain;

/**
 * Header data for a report 'VariablesToStage'
 * @author Michael Kuznetsov 
 */
public class VariablesToStageReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private String process_name;
	
	public VariablesToStageReportHeader() {
        super();        
    }

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}
}

