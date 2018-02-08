<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page import="ru.md.domain.percenthistory.DealPercentHistory"%>
<%@page import="ru.md.domain.percenthistory.FactPercentHistory"%>
<%@page import="ru.md.domain.percenthistory.IndrateHistory"%>
<%@page import="ru.md.domain.User"%>
<%@page import="java.util.Date"%>
<%@page import="ru.md.domain.Department"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<% long tstart=System.currentTimeMillis();%>
<html:html>
<head>
<% 
String creditDealNumberStr = request.getParameter("creditDealNumber");
if (creditDealNumberStr == null || creditDealNumberStr.trim().isEmpty())
	throw new Exception("creditDealNumber is empty");
Long creditDealNumber = Long.parseLong(creditDealNumberStr);

List<User> filterUsers = SBeanLocator.singleton().mdTaskMapper().getDealPercentHistoryUsers(creditDealNumber);
List<Department> filterDepartments = SBeanLocator.singleton().mdTaskMapper().getDealPercentHistoryDepartments(creditDealNumber);

Date filterStartDate = Formatter.parseDate(request.getParameter("filterStartDate")); 
Date filterEndDate = Formatter.parseDate(request.getParameter("filterEndDate")); 

String filterChoiceType = request.getParameter("filterChoiceType");
if (filterChoiceType == null)
	filterChoiceType = "";
	
Long filterPerformerId = filterChoiceType.equals("performerZone") ? Formatter.parseLong(request.getParameter("filterPerformerId")) : null;
Long filterDepartmentId = filterChoiceType.equals("departmentZone") ? Formatter.parseLong(request.getParameter("filterDepartmentId")) : null;

List<DealPercentHistory> dealPercentHistories = SBeanLocator.singleton().mdTaskMapper().getDealPercentHistories(creditDealNumber, filterStartDate, filterEndDate, filterPerformerId, filterDepartmentId, false);
%>
	<title>История изменения ставки по сделке №<%=creditDealNumber%></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
	<link rel="stylesheet" href="style/style.css" />
	<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
	
    <link type="text/css" rel="stylesheet" href="/compendium/calendar/dhtmlgoodies_calendar.css">
    <script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>	
</head>
<body>

<div class="deal_percent_history_report">
<form action="" id="dealPercent">
	<h1>История изменения ставки по сделке №<%=creditDealNumber%></h1>

	Дата изменения ставки с 
	<md:input name="filterStartDate" 
		id="filterStartDate" 
		value="<%=Formatter.format(filterStartDate) %>"
		readonly="false"
		onFocus="displayCalendarWrapper('filterStartDate', '', false); return false;"
		onChange="input_autochange(this,'date');"
		styleClass="text date"/>
	по
	<md:input name="filterEndDate" 
		id="filterEndDate" 
		value="<%=Formatter.format(filterEndDate) %>"
		readonly="false"
		onFocus="displayCalendarWrapper('filterEndDate', '', false); return false;"
		onChange="input_autochange(this,'date');"
		styleClass="text date"/>
		
	<br/>
	
	Применяется к 
<select name="filterApplyTo">
    <option value="">Сделка</option>
</select>
	<br/>
	
	По исполнителям 
   <span class="filter_choice">
	   <input 
		   	onclick="document.getElementById('departmentZone').style.display = 'none'; document.getElementById('performerZone').style.display = 'none';"
		   	type="radio" name="filterChoiceType" value="" <%= filterChoiceType.equals("") ? "checked='checked'" : ""%>> не учитывать</span>
   <span class="filter_choice">
   		<input 
	   		onclick="document.getElementById('departmentZone').style.display = ''; document.getElementById('performerZone').style.display = 'none';"
	   		type="radio" name="filterChoiceType" value="departmentZone" <%= filterChoiceType.equals("departmentZone") ? "checked='checked'" : ""%> > Подразделение</span>
   <span class="filter_choice">
   		<input 
	   		onclick="document.getElementById('departmentZone').style.display = 'none'; document.getElementById('performerZone').style.display = '';"
	   		type="radio" name="filterChoiceType" value="performerZone" <%= filterChoiceType.equals("performerZone") ? "checked='checked'" : ""%> > Пользователь</span>
 
<span id="departmentZone" style="display:<%= filterChoiceType.equals("departmentZone") ? "" : "none"%>;">
   <br/>
	Подразделение 
	<select name="filterDepartmentId">
	    <option value="" <%=filterDepartmentId == null ? "selected='selected'" : "" %> >-все-</option>
	<% for(Department department: filterDepartments) { %>
	    <option value="<%=department.getId()%>" <%=filterDepartmentId != null && filterDepartmentId.equals(department.getId()) ? "selected='selected'" : "" %>><%=department.getName()%></option>
	<% } %>
	</select>	
</span>

