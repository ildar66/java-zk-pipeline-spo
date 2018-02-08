package org.uit.director.action;

import java.text.MessageFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.servlet.NewProjectTeamAction;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.domain.SPOMessage;
import com.vtb.domain.Task;
import com.vtb.exception.FactoryException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import ru.md.spo.util.CpsFacade;

public class AssignUserAction extends Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssignUserAction.class);

	@Override
	public ActionForward execute(ActionMapping actionMapping,
			ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionForward forward = new ActionForward();
		forward.setRedirect(true);
		String returnTo=request.getParameter("returnTo");
		if(returnTo==null)returnTo="noAccept";
		forward.setPath("showTaskList.do?typeList="+returnTo);
		Long idUser = Long.valueOf(request.getParameter("idUser"));
		long idTask = Long.parseLong(request.getParameter("idTask"));

		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);

		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		TaskInfoJPA taskInfo = pupFacadeLocal.getTask(idTask);

		Long idUserRuk = wsc.getIdUser();
		if (wsc.isNewContext())
			return (actionMapping.findForward("start"));
		if (idUserRuk.equals(idUser)){//если назначаем на себя, то вместо
			//назначения взятие в обработку VTBSPO-983
			wsc.getCacheManager().deleteAllCach();
			try {
				pupFacadeLocal.acceptWork(idTask, idUserRuk);
				forward.setPath("/task.context.do?id=" + idTask);
				return forward;
			} catch (Exception e) {
				e.printStackTrace();
				wsc.setErrorMessage("Взятие в работу не выполнено. "+e.getMessage());
				return (actionMapping.findForward("errorPage"));
			}
		} else {
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			//список хороших годных ролей
			HashMap<Long, String> roles2Assign = processor.getRoles2Assign(idUser, idTask);
			if (roles2Assign.size()!=1&&request.getParameter("idRole")==null){
				//нужно выбрать роль
				forward.setPath("selectRole4Assign.jsp?userid="+idUser+"&taskid="+idTask);
				return forward;
			}
			Long roleid=null;
			if(roles2Assign.size()==1)
				roleid = roles2Assign.keySet().iterator().next();
			if (request.getParameter("idRole")!=null)
				roleid = new Long(request.getParameter("idRole"));

			pupFacadeLocal.assign(idUser, roleid, taskInfo.getProcess().getId(), idUserRuk);
			//перевести операцию в департамент того, кого назначили
			taskInfo.setIdDepartament(pupFacadeLocal.getUser(idUser).getDepartment().getIdDepartment());
			pupFacadeLocal.merge(taskInfo);
			String rolename = pupFacadeLocal.getRole(roleid).getNameRole();
			if(rolename.equals("Клиентский менеджер") || rolename.equals("Клиентский менеджер поддерживающего подразделения"))
				try {
					addUserToProjectTeam(idUser, wsc, taskInfo);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
	        String url = pupFacadeLocal.getBaseURL(wsc.getIdUser());
	
			try {
				Task task = processor.findByPupID(taskInfo.getProcess().getId(), false);
				String number = SBeanLocator.singleton().mdTaskMapper().getNumberAndVersion(task.getId_task());
				//Вместо ПУПовского используем наш асинхронный по последнему слову техники
				//на острие прогресса суперуведомитель VTBSPO-713
				CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
				UserJPA doer = TaskHelper.pup().getUser(idUser);
				User from = compenduim.getUser(new User(idUserRuk.intValue()));

				NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
				String bodyMessage = MessageFormat.format(SPOMessage.assignBodyMessageFormat,
				        taskInfo.getStage().getDescription(), url,
				        task.getHeader().getNumber().toString(),number,
						notifyFacade.getAllContractors(task.getId_task()),taskInfo.getProcessType().getDescriptionProcess(),roles2Assign.get(roleid),
						notifyFacade.getTypeNamePraepositionalis(task.getId_task()))
						+ notifyFacade.getDescriptionTask(task.getId_task());
				String subject = MessageFormat.format(SPOMessage.assignSubjectMessageFormat,
						taskInfo.getStage().getDescription(), notifyFacade.getNamePraepositionalis(task.getId_task()));
				notifyFacade.send(from.getId(),doer.getIdUser(), subject, bodyMessage);
			} catch(Exception e) {
				//do nothing
			}
		}

		return forward;
	}

	private void addUserToProjectTeam(Long idUser,
			WorkflowSessionContext wsc, TaskInfoJPA taskInfo) throws FactoryException {
		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA task = taskFacadeLocal.getTaskByPupID(taskInfo.getProcess().getId());
		UserJPA user = pupFacadeLocal.getUser(idUser);
		for(ProjectTeamJPA pt : task.getProjectTeam("p")){
			if(pt.getUser().equals(user))
				return;
		}
		//добавить в проектную команду
		ProjectTeamJPA pt = new ProjectTeamJPA();
		pt.setTask(task);
		pt.setUser(user);
		pt.setTeamType("p");
		task.getProjectTeam().add(pt);
		taskFacadeLocal.merge(pt);
		CpsFacade.addMember(wsc.getIdUser(), task.getId(), "p", user.getIdUser());
		//уведомить их начальников
		NewProjectTeamAction.notifyClientManagerBoss(user, task);
	}

}
