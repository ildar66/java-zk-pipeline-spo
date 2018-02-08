<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.managers.ControlActionsManager"%>
<html:html xhtml="true">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Управление пользователями</title>
	<link rel="stylesheet" href="style/style.css" />
	<script type="text/javascript">
		function showDiv() {
			document.all('splash').style.display='none'
			document.all('all').style.height='200%'
		}
	</script>
</head>

<body class="soria">
	<jsp:include page="header_and_menu.jsp" />
					<h1>Управление бизнес-процессами</h1>
					<h2>Контроль операций</h2>
							<script language="javascript" src="resources/cal2.js"></script>
							<script language="javascript" src="resources/cal_conf2.js"></script>
							<script language="javascript" src="resources/sort.js"></script>
							<script language="javascript" src="scripts/applicationScripts.js"></script>
							<%	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
								ControlActionsManager cam = wsc.getControlActionsManager();
								ControlActionsManager.ControlActionsForm form = cam.getForm();
							%>
							<form action="control.action.do" method="post" name="mainform">
							<div class="tabledata">
								<table>
									<caption>Поиск операций</caption>
									<tr>
										<td>Период</td>
										<td>
											<input type="text" name="leftDate" size="10" value="<%=form.getDateLeft() %>">
											<a href="javascript:showCal('Calendar1')"><img src="resources/calendar.jpg" alt="Выбрать дату"></a>
											<input type="text" name="rightDate" size="10" value="<%=form.getDateRight() %>">
											<a href="javascript:showCal('Calendar2')"><img src="resources/calendar.jpg" alt="Выбрать дату"></a>
										</td>
									</tr>
									<tr>
										<td>Пользователь</td>
										<td>
											<input type="text" name="idUser" value="<%=form.getIdUser() %>">
										</td>
									</tr>
									<tr>
										<td>IP адресс</td>
										<td>
											<input type="text" name="ipAddress" value="<%=form.getIpAddress() %>">
										</td>
									</tr>
									<tr>
										<td>Вид действия</td>
										<td>
											<select name="typeAction" >
											<option>-</option>
											<option value="AcceptWork">Взять\Вернуть задание</option>
											<option value="RedirectWork">Передать задание на этап</option>
											<option value="UpdateAttributes">Обновить арибуты</option>
											<option value="DeleteProcessMark">Пометить процесс удаленным</option>
											<option value="DeleteProcessFinaly">Удалить процесс</option>
											<option value="AddWorkflowUserRole">Добавить пользователя с ролью</option>
											<option value="AddWorkflowRole">Добавить роль пользователя</option>
											<option value="DeleteWorkflowRole">Удалить роль пользователя</option>
											<option value="DeleteWorkflowUser">Удалить пользователя</option>
											<option value="AddUserNotify">Добавить данные уведомления пользователя</option>
											<option value="UpdateUserNotify">Обновить данные уведомления пользователя</option>
											<option value="DeleteUserNotify">Удалить данные уведомления пользователя</option>
											<option value="RedirectWorksBetweenUsers">Передать задания между пользователями </option>		
										 	<option value="LoadProcessPacket">Загрузка процесса</option>
											</select>
										</td>
									</tr>
										<tr>
										<td colspan="2" align="center"> <input type="submit" value="Найти"></td>
									</tr>
								</table>
							</div>
							</form>
					<hr noshade size=1>
					<%if (cam.isActionsView()) {%>
					<jsp:include page="actions.jsp"/>
					<%} else {%>
					<jsp:include page="transactions.jsp"/>
					<%} %>
<jsp:include flush="true" page="footer.jsp" />
  </body>
</html:html>