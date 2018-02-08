package com.vtb.domain;

public class SupplyConclusion extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String condition;
	private String analysis;
	private String about;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getAnalysis() {
		return analysis;
	}
	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
}