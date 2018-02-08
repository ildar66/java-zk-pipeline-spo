<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.model.ActionProcessorFactory" %>
<%@page import="com.vtb.model.TaskActionProcessor" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.CrmFacadeLocal" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.crm.dbobjects.ProductQueueJPA"%>
<%@page import="com.vtb.domain.SPOAcceptType"%>
<html>
<%WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
String typeParam = request.getParameter("type");
	//из статических таблиц всех подчиненных
	SPOAcceptType type = SPOAcceptType.NOTACCEPT;
if (typeParam!=null && typeParam.equals("1")) type = SPOAcceptType.ACCEPT;
if (typeParam!=null && typeParam.equals("2")) type = SPOAcceptType.ERROR;
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
String sendLeftDate=request.getParameter("sendLeftDate")==null?Formatter.format(new java.util.Date()):request.getParameter("sendLeftDate");
String sendRightDate=request.getParameter("sendRightDate")==null?Formatter.format(new java.util.Date()):request.getParameter("sendRightDate");
String login = null;
ru.md.pup.dbobjects.UserJPA user = pupFacadeLocal.getUser(wsc.getIdUser());
if (!user.isAdmin()) {
    login = user.getLogin();
}
ProductQueueJPA[] list = crmFacadeLocal.getProductQueue(type,sendLeftDate,sendRightDate,login);
	%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Список сделок из CRM</title>
	<link rel="stylesheet" href="style/style.css" />
	<script language="javascript" src="resources/cal2.js"></script>
    <script language="javascript" src="resources/cal_conf2.js"></script>
</head>
<body class="soria">
<jsp:include page="header_and_menu.jsp" />
	<form method="post" action="crmproductlist.jsp">
    Статус сделки в очереди: <select name="type" onchange="submit()">
       <option value="0" <%if(type==SPOAcceptType.NOTACCEPT){ %>selected="selected"<%} %>>В очереди загрузки</option>
       <option value="1" <%if(type==SPOAcceptType.ACCEPT){ %>selected="selected"<%} %>>Загружено</option>
       <option value="2" <%if(type==SPOAcceptType.ERROR){ %>selected="selected"<%} %>>Возникли ошибки во время загрузки</option>
    </select>
    <br />Дата выгрузки из CRM
    c <input type="text" class="text date" id="sendLeftDate" name="sendLeftDate" 
    value="<%=sendLeftDate %>" 
    onFocus="displayCalendarWrapper('sendLeftDate', '', false); return false;" />
    по <input type="text" class="text date" id="sendRightDate" name="sendRightDate" 
    value="<%=sendRightDate %>" 
    onFocus="displayCalendarWrapper('sendRightDate', '', false); return false;" />
    <br /><input type="submit" value="Обновить">
	<h1>Сделки из CRM</h1>
	<table class="regular">
	<thead>
			<tr>
				<th>дата выгрузки из CRM</th>
                <%if(type!=SPOAcceptType.NOTACCEPT){ %><th>дата загрузки в СПО</th><%} %>
                <%if(type==SPOAcceptType.ERROR){ %><th>ошибка</th><%} %>
				<th>номер</th>
				<th>название</th>
				<th>сумма</th>
				<th>контрагент</th>
				<th>логин менеджера</th>
				<%if(type==SPOAcceptType.ERROR){ %><th></th><%} %>
				<%if(type==SPOAcceptType.ERROR && user.isAdmin()){ %><th>Загрузить на другого пользователя</th><%} %>
			</tr>
	</thead>
	<tbody>
	<%
	
	for (ProductQueueJPA q : list) {
	    %>
	    <tr>
	    <td><%=Formatter.formatDateTime(q.getSendDate()) %></td>
        <%if(type!=SPOAcceptType.NOTACCEPT){ %>
        <td><%=Formatter.formatDateTime(q.getAcceptDate()) %></td><%} %>
        <%if(type==SPOAcceptType.ERROR){ 
        java.util.Collections.sort(q.getLogs());%><td>
        <%for(ru.md.crm.dbobjects.FbSpoLogStatusJPA log : q.getLogs()){ %>
        <%=Formatter.formatDateTime(log.getERR_DATE()) %> <%=log.getLOG() %><br />
        <%} %></td><%} %>
	    <td class="limit_number"><%=q.getOpportunity().getNUM() %></td>
		<td><%=q.getOpportunity().getProductName() %></td>
		<td class="number"><%=q.getOpportunity().getSumAndCurrency() %></td>
		<td><%=q.getAccount().getName() %></td>
		<td><a 
		<%if(q.getUSERCODE()!=null){ %>class="login" href="roleslist.jsp?login=<%=q.getUSERCODE()%>" <%} %>>
		<%=q.getUSERCODE()==null?"логин менеджера не задан":q.getUSERCODE()%></a></td>
		<%if(type==SPOAcceptType.ERROR){ %><td><a href="loadProduct.do?id=<%=q.getId() %>">повторить загрузку</a></td><%} %>
		<%if(type==SPOAcceptType.ERROR && user.isAdmin()){ %><td><a href="loadProduct.do?id=<%=q.getId() %>&user=<%=wsc.getIdUser()%>">загрузить на пользователя <%=wsc.getFullUserName() %></a></td><%} %>
	    </tr>
	    <%
	}
	 %>
	 </tbody>
	 </table>
	 </form>
<jsp:include page="footer.jsp" />
</body>
<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
<script>
    //Это для календарика
    function popCalInFrame(dateCtrl) {
        var w = gfPop;
        w.fPopCalendar(dateCtrl);   // pop calendar
    }
    
    function showhide ()
    {
        var style = document.getElementById("sendDate").style
        if (style.display == "none")
            style.display = "";
        else {
            style.display = "none";
            document.getElementById("sendLeftDate").value = "";
        }
    }
 
</script>
</html>