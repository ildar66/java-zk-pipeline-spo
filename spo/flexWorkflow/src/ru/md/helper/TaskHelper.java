package ru.md.helper;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Task;
import com.vtb.domain.TaskListType;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.ApplProperties;
import com.vtb.util.CollectionUtils;
import com.vtb.util.EjbLocator;
import com.vtb.util.Formatter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.ProcessInfo;
import org.uit.director.tasks.ProcessList;
import org.uit.director.tasks.TaskInfo;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.ced.CedService;
import ru.masterdm.integration.ced.ws.PermissionMapFilter;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.controller.FundList;
import ru.md.controller.N6List;
import ru.md.dict.dbobjects.SystemModuleJPA;
import ru.md.domain.MdTask;
import ru.md.domain.PupTask;
import ru.md.domain.dict.FundingCompany;
import ru.md.jsp.tag.IConst_PUP;
import ru.md.persistence.CompendiumMapper;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.PupMapper;
import ru.md.persistence.UserMapper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.DictionaryFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.loader.TaskLine;
import ru.md.spo.util.Config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Help jsp page to find task by request param.
 *
 * @author Andrey Pavlenko
 */
public class TaskHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHelper.class.getName());

    /**
     * Работает специальная система прав, которая зависит от заявки. Тогда настройки БП не смотрим
     */
    public static boolean isSpecialEditMode(String sectionname, HttpServletRequest request) throws Exception {
        try {
            //форма открыта из всех заявок или из работы проектой команды или из завершённых.
            //Предоставить пользователю с ролью  "загрузка из access" или "контроль загрузки из access"
            //возможность вносить изменения, открывая ее на просмотр для заявок, импортированнх из access
            MdTask mdtask = SBeanLocator.singleton().mdTaskMapper().getById(TaskHelper.getIdMdTask(request));
            if (!mdtask.isImported())
                return false;
            List<String> allUserRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userAllRoles(getCurrentUser(request).getIdUser());
            String status = pup().getPUPAttributeValue(mdtask.getIdPupProcess(), "Статус");
            if ((allUserRoles.contains(UserJPA.ACCESS_DOWNLOAD) && !status.equals("Обработан") && !status.equals("Акцептован")
                    || allUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL) && status.equals("Обработан")) &&
                    !sectionname.startsWith("L_") && mdtask.isImportedAccess())
                return true;
            if ((allUserRoles.contains(UserJPA.ACCESS_DOWNLOAD) && !status.equals("Одобрено")
                    || allUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL) && !status.equals("Одобрено")) &&
                    !sectionname.startsWith("L_") && mdtask.isImportedBM())
                return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    public static boolean isCanEditCedCreditDeal(Long idMdtask, Long idUser) {
        try {
            PermissionMapFilter filter = new PermissionMapFilter();
            filter.setCreditDealId(idMdtask);
            filter.setPerformerId(idUser);
            boolean res = ServiceFactory.getService(CedService.class).getCedCreditDealPermissionMap(filter).isCanEdit();
            LOGGER.info("isCanEditCedCreditDeal(" + idMdtask + ", " + idUser + ") = " + res);
            return res;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("isCanEditCedCreditDeal error" + e.getMessage());
            return true;//если CED не установлен, то считаем что ответ да
        }
    }

    /**
     * Проверяет режим формы заявки. Редактирование или просмотр
     */
    public static boolean isEditMode(String sectionname, HttpServletRequest request) throws Exception {
        //long tstart = System.currentTimeMillis();
        try {
            Boolean romode = ((Boolean) request.getAttribute(IConst_PUP.READONLY));
            if (romode != null && romode) return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (request.getParameter("monitoringmode") != null) {//специальный режим редактирования для МИУ-2
            List<String> cpsRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userCpsRoles(getCurrentUser(request).getIdUser());
            TaskJPA taskJPA = TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request));
            if (!cpsRoles.contains("Руководитель мидл-офиса (мониторинг)") && !cpsRoles.contains("Работник мидл-офиса (мониторинг)"))
                throw new Exception("Режим изменения сделки МИУ-2 доступен только для ролей 'Работник мидл-офиса (мониторинг)' и 'Руководитель мидл-офиса (мониторинг)'");
            String currentStatus = taskJPA.getMonitoringMode();
            Long userInWork = taskJPA.getMonitoringUserWorkId();
            if (userInWork == null && (
                    currentStatus.equals("Редактирование ставки") &&
                            (cpsRoles.contains("Руководитель мидл-офиса (мониторинг)") || cpsRoles.contains("Работник мидл-офиса (мониторинг)"))
                            || currentStatus.equals("Акцепт изменений") && cpsRoles.contains("Руководитель мидл-офиса (мониторинг)")
                            && (taskJPA.getMonitoringPriceUserId() == null || !taskJPA.getMonitoringPriceUserId().equals(getCurrentUser(request).getIdUser())))) {
                taskJPA.setMonitoringUserWorkId(getCurrentUser(request).getIdUser());
                taskFacade().merge(taskJPA);
                userInWork = taskJPA.getMonitoringUserWorkId();
            }
            if (userInWork != null && userInWork.equals(getCurrentUser(request).getIdUser()) &&
                    (sectionname == null || sectionname.equals("") || sectionname.equals("Комментарии") || sectionname.equals("Стоимостные условия"))) {
                return true;
            }
        }
        if (request.getParameter("ced_id") != null) {
            try {
                PermissionMapFilter filter = new PermissionMapFilter();
                filter.setCreditDealId(getIdMdTask(request));
                filter.setPerformerId(getCurrentUser(request).getIdUser());
                if (taskFacade().getTask(getIdMdTask(request)).getProcess() == null
                        && isCanEditCedCreditDeal(getIdMdTask(request), getCurrentUser(request).getIdUser())
                        && (sectionname == null || sectionname.equals("")
                        || sectionname.equals("Комментарии")
                        || sectionname.equals("Договоры")
                        || sectionname.equals("Общие условия")
                        || sectionname.equals("Условия досрочного погашения")
                        || sectionname.equals("Прочие условия")
                        || sectionname.equals("сублимиты")
                        || sectionname.equals("Структура лимита")
                        || sectionname.equals("Стоимостные условия")
                        || sectionname.equals("Документы по сделке")
                        || sectionname.equals("R_Обеспечение")
                        || sectionname.equals("R_Залоги")
                        || sectionname.equals("R_Гарантии")
                        || sectionname.equals("R_Поручительство")
                        || sectionname.equals("R_Стоимостные условия")
                        || sectionname.equals("Основные параметры")))
                    return true;//если это заявка без БП, то переходим в режим редактирования сделки для КОД
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (isSpecialEditMode(sectionname, request))
            return true;
        try {
            if (request.getParameter("viewtype") != null && request.getParameter("viewtype").equals("all"))
                return false;
            PupTask pupTask = getCurrPupTask(request);
            if (pupTask == null) return false;
            if (!pupTask.getIdStatus().equals(2L)) return false;
        } catch (Exception e) {
            return false;
        }
        if (sectionname == null || sectionname.equals("") || sectionname.equals("Комментарии")
                || sectionname.equals("Документы по сделке") || sectionname.equals("Транши"))
            return true;// не проверяем. Могут редактировать все
        if (sectionname.equals("sectionname"))
            LOGGER.info("sectionname");
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
                .getActionProcessor("Task");
        try {
            PupTask pupTask = getCurrPupTask(request);
            if (!sectionname.startsWith("L_") && !sectionname.startsWith("R_"))
                sectionname = "R_" + sectionname;
            long idStage = pupTask.getIdStageTo();
            Integer idTypeProcess = pupTask.getIdTypeProcess().intValue();
            boolean res = processor.isPermissionEdit(idStage, sectionname, idTypeProcess);
            return res;
        } catch (Exception e) {
            LOGGER.warn("атрибут '" + sectionname + "' не задан");
            return false;
        }
    }

    /**
     * Показать кнопку отказа для МО процессов
     */
	public static boolean isShowRefuseMo(HttpServletRequest request) throws Exception {
        //Для активных (незавершенных) заявок по бп «Крупный бизнес ГО (Структуратор за МО)», «Изменение условий Крупный бизнес ГО (Структуратор за МО)»
        // на эф просмотра / редактирования пользователям с ролью структуратор с галкой в ПрК, рук структуратора с галкой в ПрК (подразделение структуратора или выше)
        // должна быть доступна кнопка Отказать
        MdTask task = getMdTask(request);
        if(task.getIdStatus() == null || task.getIdStatus() > 1)
            return false;
        String pName = task.getProcessname();
        PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        if (!pupFacade.isCurrentUserInProjectTeam(task.getIdMdtask()))
            return false;
        if (task.getIdPupProcess() == null )
            return false;
        List<String> currentUserAssignedAs = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).
            userAssignedAs(getCurrentUser(request).getIdUser(), task.getIdPupProcess());
        return ((pName.equalsIgnoreCase("Крупный бизнес ГО (Структуратор за МО)")
                || pName.equalsIgnoreCase("Изменение условий Крупный бизнес ГО (Структуратор за МО)")) &&
                (currentUserAssignedAs.contains("Структуратор (за МО)") || currentUserAssignedAs.contains("Руководитель структуратора (за МО)")))
                ||((pName.equalsIgnoreCase("Крупный бизнес ГО") || pName.equalsIgnoreCase("Изменение условий Крупный бизнес ГО")) &&
                (currentUserAssignedAs.contains("Структуратор") || currentUserAssignedAs.contains("Руководитель структуратора")));
    }
	public static String isExternal(HttpServletRequest request) throws Exception {
		Object attr = request.getSession().getAttribute("IS_EXTERNAL");
		if (attr == null)
			return "0";
		return attr.toString();
	}
	public static MdTask getMdTask(HttpServletRequest request) throws Exception {
		if (request.getAttribute("mdtask") != null)
			return (MdTask) request.getAttribute("mdtask");
		MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(getIdMdTask(request));
		request.setAttribute("mdtask", task);
		return task;
	}
	/**
	 * По параметрам запроса возвращает mdtaskid заявки.
	 */
	public static Long getIdMdTask(HttpServletRequest request) throws Exception {
		String mdtaskid = request.getParameter("mdtask");// возможно номер есть в запросе, если это
																											// саблимит
		//"0" при обращении из фрейма
		if (mdtaskid != null && !mdtaskid.equals("") && !mdtaskid.equals("0"))
			return Formatter.parseLong(mdtaskid);
		mdtaskid = request.getParameter("mdtaskid");// возможна номер есть в запросе, если это фрейм в
																								// аяксе
		if (mdtaskid != null && !mdtaskid.equals(""))
			return Formatter.parseLong(mdtaskid);

        // или его уже положили в request
        if (request.getAttribute("mdtaskid") != null)
            return (Long) request.getAttribute("mdtaskid");

        // Если дошли до этих строк, значит в кеше ничего нет, придется вычислять
        String idProcessList = request.getParameter(IConst_PUP.PROCESS_LIST_ID);// используется для
        // списка все заявки
        WorkflowSessionContext wsc = AbstractAction
                .getWorkflowSessionContext(request);
        Long idProcess = null;
        if (idProcessList == null) {// открываем workList
            idProcess = wsc.getCurrTaskInfo(false).getIdProcess();
        } else {// открываем processList
            idProcess = wsc.getProcessList().getTableProcessList().get(Integer.parseInt(idProcessList))
                    .getIdProcess();
        }
        try {
            TaskFacadeLocal taskFacadeLocal = EjbLocator.getInstance()
                    .getReference(TaskFacadeLocal.class);
            // целый TaskJPA начитать не страшно, так как он в кеше и мы просто получим ссылку
            Long id = taskFacadeLocal.getTaskByPupID(idProcess).getId();
            request.setAttribute("mdtaskid", id);
            return id;
        } catch (FactoryException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Обертка для получения mdtask для формы просмотра/редактирования.
     */
    @Deprecated
    // лучше использовать taskFacadeLocal с кешем
    public static Task findTask(HttpServletRequest request) throws Exception {
        String taskid = request.getParameter("mdtask");// какую заявку просят
        if (taskid == null)
            taskid = request.getParameter("mdtaskid");
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
                .getActionProcessor("Task");
        Task task = (Task) request.getAttribute("mdtaskobject");// объект возможно уже есть в сессии
        if (taskid != null && !taskid.equals("0")) {
            if (task == null || !task.getId_task().toString().equals(taskid)) {
                task = processor.getTask(new Task(new Long(taskid)));
                request.setAttribute("mdtaskobject", task);
            }
            return task;
        }

        String idProcessList = request.getParameter(IConst_PUP.PROCESS_LIST_ID);
        WorkflowSessionContext wsc = AbstractAction
                .getWorkflowSessionContext(request);
        Long idprocess = null;
        Integer processType = null;
        ProcessInfo processInfo = null;// информация о процессе. Получить её трудно для процессора.
        if (idProcessList == null) {
            processInfo = wsc.getCurrTaskInfo(false);
            idprocess = processInfo.getIdProcess();
            processType = processInfo.getIdTypeProcess();
        } else {
            ProcessList processList = wsc.getProcessList();
            processInfo = processList.getTableProcessList().get(
                    Integer.parseInt(idProcessList));
            idprocess = processInfo.getIdProcess();
            processType = processInfo.getIdTypeProcess();
        }
        try {
            if (taskid == null && // найдем по idprocess
                    (task == null || idprocess.longValue() != task.getId_pup_process().longValue())) {
                task = processor.findByPupID(idprocess, true);// долгая функция
                request.setAttribute("mdtaskobject", task);
                if (processInfo != null)
                    processInfo.execute();// Долгая операция
            }
            if (taskid != null && // найдем по taskid
                    (task == null || (!taskid.equals(task.getId_task().toString())))) {
                task = processor.getTask(new Task(new Long(taskid)));// долгая функция
                request.setAttribute("mdtaskobject", task);
                if (processInfo != null)
                    processInfo.execute();// Долгая операция
            }
            task.setId_pup_process_type(processType);
        } catch (MappingException e) {
            LOGGER.error("MappingException " + e.getMessage());
            e.printStackTrace();
        }

        return task;
    }

    /**
     * Возвращает URL на редактирование или просмотр саблимита.
     *
     * @return URL на редактирование или просмотр саблимита
     */
    public static String getSublimitURL(HttpServletRequest request, boolean readOnly, Task sublimit) {
        if (readOnly || sublimit.getHeader().getCrmstatus().equalsIgnoreCase("Утвержденный")) {// режим
            // чтения
            if (request.getParameter("idListProcess") != null) {// если зашли из все заявки
                return "form.jsp?idListProcess=" + request.getParameter("idListProcess") + "&mdtask="
                        + sublimit.getId_task();
            } else {// зашли через
                return "task.context.do?id=" + request.getParameter("id") + "&mdtask="
                        + sublimit.getId_task();
            }
        } else {// режим редактирования
            WorkflowSessionContext wsc = AbstractAction
                    .getWorkflowSessionContext(request);
            Long idcurrtask = Long.valueOf(wsc.getIdCurrTask());
            return "task.context.do?id=" + idcurrtask + "&mdtask=" + sublimit.getId_task();
        }
    }

    /**
     * Возвращает страницу списка заявок.
     *
     * @throws FactoryException
     */
    public static TaskPage getTaskPage(HttpServletRequest request) throws FactoryException {
        long tstart = System.currentTimeMillis();
        PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);

        TaskPage taskpage = new TaskPage();
        Long navigation = Formatter.parseLong(request.getParameter("navigation"));
        if (navigation != null) {
            LOGGER.trace("============getTaskPage navigation is '" + navigation + "'. set taskpage.setCurr to '" + navigation + "'");

            taskpage.setCurr(navigation);
        } else {
            ProcessSearchParam param = new ProcessSearchParam(request, request.getParameter("closed") != null);

            LOGGER.trace("============getTaskPage navigation is null. set taskpage.setCurr as ProcessSearchParam.getPageNumber value '" + param.getPageNumber() + "'");

            taskpage.setCurr(param.getPageNumber());
        }
        String readonly = request.getParameter(IConst_PUP.READONLY);
        taskpage.setTypeList(request.getParameter("typeList"));
        if (request.getAttribute("typeList") != null)
            taskpage.setTypeList((String) request.getAttribute("typeList"));
        String typeListAttr = taskpage.getTypeList();
        if (request.getParameter("closed") != null)
            typeListAttr += "&closed=true";
        if (request.getParameter("favorite") != null)
            typeListAttr += "&favorite=true";
        if (request.getParameter("expertteam") != null)
            typeListAttr += "&expertteam=true";
        request.getSession().setAttribute("typeListAttr", typeListAttr);
        ArrayList<Long> list = new ArrayList<Long>();
        Long pageSize = Long.valueOf(Config.getProperty("PROCESSES_ON_PAGE"));
        if (taskpage.isAllMode()) {
            LOGGER.info("typeList.equals(all)");
            taskpage.setCount(pupFacade.getProcessListCount(wsc.getIdUser(), (Integer) request.getAttribute("idDepartment"), new ProcessSearchParam(request, request.getParameter("closed") != null)));
            list = pupFacade.getProcessList(wsc.getIdUser(), (Integer) request.getAttribute("idDepartment"), new ProcessSearchParam(request, request.getParameter("closed") != null), pageSize, taskpage.getCurr() * pageSize);
        } else {
            LOGGER.info("typeList is not equals(all)");
            taskpage.setCount(pupFacade.getWorkListCount(wsc.getIdUser(), taskpage.getTaskListType(), new ProcessSearchParam(request, true)));
            list = pupFacade.getWorkList(wsc.getIdUser(), taskpage.getTaskListType(), new ProcessSearchParam(request, true),
                    pageSize, taskpage.getCurr() * pageSize);
        }
        boolean createFunding = taskFacade.getGlobalSetting("createFunding").equalsIgnoreCase("true");
        // Цикл по всем операциям (заявкам), попадающим под условия фильтрации.
        boolean trStripe = true;
        for (Long id : list) {
            TaskLine taskLine = new TaskLine(taskpage.getTypeList());
            taskLine.setIdUser(wsc.getIdUser().toString());
            taskLine.setTrClass(trStripe ? "a" : "b");
            trStripe = !trStripe;
            if (taskpage.isAllMode()) {
                taskLine.setIdProcess(id);
            } else {
                taskLine.setIdTask(id);
                TaskInfoJPA ti = pupFacade.getTask(id);
                taskLine.setIdProcess(ti.getProcess().getId());
                taskLine.setNameStageTo(ti.getStage().getDescription());
                taskLine.setIdTaskDepartment(ti.getIdDepartament());
                if (taskpage.getTaskListType() == TaskListType.ACCEPT_FOR_REFUSE)
                    taskLine.setDateOfTakingStr(Formatter.formatDateTime(ti.getStartDate()));
                if (ti.getExecutor() != null && ti.getExecutor().getIdUser() != null)
                    try {
                        Long idExecutor = ti.getExecutor().getIdUser();
                        taskpage.setNameIspoln(pup().getUser(idExecutor).getFullName());
                        taskLine.setNameIspoln(pup().getUser(idExecutor).getFullName());
                        taskLine.setIdUser(String.valueOf(idExecutor));
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage());
                    }
            }
            taskpage.getTaskLineList().add(taskLine);
        }
        try {
            taskpage.setTaskLineList(taskFacade.loadTaskLines(taskpage.getTaskLineList(), wsc.getIdUser()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        for (TaskLine taskLine : taskpage.getTaskLineList()) {
            try {
                taskLine.setUrl(getUrl(request, taskpage, readonly, taskLine));
                List<String> currentUserAssignedAs = new ArrayList<String>();
                if (createFunding || taskLine.isShowEditConditionLink())
                    currentUserAssignedAs = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userAssignedAs(wsc.getIdUser(), taskLine.getIdProcess());
                List<String> currentUserRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userRoles(wsc.getIdUser(), taskLine.getIdTypeProcess());
                // проверим созданы ли заявки в МАК
                taskLine.setHasN6(N6List.isHasFunds(taskLine.getIdMDTask()));
                if (taskLine.isOpportunity())
                    taskLine.setHasFunds(FundList.isHasFunds(taskLine.getIdMDTask()));

                if (createFunding) {
                    taskLine.setShowFundingCreateN6Link(
                            currentUserAssignedAs.contains("Кредитный аналитик")
                                    && (request.getParameter("projectteam") != null || request.getParameter("closed") != null)
                                    || request.getParameter("projectteam") != null && (taskLine.isOpportunity()
                                    && currentUserAssignedAs.contains("Продуктовый менеджер") || !taskLine.isOpportunity()
                                    && currentUserAssignedAs.contains("Клиентский менеджер")));
                    taskLine.setShowFundingCreateN6Link(taskLine.isShowFundingCreateN6Link()
                            && !pupFacade.getProcessById(taskLine.getIdProcess()).isPaused());
                    if (taskLine.getStatusReturnType().equals("0"))
                        taskLine.setShowFundingCreateN6Link(false);
                    taskLine.setShowFundingCreateLink(taskLine.isOpportunity()
                            && taskLine.isShowFundingCreateN6Link()
                            && currentUserAssignedAs.contains("Кредитный аналитик"));
                } else {
                    taskLine.setShowFundingCreateN6Link(false);
                    taskLine.setShowFundingCreateLink(false);
                }
                if (request.getParameter("projectteam") != null || request.getParameter("closed") != null) {
                    // пользователь ЧПК
                    // с ролью «Структуратор»/«Руководитель структуратора»/«Продуктовый менеджер»
                    boolean ready4DealConclusion = pupFacade.isCurrentUserInProjectTeam(taskLine
                            .getIdMDTask())
                            && taskLine.isOpportunity()
                            && (currentUserAssignedAs.contains("Продуктовый менеджер")
                            || currentUserAssignedAs.contains("Руководитель структуратора")
                            || currentUserRoles.contains("Руководитель структуратора (за МО)")
                            || currentUserRoles.contains("Структуратор (за МО)")
                            || currentUserAssignedAs.contains("Структуратор"));
                    taskLine.setShowDealConclusionCreateLink(ready4DealConclusion
                            && (taskLine.getStatusReturnType().equals("1") || taskLine.getStatusReturnType()
                            .isEmpty() && request.getParameter("projectteam") != null));
                    taskLine.setShowPreDealConclusionCreateLink(false);
                }
                // выделять цветом просроченные
                if (taskpage.isAllMode()
                        && request.getParameter("paused") != null
                        && request.getParameter("paused").equals("true")
                        && pupFacade.getProcessById(taskLine.getIdProcess()).getResumeDate().before(new Date()))
                    taskLine.setTrClass("expired");
                // отображение иконки "Изменить условия"
                if (taskLine.isShowEditConditionLink()) {
                    // доступна для ролей «Структуратор», включенный в «Проектную команду» с признаком
                    // «выполнение операции», и «Руководитель структуратора»
                    taskLine.setShowEditConditionLink(request.getParameter("closed") != null && (
                            currentUserRoles.contains("Руководитель структуратора")
                                    || currentUserRoles.contains("Руководитель структуратора (за МО)")
                                    || (pupFacade.isCurrentUserInProjectTeam(taskLine.getIdMDTask())
                                    && (currentUserAssignedAs.contains("Структуратор") || currentUserAssignedAs.contains("Структуратор (за МО)")))));
                }
                if (taskLine.getDescriptionProcess().equalsIgnoreCase("Pipeline")) {
                    MdTask mdtask = SBeanLocator.singleton().mdTaskMapper().getById(taskLine.getIdMDTask());
                    String projectName = ru.masterdm.spo.utils.Formatter.str(mdtask.getProjectName());
                    if (!projectName.isEmpty())
                        taskLine.setContractors(projectName);
                    taskLine.setShowEditConditionLink(false);
                    taskLine.setShowFundingCreateLink(false);
                    taskLine.setShowFundingCreateN6Link(false);
                    taskLine.setShowDealConclusionCreateLink(false);
                    taskLine.setShowPreDealConclusionCreateLink(false);
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                taskLine.setContractors("Нет информации о контрагентах для pupid="
                        + taskLine.getIdProcess() + ". " + " Код ошибки: " + e.getMessage());
                taskLine.setSum(new BigDecimal(0));
                taskLine.setIdMDTask(0l);
                taskLine.setIdTask(0l);
                taskLine.setIdTaskDepartment(0l);
            }

            if (taskpage.getTypeList().equals("noAccept") || taskpage.getTypeList().equals("perform")) {
                taskLine.setAssignedUsers(pupFacade.getAssignedUser(taskLine.getIdTask()));
            }
        }
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getTaskPage() time " + loadTime);
        request.setAttribute("loadTime", Formatter.format(Double.valueOf(loadTime) / 1000));
        return taskpage;
    }

    private static String getUrl(HttpServletRequest request, TaskPage taskpage,
                                 String readonly, TaskLine taskLine) {
        String url = "";
        if (taskpage.isAllMode()) {
            url = "/" + ApplProperties.getwebcontextFWF() + "/form.jsp?mdtaskid=" + taskLine.getIdMDTask();
            if (request.getParameter("projectteam") != null) {
                url += "&viewtype=projectteam";
            }
            if (request.getParameter("expertteam") != null) {
                url += "&viewtype=expertteam";
            }
            if (request.getParameter("closed") != null) {
                url += "&viewtype=closed";
            }
            if (request.getParameter("paused") != null) {
                url += "&paused=true";
            }
            if (request.getParameter("projectteam") == null && request.getParameter("expertteam") == null
                    && request.getParameter("closed") == null && request.getParameter("paused") == null) {
                url += "&viewtype=all";
            }
        } else {
            url = "/" + ApplProperties.getwebcontextFWF() + "/task.context.do?id="
                    + taskLine.getIdTask() + ((readonly != null) ? "&" + IConst_PUP.READONLY + "=1" : "");
        }
        return url;
    }

    /**
     * Escape characters for text appearing in HTML markup.
     * <p>
     * This method exists as a defence against Cross Site Scripting (XSS) hacks. The idea is to
     * neutralize control characters commonly used by scripts, such that they will not be executed by
     * the browser. This is done by replacing the control characters with their escaped equivalents.
     * See {@link hirondelle.web4j.security.SafeText} as well.
     * <p>
     * The following characters are replaced with corresponding HTML character entities :
     * <table border='1' cellpadding='3' cellspacing='0'>
     * Note that JSTL's {@code <c:out>} escapes <em>only the first
     * five</em> of the above characters.
     */
    public static String forHTML(String aText) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '&') {
                result.append("&amp;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\t') {
                addCharEntity(9, result);
            } else if (character == '!') {
                addCharEntity(33, result);
            } else if (character == '#') {
                addCharEntity(35, result);
            } else if (character == '$') {
                addCharEntity(36, result);
            } else if (character == '%') {
                addCharEntity(37, result);
            } else if (character == '\'') {
                addCharEntity(39, result);
            } else if (character == '(') {
                addCharEntity(40, result);
            } else if (character == ')') {
                addCharEntity(41, result);
            } else if (character == '*') {
                addCharEntity(42, result);
            } else if (character == '+') {
                addCharEntity(43, result);
            } else if (character == ',') {
                addCharEntity(44, result);
            } else if (character == '-') {
                addCharEntity(45, result);
            } else if (character == '.') {
                addCharEntity(46, result);
            } else if (character == '/') {
                addCharEntity(47, result);
            } else if (character == ':') {
                addCharEntity(58, result);
            } else if (character == ';') {
                addCharEntity(59, result);
            } else if (character == '=') {
                addCharEntity(61, result);
            } else if (character == '?') {
                addCharEntity(63, result);
            } else if (character == '@') {
                addCharEntity(64, result);
            } else if (character == '[') {
                addCharEntity(91, result);
            } else if (character == '\\') {
                addCharEntity(92, result);
            } else if (character == ']') {
                addCharEntity(93, result);
            } else if (character == '^') {
                addCharEntity(94, result);
            } else if (character == '_') {
                addCharEntity(95, result);
            } else if (character == '`') {
                addCharEntity(96, result);
            } else if (character == '{') {
                addCharEntity(123, result);
            } else if (character == '|') {
                addCharEntity(124, result);
            } else if (character == '}') {
                addCharEntity(125, result);
            } else if (character == '~') {
                addCharEntity(126, result);
            } else {
                // the char is not a special one
                // add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    private static void addCharEntity(Integer aIdx, StringBuilder aBuilder) {
        String padding = "";
        if (aIdx <= 9) {
            padding = "00";
        } else if (aIdx <= 99) {
            padding = "0";
        } else {
            // no prefix
        }
        String number = padding + aIdx.toString();
        aBuilder.append("&#" + number + ";");
    }

    public static Long getProcessTypeId(HttpServletRequest request, HttpServletResponse response)
            throws FactoryException {
        Long processTypeId = null;
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                PupFacadeLocal.class);
        Set<ProcessTypeJPA> processTypeList = pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null);
        if (processTypeList.size() == 1)
            processTypeId = ((ProcessTypeJPA) processTypeList.toArray()[0]).getIdTypeProcess();
        String SprocessTypeId = request.getParameter("idProcessType");
        if (SprocessTypeId != null) {
            request.getSession().setAttribute("idProcessType", request.getParameter("idProcessType"));
            Cookie cookie = new Cookie("idProcessType", request.getParameter("idProcessType"));
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
        }
        if (SprocessTypeId == null) {
            SprocessTypeId = (String) request.getSession().getAttribute("idProcessType");
            for (Cookie c : request.getCookies())
                if (c.getName().equals("idProcessType"))
                    SprocessTypeId = c.getValue();
        }
        if (SprocessTypeId != null) {
            for (ProcessTypeJPA p : processTypeList) {
                if (p.getIdTypeProcess().toString().equals(SprocessTypeId)) {
                    processTypeId = p.getIdTypeProcess();
                }
            }
        }
        return processTypeId;
    }

    public static String getMemorandumMessage(HttpServletRequest request) throws Exception {
        if (!isMemorandumStage(request))
            return "";
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                    PupFacadeLocal.class);
            return pupFacadeLocal.getPUPAttributeValue(findTask(request).getId_pup_process(),
                    "сообщение_фкм");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "";
    }

    private static boolean isMemorandumStage(HttpServletRequest request) throws Exception {
        if (!isEditMode("", request))
            return false;
        TaskInfo taskInfo = getCurrTaskInfo(request);
        if (taskInfo == null)
            return false;
        String stage = taskInfo.getNameStageTo();
        return (taskInfo.getNameTypeProcess().startsWith("Крупный бизнес ГО")
                || taskInfo.getNameTypeProcess().startsWith("Изменение условий Крупный бизнес ГО"))//Включает БП в связи с изменениями
                &&
                (stage.startsWith("Определение необходимых экспертиз")
                        || stage.startsWith("Акцепт перечня дополнительных экспертиз")
                        || stage.startsWith("Акцепт перечня экспертиз")
                        || stage.startsWith("Получение решения УО/УЛ и направление запрос"));
    }

    public static PupTask getCurrPupTask(HttpServletRequest request) throws Exception {
        PupMapper pupMapper = SBeanLocator.singleton().getPupMapper();
        Long pupTaskId = Long.valueOf(getCurrPupTaskId(request));
        if (pupTaskId.equals(0L))
            return null;
        else
            return pupMapper.getPupTask(pupTaskId);
    }

    public static String getCurrPupTaskId(HttpServletRequest request) throws Exception {
        String pupTaskId = request.getParameter("pupTaskId");
        if (pupTaskId == null)
            pupTaskId = request.getParameter("id");
        if (pupTaskId == null)
            pupTaskId = "0";
        return pupTaskId;
    }

    public static String getCurrStageName(HttpServletRequest request) throws Exception {
        TaskInfo taskInfo = TaskHelper.getCurrTaskInfo(request);
        if (taskInfo == null)
            return "";
        return Formatter.str(taskInfo.getNameStageTo());
    }

    public static TaskInfo getCurrTaskInfo(HttpServletRequest request) throws Exception {
        TaskInfo taskInfo = (TaskInfo) request.getAttribute(IConst_PUP.TASK_INFO);
        String pupTaskId = getCurrPupTaskId(request);
        if (taskInfo == null && pupTaskId != null && !pupTaskId.equals("0")) {
            taskInfo = new TaskInfo();
            taskInfo.init(AbstractAction.getWorkflowSessionContext(request), Long.valueOf(pupTaskId), false);
            taskInfo.execute();
        }
        return taskInfo;
    }

    public static boolean blockExpertise(HttpServletRequest request, String attrName)
            throws Exception {
        if (!attrName.equals("Требуется экспертиза подразделения по анализу рисков"))
            return false;
        if (!isMemorandumStage(request))
            return false;
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                    PupFacadeLocal.class);
            String exp = pupFacadeLocal.getPUPAttributeValue(findTask(request).getId_pup_process(),
                    "Формирование Кредитного меморандума");
            if (exp.equals("0") || exp.equals("2"))
                return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    public static UserJPA getCurrentUser(HttpServletRequest request) throws FactoryException {
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                PupFacadeLocal.class);
        ru.md.pup.dbobjects.UserJPA user = pupFacadeLocal.getUser(wsc.getIdUser());
        return user;
    }

    public static DictionaryFacadeLocal dict() {
        try {
            return com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static PupFacadeLocal pup() {
        try {
            return com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static TaskFacadeLocal taskFacade() {
        try {
            return com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean isCanEditDoc(AttachJPA a, UserJPA currentUser, Long idTypeProcess) {
        if (a == null)
            return false;
        if (a.getWhoAdd() != null) {
            if (a.getWhoAccepted() == null)
                // Для неутвержденного документа, загруженного реальным пользователем: пользователь,
                // загрузивший документ.
                return a.getWhoAdd().equals(currentUser);
            else
                // Для утвержденного документа, загруженного реальным пользователем: пользователь,
                // утвердивший документ.
                return a.getWhoAccepted().equals(currentUser);
        } else {
            return currentUser.hasRole(idTypeProcess, "Работник мидл-офиса")
                    || currentUser.hasRole(idTypeProcess, "Руководитель мидл-офиса");
        }
    }

    /**
     * Можно ли акцептовать версию сделки для КОД
     */
    public static boolean isCanAcceptCedProduct(HttpServletRequest request) throws Exception {
        try {
            PermissionMapFilter filter = new PermissionMapFilter();
            filter.setCreditDealId(getIdMdTask(request));
            filter.setPerformerId(getCurrentUser(request).getIdUser());
            return ServiceFactory.getService(CedService.class).getCedCreditDealPermissionMap(filter).isCanAccept();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Проверяет режим редактирования с целью фондирования
     *
     * @param request объект Request
     * @return разрешено редактировать
     * @throws Exception ошибка
     */
    public static boolean isCanEditFund(HttpServletRequest request) throws Exception {
        if (true)//Отключим этот режим так как не можем найти кто такие требования выдвигал
            return false;
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(getIdMdTask(request));
        // для лимита и версий КОД можно сразу венрнуть отрицательный результат
        if (!task.isProduct() || task.getIdPupProcess() == null) {
            return false;
        }
        // в представлении Проектная команда или все заявки или завершенные заявки
        if (!(request.getParameter("viewtype") != null && (request.getParameter("viewtype").equals("all")
                || request.getParameter("viewtype").equals("projectteam") || request.getParameter("viewtype").equals("closed"))))
            return false;
        // и назначен как Продуктовый менеджер или Клиентский менеджер на задачу
        List<String> currentUserAssignedAs = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).
                userAssignedAs(getCurrentUser(request).getIdUser(), task.getIdPupProcess());
        if (!currentUserAssignedAs.contains("Продуктовый менеджер")
                && !currentUserAssignedAs.contains("Клиентский менеджер"))
            return false;
        // и задача не находится на ком-либо, кроме него
        List<TaskInfoJPA> taskInfos = pup().getTaskInWork(task.getIdPupProcess());
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        for (TaskInfoJPA taskInfo : taskInfos)
            if (taskInfo.getExecutor() != null && !taskInfo.getExecutor().getIdUser().equals(wsc.getIdUser()))
                return false;
        return true;
    }

    public static String versionCheck() {
        long tstart = System.currentTimeMillis();
        String msg = versionCheck("CD", ApplProperties.required_version_compendium);
        msg += versionCheck("CC", ApplProperties.required_version_cc);
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** versionCheck() time " + loadTime);
        return msg;
    }

    public static String versionCheck(String moduleKey, String requiredVersion) {
        SystemModuleJPA module = dict().getModuleInfo(moduleKey);
        String msg = "<div class='error'>Требуется модуль '" + module.getName() + "' версии не ниже " + Formatter.parseDouble(requiredVersion);
        if (module == null || module.getCurrentVersion() == null || module.getCurrentVersion().isEmpty())
            return msg + "</div>";
        String currentVersion = module.getCurrentVersion().replaceAll("-SNAPSHOT", "");
        if (Formatter.parseDouble(requiredVersion) == null || Formatter.parseDouble(currentVersion) == null)
            return "";
        if (Formatter.parseDouble(requiredVersion) > Formatter.parseDouble(currentVersion))
            return msg + ", сейчас установлен модуль " + " версии " + currentVersion + "</div>";
        else
            return "";
    }

    /**
     * блокировать кнопку далее
     */
    public static boolean blockNextButton(HttpServletRequest request) {
        if (request.getParameter("readonly") != null)
            return true;
        try {
            if (pup().getPUPAttributeValue(getMdTask(request).getIdPupProcess(), "Decision").equals("5")) {
                //Decision==5 и статуса нет
                Long ccStatus = findTask(request).getCcStatus().getStatus().getId();
                if (ccStatus == null || ccStatus.longValue() == 0 || ccStatus.longValue() == 6)
                    return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * показывать кнопку далее
     */
    public static boolean showNextButton(String stageName, Long processId) {
        if (stageName == null || stageName.equals("Получение результатов экспертиз") || stageName.equals("Получение результатов экспертиз в связи с изменениями"))
            return false;
        if (pup().getPUPAttributeBooleanValue(processId, "Убрать кнопку Завершить"))
            return false;
        return true;
    }

    public static String getIndRateNameById(String id) {
        if (id == null || id.isEmpty())
            return "";
        CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        for (FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(), null)) {
            if (fpar.getId().equals(id))
                return fpar.getText();
        }
        return id;
    }

    public static List<FloatPartOfActiveRate> getIndRateOptions(Long idMdtask) throws Exception {
        ArrayList<FloatPartOfActiveRate> res = new ArrayList<FloatPartOfActiveRate>();
        CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        FloatPartOfActiveRate[] allInd = compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(), null);
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacade.getTask(idMdtask);

        if (!taskJPA.isInterestRateDerivative() || taskJPA.getMainIndrates().size() == 0) {//тогда вернём весь список
            for (FloatPartOfActiveRate fp : allInd) res.add(fp);
            return res;
        }

        for (String id : taskJPA.getMainIndrates())
            for (FloatPartOfActiveRate fp : allInd)
                if (id.equals(fp.getId()))
                    res.add(fp);
        return res;
    }

    public static boolean showSectionReturnStatusCC(Task task) {
        return task.getCcStatus().getStatus().getId() != null
                && task.getCcStatus().getStatus().getId().longValue() != 0
                && task.getCcStatus().getStatus().getCategoryId() != null
                && task.getTaskStatusReturn().getDateReturn() == null;
    }

    public static boolean showSectionReturnStatus(Task task) {
        return task.getTaskStatusReturn() != null && task.getTaskStatusReturn().getStatusReturn() != null
                && task.getTaskStatusReturn().getStatusReturn().getId() != null && !task.getTaskStatusReturn().getStatusReturn().getId().isEmpty();
    }

    public static boolean showSectionReturnStatusCC(MdTask mdtask) {
        return mdtask.getCcCacheStatusid() != null
                && mdtask.getCcCacheStatusid().longValue() != 0;
    }

    public static boolean showSectionReturnStatus(MdTask mdtask) {
        return mdtask.getStatusreturn() != null && !mdtask.getStatusreturn().isEmpty();
    }

    public static boolean showSpecialDecision(MdTask mdtask) {
        return mdtask.isProduct() && mdtask.getIdPupProcess() != null
                && pup().getAttributeList(mdtask.getIdTypeProcess()).contains("R_Особые условия решения");
        //убрал проверку аттрибута в интересах VTBSPO-826
        //вернул в интересах VTBSPO-866
    }

    /**
     * Возвращает спискок выдающих банков, по которым можно запустить процесс. Через запятую
     *
     * @return
     */
    public static String getRunProcessFundingCompanies() {
        ArrayList<String> res = new ArrayList<String>();
        CompendiumMapper compendiumMapper = SBeanLocator.singleton().getCompendiumMapper();
        for (FundingCompany f : compendiumMapper.getFundCompaniesFull(null))
            if (f.getRunProcess())
                res.add(f.getName());
        if (res.size() == 0)
            return "нет подходящего выдающего банка";
        return CollectionUtils.listJoin(res);
    }

    /**
     * Возвращает стоимостные условия для заявки
     *
     * @param monitoring_copy брать временную копию для мониторинга
     */
    public static Task getTask4PriceCondition(Long mdtaskid, boolean monitoring_copy) throws Exception {
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        if (monitoring_copy) {
            //провериить где у нас копия вернуть её
            TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacade.getTask(mdtaskid);
            if (taskJPA.getMonitoringMdtask() != null)
                return processor.getTask(new Task(taskJPA.getMonitoringMdtask()));
            else //создать копию стоимостных условий
                return processor.getTask(new Task(taskFacade.createPriceConditionVersion(mdtaskid)));
        } else
            return processor.getTask(new Task(mdtaskid));
    }

    public static Cookie prepareCookie(String name, String value) {
        try {
            Cookie c = new Cookie(name, value == null ? "" : URLEncoder.encode(value, "UTF-8"));
            if (value == null) c.setMaxAge(0);
            return c;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Cookie("URLEncoder.encodeerror", e.getMessage());
        }
    }
    private static String formatStatus(MdTask mdtask) {
        Integer idDep = SBeanLocator.singleton().mdTaskMapper().getCCQuestion(mdtask.getIdMdtask()).idDep;
        String statusmap = Formatter.str(mdtask.getMapStatus());
        if (idDep == null || statusmap.isEmpty())
            return statusmap;
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        return compenduim.getDepartment(new Department(idDep)).getShortName() + ". "+ statusmap.replaceAll("КК. ","");
    }
    public static String getCcMapStatus4List(Long mdtaskid) {
        MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        MdTask mdtask = mapper.getById(mdtaskid);
        if (mdtask.getQuestionGroup() == null)
            return formatStatus(mdtask);
        ArrayList<String> status = getStatusList(mdtask.getQuestionGroup());
        if (status.size() < 2)
            return StringUtils.join(status, "; ");
        return "<span title='" + StringUtils.join(status, "; ")+"'>"+ status.get(0)+"...</span>";
    }
    private static ArrayList<String> getStatusList(Long questionGroup){
        MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        ArrayList<String> status = new ArrayList<String>();
        for(Long id : mapper.getIdMdtaskByQuestionGroup(questionGroup)){
            String s = formatStatus(mapper.getById(id));
            if (!s.isEmpty())
                status.add(s);
        }
        return status;
    }
    public static String getCcMapStatus(MdTask mdtask) {
        if (mdtask.getQuestionGroup() == null)
            return formatStatus(mdtask);
        ArrayList<String> status = getStatusList(mdtask.getQuestionGroup());
        if (status.size() < 3)
            return StringUtils.join(status, "; ");
        return "<span title='" + StringUtils.join(status, "; ")+"'>"+
                status.get(0)+";"+status.get(1)+"...</span>";
    }
}
