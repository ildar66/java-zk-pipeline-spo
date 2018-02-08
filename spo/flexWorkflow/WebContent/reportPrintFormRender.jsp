<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@page import="java.util.Map"%>
<%@page import="com.vtb.value.BeanKeys"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<link rel="stylesheet" href="style/style.css" />
	<title>Отчёт</title>
	<SCRIPT LANGUAGE=javascript>
		<!--
		//работает в IE6, остальные браузеры могут не работать
		function timer() { setTimeout('reSize()', 5000); }
		function reSize(){
			try{
				var oBody	=	ifrm.document.body;
				var oFrame	=	document.all("ifrm");
				oFrame.style.height = oBody.scrollHeight + (oBody.offsetHeight - oBody.clientHeight);
				oFrame.style.width = oBody.scrollWidth + (oBody.offsetWidth - oBody.clientWidth);
			}
			//An error is raised if the IFrame domain != its container's domain
			catch(e){window.status ='Error: ' + e.number + '; ' + e.description;}
		}
		//-->
	</SCRIPT>
</head>
<body >
	<jsp:include page="header_and_menu.jsp" />
					<iframe onload="timer()" src="<bean:write name="<%=BeanKeys.REPORT_URL%>" />" onresize="reSize()" id="ifrm"></iframe>
					<div><a href="<bean:write name="<%=BeanKeys.REPORT_URL%>" />">Форма для печати</a></div>
	<jsp:include flush="true" page="footer.jsp" />
</body>
</html>