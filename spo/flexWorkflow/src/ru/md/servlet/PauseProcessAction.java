package ru.md.servlet;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.domain.Comment;
import com.vtb.domain.Operator;
import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.SPOMessageActionProcessor;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

public class PauseProcessAction extends Action {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Long pupid = Long.valueOf(request.getParameter("id"));
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        Long userid = wsc.getIdUser();
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(userid);
        TaskJPA task = taskFacadeLocal.getTaskByPupID(pupid);
        Calendar pauseTimeCal = Calendar.getInstance();
        pauseTimeCal.setTime(Formatter.parseDate(request.getParameter("pauseDate")));
        pauseTimeCal.set(Calendar.HOUR_OF_DAY, 23);
        pauseTimeCal.set(Calendar.MINUTE, 59);
        
        wsc.setPageData("Для заявки номер "+task.getNumberAndVersion()+" изменен срок восстановления.");
        if(!task.getProcess().isPaused()){//если уже приостановлена, то это изменение срока. Уведомления рассылать не нужно
            try {
                String subject = notifyFacade.getName(task.getId()) + " приостановлен(а).";
                String body = subject+"\n<br />Коментарий: "+request.getParameter("cmnt") +
                        notifyFacade.getDescriptionTask(task.getId());
                for(ProjectTeamJPA pt : task.getProjectTeam()){
                    notifyFacade.send(user.getIdUser(), pt.getUser().getIdUser(), subject, body);
                }
                TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
                Task taskJDBC = processor.getTask(new Task(task.getId()));
                taskJDBC.getComment().add(new Comment(null, 
                        "Приостановление заявки.\n "+
                        request.getParameter("cmnt"),
                        request.getParameter("cmnt"),
                        new Operator(wsc.getIdUser().intValue()), 
                        null, 
                        new Timestamp(System.currentTimeMillis())));
                processor.updateTask(taskJDBC);
                wsc.setPageData(subject+" Отправлены уведомления членам проектной команды.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }
        pupFacadeLocal.pauseProcess(pupid, userid, request.getParameter("cmnt"), pauseTimeCal.getTime());
        
        return mapping.findForward("textPage");
    }
}
