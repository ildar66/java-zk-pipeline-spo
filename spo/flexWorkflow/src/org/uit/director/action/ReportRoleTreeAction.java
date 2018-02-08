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
 * Action для вывода отчета "Перечень недоставленных в ГО документов, прикрепленных в филиалах"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-430
 *
 */
public class ReportRoleTreeAction extends Action {

    private static final Logger logger = Logger.getLogger(ReportOpportunityFilterAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Вызов отчета 'Перечень недоставленных в ГО документов, прикрепленных в филиалах'");
        ActionForward forward;
        try {
        	/* МК, 02 ноября 2009. Теперь отчет доступен не только администратору.
        	//открыть доступ только администатору
            forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;
        	 */
        	ReportActionUtils.setProcess(request, false);
        	
        	// Сформируем список департаментов для фильтрации
        	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);        	
        	if (wsc.isAdmin()) // Покажем администратору ВСЕ подразделения
            	ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, true, false);
            else
                /* здесь 'Все подразделения' означают получение данных не по самому подразделению, а по этому и по всем подчиненным подразделениям*/
                ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_SUBORDINATE, true, true);

        	// Покажем только дочерние департаменты и департамент самого пользователя.
            //ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_SUBORDINATE, false, true);

            
            ReportActionUtils.setFileReportInRequest(request, "Audit/role_tree.rptdesign");
            forward = mapping.findForward("success");
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));
            logger.log(Level.WARNING, "Ошибка при формировании отчета иерархии ролей", e);
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;

    }
}
