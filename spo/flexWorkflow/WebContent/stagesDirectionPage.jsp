<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="ru.md.helper.TaskHelper"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="ru.md.pup.dbobjects.TaskInfoJPA"%>
<%@ page import="org.uit.director.action.AbstractAction"%>
<%@ page import="org.uit.director.contexts.WPC"%>
<%@ page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@ page import="org.uit.director.db.ejb.DBFlexWorkflowCommon"%>
<%@ page import="org.uit.director.db.dbobjects.Attribute"%>
<%@ page import="org.uit.director.db.dbobjects.WorkflowTypeProcess"%>
<%@ page import="org.uit.director.db.dbobjects.WorkflowStages"%>
<%@ page import="org.uit.director.managers.StagesDirectionManager"%>
<%@ page import="org.uit.director.tasks.TaskInfo"%>
<%@ page import="org.uit.director.tasks.TaskList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Map.*"%>
<%@ page import="org.uit.director.db.dbobjects.Cnst"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page pageEncoding="Cp1251"%>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>

<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<%@page import="com.vtb.domain.TaskListType"%><html:html xhtml="true">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Управление этапами</title>
	<style type="text/css">@import url( "resources/stylesheet.css" );</style>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<script language="JavaScript" src="scripts/applicationScripts.js"></script>  
<script>
	function submitUserForm() {
		usersform.submit();
	}

	function typeProcessChanged() {
		var user = document.getElementById("user");   
		user.value = "-";
		submitUserForm();
	}

	function redirectTask(idTypeProcess, idTask, idx, idUser, idCurrentStage, idCurrentDepartment) {
		var stageName = "stages" + idx;
		var name;
		var dep = "departament" + idx;;
		for (var i = 0; i < mainForm.elements.length; i++) {
			name = mainForm.elements[i].name;
			if (name == stageName) {
				var idStage = mainForm.elements[i].value;
			}
			if (name == dep) {
			 dep = mainForm.elements[i].value;
			 break;
			}
		}
		window.location = "task.redirect.do?idTypeProcess=" + idTypeProcess + "&idTask=" + idTask + "&idStage=" + idStage + "&departament=" + dep + "&idUser=" + idUser + "&idCurrentStage=" + idCurrentStage + "&currentDepartament=" + idCurrentDepartment;
	}
