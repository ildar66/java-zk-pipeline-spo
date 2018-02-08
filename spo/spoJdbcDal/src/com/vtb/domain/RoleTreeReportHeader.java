package com.vtb.domain;

/**
 * Header data for a report 'VariablesToStage'
 * @author Michael Kuznetsov 
 */
public class RoleTreeReportHeader  extends VtbObject{
	private static final long serialVersionUID = 1L;

	private String process_name;
	private String department_name;
	private String showactiveflag;
	private String information;
	public RoleTreeReportHeader() {
        super();        
    }

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public String getShowactiveflag() {
		return showactiveflag;
	}

	public void setShowactiveflag(String showactiveflag) {
		this.showactiveflag = showactiveflag;
	}

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}

