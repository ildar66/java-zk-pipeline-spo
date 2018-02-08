<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="ru.md.spo.util.Config"%>
<%@page import="java.util.List"%>
<%@page import="com.vtb.domain.Report"%>
<%@page import="com.vtb.value.BeanKeys"%>

<html:html>
<head>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta http-equiv="Pragma" content="no-cache"> 
<title>Печатные формы</title>
<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<h1>Печатные формы</h1>
<br />
	<logic:iterate id="url" name="<%=BeanKeys.REPORT_PRINT_URLS%>"  type="java.util.Map.Entry" scope="request">
		<li><a href="<bean:write name="url" property="key"/>" title="Скачать документ"><bean:write name="url" property="value"/></a></li>
	</logic:iterate>
</body>
</html:html>
