package org.uit.director.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.domain.crm.StatusReturn;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.spo.utils.SBeanLocator;

import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.CrmFacadeLocal;
import ru.md.spo.ejb.NotifyFacade;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;
import ru.md.spo.ejb.TaskFacadeLocal;

public class ClientRefuseAction extends Action {
	
	private final static Logger LOGGER = Logger.getLogger(ClientRefuseAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String statusReturnId = request.getParameter("StatusReturn");
		String statusReturnName = request.getParameter("nameStatusReturn");
        LOGGER.info("status return id is '" + statusReturnId + "'");
        LOGGER.info("statusReturnName is '" + statusReturnName + "'");
		if(statusReturnId == null && statusReturnName == null) {
			LOGGER.info("status return is null");
			ProcessSearchParam processSearchParam = new ProcessSearchParam(request,false);
		    processSearchParam.saveCookies(response);
			return mapping.findForward("success");
		}
        if (statusReturnId == null)
            statusReturnId = findStatusReturnId(statusReturnName);

		String mdtaskid = request.getParameter("mdtaskid");
		LOGGER.info("mdTaskId is '" + mdtaskid + "'");
		String statusReturnText = request.getParameter("StatusReturnText");
		LOGGER.info("status return text is '" + statusReturnText + "'");
		Date date = Formatter.parseDate(request.getParameter("refuse_date"));
		if (date==null) date = new Date();
		
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		CompendiumCrmActionProcessor compenduimcrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
		
		Task task = processor.getTask(new Task(new Long(mdtaskid)));
		StatusReturn[] allstatus = compenduimcrm.findStatusReturn(null);
		for(StatusReturn sr : allstatus){
			if(statusReturnId != null && sr.getId().equalsIgnoreCase(statusReturnId)) {
				LOGGER.info("found status return id from compendium '" + statusReturnId + "'");
				task.getTaskStatusReturn().setStatusReturn(sr);
				break;
			}
		}
		task.getTaskStatusReturn().setStatusReturnText(statusReturnText);
		task.getTaskStatusReturn().setDateReturn(date);
		processor.updateTask(task);
		LOGGER.info("update task with status return id '" + statusReturnId + "' and status return text '" + statusReturnText + "' completed");
		
		//отправить уведомление пользователям
		User from = compenduim.getUser(new User(wsc.getIdUser()));
		ArrayList<Long> users = processor.findAffiliatedUsers(task.getId_task());
		for (Long userid: users){
			String subject="Отказ Клиента";
			String bodyMessage = "Оформлен отказ Клиента от операции по "+notifyFacade.getTypeNamePraepositionalis(task.getId_task())+
					notifyFacade.getAllContractors(task.getId_task()) + " № <a href=\""+
					notifyFacade.getBaseURL(userid)+"/showTaskList.do?typeList=all&searchNumber="+
					task.getHeader().getNumber().toString() + "&closed=true\">"+
					SBeanLocator.singleton().mdTaskMapper().getNumberAndVersion(task.getId_task())+"</a> "+
					" , контрагентами выступали ";
			for(TaskContractor tc : task.getContractors()){
				bodyMessage+=tc.getOrg().getAccount_name()+"<br />";
			}
			notifyFacade.send(from.getId(),userid, subject, bodyMessage + notifyFacade.getDescriptionTask(task.getId_task()));
		}
		//завершить все этапы, обновить статус процесса
		
		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		pupFacadeLocal.closeProcess(task.getId_pup_process(), wsc.getIdUser());
        String status = statusReturnName==null?"Отказано":statusReturnName;
		pupFacadeLocal.updatePUPAttribute(task.getId_pup_process(), "Статус", status);
		SBeanLocator.getDashboardMapper().fixClientRefused();

		//уведомить crm
		try {
            CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            if (task.isLimit()){
                crmFacadeLocal.exportLimit(task);
            } else {
                crmFacadeLocal.exportProduct(task);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
		wsc.setPageData("Уведомления менеджерам отправлены");
		return mapping.findForward("textPage");
	}
    private String findStatusReturnId(String name) {
        CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        for (ru.masterdm.compendium.domain.crm.StatusReturn statusReturn : compenduim.findStatusReturn("0"))
            if(statusReturn.getDescription().equals(name))
                return statusReturn.getId();
        return null;
    }
}
