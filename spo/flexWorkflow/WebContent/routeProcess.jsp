<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.pup.dbobjects.DepartmentJPA"%>
<%@page isELIgnored="true"%>
<%@page import="ru.md.pup.dbobjects.StageJPA"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.spo.ejb.DictionaryFacadeLocal" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.spo.dbobjects.SpoRouteVersionJPA"%>
<%@page import="ru.md.pup.dbobjects.ProcessTypeJPA"%>
<%@page import="java.util.*" %>
<% String title = "Настройка маршрутизации заявки";
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);

String versionParam = request.getParameter("idversion");
if(request.getParameter("edited")!=null &&request.getParameter("edited").equals("y")){
    DictionaryFacadeLocal dict = com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
    versionParam = dict.saveSpoRoute(request);
}

Set<ProcessTypeJPA> processTypeList = pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null);
Long processTypeId = ru.md.helper.TaskHelper.getProcessTypeId(request, response);
ProcessTypeJPA processType = null;
for (ProcessTypeJPA p : processTypeList){
 	if(p.getIdTypeProcess().equals(processTypeId)){
 	    processType = p;
        title += " для БП '" + p.getDescriptionProcess() + "'";
}}
ru.md.pup.dbobjects.UserJPA user = pupFacadeLocal.getUser(wsc.getIdUser());
boolean editMode = user.isAdmin();
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title><%=title %></title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<jsp:include page="header_and_menu.jsp" />
<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<h1><%=title %></h1>
<form method="post" action="routeProcess.jsp" name="form1">
Бизнес-процесс:
<select name="idProcessType" onchange="submit()">
<%if(processTypeList.size()>1){ %>
<option value=""></option>
<%} 
for (ProcessTypeJPA p : processTypeList){ %>
<option <%=processTypeId!=null&&p.getIdTypeProcess().equals(processTypeId)?"selected":"" %> 
value="<%=p.getIdTypeProcess().toString() %>"><%=p.getDescriptionProcess() %></option>
<%} %>
</select><br />
<%if(processTypeId==null){ %>
<h1>Необходимо выбрать бизнес-процесс!</h1>
<%}else{
if(processType.getSpoRouteVersion().isEmpty()){
    versionParam = pupFacadeLocal.createSpoRouteVersion(processTypeId).toString();
    for (ProcessTypeJPA p : pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null)){
 	    if(p.getIdTypeProcess().equals(processTypeId)){
 	        processType = p;
    }}
} %>
<br />Версия маршрутов БП:
<select name="idversion" onchange="submit()">
<% 
SpoRouteVersionJPA version = null;
if(versionParam==null) {versionParam = processType.getSpoRouteVersion().iterator().next().getId().toString();}
for(SpoRouteVersionJPA v : processType.getSpoRouteVersion()){
	String selected = "";
	if(v.getId().toString().equals(versionParam)){
	    selected="selected";
	    version = v;
	}
	if (version==null) {version = processType.getSpoRouteVersion().iterator().next();}
	%>
	<option <%=selected %> value="<%=v.getId() %>">№<%=v.getId() %> (дата <%=v.getFormattedDate() %>)</option>
<%} %>
</select>
<br />
<br />
<%if(editMode){%>
<input type='submit' value='Сохранить версию' onclick='$("#edited").val("y");return validateRouteProcess();'>
<%} else{ %>
Справочник доступен на просмотр. Чтобы редактировать его должна быть роль "Администратор системы".
<%} %>
<input type="hidden" name="edited" value="n" id="edited">
<br />
<table class="regular fixed" id="main">
    <col width="250px" /><col width="450px" /><col width="200px" />
    <thead>
        <tr>
        <th>Операция</th>
        <th>Инициирующие подразделения</th>
        <th>Подразделение по-умолчанию</th>
        </tr>
    </thead>
<tbody>
<%for(ru.md.spo.dbobjects.SpoRouteJPA route : version.getRoutes()){ %>
<tr id="route<%=route.getId()%>_Tr"><td>
<%=route.getStageName() %> <input type="hidden" name="stage" value="<%=route.getStageName() %>"><input type="hidden" name="routeid" value="<%=route.getId() %>">
</td><td id="route<%=route.getId()%>_initDepTd">
<%if(editMode){%>
<a href="javascript:;" onclick="routeAddInitDep('<%=route.getId()%>')">Добавить инициирующее подразделение</a><br /><br />
<%}for(DepartmentJPA dep : route.getInitDepartments()){ %>
<div id="routeInitDep<%=route.getId()%>_<%=dep.getIdDepartment() %>">
<input type="hidden" name="route<%=route.getId()%>_initdep" value="<%=dep.getIdDepartment()%>">
<%=dep.getShortName() %> <%if(editMode){%><a href="javascript:;" onclick="$('#routeInitDep<%=route.getId()%>_<%=dep.getIdDepartment() %>').remove();">исключить</a><%} %></div>
<%} %>
</td><td>
<input name="defaultDepartment" value="<%=route.getDefaultDepartment().getIdDepartment()%>" 
id="route<%=route.getId()%>_defaultDepartment" type="hidden">
<%if(editMode){%><a href="javascript:;" onclick="routeChangeDefDep('<%=route.getId()%>')"><%} %>
<span id="route<%=route.getId()%>_defaultDepartmentName"><%=route.getDefaultDepartment().getShortName() %></span>
<%if(editMode){%></a><%} %>
</td><td><%if(editMode){%><a href="javascript:;" onclick="$('#route<%=route.getId()%>_Tr').remove()">исключить</a><%} %></td></tr>
<%} %>
</tbody>
</table>
<%if(editMode){%>
<a href="javascript:;" onclick="routeAdd('<%=processTypeId%>')">Добавить маршрут БП</a>
<%}} %>

</form>
<jsp:include flush="true" page="footer.jsp" />

<script id="newRouteInitDepTemplate" type="text/x-jquery-tmpl">
<div id="routeInitDep${nextval}"><input type="hidden" name="route${routeid}_initdep" value="${id}">
${name} <a href="javascript:;" onclick="$('#routeInitDep${nextval}').remove();">исключить</a></div>
</script>
<script id="newRouteTemplate" type="text/x-jquery-tmpl">
<tr id="route${nextval}_Tr"><td>${name}<input type="hidden" name="stage" value="${name}"><input type="hidden" name="routeid" value="${nextval}"></td><td id="route${nextval}_initDepTd">
<a href="javascript:;" onclick="routeAddInitDep('${nextval}')">Добавить инициирующее подразделение</a><br /><br />
</td><td>
<input name="defaultDepartment" value="<%=user.getDepartment().getIdDepartment()%>" 
id="route${nextval}_defaultDepartment" type="hidden">
<a href="javascript:;" onclick="routeChangeDefDep('${nextval}')">
<span id="route${nextval}_defaultDepartmentName"><%=user.getDepartment().getShortName() %></span></a>
</td><td><a href="javascript:;" onclick="$('#route${nextval}_Tr').remove()">исключить</a></td></tr>
</script>
</body>
</html>
