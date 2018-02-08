package ru.md.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.pup.dbobjects.RoleJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.util.CpsFacade;

public class AssignProjectTeamAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignProjectTeamAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        Long roleid = Long.valueOf(request.getParameter("role"));
        //при назначении (с помощью чекбоксов) на выполнение операций нового Пользователя с ролью "Структуратор" либо с ролью "Руководитель структуратора",
        //необходимо снимать назначение с ранее назначенного на выполнение операций* Пользователя с ролью "Структуратор" либо с ролью "Руководитель структуратора")
        //VTBSPO-1453
        RoleJPA role = pupFacadeLocal.getRole(roleid);
        if (role.getNameRole().equals("Структуратор") || role.getNameRole().equals("Руководитель структуратора")){
        	pupFacadeLocal.deleteAssign("Структуратор", task.getProcess().getId(), wsc.getIdUser());
        	pupFacadeLocal.deleteAssign("Руководитель структуратора", task.getProcess().getId(), wsc.getIdUser());
        }
        if (role.getNameRole().equals("Структуратор (за МО)") || role.getNameRole().equals("Руководитель структуратора (за МО)")){
        	pupFacadeLocal.deleteAssign("Структуратор (за МО)", task.getProcess().getId(), wsc.getIdUser());
        	pupFacadeLocal.deleteAssign("Руководитель структуратора (за МО)", task.getProcess().getId(), wsc.getIdUser());
        }
        pupFacadeLocal.assign(Long.valueOf(request.getParameter("user")), roleid, 
                task.getProcess().getId(), wsc.getIdUser(), true);
        try {
            CpsFacade.executorSetting(wsc.getIdUser(), task.getId(),
                    Long.valueOf(request.getParameter("user")), role.getNameRole());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
        response.getWriter().write("OK");
        return null;
    }
}
