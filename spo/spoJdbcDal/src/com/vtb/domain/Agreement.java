package com.vtb.domain;

import java.util.Date;

import ru.masterdm.compendium.domain.Department;

public class Agreement extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Department department;
	private Date date;
	private String comment;
	private String remark;
	public Agreement(Long id) {
		super();
		this.id = id;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Agreement(Long id, Department department, Date date,
			String comment, String remark) {
		super();
		this.id = id;
		this.department = department;
		this.date = date;
		this.comment = comment;
		this.remark = remark;
	}
	
}