<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page pageEncoding="Cp1251"%>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@page import="java.util.List"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="org.uit.director.db.dbobjects.WorkflowTypeProcess"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<html:html xhtml="true">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Добавление специальных отчётов</title>
	<style type="text/css">@import url( "resources/stylesheet.css" );</style>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<script>
    function submitUserForm() {
        usersform.submit();
    }
</script>

	<jsp:include page="header_and_menu.jsp" />
	<h1>Добавление специальных отчетов</h1>
	<form action="addSpetialReport.do" method="post">
		<table border="0" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<td>Тип процесса</td>
					<td>
						<select name="typeProc">
					<option>-</option>
					
					<%		
					WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
					List typeProcList = WPC.getInstance().getTypeProcessesList(wsc.getIdUser());
					for (int i = 0; i < typeProcList.size(); i++) {
						WorkflowTypeProcess typeProcess = (WorkflowTypeProcess) typeProcList.get(i);
						long idTP = typeProcess.getIdTypeProcess();		
					%><option value="<%=idTP%>"><%=typeProcess.getNameTypeProcess()%></option><%
							}
							%>
				</select>
					</td>
				</tr>
				<tr>
					<td width="140">Имя отчета</td>
					<td width="459">
					<input type="text" name="nameReport" size="25">
					</td>
				</tr>
				<tr>
					<td width="140">Класс отчета</td>
					<td width="459">
					<input type="text" name="classReport" size="70">
					</td>
				</tr>
				<tr>
					<td width="140" height="41">Описание отчета</td>
					<td height="41" width="459"><textarea rows="2" cols="55"
						name="descriptionReport"></textarea></td>
				</tr>
				<tr>
					<td width="140" colspan="2" height="17">
					<%if (wsc.isAdmin()) { %>
					<input type="submit"
						name="addReport" value="Добавить">
						<%} else { %>
						<p style="font-style: italic;">Недоступно</p>
						<%} %>
						</td>
				</tr>
			</tbody>
		</table>
	</form>
	<jsp:include flush="true" page="footer.jsp" />
</body>
</html:html>