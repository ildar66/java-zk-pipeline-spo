package ru.md.servlet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import ru.md.spo.util.CpsFacade;

public class NewProjectTeamAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewProjectTeamAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        UserJPA user = pupFacadeLocal.getUser(Long.valueOf(request.getParameter("id")));
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        //проверить нет ли этого пользователя уже в проектной команде
        for(ProjectTeamJPA oldPT : task.getProjectTeam(request.getParameter("section")))
        	if(oldPT.getUser().equals(user)){
        		String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
                ans += "<root><id>"+user.getIdUser()+"</id><name></name><login>"+
                    user.getLogin()+"</login><dep></dep></root>";
                response.getWriter().write(ans);
                return null;
        	}
        //добавить в проектную команду
        ProjectTeamJPA pt = new ProjectTeamJPA();
        pt.setTask(task);
        pt.setUser(user);
        pt.setTeamType(request.getParameter("section"));
        task.getProjectTeam().add(pt);
        taskFacadeLocal.merge(pt);
        try {
			CpsFacade.addMember(wsc.getIdUser(), task.getId(), request.getParameter("section"), user.getIdUser());
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
        //сделать назначения
        /*При добавлении нового члена проектной команды в заявку нужно назначать его по всем его ролям на эту заявку, 
         * по которым еще не назначены другие пользователи. Учитываются только роли проектной команды.*/
        if(pt.getTeamType().equals("p")){
        	for (RoleJPA role : user.getRoles()) {
        		if(!role.getProcess().getIdTypeProcess().equals(task.getProcess().getProcessType().getIdTypeProcess())) continue;
        		List<String> sectionRoleNames = TaskHelper.dict().findProjectTeamRoles();
        		if(!sectionRoleNames.contains(role.getNameRole())) continue;
        		if (pupFacadeLocal.isAssigned(role.getIdRole(), task.getProcess().getId())) continue;
        		if ((role.getNameRole().equals("Структуратор") || role.getNameRole().equals("Руководитель структуратора"))
        				&& (pupFacadeLocal.isAssigned(pupFacadeLocal.getRole("Структуратор",task.getProcess().getProcessType().getIdTypeProcess()).getIdRole(), task.getProcess().getId())
        						||pupFacadeLocal.isAssigned(pupFacadeLocal.getRole("Руководитель структуратора",task.getProcess().getProcessType().getIdTypeProcess()).getIdRole(), task.getProcess().getId()))) 
        			continue;
        		if ((role.getNameRole().equals("Структуратор (за МО)") || role.getNameRole().equals("Руководитель структуратора (за МО)"))
        				&& (pupFacadeLocal.isAssigned(pupFacadeLocal.getRole("Структуратор (за МО)",task.getProcess().getProcessType().getIdTypeProcess()).getIdRole(), task.getProcess().getId())
        						||pupFacadeLocal.isAssigned(pupFacadeLocal.getRole("Руководитель структуратора (за МО)",task.getProcess().getProcessType().getIdTypeProcess()).getIdRole(), task.getProcess().getId()))) 
        			continue;
        		pupFacadeLocal.assign(user.getIdUser(), role.getIdRole(), 
        				task.getProcess().getId(), wsc.getIdUser(), true);
        		try {
					CpsFacade.executorSetting(wsc.getIdUser(), task.getId(), user.getIdUser(), role.getNameRole());
        		} catch (Exception e) {
        			LOGGER.warn(e.getMessage(), e);
        		}
        	}
        }
        //отправить уведомление
        UserJPA from = pupFacadeLocal.getUser(AbstractAction.getWorkflowSessionContext(request).getIdUser());
		NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        String body = "Вы включены в состав проектной команды в СПО по "+ notifyFacade.getTypeNamePraepositionalis(task.getId())
				+notifyFacade.getAllContractors(task.getId())+" № <a href=\"" +
            pupFacadeLocal.getBaseURL(user.getIdUser()) + "/showTaskList.do?typeList=all&searchHideApproved=n&projectteam=true&searchNumber="+
            task.getMdtask_number()+"\">" + task.getNumberAndVersion()+"</a> ";
		String subject = "Вы включены в состав проектной команды в СПО по "
				+ notifyFacade.getNamePraepositionalis(task.getId());
        notifyFacade.send(from.getIdUser(), user.getIdUser(), subject, body
				+ notifyFacade.getDescriptionTask(task.getId()));
        //для клиентского менеджера отправить еще уведомления начальникам
        if(pt.getTeamType().equals("p"))
            notifyClientManagerBoss(user,task);
        //ответ
        LOGGER.info("отправлено уведомление на " + user.getMailUser());
        String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        ans += "<root><id>"+user.getIdUser()+"</id><name>"+user.getFullName()+"</name><login>"+
            user.getLogin()+"</login><dep>"+user.getDepartment().getFullName()+"</dep></root>";
        response.getWriter().write(ans);
        return null;
    }
    public static void notifyClientManagerBoss(UserJPA user, TaskJPA task){
    	Long idProcessType = task.getProcess().getProcessType().getIdTypeProcess();
    	try {
    		if(!user.hasRole(idProcessType, "Клиентский менеджер"))
    			return;
    		PupFacadeLocal pupEjb = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
    		NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
			for(UserJPA boss : pupEjb.getParentList(user.getIdUser(), idProcessType))
				if(boss.hasRole(idProcessType, "Руководитель клиентского подразделения")){
					String subject = user.getFullName()+" включен(а) в состав проектной команды в СПО по "
							+ notifyFacade.getNamePraepositionalis(task.getId());
					String body =  user.getFullName()+" включен(а)  в состав проектной команды в СПО по "
							+ notifyFacade.getTypeNamePraepositionalis(task.getId())
							+ notifyFacade.getAllContractors(task.getId())+" № <a href=\"" +
							pupEjb.getBaseURL(boss.getIdUser()) + "/showTaskList.do?typeList=all&searchNumber="+
            			task.getMdtask_number()+"\">" + task.getNumberAndVersion()+"</a>"
							+ notifyFacade.getDescriptionTask(task.getId());
					notifyFacade.send(user.getIdUser(), boss.getIdUser(), subject, body);
				}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    	//getParentList
    }
    /**
     * Возвращает список ролей, которые могут включать в проектную команду новых пользователей.
     * @return
     */
    public static Set<String> getPrivilegeAddRoles(){
    	HashSet<String> set = new HashSet<String>();
    	set.add("Структуратор");
    	set.add("Руководитель структуратора");
    	set.add("Руководитель структуратора (за МО)");
    	set.add("Структуратор (за МО)");
    	set.add("Руководитель продуктового подразделения");
    	set.add("Руководитель клиентского подразделения");
    	set.add("Руководитель поддерживающего клиентского подразделения");
		set.add(UserJPA.ACCESS_DOWNLOAD);
		set.add(UserJPA.ACCESS_DLD_CNTRL);
    	return set;
    }
}
