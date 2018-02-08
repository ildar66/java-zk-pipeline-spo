package org.uit.director.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;

import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskManager;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.masterdm.integration.cps.CpsService;
import ru.masterdm.spo.integration.FilialTaskList;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.controller.PipelineController;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.*;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.util.TaskVersionHelper;

// Referenced classes of package org.uit.director.action:
//            AbstractAction, UpdateAttributesAction
/**
 * Создание заявки (сделки или лимита) пользователем через UI (CreateApplication.jsp)
 */
public class RemoteCreateAction extends Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCreateAction.class);

	private PupFacadeLocal pupFacadeLocal;
	private TaskActionProcessor processor;
	private NotifyFacadeLocal notifyFacade;
	private CompendiumActionProcessor compenduim;
	private TaskFacadeLocal taskFacadeLocal;
	private CompendiumSpoActionProcessor compenduimSPO;
	private String target;

	/**
	 * Конструктор.
	 */
	public RemoteCreateAction() {
	    target = "acceptedTasks";
	    try {
    	    pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
            compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
            taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	    } catch (Exception e) {
	        LOGGER.error(e.getMessage(), e);
	    }
	}

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	    WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        target = "acceptedTasks";
		try {
            Task task = init(request, null);

            if (task.getHeader().getVersion() != null) {
                LOGGER.info("RemoteCreateAction new version");
                createVersion(request, wsc, task);
                response.sendRedirect(mapping.findForward(target).getPath().substring(1));
                return null;
            }

			Long pupId = null;
			if (task.getId_task() != null) {
                LOGGER.info("RemoteCreateAction renewProcess");
			    pupId = renewProcess(request, wsc, task);
			} else {
                LOGGER.info("RemoteCreateAction createNewProcess");
			    pupId = createNewProcess(request, wsc, task);
			}

            // если это документы по филиалу, то только сделка и переходим на портал
            ProcessTypeJPA ptype = pupFacadeLocal.getProcessTypeById(task.getId_pup_process_type().longValue());
            if (ptype.isPortalProcess()) {
                pupFacadeLocal.updatePUPAttribute(pupId, "Тип кредитной заявки", "Сделка");
                // переадресация на портал
                response.sendRedirect(FilialTaskList.portalUrl);
                return null;
            }
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
			wsc.setErrorMessage("Ошибка запуска процесса: " + e.getMessage());
			target = "errorPage";
		}
        LOGGER.info("ActionForward target=" + target);
		return new ActionForward(mapping.findForward(target).getPath(), true);
	}

	/**
     * Инициалиация параметров.
     * @param request http-запрос
     */
    private Task init(HttpServletRequest request, Task task) {
        task = task == null ? new Task() : task;

        task.getMain().setDescriptionProcess(request.getParameter("nameTypeProcess"));
        if (request.getParameter("nameTypeProcess")==null || request.getParameter("nameTypeProcess").isEmpty())
            task.getMain().setDescriptionProcess("Pipeline");
        task.getMain().setIssuingBank(request.getParameter("issuingBank"));

        try {
            task.getHeader().setVersion(Long.valueOf(request.getParameter("versionOf")));
        } catch (Exception e) {}

        try {
            task.setId_task(Long.valueOf(request.getParameter("mdTaskId")));
        } catch (Exception e) {}

        try {
            task.setId_pup_process_type(Integer.valueOf(request.getParameter("processTypeId")));
        } catch (Exception e) {}

        try {
            task.getHeader().setStartDepartment(new Department(Integer.valueOf(request.getParameter("Инициирующее подразделение0"))));
        } catch (Exception e) {}

        try {
            if ("Лимит".equalsIgnoreCase(request.getParameter("Тип кредитной заявки0"))) {
                task.getHeader().getPlaces().add(new Department(Integer.valueOf(request.getParameter("Место проведения сделки0"))));
            } else {
                task.getHeader().setPlace(new Department(Integer.valueOf(request.getParameter("Место проведения сделки0"))));
            }
        } catch (Exception e) {}

        try {
            Long subplaceId = Long.valueOf(request.getParameter("subplace"));
            if(subplaceId != null) {
                task.getHeader().setSubplace(subplaceId);
            }
        } catch (Exception e) {}

        try {
            Long inlimitId = Long.valueOf(request.getParameter("inlimitID"));
            if (inlimitId != null && "Сделка".equalsIgnoreCase(request.getParameter("Тип кредитной заявки0"))) {
                task.setParent(inlimitId);
            }
        } catch (Exception e) {}

        if (task.getId_pup_process_type() == null && !StringUtils.isEmpty(task.getMain().getDescriptionProcess())) {
            task.setId_pup_process_type(WPC.getInstance().getIdTypeProcessByDescription(task.getMain().getDescriptionProcess()));
        }

        if (task.getId_pup_process_type() == null)
            throw new RuntimeException("нет такого бизнес-процесса " + task.getMain().getDescriptionProcess());

        task.getSupply().setExist(true);

        return task;
    }


	/**
	 * Создание версии заявки.
	 * @param request http-request
	 * @param wsc контекст
	 * @param task данные по заявке
	 * @throws Exception ошибка
	 */
	private void createVersion(HttpServletRequest request, WorkflowSessionContext wsc, Task task) throws Exception {
	    // проверка возможности назначения нового пользователя
        String oldRoleName = request.getParameter("oldRoleName");
        String strNewUserId = request.getParameter("newUserId");
        Integer idTypeProcess = task.getId_pup_process_type();
        Long newUserId = null;
        String newRoleName = null;
        UserJPA newUser = null;
        if (strNewUserId != null && !strNewUserId.isEmpty() && !strNewUserId.equals("undefined"))
            newUserId = Long.parseLong(strNewUserId);
        boolean isMO = false;
        if (newUserId != null) {
            if (oldRoleName != null && !oldRoleName.isEmpty())
                isMO = oldRoleName.endsWith("(за МО)");
            newUser = pupFacadeLocal.getUser(newUserId);
            if (isMO) {
                if (newUser.hasRole(idTypeProcess.longValue(), "Структуратор (за МО)"))
                    newRoleName = "Структуратор (за МО)";
                if (newUser.hasRole(idTypeProcess.longValue(), "Руководитель структуратора (за МО)"))
                    newRoleName = "Руководитель структуратора (за МО)";
            } else {
                if (newUser.hasRole(idTypeProcess.longValue(), "Структуратор"))
                    newRoleName = "Структуратор";
                if (newUser.hasRole(idTypeProcess.longValue(), "Руководитель структуратора"))
                    newRoleName = "Руководитель структуратора";
            }
            if (newRoleName == null)
                throw new RuntimeException("У пользователя " + newUser.getFullName() + " отсутствует роль структуратора для процесса изменения условий");
        }
        // необходимо создать версию запроса
        Long oldTaskId = task.getHeader().getVersion();
        if (oldTaskId == null)
            throw new RuntimeException("невозможно создать версию сделки id=" + oldTaskId + " : такой сделки не существует");
        Task oldTask = null;
        oldTask = processor.getTask(new Task(oldTaskId));

        // требуется ли отправлять
        UserJPA currUser = null;
        boolean needAssign = true;
        currUser = pupFacadeLocal.getUser(wsc.getIdUser());
        if (currUser.hasRole(idTypeProcess.longValue(), "Руководитель структуратора")) {
            if ((newUserId == null && !(pupFacadeLocal.currentUserAssignedAs("Руководитель структуратора", oldTask.getId_pup_process())
                            || pupFacadeLocal.currentUserAssignedAs("Структуратор", oldTask.getId_pup_process())))
                    || (newUserId != null && !newUserId.equals(currUser.getIdUser())))
                needAssign = false;
        }
        if (currUser.hasRole(idTypeProcess.longValue(), "Руководитель структуратора (за МО)")) {
            if ((newUserId == null && !(pupFacadeLocal.currentUserAssignedAs("Руководитель структуратора (за МО)", oldTask.getId_pup_process())
                            || pupFacadeLocal.currentUserAssignedAs("Структуратор (за МО)", oldTask.getId_pup_process())))
                    || (newUserId != null && !newUserId.equals(currUser.getIdUser())))
                needAssign = false;
        }
        TaskJPA newTaskJPA = TaskVersionHelper.createVersion(oldTask, idTypeProcess, wsc.getIdUser(), request.getParameter("versionReason"),
                needAssign, null, null, taskFacadeLocal, oldRoleName, newRoleName, newUser, processor);
        LOGGER.info("new mdtaskid=" + newTaskJPA.getId());
        if (newTaskJPA.getIdProcess() != null)
            pupFacadeLocal.setStandardPeriodVersion(newTaskJPA.getIdProcess());
        // уведомляем всех, кого требуется
        User from = compenduim.getUser(new User(wsc.getIdUser()));
        notifyFacade.notifyStartEditProcess(newTaskJPA.getId(), from);
        Long creditDealNumber = newTaskJPA.getMdtask_number();
        Long lastCedConfirmedCreditDealId = SBeanLocator.singleton().mdTaskMapper().getLastCedConfirmedCreditDealId(creditDealNumber);
        if (lastCedConfirmedCreditDealId == null)
            throw new Exception("lastCedConfirmedCreditDealId is null");
        LOGGER.info("=======RemoteCreateAction.execute(mapping, form, request, response) oldTaskId '" + oldTaskId + "'. call CpsService.syncMembers(idUser=" + wsc.getIdUser() + ", lastCedConfirmedCreditDealId=" + lastCedConfirmedCreditDealId + ", idNewMdTask=" + newTaskJPA.getId() + ")");
        SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(newTaskJPA.getId(), "Подготовка ПРКК/ПРУЛ");

        ru.masterdm.integration.ServiceFactory.getService(CpsService.class).syncMembers(wsc.getIdUser(), lastCedConfirmedCreditDealId, newTaskJPA.getId());
        // полное перенаправление позволяет избежать ошибки повторного выполнения действия при обновлении страницы
        // удаление первого символа "/" для правильной ссылки
        if (!needAssign)
            target = "performTasks";
	}

	/**
	 * Создание нового процесса.
	 * @param request http-request
	 * @param wsc контекст
	 * @param task заявка
	 * @return идентификатор нового процесса ПУП
	 * @throws Exception ошибка
	 */
	private Long createNewProcess(HttpServletRequest request, WorkflowSessionContext wsc, Task task) throws Exception {
        ArrayList<ContractorType> ct = new ArrayList<ContractorType>();
        ct.add(compenduimSPO.getContractorTypeByID(new Long(1)));
        task.getContractors().add(new TaskContractor(new Organization(request.getParameter("IDCRM_Contractors0")), ct, null));
        if(request.getParameter("kz") != null){
            task.getContractors().clear();
            task.getContractors().add(new TaskContractor(new Organization(request.getParameter("kz")), ct, null));
        }

        task.getMain().setSum(Formatter.parseBigDecimal(request.getParameter("Сумма лимита0")));
        task.getMain().setLimitIssueSum(Formatter.parseBigDecimal(request.getParameter("Сумма лимита0")));
        task.getMain().setCurrency(new Currency(request.getParameter("Валюта0")));
        task.getMain().setPeriod(Formatter.parseInt(request.getParameter("Срок действия лимита0")));
        task.getMain().setPeriodDimension(request.getParameter("periodDimension"));
        // по умолчанию новый лимит всегда имеет занчение renewable = true
        // и mayBeRenewable = true
        task.getMain().setRenewable(true);
        task.getMain().setMayBeRenewable(true);
        task.getMain().setProjectName(request.getParameter("projectName"));

        task.getHeader().getManagers().add(new TaskManager("", new User(wsc.getIdUser().intValue()), null));
        task.getHeader().setNumber(pupFacadeLocal.getNextMdTaskNumber());// генерация номера
        boolean opportunityInLimit = task.getParent() != null;

        // создать процесс ПУП и взять в работу
        Long pupID = pupFacadeLocal.createProcess(task.getId_pup_process_type().longValue(), wsc.getIdUser());
        task.setId_pup_process(pupID);
        task.getHeader().setProcessType(request.getParameter("Тип кредитной заявки0"));
        processor.createTask(task);

        // обновить атрибут "Заявка №" и Статус
        pupFacadeLocal.updatePUPAttribute(pupID, "Заявка №", task.getHeader().getNumber().toString());
        pupFacadeLocal.updatePUPAttribute(pupID, "Тип кредитной заявки", request.getParameter("Тип кредитной заявки0"));
        pupFacadeLocal.updatePUPAttribute(pupID, "Статус", "Начало работы по заявке");
        pupFacadeLocal.setStandardPeriodVersion(pupID);

        assignAndNotify(task, opportunityInLimit, pupID, wsc);

        TaskJPA taskJPA = taskFacadeLocal.getTaskByPupID(pupID);
        taskFacadeLocal.spoContractorSync(taskJPA.getId());
        if(request.getParameter("main_org_changeble")!=null){
            taskJPA.setMainOrgChangeble(request.getParameter("main_org_changeble"));
            //какая сейчас группа у основного заёмщика
            taskJPA.setMainOrgGroup(SBeanLocator.singleton().compendium().getEkGroupId(request.getParameter("IDCRM_Contractors0")));
            taskFacadeLocal.merge(taskJPA);
            taskFacadeLocal.logMainBorrowerChanged(taskJPA.getId(), TaskHelper.getCurrentUser(request).getIdUser(),
                    request.getParameter("kz"), request.getParameter("kz"));
        }
        DepartmentJPA place = new DepartmentJPA();
        place.setIdDepartment(Long.valueOf(request.getParameter("Место проведения сделки0")));
        taskJPA.setPlace(place);
        taskFacadeLocal.merge(taskJPA);
        SBeanLocator.singleton().getDepartmentHistoryMapper().setDepartmentHistory(taskJPA.getId().intValue(),
                Integer.valueOf(request.getParameter("Инициирующее подразделение0")),
                Integer.valueOf(request.getParameter("Инициирующее подразделение0")),
                TaskHelper.getCurrentUser(request).getIdUser().intValue(), new Date());
        SBeanLocator.singleton().getPlaceHistoryMapper().setPlaceHistory(taskJPA.getId().intValue(),
                place.getIdDepartment().intValue(), place.getIdDepartment().intValue(),
                TaskHelper.getCurrentUser(request).getIdUser().intValue(), new Date());

        //После создания заявки в рамках бизнес-процесса «Pipeline», необходимо в «Секции ПМ» менять значения полей:
        //«Стадии сделки» = «Ориджинирование»,
        //«Вероятность закрытия» заполняется автоматически. Для всех типов заявки
        if (taskJPA.getProcessTypeName().equalsIgnoreCase("Pipeline"))
            SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(task.getId_task(), "Ориджинирование");
        if (taskJPA.getProcessTypeName().equalsIgnoreCase("Крупный бизнес ГО") ||
                taskJPA.getProcessTypeName().equalsIgnoreCase("Крупный бизнес ГО (Структуратор за МО)"))
            SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(task.getId_task(), "Подготовка ПРКК/ПРУЛ");

        IDashboardService dashboardService = (IDashboardService) SBeanLocator.singleton().getBean("dashboardService");
        dashboardService.logTask(taskJPA.getId());

        return pupID;
	}


	/**
	 * Обновление процесса с сохранением данных заявки.
	 * @param request http-request
	 * @param wsc контекст
	 * @param task заявка
	 * @return идентификатор нового процесса ПУП
	 * @throws Exception ошибка
	 */
	private Long renewProcess(HttpServletRequest request, WorkflowSessionContext wsc, Task task) throws Exception {
        Task originTask = processor.getTask(task);
        Long originPupID = originTask.getId_pup_process();//конвертировать назначения и документы
        String priority = pupFacadeLocal.getPUPAttributeValue(originTask.getId_pup_process(), "Приоритет");

	    String idOrg = request.getParameter("IDCRM_Contractors0");
	    String idClientRecordOrg = request.getParameter("kz");
	    if(!StringUtils.isEmpty(idClientRecordOrg)) {
            idOrg = idClientRecordOrg;
        }
	    ArrayList<ContractorType> contractorTypes = new ArrayList<ContractorType>();
	    contractorTypes.add(compenduimSPO.getContractorTypeByID(new Long(1)));

	    TaskContractor taskContractor = new TaskContractor(new Organization(idOrg), contractorTypes, null);
	    taskContractor.setMainBorrower(true);
	    for (TaskContractor contractor : originTask.getContractors()) {
	        if (contractor.isMainBorrower()) {
	            contractor.setMainBorrower(false);
	            break;
	        }
	    }
        if(originTask.getContractors().size()>0
                && SBeanLocator.singleton().mdTaskMapper().getById(originTask.getId_task()).getProjectName() == null)
            originTask.getContractors().remove(0);
	    originTask.getContractors().add(0, taskContractor);

	    boolean opportunityInLimit = task.getParent() != null;

        // создать процесс ПУП и взять в работу
        Long pupID = pupFacadeLocal.createProcess(task.getId_pup_process_type().longValue(), wsc.getIdUser());
        task = init(request, originTask);
        task.setId_pup_process(pupID);
        processor.renewTask(task);

        pupFacadeLocal.updatePUPAttribute(pupID, "Статус", "Начало работы по заявке");
        pupFacadeLocal.updatePUPAttribute(pupID, "Приоритет", priority);
        pupFacadeLocal.updatePUPAttribute(pupID, "Тип кредитной заявки", request.getParameter("Тип кредитной заявки0"));
        pupFacadeLocal.setStandardPeriodVersion(pupID);

        assignAndNotify(task, opportunityInLimit, pupID, wsc);

        TaskJPA taskJPA = taskFacadeLocal.getTaskByPupID(pupID);
        taskFacadeLocal.spoContractorSync(taskJPA.getId());
        if(request.getParameter("main_org_changeble")!=null){
            taskJPA.setMainOrgChangeble(request.getParameter("main_org_changeble"));
            //какая сейчас группа у основного заёмщика
            taskJPA.setMainOrgGroup(SBeanLocator.singleton().compendium().getEkGroupId(request.getParameter("IDCRM_Contractors0")));
            taskFacadeLocal.merge(taskJPA);
        }
        SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(task.getId_task(), "Подготовка ПРКК/ПРУЛ");

        pupFacadeLocal.convertPMandDoc(originPupID,pupID);
        return pupID;
	}

	/**
	 * Назначение исполнителя и уведомление руководителей.
     * @param task заявка
     * @param opportunityInLimit признак заявки в рамках лимита
     * @param pupId идентификатор процесса ПУП
     * @param wsc контекст
     * @throws Exception
     */
    private void assignAndNotify(Task task, boolean opportunityInLimit, Long pupId, WorkflowSessionContext wsc) throws Exception {
        String nameTypeProcess = task.getMain().getDescriptionProcess();
        Integer idTypeProcess = task.getId_pup_process_type();
        if (nameTypeProcess.contains("илот") || nameTypeProcess.equals("Крупный бизнес ГО") || nameTypeProcess.equals("Крупный бизнес ГО (Структуратор за МО)")
                || nameTypeProcess.equalsIgnoreCase("pipeline")) {
            // назначаем текущего пользователя как структуратора на эту заявку
            TaskJPA taskJPA = taskFacadeLocal.getTaskByPupID(pupId);
            UserJPA user = pupFacadeLocal.getUser(wsc.getIdUser());
            if (user.hasRole(idTypeProcess.longValue(), "Структуратор (за МО)"))
                setAsRole(pupId, taskJPA, user,
                        "Структуратор (за МО)", wsc, task);
            else {
                if (user.hasRole(idTypeProcess.longValue(), "Руководитель структуратора (за МО)"))
                    setAsRole(pupId, taskJPA, user,
                            "Руководитель структуратора (за МО)", wsc, task);
                else {
                    if (user.hasRole(idTypeProcess.longValue(), "Структуратор"))
                        setAsRole(pupId, taskJPA, user, "Структуратор", wsc, task);
                    else if (user.hasRole(idTypeProcess.longValue(), "Руководитель структуратора"))
                        setAsRole(pupId, taskJPA, user,
                                "Руководитель структуратора", wsc, task);
                }
            }
            // добавим его в проектную команду
            //проверить на наличие уже в ПК
            boolean userAlredyInProjectTeam = false;
            for(ProjectTeamJPA t : taskJPA.getProjectTeam())
                if(t.getUser().equals(user))
                    userAlredyInProjectTeam = true;
            if(!userAlredyInProjectTeam) {
                ProjectTeamJPA pt = new ProjectTeamJPA();
                pt.setTask(taskJPA);
                pt.setUser(user);
                taskJPA.getProjectTeam().add(pt);
                taskFacadeLocal.merge(pt);
            }
            // вызываем сервис
            try {
                ru.masterdm.integration.ServiceFactory.getService(CpsService.class).addMember(wsc.getIdUser(), taskJPA.getId(), "p", wsc.getIdUser());
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            // уведомляем всех руководителей мидл-офиса
            RoleJPA role = pupFacadeLocal.getRole("Руководитель мидл-офиса", idTypeProcess.longValue());
            if (role != null) {
                for (UserJPA rukMO : role.getUsers()) {
                    String url = pupFacadeLocal.getBaseURL(user.getIdUser()) + "/showTaskList.do?typeList=all&searchNumber=" + taskJPA.getMdtask_number();
                    String subject = "Создан(а) " +notifyFacade.getName(taskJPA.getId()) + " по процессу " + taskJPA.getProcess().getProcessType().getDescriptionProcess() + " в СПО";
                    String body = "Создан(а) " +taskJPA.getType() +notifyFacade.getAllContractors(task.getId_task())+ " № <a href='" + url + "'>" + taskJPA.getNumberAndVersion() + "</a> по процессу " + taskJPA.getProcess().getProcessType().getDescriptionProcess()
                            + " в СПО. Вы являетесь " + "Руководителем мидл-офиса в этом бизнес-процессе и можете назначить работника мидл-офиса."
                            + notifyFacade.getDescriptionTask(taskJPA.getId());
                    notifyFacade.send(user.getIdUser(), rukMO.getIdUser(), subject, body);
                    LOGGER.info("отправлено уведомление на " + rukMO.getMailUser());
                }
            }
            // уведомляем секретарей о начале нового этапа
            User from = compenduim.getUser(new User(wsc.getIdUser()));
            for (TaskInfoJPA ti : taskJPA.getProcess().getTasks())
                notifyFacade.notifySecretaryNewStage(taskJPA.getIdProcess(), from, ti.getStage().getIdStage(), null, ti.getIdDepartament());

            if (opportunityInLimit)
                copyParametersFromParent(taskJPA, task);
        }
    }

	private void setAsRole(Long pupID, TaskJPA taskJPA, UserJPA user, String roleName, WorkflowSessionContext wsc, Task task) {
	    Integer idTypeProcess = task.getId_pup_process_type();
		try {
			pupFacadeLocal.assign(user.getIdUser(), pupFacadeLocal.getRole(roleName,
					idTypeProcess.longValue()).getIdRole(), pupID, user.getIdUser(), true);
			ru.masterdm.integration.ServiceFactory.getService(CpsService.class).executorSetting(wsc.getIdUser(), taskJPA.getId(),
					wsc.getIdUser(), roleName);
		}
		catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}

	/**
	 * Копируем значения из родительского лимита в сделку
	 * @param task -- сделка, в параметры которой вносим значения родительского (суб)лимита
	 * @param taskJPA -- эта же сделка, но в виде JPA-сущности
	 */
	private void copyParametersFromParent(TaskJPA taskJPA, Task task) {
        String project_name = task.getMain().getProjectName();
		try {
			// перечитаем еще раз Task (чтобы были все поля)
			task = processor.getTask(new Task(task.getId_task()));

			// здесь происходит копирование в task из родительского лимита\сублимита
			taskFacadeLocal.findParentHash(taskJPA, task);

			processor.saveCurrencyList(task);
			processor.saveTarget(task);
			processor.saveSpecialOtherConditions(task);
			processor.saveProductTypes(task);

			// и еще раз сохраним основные параметры (на самом деле, проектное финансирование)
            task.getMain().setProjectName(project_name);
			processor.saveParameters(task);

		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
