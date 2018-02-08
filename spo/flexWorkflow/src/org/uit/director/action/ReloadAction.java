package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import ru.masterdm.spo.utils.CollectStages;

public class ReloadAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "start";
		String isFromCheckWholeness = request
				.getParameter("isFromCheckWholeness");

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		if (!wsc.isAdmin()) {
			wsc.setErrorMessage("Нет прав администратора");
			target = "errorPage";
			//            return mapping.findForward(target);
		} else {

			WPC.getInstance().reload(wsc.getDbManager().getDbFlexDirector());
			if (wsc.getCacheManager() != null)
				wsc.getCacheManager().deleteAllCach();

			if (wsc.getTaskList() != null)
				wsc.getTaskList().clear();

			if (isFromCheckWholeness != null)
				if (isFromCheckWholeness.equals("true")) {
					response
							.sendRedirect("checkWholeness.do?isFromReload=true");
					return null;
				}
		}

		AbstractAction.resetWorkflowSessionContext(request);
		CollectStages.singleton().reset();
		return (mapping.findForward(target));
	}
}