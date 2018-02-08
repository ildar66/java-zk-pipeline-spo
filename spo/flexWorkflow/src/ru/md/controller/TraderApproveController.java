package ru.md.controller;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.spo.utils.Formatter;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

@Controller
public class TraderApproveController extends Action {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    
    @RequestMapping(value = "/ajax/trader_approve.html")
    public String traderApprove(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("id") Long mdtaskid,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOGGER.info("traderApprove, mdtaskid="+mdtaskid);
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        response.addHeader("Cache-control", "no-cache");
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        Long userid = wsc.getIdUser();
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(userid);
        TaskJPA task = taskFacadeLocal.getTask(mdtaskid);
        if(pupFacadeLocal.isCurrentUserInProjectTeam(mdtaskid) 
    		    && pupFacadeLocal.currentUserAssignedAs("Кредитный аналитик", task.getProcessId())
    		    && !pupFacadeLocal.isCedEnded(mdtaskid)){
        	task.setTraderApprove(true);
        	task.setTrader_approve_date(new Date());
        	task.setTrader_approve_user(userid);
        	taskFacadeLocal.merge(task);
        	task = taskFacadeLocal.getTask(mdtaskid);
        	model.addAttribute("msg","Подтверждено Трейдером "+user.getFullName() + " "+Formatter.formatDateTime(task.getTrader_approve_date()));
        } else {
        	model.addAttribute("msg","Нет прав на операцию подтверждение трейдером");
        }
        return "utf8";
	}
}
