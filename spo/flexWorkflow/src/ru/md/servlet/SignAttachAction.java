package ru.md.servlet;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;

import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * Утверждение документа. 
 * @author Andrey Pavlenko
 */
public class SignAttachAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignAttachAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String unid = request.getParameter("unid");
        String sign = request.getParameter("sign");
        if(sign==null || sign.trim().isEmpty() || sign.equalsIgnoreCase("err")){
        	response.getWriter().write("Чтобы подписать документ нужна ЭЦП.");
        	return null;
        }
        Long userid = AbstractAction.getWorkflowSessionContext(request).getIdUser();
        LOGGER.info("SignAttachAction start. unid=" + unid + ", userid=" + userid.toString());
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(userid);
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        try {
        	AttachJPA attach = pupFacadeLocal.getAttachemnt(unid);
        	if(!attach.getOWNER_TYPE().equals(0L)){
        		pupFacadeLocal.signAttachment(userid,unid,sign);
        	    response.getWriter().write("OK");
        	} else {
        		//утвердить документ может только руководитель прикрепившего документ или тот, кто прикрепил
        		Set<UserJPA> slaves = pupFacadeLocal.getSlave(userid, 
        				taskFacade.getTaskByPupID(Long.valueOf(attach.getID_OWNER())).getProcess().getProcessType().getIdTypeProcess());
        		if(user.equals(attach.getWhoAdd()) || slaves.contains(attach.getWhoAdd())){
        			pupFacadeLocal.signAttachment(userid,unid,sign);
        			response.getWriter().write("OK");
        		} else {
        			response.getWriter().write("Подписать документ может только руководитель прикрепившего документ или тот, кто прикрепил.");
        		}
        	}
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
        return null;
    }
}
