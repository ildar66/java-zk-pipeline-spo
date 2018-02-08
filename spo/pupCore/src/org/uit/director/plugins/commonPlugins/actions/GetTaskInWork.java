/*
 * Created on 30.10.2007
 * 
 */
package org.uit.director.plugins.commonPlugins.actions;

import java.rmi.RemoteException;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.TaskInfo;
import org.uit.director.tasks.TaskList;

import com.vtb.domain.TaskListType;

public class GetTaskInWork implements PluginInterface {

	private List params;

	private WorkflowSessionContext wsc;

	public void init(WorkflowSessionContext wsc, List params) {
		this.wsc = wsc;
		this.params = params;
	}

	public String execute() {

		String nameVarUser = (String) params.get(0);
		String nameVarIsGetTaskInWork = (String) params.get(1);

		TaskInfo oldTaskInfo = wsc.getTaskList().findTaskInfo(
				wsc.getIdCurrTask());
		DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager()
				.getDbFlexDirector();

		AttributesStructList attributes = oldTaskInfo.getAttributes();
		String idUserS = attributes.getStringValueByName(nameVarUser)
				.toLowerCase();		

		TaskList taskList = new TaskList();
		Long idUserL = Long.valueOf(idUserS);
		taskList.init(TaskListType.NOT_ACCEPT, wsc, idUserL);
		taskList.setFilter(TaskList.FILTER_BY_NAME_TYPE_PROCESS, oldTaskInfo.getNameTypeProcess());
		taskList.setFilter(TaskList.FILTER_BY_NAME_STAGE, "NULL_String");
		Attribute attr = attributes.getMainAttributes(/*
				oldTaskInfo.getIdTypeProcess()*/).get(0).getAttribute();

		taskList.setFilter(TaskList.FILTER_BY_ATRIBUTES, attr
				.getValueAttributeString());
		taskList.execute(null);

		TaskInfo newTaskInfo;
		int idx = 0;
		while ((newTaskInfo = taskList.getTaskIdx(idx++)) != null) {
			if (newTaskInfo.getIdProcess() == oldTaskInfo.getIdProcess()
					&& newTaskInfo.getDateOfTakingStr().equals("-")) {
				break;
			}

		}

		boolean isGetTaskInWork = attributes.getStringValueByName(
				nameVarIsGetTaskInWork).equalsIgnoreCase("true")
				&& idUserS != null && !idUserS.equals("-") && !idUserS.equals("") ? true
				: false;

		if (isGetTaskInWork) {

			long idOldTask = oldTaskInfo.getIdTask().longValue();
			String signumComplation;
			try {
				signumComplation = dbFlexDirector
						.getSignum(idOldTask, false);
				dbFlexDirector.acceptWork(newTaskInfo.getIdTask().longValue(),
						idUserL, signumComplation);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			

		}
		return null;
	}

}
