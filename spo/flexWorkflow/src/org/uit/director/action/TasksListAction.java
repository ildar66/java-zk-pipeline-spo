package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WorkflowSessionContext;

import com.vtb.domain.ProcessSearchParam;

public class TasksListAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksListAction.class.getName());
    
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		long tstart = System.currentTimeMillis();
    	request.setAttribute("startTime", tstart);
	    WorkflowSessionContext wsc;
		try {
            wsc = AbstractAction.getWorkflowSessionContext(request);
            if(wsc.emptyRoles()){
            	wsc.setErrorMessage("Вы не назначены ни на одну роль");
            	return mapping.findForward("errorPage");
            }
        } catch (Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(e.getMessage(), new ActionError("error.message"));
            LOGGER.warn("Ошибка", e);
            saveErrors(request, errors);
            return mapping.findForward("errorPage");
        }
		String target = "pageNewTasks";
		
		String idDepartmentStr = request.getParameter("idDepartment");
		Integer idDepartment = null;
		if (idDepartmentStr != null && !idDepartmentStr.equalsIgnoreCase("")) {
			try {
				idDepartment = Integer.valueOf(idDepartmentStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
		
		request.setAttribute("idDepartment", idDepartment);
		
		String stTypeList = request.getParameter("typeList");
		
		String filterNameTypeProcess = request.getParameter("filterNameTypeProcess");
		if (filterNameTypeProcess == null || filterNameTypeProcess.trim().equals("")) filterNameTypeProcess = "NULL_String";

		String filterNameStage = request.getParameter("filterNameStage");
		if (filterNameStage == null || filterNameStage.trim().equals("")) filterNameStage = "NULL_String";

		String filterAtribute = request.getParameter("filterAtribute");
		if (filterAtribute == null || filterAtribute.trim().equals("")) filterAtribute = "NULL_String";
	
		try {

			if (stTypeList.equals("noAccept")) target = "pageNewTasks";
			if (stTypeList.equals("accept")) {target = "pageAcceptedTasks"; }
			if (stTypeList.equals("perform")) {target = "pagePerformTasks"; }
			if (stTypeList.equals("all")) target = "pageProcessList";
			
			//сохраняет куки
			ProcessSearchParam p = new ProcessSearchParam(request,request.getParameter("closed")!=null);
			p.saveCookies(response);
		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка получения списка заданий");
			target = "errorPage";
		}
		Long loadTime = System.currentTimeMillis()-tstart;
		LOGGER.warn("*** total TasksListAction.execute() time "+loadTime);
		return (mapping.findForward(target));
	}
}
