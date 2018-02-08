package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;

import ru.md.spo.ejb.PupFacadeLocal;

/**
 * передача на СКК асинхронно документа. 
 * @author Andrey Pavlenko
 */
public class Attach4ccAction extends Action {
    private static final Logger LOGGER = Logger
            .getLogger(Attach4ccAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String unid = request.getParameter("unid");
        String mdtaskid = request.getParameter("mdtaskid");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        pupFacadeLocal.attachment4cc(unid, 
                request.getParameter("cc")==null || request.getParameter("cc").equals("1"),
                 Long.valueOf(mdtaskid));
        response.getWriter().write("OK");
        return null;
    }
}
