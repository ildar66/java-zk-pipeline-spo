package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'Active Stages' 
 * @author Michael Kuznetsov 
 */
public class ActiveStagesReportProcessType  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long idProcessType;
	private String description;
	private List<ActiveStagesReportOperation> operations;

	public ActiveStagesReportProcessType() {
        super();        
    }
	
	public void setOperations (List<ActiveStagesReportOperation> list) {
		operations = list;
	}
	
	public List<ActiveStagesReportOperation> getOperations () {
		return operations;
	}

	public Long getIdProcessType() {
		return idProcessType;
	}

	public void setIdProcessType(Long idProcessType) {
		this.idProcessType = idProcessType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
