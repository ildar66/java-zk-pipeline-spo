package ru.md.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.vtb.util.ApplProperties;
import ru.md.domain.MdTask;
import ru.md.domain.User;
import ru.md.domain.dashboard.TaskListFilter;
import ru.md.domain.dashboard.TaskListParam;
import ru.md.persistence.CurrencyMapper;
import ru.md.persistence.DashboardMapper;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.PupMapper;
import ru.md.persistence.UserMapper;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

@Controller
public class DashboardTaskListController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardTaskListController.class);

	@Autowired
	private MdTaskMapper mdTaskMapper;
	@Autowired
	private DashboardMapper dashboardMapper;
	@Autowired
	private IDashboardService dashboardService;
	@Autowired
	private PupMapper pupMapper;
    @Autowired
    private CurrencyMapper currencyMapper;

    private HashMap<String,Object> getTaskLine(Long idMdtask) {
        HashMap<String,Object> taskLine = new HashMap<String, Object>();
        MdTask task = mdTaskMapper.getById(idMdtask);
        taskLine.put("ekname", Formatter.str(task.getEkname()).isEmpty()?task.getProjectName():task.getEkname());
        taskLine.put("number",task.getMdtaskNumber());
        taskLine.put("idMdtask",idMdtask);
        taskLine.put("pupid",task.getIdPupProcess());
        taskLine.put("version",task.getVersion());
        taskLine.put("mdtaskSum",Formatter.format(task.getMdtaskSum()));
        taskLine.put("currency",task.getCurrency());
        taskLine.put("processname",task.getProcessname());
        taskLine.put("status",pupMapper.getPUPAttributeValue(task.getIdPupProcess(),"Статус"));//здесь могут быть тормоза
        taskLine.put("priority",pupMapper.getPUPAttributeValue(task.getIdPupProcess(),"Приоритет"));//здесь могут быть тормоза
        taskLine.put("tasktype",task.getType());
        taskLine.put("initdep",task.getInitdep());
        taskLine.put("ekgroup",task.getEkgroup());
        return taskLine;
    }

	@RequestMapping(value = "/dash_list.html")
    public String dash_list(@ModelAttribute("model") ModelMap model,
                        @RequestParam("statusids") String statusidsParam, @RequestParam("page") Long page,
                        @RequestParam("filter") String filterStringify,
                        @RequestParam("listParam") String listParam,
                        @RequestParam("from") Long from, @RequestParam("to") Long to,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		long tstart = System.currentTimeMillis();
        Gson gson = new Gson();
        TaskListFilter filter = gson.fromJson(filterStringify, TaskListFilter.class);
        TaskListParam param = gson.fromJson(listParam, TaskListParam.class);
        if (!param.departments.isEmpty()) param.departmentsIds = gson.fromJson("[" + param.departments + "]", Long[].class);
        if (!param.tradingDeskSelected.isEmpty()) param.tradingDeskSelectedIds = gson.fromJson("[" + param.tradingDeskSelected + "]", Long[].class);
        Long[] statusids = gson.fromJson("[" + statusidsParam + "]", Long[].class);
        LOGGER.info("dash_list filter "+filterStringify);
        LOGGER.info("dash_list param "+listParam);

        List<Long> taskids = dashboardMapper.getTaskListPage(statusids, new Date(from), new Date(to), filter, param, page);
		HashMap<String,Object> res = new HashMap<String, Object>();
		res.put("currPage",page);
        List<HashMap<String,Object>> tasks = new ArrayList<HashMap<String,Object>>();
        for (Long taskid : taskids)
            tasks.add(getTaskLine(taskid));
        res.put("total",dashboardMapper.getTaskListCount(statusids, new Date(from), new Date(to), filter, param));
        res.put("tasks",tasks);
        Long loadTime = System.currentTimeMillis()-tstart;
        LOGGER.warn("*** dash_list.html time " + loadTime);
        res.put("loadTime",Formatter.format(Double.valueOf(loadTime)/1000));

		model.addAttribute("msg", gson.toJson(res));
        return "utf8";
    }

	@RequestMapping(value = "/dash_list_table.html")
    public String dash_list_table(@ModelAttribute("model") ModelMap model,
                       @RequestParam(value="taskType",required=false) String taskType,
                       @RequestParam(value="statusid",required=false) Long statusid,
                       @RequestParam("creditDocumentary") Long creditDocumentary,
                       @RequestParam(value="departments",required=false) String departments,
                       @RequestParam(value="tradingDeskSelected",required=false) String tradingDeskSelected,
                       @RequestParam(value="mainPeriod",required=false) Boolean mainPeriod,
                       @RequestParam(value="branch",required=false) String branch,
                       @RequestParam("isTradingDeskOthers") Boolean isTradingDeskOthers,
					   @RequestParam("from") Long from, @RequestParam("to") Long to,
                       @RequestParam(value="page",required=false) Long initPage,//страница по умолчанию
                       @RequestParam(value="filter",required=false) String initFilter,//фильтры по умолчанию
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        response.addHeader("Cache-control", "no-cache");
        Gson gson = new Gson();
        Long[] departmentsParam = (departments==null || departments.isEmpty())?null: gson.fromJson("[" + departments + "]", Long[].class);
        Long[] tradingDeskSelectedParam = (tradingDeskSelected==null || tradingDeskSelected.isEmpty())?null:
                                          gson.fromJson("[" + tradingDeskSelected + "]", Long[].class);
        Boolean isTradingDeskOthersParam = isTradingDeskOthers!= null && isTradingDeskOthers;
        if (statusid == null) {
            model.addAttribute("taskType", taskType);
            model.addAttribute("statusids", listJoin(dashboardService.getStatusidsByTaskType(taskType)));
            model.addAttribute("title", dashboardService.getTitle(null, taskType, new Date(from), new Date(to), creditDocumentary,
                                                                  departmentsParam, tradingDeskSelectedParam, isTradingDeskOthersParam, mainPeriod, branch));
        } else {
            model.addAttribute("taskType", "");
            model.addAttribute("statusids", statusid);
            model.addAttribute("title", dashboardService.getTitle(statusid, null, new Date(from), new Date(to), creditDocumentary,
                                                                  departmentsParam, tradingDeskSelectedParam, isTradingDeskOthersParam, mainPeriod, branch));
        }
		model.addAttribute("creditDocumentary", creditDocumentary);
		model.addAttribute("departments", departments == null ? "" : departments);
		model.addAttribute("tradingDeskSelected", tradingDeskSelected == null ? "" : tradingDeskSelected);
		model.addAttribute("isTradingDeskOthers", isTradingDeskOthers);
		model.addAttribute("from_param", from);
		model.addAttribute("to_param", to);
		model.addAttribute("branch", branch == null? "" : branch);
		model.addAttribute("appversion", ApplProperties.getVersion());
		model.addAttribute("currencyList", gson.toJson(currencyMapper.getCurrencies()));
		model.addAttribute("processTypeList", gson.toJson(pupMapper.getAllProcessTypes()));
        model.addAttribute("initPage", initPage == null ? 1L : initPage);
        model.addAttribute("initFilter", initFilter == null ? "" : initFilter);
        return "dash_list_table";
    }
    /**
     * Строки в списке выводит через запятую
     */
    public static String listJoin(List<Integer> list){
        if(list==null)
            return "";
        Integer[] arr = list.toArray(new Integer[list.size()]);
        return StringUtils.join(arr, ", ");
    }

    public static String getDashListBackUrl(HttpServletRequest request) {
        String url = "dash_list_table.html?from="+request.getParameter("from")+"&to="+request.getParameter("to");
        if (request.getParameter("taskType") != null)
            url += "&taskType="+request.getParameter("taskType");
        else
            url += "&statusid=" + request.getParameter("statusid");
        Gson gson = new Gson();
        TaskListParam param = gson.fromJson(request.getParameter("listParam"), TaskListParam.class);
        url +="&creditDocumentary="+param.creditDocumentary;
        url +="&isTradingDeskOthers="+param.isTradingDeskOthers;
        if (!Formatter.str(param.departments).isEmpty())
            url +="&departments="+param.departments;
        if (!Formatter.str(param.tradingDeskSelected).isEmpty())
            url +="&tradingDeskSelected="+param.tradingDeskSelected;
        url +="&filter="+request.getParameter("filter");
        url +="&page="+request.getParameter("page");
        return url;
    }
    private static final String[] DASHBOARD_ROLES = new String[] { "Аудитор ДКАБ", "Руководитель клиентского подразделения",
       "Руководитель поддерживающего клиентского подразделения", "Руководитель структуратора", "Руководитель структуратора (за МО)",
       "Руководитель продуктового подразделения","Руководитель подразделения кредитных аналитиков","Администратор системы"};
    private static final HashSet<String> DASHBOARD_PROCESS = new HashSet<String>(Arrays.asList("Pipeline", "Изменение условий", "Крупный бизнес ГО",
       "Изменение условий Крупный бизнес ГО", "Крупный бизнес ГО (Структуратор за МО)",
       "Изменение условий Крупный бизнес ГО (Структуратор за МО)"));
    public static boolean showDashboardLink() throws Exception {
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        if(!taskFacade.getGlobalSetting("showDashboardLink").equalsIgnoreCase("true"))
            return false;
        SBeanLocator locator = SBeanLocator.singleton();
        //имеющих привязку к подразделениям из справочника Общие-Структура доступа, у которых признак Подразделение для Dashboards=true.
        PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        Long userid = pupFacade.getCurrentUser().getIdUser();
        User user = ((UserMapper) locator.getBean("userMapper")).getUserById(userid);
        boolean isDashboardDep = locator.getDepartmentMapper().getById(user.getIdDepartment()).isDashboardDep();
        //обладающим хотя бы одной из ролей
        for(Long idProcessType : pupFacade.getIdProcessTypeForUser(userid))
            if(DASHBOARD_PROCESS.contains(pupFacade.getProcessTypeById(idProcessType).getDescriptionProcess())){
                List<String> currentUserRoles = ((UserMapper) locator.getBean("userMapper")).userRoles(userid, idProcessType);
                //если выявлены пользователи с ролью Аудитор ДКАБ или "Администратор системы", то для них не делать проверку на привязку к подразделениям
                if (currentUserRoles.contains("Аудитор ДКАБ") || currentUserRoles.contains("Администратор системы"))
                    return true;
                for(String dashRole : DASHBOARD_ROLES)
                    if(currentUserRoles.contains(dashRole) && isDashboardDep)
                        return true;
            }
        return false;
    }
}
