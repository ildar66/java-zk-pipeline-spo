package ru.md.attachment.forms;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * @version 	1.0
 * @author
 */
public class AttachmentsListForm extends ActionForm
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String ACTION_SHOW = "show"; 
	public static final String ACTION_REMOVE = "remove";
	public static final String ACTION_ACCEPT = "accept";
	public static final String ACTION_REQUEST = "request";
	
	String id = "0";
    int type = 0;
    String action = ACTION_SHOW;
	String[] unid;

    public void reset(ActionMapping mapping, HttpServletRequest request) {

	// Reset field values here.

    }

    public ActionErrors validate(ActionMapping mapping,
	    HttpServletRequest request) {

	ActionErrors errors = new ActionErrors();
	// Validate the fields in your form, adding
	// adding each error to this.errors as found, e.g.

	// if ((field == null) || (field.length() == 0)) {
	//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
	// }
	return errors;

    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getUnid() {
		return unid;
	}

	public void setUnid(String[] unid) {
		this.unid = unid;
	}
}
