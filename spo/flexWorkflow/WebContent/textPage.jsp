<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<META HTTP-EQUIV="content-type" content="text/html; charset=utf-8">
<%@ page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@ page import="org.uit.director.action.AbstractAction"%>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
 <%--
	Created by IntelliJ IDEA.
	User: pd190390
	Date: 28.02.2005
	Time: 13:21:27
	To change this template use File | Settings | File Templates.
--%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Сообщение</title>
	<style type="text/css">@import url("resources/stylesheet.css"); </style>
	<link rel="stylesheet" href="style/style.css" />
</head>


<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Информационное сообщение</h1>
				<br>
				<%
				    WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
				if (wsc.isNewContext()) {%><script> document.location = "start.do"; </script> <% return;}
				%>
				<big> 
				    <center>
				        <%=wsc.getPageData()%>
				        <%
				        wsc.setPageData(null);
				        %>
				    </center>
				</big>
				<br>
				<br>
<jsp:include flush="true" page="footer.jsp" />

  </body>
</html>