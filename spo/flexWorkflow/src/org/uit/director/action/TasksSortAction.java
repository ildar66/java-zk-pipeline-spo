package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 10.12.2004
 * Time: 13:26:40
 * To change this template use File | Settings | File Templates.
 */
public class TasksSortAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String target = "pageNewTasks";
        String param = request.getParameter("paramSort");
        String isAccept = request.getParameter("isAccept");


        if (isAccept != null)
            if (isAccept.equals("true")) target = "pageAcceptedTasks";

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));

        try {

            if (param != null) {
                wsc.getTaskList().sortByParam(param);
                wsc.setSortState(param);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return (mapping.findForward(target));
    }


}
