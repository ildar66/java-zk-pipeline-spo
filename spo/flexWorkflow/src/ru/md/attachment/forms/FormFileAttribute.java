package ru.md.attachment.forms;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class FormFileAttribute extends ActionForm {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String unid = null;

    public String getUnid() {
    	return unid;
    }

    
    public void setUnid(String unid) {
    	this.unid = unid;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	unid = null;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
			
		if (this.getUnid().equals("")) {
			errors.add("File unid not specified", new org.apache.struts.action.ActionError("error.message"));
		}
		
		return errors;
    }
}
