package ru.md.servlet;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.TaskList;

import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.TaskListType;
/**
 * возвращает количество элементов для списков.
 * @author Andrey Pavlenko 
*/
public class CounterAjaxAction extends Action {
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String type = request.getParameter("type");
        PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext((HttpServletRequest)request);
        Long count = pupFacade.getWorkListCount(wsc.getIdUser(), TaskListType.valueOf(type.toUpperCase()), null);
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        format += "<counter><type>{0}</type><count>{1}</count></counter>";
        String ans = "";
        ans = MessageFormat.format(format,type,count);
        response.getWriter().write(ans);
        return null;
    }
}
