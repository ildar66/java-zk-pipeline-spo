<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<html:html>
<head>
<title>UploadFileFrame</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
	<input type="hidden" name="unid" value="<bean:write name="formUploadFile" property="unid" />"/>
	<!-- <bean:write name="formUploadFile" property="unid" /> <br/>
	<bean:write name="formUploadFile" property="attachName" /> -->
</body>
</html:html>
