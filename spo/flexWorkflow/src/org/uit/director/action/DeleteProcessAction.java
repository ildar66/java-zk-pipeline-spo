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

public class DeleteProcessAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "reportPage";

		String idProcess = request.getParameter("idProcess");
		String sign = request.getParameter("sign");

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {

			if (idProcess != null && !idProcess.equals("")) {
				String res = wsc.getDbManager().getDbFlexDirector()
						.deleteProcess(Long.valueOf(idProcess),
								wsc.getIdUser(), request.getRemoteAddr(), sign);
				wsc.getReport().generateReport();
				if (res.equalsIgnoreCase("error")) {
					wsc.setErrorMessage("Ошибка при удалении процесса");
					target = "errorPage";
				}
			} else {

				wsc.setErrorMessage("Неверный параметр.");
				target = "errorPage";
				return (mapping.findForward(target));
			}

		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка выполнения операции.");
			target = "errorPage";

		}

		return (mapping.findForward(target));
	}
}
