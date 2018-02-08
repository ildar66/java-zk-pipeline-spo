<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.vtb.domain.Report"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="org.uit.director.db.dbobjects.WorkflowDepartament"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Иерархия ролей</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
			<h1>Иерархия ролей</h1>
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" target="_blank">
					<input type="hidden" name="__report" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />
						<table class="form">
						<tr>
							<th>
								Тип процесса
							</th>
							<td>
								<select name="processId">
									<logic:iterate id="p" name="<%=BeanKeys.REPORT_PROCESSES%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Подразделение
							</th>
							<td>
								<select name="p_idDepartment" WIDTH="450" STYLE="width: 450px">
									<logic:iterate id="p" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Показывать роли:
							</th>
							<td>
								<input CHECKED type="radio" name="showUnactive" value="0">только активные роли указанного процесса</input>
								<input type="radio" name="showUnactive" value="1">все роли указанного процесса</input>
							</td>
						</tr>
						<tr>
							<th>
								Формат отчёта
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
						<%  
						    StringBuilder sb = new StringBuilder();
					        Iterator it = ((Map)request.getAttribute(BeanKeys.REPORT_FILTER_DEPARMENTS)).keySet().iterator();
					        while (it.hasNext()) {
					            Object str = it.next();
					            sb.append(str  + " "); 
					        }
					        String toField = sb.toString();						
						 %>
						 <input type="hidden" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS + "sss"%>" value="<%=toField%>">						
				</table>
				</form>
<jsp:include flush="true" page="footer.jsp" />
</body>

<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
</html:html>