<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.vtb.domain.Report"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@ page import="ru.masterdm.spo.utils.*" %>
<%@ page import="ru.masterdm.spo.reporter.Reporter" %>
<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Отчёт</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Тестовая страница генератора отчётов</h1>
				<br/>
				<%=((Reporter)SBeanLocator.singleton().getBean("reporterImpl")).produceString()%>
				<br/>
				<form action="reporterAction.do" method="post" name="mainform" target="_blank">
                    <br/>
                    <button type="submit">Сформировать отчет</button>
				</form>
<jsp:include flush="true" page="footer.jsp" />
</body>

<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
</html:html>