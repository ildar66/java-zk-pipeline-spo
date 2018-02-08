package com.vtb.domain;

/**
 * Single string of department for report 'RolesOfUsers' 
 * @author Michael Kuznetsov 
 */
public class AttributeTreeReportOperation  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String name;
	private String level;
	private String status;
	
	public AttributeTreeReportOperation() {
        super();        
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

