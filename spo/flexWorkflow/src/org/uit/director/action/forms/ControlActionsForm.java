package org.uit.director.action.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 5 fields on this form:
 * <ul>
 * <li>dateLeft - [your comment here]
 * <li>dateRight - [your comment here]
 * <li>idUser - [your comment here]
 * <li>actionName - [your comment here]
 * <li>ipAddress - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class ControlActionsForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private String dateLeft = null;
    private String dateRight = null;
    private String idUser = null;
    private String actionName = null;
    private String ipAddress = null;

    /**
     * Get dateLeft
     * @return String
     */
    public String getDateLeft() {
	return dateLeft;
    }

    /**
     * Set dateLeft
     * @param <code>String</code>
     */
    public void setDateLeft(String d) {
	this.dateLeft = d;
    }

    /**
     * Get dateRight
     * @return String
     */
    public String getDateRight() {
	return dateRight;
    }

    /**
     * Set dateRight
     * @param <code>String</code>
     */
    public void setDateRight(String d) {
	this.dateRight = d;
    }

    /**
     * Get idUser
     * @return String
     */
    public String getIdUser() {
	return idUser;
    }

    /**
     * Set idUser
     * @param <code>String</code>
     */
    public void setIdUser(String i) {
	this.idUser = i;
    }

    /**
     * Get actionName
     * @return String
     */
    public String getActionName() {
	return actionName;
    }

    /**
     * Set actionName
     * @param <code>String</code>
     */
    public void setActionName(String a) {
	this.actionName = a;
    }

    /**
     * Get ipAddress
     * @return String
     */
    public String getIpAddress() {
	return ipAddress;
    }

    /**
     * Set ipAddress
     * @param <code>String</code>
     */
    public void setIpAddress(String i) {
	this.ipAddress = i;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

	// Reset values are provided as samples only. Change as appropriate.

	dateLeft = null;
	dateRight = null;
	idUser = null;
	actionName = null;
	ipAddress = null;

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
}
