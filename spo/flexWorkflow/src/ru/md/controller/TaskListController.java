package ru.md.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.vtb.domain.ProcessSearchParam;

import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.MdTask;
import ru.md.domain.RoleFlag;
import ru.md.helper.TaskHelper;
import ru.md.helper.TaskPage;
import ru.md.persistence.MdTaskMapper;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.loader.TaskLine;

@Controller
public class TaskListController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskListController.class.getName());

	@Autowired
	private MdTaskMapper mdTaskMapper;

	@RequestMapping(value = "/ajax/create_app_task_list.html")
    public String create_app_task_list(@ModelAttribute("model") ModelMap model,
					   @RequestParam("page") Long page, @RequestParam("orgid") String orgid,@RequestParam("type") String type,
                       @RequestParam("tab") String tab,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        response.addHeader("Cache-control", "no-cache");
        long tstart = System.currentTimeMillis();
        Gson gson = new Gson();
        HashMap<String,Object> res = new HashMap<String, Object>();

        res.put("page", page);
        if (tab.equalsIgnoreCase("kz")) {
            res.put("total", mdTaskMapper.getCreateApplicationKzTasksCount(orgid, mapTaskType(type)));
            res.put("tasks", mdTaskMapper.getCreateApplicationKzTasks(orgid,mapTaskType(type), page,TaskHelper.getCurrentUser(request).getIdUser()));
        } else {
            res.put("total", mdTaskMapper.getCreateApplicationGroupTasksCount(orgid, mapTaskType(type)));
            res.put("tasks", mdTaskMapper.getCreateApplicationGroupTasks(orgid,mapTaskType(type), page,TaskHelper.getCurrentUser(request).getIdUser()));
        }

        Long loadTime = System.currentTimeMillis()-tstart;
        LOGGER.warn("*** create_app_task_list time " + loadTime);
        res.put("loadTime",Formatter.format(Double.valueOf(loadTime)/1000));
        model.addAttribute("msg", gson.toJson(res));
        return "utf8";
	}
    @RequestMapping(value = "/ajax/project_team_page.html")
    public String project_team_page(@ModelAttribute("model") ModelMap model,
                                       @RequestParam("mdtask") Long mdtask,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("mdtask", mdTaskMapper.getById(mdtask));
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskJPA task=taskFacadeLocal.getTask(mdtask);
        ArrayList<HashMap<String,Object>> teamusers = new ArrayList<HashMap<String, Object>>();
        for (ProjectTeamJPA pt : task.getProjectTeam("p")){
            HashMap<String, Object> user = new HashMap<String, Object>();
            user.put("name", pt.getUser().getFullName());
            user.put("department", pt.getUser().getDepartment().getShortName());
            ArrayList<RoleFlag> roles = new ArrayList<RoleFlag>();
            for(RoleJPA role : pt.getUser().getRoles())
                if (role.getProcess().getIdTypeProcess().equals(task.getIdTypeProcess())
                        && ru.md.helper.TaskHelper.dict().findProjectTeamRoles().contains(role.getNameRole()))
                    roles.add(new RoleFlag(role.getNameRole(),
                                           pupFacadeLocal.isAssigned(pt.getUser().getIdUser(),role.getIdRole(),task.getProcess().getId())));
            user.put("roles", roles);
            teamusers.add(user);
        }
        model.addAttribute("teamusers", teamusers);
        return "project_team";
    }
    private String mapTaskType(String name){
	    if (name==null)
	        return "";
	    if (name.equalsIgnoreCase("Сделка"))
	        return "p";
	    if (name.equalsIgnoreCase("Лимит"))
	        return "l";
	    if (name.equalsIgnoreCase("Кросс-селл"))
	        return "c";
        return "";
    }
	@RequestMapping(value = "/task_list.html")
    public String home(@ModelAttribute("model") ModelMap model,
					   @RequestParam("navigation") Long navigation,@RequestParam("type") String type,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		//"accept", "perform", "starred","project_team"
		Long idCurrentUser = pupFacade.getCurrentUser().getIdUser();
		TaskPage taskpage = new TaskPage();
		taskpage.setTypeList(type);
		ProcessSearchParam processSearchParam = new ProcessSearchParam(request, false);
		if(type.equals("starred") || type.equals("project_team")) {
			taskpage.setTypeList("all");
			if(type.equals("starred"))
				processSearchParam.setFavorite(true);
			else {
				processSearchParam.setHideApproved(true);
				processSearchParam.setProjectTeam(true);
			}
		}

		if(!taskpage.isAllMode())
			taskpage.setCount(pupFacade.getWorkListCount(idCurrentUser, taskpage.getTaskListType(), processSearchParam));
		else
			taskpage.setCount(pupFacade.getProcessListCount(idCurrentUser,null,processSearchParam));
		taskpage.setPageCount((new Double(Math.ceil(taskpage.getCount().doubleValue() / 3))).longValue());

		if(!taskpage.isAllMode()){
			ArrayList<Long> list = pupFacade.getWorkList(idCurrentUser, taskpage.getTaskListType(), processSearchParam, 3L, navigation*3L);
			for (Long id : list) {
				TaskLine taskLine = new TaskLine(taskpage.getTypeList());
				taskLine.setIdUser(idCurrentUser.toString());
				taskLine.setIdTask(id);
				TaskInfoJPA ti = pupFacade.getTask(id);
				taskLine.setIdProcess(ti.getProcess().getId());
				taskLine.setNameStageTo(ti.getStage().getDescription());
				taskLine.setIdTaskDepartment(ti.getIdDepartament());
				if (ti.getExecutor() != null && ti.getExecutor().getIdUser()!=null)
					try {
						Long idExecutor = ti.getExecutor().getIdUser();
						taskLine.setNameIspoln(TaskHelper.pup().getUser(idExecutor).getSurname());
						taskLine.setIdUser(String.valueOf(idExecutor));
					} catch (Exception e) {
						LOGGER.warn(e.getMessage());
					}
				taskpage.getTaskLineList().add(taskLine);
			}
		} else {
			ArrayList<Long> list = pupFacade.getProcessList(idCurrentUser, null, processSearchParam,3L,navigation*3L);
			for (Long id : list) {
				TaskLine taskLine = new TaskLine(taskpage.getTypeList());
				taskLine.setIdUser(idCurrentUser.toString().toString());
				taskLine.setIdProcess(id);
				taskpage.getTaskLineList().add(taskLine);
			}
		}

		try {
			taskpage.setTaskLineList(taskFacade.loadTaskLines(taskpage.getTaskLineList(), idCurrentUser));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		for(TaskLine taskLine : taskpage.getTaskLineList()){
			MdTask mdtask = mdTaskMapper.getById4TaskList(taskLine.getIdMDTask());
			taskLine.setContractors(mdtask.getEkname());
			if (Formatter.str(taskLine.getContractors()).isEmpty()){
				TaskJPA taskJPA = taskFacade.getTask(mdtask.getIdMdtask());
				if (taskJPA.getOrgList().size() > 0)
				    taskLine.setContractors(SBeanLocator.singleton().getDictService().getEkNameByOrgId(taskJPA.getOrgList().get(0).getId()));
				if (Formatter.str(taskLine.getContractors()).isEmpty() && taskJPA.getOrgList().size() > 0)
					taskLine.setContractors(taskJPA.getOrgList().get(0).getOrganizationName());
				String projectName = Formatter.str(mdtask.getProjectName());
				if(taskLine.getDescriptionProcess().equalsIgnoreCase("Pipeline") && !projectName.isEmpty())
					taskLine.setContractors(projectName);
			}
		}

		Gson gson = new Gson();
		model.addAttribute("msg", gson.toJson(taskpage));
        return "utf8";
    }
}
