package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class StartMemorandumAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        pupFacadeLocal.startMemorandum(wsc.getIdUser(), 
                Long.valueOf(request.getParameter("pupid")));
        pupFacadeLocal.updatePUPAttribute(Long.valueOf(request.getParameter("pupid")), 
                "Формирование Кредитного меморандума", "0");
        wsc.setPageData("Ветка формирование кредитного меморандума запущена.");
        return mapping.findForward("textPage");
    }
}
