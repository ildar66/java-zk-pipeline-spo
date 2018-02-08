package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.ProcessInfo;
import org.uit.director.tasks.ProcessList;
import org.uit.director.tasks.TaskList;


public class EditProcessAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        String target = "stagesDirectionPage";
        String idProcess = request.getParameter("idProcess");


        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));


        try {
        	if (idProcess != null && !idProcess.equals("")) {

                ProcessInfo info = new ProcessInfo();
                info.init(wsc, Long.parseLong(idProcess), wsc.getIdUser(), true);
                info.execute();
                wsc.setCurrEditProcessInfo(info);
//                request.setAttribute("processInfo", info);
                /*wsc.setIdCurrTask(-1);
                ProcessList processList = wsc.getProcessList();
                if (processList == null) wsc.setProcessList(processList = new ProcessList());
                processList.addProcessInfo(info);*/
                target = "pageContextTask";
            } else {

                wsc.setErrorMessage("Неверный параметр.");
                target = "errorPage";
                return (mapping.findForward(target));
            }


        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка выполнения операции.");
            target = "errorPage";

        }


        return (mapping.findForward(target));
    }
}
