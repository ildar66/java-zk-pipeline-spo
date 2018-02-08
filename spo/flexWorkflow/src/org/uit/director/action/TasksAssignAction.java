package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.AssignTasksList;

public class TasksAssignAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String target = "pageAssignTasks";

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));

        AssignTasksList assignList = new AssignTasksList();
    	assignList.init(wsc);
    	
        String res;
    	res = assignList.execute();
    	
    	if (res.equalsIgnoreCase("Error")) {
            wsc.setErrorMessage("Ошибка получения списка назначенных заданий на исполнение, повторите попытку позже.");
            target = "errorPage";

        } else {
            wsc.setAssignList(assignList);
        }
    	return mapping.findForward(target);
    	
}

}
