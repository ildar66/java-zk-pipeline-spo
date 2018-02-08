package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;

/**
 * @version 	1.0
 * @author
 */
public class AddSpetialReportAction extends Action

{

    public ActionForward execute(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	ActionErrors errors = new ActionErrors();
	ActionForward forward = new ActionForward(); // return value
	
	String nameReport = request.getParameter("nameReport");
	String classReport = request.getParameter("classReport");
	String descriptionReport = request.getParameter("descriptionReport");
	String idTypeProcess = request.getParameter("typeProc");
	
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
    if (wsc.isNewContext()) return (mapping.findForward("start"));

	try {		
		wsc.getDbManager().getDbFlexDirector().addSpetialReport(idTypeProcess,nameReport,classReport,descriptionReport);
		WPC.getInstance().reload(wsc.getDbManager().getDbFlexDirector());
		forward = (mapping.findForward("analysePage"));
	} catch (Exception e) {

		forward = mapping.findForward("start");
	    errors.add("name", new ActionError("id"));

	}

	// If a message is required, save the specified key(s)
	// into the request for use by the <struts:errors> tag.

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);

	    // Forward control to the appropriate 'failure' URI (change name as desired)
	    //	forward = mapping.findForward("failure");

	} else {

	    // Forward control to the appropriate 'success' URI (change name as desired)
	    // forward = mapping.findForward("success");

	}

	// Finish with
	return (forward);

    }
}
