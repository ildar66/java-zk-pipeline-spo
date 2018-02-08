<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.util.Formatter"%>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");

Task task=TaskHelper.findTask(request);
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
boolean readOnly =!TaskHelper.isEditMode("Общие условия",request); 
%>
<script type="text/javascript" src="scripts/compareApproved.js"></script>


<script>
	$(function() {
		$( "#condition_tabs" ).tabs();
		restoreTab('condition_tabs');
	});
	$(document).ready(function() {
		loadCompareResult('conditions');
	});
</script>
<div id="condition_tabs">
	<ul>
		<li><a href="#tabs-0" onclick="storeTab('condition_tabs',0)">Условия</a></li>
		<li><a href="#tabs-1" onclick="storeTab('condition_tabs',1)">Условия досрочного погашения</a></li>
		<li><a href="#tabs-2" onclick="storeTab('condition_tabs',2)">Дополнительные/Отлагательные/Индивидуальные и прочие условия</a></li>
	</ul>
	<div id="tabs-0"><%if(task.isLimit()){ %>
		<div id="compare_conditions_generalcondition">
			Общие условия лимита<br />
			<%if(!readOnly){ %>
			<textarea rows="5" cols="15" name="generalcondition" onkeyup="fieldChanged(this)" style="width: 99%;"><%=taskJPA.getGeneralcondition() %></textarea>
			<%}else{ %><%=taskJPA.getGeneralcondition() %><%} %>
		</div>
		<%} %>
		<%if(task.isOpportunity()){ %>
		<div id="compare_conditions_changedConditions">
			<div>Измененные и дополненные условия</div>
			<% if (readOnly) { %>
			<div>
				<%=Formatter.str(task.getMain().getChangedConditions()) %>
			</div>
			<% } else { %>
			<textarea rows="5" cols="15" onkeyup="fieldChanged(this)" name="changedConditions" %><%=Formatter.str(task.getMain().getChangedConditions()) %></textarea>
			<% } %>
		</div>
		<%} %>
		<div id="compare_conditions_definition">
			Определения<br />
			<%if(!readOnly){ %>
			<textarea rows="5" cols="15" name="definition" onkeyup="fieldChanged(this)"><%=taskJPA.getDefinition() %></textarea>
			<%}else{ %><%=taskJPA.getDefinition() %><%} %>
		</div></div>
	<div id="tabs-1"><jsp:include flush="true" page="frame_earlyPaymentCondition.jsp"/></div>
	<div id="tabs-2"><jsp:include flush="true" page="frame_otherCondition.jsp"/></div>
</div>
