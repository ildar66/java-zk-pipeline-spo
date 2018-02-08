package ru.md.servlet;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.spo.utils.Formatter;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class TraderApproveAction extends Action {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    //TODO удалить нахрен
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long mdtaskid = Long.valueOf(request.getParameter("id"));
        LOGGER.info("TraderApproveAction, mdtaskid="+mdtaskid);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        Long userid = wsc.getIdUser();
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(userid);
        TaskJPA task = taskFacadeLocal.getTask(mdtaskid);
        if(pupFacadeLocal.isCurrentUserInProjectTeam(mdtaskid) 
    		    && pupFacadeLocal.currentUserAssignedAs("Кредитный аналитик", task.getProcessId())
    		    && !pupFacadeLocal.isCedEnded(mdtaskid)){
        	task.setTraderApprove(true);
        	task.setTrader_approve_date(new Date());
        	task.setTrader_approve_user(userid);
        	taskFacadeLocal.merge(task);
        	task = taskFacadeLocal.getTask(mdtaskid);
        	response.getWriter().write("Подтверждено Трейдером "+user.getFullName() + " "+Formatter.formatDateTime(task.getTrader_approve_date()));
        } else {
        	response.getWriter().write("Нет прав на операцию подтверждение трейдером");
        }
        return null;
    }
}
