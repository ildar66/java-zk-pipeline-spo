<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.model.ActionProcessorFactory" %>
<%@page import="com.vtb.model.TaskActionProcessor" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="javax.naming.InitialContext" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.pup.dbobjects.UserJPA"%>
<%@page import="ru.md.pup.dbobjects.RoleJPA"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title>Переназначение исполнителя</title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<jsp:include page="header_and_menu.jsp" />
<%
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task = processor.findByPupID(Long.valueOf(request.getParameter("pupid")),false);
task.getMain().setSumWithCurrency();
InitialContext initialContext = new InitialContext();
PupFacadeLocal flexWorkflowFacadeLocal = (PupFacadeLocal)initialContext.lookup("ejblocal:"+PupFacadeLocal.class.getName());
UserJPA boss = flexWorkflowFacadeLocal.getUser(wsc.getIdUser());
Long slaveId=wsc.getIdUser();
if(request.getParameter("user")!=null && request.getParameter("user").length()>0)
    slaveId = Long.valueOf(request.getParameter("user"));
UserJPA slave = flexWorkflowFacadeLocal.getUser(slaveId);
%>
<form method="post" action="reassignList.jsp" id="form1" name="form1">
<input type="hidden" name="idProcessType" value="<%=task.getMain().getIdProcessType() %>">
<input type="submit" value="Назад" class="button">
</form>
<h1>Назначение исполнителя</h1>
<table class="regular" style="width: 800px;">
    <thead><tr><th>параметр</th><th>значение</th></tr></thead>
    <tr class="a"><td>номер</td><td><%=task.getNumberDisplay() %></td></tr>
    <tr class="b"><td>Бизнес-процесс</td><td><%=task.getMain().getDescriptionProcess() %></td></tr>
    <tr class="a"><td>Контрагент</td><td><%=task.getOrganisation() %></td></tr>
    <tr class="b"><td>сумма</td><td><%=task.getMain().getSumWithCurrency() %></td></tr>
</table>
<%if(request.getParameter("commit")!=null && request.getParameter("commit").equals("y")&&
    slaveId!=null && request.getParameter("role")!=null){
try{
flexWorkflowFacadeLocal.assign(slaveId,Long.valueOf(request.getParameter("role")),task.getId_pup_process(),wsc.getIdUser());%>
Назначение пользователя выполнено.
<%}catch(Exception e){
     %>
Ошибка. <%=e.getMessage() %>
<%}}else{ %>
<form method="post" id="form2" name="form2">
	исполнителя
	<select name="user" onchange="submit()">
	<option value="<%=boss.getIdUser().toString() %>"><%=boss.getFullName() %></option>
	<%
	for(UserJPA user : flexWorkflowFacadeLocal.getSlave(wsc.getIdUser(),task.getMain().getIdProcessType())){
		if(user.getIdUser().equals(slaveId))slave = user;
		if(!user.getIdUser().equals(boss.getIdUser())){
		%>
		<option <%=user.getIdUser().equals(slaveId)?"selected":"" %>
		value="<%=user.getIdUser().toString() %>"><%=user.getFullName() %></option>
	<%}} %>
	</select>
	<%if(slaveId!=null){ %>
		<br />По роли
		<select name="role">
		<%
		for(RoleJPA role : slave.getRoles()){
			if(role.getProcess().getIdTypeProcess().equals(task.getMain().getIdProcessType())){
			%>
				<option <%=role.getIdRole().toString().equals(request.getParameter("role"))?"selected":"" %>
				value="<%=role.getIdRole().toString() %>"><%=role.getNameRole() %></option>
		<%}} %>
		</select>
		<br />
		<input type="hidden" name="commit" id="commit" value="n">
		<input type="submit" value="Назначить" id="btnSubmit" onclick="$('#commit').val('y');" class="button">
	<%} %>
</form>
<%} %>
<jsp:include flush="true" page="footer.jsp" />
<script type="text/javascript">
$(document).ready(function(){
$("#upperUserName, #bottomUserName, a.login").fancybox({
zoomSpeedIn: 0,
zoomSpeedOut:0,
frameWidth: 600,
frameHeight: 600,
'hideOnContentClick': false
});
});
</script>
</body>
</html>