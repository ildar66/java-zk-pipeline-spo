package org.uit.director.action;

import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

/**
 * @version 1.0
 * @author
 */
public class CommitUploadAction extends Action

{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value

		String target = "errorPage";
		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {
			String commit = request.getParameter("commit");

			if (commit != null && commit.equalsIgnoreCase("true")) {

				wsc.beginUserTransaction();

				String resultLoad = UploadProcessAction
						.loadProcessZipPacket(wsc, (ZipFile) request
								.getSession().getAttribute("zipfile"), true,
								request.getRemoteAddr());

				if (resultLoad.startsWith("ok")) {
					target = "textPage";
					wsc.setPageData("Обновление процесса прошло успешно.");
					wsc.commitUserTransaction();
				} else {

					target = "errorPage";
					wsc.setErrorMessage(resultLoad);
					wsc.rollBackUserTransaction();
				}

			} else {
				target = "errorPage";
				wsc.setErrorMessage("Действие отменено пользователем.");
			}

		} catch (Exception e) {

			errors.add("name", new ActionError("id"));
			wsc.rollBackUserTransaction();

		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);

		} else {

			// Forward control to the appropriate 'success' URI (change name as
			// desired)
			// forward = mapping.findForward("success");

		}

		// Finish with
		return mapping.findForward(target);

	}
}
