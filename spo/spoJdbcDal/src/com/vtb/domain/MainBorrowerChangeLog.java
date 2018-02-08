package com.vtb.domain;

import java.sql.Timestamp;

public class MainBorrowerChangeLog  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Timestamp logDate;
	private String oldOrg;
	private String newOrg;
	private String userName;
	private String unitedClient;
	public MainBorrowerChangeLog(Timestamp logDate, String oldOrg,
			String newOrg, String userName, String unitedClient) {
		super();
		this.logDate = logDate;
		this.oldOrg = oldOrg;
		this.newOrg = newOrg;
		this.userName = userName;
		this.unitedClient = unitedClient;
	}
	public Timestamp getLogDate() {
		return logDate;
	}
	public void setLogDate(Timestamp logDate) {
		this.logDate = logDate;
	}
	public String getOldOrg() {
		return oldOrg;
	}
	public void setOldOrg(String oldOrg) {
		this.oldOrg = oldOrg;
	}
	public String getNewOrg() {
		return newOrg;
	}
	public void setNewOrg(String newOrg) {
		this.newOrg = newOrg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUnitedClient() {
		return unitedClient;
	}
	public void setUnitedClient(String unitedClient) {
		this.unitedClient = unitedClient;
	}
}