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
<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Журнал прохождения заявки</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Журнал прохождения заявки</h1>
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" target="_blank">
					<input type="hidden" name="__report" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />
					<table class="form">
						<tr>
							<th>
								Номер заявки
							</th>
							<td>
								<input type="text" name="mdtask_number" size="31">
							</td>
						</tr>
						<tr>
							<th>
								Просроченные операции
							</th>
							<td>
								<input type="checkbox" name="isDelinquency" size="31">
							</td>
						</tr>
						<tr>
							<th>
								Статус операции
							</th>
							<td>
								<select name="operation_status">
									<option selected value="-1">Все операции</option>
									<option value="1">Поступившие</option>
									<option value="2">Активные (находящиеся в обработке)</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Формат отчета
							</th>
							<td>
								<select name="__format">
									<option selected value="html">Отчет в WEB</option>
									<option value="doc">Отчет в MS Word</option>
								</select>
							</td>
						</tr>
						<tr>
							<th /><td><button type="submit">Сформировать отчет</button></td>
						</tr>
					</table>
				</form>
<jsp:include flush="true" page="footer.jsp" />
</body>

<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
</html:html>