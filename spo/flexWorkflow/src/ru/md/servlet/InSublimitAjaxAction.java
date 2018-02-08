package ru.md.servlet;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;
/**
 * возвращает дочерние элементы (сублимиты и сделки) этой заявкм (сублимита или лимита).
 * @author Andrey Pavlenko
*/
public class InSublimitAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA task = taskFacadeLocal.getTask(new Long(request.getParameter("id")));
            String parentid = request.getParameter("parentid");
            task.setParent(taskFacadeLocal.getTask(new Long(parentid)));
            taskFacadeLocal.merge(task);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        response.getWriter().write("OK");
        return null;
    }
}
