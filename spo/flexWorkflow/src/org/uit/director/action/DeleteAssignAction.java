package org.uit.director.action;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.pup.dbobjects.AssignJPA;
import ru.md.spo.ejb.PupFacadeLocal;

/**
 * @version 1.0
 * @author
 */
public class DeleteAssignAction extends Action

{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value
		String target = "errorPage";
		String idAssign = request.getParameter("idAssign");
		String sign = request.getParameter("sign");

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {

			if (idAssign != null) {
			    InitialContext initialContext = new InitialContext();
			    PupFacadeLocal ejb = (PupFacadeLocal)initialContext.lookup("ejblocal:"+PupFacadeLocal.class.getName());
		        AssignJPA assign = ejb.getAssignbyId(Long.valueOf(idAssign));
		        Long idTypeProcess = assign.getId_type_process();
		        request.getSession().setAttribute("idProcessType",idTypeProcess.toString());
				wsc.getDbManager().getDbFlexDirector().deleteAssign(
						Long.valueOf(idAssign), wsc.getIdUser(),
						request.getRemoteAddr(), sign);
			}

			target = "target";
		} catch (Exception e) {

		}

		forward = mapping.findForward(target);
		return (forward);

	}
}
