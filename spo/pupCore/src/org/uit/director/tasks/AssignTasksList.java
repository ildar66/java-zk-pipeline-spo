package org.uit.director.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;

public class AssignTasksList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AssignTaskInfo> tableAssignTaskList;

	private WorkflowSessionContext wsc;

	private Long idUser;

	public AssignTasksList() {
		tableAssignTaskList = new ArrayList<AssignTaskInfo>();
	}

	public void init(WorkflowSessionContext wsc) {
		this.wsc = wsc;
		idUser = wsc.getIdUser();
	}

	public void init(WorkflowSessionContext wsc,
			Long idUser) {
		this.wsc = wsc;
		this.idUser = idUser;
	}
	
	public String execute() {

		String result = "ok";
		try {

			DBFlexWorkflowCommon dbFW = wsc.getDbManager().getDbFlexDirector();

			List<Long> idAssignList = dbFW.getAssignToUsersTasksList(idUser);

			int allAssignTasks = idAssignList.size();

			for (int i = 0; i < allAssignTasks; i++) {
				Long idAssign = idAssignList.get(i);
				AssignTaskInfo assignInfo = new AssignTaskInfo();				
				assignInfo.init(wsc, idAssign);
				assignInfo.execute();
				tableAssignTaskList.add(assignInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = "Error";
		}

		return result;
	}

	public List<AssignTaskInfo> getTableAssignTaskList() {
		return tableAssignTaskList;
	}

	public AssignTaskInfo findAssignTask(Long idProcess) {

		int col = tableAssignTaskList.size();
		for (int i = 0; i < col; i++) {
			AssignTaskInfo map = tableAssignTaskList.get(i);
			Long idP = map.getIdProcess();

			if (idP.equals(idProcess)) {
				return map;
			}

		}
		return null;

	}

}
