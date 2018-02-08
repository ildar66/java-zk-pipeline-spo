package org.uit.director.plugins.SPO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.TaskInfo;

import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

public class SaveVersion implements PluginInterface {
	private WorkflowSessionContext wsc;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public String execute() {
		logger.info("saveVersion Plugin STARTED");
		try {
			TaskInfo taskInfo = (TaskInfo) this.wsc.getCurrTaskInfo(true);
			
			//читать из маппера
			TaskActionProcessor taskprocessor = (TaskActionProcessor) ActionProcessorFactory
	            .getActionProcessor("Task");
			Task task = taskprocessor.findByPupID(taskInfo.getIdProcess(),true);
			logger.info("saveVersion Plugin mdtaskid = " + task.getId_task().toString());
			long idStage = taskInfo.getIdStageTo();
			Long idUser = taskInfo.getIdExecutor();
			List<Long> roles4user = WPC.getInstance().getIDRolesForUser(idUser);
			String roles = "";
			for(Long idRole : roles4user){
				List<Long> stages4role = WPC.getInstance().getStagesInRole().get(idRole);
				if (stages4role==null) continue;
				for(Long s : stages4role){
					if(s.longValue()==idStage){
						roles += WPC.getInstance().getRoleName(idRole)+"<br />";
					}
				}
			}
			taskprocessor.makeVersion(task.getId_task(),
					idUser,
					taskInfo.getNameStageTo(),
					roles);
			return "ok";
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			
			return "error";
		}
	}

	@SuppressWarnings("unchecked")
	public void init(WorkflowSessionContext wsc, List params) {
		this.wsc = wsc;
		logger.info("PUP plugin saveVersion started");
	}

}
