package org.uit.director.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.nfunk.jep.JEP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.ProcessControlType;
import org.uit.director.db.dbobjects.VariablesType;
import org.uit.director.db.dbobjects.WorkflowStages;
import org.uit.director.db.dbobjects.WorkflowSubProcess;
import org.uit.director.db.dbobjects.WorkflowVariables;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.decider.BusinessProcessDecider;
import org.uit.director.decider.NextStagesInfo;
import org.uit.director.decider.NextStagesInfo.Statuses;
import org.uit.director.decider.NextStagesTransition;
import org.uit.director.decider.TransitionAction;
import org.uit.director.plugins.commonPlugins.PluginActionImpl;
import org.uit.director.tasks.ProcessInfo;
import org.uit.director.tasks.TaskInfo;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.integration.CCStatus;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.ced.CedService;
import ru.masterdm.integration.ced.ws.CedUser;
import ru.masterdm.integration.mailer.MailerService;
import ru.masterdm.spo.integration.FilialTaskList;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.controller.PipelineController;
import ru.md.helper.TaskHelper;
import ru.md.persistence.UserMapper;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.StageJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ProductTypeJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.StandardPeriodChangeJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.dbobjects.StandardPeriodValueJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.dbobjects.TaskStopFactorJPA;
import ru.md.spo.ejb.DictionaryFacadeLocal;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.Comment;
import com.vtb.domain.Operator;
import com.vtb.domain.SPOMessage;
import com.vtb.domain.Task;
import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.SPOMessageActionProcessor;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.CollectionUtils;
import com.vtb.util.Formatter;

