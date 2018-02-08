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
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.utils.ReportActionUtils;

/**
 * @author Michael Kuznetsov
 * Action для Отказ от выполнения операции, VTBSPO-595
 */
public class ReportActiveStagesAction extends Action {
    private static final Logger logger = Logger.getLogger(ReportActiveStagesAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Вызов отчета 'активные задачи'");
        ActionForward forward = null;
        try {
        	// Сформируем список процессов для фильтрации
        	ReportActionUtils.setProcess(request, true);

        	// Сформируем список департаментов для фильтрации
        	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);        	
        	if (wsc.isAdmin()) // Покажем администратору ВСЕ подразделения
            	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, false, false);
            else
	        	// Покажем только дочерние департаменты и департамент самого пользователя.
	        	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_SUBORDINATE, false, true);

        	ReportActionUtils.setFileReportInRequest(request, "Audit/active_stages.rptdesign");
            forward = mapping.findForward("success");      	
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Ошибка при формировании отчета 'активные задачи'", e);
            e.printStackTrace();
        	
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));            
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;
         
    }	
}
