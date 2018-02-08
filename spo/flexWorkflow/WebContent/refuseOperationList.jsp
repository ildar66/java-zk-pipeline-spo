<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.helper.TaskPage" %>
<%@page import="ru.md.spo.loader.TaskLine" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.ProcessSearchParam"%>
<%@page import="ru.masterdm.compendium.domain.crm.Organization"%>
<%@page import="ru.masterdm.compendium.value.Page"%>
<%@page import="ru.md.spo.util.Config"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.Date"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.logging.*"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Отзыв операции, находящейся в работе у исполнителя</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
<script language="javaScript">
    var a=false;
    function showSearch() {
        document.getElementById('fullSearchForm').style.display = (document.getElementById('fullSearchForm').style.display == 'none' ? '' : 'none');
        a=~a;
    }
    dialogArray = new Array();	//дочерние окна
    function openDialog(hrefStr, name, prop){
        var wnd = window.open(hrefStr, name, prop);
        dialogArray[dialogArray.length]=wnd;
        wnd.focus();
        return false;
    }
</script>
				<h1>Отзыв операции, находящейся в работе у исполнителя</h1>

<form id="processGrid" name="processGrid" action="refuseOperationList.do" method="post">
	<input type="hidden" id="department" name="department" value="-1">
	<input type="hidden" id="p_idDepartment" name="p_idDepartment" value="-1">
	
