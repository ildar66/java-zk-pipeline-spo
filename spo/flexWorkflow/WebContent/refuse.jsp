<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.model.ActionProcessorFactory" %>
<%@page import="com.vtb.model.TaskActionProcessor" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.TaskContractor" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.domain.crm.Organization"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="ru.masterdm.compendium.value.Page"%>
<%@page import="ru.md.spo.util.Config"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper"%>
<%@page import="com.vtb.domain.ProcessSearchParam"%>
<%@page import="java.util.logging.Logger"%>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Отказ Клиента от заявки</title>
	<link rel="stylesheet" href="style/style.css" />
	<script language="JavaScript" src="scripts/applicationScripts.js"></script>
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
<%
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	ProcessSearchParam processSearchParam = new ProcessSearchParam(request,false);
	String dialogProperty = "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes";
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	CompendiumCrmActionProcessor compendiumcrm =(CompendiumCrmActionProcessor) 
		ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
	CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
		ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
	String pagenum=request.getParameter("page");
	Long currentpage =1l;
	try{
		currentpage = Long.parseLong(pagenum);
	}catch(NumberFormatException nfe){
		currentpage = 1l;}
	Long pageSize = Long.parseLong(Config.getProperty("PROCESSES_ON_PAGE"));
	Page p = processor.findRefusableTask(wsc.getIdUser(),
		(currentpage -1)*pageSize+1,pageSize,processSearchParam);
	Long pagecount = p.getTotalCount()==0?0:((p.getTotalCount()-1)/pageSize)+1;
	String navigation="<div class=\"paging\">Всего заявок: "+String.valueOf(p.getTotalCount())+
		"; страниц: "+String.valueOf(pagecount)+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	if (currentpage != 1) {
        	navigation += "<a onClick=\"document.getElementById('page').value='1';$('#processGrid').submit()\" href=\"#\">Первая</a> &nbsp;";
        	navigation += "<a onClick=\"document.getElementById('page').value='"+(currentpage -1)+"';$('#processGrid').submit()\" href=\"#\">Назад</a> &nbsp;";
    }
	for(int i=1;i<=pagecount;i++){
		if(i > (currentpage + 4)){continue;}
		if(i < (currentpage - 4)){continue;}
		if(i!= currentpage){
			navigation += " <a onClick=\"document.getElementById('page').value='"+String.valueOf(i)+"';$('#processGrid').submit()\""+
			" href=\"#\">";
			navigation += String.valueOf(i);
			navigation += "</a>";
		}else{
			navigation += "<span class=\"selected\">" + String.valueOf(i) + "</span>";		
		}
	}
	if (currentpage != pagecount) {
			navigation += " &nbsp;<a onClick=\"document.getElementById('page').value='"+(currentpage + 1)+"';$('#processGrid').submit()\" href=\"#\">Вперед</a> &nbsp;";
			navigation += "<a onClick=\"document.getElementById('page').value='"+pagecount+"';$('#processGrid').submit()\" href=\"#\">Последняя</a>";
		}
	navigation += "</div>";
	ArrayList<Task> refTask = (ArrayList<Task>)p.getList();
	Logger LOGGER = Logger.getLogger(this.getClass().getName());
	LOGGER.info("navigation="+navigation);
 %>
 <h1>Отказ клиента от заявки</h1>
 <form id="processGrid" name="processGrid" method="post">
 <%=navigation %>
 <input type="hidden" id="page" name="page" value=""/>
	<table class="regular">
		<thead>
			<tr>
				<th><a href="#" onClick="showSearch()">поиск</a></th>
				<th>№</th>
				<th>Контрагент</th>
				<th>Сумма</th>
				<th title="Валюта">Вал.</th>
				<th title="Тип заявки">Тип</th>
				<th title="Приоритет">Приор.</th>
				<th>Статус</th>
				<th>Инициирующее подразделение</th>
				<th>Тип процесса</th>
				<th></th>
			</tr>
			<tr id="fullSearchForm" <%=processSearchParam.showFilter()?"":"style=\"display:none\"" %>>
				<td><button type="submit">найти</button>
				<button onclick="clearForm(this.form);submit();" name="clear" class="button clear">очистить </button></td>
				<td><input name="searchNumber" type="text" style="width:10em;" 
					value="<%=processSearchParam.getNumber()==null?"":processSearchParam.getNumber() %>"></td>
				<td><input name="searchContractor" type="hidden"
                    value="<%=processSearchParam.getContractor()==null?"":processSearchParam.getContractor() %>">
                    <input type="hidden" id="SPOcontractorID" name="IDSPO_Contractors0" value=""/>
                    <%String orgname="не выбрано";
                    if(processSearchParam.getContractor()!=null){
                            Page orgpage = compendiumcrm.findOrganizationPage(new Organization(true,processSearchParam.getContractor()),0,1,null);//найти из бина
                            if (orgpage.getSize()==1){
                                orgname = ((Organization)orgpage.getList().get(0)).getOrganizationName();
                                orgname = orgname.replaceAll("\"","&quot;");
                            }
                    }
                     %>
                    <input onclick='return openDialog("popup_org.jsp?formName=processGrid&ek=only&fieldNames=IDSPO_Contractors0|selectedName|searchContractor", "organizationLookupList", "<%= dialogProperty%>");' type="text" class="text" readonly="true" name="selectedName" value='<%=orgname %>' />
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
				<td></td>
			</tr>
		</thead>
		<tbody>
		<%boolean even = false;
		for(Task task:refTask){ 
		even=!even;
		String activeStageUrl="file:///"+ApplProperties.getReportsPath()+"Audit/active_stages.rptdesign";
		activeStageUrl= "reportPrintFormRenderAction.do?__format=html&notused=off&__report="+activeStageUrl
				+"&isDelinquency=-1&correspondingDeps=on&p_idDepartment=-1&mdtaskId="+task.getId_task();
		String contractors="";
		for (TaskContractor tc : task.getContractors()){contractors+=tc.getOrg().getAccount_name()+"<br />";}
			if(task.getMain().getDescriptionProcess().equalsIgnoreCase("Pipeline")){
				MdTask mdtask = SBeanLocator.singleton().mdTaskMapper().getById(task.getId_task());
				String projectName = ru.masterdm.spo.utils.Formatter.str(mdtask.getProjectName());
				if(!projectName.isEmpty())
					contractors = projectName;
			}
		%>
			<tr class="<%=even?"b":"a" %>">
				<td>
				  <a target="_blank" href="<%=activeStageUrl%>" title="Посмотреть активные операции"
						><img src="style/in_progress.png" alt="Активные операции"
					></a>
					<a href="report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=<%=task.getId_pup_process()%>" title="Посмотреть хронологию выполнения операций по этой заявке"
						><img src="style/time.png" alt="хронология"
					></a>
					<a target="_blank"
					href="plugin.action.do?class=<%=ViewProcessWrapper.class.getName()%>&idProcess=<%=task.getId_pup_process()%>"
					title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"></a></td>
				<td><a target="mdtaskprintform" href="print_form.do?mdtask=<%=task.getId_task().toString()%>"><%=task.getNumberDisplay()%></a></td>
				<td><%=contractors%></td>
				<td class="number"><%=com.vtb.util.Formatter.format(task.getMain().getSum())%></td>
				<td><%=task.getMain().getCurrency2().getCode()%></td>
				<td><%=task.getHeader().getProcessType() %></td>
				<td><%=task.getHeader().getPriority() %></td>
				<td><%=task.getHeader().getStatus() %></td>
				<td><%=task.getHeader().getStartDepartment().getShortName() %></td>
				<td><%=task.getMain().getDescriptionProcess() %></td>
				<td><a href="javascript:;" 
				onclick="$('#refuse_dialog').load('refuse_form.jsp?mdtaskid=<%=task.getId_task().toString() %>').dialog({width:800,draggable: false})" 
				class="refuse button">
				   Отказ Клиента   </a></td>
			</tr>
		<%} %>
		</tbody>
	</table>
</form>
<jsp:include flush="true" page="footer.jsp" />
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
</body>
</html>