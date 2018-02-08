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

import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import ru.md.spo.util.CpsFacade;

public class DelPjojectTeamAction extends Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(DelPjojectTeamAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String section = request.getParameter("section");
        
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        
        UserJPA user = pupFacadeLocal.getUser(Long.valueOf(request.getParameter("id")));
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        for (ProjectTeamJPA pt : task.getProjectTeam()){
            if(pt.getUser().getIdUser().equals(user.getIdUser()) && pt.getTeamType().equals(section)){
                task.getProjectTeam().remove(pt);
                taskFacadeLocal.removeProjectTeamJPA(pt.getId());
                try {
                    CpsFacade.removeMember(wsc.getIdUser(), task.getId(), request.getParameter("section"), user.getIdUser());
        		} catch (Exception e) {
        			LOGGER.warn(e.getMessage(), e);
        		}
            }
        }
        //отправить уведомление
        UserJPA from = pupFacadeLocal.getUser(AbstractAction.getWorkflowSessionContext(request).getIdUser());
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        String body = "Вы исключены из проектной команды в СПО по " + notifyFacade.getNamePraepositionalis(task.getId());
        notifyFacade.send(from.getIdUser(), user.getIdUser(), body, body + notifyFacade.getDescriptionTask(task.getId()));
        LOGGER.info("отправлено уведомление на " + user.getMailUser());
        //снять с пользователя все роли проектной команды
        if(section.equals("p")){
            for (RoleJPA role : user.getRoles()){
                if(!task.getProcess().getProcessType().getIdTypeProcess().equals(role.getProcess().getIdTypeProcess()))
                    continue;
                if(TaskHelper.dict().findProjectTeamRoles().contains(role.getNameRole()) 
                        && pupFacadeLocal.isAssigned(user.getIdUser(), role.getIdRole(), task.getProcess().getId())){
                    pupFacadeLocal.deleteAssign(role.getNameRole(), task.getProcess().getId(), wsc.getIdUser());
                }
            }
        }
        return null;
    }
}