<%Logger logger = Logger.getLogger("refuseOperationList_jsp");
	try {
	String dialogProperty = "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes";
	logger.info("start refuseOperationList_jsp");
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		TaskPage taskpage = TaskHelper.getTaskPage(request);
		String state = "";
		//пишем в сессию все зараметры со значениями
		StringBuffer strParams = new StringBuffer();
		Enumeration params = request.getParameterNames();
		String param = "";
		while (params.hasMoreElements()) {
			param = (String) params.nextElement();
			strParams.append(param).append("=").append(request.getParameter(param)).append("&");
		}
		request.getSession().setAttribute("refuseOperationList", strParams.toString());
		String nameCert = Config.getProperty("NAME_SIGN_CENTER");
		nameCert = new String(nameCert.getBytes("ISO-8859-1"));
		long countOfApplication = 0;
		CompendiumActionProcessor compendium = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		CompendiumCrmActionProcessor compendiumcrm =(CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
		ProcessSearchParam processSearchParam = new  ProcessSearchParam(request,request.getParameter("closed")!=null);
%>

<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<div id="spisokZayavok">
<table class="regular">
		<thead>
			<tr>
				<th><a href="#" onClick="showSearch()">поиск</a></th>
				<th>Исполнитель</th>
				<th title="Номер заявки">№</th>
				<th style="width: 20em;">Контрагент</th>
				<th>Сумма</th>
				<th title="Валюта">Вал.</th>
				<th title="Тип заявки">Тип</th>
				<th title="Приоритет">Приор.</th>
				<th>Статус</th>
				<th>Текущая операция</th>
				<th>Дата взятия<br>в работу</th>
				<th>Инициирующее подразделение</th>
				<th>Тип процесса</th>
			</tr>
			<tr id="fullSearchForm" <%=processSearchParam.showFilter()?"":"style=\"display:none\"" %>>
				<td>
					<button type="submit">найти</button>
					<button onclick="clearForm(this.form);submit();" name="clear" class="button clear">очистить </button>
				</td>
				<td>
					<input type="hidden" name="searchExecutorId" value="<%=processSearchParam.getExecutorId()==null?"":processSearchParam.getExecutorId() %>"/>
					<input
                        onclick="window.open('popup_users.jsp?reportmode=true&formName=processGrid&fieldNameIdUser=searchExecutorId&fieldNames=searchExecutorId|searchExecutor&department='+$('#p_idDepartment').val(), 'org','top=100, left=100, width=800, height=710');" 
                        type="text" class="text" readonly="readonly" name="searchExecutor" 
                        value="<%=processSearchParam.getExecutor()==null?"":processSearchParam.getExecutor() %>" />
				</td>
				<td><input name="searchNumber" type="text" style="width:10em;" 
					value="<%=processSearchParam.getNumber()==null?"":processSearchParam.getNumber() %>"></td>
				<td><input name="searchContractor" type="hidden"
					value="<%=processSearchParam.getContractor()==null?"":processSearchParam.getContractor() %>">
					<input type="hidden" id="SPOcontractorID" name="IDSPO_Contractors0" value=""/>
					<%String orgname="не выбрано";
                    if(processSearchParam.getContractor()!=null){
                            Page p = compendiumcrm.findOrganizationPage(new Organization(true,processSearchParam.getContractor()),0,1,null);//найти из бина
                            if (p.getSize()==1){
                                orgname = ((Organization)p.getList().get(0)).getOrganizationName();
                                orgname = orgname.replaceAll("\"","&quot;");
                            }
                    }
                     %>
					<input href="popup_org.jsp?formName=processGrid&ek=only&fieldNames=IDSPO_Contractors0|selectedName|searchContractor" onclick='return openDialog(this.href, "organizationLookupList", "<%= dialogProperty%>");' 
					  type="text" class="text" readonly="true" name="selectedName" value="<%=orgname %>" />
				</td>
				<td>
					от<input name="searchSumFrom" type="text"
                    value="<%=Formatter.format(processSearchParam.getSumFrom()) %>">
                    <br />до<input name="searchSumTo" type="text"
                    value="<%=Formatter.format(processSearchParam.getSumTo()) %>"></td>
				<td><select name="searchCurrency">
						<option value="all"></option>
						<% ru.masterdm.compendium.domain.Currency[] currencyList = compendiumcrm.findCurrencyList("%",null);
						for(ru.masterdm.compendium.domain.Currency currency : currencyList){ %>
						<option value="<%=currency.getCode() %>"
						<%=(processSearchParam.getCurrency()!=null&&processSearchParam.getCurrency().equals(currency.getCode()))?"selected":"" %>
						><%=currency.getCode() %></option>
						<%} %>
					</select
				></td>
				<td><input name="searchType" type="text"
					value="<%=processSearchParam.getType()==null?"":processSearchParam.getType() %>"></td>
				<td><select name="searchPriority">
						<option value="all"></option>
						<option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("высокий"))?"selected":"" %> value="высокий">высокий</option>
						<option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("средний"))?"selected":"" %> value="средний">средний</option>
						<option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("низкий"))?"selected":"" %> value="низкий">низкий</option>
					</select
				></td>
				<td><input name="searchStatus" type="text"
                    value="<%=processSearchParam.getStatus()==null?"":processSearchParam.getStatus() %>"></td>
				<td><input name="searchCurrOperation" type="text"
					value="<%=processSearchParam.getCurrOperation()==null?"":processSearchParam.getCurrOperation() %>"></td>
				<td></td>
				<td><input name="searchInitDepartment" type="text"
					value="<%=processSearchParam.getInitDepartment()==null?"":processSearchParam.getInitDepartment() %>"></td>
				<td><select name="searchProcessType">
						<option value="all"></option>
						<% ru.masterdm.compendium.domain.ProcessType[] processTypeList = compendium.getProcessTypeList(); 
						for(ru.masterdm.compendium.domain.ProcessType processType : processTypeList){ %>
							<option value="<%=processType.getId().toString() %>"
							<%=(processSearchParam.getProcessTypeID()!=null&&processSearchParam.getProcessTypeID().equals(processType.getId()))?"selected":"" %>
							><%=processType.getDescription() %></option>
						<%} %>
					</select
				></td>
			</tr>
		</thead>

		<!-- тело таблицы -->
		<tbody>
