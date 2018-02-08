package org.uit.director.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;


public class UserInRoleAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "errorPage";
		String idUserS = request.getParameter("idUser");
		String idRole = request.getParameter("idRole");
		String flag = request.getParameter("flag");

		Long idUserL = (idUserS == null || idUserS.equals("") ) ? null : Long.valueOf(idUserS);
		idRole = idRole == null ? "" : idRole;
		flag = flag == null ? "" : flag;

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {		
			
			DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();
			boolean isUsersPage = false;
			if (flag.equalsIgnoreCase("deleteUser") && idUserL != null) {
				
				ArrayList<Long> idRoleList = (ArrayList<Long>) WPC.getInstance().getIDRolesForUser(idUserL);
				dbFlexDirector.deleteWorkflowUser(idUserL, idRoleList, wsc.getIdUser(), request.getRemoteAddr());					
				isUsersPage = true;
			}

			if (flag.equalsIgnoreCase("deleteRole") && idUserL != null
					&& !idRole.equals("")) {
				
				dbFlexDirector.deleteWorkflowRole(idUserL, idRole, wsc.getIdUser(), request.getRemoteAddr());
				
			}

			if (flag.equalsIgnoreCase("addRole") && idUserL != null
					&& !idRole.equals("")) {
				
				dbFlexDirector.addWorkflowRole(idUserL, idRole,wsc.getIdUser(), request.getRemoteAddr());
				
			}			
			
			if (flag.equalsIgnoreCase("setNotify")) {
				String mail = request.getParameter("mailAddress");
				String ip = request.getParameter("ipAddress");
				
				dbFlexDirector.addUserNotify(idUserL, mail, ip, wsc.getIdUser(), request.getRemoteAddr());
								
			} 

			WPC.getInstance().reloadStaticTables(
					dbFlexDirector);
			//пользователь не удаляется из системы
			/*if (flag.equalsIgnoreCase("deleteUser"))
				WPC.getInstance().getUsersMgr().deleteWorkflowUser(idUserL);*/
			if (!isUsersPage) {
				response.sendRedirect("usersDirection.jsp?idUser=" + idUserL);
				return null;
			}
			target = "directionUsers";

		} catch (Exception e) {
			wsc.setErrorMessage("Ошибка выполнения запроса:" + e.getMessage());
			e.printStackTrace();
		}

		return (mapping.findForward(target));
	}
}
