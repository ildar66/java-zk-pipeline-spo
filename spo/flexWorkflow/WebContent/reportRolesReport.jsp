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
<%@page import="com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Переменные к операции</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
			<h1>Переменные к операции</h1>
			<table class="form">
				<th>
					Тип процесса
				</th>
				<td>
					<form action="reportRolesReport.do" method="get" name="processForm">
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
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" target="_blank">
					<input type="hidden" name="<%=ReportTemplateParams.REPORT_MARK.getValue()%>" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />			
					<input type="hidden" name="<%=ReportTemplateParams.PROCESS_ID.getValue()%>" value="<bean:write name="<%=BeanKeys.REPORT_CURRENT_PROCESS%>" />" />
						<tr>
							<th>
								Операции
							</th>
							<td>
								<select name="<%=ReportTemplateParams.STAGE_ID.getValue()%>">
									<logic:iterate id="s" name="<%=BeanKeys.REPORT_STAGES%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="s" property="key"/>"><bean:write name="s" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Формат отчета
							</th>
							<td>
								<select name="<%=ReportTemplateParams.REPORT_FORMAT.getValue()%>">
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

<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
<script>
	function doSubmit(dateCtrl) {
		document.processForm.submit();
	}
</script>
</html:html>