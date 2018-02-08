/**
 *  Created by Struts Assistant.
 *  Date: 19.06.2006  Time: 04:44:55
 */

package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.WorkflowUser;

public class AddWorkflowUserAction extends org.apache.struts.action.Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String target = "errorPage";
		String idUser = request.getParameter("idUser");		
		/*String login = request.getParameter("login");
		String family = request.getParameter("family");
		String name = request.getParameter("name");
		String patronic = request.getParameter("patronic");
		Long idDepartment = Long.valueOf(request.getParameter("idDepartment"));	*/			
		String addRoleUser = request.getParameter("addRoleUser");
		String typeProc = request.getParameter("typeProc");

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));
	
		try {

			if (typeProc.equals("-")) {
				wsc.setErrorMessage("Необходимо задать тип процесса.");
				target = "errorPage";
			} else {
				if (idUser.equals("-")){
					wsc.setErrorMessage("Необходимо выбрать пользователя.");
					target = "errorPage";
				}
				else
				if (wsc.isUserAdmin(Integer.parseInt(typeProc))) {

					Long idUserL = Long.valueOf(idUser);
					WorkflowUser mapUser = WPC.getInstance().getUsersMgr()
							.getInfoUserByIdUser(idUserL);

					if (mapUser == null) {
						wsc
								.setErrorMessage("Пользователь '"
										+ idUser
										+ "' отсутствует в системе управления пользователями");
						target = "errorPage";

					} else {

						if (addRoleUser.equals("-")) {
							wsc
									.setErrorMessage("Необходимо задать роль пользователя.");
							target = "errorPage";
						} else {
							
							String res = wsc.getDbManager().getDbFlexDirector()
									.addWorkflowUser(idUserL, addRoleUser,
											wsc.getIdUser(),
											request.getRemoteAddr());
							if (res.equals("ok"))
								WPC.getInstance().reloadStaticTables(
										wsc.getDbManager().getDbFlexDirector());
							target = "directionUsers";
						}
					}
				} else {
					wsc.setErrorMessage("Нет прав на выполнение операции.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка добавления пользователя");
			target = "errorPage";

		}

		return mapping.findForward(target);
	}
}
