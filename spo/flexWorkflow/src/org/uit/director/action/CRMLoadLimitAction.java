package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

public class CRMLoadLimitAction extends Action {
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception { 
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        try {
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            wsc.setPageData("Лимит успешно загружен. Создана заявка номер "+processor.limitLoad(request.getParameter("id")));
            return mapping.findForward("textPage");
        } catch (Exception e) {
            wsc.setErrorMessage(e.getMessage());
            return mapping.findForward("errorPage");
        }
    }
}
