<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page	language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
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
<tr><td onClick="$.fancybox.close();location.href='showTaskList.do?typeList=all&idDepartment='">все подразделения</td></tr>
<%
WorkflowSessionContext wsc = null;
try {
		wsc = AbstractAction.getWorkflowSessionContext(request);
	} catch(Exception e) {
		response.sendRedirect("/errorPage.jsp");
		return;
	}
CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
ru.md.spo.ejb.PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(ru.md.spo.ejb.PupFacadeLocal.class);

ru.md.pup.dbobjects.UserJPA user = pupFacadeLocal.getUser(wsc.getIdUser());
Set<Long> childdepartmentIds = WPC.getInstance().getAllChildrenOfDeparment(user.getDepartment().getIdDepartment());
childdepartmentIds.add(user.getDepartment().getIdDepartment());
if(user.isAuditor()){
    childdepartmentIds = WPC.getInstance().getAllChildrenOfDeparment(1L);
    childdepartmentIds.add(1L);
}

Map<String, String> depsHierarchy = WPC.getInstance().getDepartmentsHierarchy();
	if (childdepartmentIds != null && childdepartmentIds.size()>0){
		Map<Long, String> allDepartments = compenduim.findDepartmentHierarchyData("path_short");
		for (Long key : allDepartments.keySet()) {
			if (childdepartmentIds.contains(key)){
%>
<tr><td onClick="$.fancybox.close();location.href='showTaskList.do?typeList=all&idDepartment=<%=key.toString() %>'"><%=allDepartments.get(key)%></td></tr>
<%
		}}
	}
%>	
</TBODY>
</table>
</body>
</html:html>