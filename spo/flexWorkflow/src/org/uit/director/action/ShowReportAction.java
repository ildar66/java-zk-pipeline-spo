package org.uit.director.action;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.spo.util.Config;

import com.vtb.domain.ReportTemplate;
import com.vtb.domain.ReportTemplate.ReportTemplateTypeEnum;
import com.vtb.exception.MappingException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.ReportBuilderActionProcessor;
import com.vtb.system.AppService;
import com.vtb.value.BeanKeys;

/**
 * Класс для формирования списка печатных форм
 * @author Какунин Константин Юрьевич, Kuznetsov Michael 
 *
 */
public class ShowReportAction extends Action {
	private static Logger logger = Logger.getLogger(ShowReportAction.class.getName());
	
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        ActionForward forward = null;
        try {
			request.setAttribute(BeanKeys.REPORT_PRINT_URLS, getUrl(new Long(request.getParameter("mdtaskid"))));
            forward = mapping.findForward("success");
        } catch (Throwable e) {
            AppService.error("Ошибка при формировании представления для печатной формы", e);
        }
        if (forward == null) {
            forward = mapping.findForward("error");
        }
        return forward;
    }

	public static Map<String, String> getUrl(Long mdtaskId) {
		//список url, для отображения в jsp-странице
		Map<String, String> urls = new LinkedHashMap<String, String>();
		//вытащим id процесса для передачи в отчет
		
		// get NEW Reports from report_template table
		ReportBuilderActionProcessor reportBuilder = (ReportBuilderActionProcessor) ActionProcessorFactory.getActionProcessor("ReportBuilder");        	  
		List<ReportTemplate> reportTemplates = null; 
		try {
			reportTemplates = reportBuilder.findByType(ReportTemplateTypeEnum.PRINT_DOC_REPORT_TYPE.getValue());
			Collections.sort(reportTemplates, new Comparator<ReportTemplate>() {
			    public int compare(ReportTemplate d1, ReportTemplate d2) {
		            return d1.getName().compareTo(d2.getName());
		        }
			});
			for (ReportTemplate report : reportTemplates) {
				if ("signature_report".equalsIgnoreCase(report.getFilename())) {
					reportTemplates.remove(report);
					break;
				}
			}
		} catch (MappingException e) {
			logger.severe("get list of reportTemplates:" + e.getMessage());
		}
		
		/* add reports to list of URLs*/
		if (reportTemplates != null)
			for (ReportTemplate r : reportTemplates) {
		        String path = formatUrlForReport(r, mdtaskId);
		        if (path != null) urls.put(path, r.getName());
		    }
		return urls;
	}

    /**
     * Получить URL для отчета 
     * @param r информация о печатной форме
     * @param mdtaskId -- mdtask id для заявки (лимита \ сделки \ сублимита)
     * @return строка url
     */
    private static String formatUrlForReport(ReportTemplate r, Long mdtaskId) {
        final String reportFormat = Config.getProperty("REPORT_FORMAT_DEFAULT");
        StringBuilder url = new StringBuilder();
        url.append("reportPrintFormRenderAction.do?__report=").append(r.getFilename());
        url.append("&mdtaskId=").append( mdtaskId);
        url.append("&__reportType=").append(r.getType());
        url.append("&__format=").append(reportFormat);
        //logger.info("Report attachments url: " + url.toString());        
        return url.toString();
    }    
}
