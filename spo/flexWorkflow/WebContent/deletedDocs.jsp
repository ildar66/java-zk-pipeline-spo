<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="java.util.List"%>
<%@page import="ru.md.pup.dbobjects.AttachJPA"%>
<%@page import="com.vtb.util.Formatter"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html:html>
<head>
    <base target="_self">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Отчет: Удаленные документы по заявке</title>
	<link rel="stylesheet" href="style/style.css" />
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<script language="javascript" src="scripts/date.js"></script>
	<script language="javascript" src="scripts/applicationScripts.js"></script>
	<script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
    <script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
	<script language="javascript" src="scripts/form/frame.js"></script>
</head>
<body class="soria">
<%
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
Long pupid = Long.valueOf(request.getParameter("pupid"));
Long processTypeId = taskFacade.getTaskByPupID(pupid).getProcess().getProcessType().getIdTypeProcess();
 %>
<h1>Отчет: Удаленные документы по заявке</h1>
<div>Тип процесса: <%=taskFacade.getTaskByPupID(pupid).getProcessTypeName() %></div>
<div>Заявка: <%=taskFacade.getTaskByPupID(pupid).getNumberDisplay() %></div>
<table class="regular" id="attachtable">
<thead>
<tr>
    <td style="width:15%">Наименование файла</td>
    <td style="width:10%">Срок действия</td>
    <td style="width:15%">Добавил</td>
    <td style="width:15%">Утвердил</td>
    <td style="width:15%">Удалил</td>
</tr>
</thead>
<tbody>
<%
for(AttachJPA a : pupFacade.findDelAttachemntByOwnerAndType(request.getParameter("pupid"), 0L)){
%>
<tr>
<td><%=a.getFILENAME() %></td>
<td><%=a.getFormatedDateOfExpiration() %></td>
<td><%=a.getWhoAdd()==null?"ЭДК":a.getWhoAdd().getFullNameWithRoles(processTypeId)%>, 
<%=Formatter.formatDateTime(a.getDATE_OF_ADDITION()) %></td>
<td><%=a.getWhoAccepted()==null?"":a.getWhoAccepted().getFullNameWithRoles(processTypeId)+", "%>
<%=Formatter.format(a.getDATE_OF_ACCEPT()) %></td>
<td><%=a.getWhoDel().getFullNameWithRoles(processTypeId)%>, 
<%=Formatter.format(a.getDATE_OF_DEL()) %></td>
</tr>
<%} %>
</tbody>
</table>
<h1 style="color:red">Примечание: Для восстановления удаленного документа необходимо обратиться в Отдел поддержки пользователей.</h1>
</body>
</html:html>