/**
 * 
 */
package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.ejb.PupFacadeLocal;

/**
 * Отказ по акцепту операции
 * 
 * @author imatushak@masterdm.ru
 * 
 */
public class DeclineAcceptAction extends Action {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.struts.action.Action#execute(org.apache.struts.action.
     * ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String taskId = request.getParameter("taskId");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        pupFacadeLocal.deleteAcceptList(Long.parseLong(taskId));
        TaskInfoJPA task = pupFacadeLocal.getTask(Long.parseLong(taskId));
        task.setIdStatus(2);
        pupFacadeLocal.merge(task);

        ActionForward forward = new ActionForward();
        forward.setRedirect(true);
        forward.setPath("acceptList.jsp");
        return forward;
    }

}
