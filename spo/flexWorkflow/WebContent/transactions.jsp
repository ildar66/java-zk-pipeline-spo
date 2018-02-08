<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>


<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.managers.ControlActionsManager"%>
<%@page import="org.uit.director.contexts.WPC"%>
<div class="tabledata">
<table>
<caption></caption>
<tr>
<th>Номер транзакции </th>
<th>Дата </th>
<th>Пользователь</th>
<th>IP адрес</th>
<th>Тип действия </th>
</tr>
<%
	WorkflowSessionContext wsc = AbstractAction
						.getWorkflowSessionContext(request); 				
						
	ControlActionsManager cam = wsc.getControlActionsManager();								

	List res = cam.getTransactionsList();
	
	if (res != null) {
	
		for (int i = 0; i < res.size(); i++) {
			%><tr><%
			Map m = (Map)res.get(i);
			String idTr = (String)m.get("ID_TRANSACTION");
			String date = (String)m.get("DATE_TRANSACTION");
			date = WPC.getInstance().formatDateTimeDBToDateTime(date);
			String idUser = (String)m.get("ID_USER");
			String ipAddress = (String)m.get("IP_ADDRESS");
			String typeAction = (String)m.get("NAME_ACTION");
	%>
			<td><a href="control.action.do?idTransaction=<%=idTr%>"><%=idTr%> </a></td>
			<td><%=date %></td>
			<td><%=idUser %></td>
			<td><%=ipAddress %></td>
			<td><%=typeAction %></td>
			</tr>
	
<%	
		}	
	}
%>
</table>
</div>