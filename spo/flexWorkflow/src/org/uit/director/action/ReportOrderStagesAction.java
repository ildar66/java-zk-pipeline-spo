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
 * @author Sergey Melnikov
 * Action для Отчет по заявке, VTBSPO-462, VTBSPO-473
 */
public class ReportOrderStagesAction extends Action {
    private static final Logger logger = Logger.getLogger(ReportOrderStagesAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Вызов отчета 'журнал прохождения сделок'");
        ActionForward forward = null;
        try {        	
        	ReportActionUtils.setFileReportInRequest(request, "Audit/order_stages.rptdesign");
            forward = mapping.findForward("success");
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Ошибка при формировании отчета 'журнал прохождения сделок'", e);
            e.printStackTrace();
        	
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));            
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
        }
        return forward;         
    }	
}
