<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.Arrays"%>
<%@page import="ru.md.pup.dbobjects.ProcessTypeJPA"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="ru.md.pup.dbobjects.AssignJPA"%>
<%@page import="ru.md.pup.dbobjects.RoleJPA"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="ru.md.pup.dbobjects.UserJPA"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal"%>
<%@page import="ru.md.spo.dbobjects.ProjectTeamJPA"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.vtb.util.EjbLocator"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<html:html>
<head>
<title>Процессы</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body class="popup">
<%
	response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");
	
	Logger LOGGER = Logger.getLogger(this.getClass().getName());
	TaskFacadeLocal taskFacade = EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	if (taskFacade == null)
		LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект TaskFacade");
	PupFacadeLocal pupFacadeLocal = EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	if (pupFacadeLocal == null)
		LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект PupFacade");
	String strId = request.getParameter("idTask");
	Long taskId = null;
	List<String> processes = new ArrayList<String>();
	if (strId != null && !strId.isEmpty())
		taskId = Long.parseLong(strId);
	String procName = null;
	TaskJPA task = null;
	if (taskId != null) {
		task = taskFacade.getTask(taskId);
		if (task == null)
			LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект сделки id=" + taskId);
		TaskActionProcessor taskProc = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		if (taskProc == null)
			LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект TaskActionProcessor");
		TaskJPA firstVersion = taskFacade.getFirstVersion(task.getMdtask_number().toString());
		if (firstVersion == null)
			LOGGER.log(Level.SEVERE, "Не удалось инициализировать первую версию по заявке " + task.getMdtask_number().toString());
		else
			LOGGER.log(Level.INFO, "Получена первая версия заявки с id_mdtask = " + firstVersion.getId()
					+ ", mdtask_number = " + firstVersion.getNumberDisplay());
		Long firstVersionIdTypeProcess = firstVersion.getIdTypeProcess();
		if (firstVersionIdTypeProcess == null)
			LOGGER.log(Level.SEVERE, "Не удалось получить тип процесса по id_mdtask = " + firstVersion.getId());
		ProcessTypeJPA firstVersionType = pupFacadeLocal.getProcessTypeById(firstVersionIdTypeProcess);
		if (firstVersionType == null)
			LOGGER.log(Level.SEVERE, "Не удалось получить тип процесса по id_type_process = " + firstVersionIdTypeProcess);
		procName = firstVersionType.getDescriptionProcess();
		LOGGER.log(Level.INFO, "Тип процесса по первой заявке '" + procName + "'");
	}
		if ("Крупный бизнес ГО (Структуратор за МО)".equalsIgnoreCase(procName))
			processes.add("Изменение условий Крупный бизнес ГО (Структуратор за МО)");
		else if ("СПО_пилот".equalsIgnoreCase(procName) || "Крупный бизнес ГО".equalsIgnoreCase(procName))
			processes.add("Изменение условий Крупный бизнес ГО");
	processes.add("Изменение условий");
			%>
	<script language="javascript">
		var went = false;
		function goToCreateVersion() {
			if (went)
				return false;
			var str = '';
			if (($.inArray($("#processTypeId").val(), allowed) != -1) 
					&& ($("#newUserId") !== undefined))
				str = '&newUserId=' + $("#newUserId").val();
			went = true;
			location.href = 'remote.create.process.do?versionOf=' + <%=taskId.toString()%>
					+ '&processTypeId=' + $("#processTypeId").val() + '&versionReason='
					+ $("#versionReason").val() + '&oldRoleName=' + $("#oldRoleName").val() + str;
		}
	</script>
	<div>
		<p>Выбрать процесс для изменения условий:</p>
		<p><select name="processTypeId" id="processTypeId">
			<%
					if (WPC.getInstance() == null)
						LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект WPC");
					List<Integer> procIds = new ArrayList<Integer>();
					for (String proc : processes) {
						Integer procId = WPC.getInstance().getIdTypeProcessByDescription(proc);
						procIds.add(procId);
			%>
			<option value="<%=procId%>"><%=proc%></option>
			<%
				}
			%>
		</select></p>
		<br/>
			<%
				WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
				if (wsc == null)
					LOGGER.log(Level.SEVERE, "Не удалось инициализировать объект WorkflowSessionContext");
				UserJPA currUser = pupFacadeLocal.getUser(wsc.getIdUser());
				if (currUser == null)
					LOGGER.log(Level.SEVERE, "Не удалось инициализировать текущего пользователя");
				String mode = "struct" + (procName.endsWith("(Структуратор за МО)") ? "MO" : "");
				String oldRoleName = null;
				String userFullName = null;
				String allowedProcIds = "";
				for (Integer pId : procIds) {
					if ((currUser.hasRole(task.getIdTypeProcess(), "Руководитель структуратора (за МО)") ||
							currUser.hasRole(task.getIdTypeProcess(), "Руководитель структуратора"))
							&& (currUser.hasRole(pId.longValue(), "Руководитель структуратора (за МО)") ||
							currUser.hasRole(pId.longValue(), "Руководитель структуратора")))
						allowedProcIds += "\"" + pId + "\",";
				}
				allowedProcIds += "\"\"";
				for (ProjectTeamJPA team : task.getProjectTeam()) {
					if (team == null || team.getUser() == null)
						LOGGER.log(Level.SEVERE, "Не удалось получить пользователя в проектной команде");
					if (mode.equals("struct") && procIds.size() > 0) {
					// возможность переназначить ответственного пользователя с ролью 
					// "Структуратор" или "Руководитель структуратора"
						if (pupFacadeLocal.userAssignedAs(team.getUser().getIdUser(), "Структуратор", 
								task.getIdProcess())) {
							oldRoleName = "Структуратор";
							userFullName = team.getUser().getFullName();
						}
						else if (pupFacadeLocal.userAssignedAs(team.getUser().getIdUser(), "Руководитель структуратора",
								task.getIdProcess())) {
							oldRoleName = "Руководитель структуратора";
							userFullName = team.getUser().getFullName();
						}
					}
					if (mode.equals("structMO") && procIds.size() > 0) {
					// возможность переназначить ответственного пользователя с ролью "Структуратор (за МО)"
					// или "Руководитель структуратора (за МО)"
						if (pupFacadeLocal.userAssignedAs(team.getUser().getIdUser(), "Структуратор (за МО)", 
								task.getIdProcess())) {
							oldRoleName = "Структуратор (за МО)";
							userFullName = team.getUser().getFullName();
						}
						if (pupFacadeLocal.userAssignedAs(team.getUser().getIdUser(),
								"Руководитель структуратора (за МО)", task.getIdProcess())) {
							oldRoleName = "Руководитель структуратора (за МО)";
							userFullName = team.getUser().getFullName();
						}
					}
				}
				if (oldRoleName != null) {
			%>
				<p>
					Текущий <%=oldRoleName%><br/>
					<input type="text" value="<%=userFullName%>" readonly/>
				</p><br/>
			<%
				}
