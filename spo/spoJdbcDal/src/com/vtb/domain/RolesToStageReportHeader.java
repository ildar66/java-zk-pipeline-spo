package com.vtb.domain;

/**
 * Header data for a report 'RolesToStage'
 * @author Michael Kuznetsov 
 */
public class RolesToStageReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private String process_name;
	
	public RolesToStageReportHeader() {
        super();        
    }

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}
}

