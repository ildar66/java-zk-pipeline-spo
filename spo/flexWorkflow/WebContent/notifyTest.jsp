<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.NotifyFacadeLocal" %>
<html>
<head>
<title>Notify test</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
<h1>Рассылка уведомлений...</h1>
<%
NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
notifyFacade.doNotifyNow();
  %>
выполнена
<hr />
</body>
</html>