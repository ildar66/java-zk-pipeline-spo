package ru.md.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
/**
 * Удаляет саблимит
 * @author Andrey Pavlenko
*/
public class DelSublimitAjaxAction extends Action {
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        Task sublimit = processor.getTask(new Task(Long.valueOf(request.getParameter("id"))));
        sublimit.setDeleted(true);
        processor.updateTask(sublimit);
        response.getWriter().write("OK");
        return null;
    }
}
