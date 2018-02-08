package org.uit.director.plugins.CRM;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.TaskInfo;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;

import com.vtb.domain.SpoHistory;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.CRMActionProcessor;
import com.vtb.model.TaskActionProcessor;

/**Отдает статусы в CRM.
 * @author Валиев
 */
public class SetCRMData implements PluginInterface {
	private final String RESULT_OK = "ok";
	private final String RESULT_ERR = "error";
	
	private final static String SEQUENCE_TYPE_CRM_HISTORY = "CRMHistory";
	
	private final static String PUP_PROCESS_PARAM_OPPORTUNITY_ID = "IDCRM_Заявка";
	private final static String PUP_PROCESS_PARAM_STATUS = "Статус";
	
	private WorkflowSessionContext wsc;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public String execute() {
		try {
			logger.info("SetCRMData Plugin STARTED");
			
			TaskInfo taskInfo = (TaskInfo) this.wsc.getCurrTaskInfo(true);
			
			//читать из маппера
			TaskActionProcessor taskprocessor = (TaskActionProcessor) ActionProcessorFactory
                .getActionProcessor("Task");
			String opportunityId = taskprocessor.findByPupID(taskInfo.getIdProcess(),true).getOPPORTUNITYID();
			logger.info("SetCRMData Plugin opportunityId = " + opportunityId);
			
			if (opportunityId == null || opportunityId.equalsIgnoreCase("")) {
				logger.warning("No opportunityCRMId found");
				return RESULT_OK;
			}
			
			String status = taskInfo.getAttributes().getStringValueByName(PUP_PROCESS_PARAM_STATUS);
			logger.info("SetCRMData Plugin status = " + status);
			
			logger.info("SetCRMData Plugin userId = " + this.wsc.getIdUser()); // taskInfo.getIdUser());
			
			String userInfo = this.getUserInfo(WPC.getInstance().getUsersMgr().getLoginByIdUser(wsc.getIdUser()));
			logger.info("SetCRMData Plugin userInfo = " + userInfo);
			
			String stage = taskInfo.getNameStageTo();
			logger.info("SetCRMData Plugin stage = " + stage);
			
			SpoHistory spoHistory = new SpoHistory(
					"",
					userInfo,
					opportunityId,
					status,
					stage,
					new java.sql.Timestamp(new java.util.Date().getTime()-3*60*60*1000));//по Гринвичу
			try {
			    
			    CRMActionProcessor processor = (CRMActionProcessor) ActionProcessorFactory.getActionProcessor("CRM");
			    processor.insertSpoHistory(spoHistory);
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				throw new ModelException(e, ("Exception caught in SpoHistoryMapper.update " + e));
			}
			
			logger.info("SetCRMData Plugin ENDED");
			
			return this.RESULT_OK;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			
			return this.RESULT_ERR;
		}
	}
	
	private String getUserInfo(String userId) {
		logger.info("SetCRMData Plugin getUserInfo STARTED");
		
		StringBuffer result = new StringBuffer("");
		try {
		    CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		    User operator = compenduim.getUser(new User(Integer.valueOf(userId)));
			result.append(operator.getName().getFIO());
		} catch (Exception e) {
			result = null;
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		logger.info("SetCRMData Plugin getUserInfo ENDED");
		if (result != null)
			return result.toString();
		return null;
	}

	public void init(WorkflowSessionContext wsc, List params) {
		this.wsc = wsc;
	}
}
