/**
 *  Created by Struts Assistant.
 *  Date: 21.09.2006  Time: 04:30:25
 */

package org.uit.director.action;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.ProcessControlType;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.plugins.CRM.SetCRMData;
import org.uit.director.tasks.TaskInfo;

public class RefusalAction extends org.apache.struts.action.Action {

	private static Logger logger = Logger.getLogger(RefusalAction.class.getName());
	
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        String target = "errorPage";
        String idTask = request.getParameter("idTask");
        
        logger.info("idTask = " + idTask);

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));


        try {
        	//TaskInfo taskInfo = (TaskInfo) request.getAttribute(IConst_PUP.TASK_INFO);
        	TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(Long.parseLong(idTask));
        	if (taskInfo != null) {
        		logger.info("taskInfo not null");
        		
        		updateAttributes(wsc, taskInfo);
        		
        		SetCRMData plugin = new SetCRMData();
        		plugin.init(wsc, null);
        		plugin.execute();
        	} else {
        		logger.warning("taskInfo is null");
        	}
        	
        	
            wsc.getDbManager().getDbFlexDirector().refuseProcess(idTask);
            
            logger.info("refuse process completed: idTask = " + idTask);
            
            target = "acceptedTasks";
        } catch (Exception e) {
        	logger.severe(e.getMessage());
        	e.printStackTrace();
        	
            wsc.setErrorMessage("Ошибка выполнения действия (" + e.getMessage() + ")");
        }


        return mapping.findForward(target);
    }

	/**
	 * Обновляет значения атрибутов процесса
	 * 
	 * @param wsc контекс
	 * @param taskInfo информация о задании
	 * @throws Exception
	 */
	private void updateAttributes(WorkflowSessionContext wsc, TaskInfo taskInfo) throws Exception {
		try {
			DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();
	
			logger.info("dbFlexDirector not null: " + (dbFlexDirector != null));
			logger.info("idTypeProcess: " + taskInfo.getIdTypeProcess());
	
			ProcessControlType controlType = WPC.getInstance().getControlType(taskInfo.getIdTypeProcess());
	
			logger.info("controlType is " + controlType.toString());
	
			ArrayList<Object[]> param = new ArrayList<Object[]>();
			Object[] par = new Object[3];
			par[0] = taskInfo.getIdProcess();
			par[1] = WPC.getInstance().findVariableByName("Статус", taskInfo.getIdTypeProcess()).getIdVariable();
			par[2] = "Отказ клиента";
			param.add(par);
	
			logger.info("idProcess:" + par[0]);
			logger.info("\"Статус\" idVariable:" + par[1]);
	
			String resUpd = dbFlexDirector.updateAttributes(param, controlType);
			if (!resUpd.equalsIgnoreCase("ok")) {
				if (resUpd.startsWith("Error")) {
					logger.severe("error occured while update attribute");
	
					wsc.setErrorMessage(resUpd);
				} else {
					logger.warning("update attribute completed with warnings");
	
					wsc.setWarningMessage(resUpd);
				}
			} else {
				logger.info("update attribute completed");
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
}