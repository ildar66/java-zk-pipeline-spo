package ru.md.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IDictService;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.helper.TaskHelper;
import ru.md.persistence.CurrencyMapper;
import ru.md.persistence.DepartmentMapper;
import ru.md.persistence.UserMapper;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.ReportBeanLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;


@Controller
public class TestController {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private CurrencyMapper currencyMapper;
	@Autowired
	private DepartmentMapper departmentMapper;
	@Autowired
	private IDictService dictService;
	@Autowired
	private IDashboardService dashboardService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@RequestMapping(value = "/dash.html")
    public String dash(@ModelAttribute("model") ModelMap model,HttpServletRequest request) throws Exception {
        long tstart = System.currentTimeMillis();
        dashboardService.recalculateOldTasks();
        Long loadTime = System.currentTimeMillis()-tstart;
        model.addAttribute("msg", "time " + Formatter.format(Double.valueOf(loadTime)/1000) + " секунд");
        return "utf8";
	}

	@RequestMapping(value = "/test.html")
    public String home(@ModelAttribute("model") ModelMap model,HttpServletRequest request) throws Exception {
		UserJPA user =  TaskHelper.pup().getCurrentUser();
		if(!user.isAdmin()){
			model.addAttribute("msg", "Только администратор может открыть эту страницу");
			return "utf8";
		}
		NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);

		model.addAttribute("currency", StringUtils.join(currencyMapper.getCurrencyList().iterator(), ','));
		model.addAttribute("user", userMapper.getUserByLogin(user.getLogin()).getFullName() 
				+ ", " + departmentMapper.getById(user.getDepartment().getIdDepartment()).toString()
				+ ", MqFileHostType=" + notifyFacade.getMqFileHostTypeByDepId(user.getDepartment().getIdDepartment()));


        String msg = TaskHelper.versionCheck();
        msg += "<br />getNextWorkDayBegin " + Formatter.formatDateTime(notifyFacade.getNextWorkDayBegin());
        if(request.getParameter("mdtask")!=null){
            ReportBeanLocal reportFacade = com.vtb.util.EjbLocator.getInstance().getReference(ReportBeanLocal.class);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            msg = gson.toJson(reportFacade.getTaskReport(Long.valueOf(request.getParameter("mdtask"))));
        }
		model.addAttribute("msg", msg);

        return "test";
    }

	@RequestMapping(value = "/cr.html")
	public String cr(@ModelAttribute("model") ModelMap model,HttpServletRequest request) throws Exception {
		long tstart = System.currentTimeMillis();
		StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
		spLocal.clearAuditClientReport();
		spLocal.generateAuditClientReport();
		Long loadTime = System.currentTimeMillis()-tstart;
		model.addAttribute("msg", "время формирования отчёта " + Formatter.format(Double.valueOf(loadTime)/1000) + " секунд");
		return "utf8";
	}
	@RequestMapping(value = "/cr2.html")
	public String cr2(@ModelAttribute("model") ModelMap model,HttpServletRequest request) throws Exception {
		long tstart = System.currentTimeMillis();
		//dashboardService.clearOldClientReport();
		dashboardService.generatePipelineClientReport();
		Long loadTime = System.currentTimeMillis()-tstart;
		model.addAttribute("msg", "время формирования отчёта " + Formatter.format(Double.valueOf(loadTime)/1000) + " секунд");
		return "utf8";
	}
}
