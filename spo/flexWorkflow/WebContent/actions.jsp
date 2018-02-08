<%@ page contentType="text/html;charset=utf-8" language="java" %>



<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.managers.ControlActionsManager"%>

<% WorkflowSessionContext wsc = AbstractAction
						.getWorkflowSessionContext(request); 				
						
	ControlActionsManager cam = wsc.getControlActionsManager();			
	String dateLeft = cam.getForm().getDateLeft();
	String dateRight = cam.getForm().getDateRight();
%>	

<div class="tabledata">
<center> Действия администраторов <%if (!dateLeft.equals("") && !dateRight.equals("")) {%>
		в период с '<%=dateLeft %>' по '<%=dateRight %>'
<%} %>
</center>

<%=cam.getHistoryTableHTML("ID", "ATTRIBUTES", cam.getAttributesHistory())%>

<%=cam.getHistoryTableHTML("ID_GROUP", "GROUPS", cam.getGroupesHistory())%>

<%=cam.getHistoryTableHTML("ID_MESSAGE", "MESSAGES", cam.getMessagesHistory())%>

<%=cam.getHistoryTableHTML("ID_TYPE_PROCESS,TYPE_PARAMETER", "PROCESS_PARAMETERS", cam.getProcessParametersHistory())%>

<%=cam.getHistoryTableHTML("ID_PROCESS", "PROCESSES", cam.getProcessHistory())%>

<%=cam.getHistoryTableHTML("ID_ROLE", "ROLES", cam.getRolesHistory())%>

<%=cam.getHistoryTableHTML("ID_STAGE", "STAGES", cam.getStagesHistory())%>

<%=cam.getHistoryTableHTML("ID_ROLE,ID_STAGE", "STAGES_IN_ROLE", cam.getStagesInRoleHistory())%>

<%=cam.getHistoryTableHTML("ID_TASK", "TASKS", cam.getTasksHistory())%>

<%=cam.getHistoryTableHTML("ID_TYPE_PROCESS", "TYPE_PROCESS", cam.getTypeProcessHistory())%>

<%=cam.getHistoryTableHTML("ID_ROLE,ID_USER", "USER_IN_ROLE", cam.getUserInRoleHistory())%>

<%=cam.getHistoryTableHTML("ID_USER", "USERS", cam.getUsersHistory())%>

<%=cam.getHistoryTableHTML("ID_VAR", "VARIABLES", cam.getVariablesHistory())%>


</div>