</script>
<jsp:include page="header_and_menu.jsp" />
				<h1>Управление этапами</h1>
				<%WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
				Logger LOGGER = LoggerFactory.getLogger("stagesDirectionPage.jsp");
				if (wsc.isNewContext()) {
					%>
					<script> document.location = "start.do"; </script>
					<%return;
				}
				final WPC wpc = WPC.getInstance();
				Map users = WPC.getInstance().getUsersMgr().getWorkflowUsers().getWfUserMap();
				if (users != null) {
					Long idUserWork = (Long) request.getAttribute("iduser");
					%>
<form action="direction.stages.do" method="POST" name="usersform">
<div>
<table>
	<tr>
		<td>Тип процесса</td>
		<td><select name="typeProc" id="typeProc" onchange="typeProcessChanged();">
							<option>-</option>
			<%  
				// инициализация. Что у нас в реквесте на предыдущих вызовах?
				String typeProc = request.getParameter("typeProc");
				if ((typeProc != null ) && (typeProc.equals("-"))) typeProc = null; 
				boolean isUserAdmin = false;
				String department = request.getParameter("department");
				if ((department != null ) && (department.equals("-"))) department = null;
				String userName= request.getParameter("user");
				if ((userName != null ) && (userName.equals("-"))) userName = null;
				
				DBFlexWorkflowCommon director = wsc.getDbManager().getDbFlexDirector();

				long idTypPrL = -1;
				int idDep = -1;
				int userId = -1;
				int idx = 1;
				if (typeProc != null) {
					idTypPrL = Long.parseLong(typeProc);
					isUserAdmin = wsc.isUserAdmin(Integer.parseInt(typeProc));
				}
				if (department != null) {
					idDep = Integer.parseInt(department);
				}
				if (userName != null) {
					userId = Integer.parseInt(userName);
				}
				// выведем список типов процессов
				int selectedIndex = 0;
				List typeProcList = WPC.getInstance().getTypeProcessesList(wsc.getIdUser());
				for (int i = 0; i < typeProcList.size(); i++) {
					WorkflowTypeProcess typeProcess = (WorkflowTypeProcess) typeProcList.get(i);
					long idTP = typeProcess.getIdTypeProcess();
					if (idTP == idTypPrL) {
						selectedIndex = idx;
			%>
			<option selected value="<%=idTP%>"><%=typeProcess.getNameTypeProcess()%></option>
			<%
					}
					else 
					{
			%>
			<option value="<%=idTP%>"><%=typeProcess.getNameTypeProcess()%></option>
			<%	  }
					idx++;
				}
			%>
		</select></td>
	</tr>
	<tr>
		<td>Подразделение</td>
		<td><select name="department" onchange="submitUserForm();">
							<option>-</option>
			<%				
				   // выведем список всех подразделений
					    try{
					int selectedIndexDep = 0;
					Map<String, String> departments = WPC.getInstance().getDepartmentsHierarchy();
					Iterator<Entry<String, String>> itDep = departments.entrySet().iterator();
					while (itDep.hasNext()) {
							Entry<String, String> entry = itDep.next(); 
							long depId = Long.valueOf(entry.getKey()).longValue();
							// для вывода selected!
							if (depId == idDep) {
								selectedIndexDep = idx;
								%>
								<option selected value="<%=depId%>"><%=entry.getValue()%></option>
								<%
							}
							else {
								%>
								<option value="<%=depId%>"><%=entry.getValue()%></option>
								<%
							}
							idx++;
					}
						}catch (Exception e){}
					%>
		</select></td>
	</tr>
	<tr>
		<td>Пользователь</td>
		<td>
			<select name="user" id="user">
				<option>-</option>
		<% 
				// все пользователи департамента, имеющие доступ к процессу (имеющие роли на процесс)
				Map allUsers = wpc.getUsersInDepartment();
								
				List depUsers = new ArrayList();
				List filterdDepUsers = new ArrayList();
				// список пользователей выбранного подразделения				
				if (department!= null)  {
					Integer id = new Integer (idDep);
					if (allUsers.containsKey(id))
					  depUsers.addAll((List) allUsers.get(id));
				}
								
				// Получим список ВСЕХ ролей процесса
				HashSet procRolesSet = new HashSet();
				Map allRolesInProcesses = wpc.getRolesInTypeProcess();
				if ((typeProc != null) && (allRolesInProcesses.containsKey( (Object) new Integer ((int)idTypPrL)))) {   
					procRolesSet.addAll( (List) allRolesInProcesses.get( (Object) new Integer ((int)idTypPrL)));
				}
				// для каждого пользователя поймем, есть ли для него роли в выбранном процессе.
				Iterator it = depUsers.iterator();
				while (it.hasNext()){
					Long idUser = (Long)it.next();
					// Список ролей пользователя
					List userRoles = wpc.getIDRolesForUser(idUser);
					// есть ли у пользователя роли в процессе? 
					boolean found = false;
					Iterator itRoles = userRoles.iterator();
					while (itRoles.hasNext())
					{
						Long role = (Long) itRoles.next();
						if (procRolesSet.contains( (Object)role))
						{ 
							found = true;  break;
						}
					}
					 
					// если роли есть, то включаем пользователя в список доступных на просмотр 
					if (found) 
					{
						filterdDepUsers.add(idUser); 
					}
				}
				Collections.sort(filterdDepUsers, new Comparator()
					{
						public int compare(Object o1, Object o2) {
							String object1 = wpc.getUsersMgr().getFullNameWorkflowUser((Long)o1);
							String object2 = wpc.getUsersMgr().getFullNameWorkflowUser((Long)o2);
							return object1.compareToIgnoreCase(object2);
						}
					}
				);
				it = filterdDepUsers.iterator();
				while (it.hasNext()){
					Long idUser = (Long)it.next();
					if (idUser.longValue() == userId) {
						%>
							<option selected value="<%=idUser%>"><%=wpc.getUsersMgr().getFullNameWorkflowUser(idUser)%></option>
						<%
					}
					else
					{
						%>
							<option value="<%=idUser%>"><%=wpc.getUsersMgr().getFullNameWorkflowUser(idUser)%></option>
						<%
					}
				}
			%>
			</select>
		</td>
		</tr>
					
		<tr>
        <td>номер заявки</td>
        <td><input type="text" name="searchNumber" 
        value="<%=request.getParameter("searchNumber")==null?"":request.getParameter("searchNumber") %>"></td>
        </tr>
        <tr><td rowspan="2"><td colspan="2" style="text-align:left;">    <button type="submit">Найти</button></td></tr>
</table>
</div>
<%StagesDirectionManager directionManager = wsc.getStagesDirectionManager();
					if (directionManager != null && idUserWork != null) {
						%></form>
<br>
<form name="mainForm">
<div class="tabledata">
<table width="100%" class="regular">
	<tr>
		<th></th>
							<th>Задание</th>  <!-- Основная таблица. Колонка 1. Заголовок -->
							<th>Заявка №</th> <!-- Основная таблица. Колонка 2. Заголовок --> 							
							<th>Этап</th> <!-- Основная таблица. Колонка 3. Заголовок -->
							<!-- <th>Атрибуты</th> -->
							<th>Принять/<br>отказаться</th>   <!-- Основная таблица. Колонка 4. Заголовок -->
							<th>Отправить на этап /в филиал</th>  <!-- Основная таблица. Колонка 5. Заголовок -->
	</tr>
	<%idx = 1;
						String chosenTypeProc = null; String chosenUser = null; Long chosenUserId = null; 
						chosenTypeProc = request.getParameter("typeProc");
						if ((chosenTypeProc != null ) && (chosenTypeProc.equals("-"))) chosenTypeProc = null; 
						chosenUser = request.getParameter("user");
						
						if ((chosenUser != null ) && (chosenUser.equals("-"))) chosenUser = null;
						if ( chosenUser != null) { 
							chosenUserId = new Long(chosenUser);
						}   
						TaskList taskList;
						String isChecked = "Принять";
						String isInWork = "Ожидает обработки";
						Iterator<com.vtb.domain.TaskListType> iteratorTypeList = directionManager.getList().keySet().iterator();
						 while (iteratorTypeList.hasNext()) {
						    com.vtb.domain.TaskListType type = iteratorTypeList.next();
	LOGGER.info("table type "+type.name());
						    taskList = directionManager.getList().get(type);
                            if(type.equals(com.vtb.domain.TaskListType.ACCEPT)) {
                                isChecked = "Отказаться";
                                isInWork = "В работе";
                            }
                            if(type.equals(com.vtb.domain.TaskListType.ASSIGN)) {
                                isInWork = "Назначена";
                            }
							int count = taskList.size();
							int idTypeProcess = 0;List stages = null; ArrayList keys = null;
							Long idStage; String nameStage;
							HashMap hashOperations = new HashMap();
							boolean operationListGenerated = false;
							HashSet subordinateDeps = new HashSet();
							ArrayList sortedDeps = null;
							
							for (int i = 0; i < count; i++) {
								TaskInfo taskInf = taskList.getTaskIdx(i);
								TaskInfoJPA taskInfo = TaskHelper.pup().getTask(taskInf.getIdTask());
								LOGGER.info("2 table indx "+i);
								if (taskInf==null) continue;
								idTypeProcess = taskInfo.getProcessType().getIdTypeProcess().intValue();
								LOGGER.info("3 table indx "+i);
								// Проверим, что данные того самого типа процесса, а не всех процессов подряд
								if ((chosenTypeProc !=null) && (!String.valueOf(idTypeProcess).equals(chosenTypeProc))) continue;
								
								List attr = taskInf.getAttributes().getAttributesOrder();
								String aatr = "";
								LOGGER.info("4 table indx "+i);
								for (int j = 0; j < attr.size(); j++) {
									if (attr.get(j) instanceof AttributeStruct) {
									try{
										Attribute attrEl = (Attribute) ((AttributeStruct)attr.get(j)).getAttribute();
										aatr += attrEl.getValueAttributeString() + "<br>";
									}catch(Exception e){}}
								}
								LOGGER.info("5 table indx "+i);
								%>
	<tr>
		<td><%=idx++%></td> 					<!-- Основная таблица. Колонка 0. Нумерация  -->
		<td align="center"><%=isInWork%></td><!-- Основная таблица. Колонка 1. Статус Задания  -->
		<% String mdTaskNumber = TaskHelper.taskFacade().getTaskByPupID(taskInfo.getProcess().getId()).getNumberDisplay();
           String CRMClaimName = null; 
           try {
               CRMClaimName = director.findCRMClaimName(mdTaskNumber);
               if ((CRMClaimName != null) && (!CRMClaimName.equals(mdTaskNumber)))  mdTaskNumber = CRMClaimName + " ("+ mdTaskNumber + ")";
           } catch (Exception e) {
               CRMClaimName = null;
           }
		%>
		<td align="center"><%=mdTaskNumber%></td>  <!-- Основная таблица. Колонка 2. Номер заявки -->
		<td><%=taskInfo.getStage().getDescription()%></td>  <!-- Основная таблица. Колонка 3. Название этапа --> 
		<!--  <td><%//=aatr%></td>  -->
		<td><!-- Основная таблица. Колонка 4. Принять\Отказаться -->
		<a href="task.accept.do?isAccept=<%=type.equals(TaskListType.ACCEPT)?"1":"0"%>&id0=<%=taskInf.getIdTask()%>&idUser=<%=idUserWork%>&typeProc=<%=typeProc%>"><%=isChecked%></a>
		</td>
		<td><!-- Этап --><!-- Основная таблица. Колонка 5. Отправить на этап\филиал -->
				<select name="stages<%=idx%>"><!-- Список 1. Список операций -->
					<option></option>
					<%
						// т.к. список операций -- один и тот же для всех заявок (т.к. только заявки одного типа процесса показываются)
						// то вынесем формирование списка этапов на первую операцию (i==0)
						LOGGER.info("6 table indx "+i);
						if (!operationListGenerated) {
							idTypeProcess = taskInfo.getProcessType().getIdTypeProcess().intValue();
							stages = (List) WPC.getInstance().getStagesInTypeProcess().get(new Integer(idTypeProcess));
							// сформируем и отсортируем список операций по алфавиту
							for(int j = 0; j < stages.size(); j++) {
								idStage = (Long)stages.get(j);
								nameStage = (String)WPC.getInstance().getData(Cnst.TBLS.stages, idStage.longValue(), Cnst.TStages.name);
								WorkflowStages wfStage = wpc.findStage(idStage);
								// добавим в список только АКТИВНЫЕ этапы (те, что используются в данной версии загруженного описания процесса)
								if (wfStage.isActive()) {
									hashOperations.put(nameStage, idStage);
								}
							}
							keys = new ArrayList();
							keys.addAll(hashOperations.keySet());
							
							// отсортируем ЯВНО, не доверяя стандартным вызовам Java
							Collections.sort(keys, new Comparator() {
							   public int compare(Object object1, Object object2) {
									return ((String) object1).compareTo( (String) object2);
							   }
							});
														
							// Получим список департаментов, подчиненных департаменту пользователя.												
							if (isUserAdmin == true) {
								// Пусть администратор смотрит ВСЕ подразделения
								List deps = (List)WPC.getInstance().getDepartments();
								sortedDeps = new ArrayList();
								Iterator li = deps.listIterator();
								while (li.hasNext()) { sortedDeps.add( ((WorkflowDepartament)li.next()).getId()); }
							}
							else {
								// А обычный пользователь -- лишь свое и подчиненные ему
								Long userDep = WPC.getInstance().getUsersMgr().getInfoUserByIdUser(wsc.getIdUser()).getDepartament().getIdDepartament();
								WPC.getInstance().getChildrenOfDeparment(userDep, subordinateDeps);
								sortedDeps = new ArrayList (subordinateDeps);
								sortedDeps.add(userDep);
							} 
							operationListGenerated = true;
						}
						LOGGER.info("7 table indx "+i);
						Iterator sortedOperationsIterator = keys.iterator();
						while (sortedOperationsIterator.hasNext()) {
						  nameStage = (String)sortedOperationsIterator.next();
						  idStage = (Long)hashOperations.get(nameStage);
						  %><option value=<%=idStage%>><%=nameStage%> </option> <%
						}
						LOGGER.info("8 table indx "+i);
					 %>
				</select>
				<select name ="departament<%=idx%>"> <!-- Список 2. Список подразделений  -->
					<option value="-1">Без изменения</option>
					<%					
						Map<String, String> depsHierarchy = WPC.getInstance().getDepartmentsHierarchy();
						Iterator<Entry<String, String>> it2 = depsHierarchy.entrySet().iterator();
						while(it2.hasNext()) {
							try{
							 Entry<String, String> curDep = it2.next();
							 if (sortedDeps.contains(Long.parseLong(curDep.getKey()))) {
							 %>
							 <option value="<%=curDep.getKey()%>"><%=curDep.getValue()%></option>">
							 <%
							 }
						}catch(Exception e){} }
						LOGGER.info("9 table indx "+i);
					%>
				</select>
				<a href="javascript:redirectTask(<%=idTypeProcess%>,<%=taskInf.getIdTask()%>, <%=idx%>, '<%=idUserWork%>', <%=taskInfo.getStage().getIdStage()%>, <%=taskInfo.getIdDepartament()%> );">
				Отправить</a>
		</td>
	</tr>
	<%	}
	}
%>
</table>
</div>
<%}%></form>
<%}%>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html:html>
