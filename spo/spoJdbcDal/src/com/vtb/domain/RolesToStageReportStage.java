package com.vtb.domain;

import java.util.List;

/**
 * Single string data for report 'RolesToStage' 
 * @author Michael Kuznetsov 
 */
public class RolesToStageReportStage  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String operation_name;	
	private Long operationId;
	private List<String> roles;
	
	public RolesToStageReportStage() {
        super();        
    }

	public String getOperation_name() {
		return operation_name;
	}

	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Long getOperationId() {
		return operationId;
	}

	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}
}

