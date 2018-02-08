<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.pup.dbobjects.AssignJPA"%>
<%@page import="ru.md.pup.dbobjects.ProcessTypeJPA"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="java.util.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title>Переназначение исполнителя</title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<jsp:include page="header_and_menu.jsp" />
<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<h1>Переназначение исполнителя</h1>
<%
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
Set<ProcessTypeJPA> processTypeList = pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null);
Long processTypeId=null;
if(processTypeList.size()==1)processTypeId=((ProcessTypeJPA)processTypeList.toArray()[0]).getIdTypeProcess();
String SprocessTypeId=request.getParameter("idProcessType");
if(SprocessTypeId!=null){
    request.getSession().setAttribute("idProcessType",request.getParameter("idProcessType"));
    Cookie cookie = new Cookie("idProcessType",request.getParameter("idProcessType"));
    cookie.setMaxAge(24 * 60 * 60);
    response.addCookie(cookie);
}
if(SprocessTypeId==null){
    SprocessTypeId=(String)request.getSession().getAttribute("idProcessType");
    for(Cookie c : request.getCookies())
        if(c.getName().equals("idProcessType"))SprocessTypeId=c.getValue();
}
if (SprocessTypeId!=null) 
    for (ProcessTypeJPA p : processTypeList)
        if(p.getIdTypeProcess().toString().equals(SprocessTypeId))
            processTypeId=p.getIdTypeProcess();
%>
<form method="post" action="reassignList.jsp">
Бизнес-процесс:
<select name="idProcessType" onchange="submit()">
<%if(processTypeList.size()>1){ %>
<option value=""></option>
<%} 
for (ProcessTypeJPA p : processTypeList){ %>
<option <%=processTypeId!=null&&p.getIdTypeProcess().equals(processTypeId)?"selected":"" %> 
value="<%=p.getIdTypeProcess().toString() %>"><%=p.getDescriptionProcess() %></option>
<%} %>
</select>
<%if(processTypeId==null){ %>
<h1>Необходимо выбрать бизнес-процесс!</h1>
<%}else{ %>
<table class="regular">
        <thead>
            <tr>
            <th></th>
            <th>Заявка №</th>
            <th>Контрагент</th>
            <th>Сумма</th>
            <th>Исполнитель</th>
            <th></th>
            <th>Роль, Процесс</th>
            <th>Кто назначил</th>
            <th>Дата назначения</th>
            <th></th>
            </tr>
        </thead>
<tbody>
<% 
for(AssignJPA assign : pupFacadeLocal.getAssignToUsersTasksList(wsc.getIdUser(),processTypeId)){
ru.md.spo.dbobjects.TaskJPA task = taskFacadeLocal.getTaskByPupID(assign.getId_process());
%>
<tr>
<td style="width:60px">
    <a target="_blank" href="<%=task.getActiveStageUrl()%>" title="Посмотреть активные операции"
        ><img src="style/in_progress.png" alt="Активные операции"
    ></a>
    <a href="report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=<%=assign.getId_process().toString()%>&menuOff=1" target="_blank" title="Посмотреть хронологию выполнения операций по этой заявке"
        ><img src="style/time.png" alt="хронология"
    ></a>
    <a target="_blank"
        href="plugin.action.do?class=org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper&idProcess=<%=assign.getId_process().toString()%>"
        title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"
    ></a> 
</td>
<td style="width:120px"><%=task.getNumberDisplay() %></td>
<td><%=task.getOrganisation() %></td>
<td style="width:120;text-align:right"><%=task.getSumWithCurrency() %></td>
<td><a class="login"  href="roleslist.jsp?login=<%=assign.getUserTo().getLogin() %>"><%=assign.getUserTo().getFullName() %></a></td>
<td><a href="deleteAssign.do?idAssign=<%=assign.getIdAssign() %>">
<img src="theme/img/delete.gif" alt="Отозвать назначение" title="Отозвать назначение"></a></td>
<td><%=assign.getRole().getNameRole() %><br />(<%=assign.getRole().getProcess().getDescriptionProcess() %>)</td>
<td><%if(assign.getUserFrom()!=null){ %>
<a class="login"  href="roleslist.jsp?login=<%=assign.getUserFrom().getLogin() %>">
<%=assign.getUserFrom().getFullName() %></a>
<%} %>
</td>
<td><%=Formatter.format(assign.getDate_event()) %></td>
<td><a href="assign.jsp?pupid=<%=assign.getId_process() %>">назначить исполнителя</a></td>
</tr>
<%} %>
</tbody>
</table>
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
