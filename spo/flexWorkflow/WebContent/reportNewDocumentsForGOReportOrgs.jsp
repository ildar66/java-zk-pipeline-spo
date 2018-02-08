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
	<title>Перечень недоставленных документов по контрагентам</title>
	<link rel="stylesheet" href="style/style.css" />
	<script language="javascript" src="resources/cal2.js"></script>
	<script language="javascript" src="resources/cal_conf2.js"></script>
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Перечень недоставленных документов по контрагентам</h1>
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" target="_blank">
					<input type="hidden" name="__report" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />
					<table class="form">
						<tr>
							<th>
								Дата 
							</th>
							<td>
								c <input type="text" class="text date" name="leftDate" id="leftDate" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_LEFT_DATE%>" />" onFocus="displayCalendarWrapper('leftDate', '', false); return false;"/>
								по <input type="text" class="text date" name="rightDate" id="rightDate" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_RIGHT_DATE%>" />" onFocus="displayCalendarWrapper('rightDate', '', false); return false;" />
							</td>
						</tr>
						<tr>
						<th>
							Подразделение
						</th>
							<td>
								<select name="departmentId" WIDTH="450" STYLE="width: 450px">
										<logic:iterate id="d" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS%>" type="java.util.Map.Entry" scope="request">
											<option value="<bean:write name="d" property="key"/>"><bean:write name="d" property="value"/></option>
										</logic:iterate>
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
<script>
	//Это для календарика
	function popCalInFrame(dateCtrl) {
		var w = gfPop;
		w.fPopCalendar(dateCtrl);	// pop calendar
	}
	
	function showhide ()
	{
		var style = document.getElementById("sendDate").style
		if (style.display == "none")
			style.display = "";
		else {
			style.display = "none";
			document.getElementById("sendLeftDate").value = "";
		}
	}

</script>
</html:html>