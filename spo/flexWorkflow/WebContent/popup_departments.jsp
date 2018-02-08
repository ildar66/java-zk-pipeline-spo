<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.Department"%>
<%@page import="java.util.Set" %>
<%@page import="org.uit.director.contexts.WPC"%>
<html:html>
<head>
<title>department</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
Выбрать подразделение:
<table class="pane" border="1" style="width: 600px;">
<TBODY>
<%
CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
String root_department = request.getParameter("root_department");
Set<Long> legitimate = null;
if (root_department!=null){
    WPC wpc = WPC.getInstance();
    legitimate = wpc.getAllChildrenOfDeparment(new Long(root_department));
    legitimate.add(new Long(root_department));
}
Department[] list = compenduim.getDepartmentListAll();
for(Department department: list){
if(department.isOutdated())continue;
if(request.getParameter("onlyExecDep")!=null&&!department.isExec())continue;
if(request.getParameter("onlyInitialDep")!=null&&!department.isInitial())continue;
if(legitimate!=null && !legitimate.contains(department.getId().longValue()))continue;
%>
<tr><td 
onClick="document.getElementById(document.getElementById('supplyid').value).value='<%=department.getId().toString() %>';
document.getElementById('sp'+document.getElementById('supplyid').value).innerHTML=document.getElementById('spandep<%=department.getId().toString() %>').innerHTML;
<% if(request.getParameter("javascript")!=null){%><%=request.getParameter("javascript")%>;<%} %>$.fancybox.close()">
<span id="spandep<%=department.getId().toString() %>"><%=department.getShortName() %></span></td></tr>
<%} %>
</TBODY>
</table>
</body>
</html:html>