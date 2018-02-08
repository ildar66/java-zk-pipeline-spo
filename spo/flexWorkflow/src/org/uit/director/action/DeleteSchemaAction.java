package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

/**
 * @version 	1.0
 * @author
 */
public class DeleteSchemaAction extends Action

{

    public ActionForward execute(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	String result = "target";
	ActionForward forward = new ActionForward(); // return value

	String idTypeProcess = request.getParameter("typeProcess");
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	
	try {
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		if (!wsc.isAdmin()) {
			wsc.setErrorMessage("Нет прав администратора");
			return mapping.findForward("errorPage");
		}
		
		if (idTypeProcess != null && !idTypeProcess.equals("")) {
			String res = wsc.getDbManager().getDbFlexDirector().deleteSchema(idTypeProcess);
			if (!res.equals("ok")) {
				wsc.setErrorMessage("Ошибка при удалении схемы процесса: " + res);
				result = "errorPage";
				
			} else {
				wsc.setPageData("Схема бизнес-процесса успешно удалена, для обновления данных необходимо выполнить перезагрузку системы (доступно на вкладке Управление)");
				result = "textPage";
			}
		}
		

	} catch (Exception e) {

		wsc.setErrorMessage("Ошибка при удалении схемы процесса: " + e.getMessage());
		result = "errorPage";
	}	
	// Write logic determining how the user should be forwarded.
	forward = mapping.findForward(result);

	// Finish with
	return (forward);

    }
}
