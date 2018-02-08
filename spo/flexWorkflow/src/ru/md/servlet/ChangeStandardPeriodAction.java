package ru.md.servlet;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.util.Formatter;

public class ChangeStandardPeriodAction extends Action {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
        
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        UserJPA user = pupFacadeLocal.getUser(AbstractAction.getWorkflowSessionContext(request).getIdUser());
        LOGGER.info(request.getParameter("vid"));
        String comment = request.getParameter("cmnt");
        try {
            if (request.getCharacterEncoding() != null) comment = 
                new String(comment.getBytes(request.getCharacterEncoding()), "UTF8");
            else comment = new String(comment.getBytes("UTF8"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            comment = request.getParameter("cmnt");
        }
        LOGGER.info(comment);
        

        spLocal.changeStandardPeriod(task.getId(), user.getIdUser(), 
                comment, Formatter.parseLong(request.getParameter("vid")),
                Formatter.parseLong(request.getParameter("days")),
                Formatter.parseLong(request.getParameter("grid")));
        spLocal.recalculateDeadline(task.getProcess().getId());
        
        response.getWriter().write("OK");
        return null;
    }
}
