package org.uit.director.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.plugins.commonPlugins.PluginActionImpl;
import org.uit.director.tasks.TaskInfo;
import org.uit.director.tasks.TaskList;

import com.vtb.domain.TaskListType;

public class CreateProcessAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "acceptedTasks";

		String idTypeProc = request.getParameter("idTypeProc");
		String sign = request.getParameter("sign");
		if (sign == null)
			sign = "NULL_String";

		boolean acceptTask = (request.getParameter("acceptTask")
				.equals("false")) ? false : true;
		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		try {

			wsc.beginUserTransaction();

			Integer idTypeProcess = Integer.valueOf(idTypeProc);
			DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager()
					.getDbFlexDirector();
			// Long idstageBegin =
			// BusinessProcessDecider.getStartIdStage(idTypeProcess);
			Long idProcess = dbFlexDirector.createProcess(idTypeProcess, null,
					wsc.getIdUser());

			if (idProcess != null) {
				/* Если пользователь не имеет права запускать процесс */
				if (idProcess.longValue() == -1) {
					wsc
							.setErrorMessage("Вы не имеете прав для запуска процесса");
					target = "errorPage";

				} else if (idProcess.longValue() == -2) {
					wsc.setErrorMessage("Ошибка запуска процесса");
					target = "errorPage";

				} else {

					List<Long> activeStages = null;

					if (acceptTask) {

						TaskList taskList = new TaskList();

						taskList.init(TaskListType.NOT_ACCEPT, wsc, true);
						taskList.setFilter(
								TaskList.FILTER_BY_NAME_TYPE_PROCESS,
								"NULL_String");
						taskList.setFilter(TaskList.FILTER_BY_NAME_STAGE,
								"NULL_String");
						taskList.setFilter(TaskList.FILTER_BY_ATRIBUTES,
								"NULL_String");
						taskList.execute(null);

						for (int i = 0; i < taskList.size(); i++) {
							TaskInfo tInfo = taskList.getTaskIdx(i);
							if (tInfo.getIdProcess().equals(idProcess)) {
								activeStages = tInfo.getActiveStages();
								dbFlexDirector.acceptWork(tInfo.getIdTask(),
										wsc.getIdUser(), sign);
							}

						}

						response
								.sendRedirect("showTaskList.do?typeList=accept");
					} else {
						response
								.sendRedirect("showTaskList.do?typeList=noAccept");
					}

					if (activeStages != null) {
						for (Long idStage : activeStages) {
							PluginActionImpl.executePluginAction(wsc, idStage,
									Cnst.TStages.classEntry);
						}
					}
					wsc.commitUserTransaction();

					return null;

				}
			}

			wsc.rollBackUserTransaction();

		} catch (Exception e) {
			wsc.rollBackUserTransaction();
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка инициализации процесса.");
			target = "errorPage";

		}

		return (mapping.findForward(target));
	}
}