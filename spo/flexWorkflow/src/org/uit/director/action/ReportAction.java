package org.uit.director.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;
import org.uit.director.report.mainreports.HistoryReport;
import org.uit.director.report.mainreports.SearchProcessReport;
import org.uit.director.utils.ReportActionUtils;

import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

@SuppressWarnings("unchecked")
public class ReportAction extends Action {
	private static final Logger LOGGER = Logger.getLogger(ReportAction.class.getName());
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        String target = "reportPage";

        //Если отчет называется "редактирование процесса", то дать доступ только администратору.
        String classReportStr = request.getParameter("classReport");
        
        LOGGER.info("classReport is '" + classReportStr + "'");
        
        if (classReportStr.equalsIgnoreCase(SearchProcessReport.class.getName())) {
        	String editparameter = request.getParameter("par1");
        	
        	LOGGER.info(SearchProcessReport.class.getSimpleName() + " has editparameter with value '" + editparameter + "'");
        	
        	if ((editparameter != null) && (editparameter.equalsIgnoreCase("edit")))  {
        		//открыть доступ только администатору
                ActionForward forward = ReportActionUtils.accessForAdmin(request, mapping);
                if (forward != null)
                    return forward;		
        	}       	
        }

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));
        try {
            List params = new ArrayList();
            boolean isEditMode = false;
            boolean isGenReport = false;

            // условие создания экземпляра отчета
            if (classReportStr != null) {
            	if (classReportStr.startsWith("../report")) {
            		classReportStr = classReportStr.replaceAll("!", String.valueOf(wsc.getIdUser()));
            		classReportStr = classReportStr.replaceAll(",", "&");
            		response.sendRedirect(classReportStr);
            		return null;
            	}
                Class classreport = Class.forName(classReportStr);
                WorkflowReport reportInterface;
                reportInterface = (WorkflowReport) classreport.newInstance();

                String param;

                if (request.getParameter("genReport") != null) {
                    isGenReport = true;
                }
                
                LOGGER.info("generate report: " + isGenReport);
                
                int numb = 1;
                while ((param = request.getParameter("par" + numb++)) != null) {

                	LOGGER.info("receive param 'par" + numb + "' with value '" + param + "'");
                	
                    params.add(param);
                    if (param.equalsIgnoreCase("edit")) isEditMode = true;

                }
                
                if (classReportStr.equalsIgnoreCase(HistoryReport.class.getName())) {
                	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
                	Task task = processor.findByPupID(Long.valueOf((String)params.get(0)), false);
                	
                	LOGGER.info("found task with PUP id '" + (String)params.get(0) + "'");
                	
                	params.add(task.getNumberDisplay());
                	params.add(task.getHeader().getNumber() == null ? "" : task.getHeader().getNumber().toString());
                	LOGGER.info("set as report parameter task display number '" + task.getNumberDisplay() + "'");
                	
                	
                	if (request.getParameter("menuOff") != null)
                		request.setAttribute("menuOff", "menuOff");
                }
                
                reportInterface.init(wsc, params);
                wsc.setReport(reportInterface);

            }

            //  Условие генерации отчета по уже существующему экземпляру отчета
            if ((classReportStr == null) || (!isEditMode && classReportStr != null && params.size() > 0) || isGenReport) {

                WorkflowReport reportInstance = wsc.getReport();
                List comps = reportInstance.getComponentList();
                if (comps != null) {
                    for (int i = 0; i < comps.size(); i++) {
                        ComponentReport comp = (ComponentReport) comps.get(i);
                        String type = comp.getType();
                        String name = comp.getDescription();
                        String value = request.getParameter(name.replaceAll(" ", "_"));

                        if (type.equalsIgnoreCase("select")) {

                            List types = (List) comp.getValue();
                            if (types == null) {

                                types = new ArrayList();
                                Map map = new HashMap();
                                map.put(value, name);
                                types.add(map);
                                comp.setValue(types);

                            } else {
                                for (int j = 1; j < types.size(); j++) {
                                    Map map = (Map) types.get(j);

                                    String idTypeMap = (String) map.keySet().iterator().next();
                                    if (idTypeMap.equals(value)) {
                                        comp.setIndexSelect(j);
                                        break;
                                    }
                                }
                            }


                        } else if (type.equalsIgnoreCase("check")) {

                            if (value == null) comp.setValue(Boolean.FALSE);
                            else
                                comp.setValue(Boolean.TRUE);

                        } else if (type.equalsIgnoreCase("period")) {
                            String leftDate = request.getParameter("leftDate");
                            String rightDate = request.getParameter("rightDate");
                            List val = (List) comp.getValue();
                            val.set(0, leftDate);
                            val.set(1, rightDate);

                        } else if (!type.equalsIgnoreCase("script")) {
                            comp.setValue(value);
                        }
                    }
                }

                reportInstance.generateReport();


            }


        } catch (Exception e) {
        	LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            
            wsc.setErrorMessage("Ошибка выполнения действия (" + e.getMessage() + ")");
            target = "errorPage";

        }


        return (mapping.findForward(target));
    }
}