<span id="performerZone" style="display:<%= filterChoiceType.equals("performerZone") ? "" : "none"%>;">
	<br/>
	Пользователь 
	<select name="filterPerformerId">
	    <option value="" selected="selected" <%=filterPerformerId == null ? "selected='selected'" : "" %>>-все-</option>
	<% for(User user: filterUsers) { %>
	    <option value="<%=user.getId()%>" <%=filterPerformerId != null && filterPerformerId.equals(user.getId()) ? "selected='selected'" : "" %> ><%=user.getFullName()%></option>
	<% } %>
	</select>
</span>

	<div>
		<button class="button">Найти</button>
		<a href="dealPercentHistoryReport.jsp?creditDealNumber=<%=request.getParameter("creditDealNumber")%>">Очистить форму поиска</a>
	</div>
	
	<table class="regular percent_history">
		<tr>
			<th rowspan="2">№ <span class="number">п/п</span></th>
			<th rowspan="2">Применяется к</th>
			
			<th rowspan="2">Ставка</th>
			<th rowspan="2">Значение ставки, % годовых</th>
			<th rowspan="2">Надбавка к индикативной ставке/маржа</th>
			<th colspan="2">Период применения</th>
			<th rowspan="2">Основание</th>
			
			<th rowspan="2">ФИО</th>
			<th rowspan="2">Подразделение</th>
			<th rowspan="2">Дата изменения ставки</th>
		</tr>
		<tr>
			<th>с</th>
			<th>по</th>
		</tr>		
<%
Long statusIndex = 0L;
if (dealPercentHistories != null)
	for (DealPercentHistory dealPercentHistory : dealPercentHistories) {
		Long factPercentStatusIndex = 0L;
		if (dealPercentHistory.getPercentHistories() != null)
			for (FactPercentHistory percentHistory : dealPercentHistory.getPercentHistories()) {
				Long indrateStatusIndex = 0L;
				if (percentHistory.getIndrateHistories() != null)
					for (IndrateHistory indrateHistory : percentHistory.getIndrateHistories()) {
%>

		<tr>
		<% if (factPercentStatusIndex == 0 && indrateStatusIndex == 0) { %>
			<td rowspan="<%=dealPercentHistory.getIndrateHistoryCount()%>"><%=dealPercentHistory.getChangeNumber()%>.</td>
		<% } %>

		<% if (indrateStatusIndex == 0) { %>
			<td rowspan="<%=percentHistory.getIndrateHistoryCount()%>">
				<% if (percentHistory.getPeriodNumber() == null) { %>	
					Сделка
				<% } %>
				<% if (percentHistory.getPeriodNumber() != null) { %>	
					Период №<%=percentHistory.getPeriodNumber()%>
				<% } %>
			
			<% if (percentHistory.getPeriodNumber() != null) { %>
				<span class="period">
					<%=Formatter.format(percentHistory.getStartDate())%>-<%=Formatter.format(percentHistory.getEndDate())%>
				</span>
			<% } %>
			</td>
		<% } %>
			
			<td><%=indrateHistory.getRateType() != null ? indrateHistory.getRateType() : ""%></td>
			<td>
				<span class="rate4_description">
					<span class="money"><%=Formatter.format(indrateHistory.getValue())%><%=(indrateHistory.getValueComment() != null && !indrateHistory.getValueComment().isEmpty()) ? "." : "" %>
				</span>
				<span class="rate4_description"><%=indrateHistory.getValueComment() != null ? indrateHistory.getValueComment() : ""%></span>
			</td>
			<td>
				<span class="money"><%=indrateHistory.getAdditionValue() != null ? indrateHistory.getAdditionValue() : ""%></span>
			</td>
			<td><%=Formatter.format(indrateHistory.getStartDate())%></td>
			<td><%=Formatter.format(indrateHistory.getEndDate())%></td>
			<td><%=indrateHistory.getReason() != null ? indrateHistory.getReason() : ""%></td>
			
		<% if (factPercentStatusIndex == 0 && indrateStatusIndex == 0) { %>
			<td rowspan="<%=dealPercentHistory.getIndrateHistoryCount()%>"><%=dealPercentHistory.getChangeUserFullName()!=null ? dealPercentHistory.getChangeUserFullName() : ""%></td>
			<td rowspan="<%=dealPercentHistory.getIndrateHistoryCount()%>"><%=dealPercentHistory.getDepartmentName()!=null ? dealPercentHistory.getDepartmentName() : ""%></td>
			<td rowspan="<%=dealPercentHistory.getIndrateHistoryCount()%>"><%=Formatter.format(dealPercentHistory.getChangeDate())%></td>
		<% } %>
		</tr>
		
<% 
				indrateStatusIndex++; 
			}
			factPercentStatusIndex++; 
		}
		statusIndex++; 
	}
%>
		</table>	
		
	<input type="hidden" name="creditDealNumber" value="<%=creditDealNumber%>" />	
</form>
</div>
</body>
</html:html>