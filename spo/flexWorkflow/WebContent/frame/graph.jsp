<%@page import="ru.md.helper.TaskHelper"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
boolean readOnly = !TaskHelper.isEditMode("R_Стоимостные условия",request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
try{
if (!readOnly || isCanEditFund) { %><input type="hidden" name="graph_section" value="YES" /><% } %>
<script>
	$(function() {
		$( "#graph_tabs" ).tabs();
		restoreTab('graph_tabs');
		dialogHandler();
	});
</script>
<div id="graph_tabs" style="width: 99%; padding: 0;">
	<ul>
		<%if(task.isOpportunity()){ %><li><a href="#tabs-1" onclick="storeTab('graph_tabs',0)">Погашение основного долга</a></li><%} %>
		<li><a href="#tabs-2" onclick="storeTab('graph_tabs',1)">График платежей</a></li>
		<%if(task.isOpportunity()){ %><li><a href="#tabs-3" onclick="storeTab('graph_tabs',2)">График погашения процентов</a></li><%} %>
	</ul>
	<%if(task.isOpportunity()){ %><div id="tabs-1">
    	<jsp:include flush="true" page="graphPrincipalPayments.jsp"/>
	</div><%} %>
	<div id="tabs-2">
		<jsp:include flush="true" page="graphPayments.jsp" />
	</div>
	<%if(task.isOpportunity()){ %><div id="tabs-3">
	    <jsp:include flush="true" page="graphPercentPayments.jsp"/>
	</div><%} %>
</div>
<%}catch (Exception e) {out.println("Ошибка в секции  frame_priceCondition.jsp:" + e.getMessage());e.printStackTrace();} %>
