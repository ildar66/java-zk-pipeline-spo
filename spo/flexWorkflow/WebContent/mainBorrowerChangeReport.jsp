<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<html>
<head>
<%
String number = TaskHelper.taskFacade().getTask(Long.valueOf(request.getParameter("id"))).getNumberDisplay();
 %>
<title>История изменения основного заемщика в Заявке № <%=number %></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
<h1>История изменения основного заемщика в Заявке № <%=number %></h1>
<table class="regular" border="1">
	<thead>
		<tr>
			<th>Единый клиент</th>
			<!--  <th>Старый заемщик</th>-->
			<!--  <th>Новый заемщик</th>-->
			<%--<th>Клиентская запись</th>--%>
			<th>Дата и время</th>
			<th>Автор</th>
		</tr>
	</thead>
	<tbody>
<%for(com.vtb.domain.MainBorrowerChangeLog r : TaskHelper.taskFacade().getMainBorrowerChangeLog(Long.valueOf(request.getParameter("id")))){
String org = r.getNewOrg();
if (!Formatter.str(r.getUnitedClient()).isEmpty()) org =r.getUnitedClient();
%>
<tr>

<td><%=org %></td>
<%--<td><%=r.getOldOrg() %></td>--%>
<%--<td><%=r.getNewOrg() %></td>--%>
<td><%=Formatter.formatDateTime(r.getLogDate()) %></td>
<td><%=r.getUserName() %></td>
</tr>
<%} %>
	</tbody>
</table>
</body>
</html>