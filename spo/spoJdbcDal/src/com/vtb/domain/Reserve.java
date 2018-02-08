package com.vtb.domain;

public class Reserve  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String resume;
	private String quality;
	private String reserve_class;
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getReserve_class() {
		return reserve_class;
	}
	public void setReserve_class(String reserve_class) {
		this.reserve_class = reserve_class;
	}

}
