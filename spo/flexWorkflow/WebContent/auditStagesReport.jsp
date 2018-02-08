<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="com.vtb.util.Formatter"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Аудит прохождения этапов</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Аудит прохождения этапов</h1>
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" target="_blank">
					<input type="hidden" name="__report" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />
					<input type="hidden" name="__format" value="excel" />
					<table class="form">
						<tr>
							<th>
								Тип процесса
							</th>
							<td>
								<select name="<%=BeanKeys.REPORT_CURRENT_PROCESS%>">
							<logic:iterate id="p" name="<%=BeanKeys.REPORT_PROCESSES%>" type="java.util.Map.Entry" scope="request">
								<% if (p.getValue().equals("Крупный бизнес ГО")) {
								%>
									<option selected value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
								<% } else { %>
									<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
								<% } %>	
							</logic:iterate>
								</select>
							</td>
						</tr>
						<tr><th>Временной период</th><td>c <input type="text" class="text date" id="sendLeftDate" name="sendLeftDate" 
    value="<%=Formatter.format(new java.util.Date()) %>"
    onFocus="displayCalendarWrapper('sendLeftDate', '', false); return false;" />
    по <input type="text" class="text date" id="sendRightDate" name="sendRightDate" 
    value="<%=Formatter.format(new java.util.Date()) %>" 
    onFocus="displayCalendarWrapper('sendRightDate', '', false); return false;" /></td></tr>
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