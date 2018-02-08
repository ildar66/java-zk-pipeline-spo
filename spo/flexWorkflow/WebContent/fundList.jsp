<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="com.vtb.domain.TaskFund"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%
    TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
    Task task = processor.getTask(new Task(new Long(request.getParameter("mdtaskid"))));
%>
<table class="regular">
	<tr>
		<th>№</th>
		<th>КБ</th>
		<th>Сумма фондир.</th>
		<th>Вид</th>
		<th>Выдача</th>
		<th>Погашение</th>
		<th>Статус</th>
	</tr>
	<%
	    for (TaskFund fund : task.getFunds()) {
	%>
	<tr>
		<td><a target="_blank" href="/Funding/request/form/fundingrequest/VIEW/FUNDING_REQUEST-<%=fund.getId().toString()%>/reserved">
		<%=fund.getId().toString()%></a></td>
		<td><%=fund.getCorpBlock()%></td>
		<td class="number"><%=fund.getFormattedSum()%> <%=fund.getCurrencyCode()%></td>
		<td><%=fund.getProductName()%></td>
		<td><%=Formatter.format(fund.getPlannedDate())%></td>
		<td><%=Formatter.format(fund.getPlannedRepaymentDate())%></td>
		<td><%=fund.getStatus()%></td>
	</tr>
	<%
	    }
	%>
</table>