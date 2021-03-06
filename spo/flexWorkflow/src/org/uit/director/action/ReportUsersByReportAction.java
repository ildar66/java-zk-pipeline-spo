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
 * Action для вывода отчета "Пользователи филиала с определенной ролью"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-384
 *
 */
public class ReportUsersByReportAction extends Action {

    private static final Logger logger = Logger.getLogger(ReportOpportunityFilterAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        logger.info("формирование формы для вызова отчета 'Роли пользователя'");
        try {          
        	
            /* forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;
            */
            forward = new ActionForward(); // return value

            WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
            
            //установить текущий процесс и все остальные процессы
            ReportActionUtils.setProcess(request, false);

            long processId = ReportActionUtils.setCurrentProcess(request);

            ReportActionUtils.setRolesByProcess(request, processId);
            
            if (wsc.isAdmin()) // Покажем администратору ВСЕ подразделения
            	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, true, false);
            else
            	/* здесь 'Все подразделения' означают получение данных не по самому подразделению, а по этому и по всем подчиненным подразделениям*/
                ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_SUBORDINATE, true, true);
            
            ReportActionUtils.setFileReportInRequest(request, "Audit/user_by_role.rptdesign");

            logger.info("подготовлены параметры для формы по отчету 'Роли пользователя'");

            //в зависимости от принадлежности пользования ГО-отделению выбирается jsp-страница
            forward = mapping.findForward("success");
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));
            logger.log(Level.WARNING, "Ошибка при формировании представления для отчета 'Роли пользователя'", e);
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;

    }
}
