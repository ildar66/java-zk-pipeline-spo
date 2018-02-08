/**
 * 
 */
package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Акцепт операции
 * 
 * @author imatushak@masterdm.ru
 * 
 */
public class ApproveAcceptAction extends Action {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.struts.action.Action#execute(org.apache.struts.action.
     * ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String taskId = request.getParameter("taskId");
        request.setAttribute("taskId", Long.parseLong(taskId));
        request.setAttribute("approved", Boolean.TRUE);
        return (mapping.findForward("assignAccept"));

    }

}
