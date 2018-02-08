package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;

import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;

import ru.masterdm.spo.utils.Formatter;

/**
 * Удаление документа. 
 * @author Andrey Pavlenko
 */
public class DeleteAttachAction extends Action {
    private static final Logger LOGGER = Logger
            .getLogger(DeleteAttachAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String unid = request.getParameter("unid");
        String reason = Formatter.str(request.getParameter("reason"));
        String mdtask = request.getParameter("mdtask");
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        UserJPA user = TaskHelper.getCurrentUser(request);
        LOGGER.info("DeleteAttachAction start. unid=" + unid + ", userid=" + user.getIdUser().toString()+" mdtask="+mdtask);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        AttachJPA a = pupFacadeLocal.getAttachemnt(unid);
        //проверить права.
        if (a.getWhoAccepted()!=null && !a.getWhoAccepted().equals(user)) {
        	response.getWriter().write("Удалить утвержденный документ может только утвердивший его руководитель");
        	return null;
        }
        if(a.getWhoAccepted()==null && a.getWhoAdd()!=null && !a.getWhoAdd().equals(user)){
        	response.getWriter().write("Удалить неутвержденный документ может только его автор");
        	return null;
        }
        if(a.getWhoAdd()==null && a.getOWNER_TYPE().equals(0L) && 
        		!user.hasRole(pupFacadeLocal.getProcessById(Long.valueOf(a.getID_OWNER())).getProcessType().getIdTypeProcess(), "Работник мидл-офиса") 
        		&& !user.hasRole(pupFacadeLocal.getProcessById(Long.valueOf(a.getID_OWNER())).getProcessType().getIdTypeProcess(), "Руководитель мидл-офиса")){
        	response.getWriter().write("У вас нет полномочий удалять документ СДК");
        	return null;
        }
        //Всё в порядке, можно удалять
        pupFacadeLocal.deleteAttachment(unid,user.getIdUser(), reason);
        if(!Formatter.str(mdtask).isEmpty())
            notifyFacade.notifyDeleteDoc(Long.valueOf(mdtask),reason,unid, a.getOWNER_TYPE().equals(1L)?a.getID_OWNER():null);
        response.getWriter().write("OK");
        return null;
    }
}
