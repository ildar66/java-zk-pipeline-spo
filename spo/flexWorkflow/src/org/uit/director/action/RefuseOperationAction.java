package org.uit.director.action;

import java.util.logging.Logger;

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

/**
 * @author Michael Kuznetsov
 * Action для Отказ от выполнения операции, VTBSPO-595
 */
public class RefuseOperationAction extends Action {
    private static final Logger logger = Logger.getLogger(RefuseOperationAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("start RefuseOperationAction");
        request.setAttribute("typeList", "refuse");
        
        ActionForward forward = null;

    	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);        	
        String target = "success";
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
        
        String navigation = request.getParameter("navigation");
        Integer isOwnDepartmentInt = 0;

        String filterNameTypeProcess = request.getParameter("filterNameTypeProcess");
        if (filterNameTypeProcess == null || filterNameTypeProcess.trim().equals("")) filterNameTypeProcess = "NULL_String";

        String filterNameStage = request.getParameter("filterNameStage");
        if (filterNameStage == null || filterNameStage.trim().equals("")) filterNameStage = "NULL_String";

        String filterAtribute = request.getParameter("filterAtribute");
        if (filterAtribute == null || filterAtribute.trim().equals("")) filterAtribute = "NULL_String";

        
        try {
            String res;
            if (navigation == null || navigation.length()==0) {//читаем новый  список из базы
                TaskList taskList;//список заявок
                boolean isOwnDepartmentBool=isOwnDepartmentInt.intValue() == 1 ? true : false;//эта строка - код Ижевцев. Я от авторства отказываюсь! Сохранено для истории.

                taskList = new TaskList();
                taskList.init(TaskListType.ACCEPT_FOR_REFUSE, wsc, isOwnDepartmentBool);
                taskList.setFilter(TaskList.FILTER_BY_NAME_TYPE_PROCESS, filterNameTypeProcess);
                taskList.setFilter(TaskList.FILTER_BY_NAME_STAGE, filterNameStage);
                taskList.setFilter(TaskList.FILTER_BY_ATRIBUTES, filterAtribute);
                res = taskList.execute(new ProcessSearchParam(request,true));
                // TODO : а нам как надо сортировать? или вообще не надо?
                if (taskList.isLoadAllTaskList()) {
                    if (!wsc.getSortState().equals("")) taskList.sortByParam(wsc.getSortState());
                }

                if (res.equalsIgnoreCase("Error")) {
                    wsc.setErrorMessage("Ошибка при обработке запроса 'отказ от выполнения операции', повторите попытку позже.");
                    target = "errorPage";
                } else {
                    wsc.setPageTask(taskList);
                }
            } else {//next or prev page
                TaskList taskList = wsc.getTaskList();
                if (navigation.equals("next")) taskList.nextPage();
                else if (navigation.equals("previos")) taskList.previosPage();
                else taskList.setPage(navigation);
            }
            //сохраняет куки
            // TODO : разве нам оно надо?
            ProcessSearchParam p = new ProcessSearchParam(request,request.getParameter("closed")!=null);
            p.saveCookies(response);
        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка при обработке запроса 'отказ от выполнения операции'");
            target = "errorPage";
        }
    	forward = mapping.findForward(target);      	
        return forward;
    }	
}
