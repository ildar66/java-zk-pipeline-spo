<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="ru.md.helper.TaskHelper"%>
<%@ page contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.model.ActionProcessorFactory" %>
<%@page import="com.vtb.model.TaskActionProcessor" %>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor" %>
<%@page import="org.uit.director.tasks.TaskInfo" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>

<%@page import="ru.masterdm.compendium.domain.User"%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Выбор роли при назначении</title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<jsp:include page="header_and_menu.jsp" />
<%
WorkflowSessionContext wsc = null;
try {
		wsc = AbstractAction.getWorkflowSessionContext(request);
	} catch(Exception e) {
		response.sendRedirect("/errorPage.jsp");
		return;
}
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
HashMap<Long,String> roles = processor.getRoles2Assign(new Long(request.getParameter("userid")),new Long(request.getParameter("taskid")));
ru.md.pup.dbobjects.TaskInfoJPA taskInfo = TaskHelper.pup().getTask(new Long(request.getParameter("taskid")));
String number = processor.findByPupID(taskInfo.getProcess().getId(),false).getNumberDisplay();
%>
Пользователь <%=compenduim.getUser(new ru.masterdm.compendium.domain.User(new Integer(request.getParameter("userid")))).getFullName() %> может быть назначен на 
задачу <%=taskInfo.getStage().getDescription() %> 
заявки номер
<%=number %>
 по нескольким ролям.
<table class="pane" border="1" style="width: 800px;">
<TBODY>
<%
Set<Long> set = roles.keySet();
Iterator<Long> iter = set.iterator();
while (iter.hasNext()) {
Long id=iter.next();
String name=roles.get(id);
%>
<tr><td><a href="assign.user.do?idTask=<%=request.getParameter("taskid") %>&idUser=<%=request.getParameter("userid") %>&idRole=<%=id %>"><%=name %></a></td></tr>
<%}%>
</TBODY>
</table>
<a href="start.do">Отмена</a>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>