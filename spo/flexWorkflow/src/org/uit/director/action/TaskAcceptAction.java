package org.uit.director.action;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.MyOracleBlob;

import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.util.Config;

public class TaskAcceptAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "newTasks";
		String idUserS = request.getParameter("idUser");
		String isAccept = request.getParameter("isAccept"); // 1-принять задание, 0 -отказаться
		String typeProc = request.getParameter("typeProc");
		Long idUserL = idUserS == null ? null : Long.valueOf(idUserS);
		
		Integer isAcceptInt = (isAccept.equalsIgnoreCase("1") ? new Integer(1)
				: new Integer(0));

		boolean isActionFromSatgesDirection = (idUserS != null);
		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		String idTask = request.getParameter("id0");
		String idTaskRedir = null;
		String sign = request.getParameter("sign0");

		if (sign == null || sign.equals("")) {
			if (Config.getProperty("VALIDATE_SIGNUM").equalsIgnoreCase("true")) {
				wsc
						.setErrorMessage("Действие не подписано, операция не может быть выполнена");
				return (mapping.findForward("errorPage"));
			} else {

				sign = "NULL_String";
			}
		}
		int count = 0;
		try {

			if (idTask != null) {
                idTaskRedir = idTask;
				ArrayList params = new ArrayList();
				if (isActionFromSatgesDirection) {
					if (!wsc.isUserAdmin(Integer.parseInt(typeProc))) {
						wsc
								.setErrorMessage("Нет прав пользователя с правами администратора на текущий процесс.");
						target = "errorPage";
						return (mapping.findForward(target));
					}
				} else {
					idUserL = wsc.getIdUser();
				}
 
				while (idTask != null) {
					try {
						Object[] par = new Object[4];
						par[0] = new Long(idTask);
						par[1] = idUserL;
						par[2] = isAcceptInt;
						par[3] = new MyOracleBlob(sign);
						params.add(par);

					} catch (Exception e) {
						e.printStackTrace();
					}

					wsc.getCacheManager().deleteCacheElement(Long.valueOf(idTask));
					count++;
					idTask = request.getParameter("id" + count);
					sign = request.getParameter("sign" + count);

				}

				if (isActionFromSatgesDirection) {
					wsc.getDbManager().getDbFlexDirector().acceptWorksControl(
							params,wsc.getIdUser(),
							request.getRemoteAddr());
					response.sendRedirect("direction.stages.do?typeProc="
							+ typeProc + "&user=" + idUserL);
					return null;
				} else {
					PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
				    if (isAcceptInt.intValue()==1){
				        pupFacadeLocal.acceptWork(new Long(request.getParameter("id0")), wsc.getIdUser());
				    } else {
				    	pupFacadeLocal.reacceptWork(new Long(request.getParameter("id0")), wsc.getIdUser());
					    //пересчитываем deadline
					    StandardPeriodBeanLocal spFacade = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
					    spFacade.recalculateDeadline(pupFacadeLocal.getTask(new Long(request.getParameter("id0"))).getProcess().getId());
				    }
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Взятие в работу не выполнено. "+e.getMessage());
			target = "errorPage";

		}
		if (request.getParameter("target")!=null&&request.getParameter("target").equals("accept")){
			ActionForward forward = new ActionForward();
			forward.setPath("showTaskList.do?typeList=accept");
			return forward;
		}
		if (request.getParameter("target")!=null&&request.getParameter("target").equals("refuseOperationListFromAcceptAction")){
		    return mapping.findForward("refuseOperationListFromAcceptAction");
        }
		
		if (idTaskRedir != null && !target.equalsIgnoreCase("errorPage") ) {
			System.out.println("!!! CREATE APPLICATION FORM PATH");
			ActionForward forward = new ActionForward();
			
			String redirectUrl = "task.context.do?id="+idTaskRedir;
			forward.setPath("/" + redirectUrl);
			
			System.out.println("!!! OPEN APPLICATION FORM OPERATION");
			
        	response.sendRedirect(redirectUrl);
			
			return forward;
		}
		
		return (mapping.findForward(target));
	}
}