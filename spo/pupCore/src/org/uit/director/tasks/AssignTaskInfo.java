package org.uit.director.tasks;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.WorkflowAssignInfo;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;

public class AssignTaskInfo implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	protected WorkflowSessionContext wsc;

	protected WorkflowAssignInfo wfAssignInfo;
	
	protected ProcessInfo processInfo;

	public AssignTaskInfo() {
		super();
	}
	
	public void init(WorkflowSessionContext wsc, Long idAssign) {		
		this.wsc = wsc;
		wfAssignInfo = new WorkflowAssignInfo(idAssign);
		processInfo = new ProcessInfo();
	}

	public void init(WorkflowSessionContext wsc2) {
		wsc = wsc2;
		wfAssignInfo = new WorkflowAssignInfo(null);
		processInfo = new ProcessInfo();
	}

	public String execute() {
		String res = "error";

		try {

			DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager()
					.getDbFlexDirector();

			wfAssignInfo = dbFlexDirector.getAssignInfo(wfAssignInfo.getId());			
			processInfo.init(wsc, getIdProcess(), getIdUser(), false);
			processInfo.execute();
			
			res = "ok";

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public WorkflowAssignInfo getWfAssignInfo() {
		return wfAssignInfo;
	}
	
	public String getDateAssignUser() {
		String dateAssignUser = wfAssignInfo.getDateAssignUser();
		
		try {
			Date d = WPC.getInstance().dateTimeDBFormat.parse(dateAssignUser);
			dateAssignUser = WPC.getInstance().dateTimeFormat.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dateAssignUser;
	}

	public Long getIdAssign() {
		return wfAssignInfo.getId();
	}

	public Long getIdProcess() {
		return wfAssignInfo.getIdProcess();
	}

	public Long getIdRole() {
		return wfAssignInfo.getIdRole();
	}

	public boolean isMayReassign() {
		return wfAssignInfo.isMayReassign();
	}
	
	public Long getIdUser(){
		return wfAssignInfo.getIdUser();
	}

	public Long getIdUserFrom(){
		return wfAssignInfo.getIdUserFrom();
	}

	public ProcessInfo getProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(ProcessInfo processInfo) {
		this.processInfo = processInfo;
	}
	
	
}
