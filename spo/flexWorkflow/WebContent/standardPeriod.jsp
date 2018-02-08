<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page isELIgnored="true"%>
<%@page import="ru.md.spo.dbobjects.StandardPeriodValueJPA"%>
<%@page import="ru.md.pup.dbobjects.StageJPA"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="ru.md.spo.dbobjects.StandardPeriodGroupJPA"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.StandardPeriodBeanLocal" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.spo.dbobjects.StandardPeriodVersionJPA"%>
<%@page import="ru.md.pup.dbobjects.ProcessTypeJPA"%>
<%@page import="java.util.*" %>
<% String title = "Справочник нормативных сроков";
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);

String standardPeriodVersionParam = request.getParameter("idversion");
if(request.getParameter("edited")!=null &&request.getParameter("edited").equals("y")){
    StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
    standardPeriodVersionParam = spLocal.saveStandardPeriod(request);
}

Set<ProcessTypeJPA> processTypeList = pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null);
Long processTypeId = ru.md.helper.TaskHelper.getProcessTypeId(request, response);
ProcessTypeJPA processType = null;
for (ProcessTypeJPA p : processTypeList){
 	if(p.getIdTypeProcess().equals(processTypeId)){
 	    processType = p;
        title += " для БП '" + p.getDescriptionProcess() + "'";
}}
boolean editMode = pupFacadeLocal.getUser(wsc.getIdUser()).isStandardPeriodEditor(processTypeId);
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
<form method="post" action="standardPeriod.jsp" name="form1">
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
if(processType.getStandardPeriodVersions().isEmpty()){
    standardPeriodVersionParam = pupFacadeLocal.createStandardPeriodVersion(processTypeId).toString();
    for (ProcessTypeJPA p : pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null)){
 	    if(p.getIdTypeProcess().equals(processTypeId)){
 	        processType = p;
    }}
} %>
<br />Версия нормативных сроков:
<select name="idversion" onchange="submit()">
<% 
StandardPeriodVersionJPA version = null;
if(standardPeriodVersionParam==null) {standardPeriodVersionParam = processType.getStandardPeriodVersions().iterator().next().getId().toString();}
for(StandardPeriodVersionJPA v : processType.getStandardPeriodVersions()){
	String selected = "";
	if(v.getId().toString().equals(standardPeriodVersionParam)){
	    selected="selected";
	    version = v;
	}
	if (version==null) {version = processType.getStandardPeriodVersions().iterator().next();}
	%>
	<option <%=selected %> value="<%=v.getId() %>">№<%=v.getId() %> (дата <%=v.getFormattedDate() %>)</option>
<%} %>
</select>
<br />
<%if(version!=null){ %>Этапов в версии: <%=version.getStandardPeriodGroups().size() %><%} %>
<br />
<%if(editMode){%>
<input type='submit' value='Сохранить версию' onclick='return validateStandartPeriod();'>
<%} else{ %>
Справочник доступен на просмотр. Чтобы редактировать его должна быть роль "Редактор нормативных сроков".
<%} %>
<input type="hidden" name="edited" value="n" id="edited">
<br /><span class="notActive">Так выделены устаревшие этапы.</span>
<table class="regular fixed" id="main">
    <col width="250px" /><col width="450px" /><col width="200px" />
    <thead>
        <tr>
        <th>Этап</th>
        <th>Нормативный срок</th>
        <th>Операция определения критерия</th>
        </tr>
    </thead>
