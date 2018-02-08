package ru.md.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;
/**
 * Возвращает список пользователей, которых можно назначить на эту заявку.
 * @author Andrey Pavlenko
 */
public class User4assignAjaxAction extends Action {
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskInfoJPA task = pupFacadeLocal.getTask(Long.valueOf(request.getParameter("taskid")));
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext((HttpServletRequest)request);
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<User4assign>\n";
        for (UserJPA user : pupFacadeLocal.getUser4assign(Long.valueOf(task.getStage().getIdStage()), wsc.getIdUser())){
            ans += "<user><id>"+user.getIdUser()+"</id><name>" +
            		user.getFullName()+"</name></user>\n";
        }
        ans += "<taskid>" + task.getIdTask() + "</taskid></User4assign>";
        response.getWriter().write(ans);
        return null;
    }
}