%>
			<script language="javascript">
				var allowed = [<%=allowedProcIds %>];
				onProcessChange();
				function selectNewStructurator() {
					window.open('popup_users.jsp?formName=editConditions&reportmode=true&fieldNames=newUserId|newUserFIO'
						+ '&processTypeId=' + $("#processTypeId").val() + '&section=' + $("#mode").val(), 'org',
						'top=100, left=100, width=800, height=710');
					return false;//don't submit
				}
				function onProcessChange() {
					if ($.inArray($("#processTypeId").val(), allowed) != -1){
						$("#editConditionsContent").show();
					}
					else {
						$("#editConditionsContent").hide();
					}
				}
				$("#processTypeId").on("change", function() {onProcessChange(); });
				
				
			</script>

		<form id="editConditions" name="editConditions">
			<div id="editConditionsContent" name="editConditionsContent">
<%
				if (procIds.size() > 0) {
			%>
			<input type="hidden" name="newUserId" id="newUserId" value=""/>
			<p>
				Новый исполнитель<br/>
				<input type="text" name="newUserFIO" id="newUserFIO" value="" readonly/><br/>
				<button onclick="return selectNewStructurator()">Выбрать нового исполнителя</button>
			</p><br/>
			<%
				}
			%>
			</div>
		</form>
		<input type="hidden" name="mdtaskid" id="mdtaskid" value="<%=strId %>"/>
		<input type="hidden" name="oldRoleName" id="oldRoleName" value="<%=(oldRoleName == null ? "" : oldRoleName) %>"/>
		<input type="hidden" name="mode" id="mode" value="<%=(procIds.size() > 0 ? mode : "") %>"/>
		<p>
			Причина изменения параметров:<br />
			<textarea rows="5" id="versionReason" name="versionReason"></textarea>
			<br/>
			<button type="button" onclick="goToCreateVersion();">Создать</button>
			<button type="button" onclick="$.fancybox.close();">Отмена</button>
			<br/>
		</p>
		<br/>
	</div>
</body>
</html:html>
