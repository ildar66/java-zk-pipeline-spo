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


public class GoBackAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

    	String target = "acceptedTasks";

		String idTask = request.getParameter("idTask");
		String idStageRedirect = request.getParameter("idStageRedirect");
		System.out.println("idTask="+idTask+"; idStageRedirect="+idStageRedirect);

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {
				if (idTask != null && idStageRedirect != null && !idTask.equals("") && !idStageRedirect.equals("")) {
					String res = wsc.getDbManager().getDbFlexDirector()
					.backWorkToStage(Long.parseLong(idTask), Long.parseLong(idStageRedirect));
					if (res.equalsIgnoreCase("error")) {
						wsc.setErrorMessage("Ошибка перенаправления задания.");
						return (mapping.findForward("errorPage"));
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка отката задания.");
			target = "errorPage";
		}
		return (mapping.findForward(target));

    }
}
