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
import org.uit.director.utils.ReportActionUtils;


/**
 * Action для вывода отчета "Пользователи филиала с определенной ролью"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-415
 *
 */
public class ReportAttributeTreeReportAction extends Action {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        logger.info("формирование формы для вызова отчета");
        try {
        	/* МК, 02 ноября 2009. Теперь отчет доступен не только администратору.
        	//открыть доступ только администатору
            forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;
			*/
            ReportActionUtils.setProcess(request, false);
            
            ReportActionUtils.setFileReportInRequest(request, "Audit/attribute_tree.rptdesign");
            logger.info("подготовлены параметры для формы по отчету");

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
