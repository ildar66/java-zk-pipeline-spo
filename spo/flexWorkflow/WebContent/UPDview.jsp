<%@ page contentType="text/html; charset=Cp1251"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<html:html>
<HEAD>
<META name="GENERATOR" content="IBM Software Development Platform">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/Master.css" rel="stylesheet"
	type="text/css">
<TITLE></TITLE>
</HEAD>

<BODY>
<%

String res = (String)request.getAttribute("Content");
res = res.replaceAll("<input type=\"submit\" value=\"Изменить\">", "");
res = res.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать документ\" width=\"17px\" hight=\"17px\">", "");
res = res.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать страницу\" width=\"17px\" hight=\"17px\">", "");
%>
<%=res%>
<center><a href="javascript:history.go(-1)">Назад</a></center>
</BODY>
</html:html>
