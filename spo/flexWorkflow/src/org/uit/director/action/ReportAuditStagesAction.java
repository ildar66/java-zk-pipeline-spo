package org.uit.director.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.utils.ReportActionUtils;

import ru.md.helper.TaskHelper;

/**
 * @author Andrey Pavlenko
 * Action для Отчет по заявке Аудит прохождения заявки
 */
public class ReportAuditStagesAction extends Action {
    private static final Logger logger = Logger.getLogger(ReportAuditStagesAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ActionForward forward = null;
        logger.info("Вызов отчета 'Аудит прохождения этапов'");
        if(!TaskHelper.getCurrentUser(request).hasRole(null, "Аудитор ДКАБ")){
        	ActionErrors errors = new ActionErrors();
            errors.add("Для этого отчёта необходима роль 'Аудитор ДКАБ'", new ActionError("error.message"));            
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
            WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
            wsc.setErrorMessage("Для этого отчёта необходима роль 'Аудитор ДКАБ'");
            return forward;
        }
        try {
        	ReportActionUtils.setProcess(request, false);
        	ReportActionUtils.setFileReportInRequest(request, "audit_dur_stages");
            forward = mapping.findForward("success");      	
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Ошибка при формировании отчета 'Аудит прохождения этапов'", e);
            e.printStackTrace();
        	
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));            
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;
    }
}
