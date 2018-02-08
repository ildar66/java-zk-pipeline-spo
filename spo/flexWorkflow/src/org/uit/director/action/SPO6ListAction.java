package org.uit.director.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.compendium.value.Page;

import com.vtb.domain.Process6;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.value.BeanKeys;

public class SPO6ListAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		if(!wsc.isAdmin()){
			wsc.setErrorMessage("Эта страница доступна только администратору");
			return mapping.findForward("errorPage");
		}
		try{
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			Page page = processor.findSPO6List(1,100000);
			ArrayList<Process6> filteredlist = new ArrayList<Process6>();
			ArrayList<Process6> list = (ArrayList<Process6>) page.getList(); 
			for (Process6 pr : list) {
				if (!processor.isCRMLimitLoaded("spo6-" + pr.getNumber())) {
					filteredlist.add(pr);
				}
			}
			request.setAttribute(BeanKeys.PROCESS6_LIST, filteredlist);
		}catch(Exception e){
			wsc.setErrorMessage(e.getMessage());
			return mapping.findForward("errorPage");
		}
		return mapping.findForward("success");
	}
}
