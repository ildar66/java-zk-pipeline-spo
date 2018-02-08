/**
 *  Created by Struts Assistant.
 *  Date: 22.06.2006  Time: 03:41:30
 */

package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

public class RedirectTasksAction extends org.apache.struts.action.Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String target = "redirectTasks";
		String idUserFrom = request.getParameter("idUserFrom");
		String idUserTo = request.getParameter("idUserTo");
		String idTypeProcess = request.getParameter("typeProc");

		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {

			if ((idUserFrom != null && idUserTo != null) && !idUserTo.equals("-") && !idUserFrom.equals("-")
				&& !idUserTo.equals(idUserFrom) 
				&& (idUserTo != null) && !idTypeProcess.equals("-")) {

				//if (wsc.isUserAdmin(Long.valueOf(idUserFrom)) && wsc.isUserAdmin(Long.valueOf(idUserTo))) {

				String res = wsc.getDbManager().getDbFlexDirector()
						.redirectWorks(Long.valueOf(idUserFrom),
								Long.valueOf(idUserTo), idTypeProcess,
								wsc.getIdUser(), request.getRemoteAddr());
				if (res.equalsIgnoreCase("ok")) {
					wsc.setWarningMessage("Задания пользователя '"
							+ idUserFrom
							+ "' успешно перенаправлены пользователю '"
							+ idUserTo + "'");
				} else {
					wsc.setErrorMessage("Ошибка перенаправления задания");
				}

				//} else {
					//wsc.setErrorMessage("Нет прав на выполнение операции.");
					//target = "redirectTasks";
				//}

			} else 
			if (idUserFrom == null && idUserTo == null) {										
				// при загрузке страницы. Пользователь еще ничего не выбрал
				target = "redirectTasks";
			}
			else
			if (idUserFrom == null || idUserTo == null || idUserFrom.equals("-") || idUserTo.equals("-")) {
				wsc.setErrorMessage("Укажите, от кого и кому необходимо перенаправить задания");
				target = "errorPage";
			} 
			else 
			if (idUserFrom.equals(idUserTo)) {
				wsc.setErrorMessage("Пользователь не может переназначать задания самому себе");
				target = "errorPage";
			}
			else 
			if (idTypeProcess == null || idTypeProcess.equals("-")) {
				wsc.setErrorMessage("Выберите тип процесса, задачи по которому Вы хотите перенаправить");
				target = "errorPage";
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка перенаправления задания");
			target = "errorPage";

		}

		return mapping.findForward(target);
	}
}