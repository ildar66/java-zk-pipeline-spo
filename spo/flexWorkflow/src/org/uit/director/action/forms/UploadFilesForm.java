package org.uit.director.action.forms;

import org.apache.struts.action.ActionForm;

public class UploadFilesForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private String id;
	private String type;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
