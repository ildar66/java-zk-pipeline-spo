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
import org.uit.director.utils.ReportActionUtils;

/**
 * Action для формирования формы для отчета по заявкам
 * @author Какунин Константин Юрьевич
 * (создано для bug VTBSPO-328)
 *
 */
public class ReportOpportunityFilterAction extends Action {

    private static final Logger logger = Logger.getLogger(ReportOpportunityFilterAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        logger.info("формирование формы для вызова отчета");
        try {
            //открыть доступ только администатору
            forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;            

            forward = new ActionForward(); // return value

            // Покажем администратору ВСЕ подразделения
            ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, true, false);
            ReportActionUtils.setPeriodInRequest(request);
            
            ReportActionUtils.setFileReportInRequest(request, "Audit/orderReport.rptdesign");
            logger.info("подготовлены параметры для формы по отчету");
            
            
            //в зависимости от принадлежности пользования ГО-отделению выбирается jsp-страница
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
