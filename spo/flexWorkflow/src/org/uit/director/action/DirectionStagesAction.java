package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.managers.StagesDirectionManager;
import org.uit.director.tasks.TaskList;
import org.uit.director.utils.ReportActionUtils;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.TaskListType;


public class DirectionStagesAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        String target = "stagesDirectionPage";
        String typeProc = request.getParameter("typeProc");
        String idUser = request.getParameter("user");

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));

        ActionForward forward;
        try {

            //открыть доступ только администатору
            forward = ReportActionUtils.accessForAdmin(request, mapping);
            if (forward != null)
                return forward;

            if (idUser == null || idUser.equals("-") ) {
            	// ничего не делаем, просто ждем, когда будут заполнены поля
            	return (mapping.findForward(target));
            }
            	Long idUserL = Long.valueOf(idUser);            	
            	Long idTypeProc = Long.valueOf(typeProc);
            	WPC wpc = WPC.getInstance();
            	String typeProcName = wpc.getTypeProcessById(idTypeProc.intValue()).getNameTypeProcess();
            	
                StagesDirectionManager manager = wsc.getStagesDirectionManager();
                if (manager == null) manager = new StagesDirectionManager();

                for (TaskListType type : TaskListType.values()){
                    TaskList taskList = new TaskList();
                    taskList.init(type, wsc, idUserL);
                    taskList.setTasksOnPage(1000);
                    taskList.setFilter(TaskList.FILTER_BY_NAME_TYPE_PROCESS, typeProcName);
                    taskList.setFilter(TaskList.FILTER_BY_NAME_STAGE, "NULL_String");
                    taskList.setFilter(TaskList.FILTER_BY_ATRIBUTES, "NULL_String");
                    String res = taskList.execute(new ProcessSearchParam(request,false));


                    if (res.equalsIgnoreCase("erroe")) {
                        wsc.setErrorMessage("Ошибка получения списка заданий "+type.toString());
                        target = "errorPage";
                        return (mapping.findForward(target));
                    } else
                        manager.getList().put(type, taskList);
                }                
                
                wsc.setStagesDirectionManager(manager);
                request.setAttribute("iduser", idUserL);
            
        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка выполнения операции.");
            target = "errorPage";
        }
        return (mapping.findForward(target));
    }
}