public class TasksCompleteAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksCompleteAction.class.getName());

    private static final Set<String> processForNotifyException = CollectionUtils.set("Крупный бизнес ГО", "Изменение условий Крупный бизнес ГО"); 
    private static final Set<String> stageForNotifyException = CollectionUtils.set("Получение проекта Справки и изменений к Кредитному решению",
    		                                                                       "Консолидация замечаний и направление Справки и изменений к Кредитному решению в связи с изменениями"); 
    
    /*
     * Выполянем подготовку к завершению операции (само завершение - процедура completeTask).
     * Если можем заврешить, то в результате на экране появляется диалог: "будет отправлено туда-то. Подтверждаете?"
     * Если не можем, то сообщение на экране: не можем, потому что...   
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            String target = "acceptedTasks";

            String idTask = request.getParameter("id0");
            String sign = request.getParameter("sign0");
            String commit = request.getParameter("commit");
            LOGGER.info("param export2cc = " + request.getParameter("export2cc"));
            Boolean export2cc = request.getParameter("export2cc")!=null && request.getParameter("export2cc").equals("true");
            //Решение руководителя мидл-офиса & Коллегиальный
            TaskInfoJPA taskInfo = pup.getTask(Long.valueOf(idTask));
            Long processId = taskInfo.getProcess().getId();
            pup.updatePUPAttribute(processId, "Текущая дата", Formatter.formatDateTime(new Date()));
            TaskJPA task = taskFacade.getTaskByPupID(processId);
            if (pup.getPUPAttributeValue(processId, "Коллегиальный").equalsIgnoreCase("y") &&
                    pup.getPUPAttributeValue(processId, "Решение руководителя мидл-офиса").equals("1")){
                export2cc = true;
            }
            WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
            //VTBSPO-1524 По кнопкам "Одобрить" и "Отказать" нужно завершать программно только текущую активную операцию ветки ФКМ БП "Крупный бизнес ГО"
            if((pup.getPUPAttributeValue(processId, "Одобрить").equals("true") 
                            || pup.getPUPAttributeValue(processId, "Отказать").equals("true"))) {
                //closeMemorandum(processId, task, wsc);
            	//передумали
                //передумали обратно. Если еще раз захотим передумать, то давайте сперва подумаем, а затем... только затем поменяем
                //передумали третий раз. В настройку что-ли вынести...
            }
            String stopfactors = "";
            if(taskInfo.getStage().getDescription().startsWith("Получение проекта Справки и изменений к Кредитному решению")
                    		||taskInfo.getStage().getDescription().startsWith("Консолидация замечаний")){
                for(TaskStopFactorJPA taskStopFactor : task.getTaskStopFactors()){
                    if(taskStopFactor.getFlag()!=null && taskStopFactor.getFlag().equals("1")){
                        if (stopfactors!="") stopfactors += "; ";
                        stopfactors += taskStopFactor.getStopFactor().getStopFactor()+" ("+
                            taskStopFactor.getStopFactor().getType().getStopFactorType()+")";
                    }
                }
            }
            
            LOGGER.warn("param refuseMode = " + request.getParameter("refuseMode"));
            boolean refuseMode = request.getParameter("refuseMode")!=null && request.getParameter("refuseMode").equals("true");
            
            LOGGER.info("idTask "+idTask);
            LOGGER.info("sign "+sign);
            LOGGER.info("commit "+commit);
            
            if (wsc.isNewContext())
                return (mapping.findForward("start"));

            //проверяем нужно ли выбрать критерий дифференциации срока
            //этапы на которых нужно выбрать критерий дифференциации срока прямо сейчас
            ArrayList<Long> standardPeriodToDefine = getStandardPeriodToDefine(request, taskInfo, task);
            
            if (idTask != null || commit != null) {
                LOGGER.info("idTask or commit not null");           
                try {
                    String messageForCommit = "";
                    if (commit != null && commit.equals("true") && standardPeriodToDefine.isEmpty()
                            ||commit != null && !commit.equals("true")) {
                        // в случае, если завершаем операцию, а не подготваливаемся к ней.
                        LOGGER.info("commit not null");
                        if (commit.equals("true")) {
                            LOGGER.info("commit equals 'true'");
							//List idStNextList = (List) request.getSession().getAttribute("objectFromAction");
                            @SuppressWarnings("rawtypes")
                            List idStNextList = new ArrayList<NextStagesInfo>();
                            Object[] resDefinNextStages = getNextStages(wsc, Long.valueOf(idTask), sign);
                            if (resDefinNextStages[0] != null && (Integer) resDefinNextStages[0] == 0) {
                            	idStNextList.add(resDefinNextStages[2]);
                            }
                            
                            if (idStNextList != null) {
                                int countNSL = idStNextList.size();
                                LOGGER.info("countNSL: " + countNSL);
                                for (int i = 0; i < countNSL; i++) {
                                    NextStagesInfo stNext = (NextStagesInfo) idStNextList.get(i);
                                    boolean success = setDep(request, stNext);
                                    if (!success) {
                                    	wsc.setErrorMessage("Не указаны подразделения для переходов. Пожалуйста, выберите их из списка");
                                        target = "errorPage";
                                        return (mapping.findForward(target));
                                    }
                                    completeTask(wsc, stNext,export2cc);
                                }
                            }
                            //обновляем комментарий
                            if(request.getParameter("cmnt")!=null){
                                TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
                                Task mdtask = processor.findByPupID(processId,true);
                                String comment = "Обоснование продолжения работ при наличии стоп-факторов:\n ";
                                mdtask.getComment().add(new Comment(null, 
                                		comment + request.getParameter("cmnt"), //комментарий
                                		comment + request.getParameter("cmnt"), //форматированный комментарий
                                        new Operator(wsc.getIdUser().intValue()), 
                                        taskInfo.getStage().getIdStage().intValue(), 
                                        new Timestamp(System.currentTimeMillis())));
                                processor.updateTask(mdtask);
                            }
                        } else {
                            LOGGER.info("commit not equals 'true'");                        
                            request.getSession().setAttribute("objectFromAction",null);
                            
                            if (refuseMode){
                                //очистить секцию
                                clearStatus(pup.getTask(Long.valueOf(idTask)).getProcess().getId());
                                PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                                pupFacadeLocal.updatePUPAttribute(processId, "Decision", "3");
                                pupFacadeLocal.updatePUPAttribute(processId, "Статус", task.getStatus_backup());
                            }
                            response.sendRedirect("task.context.do?id="+idTask);
                            return mapping.findForward("back");
                        }
                    } else {

                        // подготовимся к завершению операции. Получим список операций, на которые будем переходить. 
                        LOGGER.info("commit is null");
                        List<NextStagesInfo> stagesNextInfo = new ArrayList<NextStagesInfo>();

                        Object[] resDefinNextStages = getNextStages(wsc, Long.valueOf(idTask), sign);

                        if (resDefinNextStages[0] != null && (Integer) resDefinNextStages[0] == 0) {
                            messageForCommit += resDefinNextStages[1];
                            stagesNextInfo.add((NextStagesInfo) resDefinNextStages[2]);
                        } else {
                            target = "errorPage";
                            return (mapping.findForward(target));
                        }

                       	messageForCommit += "<br><br> Подтвердить действие?";
                        request.getSession().setAttribute("objectFromAction", stagesNextInfo);
                        LOGGER.info("stagesNextInfo.size(): " + stagesNextInfo.size());
                        request.setAttribute("actionFrom", "task.complete.do");
                        request.setAttribute("message", messageForCommit);
                        request.setAttribute("idTask", idTask);
                        request.setAttribute("export2cc", export2cc);
                        request.setAttribute("standardPeriodToDefine", standardPeriodToDefine);
                        request.setAttribute("idPupProcess", processId);
                        request.setAttribute("stopfactors", stopfactors);
                        if(isConditionValidationStage(taskInfo.getStage().getDescription(), task.getProcessTypeName())){
	                        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	                        request.setAttribute("condition_warning", processor.getTask(new Task(task.getId())).conditionWarning());
                        }
                        target = "commitPage";
                    }
                    
                    LOGGER.info("complete action. Sending to page alias: " + target);

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    e.printStackTrace();
                    wsc.setErrorMessage("Ошибка завершения операции. "+e.getMessage(),taskInfo.getIdTask());
                    target = "errorPage";
                }
            }
            //если это особый БП для филиалов, то переходим на портал, а не к списку в работе
            if(task!=null && task.getProcess()!=null && task.getProcess().getProcessType().isPortalProcess()){
            	response.sendRedirect(FilialTaskList.kodUrl+task.getId().toString());
            	return null;
            }
            return (mapping.findForward(target));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Разрешено ли аттрибутами выбирать критерий дифференциации для этого этапа.
     * VTBSPO-1595 & VTBSPO-1641
     */
    private boolean isAttrPermitDefineStandardPeriod(TaskJPA task, String groupName) throws FactoryException {
    	PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
    	if(pup.getPUPAttributeBooleanValue(task.getProcess().getId(), "Отправить на доработку"))//VTBSPO-1641
    		//в параметр "дополнение" будут определены наименования тех этапов, для которых не требуется выводить критерии дифференциации
    		for(WorkflowVariables var : WPC.getInstance().getVariablesForTypeProcess(task.getProcess().getProcessType().getIdTypeProcess().intValue()))
    			if(var.getName().equals("Отправить на доработку"))
    				for(String s : var.getAddition().split(";"))
    	    			if(groupName.equalsIgnoreCase(s))
    	    				return false;
    	for(WorkflowVariables var : WPC.getInstance().getVariablesForTypeProcess(task.getProcess().getProcessType().getIdTypeProcess().intValue())){
    		String addition = var.getAddition();
    		String varName = var.getName();
    		LOGGER.info(varName);
    		if(!addition.startsWith("100;") && !addition.startsWith("0;")) continue;
    		for(String s : addition.split(";"))
    			if(groupName.equalsIgnoreCase(s)){
    				boolean val = pup.getPUPAttributeBooleanValue(task.getProcess().getId(), var.getName());
    				if(val &&  addition.startsWith("0;"))
    					return false;
    				if(addition.startsWith("100;"))
    					return val;
    			}
    	}
    	return true;
    }
    private ArrayList<Long> getStandardPeriodToDefine(
            HttpServletRequest request, TaskInfoJPA taskInfo, TaskJPA task) throws FactoryException {
        ArrayList<Long> standardPeriodToDefine = new ArrayList<Long>();
        StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
        if(task.getActiveStandardPeriodVersion()!=null){
        	boolean need2select = false;
            TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            for (StandardPeriodGroupJPA group : task.getActiveStandardPeriodVersion().getStandardPeriodGroups()){
                if(/*есть что выбирать*/group.getDecisionStages().contains(taskInfo.getStage())
                        && group.getValues().size()>1
                        && !spLocal.isGroupActive(group.getId(), task.getProcess().getId())
                        && isAttrPermitDefineStandardPeriod(task,group.getName())){
                	standardPeriodToDefine.add(group.getId());
                    //Только что выбрали?
                    if(request.getParameter("criterium"+group.getId().toString())!=null
                            &&!request.getParameter("criterium"+group.getId().toString()).equals("")){
                        for (StandardPeriodValueJPA value : group.getValues())
                            if(value.getId().toString().equals(request.getParameter("criterium"+group.getId().toString()))){
                                StandardPeriodChangeJPA change = new StandardPeriodChangeJPA();
                                change.setTask(task);
                                change.setValue(value);
                                change.setWhenChange(new Date());
                                change.setWhoChange(taskInfo.getExecutor());
                                taskFacade.persist(change);
                                task.getStandardPeriodDefined().add(change);
                                taskFacade.merge(task);
                            }
                    } else {
                    	need2select = true;
                    }
                }
            }
            if(!need2select)
            	return new ArrayList<Long>();
        }
        return standardPeriodToDefine;
    }

    /**
     *завершить заявку с нужным статусом
     */
    @SuppressWarnings("unused")
	private void closeMemorandum(Long processId, TaskJPA task,WorkflowSessionContext wsc) throws FactoryException {
        PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        if (pup.getPUPAttributeValue(processId, "Одобрить").equals("true")) {
            pup.updatePUPAttribute(processId, "Статус", "Одобрено");
        } else {
            pup.updatePUPAttribute(processId, "Статус", "Отказано");
        }
        pup.closeMemorandum(processId, wsc.getIdUser());
    }

    /**
     * Завершить выполнение операции. Список этапов, на которые переходим, хранится в stNext
     * (вычисляется ранее)
     * @return всегда 0  
     */
    private int completeTask(WorkflowSessionContext wsc, NextStagesInfo stNext, boolean export2cc) throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");

        Long idTaskCompleted = stNext.getIdTask();
        wsc.setIdCurrTask(idTaskCompleted);
        TaskInfo taskInfo = (TaskInfo) wsc.getCurrTaskInfo(true);
        TaskJPA task = taskEJB.getTaskByPupID(taskInfo.getIdProcess());
        Long idMdTask = task.getId();

        LOGGER.info("idTaskCompleted "+idTaskCompleted);
        LOGGER.info("taskInfo.IdStageTo "+taskInfo.getIdStageTo());
        LOGGER.info("taskInfo.NameStageTo "+taskInfo.getNameStageTo());
        LOGGER.info("taskInfo.NameTypeProcess "+taskInfo.getNameTypeProcess());
        LOGGER.info("taskInfo.IdDepartament "+taskInfo.getIdDepartament());
        LOGGER.info("taskInfo.IdExecutor "+taskInfo.getIdExecutor());
        LOGGER.info("taskInfo.IdProcess "+taskInfo.getIdProcess());
        LOGGER.info("taskInfo.IdTask "+taskInfo.getIdTask());
        LOGGER.info("taskInfo.IdTypeProcess "+taskInfo.getIdTypeProcess());
        LOGGER.info("taskInfo.IdUser "+taskInfo.getIdUser());
        LOGGER.info("taskInfo.StatusProcess "+taskInfo.getStatusProcess().value);
        //возможно придется изменить pipeline
        for(NextStagesTransition next : stNext.getStages())
        	PipelineController.onTaskComplete(next.getNameStage(), taskInfo.getIdProcess(), taskInfo.getNameStageTo());

        DBFlexWorkflowCommon flexDb = wsc.getDbManager().getDbFlexDirector();
        // Добавлено [MK, 20100119] Чтобы убрать остаточные непонятные вызовы и состояния request, 
        // проверим реальное состояние task (никому не верю!). 
        // И завершаем задачу, только если статус = активно, переназначено или ожидание 
        // неохота цеплять TaskInfo, это может порушит передачу параметров у request и обратно 
        WorkflowTaskInfo realTaskInfo = pupFacadeLocal.getTaskInfo(idTaskCompleted);
        int idStatus = realTaskInfo.getIdStatus().intValue();
        LOGGER.info("realTaskInfo.TaskStatus "+idStatus);
        if (idStatus==2 ||idStatus==6 || idStatus==7 || idStatus==8) {
            List<NextStagesTransition> stages = stNext.getStages();
            for (NextStagesTransition transition : stages) {
                LOGGER.info("NextStagesTransition.Id: " + transition.getIdStage());
                LOGGER.info("NextStagesTransition.Name: " + transition.getNameStage());
                LOGGER.info("NextStagesTransition.IdDep: " + transition.getIdDepartament());
            }
            
            // Выполним все действия перед входом на новые этапы
            LOGGER.info("PluginActionImpl.executePluginAction. stNext.size(): " + stNext.getStages().size());
            try {
                PluginActionImpl.executePluginAction(wsc, stNext.getStages());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
    
            pupFacadeLocal.updatePUPAttribute(taskInfo.getIdProcess(), 
            		"Текущая дата", Formatter.formatDateTime(new Date()));
            // выполнить действие на переходах
            try {
                executeActionsOnTransition(flexDb, taskInfo, stNext);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            //проверим режим сброса статусов в атрибуте Decision
            String decision = pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(), "Decision");
            if(decision.equals("11") || decision.equals("5")){
                try {
                    clearStatus(taskInfo.getIdProcess());
                } catch (Exception e) {
                    LOGGER.error( e.getMessage(), e);
                }
                //При Decigion=11 данные обсуждаемой секции очищаются программно, 
                //одновременно с этим переменной Decigion присваивается значение 3. VTBSPO-852
                if (decision.equals("11")) 
                    pupFacadeLocal.updatePUPAttribute(taskInfo.getIdProcess().longValue(), "Decision", "3");
            }

            Lock lock = new ReentrantLock();
            lock.lock();
            try {
                int idStatusSync = pupFacadeLocal.getTaskInfo(idTaskCompleted).getIdStatus().intValue();
                if (idStatusSync==2 ||idStatusSync==6 || idStatusSync==7 || idStatusSync==8) {
                    if (export2cc && SBeanLocator.singleton().mdTaskMapper().getById(idMdTask).getIdInstance() == null) {
                        try {
                            // create проект решения
                            processor.export2cc(idMdTask, wsc.getIdUser());
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            e.printStackTrace();
                            throw new Exception(e.getMessage());
                        }
                    }
                    //Собственно, завершить выполнение операции, инициализировав новые операции
                    pupFacadeLocal.completeWorks(stNext.getParamsForLoad(), idTaskCompleted);
                }
            } finally {
                lock.unlock();
            }
                
            // если был запущен подпроцесс-запускаем его.
            // если завершаем подпроцесс-передаем управление главному процессу
            LOGGER.info("startEndSubProcess");
            startEndSubProcess(wsc, taskInfo, stNext);
    
            //проверить статус. Если не поменялся, то поменять вручную.
            pupFacadeLocal.setTaskStatus(3L, idTaskCompleted);
            //пересчитать дедлайн для незавершенных операций
            StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
            spLocal.recalculateDeadline(taskInfo.getIdProcess());
            LOGGER.info("Notify");
            sendNotify(wsc, taskInfo, stNext.getStages());
            //отозвать назначение с экспертиз
            try {
            	pupFacadeLocal.removeAssignOnExpertiseStage(idTaskCompleted, stNext.getStageIds());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
            
            long idStageTo = ((TaskInfo) taskInfo).getIdStageTo();
            // Выполним действия после выхода с этапа
            LOGGER.info("PluginActionImpl.executePluginAction.classExit");
            try {
                PluginActionImpl.executePluginAction(wsc, idStageTo, Cnst.TStages.classExit);
            } catch (Exception e) {
            	LOGGER.error(e.getMessage(), e);
            }
    
            wsc.getCacheManager().deleteCacheElement(idTaskCompleted);
        } else {
            LOGGER.warn("ATTENTION!!! A TRY TO COMPLETE Task with status "+idStatus);
            //для тестирования в банке throw new Exception("Невозможно завершить эту операцию. Она уже завершена (статус="+idStatus+")");
        }

        return 0;
    }

    /**
     * очистить секцию
     */
    private void clearStatus(Long idProcess) throws MappingException,
            Exception {
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        Task task = processor.findByPupID(idProcess, true);
        task.getTaskStatusReturn().setDateReturn(null);
        task.getTaskStatusReturn().setStatusReturn(null);
        task.getTaskStatusReturn().setStatusReturnText(null);
        processor.updateTask(task);
        processor.statusNotification(new CCStatus(),task.getId_task());
    }

    /**
     * Проверка имени процесса и имени операции на попадание в исключения (для отмены отсылки уведомлений).
     */
    private boolean isProcessAndStageForExclusion(String processName, String stageName) {
    	if (processName == null || stageName == null)
    		return false;
    	return (processForNotifyException.contains(processName) && stageForNotifyException.contains(stageName));
    }

    /**
     * Проверка нужно ли пропустить уведомление.
     * Уведомления пропускаются, если на собирающей операции есть родительские активные операции,
     * т.е. не все родительские операции выполнены.
     * Правило действует для БП "Крупный бизнес ГО" операция "Получение проекта Справки и изменений к Кредитному решению"
     * и БП "Изменение условий Крупный бизнес ГО" операция "Консолидация замечаний и направление Справки и изменений к Кредитному решению в связи с изменениями"
     */
    private boolean skipNotification(Long idStage, PupFacadeLocal pupFacadeLocal, TaskJPA taskJPA, TaskInfo taskInfo) {
    	boolean result = false;
    	try {
    		if (idStage == null || pupFacadeLocal == null || taskJPA == null)
    			return result;
        	String processName = taskJPA.getProcessTypeName();
        	
        	StageJPA stage = pupFacadeLocal.getStage(idStage);
        	if (stage == null)
        		return result;
        	String stageName = stage.getDescription();

        	//проверка процесса и операции
        	if (isProcessAndStageForExclusion(processName, stageName) == false)
        		return result;

        	StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
        	Set<Long> waitStagesIds = spLocal.getCollectStages(pupFacadeLocal.getStage(idStage));
        	if (waitStagesIds!= null) {
        		for (Long idWaitStage : waitStagesIds) {
        			StageJPA waitStage = pupFacadeLocal.getStage(idWaitStage);
            		if (waitStage == null)
            			continue;
        			if (pupFacadeLocal.isHasActiveTask(taskInfo.getIdProcess(), waitStage.getIdStage())) {
        				result = true;
        				break;
        			}
        		}
        	}
    		
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    	return result;
    }

    /**
     * Отправка уведомлений 
     *  оповещении всех потенциальных пользователей, которые видят операции в представлении "ожидающие обработки"
     *  оповещение всех назначенных исполнителей, при поступлении операций в представление "назначенные мне"
     *  и даже оповещение секретарей о начале нового этапа или завершении текущего
     */
    private void sendNotify(WorkflowSessionContext wsc, TaskInfo taskInfo, List<NextStagesTransition> stages) {
        try {
            TaskActionProcessor taskprocessor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA taskJPA = taskFacadeLocal.getTaskByPupID(taskInfo.getIdProcess());
            User from = compenduim.getUser(new User(wsc.getIdUser()));

            
            for (NextStagesTransition nst : stages) {//по каждому этапу
                if (nst.getIdStage() != null) {
                	Long idStage = nst.getIdStage();
                	if (skipNotification(idStage, pupFacadeLocal, taskJPA, taskInfo))
                		continue;
                	/*
                    try {
                    	//если это операция сбора, то уведомляем только если завершены все операции, которых ждём МО.165
                    	boolean existWaitStage = false;
                    	StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
                    	Set<StageJPA> collectStages = spLocal.getCollectStages(pupFacadeLocal.getStage(idStage));
                    	if(collectStages!= null)
                    		for (StageJPA waitStage : collectStages)
                    			if(pupFacadeLocal.isHasActiveTask(taskInfo.getIdProcess(), waitStage.getIdStage()))
                    				existWaitStage = true;
                    	if(existWaitStage)
                    	    continue; //не уведомлять
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
                    	 */
                    Long idDepartament = nst.getIdDepartament();
                    if (idDepartament == null) {
                        idDepartament = taskInfo.getIdDepartament();
                    }
                    Task task = taskprocessor.findByPupID(taskInfo.getIdProcess(), false);
                    String subjectMessageFormat = "";
                    String bodyMessageFormat = "";
                    //проверка есть ли назначенные
                    HashMap<Long,String> assignUsers = taskprocessor.findAssignUser(idStage, taskInfo.getIdProcess());
                    boolean assignMode = true;
                    if (assignUsers!=null && assignUsers.size()!=0){//Уведомляем назначенных
                        subjectMessageFormat = SPOMessage.executionSubjectMessageFormat;
                        bodyMessageFormat = SPOMessage.executionBodyMessageFormat;
                    } else {// если нет назначенных, то уведомляем всех, у кого права
                        subjectMessageFormat = SPOMessage.waitSubjectMessageFormat;
                        bodyMessageFormat = SPOMessage.waitBodyMessageFormat;
                        assignUsers = findUser(idStage, idDepartament);
                        assignMode = false;
                    }
                    String subject = MessageFormat.format(subjectMessageFormat,
                            notifyFacade.getName(task.getId_task()));
                    Iterator<Long> iter = assignUsers.keySet().iterator();
                    while (iter.hasNext()) {
                        Long userid = iter.next();
                        if (!assignMode && pupFacadeLocal.getUser(userid).isWorker(taskInfo.getIdTypeProcess().longValue()))
                            continue;
                        @SuppressWarnings("unused")
                        String url = pupFacadeLocal.getBaseURL(userid);
                        String bodyMessage = MessageFormat.format(
                            bodyMessageFormat,
                            pupFacadeLocal.getBaseURL(userid),
                            task.getHeader().getNumber().toString(),
                                SBeanLocator.singleton().mdTaskMapper().getNumberAndVersion(task.getId_task()),
                            task.getOrganisation(),
                            nst.getNameStage(),
                            taskInfo.getNameTypeProcess(),
                                taskJPA.getType()
                         );
                        notifyFacade.send(from.getId(),userid,subject,bodyMessage
                                + notifyFacade.getDescriptionTask(task.getId_task()));
                    }
                    notifyFacade.notifySecretaryNewStage(taskInfo.getIdProcess(), from, nst.getIdStage(), taskInfo.getIdStageTo(), nst.getIdDepartament());
                }
            }
            //оповещение секретарей о завершении текущего этапа
            notifySecretary(taskInfo, stages, from);
            //В рамках бизнес-процессов СПО «Крупный бизнес ГО» и «Изменение условий Крупный бизнес ГО»
            //переходе на «Формирование Кредитного меморандума завершено».
            //необходимо направлять уведомление «Работнику мидл-офиса», назначенному на обработку заявки, о завершении формирования кредитного меморандума.
            UserMapper userMapper = (UserMapper) SBeanLocator.singleton().getBean("userMapper");
            if (taskJPA.getProcessTypeName().equals("Крупный бизнес ГО")||taskJPA.getProcessTypeName().equals("Изменение условий Крупный бизнес ГО"))
                for (NextStagesTransition nst : stages)
                    if (nst.getNameStage().equals("Формирование Кредитного меморандума завершено") && taskJPA.getIdProcess() != null)
                        for (Long idUser : userMapper.userAssigned(taskJPA.getIdProcess()))
                            if (userMapper.userAssignedAs(idUser, taskJPA.getIdProcess()).contains("Работник мидл-офиса"))
                                notifyFacade.send(from.getId(), idUser,
                                      " Завершено формирование Кредитного меморандума по " + notifyFacade.getNamePraepositionalis(taskJPA.getId()),
                                      "Завершено формирование Кредитного меморандума  по " +
                                      notifyFacade.getTypeNamePraepositionalis(taskJPA.getId())+" с "+taskJPA.getOrganisation()+
                                      " № <a href=\""+pupFacadeLocal.getBaseURL(idUser)+"/showTaskList.do?typeList=all&searchNumber="+
                                      taskJPA.getMdtask_number().toString()+"\">"+ taskJPA.getNumberAndVersion()+"</a> "
                                      + " (см. список заявок \"Все заявки подразделения\") <br />"
                                      + notifyFacade.getDescriptionTask(taskJPA.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
        }
    }

    /** оповещение секретарей о завершении текущего этапа */
    private void notifySecretary(TaskInfo taskInfo, List<NextStagesTransition> stages, User from) throws Exception {
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskJPA taskJPA = taskFacadeLocal.getTaskByPupID(taskInfo.getIdProcess());
        StandardPeriodGroupJPA currentGroup = pupFacadeLocal.getGroup4Stage(taskInfo.getIdStageTo(), taskInfo.getIdProcess());
        if(isFinalStageOperation(stages,currentGroup)){
            LOGGER.info("secretary: этап завершился "+currentGroup.getName() + ". taskInfo.getIdDepartament()="+taskInfo.getIdDepartament());
            for(Long idSecretary : notifyFacade.getSecretaryIds(taskInfo.getIdTypeProcess().longValue(), taskInfo.getIdDepartament())){
                UserJPA secretary = pupFacadeLocal.getUser(idSecretary);
                //Шаблон 6
                notifyFacade.send(from.getId(), secretary.getIdUser(),
                        "Завершение обработки " + notifyFacade.getNameGenitive(taskJPA.getId()),
                        "Завершен этап: \""+currentGroup.getName()+"\" по " + notifyFacade.getTypeNamePraepositionalis(taskJPA.getId()) +
                                " с " +taskJPA.getOrganisation()
                                +" № <a href=\""+
                        pupFacadeLocal.getBaseURL(secretary.getIdUser())+"/showTaskList.do?typeList=all&searchNumber="+
                            taskJPA.getMdtask_number().toString()+"\">"+
                                taskJPA.getNumberAndVersion()+"</a> "
                        + " (см. список заявок \"Все заявки подразделения\") <br />"
                        + "в подразделении: "+pupFacadeLocal.getDepartmentById(taskInfo.getIdDepartament()).getShortName()
                        + "<br />Исполнитель: "+from.getName().getFIO()
                                + notifyFacade.getDescriptionTask(taskJPA.getId()));
            }
            //VTBSPO-1225 СПО 143. СПО.ОЭ.255. Информирование проектной команды о завершении экспертиз
            if (currentGroup.isExpertGroup() &&
                    (taskJPA.getProcessTypeName().equals("Крупный бизнес ГО")||taskJPA.getProcessTypeName().equals("Изменение условий Крупный бизнес ГО"))) {
                for(ProjectTeamJPA pt : taskJPA.getProjectTeam("p")){
                    notifyFacade.send(from.getId(), pt.getUser().getIdUser(),
                      "Завершена " + currentGroup.getName() + " по " + notifyFacade.getNamePraepositionalis(taskJPA.getId()),
                      "Завершена " + currentGroup.getName() + " по " +
                      notifyFacade.getTypeNamePraepositionalis(taskJPA.getId())+" с "+taskJPA.getOrganisation()+
                      " № <a href=\""+pupFacadeLocal.getBaseURL(pt.getUser().getIdUser())+"/showTaskList.do?typeList=all&projectteam=true&searchNumber="+
                      taskJPA.getMdtask_number().toString()+"\">"+
                      taskJPA.getNumberAndVersion()+"</a> "
                      + " (см. список заявок \"работа проектной команды\") <br />"
                      + "в подразделении: "+pupFacadeLocal.getDepartmentById(taskInfo.getIdDepartament()).getShortName()+
                      "<br />Исполнитель: "+from.getFullName()+
                      notifyFacade.getDescriptionTask(taskJPA.getId()));
                }
            }
        }
    }

    /**
   	 * Ищет пользователей для конкретного этапа конкретной заявки, у которых
   	 * есть права взять её. Но кроме тех, которые по этой операции простые пользователи.
   	 * Письмо от Князевой от 20.10.2014 с темой [СПО 16.34fix3]
   	 * @return HashMap список id,email пользователей
   	 */
   	public HashMap<Long, String> findUser(Long idStage, Long idDepartament) throws ModelException {
   		if(idDepartament==null)
   			return new HashMap<Long,String>();
   		TaskActionProcessor taskprocessor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
   		HashMap<Long,String> usersInDep = taskprocessor.findUser(idStage, idDepartament);
   		Long parentDepId = TaskHelper.pup().getDepartmentById(idDepartament).getParentDepartment()==null?null:
   			TaskHelper.pup().getDepartmentById(idDepartament).getParentDepartment().getIdDepartment();
   		if(usersInDep.size()==0)
   			return findUser(idStage,parentDepId);
   		return usersInDep;
   	}
	
	private boolean isFinalStageOperation(List<NextStagesTransition> stages,
			StandardPeriodGroupJPA currentGroup) {
		if(currentGroup == null)
			return false;
		if(stages==null)
			return true;
		//все операция, на которые осуществляем переход, не входят в текущий этап
		for (NextStagesTransition nst : stages){
			if(nst==null || nst.getIdStage()==null)
				return true;
			for(StageJPA stage : currentGroup.getStages())
				if(stage.getIdStage().equals(nst.getIdStage()))
					return false;
		}
		return true;
	}

    /*
     * Получить список операций, которые будут выполняться после завершения этой операции
     * (реально вызывается  BusinessProcessDecider.getNextStageAfterComplation)
     */
    private Object[] getNextStages(WorkflowSessionContext wsc, Long idTask, String sign) throws Exception {
        Object[] res = new Object[3];
        if (sign == null) {
            try {
            sign = new String(wsc.getSignum());
            wsc.setSignum(null);
            } catch (Exception e) {
                sign = null;
                LOGGER.info("wsc.getSignum(): NullPointerException");
            }
            
        }
        if (sign == null || sign.equals("")) {
            if (Config.getProperty("VALIDATE_SIGNUM").equalsIgnoreCase("true")) {
                wsc.setErrorMessage("Действие не подписано, операция не может быть выполнена");
                res[0] = 1;
                return res;
            } else {
                sign = "";
            }
        }

        NextStagesInfo nextStagesInfo = BusinessProcessDecider.getNextStageAfterComplation(wsc, idTask, sign);

        // Проверим условия, может ли завершиться сама операция. И заполним поля возвращаемой структуры res
        // задание может завершиться
        if (nextStagesInfo.getResult() == 0) {
            // stagesNextInfo.add(nextStagesInfo);
            res[0] = 0;
            // nextStagesInfo.getMessage()
            res[1] = generateMessageExtended(wsc, nextStagesInfo);
            res[2] = nextStagesInfo;
            // messageForCommit += nextStagesInfo.getMessage();

        } else {
            // задание не может завершиться
            wsc.setErrorMessage(nextStagesInfo.getMessage(), idTask);
            res[0] = 1;
            return res;
        }
        return res;
    }

    /**
     * Generate html-markup text of the message sith departments to go to etc...
     * @param nextStagesInfo
     * @return
     */
    private String generateMessageExtended(WorkflowSessionContext wsc, NextStagesInfo nextStagesInfo) {
        if ((nextStagesInfo == null) || (nextStagesInfo.getStages() == null)) return null;
        TaskInfoJPA taskInfo = null;
        try {
            taskInfo = TaskHelper.pup().getTask(((TaskInfo) wsc.getCurrTaskInfo(true)).getIdTask());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        HashMap<Long, LinkedHashMap<Long,String>> depForStages = new HashMap<Long, LinkedHashMap<Long,String>>(); 
        DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();
        
        for (NextStagesTransition tr : nextStagesInfo.getStages()) {
            try {
                LinkedHashMap<Long, String> map = 
                    dbFlexDirector.findDepartmentsForTransition(taskInfo.getStage().getIdStage(), tr.getIdStage(), taskInfo.getIdDepartament());
                boolean noRolesFound = false;
                if (map == null) {
                	map = new LinkedHashMap<Long, String>();
                	noRolesFound = true;
                }
                LinkedHashMap<Long, String> visitedDepartments = //если переходим на экспертизу, а текущая операция не экспертиза, то выводим полный список подразделений
                		(TaskHelper.pup().isExpertiseStage(tr.getIdStage(), taskInfo.getProcess().getId()) 
                				&&!TaskHelper.pup().isExpertiseStage(taskInfo.getStage().getIdStage(), taskInfo.getProcess().getId()))?
                		new LinkedHashMap<Long, String>():
                    dbFlexDirector.getVisitedDepartments(taskInfo.getProcess().getId(), tr.getIdStage());
                
                // makes a copy of the set. Clone doesn't work here
                Set<Long> visitedSet = new LinkedHashSet<Long>();
                for (Long key : visitedDepartments.keySet()) visitedSet.add(key);     

                // finds intersection between the visited departments and possible departments.  
                visitedSet.retainAll(map.keySet());
                if (visitedSet.size() > 0) {
                    // there exist departments where we have been to already.  
                    // limit the list of possible departments with these ones.
                    LinkedHashMap<Long, String> resultMap = new LinkedHashMap<Long, String>(); 
                    for (Long dep : visitedSet) resultMap.put(dep, map.get(dep));
                    map = resultMap;
                }
                /*Если для следующей операции бизнес-процесса в Проектной команде присутствует
				Пользователь с ролью, необходимой для выполнения операции, у которого установлен 
				признак «Выполнение операции», то данная операция должна назначаться этому 
				Пользователю, подразделение устанавливается соответственно, возможности изменить не 
				предлагается.*/
                try {
					Long idDep = getDepartmentForProjectTeamMember(taskInfo.getProcess().getId(), tr.getIdStage());
					if(idDep!=null) {
						map = new LinkedHashMap<Long, String>();
						PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
						map.put(idDep, pupFacadeLocal.getDepartmentById(idDep).getShortName());
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
                
                if (map.size() == 0) {
                    // no department for transition is found. 
					if (noRolesFound) {
	                	//no roles. Preserve the SAME department 
	                    map.put(taskInfo.getIdDepartament(),
	                            // it's always in the list of the visited departments
	                    		dbFlexDirector.getVisitedDepartments(taskInfo.getProcess().getId(), tr.getIdStage()).get(taskInfo.getIdDepartament())
	                    );
	                    depForStages.put(tr.getIdStage(), map);
					} else {
						// no users found. Don't let 
						map.put(-1L, "No users found (Нет пользователей, которые могут взять операцию в работу. Необходимо назначить пользователям роли, которые смогут выполнить операцию)");
						depForStages.put(tr.getIdStage(), map);
					}
                } else if (map.size() == 1) {
                	// well, only 1 element in the list.
                	depForStages.put(tr.getIdStage(), map);
                } else {
                    // add empty department in the beginning.
                	LinkedHashMap<Long, String> depWithEmptyValue = new LinkedHashMap<Long, String>();    
                    depWithEmptyValue.put(-1L, "  ");
                    depWithEmptyValue.putAll(map);
                    depForStages.put(tr.getIdStage(), depWithEmptyValue);
                }
                
            } catch (RemoteException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }
            try {
            	TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            	TaskJPA task = taskFacade.getTaskByPupID(taskInfo.getProcess().getId());
            	if(tr.getNameStage().equals("Контроль технической исполнимости") && task.isLimit()) {
            		//департамент из настроек для лимита
            		LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>();
            		String shortName = taskFacade.getGlobalSetting("tech_ispoln");
            		DepartmentJPA dep = dict().findDepartmentByShortName(shortName);
            		map.put(dep.getIdDepartment(), dep.getFullName());
            		depForStages.put(tr.getIdStage(), map);
            	}
            	if(tr.getNameStage().equals("Контроль технической исполнимости") && task.isProduct()) {
            		for(ProductTypeJPA product : task.getProductTypes()) {
            			LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>();
            			DepartmentJPA dep = dict().findDepartmentByProduct(product.getId());
            			map.put(dep.getIdDepartment(), dep.getFullName());
            			depForStages.put(tr.getIdStage(), map);
            		}
            	}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
        }
        return getMessageExtended(depForStages,nextStagesInfo);
    }
    public DictionaryFacadeLocal dict(){
        try {
            return com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
    /**
	 * Generate list of departments for transition to choose from
	 * @return html-markup of drop-sown list or hidden input field, respectively.
	 */
	private String generateDepartmentsList(NextStagesTransition nst, LinkedHashMap<Long,String> depsMap, Long idTask) {
        StringBuilder sb = new StringBuilder(); 
	    sb.append("<td align=\"right\">");
        if (depsMap == null) {
            sb.append("</td>"); 
            return null;
        }
        Long depId = null;
        Long defaultDep = null;
        try {
        	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        	TaskJPA task = taskFacadeLocal.getTaskByPupID(pupFacadeLocal.getTask(idTask).getProcess().getId());
        	defaultDep = dict().findSpoRoute(nst.getNameStage(), task.getInitDepartment().getIdDepartment(), 
        			task.getProcess().getProcessType().getIdTypeProcess());
        	if(defaultDep!=null && !depsMap.keySet().contains(defaultDep) && getDepartmentForProjectTeamMember(task.getProcess().getId(), nst.getIdStage()) == null)
        		depsMap.put(defaultDep, pupFacadeLocal.getDepartmentById(defaultDep).getShortName());
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        }
        if (depsMap.size() == 1) {
            // only one deparment. Don't ask user.
            Iterator<Long> it = depsMap.keySet().iterator();
            if (it.hasNext()) {
                depId = it.next();
                String to = depsMap.get(depId);
                sb.append("<b><i>" + (((to == null) || (to.equals(""))) ? " " : to) + "</i></b>"
                        + "<input type=\"hidden\" name=\"stageTo" + nst.getIdStage() + "\" value=\"" + depId + "\">"
                );
            }
        } else {
    	    // many departments. Drop-down list
            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<select name=\"stageTo" + nst.getIdStage() + "\" >");
            Iterator<Long> it = depsMap.keySet().iterator();
            while (it.hasNext()) {
                depId = it.next();
                String to = depsMap.get(depId);
                String selected = depId.equals(defaultDep)?"selected":"";
                sb.append("<option "+selected+" value=\"" + depId + "\">" + (((to == null) || (to.equals(""))) ? " " : to) + "</option>");   
            }           
            sb.append("</select>");
        }
        sb.append("</td>");
        return sb.toString();
	}
	public String getMessageExtended(HashMap<Long, LinkedHashMap<Long,String>> depForStages,NextStagesInfo nextStagesInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"tabledata\"><table><caption>Задание № ").append(nextStagesInfo.getIdTask()).append("</caption>");

        for (NextStagesTransition nst : nextStagesInfo.getStages()) {
            if (!nst.isAutoStage()) {
                sb.append("<tr>");
                sb.append(nst.getMessage());
                sb.append(generateDepartmentsList(nst, depForStages.get(nst.getIdStage()), nextStagesInfo.getIdTask()));
                sb.append("</tr>");
            }
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    /*Если для следующей операции бизнес-процесса в Проектной команде присутствует
	Пользователь с ролью, необходимой для выполнения операции, у которого установлен 
	признак «Выполнение операции», то данная операция должна назначаться этому 
	Пользователю, подразделение устанавливается соответственно, возможности изменить не 
	предлагается.*/
	public Long getDepartmentForProjectTeamMember(Long idProcess, Long nextStageId) {
		if(nextStageId==null)
			return null;
		StageJPA stage = TaskHelper.pup().getStage(nextStageId);
		for (RoleJPA role : stage.getRoles()){
			if(!TaskHelper.dict().findProjectTeamRoles().contains(role.getNameRole()))
				continue;
			for(ProjectTeamJPA projectTeam : TaskHelper.taskFacade().getTaskByPupID(idProcess).getProjectTeam())
				if(TaskHelper.pup().isAssigned(projectTeam.getUser().getIdUser(), role.getIdRole(), idProcess))
					return projectTeam.getUser().getDepartment().getIdDepartment();
		}
		return null;
	}
    
    private void startEndSubProcess(WorkflowSessionContext wsc, ProcessInfo taskInfo, NextStagesInfo stNext) throws Exception {
        WPC wpc = WPC.getInstance();
        for (NextStagesTransition nst : stNext.getStages()) {

            DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();
            // если следующий этап-подпроцесс
            if (nst.getStatus() == Statuses.SEND_SUB_PROCESS) {

                // taskInfo - информация по главному процессу
                String nameSubProcess = nst.getNameStage();
                Integer idTypeProcSub = wpc.getIdTypeProcessByDescription(nameSubProcess);
                /*
                 * Long idStageBegin = BusinessProcessDecider
                 * .getStartIdStage(idTypeProcSub);
                 */

                ProcessControlType controlType = wpc.getControlType(idTypeProcSub);
                ArrayList<Object[]> params = getParamsForUpdateAttributesSubProcess(taskInfo, idTypeProcSub, true, null);
                LOGGER.info("dbFlexDirector.createProcessAndSetAttributes(" + idTypeProcSub + ", " + taskInfo.getIdProcess());
                dbFlexDirector.createProcessAndSetAttributes(idTypeProcSub,taskInfo.getIdProcess(), null, params, controlType);
            }

            Long idParProcess = taskInfo.getIdParentProcess();
            // если процесс завершается и он является подпроцессом
            if (nst.getStatus() == Statuses.COMPLETE && idParProcess != null) {

                // taskInfo - информация по подпроцессу

                // получим данные по родительскому процессу
                ProcessInfo processParInfo = new ProcessInfo();
                processParInfo.init(wsc, idParProcess, null, true);
                processParInfo.execute();

                // проверим есть ли в главном процессе активный подпроцесс
                // с таким же именем
                Long activeIdStageSP = getActiveIdStageSP(processParInfo
                        .getActiveStages(), taskInfo.getNameTypeProcess());

                if (activeIdStageSP != null) {

                    Integer idTypeProcessParent = processParInfo.getIdTypeProcess();
                    // получим данные для обновления атрибутов в главном
                    // процессе
                    ArrayList<Object[]> param = getParamsForUpdateAttributesSubProcess(
                            taskInfo, idTypeProcessParent, false, idParProcess);

                    ProcessControlType controlType = wpc.getControlType(idTypeProcessParent);

                    // обновляем атрибуты в главном процессе после окончания
                    // подпрцесса
                    dbFlexDirector.updateAttributes(param, controlType);

                    // завершаем задание-подпроцесс
                    Long idTask = getIdTask(dbFlexDirector, idTypeProcessParent, idParProcess, activeIdStageSP);

                    Object[] nextStRes = getNextStages(wsc, idTask, stNext.getSign());

                    if (nextStRes[0] != null && (Integer) nextStRes[0] == 0) {
                        LOGGER.info("completeTask");
                        NextStagesInfo info = (NextStagesInfo) nextStRes[2];
                        for( NextStagesTransition transition : info.getStages()) {
                            LOGGER.info("transition.id= " + transition.getIdStage());
                            LOGGER.info("transition.name= " + transition.getNameStage());
                        }
                        completeTask(wsc, (NextStagesInfo) nextStRes[2], false);
                    }

                    /*
                     * param = new ArrayList<Object[]>(); Object[] o = new
                     * Object[5]; o[0] = idTask; o[1] = 1; o[2] = ; o[3] = ;
                     * o[4] = ""; dbFlexDirector.completeWorks(param);
                     */
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
	private Long getIdTask(DBFlexWorkflowCommon dbFlexDirector,
            Integer idTypeProcessParent, Long idParProcess, Long activeIdStageSP)
            throws SQLException, RemoteException {
        List res = dbFlexDirector
                .execQuery("select ID_TASK from tasks where id_type_process="
                        + idTypeProcessParent.intValue() + " and id_process="
                        + idParProcess.longValue() + " and id_stage_to="
                        + activeIdStageSP.longValue()
                        + " and dateofcomplation is null");

        if (res != null && res.size() == 1) {
            String idTask = (String) ((Map) res.get(0)).get("ID_TASK");
            return Long.valueOf(idTask);
        }
        return null;
    }

    private Long getActiveIdStageSP(List<Long> activeStages, String nameTypeProcess) {

        for (Long idSt : activeStages) {
            WorkflowStages stage = WPC.getInstance().findStage(idSt);
            if (stage.getNameStage().equals(nameTypeProcess))
                return idSt;

        }
        return null;
    }

    private ArrayList<Object[]> getParamsForUpdateAttributesSubProcess(
            ProcessInfo taskInfo, Integer idTypeProcess, boolean inOrOut, Long idParProcess) {
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        Integer idTypeProcessChild = null;
        Integer idTypeProcessParent = null;

        if (inOrOut) {
            idTypeProcessChild = idTypeProcess;
            idTypeProcessParent = taskInfo.getIdTypeProcess();

        } else {
            idTypeProcessChild = taskInfo.getIdTypeProcess();
            idTypeProcessParent = idTypeProcess;
        }

        WorkflowSubProcess subProc = WPC.getInstance().findSubProcess(
                idTypeProcessChild, idTypeProcessParent);

        Map<Long, Long> mapVars = null;

        if (inOrOut) mapVars = subProc.getMapVarsIn();
        else  mapVars = subProc.getMapVarsOut();

        Iterator<Long> it = mapVars.keySet().iterator();

        while (it.hasNext()) {
            Long idVarSub = null;
            Long idVarPar = null;

            if (inOrOut) {
                idVarSub = it.next();
                idVarPar = mapVars.get(idVarSub);
            } else {
                idVarPar = it.next();
                idVarSub = mapVars.get(idVarPar);
            }

            BasicAttribute attrStruct = taskInfo.getAttributes()
                    .findAttributeById(idVarPar);

            Object[] par = new Object[3];
            if (inOrOut) par[0] = idTypeProcess;
            else par[0] = idParProcess;

            par[1] = idVarSub;
            par[2] = attrStruct.getAttribute().getValueAttributeString();
            res.add(par);
        }
        return res;
    }

    /*
     * private List<Object[]> getParamsForUpdateAttributesInSubProcess( TaskInfo
     * taskInfo, Integer idtypeProcessCh) {
     * 
     * List<Object[]> res = new ArrayList<Object[]>();
     * 
     * WorkflowSubProcess subProc = WPC.getInstance().findSubProcess(
     * idtypeProcessCh, taskInfo.getIdTypeProcess()); Map<Long, Long> inMapVars
     * = subProc.getMapVarsIn();
     * 
     * Iterator<Long> it = inMapVars.keySet().iterator(); while (it.hasNext()) {
     * Long idVarSub = it.next(); Long idVarPar = inMapVars.get(idVarSub);
     * AttributeStruct attrStruct = taskInfo.getAttributes()
     * .findAttributeById(idVarPar);
     * 
     * Object[] par = new Object[3]; par[0] = idtypeProcessCh; par[1] =
     * idVarSub; par[2] = attrStruct.getAttribute().getValueAttributeString();
     * 
     * res.add(par); }
     * 
     * return res; }
     */

    /**
     * Выполнить дейсвтия на переходе
     * @param flexDb
     * @param taskInfo
     * @param stNext
     * @return  статус "ok" или код ошибки
     * @throws FactoryException 
     */

    private String executeActionsOnTransition(DBFlexWorkflowCommon flexDb, ProcessInfo taskInfo, NextStagesInfo stNext) throws FactoryException {
        String res = "error";
        PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        try {
            // изменившиеся переменные (имя-значения)
            LOGGER.info("changed variables");
            Map<String, String[]> chVars = new HashMap<String, String[]>();
            for (NextStagesTransition nst : stNext.getStages()) {
                TransitionAction action = nst.getAction();
                JEP jep = action.getJep();

                if (jep != null) {
                    // разрешить присвоение переменных
                    LOGGER.info("Allow to change variables");
                    
                    jep.setAllowAssignment(true);
                    // добавить стандартные константы и функции
                    jep.addStandardConstants();
                    jep.addStandardFunctions();
                    String[] expressions = action.getActionExpression().split(";");
                    // распарсим все выражения, разделенные знаком ;
                    LOGGER.info("parse expressions");
                    
                    for (String expression : expressions) {
                        if (expression.trim().equals("")) continue;
                        jep.parseExpression(expression);

                        if (jep.getErrorInfo() == null) jep.getValue();
                        else res += jep.getErrorInfo() + "<br>";
                    }

                    // если при распарсивании не было ошибок
                    if (res.equals("error")) {
                        Iterator<String> it = action.getVarsData().keySet().iterator();
                        // достаем переменные, учавствующие в выражении и
                        // сравнивам их значения
                        LOGGER.info("compare variables values");
                        while (it.hasNext()) {
                            // // имя пременной
                            String nameVar = it.next();
                            Object[] ob = action.getVarsData().get(nameVar);
                            // псевданим переменной в выражении
                            String varExpr = (String) ob[0];
                            VariablesType type = (VariablesType) ob[1];
                            Object valueVarPrevios = ob[2];
                            Object valueVarAfter = jep.getVarValue(varExpr);
                            if(valueVarAfter==null)
                            	valueVarAfter = "";
                            if(nameVar.equals("Текущая дата"))//изменяется только из кода. Переходами нельзя поменять
                            	valueVarAfter = Formatter.formatDateTime(new Date()); 

                            LOGGER.info("nameVar: " + nameVar);
                            LOGGER.info("valueVarPrevios: " + valueVarPrevios.toString());
                            LOGGER.info("valueVarAfter: " + valueVarAfter.toString());
                            
                            if (WPC.getInstance().isVariableDirectVar(nameVar) && !valueVarPrevios.equals(valueVarAfter)) {
                                LOGGER.info("check department");
                                Long departId = null;
                                if (((String) valueVarAfter).trim().equals(Config.getProperty("COM_PREVIOS_DEPARTAMENT"))) {
                                    LOGGER.info("getting previous departments");
                                    departId = new Long(-1);
                                } else {
                                    // changed (MK). Now sets from nst manually.
                                    //departId = WPC.getInstance().findDepartamentByName((String) valueVarAfter).getId();
                                    departId = nst.getIdDepartament();
                                    LOGGER.info("setting new department");
                                }                               
                                LOGGER.info("departId " + departId);                              
                                if (departId != null) nst.setIdDepartament(departId);

                            } else {
                                chVars.putAll(getChangedVars(nameVar, type, valueVarPrevios, valueVarAfter));
                                LOGGER.info("checking department unnecessary");
                            }
                        }
                    }
                }
            }

            // если при распарсивании не было ошибок
            if (res.equals("error")) {
                LOGGER.info("prepare for updating variables values");
                if(chVars!=null)
                	for(String nameVar : chVars.keySet())
                		for (String value : chVars.get(nameVar))
                			pup.updatePUPAttribute(taskInfo.getIdProcess(), nameVar, value);
                
                /*List<Object[]> paramsForLoadChVars = getParamsForLoadChVars(
                        taskInfo.getIdTypeProcess(), taskInfo.getIdProcess(), chVars);

                if (paramsForLoadChVars.size() > 0)
                    flexDb.updateAttributes(
                            (ArrayList<Object[]>) paramsForLoadChVars,
                            new ProcessControlType(ProcessControlType.NONE));*/
            }
            res = "ok";
            LOGGER.info("result: " + res);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        LOGGER.info("result: " + res);
        return res;
    }

    private List<Object[]> getParamsForLoadChVars(Integer idTypeProcess, Long idProcess, Map<String, String[]> chVars) {
        List<Object[]> res = new ArrayList<Object[]>();
        Iterator<String> iter = chVars.keySet().iterator();
        while (iter.hasNext()) {
            String nameVar = iter.next();
            for (String value : chVars.get(nameVar)) {
                Object[] par = new Object[3];
                par[0] = idProcess;
                par[1] = WPC.getInstance().getIdVariableByDescription(nameVar,
                        idTypeProcess);
                par[2] = value;
                // par[3] = new Long(-1);
                res.add(par);
            }
        }
        return res;
    }

    /**
     * получить список измененных переменных
     * 
     * @param nameVar
     * @param type
     * @param valueVarPrevios
     * @param valueVarAfter
     * @return Map<String, String[]> ключ- имя переменной, значчение - список
     *         измененных значений
     */

    @SuppressWarnings("rawtypes")
	private Map<String, String[]> getChangedVars(String nameVar, VariablesType type, Object valueVarPrevios, Object valueVarAfter) {
        Map<String, String[]> res = new HashMap<String, String[]>();
        if (valueVarPrevios instanceof String) {
            // если значение переменной было не массив
            String s = getChangedVars(type, (String) valueVarPrevios,
                    valueVarAfter, false);
            if (s != null) {
                String[] sArr = { s };
                res.put(nameVar, sArr);
            }
        } else {
            List valueVarPreviosList = (List) valueVarPrevios;
            Iterator it = valueVarPreviosList.iterator();
            String[] s = new String[valueVarPreviosList.size()];
            int counter = 0;
            while (it.hasNext()) {
                s[counter] = getChangedVars(type, (String) it.next(),
                        valueVarAfter, true);
                counter++;
            }
            res.put(nameVar, s);
        }
        return res;
    }

    private String getChangedVars(VariablesType type, String valueVarPrevios, Object valueVarAfter, boolean updateValuesVar) {
        String res = null;
        switch (type.value) {
        case VariablesType.BOOLEAN:
            if (valueVarAfter instanceof Double) {
                Double dValue = (Double) valueVarAfter;
                String sValue = String.valueOf((int) dValue.doubleValue());
                // если значение переменной изменилось

                if ((!sValue.equals(valueVarPrevios) && !updateValuesVar)
                        ^ updateValuesVar) {
                    res = new String();
                    res = sValue;
                }

            }
            break;

        case VariablesType.INTEGER:
            if (valueVarAfter instanceof Double) {
                Double dValue = (Double) valueVarAfter;
                String sValue = String.valueOf((int) dValue.doubleValue());
                // если значение переменной изменилось
                if ((!sValue.equals(valueVarPrevios) && !updateValuesVar)
                        ^ updateValuesVar) {
                    res = new String();
                    res = sValue;
                }

            }
            break;
        case VariablesType.FLOAT:
            if (valueVarAfter instanceof Double) {
                Double dValue = (Double) valueVarAfter;

                // если значение переменной изменилось
                if ((!Double.valueOf((String) valueVarPrevios).equals(dValue) && !updateValuesVar)
                        ^ updateValuesVar) {

                    res = new String();
                    res = String.valueOf(dValue);
                }

            }
            break;
        case VariablesType.STRING:
        case VariablesType.SELECT:
        case VariablesType.DATE:
        case VariablesType.USER:
        case VariablesType.URL:

            if (valueVarAfter instanceof String) {
                String sValue = (String) valueVarAfter;

                // если значение переменной изменилось
                if ((!((String) valueVarPrevios).equals(sValue) && !updateValuesVar)
                        ^ updateValuesVar) {
                    res = new String();
                    res = sValue;
                }

            }
            break;
        }
        return res;
    }

    /**
     * Read department to go to in the transition to from the request and save to stNext
     * @return true, if deps are set, false otherwise 
     */
    private boolean setDep(HttpServletRequest request, NextStagesInfo stNext) {
    	if ((stNext == null) || (stNext.getStages() == null)) return false;
    	boolean success = true;
        for (NextStagesTransition st : stNext.getStages())
            try {
                String idDep = request.getParameter("stageTo" + st.getIdStage());
                Long dep = Long.parseLong(idDep);
                if (idDep != null) st.setIdDepartament(dep);
                if (-1L == dep.longValue()) success = false;
            } catch (NumberFormatException e) {
                st.setIdDepartament(null);
                success = false;
            }
       return success;
    }
    
    @SuppressWarnings("unused")
	private boolean reqEq(HttpServletRequest request, String name, String val) {
        if (request.getParameter(name) == null) {
            return false;
        }
        return request.getParameter(name).equals(val);
    }
    private boolean isConditionValidationStage(String stage, String process){
    	if(process==null || stage==null)
    		return false;
    	return process.equals("Крупный бизнес ГО") && (stage.equals("Формирование проекта Кредитного решения") || stage.equals("Согласование проекта Кредитного решения") || stage.equals("Получение проекта Справки и изменений к Кредитному решению") || stage.equals("Доработка проекта Кредитного решения") || stage.startsWith("Отправка проекта Кредитного решения на рассмотрение УО") || stage.equals("Получение решения УО/УЛ и направление запросов на проведение экспертиз") || stage.equals("Получение результатов экспертиз"))
    			|| process.equals("Крупный бизнес ГО (Структуратор за МО)") && (stage.equals("Формирование проекта Кредитного решения") || stage.equals("Консолидация замечаний, формирование Справки, доработка проекта Кредитного решения") || stage.equals("Отправка проекта Кредитного решения на рассмотрение УО/УЛ или экспертизы") || stage.equals("Получение решения УО/УЛ и направление запроса на экспертизу правового статуса") || stage.equals("Получение результатов экспертиз"))
    			|| process.equals("Изменение условий") && (stage.equals("Изменение параметров заявки") || stage.equals("Формирование измененного проекта Кредитного решения"))
    			|| process.equals("Изменение условий Крупный бизнес ГО") && (stage.equals("Формирование измененного проекта Кредитного решения") || stage.equals("Согласование измененного проекта Кредитного решения") || stage.equals("Получение проекта Справки и изменений к Кредитному решению в связи с изменениями")|| stage.equals("Консолидация замечаний и направление Справки и изменений к Кредитному решению в связи с изменениями") || stage.equals("Доработка проекта Кредитного решения в связи с изменениями") || stage.equals("Отправка проекта Кредитного решения на рассмотрение УО/УЛ в связи с изменениями") || stage.equals("Получение решения УО/УЛ и направление запросов на проведение экспертиз в связи с изменениями") || stage.equals("Получение результатов экспертиз в связи с изменениями"))
    			|| process.equals("Изменение условий Крупный бизнес ГО (Структуратор за МО)") && (stage.equals("Формирование измененного проекта Кредитного решения") || stage.equals("Консолидация замечаний, формирование Справки, доработка проекта Кредитного решения") || stage.equals("Отправка проекта Кредитного решения на рассмотрение УО/УЛ или экспертизы в связи с изменениями") || stage.equals("Получение решения УО/УЛ и направление запроса на экспертизу правового статуса в связи с изменениями") || stage.equals("Получение результатов экспертиз в связи с изменениями"));
    }
}
