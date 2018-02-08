package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ExpertTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class AddExpertTeamAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(Long.valueOf(request.getParameter("id")));
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        //добавить в команду
        ExpertTeamJPA t = new ExpertTeamJPA();
        t.setTask(task);
        t.setUser(user);
        t.setExpname(request.getParameter("expname"));
        logger.info("expname="+t.getExpname());
        task.getExpertTeam().add(t);
        taskFacadeLocal.merge(t);
        //отправить уведомление
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        String subj = "Вы включены в состав экспертной группы по " + notifyFacade.getNamePraepositionalis(task.getId());
        String body = "Вы включены в состав экспертной группы по "
                + notifyFacade.getTypeNamePraepositionalis(task.getId())+notifyFacade.getAllContractors(task.getId())+" № <a href='"+
        		notifyFacade.getBaseURL(user.getIdUser())+"/showTaskList.do?typeList=all&expertteam=true&searchNumber="+
        	    task.getMdtask_number().toString()+"'>" + task.getNumberAndVersion()+"</a>";
        body += "<br />Этап: " + t.getExpname();
        body += notifyFacade.getDescriptionTask(task.getId());
        notifyFacade.send(TaskHelper.getCurrentUser(request).getIdUser(), 
        		user.getIdUser(), subj, body);
        
        response.getWriter().write("OK");
        return null;
    }
}
