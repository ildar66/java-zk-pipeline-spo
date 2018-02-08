<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="java.util.Map"%>
<%@page import="com.vtb.domain.Report"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<link rel="stylesheet" href="style/style.css" />
	<title>Пользователи с определённой ролью</title>
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Пользователи с определенной ролью</h1>
				<table class="form">
					<tr>
						<th>
							Тип процесса
						</th>
						<td>
							<form action="reportUsersByReport.do" method="get" name="processForm">	
								<select name="<%=BeanKeys.REPORT_CURRENT_PROCESS%>" onchange="javascript: doSubmit()">
									<logic:iterate id="p" name="<%=BeanKeys.REPORT_PROCESSES%>" type="java.util.Map.Entry" scope="request">
									
										<% if (p.getKey().equals(request.getAttribute(BeanKeys.REPORT_CURRENT_PROCESS))) {
										%>
											<option selected value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
										<% } else { %>
											<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
										<% } %>	
									</logic:iterate>
								</select>
							</form>
						</td>
					</tr>
					<form action = "/compendium/reportRoles.roles_report" metod="post" target="_blank">
						<input type="hidden" name="processId" value="<bean:write name="<%=BeanKeys.REPORT_CURRENT_PROCESS%>" />" />
						<tr>
							<th>
								Подразделение
							</th>
							<td>
								<select name="curDep" WIDTH="450" STYLE="width: 450px">
									<logic:iterate id="d" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="d" property="key"/>"><bean:write name="d" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Роль
							</th>
							<td>
								<select name="selectedRole">
								<logic:iterate id="r" name="<%=BeanKeys.REPORT_ROLES%>" type="java.util.Map.Entry" scope="request">
									<option value="<bean:write name="r" property="key"/>"><bean:write name="r" property="value"/></option>
								</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Формат отчёта
							</th>
							<td>
								<select name="format">
									<option selected value="html">Отчет в WEB</option>
									<option value="doc">Отчет в MS Word</option>
								</select>
							</td>
						</tr>
						
						<tr>
							<th /><td><button type="submit">Сформировать отчет</button></td>
						</tr>
					</form>
				</table>
<jsp:include flush="true" page="footer.jsp" />
</body>
<script>
	function doSubmit(dateCtrl) {
		document.processForm.submit();
	}
</script>
</html>