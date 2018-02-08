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
import org.uit.director.tasks.TaskList;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.TaskListType;

import ru.md.jsp.tag.IConst_PUP;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.ejb.PupFacadeLocal;

public class TaskContextAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
    	long tstart = System.currentTimeMillis();
    	request.setAttribute("startTime", tstart);
        String target = "pageContextTask";
        String idTask = (request.getParameter("id") == null) ? request.getParameter("id0") : request.getParameter("id");
        String readonly = request.getParameter(IConst_PUP.READONLY);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        try {
            if (idTask == null) {
                target = "start";
            } else {
                Long idTaskL = Long.valueOf(idTask);
                wsc.setIdCurrTask(idTaskL);
                if(wsc.getTaskList()==null){
                	TaskList taskList = new TaskList();
					taskList.init(TaskListType.ACCEPT, wsc, true);
					taskList.execute(new ProcessSearchParam(request,true));
					wsc.setPageTask(taskList);
                }
                wsc.getTaskList().addTaskInfo(idTaskL);

                PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                TaskInfoJPA taskInfo = pup.getTask(idTaskL);
                request.setAttribute("taskStatusId", taskInfo.getIdStatus());

                // проверяем есть ли у нас права редактировать эту заявку
                Long im = wsc.getIdUser();
                Long executorId = taskInfo.getExecutor()==null?null:taskInfo.getExecutor().getIdUser();
                if (executorId!=null && readonly == null && !im.equals(executorId)) {
                    wsc.setErrorMessage("Заявка взята на редактирование (" + taskInfo.getExecutor().getFullName() + ")");
                    target = "errorPage";
                }
                request.setAttribute(IConst_PUP.TASK_INFO, wsc.getCurrTaskInfo(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка создания контекста задания.");
            target = "errorPage";
        }
        return (mapping.findForward(target));
    }
}
