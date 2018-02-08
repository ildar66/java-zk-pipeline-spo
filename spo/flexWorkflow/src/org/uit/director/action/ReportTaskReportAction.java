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

import com.vtb.util.ApplProperties;
import com.vtb.value.BeanKeys;

/**
 * Action для вывода отчета "Пользователи филиала с определенной ролью"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-384
 *
 */
public class ReportTaskReportAction extends Action {

    private static final Logger logger = Logger.getLogger(ReportOpportunityFilterAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        logger.info("формирование формы для вызова отчета");
        try {
            //открыть доступ только администатору
        	/*
            forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;            
            */
        	
        	// Сформируем список департаментов для фильтрации
        	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);        	
        	if (wsc.isAdmin()) // Покажем администратору ВСЕ подразделения
            	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, true, false);
            else
	        	// Покажем только дочерние департаменты и департамент самого пользователя.
	        	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_SUBORDINATE, false, true);
            
            String path = "file:///" + ApplProperties.getReportsPath().replace('\\', '/');
            String sFile = path + "Audit/task_in_work.rptdesign";
            request.setAttribute(BeanKeys.REPORT_FILTER_FILE, sFile);

            String sFile2 = path + "Audit/appointed_tasks.rptdesign";
            request.setAttribute(BeanKeys.REPORT_FILTER_FILE2, sFile2);

            String sFile3 = path + "Audit/new_tasks.rptdesign";
            request.setAttribute(BeanKeys.REPORT_FILTER_FILE3, sFile3);

            ReportActionUtils.setCurrentUserInRequest(request);

            logger.info("подготовлены параметры для формы по отчету");

            //в зависимости от принадлежности пользования ГО-отделению выбирается jsp-страница 
            forward = new ActionForward();
            forward = mapping.findForward("success");
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));
            logger.log(Level.WARNING, "Ошибка при формировании представления для отчета по заявкам", e);
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;

    }
}
