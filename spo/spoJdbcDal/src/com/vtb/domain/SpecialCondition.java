package com.vtb.domain;

public class SpecialCondition extends VtbObject{
	private static final long serialVersionUID = 1L;
	private String type;
	private String body;
	/**
	 * @return the type
	 */
	public String getType() {
		return type==null||type.equals("null")?"":type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body==null||body.equals("null")?"":body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	public SpecialCondition(String type, String body) {
		super();
		this.type = type;
		this.body = body;
	}
}
