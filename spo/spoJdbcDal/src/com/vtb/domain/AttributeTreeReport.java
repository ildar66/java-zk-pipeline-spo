package com.vtb.domain;

import java.util.List;

/**
 * Compound value object for the report 'AttributeTree' 
 * @author Michael Kuznetsov 
 */
public class AttributeTreeReport extends VtbObject {
	private static final long serialVersionUID = 1L;
	private AttributeTreeReportHeader headers;
	private List<AttributeTreeReportOperation> operations;
	
	public AttributeTreeReport() {
        super();        
    }
 
	public AttributeTreeReportHeader getHeaders() {
		return headers;
	}

	public void setHeaders(AttributeTreeReportHeader headers) {
		this.headers = headers;
	}

	public void setOperations(List<AttributeTreeReportOperation> operations) {
		this.operations = operations;
	}
	
	public List<AttributeTreeReportOperation> getOperations() {
		return operations;
	}
}
