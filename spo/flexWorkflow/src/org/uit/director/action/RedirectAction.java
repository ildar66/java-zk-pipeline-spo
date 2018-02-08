package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WorkflowSessionContext;

import com.vtb.util.Formatter;

import ru.md.pup.dbobjects.StageJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class RedirectAction extends Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedirectAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String target = "start";

		String idStageRedirect = request.getParameter("idStage");
		String idTask = request.getParameter("idTask");
		String idUser = request.getParameter("idUser");
		String idTypeProcess = request.getParameter("idTypeProcess");
		String departament = request.getParameter("departament");		
		String idCurrentStage = request.getParameter("idCurrentStage");
		String currentDepartament = request.getParameter("currentDepartament");
		
		if (departament == null) departament = "-1";

		WorkflowSessionContext wsc = AbstractAction
				.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));
		if (idStageRedirect == null || idStageRedirect.trim().isEmpty()) {
			idStageRedirect = idCurrentStage;
			/*wsc.setErrorMessage("Необходимо выбрать операцию.");
			return (mapping.findForward("errorPage"));*/
		}

		try {

			if (wsc.isUserAdmin(Integer.parseInt(idTypeProcess))) {

				// перенаправим, если было изменена операция, на которую отправляем
				// либо подразделение, либо оба (операция и подразделение). Но хотя бы один.
				boolean stageNotChanged = false;
				if ((idStageRedirect == null) || idStageRedirect.equals("") || idStageRedirect.equals("-1"))  stageNotChanged = true;
				
				boolean departmentNotChanged = false;
				if ((departament == null) || departament.equals("") || departament.equals("-1"))  departmentNotChanged = true;
				
				if ( stageNotChanged && departmentNotChanged) 
					// ничего не делаем  
					;
				else
				{
					if (stageNotChanged) 
						// получить currentStage, и использовать его.
						idStageRedirect = idCurrentStage;
					if (departmentNotChanged) 
						// получить currentDepartment, и использовать его.
						departament = currentDepartament;	
					
					if (idTask != null && !idTask.equals("")) {
						PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
						TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
						TaskInfoJPA ti = pupFacade.getTask(Long.valueOf(idTask));
						TaskJPA task = taskFacade.getTaskByPupID(ti.getProcess().getId());
						
						StageJPA stageFrom = ti.getStage();
						String res = wsc.getDbManager().getDbFlexDirector()
								.redirectWork(idTask, idStageRedirect,
										wsc.getIdUser(),Long.valueOf(departament),
										request.getRemoteAddr());
						if (res.equalsIgnoreCase("error")) {
							wsc.setErrorMessage("Ошибка перенаправления задания.");
							return (mapping.findForward("errorPage"));
						}
						//Проверяем, если перенаправление было с или на этапы экспертиз, то нужно корректно записать значения атрибутам
						if(!stageNotChanged && task.getActiveStandardPeriodVersion()!=null && !idStageRedirect.equals(idCurrentStage)){
							LOGGER.info("idCurrentStage="+idCurrentStage);
							LOGGER.info("idStageRedirect="+idStageRedirect);
							//смотрим по названию этапа нет ли атрибута Требуется экспертиза*_stage. который содержит название операции 
							//в том же этапе нормативных сроков, что и текущая операция тогда это была экспертиза
							for(String attrName : pupFacade.getAttributeList(ti.getProcessType().getIdTypeProcess())){
								if(!attrName.startsWith("Требуется экспертиза") || !attrName.endsWith("_stage")){continue;}
								String attrPrefix = attrName.substring(0,attrName.length()-"_stage".length());
								String stageName = pupFacade.getPUPAttributeValue(ti.getProcess().getId(), attrName);
								for(StandardPeriodGroupJPA group : task.getActiveStandardPeriodVersion().getStandardPeriodGroups()){
									for(StageJPA groupStage : group.getStages()){
										if(stageName.equals(groupStage.getDescription())){
											//значит, это группа из экспертиз
											if(group.getStages().contains(stageFrom)){
												//мы сейчас перенаправляем с экспертизы. Нужно
												//корректно заполняить атрибуты
												//в качестве пользователя пишем _cptbreak
												String endDateAttr = pupFacade.getPUPAttributeValue(task.getProcess().getId(),attrPrefix+"_data_end");
												String startDateAttr = pupFacade.getPUPAttributeValue(task.getProcess().getId(),attrPrefix+"_data_start");
												String userAttr = pupFacade.getPUPAttributeValue(task.getProcess().getId(),attrPrefix+"_user");
												LOGGER.info("endDateAttr="+endDateAttr);
												LOGGER.info("startDateAttr="+startDateAttr);
												LOGGER.info("userAttr="+userAttr);
												LOGGER.info(String.valueOf(startDateAttr.split("\\|").length > endDateAttr.split("\\|").length));
												if(getExpertizeAttrCardinality(startDateAttr) > getExpertizeAttrCardinality(endDateAttr))
													pupFacade.updatePUPAttribute(task.getProcess().getId(), attrPrefix+"_data_end", 
															endDateAttr+Formatter.formatDateTime(new java.util.Date())+"|");
												if(getExpertizeAttrCardinality(startDateAttr) > getExpertizeAttrCardinality(userAttr))
													pupFacade.updatePUPAttribute(task.getProcess().getId(), attrPrefix+"_user", 
															userAttr+"_cptbreak|");
												
											}
										}
									}
								}
								//TODO Если перенаправляем на экспертизу
								for(StageJPA stage :pupFacade.getStages(task.getProcess().getProcessType().getIdTypeProcess())){
									if(stage.getIdStage().toString().equals(idStageRedirect) && stage.getDescription().equals(stageName)){
										pupFacade.updatePUPAttribute(task.getProcess().getId(), attrPrefix+"_data_start", 
												pupFacade.getPUPAttributeValue(task.getProcess().getId(),attrPrefix+"_data_start")
												+Formatter.formatDateTime(new java.util.Date())+"|");
									}
								}
								;
							}
						}
					}
				}

				return (mapping.findForward(target));
				//response.sendRedirect("direction.stages.do?typeProc="+idTypeProcess+"&user=" + idUser);
				//return null;
			} else {
				wsc
						.setErrorMessage("Нет прав пользователя с правами администратора");
				target = "errorPage";
				return (mapping.findForward(target));

			}

		} catch (Exception e) {
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка выполнения операции.");
			target = "errorPage";

		}

		return (mapping.findForward(target));
	}
	/*
	 * Возвращает мощность множества для аттрибута. Разделитель |
	 */
	private int getExpertizeAttrCardinality(String val){
		if( val == null || val.isEmpty())
		    return 0;
		return val.split("\\|").length;
	}
}