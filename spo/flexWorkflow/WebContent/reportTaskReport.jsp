<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="com.vtb.value.BeanKeys"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Отчёт по операциям</title>
	<link rel="stylesheet" href="style/style.css" />
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Отчёт по операциям</h1>
				<form action="reportPrintFormRenderAction.do" method="get" name="mainform" target="_blank">
					<table class="form">
						<tr>
							<th>
								Подразделение
							</th>
							<td>
								<select name="p_idDepartment" id="p_idDepartment" 
								WIDTH="450" STYLE="width: 450px"
								onchange="$('#<%=BeanKeys.REPORT_FILTER_SELECTDED_USER_ID%>').val('');$('#<%=BeanKeys.REPORT_FILTER_SELECTDED_USER%>').val('');">
										<logic:iterate id="d" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS%>" type="java.util.Map.Entry" scope="request">
											<option value="<bean:write name="d" property="key"/>"><bean:write name="d" property="value"/></option>
										</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>Пользователь</th>
							<td>
								<input type="hidden" name="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER_ID%>" id="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER_ID%>" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER_ID%>"/>"/>
								<input
								onclick="window.open('popup_users.jsp?reportmode=true&formName=mainform&fieldNames=userId|userFIO&department='+$('#p_idDepartment').val(), 'org','top=100, left=100, width=800, height=710');" 
								type="text" class="text" readonly="true" name="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER%>" id="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER%>"
								value="<bean:write name="<%=BeanKeys.REPORT_FILTER_SELECTDED_USER%>"/>">
							</td>
						</tr>
						<tr>
							<th>Тип отчёта</th>
							<td><select name="__report">
								<option value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE3%>" />">ожидающие обработки</option>
								<option selected value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />">операции в работе</option>
								<option value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE2%>" />">назначенные мне</option>
							</select></td>
						</tr>
						<tr>
							<th>Формат отчета</th>
							<td><select name="__format">
								<option selected value="html">Отчет в WEB</option>
								<option value="doc">Отчет в MS Word</option>
							</select></td>
						</tr>
						<tr>
							<th />
							<td><button type="submit">Сформировать отчет</button></td>
						</tr>
					</table>
				</form>
<jsp:include flush="true" page="footer.jsp" />
</body>

<iframe width=174 height=189 name="gToday:normal:agenda.js"
	id="gToday:normal:agenda.js"
	src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no"
	frameborder="0"
	style="visibility: visible; z-index: 999; position: absolute; left: -500px; top: 0px;">
</iframe>
</html:html>