<div class="paging">
<%String navigation = request.getParameter("navigation");
Long processesOnPage = Long.valueOf(ru.md.spo.util.Config.getProperty("PROCESSES_ON_PAGE"));
Long curr = taskpage.getCurr();
Long right = processesOnPage * (curr+1);
if(right > taskpage.getCount()) {right = taskpage.getCount();}
%>
Всего: <%=taskpage.getCount() %>, 
<%if(curr>0){ %><a class="button" onClick="$('#navigation').val('<%=(curr-1) %>');$('#processGrid').submit()" href="#">&larr;</a><%} %>
<%if(taskpage.getCount()>0){ %><%=(curr * processesOnPage + 1 + " &#150; " + right) %><%} %>
<%if((curr+1)*processesOnPage < taskpage.getCount()){ %><a class="button" onClick="$('#navigation').val('<%=(curr+1) %>');$('#processGrid').submit()" href="#">&rarr;</a><%} %>
</div>
			<input type="hidden" id="navigation" name="navigation" value=""/>
			<%
			// Цикл по всем операциям (заявкам), попадающим под условия фильтрации.
			// Обработка каждой заявки
			for (int i = 0; i < taskpage.getTaskLineList().size(); i++) {
				TaskLine taskLine=(TaskLine)taskpage.getTaskLineList().get(i);
				String id=taskLine.getIdMDTask().toString();
				int stroc = 0;
				countOfApplication++;
			%>
			<tr class="<%=taskLine.getTrClass()%>">
				<td>
					<a href="task.accept.do?isAccept=0&id0=<%=taskLine.getIdTask()%>&target=refuseOperationListFromAcceptAction">
						<img src="style/not_take.png" alt="отказаться"
					></a>
					<a target="_blank" href="<%=taskLine.getActiveStageUrl()%>" title="Посмотреть активные операции"
						><img src="style/in_progress.png" alt="Активные операции"
					></a>
					<a href="report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=<%=taskLine.getIdProcess()%>&menuOff=1" target="_blank" title="Посмотреть хронологию выполнения операций по этой заявке"
						><img src="style/time.png" alt="хронология"
					></a>
					<a target="_blank"
						href="plugin.action.do?class=<%=ViewProcessWrapper.class.getName()%>&idProcess=<%=taskLine.getIdProcess()%>"
						title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"
					></a>
				</td>
				<td title="Исполнитель текущей операции">
						<%=taskLine.getNameIspoln() %>
				</td>
				<td>
					<a href="<%=taskLine.getUrl() %>" title="Посмотреть заявку"><%=taskLine.getNumberZ()%></a>
				</td>
				<td title="Контрагент"><%=taskLine.getContractors()%></td>
				<td title="Сумма лимита" class="number"><%=Formatter.format(taskLine.getSumOrig())%></td>
				<td title="валюта лимита"><%=taskLine.getCurrency() %></td>
				<td title="Тип заявки"><%=taskLine.getProcessType()%></td>
				<td title="Приоритет"><%=taskLine.getPriority()%></td>
				<td title="Статус"><%=taskLine.getStatus()%></td>
				<td 
					title="Сейчас над заявкой выполняется эта операция"><a
					target="_blank"
					href="plugin.action.do?class=<%=ViewProcessWrapper.class.getName()%>&idProcess=<%=taskLine.getIdProcess()%>"
					title="Посмотреть путь заявки по всем операциям"><%=taskLine.getNameStageTo()%></a>
				</td>
				<td	title="Дата взятия операции в работу">
					<%=taskLine.getDateOfTakingStr()%>
				</td>
				<td><%=taskLine.getDepartment()%></td>
				<td><%=taskLine.getDescriptionProcess()%></td>
			</tr>
			<tr>
				<td style="display: none;"><input type="hidden" name="<%="user" + i%>" value="<%=taskLine.getIdUser()%>" id="user">
					<input type="hidden" name="<%="data" + i%>" value="<%=WPC.getInstance().dateFormat.format(new Date())%>" id="data"> <input type="hidden" name="<%="id" + i%>" value="<%=taskLine.getIdTask()%>" id="id"> <input type="hidden" name="<%="idProc" + i%>" value="<%=taskLine.getIdProcess()%>" id="idProc">
					<input type="hidden" name="<%="sign" + i%>" value="" id="sign">
				</td>
			</tr>
			<%
			}//конец цикла по заявкам
			%>
			<caption><input type="hidden" name="isAccept" /></caption>
		</tbody>
	</table>
</div>
<%
	} catch (Exception e) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    pw.write("Ошибка на странице TaskTable.jsp:" + e.getMessage()+"<br /><a class=\"supply\" href=\"#StackTrace\">Код ошибки<a><br />");
	    pw.write("<div style=\"display:none\"><div id=\"StackTrace\">");
	    e.printStackTrace(pw);
	    pw.write("</div></div>");
	    out.println(sw.toString());
		logger.log(Level.SEVERE, e.getMessage(), e);
		e.printStackTrace();
	}
%>
</form>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>