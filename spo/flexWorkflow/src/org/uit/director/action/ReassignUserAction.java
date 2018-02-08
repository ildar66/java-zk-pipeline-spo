package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

public class ReassignUserAction extends Action {

	@Override
	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String target = "errorPage";
		String idUserS = request.getParameter("idUser");
		
		String idAssign = request.getParameter("idAssign");
		String mayReassign = request.getParameter("mayReassign");

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);		
		if (wsc.isNewContext())
			return (actionMapping.findForward("start"));

		try {

			if (idUserS != null && !idUserS.equals("")) {

				Long idUserL = Long.valueOf(idUserS);
				String res = wsc.getDbManager().getDbFlexDirector()
						.reassignUser(idUserL, Integer.valueOf(mayReassign),
								Long.valueOf(idAssign), wsc.getIdUser(), request.getRemoteAddr(), "");
				if (res.equalsIgnoreCase("ok")) {
					response.sendRedirect("reassignList.jsp");
					return null;
				} else {
					wsc.setErrorMessage("Ошибка перенаправления задания");
					return actionMapping.findForward(target);
				}
			} else {
				wsc
						.setErrorMessage("Укажите кому необходимо назначить задание");
				return actionMapping.findForward(target);
			}

		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка переопределения исполнителя");
			return actionMapping.findForward(target);
		}

	}

}
