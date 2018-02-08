package com.vtb.domain;
public class TaskOppType extends VtbObject{
	private Long id;// это айдишник-уникальный ключ. Не меняется. Используется как первичный ключ в БД
	private static final long serialVersionUID = 1L;
	private  boolean flag;
	private String opportunityType;
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getOpportunityType() {
		return opportunityType;
	}
	public void setOpportunityType(String opportunityType) {
		this.opportunityType = opportunityType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TaskOppType(Long id, boolean flag, String opportunityType) {
		super();
		this.id = id;
		this.flag = flag;
		this.opportunityType = opportunityType;
	}
}