<tbody>
<%for(StandardPeriodGroupJPA group : version.getStandardPeriodGroupList()){ %>
<tr id="trgroup<%=group.getId()%>">
<td id="td<%=group.getId()%>">
<%if(editMode){%>
<input type="text" name="group_name_<%=group.getId()%>" value="<%=group.getName() %>" size="35">
<%}else{ %>
<%=group.getName() %>
<%} %>
<%if(editMode){ %>
<a href="javascript:;" onclick="$('#trgroup<%=group.getId()%>').remove();">Удалить этап</a><br /> 
<a href="javascript:;" onclick="addStage('<%=processTypeId %>','<%=group.getId() %>');">Добавить операцию</a> 
<%} %>
<ol>
<%for(StageJPA stage : group.getStages()){
String id = group.getId()+"_"+stage.getIdStage();
%>
<li id="<%=id%>">
<span class="<%=stage.isActive()?"":"notActive" %>"><%=stage.getDescription() %></span>
<%if(editMode){ %><input type="hidden" name="stage_<%=id%>">
<a href="javascript:;" onclick="$('#<%=id%>').remove();">исключить</a><%} %>
</li>
<%} %>
</ol>
</td>
<td valign="top">
<table class="regular fixed" width="445px" id="standardPeriodValueTable<%=group.getId()%>">
    <col width="100px" /><col width="270px" /><col width="70px" />
    <tr><th>Нормативный срок (дни)</th><th>Критерий дифференциации</th><th>Запрет редакт. срока</th></tr>
    <%for(StandardPeriodValueJPA value : group.getValues()) {
    String id = "value_"+group.getId();
    %>
    <tr id="tr_<%=value.getId()%>"><td>
    <%if(editMode){ %>
    <input type="hidden" name="<%=id%>" value="<%=value.getId()%>">
    <input type="text" value="<%=value.getFormatedPeriod() %>" name="period_<%=id%>"><br />
    <a href="javascript:;" onclick="$('#tr_<%=value.getId()%>').remove();">исключить</a>
    <%}else{ %><%=value.getFormatedPeriod() %><%} %>
    </td>
    <td><textarea cols="30" rows="6" name="name_<%=id%>"><%=value.getName() %></textarea></td>
    <td align="center"><input type="checkbox" <%=value.isReadonly()?"checked":"" %> name="readonly_<%=value.getId()%>" value="y"></td></tr>
    <%} %>
</table>
<%if(editMode){ %><a href="javascript:;" onclick="addValue('<%=group.getId() %>');">Добавить нормативный срок</a> <%} %>
</td>
<td valign="top">
<%if(editMode){ %>
<a href="javascript:;" onclick="addDecisionStage('<%=processTypeId %>','<%=group.getId() %>');">Добавить операцию</a> 
<%} %>
<ol id="DecisionStageOl<%=group.getId()%>">
<%for(StageJPA stage : group.getDecisionStages()){
String id = "DecisionStage"+group.getId()+"_"+stage.getIdStage();
%>
<li id="<%=id%>">
<span class="<%=stage.isActive()?"":"notActive" %>"><%=stage.getDescription() %></span>
<%if(editMode){ %><input type="hidden" name="decision_id_<%=group.getId() %>" value="<%=stage.getIdStage()%>">
<a href="javascript:;" onclick="$('#<%=id%>').remove();">исключить</a><%} %>
</li>
<%} %>
</ol>

</td>
</tr>
<%} %>
</tbody>
</table>
<a href="javascript:;" onclick="addStandardPeriodGroup()">добавить этап</a>
<script id="newStandardPeriodGroupTemplate" type="text/x-jquery-tmpl">
<tr id="trgroup${id}"><td id="td${id}"><input type="text" name="group_name_${id}" value="новый этап" size="35">
<a href="javascript:;" onclick="$('#trgroup${id}').remove();">Удалить этап</a><br /> 
<a href="javascript:;" onclick="addStage('<%=processTypeId %>','${id}');">Добавить операцию</a>
<ol></ol></td>
<td valign="top">
<table class="regular fixed" width="445px" id="standardPeriodValueTable${id}">
    <col width="100px" /><col width="270px" /><col width="70px" />
    <tr><th>Нормативный срок (дни)</th><th>Критерий дифференциации</th><th>Запрет редакт. срока</th></tr>
</table>
<a href="javascript:;" onclick="addValue('${id}');">Добавить нормативный срок</a></td>
<td valign="top">
<a href="javascript:;" onclick="addDecisionStage('<%=processTypeId %>','${id}');">Добавить операцию</a> 
<ol id="DecisionStageOl${id}">
</ol>
</td></tr>
</script>
<%} %>
</form>
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
