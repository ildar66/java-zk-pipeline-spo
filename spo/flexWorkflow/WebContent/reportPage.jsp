<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ page import="org.uit.director.action.AbstractAction" %>
<%@ page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@ page import="org.uit.director.report.ComponentReport" %>
<%@ page import="org.uit.director.report.WorkflowReport" %>
<%@ page import="org.uit.director.report.mainreports.SearchProcessReport" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page pageEncoding="Cp1251" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@page import="java.util.ArrayList"%>
<html>
<head>
	<title>Отчёт</title>
	<style type="text/css">@import url( "resources/stylesheet.css" );</style>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<link rel="stylesheet" href="style/style.css" />
	<!-- Стили, которые дальше пойдут здесь быть не должно. Не отрывайте мне голову, я такого г... не мутил. Сейчас это единственный способ исправить стили. С уважением к любому кто это прочтёт, Сергей Полевич -->
	<style type="text/css">
		center {text-align:left;}
		center big {font-size:2.4em;color:#666;font-weight:bold;}
		div.tabledata table {border: solid #acf; border-width: 0 0 1px 1px; border-collapse: collapse; margin:2px;}
			div.tabledata table th {border: solid #9bd; border-width: 1px 1px 0 0; padding:3px; background-color: #fff; font-size:100%; color:#666; text-align:center; }
			div.tabledata table td {border: solid #acf; border-width: 1px 1px 0 0; padding:3px;}
	</style>
	<%
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	if (wsc.isNewContext()) {
		%> <script>document.location = "start.do";</script> <% return;
	}
	WorkflowReport reportInstance = wsc.getReport();
	List components = reportInstance.getComponentList();
	%>
	<script language="javascript" src="resources/cal2.js"></script>
	<script language="javascript" src="resources/cal_conf2.js"></script>
	<script language="javascript" src="resources/sort.js"></script>
	<script language="javascript" src="scripts/applicationScripts.js"></script>
	<script>
		//Это для календарика
		function popCalInFrame(dateCtrl) {
			var w=gfPop;
			//w.gbFixedPos=true;	// enable fixed positioning
			//w.gPosOffset=[70,0];	// set position
			w.fPopCalendar(dateCtrl);	// pop calendar
		}
	</script>
</head>
<body>
	<logic:notPresent scope="request" name="menuOff">
		<jsp:include page="header_and_menu.jsp" />
	</logic:notPresent>
				<%if (components != null) { %>
				<form name="mainform" action="report.do" method="POST">
				<%
					if (reportInstance instanceof SearchProcessReport) {
						if (((SearchProcessReport) reportInstance).isWithEdit()) {
							%><input type="hidden" name="par1" value="edit"><%
						}
					}
					%>
					<input type="hidden" name="genReport" value="true">
					<input type="hidden" name="classReport" value="<%=reportInstance.getClass().getName()%>">
					<h1><%=reportInstance.getNameReport()%></h1>
					<table>
						<%
						List updateComps = new ArrayList();
						for (int i = 0; i < components.size(); i++) {
							ComponentReport comp = (ComponentReport) components.get(i);
							String type = comp.getType();
							String nameView = comp.getDescription();
							String nameComp = nameView.replaceAll(" ", "_" ); 
							if (type.equalsIgnoreCase("string")) {
								%>
								<tr>
									<td><%=nameView%></td>
									<td><input type=text name="<%=nameComp%>" size=20 value="<%=comp.getValue()%>"></td>
								</tr>
								<%
							} else if (type.equalsIgnoreCase("select")) {
								%>
								<tr>
									<td><%=nameView%></td>
									<td>
										<select name="<%=nameComp%>"
											<%
											if (comp.getAddition() != null) {
												String secEl = ((String)comp.getAddition()).replaceAll(" ", "_");
												String[] namesUpdate = {nameComp, secEl};
												updateComps.add(namesUpdate);
												%> onchange="onCh(this, document['mainform'].<%=secEl%>)" <%
											}%>
										>
											<option>    </option>
											<%
											List complList = (List) comp.getValue();
											String sel = "";
											String param = request.getParameter(nameComp);
											if (param == null) param = "";
											if (complList != null) {
												for (int ii = 0; ii < complList.size(); ii++) {
													Map elem = (Map) complList.get(ii);
													String key = (String) elem.keySet().iterator().next();
													String val = (String) elem.get(key);
													if (key.equals(param)) sel = "selected";
													%>
													<option value="<%=key%>" <%=sel%>><%=val%> <%
												sel = "";
												}
											}
											%>
										</select>
									</td>
								</tr>
								<%
							} else if (type.equalsIgnoreCase("period")) {
									List complList = (List) comp.getValue();
								%>
								<tr>
									<td><%=nameView%></td>
									<td>
										<input name="leftDate"	value="<%=complList.get(0)%>"	validate="true" id="leftDate"	class="date"	onFocus="displayCalendarWrapper('leftDate', '', false); return false;"/>
										—
										<input name="rightDate"	value="<%=complList.get(1)%>"	validate="true"	id="rightDate"	class="date"	onFocus="displayCalendarWrapper('rightDate', '', false); return false;"/>
										
									</td>
								</tr>
								<%
							} else if (type.equalsIgnoreCase("check")) {
								%>
								<tr>
									<td><%=nameView%></td>
									<td><input type="checkbox" name="<%=nameComp%>"
										<% if (((Boolean)comp.getValue()).booleanValue()) {%> checked<%} %>></td>
								</tr>
								<%
							} else if (type.equalsIgnoreCase("script")) {
								%>
								<script>
									<%=comp.getValue()%>
									<%for (int up = 0; up < updateComps.size(); up++) {
										String[] updStr = (String[])updateComps.get(up);
										%>onCh(document['mainform'].<%=updStr[0]%>, document['mainform'].<%=updStr[1]%>);<%
									}%>
								</script>
								<%
							}
						}
						%>
						<tr>
							<td></td>
							<td><button type="submit">Найти</button></td>
						</tr>
					</table>
				</form>
				<%
				}
				if (reportInstance.isReportGenerate()) {
					String results = reportInstance.getReportHTML();
					results = results.replaceAll("<input type=\"submit\" value=\"Изменить\">", "");
					results = results.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать документ\" width=\"17px\" hight=\"17px\">", "");
					results = results.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать страницу\" width=\"17px\" hight=\"17px\">", "");
					results = results.replaceAll("<img src=\"resources/activity.plus.gif\" border=\"0\" alt=\"Добавить страницу\" width=\"17px\" hight=\"17px\">","");
					results = results.replaceAll("<img src=\"resources/activity.plus.gif\" border=\"0\" alt=\"Добавить документ\" width=\"17px\" hight=\"17px\">", "");
					%>
					<br>
					<%=results%>
					<a href="printVersion.do">Версия для печати</a>
					<!-- <center><a href="javascript:history.go(-1)">Назад</a></center>  -->
					<%
				}%>
	<logic:notPresent scope="request" name="menuOff">
		<jsp:include flush="true" page="footer.jsp" />
	</logic:notPresent>
	<!-- Это календарик: -->
	<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;"></iframe>
</body>
</html>
