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

import com.vtb.util.ApplProperties;
import com.vtb.value.BeanKeys;

/**
 * Action для вывода отчета "Переменные к операции"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-414
 * доработан Кузнецовым Михаилом
 */
public class ReportRolesReportAction extends Action {

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
            //установить текущий процесс и все остальные процессы
            ReportActionUtils.setProcess(request, false);

            long processId = ReportActionUtils.setCurrentProcess(request);

            //установить все стадии текущего процесса
            ReportActionUtils.setStagesByProcessId(request, processId);

            String path = "file:///" + ApplProperties.getReportsPath().replace('\\', '/');
            String sFile = path + "Audit/variablesByOperation.rptdesign";
            request.setAttribute(BeanKeys.REPORT_FILTER_FILE, sFile);

            logger.info("подготовлены параметры для формы по отчету");

            forward = mapping.findForward("success");
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));
            logger.log(Level.WARNING, "Ошибка при формировании представления для отчета ", e);
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;

    }
}
