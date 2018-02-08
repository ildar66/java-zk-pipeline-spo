package org.uit.director.action;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.spo.service.IReporterService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.spo.ejb.ReportBeanLocal;
import ru.md.spo.report.TaskReport;
import ru.md.spo.report.User;

import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.ReportBuilderActionProcessor;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import com.vtb.report.renderer.ReportRenderer;

/**
 * Action для вывода отчета во фрейме
 * @author Какунин Константин Юрьевич, Kuznetsov Michael
 * (создано для bug VTBSPO-328)
 *
 */
public class ReportPrintFormRenderAction extends Action {
    private static final Logger logger = Logger.getLogger(ReportPrintFormRenderAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        try {
        	// get report name and get report.
        	Map<String, String[]> parameterMap = request.getParameterMap();
        	String[] reportParam = parameterMap.get(ReportTemplateParams.REPORT_MARK.getValue());
        	String reportName = null; ReportRenderer renderer = null;
        	if (reportParam != null) reportName = reportParam[0];      	

        	String[] reportingEngineParam = parameterMap.get(ReportTemplateParams.REPORTING_ENGINE.getValue());

        	if (reportingEngineParam != null && reportingEngineParam.length > 0) {
        		if (reportingEngineParam[0].toLowerCase().equals("true")) {
        			//формирование отчёта с помощью движка Aspose - ReportingEngine
        			executeWithReportingEngine(parameterMap, response);
        			return null;
        		}
        	}
        	
        	ReportBuilderActionProcessor reportBuilder = (ReportBuilderActionProcessor) ActionProcessorFactory.getActionProcessor("ReportBuilder");        	  
            renderer = reportBuilder.getReport(reportName, request.getParameterMap()); 
            // render the report
           	renderer.render(request, response);
            // forward is completed now. Just show the output.
            return null;
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            String message = e.getMessage();
            if (message == null && e.getCause() != null)
            	message = e.getCause().toString();
            errors.add(message, new ActionError("error.message"));
            saveErrors(request, errors);
            forward = mapping.findForward("errorPage");
            WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
            wsc.setErrorMessage(message);
            return forward;    
        }
    }

	private void executeWithReportingEngine(Map<String, String[]> parameterMap, HttpServletResponse response) throws Exception {
		IReporterService reporterService = SBeanLocator.singleton().getReporterService();
		ReportBeanLocal reportFacade = com.vtb.util.EjbLocator.getInstance().getReference(ReportBeanLocal.class);
    	String[] mdtaskIds = parameterMap.get(ReportTemplateParams.MDTASK_ID.getValue());
    	Long mdTaskId = -1L;
    	if (mdtaskIds != null && mdtaskIds.length > 0 && mdtaskIds[0] != null)
    		mdTaskId = Long.parseLong(mdtaskIds[0]);
    	String[] reportNames = parameterMap.get(ReportTemplateParams.REPORT_MARK.getValue());
    	String reportKey = null;
    	if (reportNames != null && reportNames.length > 0)
    		reportKey = reportNames[0];      	

    	TaskReport dataSource = reportFacade.getTaskReport(mdTaskId);
    	
    	//List<Class<?>> classes = new ArrayList<Class<?>>();
    	//classes.add(ru.md.spo.report.User.class);
        //dataSource.clientManager = new User("Имя", "Подразделение");

        byte[] report = reporterService.buidWordReport(reportKey, dataSource);
		
    	response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		response.setHeader("Content-Disposition","attachment; filename=report.docx");
		OutputStream os = response.getOutputStream();
		if (report != null)
			os.write(report);
    }
}
