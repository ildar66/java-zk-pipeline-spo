<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.List"%>
<%@page import="org.uit.director.db.dbobjects.WorkflowTypeProcess"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Удаление схем</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
  <body class="soria">
	<jsp:include page="header_and_menu.jsp" />
	<h1>Управление бизнес-процессами</h1>
	<h2>Удаление схем</h2>
	<%	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request); 									
	%>
	<html:errors />
	<html:messages id="actionMessage"></html:messages>
	<form action="deleteSchema.do" method="post"> 
		<table class="fields">
			<tr>
				<th>Тип процесса</th>
				<td>
					<select name="typeProcess" >
					<%List tp = WPC.getInstance().getTypeProcessesList().getTypesProcesses();
					for (int i = 0; i < tp.size(); i++) {
						WorkflowTypeProcess wtp = (WorkflowTypeProcess)tp.get(i);
						%>
						<option value="<%=wtp.getIdTypeProcess()%>"><%=wtp.getNameTypeProcess()%></option>
						<%
					}
					%>
					</select>
				</td>
			</tr>
			<tr>
				<th/>
				<td><button type="submit">Удалить</button></td>
			</tr>
		</table>
	</form>
	<jsp:include flush="true" page="footer.jsp" />
  </body>
</html>