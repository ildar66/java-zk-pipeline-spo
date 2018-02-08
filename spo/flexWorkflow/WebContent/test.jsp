<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<html>
<head>
<title>test jsp spring-servlet.xml</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
<ul>
<%List<ru.md.domain.Currency> list = ru.masterdm.spo.utils.SBeanLocator.singleton().getCurrencyMapper().getCurrencyList();
for(ru.md.domain.Currency c : list){%>
<li><%=c.getCode() %></li>
<%}%>
</ul>
<a href="test.html">назад</a>
</body>
</html>