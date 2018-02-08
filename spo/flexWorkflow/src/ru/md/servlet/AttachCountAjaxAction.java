package ru.md.servlet;

import java.text.MessageFormat;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.spo.ejb.PupFacadeLocal;

public class AttachCountAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        format += "<root><id>{0}</id><count>{1}</count></root>";
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        String ans = MessageFormat.format(format,request.getParameter("org"), 
                pupFacadeLocal.findAttachemntCountByOwnerAndType(request.getParameter("org"), 1l));
        response.getWriter().write(ans);
        //logger.info("AJAX attachCount.do answer: "+ans);
        return null;
    }
}
