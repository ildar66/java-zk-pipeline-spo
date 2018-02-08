package org.uit.director.action;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.utils.ReportActionUtils;

import com.vtb.util.ApplProperties;
import com.vtb.value.BeanKeys;

/**
 * Action для вывода отчета "Перечень недоставленных в ГО документов, прикрепленных в филиалах"
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-384
  * Доработано Михаилом Кузнецовым
 */
public class ReportNewDocumentsForGOReportOrgsAction extends Action {

    private static final Logger logger = Logger.getLogger(ReportOpportunityFilterAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Вызов отчета 'Перечень недоставленных в ГО документов, прикрепленных в филиалах, по организациям'");
        
        //открыть доступ только администатору
        ActionForward forward = ReportActionUtils.accessForAdmin(request, mapping);
        if (forward != null)
            return forward;            
        
        // Покажем администратору ВСЕ подразделения
        ReportActionUtils.setDepartmentsForCurrentUser(request, WPC.INCLUDE_ALL, true, false);        

        ReportActionUtils.setPeriodInRequest(request);
        
        String path = "file:///" + ApplProperties.getReportsPath().replace('\\', '/');
        
        String sFile = path + "Audit/new_documents_for_go_for_organizations.rptdesign";
        request.setAttribute(BeanKeys.REPORT_FILTER_FILE, sFile);
        
        //в зависимости от принадлежности пользования ГО-отделению выбирается jsp-страница
        return mapping.findForward("success");
    }
}
