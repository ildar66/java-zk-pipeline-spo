<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.vtb.util.Formatter"%>
<%@ page contentType="text/html; charset=utf-8"%>
<%@page isELIgnored="true" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.Date"%>
<%@page import="ru.masterdm.compendium.domain.crm.TargetType" %>
<%@page import="ru.md.pup.dbobjects.DocumentGroupJPA"%>
<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page import="org.uit.director.tasks.ProcessInfo"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%><html>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<% //можно не стесняться брать заявку много раз из taskFacadeLocal. Там работает кеш.
//и он уже должен попасть в кеш на отображении списка заявок
long tstart = System.currentTimeMillis();
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
Logger LOGGER = Logger.getLogger(this.getClass().getName());

Long mdtaskid=TaskHelper.getIdMdTask(request);

String idProcessList = request.getParameter(IConst_PUP.PROCESS_LIST_ID);
if(request.getParameter("id")==null && request.getParameter("id0")==null){
    wsc.setIdCurrTask(0L);
}
long idTask=wsc.getIdCurrTask();
TaskInfo taskInfo = null;
AttributesStructList attrs;
Long idProcess = 0L;
try {
	ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
	CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
	TargetType[] targetTypeList = compenduimCRM.findTargetTypes("%","c.description");
	if (idProcessList != null) {
	    LOGGER.info("idProcessList != null");
		ProcessInfo processInfo = (ProcessInfo) wsc.getProcessList().getTableProcessList().get(Integer.parseInt(idProcessList));
		attrs = processInfo.getAttributes();
		idProcess = processInfo.getIdProcess();
	} else {
		LOGGER.info("idProcessList == null");
		LOGGER.info("getIdCurrTask="+wsc.getIdCurrTask());
		if(wsc.getIdCurrTask()==0){
			attrs = new AttributesStructList();
			if(taskJPA.getProcess()!=null) {idProcess = taskJPA.getProcess().getId();}
		} else {
			idTask = wsc.getIdCurrTask();
			taskInfo = (TaskInfo) wsc.getCurrTaskInfo(false);
			attrs = taskInfo.getAttributes();
			idProcess = taskInfo.getIdProcess();
		}
	}
	Task task = TaskHelper.findTask(request);
	request.setAttribute(IConst_PUP.ATTRIBUTES, attrs);
	request.setAttribute(IConst_PUP.TASK_INFO, taskInfo);
    
    String validateHash;
	try { validateHash = compenduim.jsHashForMdTaskAttribute();} catch (Exception e) {validateHash = "{'dummy' : 'dummy' };";}
	
	String parentHash;
	try { parentHash = taskFacadeLocal.findParentHash(taskJPA, null);} catch (Exception e) {parentHash = "['dummy'];";}
	
	boolean readOnly = false;
	if (idProcessList != null || request.getParameter(IConst_PUP.READONLY) != null
			|| (taskInfo != null && (taskInfo.getNameStageTo() == null))) {
		readOnly = true;
	}
	request.setAttribute(IConst_PUP.READONLY, new Boolean(readOnly));
	String viewType = request.getParameter("viewtype");
			
	%>
		<head>
		<base target="_self" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
			
			<link rel="stylesheet" href="style/style.css" />
			<link rel="stylesheet" href="style/jqModal.css" />
			<link rel="stylesheet" href="style/jquery-ui-1.10.3.custom.min.css" />
			<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
			<script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.3.custom.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.cookie.js"></script>
			<script type="text/javascript" src="scripts/jqModal.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
			<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.autosize.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>
			<script type="text/javascript" src="scripts/form.js"></script>
			<script type="text/javascript" src="scripts/loading.js"></script> 
			<title>Заявка <%=taskJPA.getNumberDisplay()%></title>
		</head>
		<body class="soria" onload="timer()" style="width:1270px" onBeforeUnload="loading()">
		<!--  1270px здесь вставлено чтобы исправить баг fancy box в ie6-->
			<script language="javascript">
				var attributesHash = <%=validateHash%>
				var checkParentAllowedArray = <%=parentHash%> 
				
				function timer() {
					setTimeout("unfade();",10)
				}
			</script>
			<table style="border-collapse:collapse">
			<tr><td>
				<jsp:include page="headerVTB.jsp" />
			</td></tr>
			<tr><td>
				<div id="all">
					<form id="variables" name="variables" action="updateVariables.do" method="POST">
					<input type="hidden" id="mdtaskid" value="<%=mdtaskid%>" name="mdtaskid" />

						<div  style="display:none">
							<input id="idProcess" value="<%=idProcess %>" type="hidden"/>
							<input type="hidden" name="viewType" id="viewType" value="<%=viewType %>"/>
							<input id="supplyid" value="" type="hidden"/>
							<input type="hidden" name="isOnlyUpdate" value="true" /> 
							<input type="hidden" name="isEditMode" value="false" /> 
							<input type="checkbox" name="check0"  checked id="check" style="visibility: hidden" />
							<input type="hidden" name="user0" value="<%=wsc.getIdUser()%>" id="user" /> 
							<input type="hidden" value="<%=WPC.getInstance().dateFormat.format(new Date())%>" name="data0" id="data" />
							<input type="hidden" value="<%=(taskInfo != null) ? taskInfo.getIdProcess() : null%>" name="idProc0" id="idProc" />
							<input type="hidden" value="<%=(taskInfo != null) ? taskInfo.getNameStageTo() : null%>" name="stageName" />
							<input type="hidden" name="sign0" id="sign" />
							<input type="hidden" id="punitiveMeasureTemplate">
							<input type="hidden" id="target_type_id">
							<input type="hidden" id="save_action" name="save_action" value="">
						</div>
						<jsp:include flush="true" page="frame_operationInfo.jsp"/>
						<jsp:include flush="true" page="frame_aboutApplication.jsp"/>

						<!-- Заголовок заявки -->
						<jsp:include flush="true" page="frame_headerApplication.jsp"/>
						<!-- Кнопки сохранить/вернуться к списку заявок... -->
						<jsp:include flush="true" page="frame_controlPanel.jsp"/>
						<!-- Запрос экспертиз ... -->
						<% if (!task.isSubLimit()) {//не сублимит %>
						<jsp:include flush="true" page="frame_requestExpertise.jsp"/>
						<%}%>
<!-- началось основное содержимое -->
<!-- Таблица для разбивки страницы на 2 колонки, параметр clear:both; нужен чтобы закончить обтекание с Информации по операции -->
<table class="columns">
	<tr>
		<td style="border:0"><img style="width:20em;height:0;"></td>
		<td style="border:0"><img style="width:20em;height:0;"></td>
	</tr>
	<tr>
		<!-- Левая колонка -->
		<td valign="top">
			<div id="errorMessage" class="error" style="display:none;">error</div>
			<jsp:include flush="true" page="frame_contractor.jsp"/>
			<%if(taskJPA.isProduct()){%>
				<md:frame empty="false" readOnly="<%=readOnly %>"
                        frame_name="inLimit" header="Структура Лимита" pupTaskId="0" 
                        mdtaskid="<%=mdtaskid.toString() %>" />
				<jsp:include flush="true" page="frame_opportunityParam.jsp"/>
                <script type="text/javascript">var sublimitEditMode = <%=!readOnly%>;</script>
			<%} else { %>
				<jsp:include flush="true" page="frame_limitParam.jsp"/>
			<%} %>
			<%boolean ro = (readOnly||!pupFacade.isPermissionEdit(idTask,"R_Договоры")&&taskJPA.getProcess()!=null); %>
			<%if(!task.getHeader().isOpportunity()){%>
				<jsp:include flush="true" page="frame_priceConditionLimit.jsp"/>
				<md:frame mdtaskid="<%=mdtaskid.toString() %>" 
	                frame_name="contract" empty="<%=task.getSupply().isEmpty() %>" header="Договоры"
	                pupTaskId="<%=String.valueOf(idTask) %>" readOnly="<%=ro %>"/>
			    <%ro = (readOnly||!pupFacade.isPermissionEdit(idTask,"R_Стоимостные условия")&&taskJPA.getProcess()!=null); %>
                <md:frame mdtaskid="<%=mdtaskid.toString() %>" 
                    frame_name="graph" empty="<%=task.isSectionPriceConditionEmpty() %>" 
                    header="Погашение основного долга" pupTaskId="<%=String.valueOf(idTask) %>" 
                    readOnly="<%=ro %>" viewType="<%=viewType %>"/>
			<%} else {%>
				<jsp:include flush="true" page="frame_priceConditionProduct.jsp"/>
				<md:frame mdtaskid="<%=mdtaskid.toString() %>" 
                    frame_name="contract" empty="<%=task.getSupply().isEmpty() %>" header="Договоры"
                    pupTaskId="<%=String.valueOf(idTask) %>" readOnly="<%=ro %>"/>
			    <%ro = (readOnly||!pupFacade.isPermissionEdit(idTask,"R_Стоимостные условия")&&taskJPA.getProcess()!=null); %>
                <md:frame mdtaskid="<%=mdtaskid.toString() %>" 
                    frame_name="graph" empty="<%=task.isSectionPriceConditionEmpty() %>" 
                    header="Графики платежей" pupTaskId="<%=String.valueOf(idTask) %>" 
                    readOnly="<%=ro %>" viewType="<%=viewType %>" />
			<%	} %>
			<jsp:include flush="true" page="frame_conditions.jsp"/>
			<%ro = (readOnly||!pupFacade.isPermissionEdit(idTask,"R_Обеспечение")&&taskJPA.getProcess()!=null&&!TaskHelper.isSpecialEditMode("", request)); %>
			<md:frame mdtaskid="<%=mdtaskid.toString() %>" 
			    frame_name="supply" empty="<%=task.getSupply().isEmpty() %>" header="Обеспечение"
			    pupTaskId="<%=String.valueOf(idTask) %>" readOnly="<%=ro %>"/>
			
			<%if(task.getHeader().isOpportunity()){%>
				<md:frame mdtaskid="<%=mdtaskid.toString() %>" 
	                frame_name="pipeline" empty="<%=task.getSupply().isEmpty() %>" header="Секция продуктового менеджера"
	                pupTaskId="<%=String.valueOf(idTask) %>" readOnly="<%=ro %>"/>
			<%} else { %>
			    <%if(!readOnly&&TaskHelper.isEditMode("сублимиты",request)){ %>
			    <input type="hidden" value="y" id="section_sublimit_edit">
			    <%} %>
			    <script type="text/javascript">var sublimitEditMode = <%=!ro%>;</script>
			    <md:frame empty="<%=taskJPA.getChilds().size()==0 %>" 
    			    readOnly="<%=ro %>"
                    frame_name="inLimit" header="Структура лимита" pupTaskId="0" 
                    mdtaskid="<%=mdtaskid.toString() %>" />
			<%	} %>
			<%if(!task.isSubLimit()&&taskJPA.getProcess()!=null){
			ro = (readOnly||!pupFacade.isPermissionEdit(idTask,"R_Решение по заявке")); %>
			<md:frame mdtaskid="<%=mdtaskid.toString() %>" readOnly="<%=ro %>"
			    frame_name="conclusion" empty="<%=task.isSectionConclusionEmpty() %>" 
			    header="Решение уполномоченного органа" pupTaskId="<%=String.valueOf(idTask) %>" />
			<jsp:include flush="true" page="frame_returnstatus.jsp"/>
			<jsp:include flush="true" page="frame_returnstatusCC.jsp"/>
			<%} %>
		</td>
		<!-- Правая колонка -->
		<td valign="top">
		    
		    <md:frame mdtaskid="<%=mdtaskid.toString() %>" 
			    frame_name="department" empty="false" header="Ответственные подразделения"
			    pupTaskId="<%=String.valueOf(idTask) %>" readOnly="<%=readOnly %>"/>
			<%if(taskJPA.getProcess()!=null 
			    && pupFacade.getAttributeList(taskJPA.getProcess().getProcessType().getIdTypeProcess()).contains("R_Проектная команда")){ %>
			<md:frame empty="<%=taskJPA.getProjectTeam().size()==0 %>" readOnly="<%=readOnly %>"
                frame_name="projectTeam" header="Проектная команда" pupTaskId="<%=String.valueOf(idTask) %>" 
                mdtaskid="<%=mdtaskid.toString() %>" />
            <%} %>
            <%if(taskJPA.getProcess()!=null 
                && pupFacade.getAttributeList(taskJPA.getProcess().getProcessType().getIdTypeProcess()).contains("R_Нормативные сроки")){ %>
				<md:frame empty="false" readOnly="true"
	                frame_name="standardPeriod" header="Сроки прохождения этапов заявки" pupTaskId="<%=String.valueOf(idTask) %>" 
	                mdtaskid="<%=mdtaskid.toString() %>" />
            <%} %>
            <jsp:include flush="true" page="frame_stopFactors.jsp"/>
            <%if(!task.isSubLimit()){
            String  ownerid=taskJPA.getProcess()!=null?task.getId_pup_process().toString():("mdtaskid"+taskJPA.getId()); %>
            <md:frame mdtaskid="<%=ownerid %>" readOnly="false"
                frame_name="documents" empty="<%=task.getDocuments_count().equals(0L) %>" 
                header="Документы по заявке" pupTaskId="0" />
            <md:frame mdtaskid="<%=task.getId_task().toString() %>" readOnly="false"
                frame_name="active_decision" empty="<%=taskJPA.getActive_decision().isEmpty() %>" 
                header="Действующие решения" pupTaskId="0" />
            <%} %>
            <%if(taskJPA.getProcess()!=null
                && pupFacade.getAttributeList(taskJPA.getProcess().getProcessType().getIdTypeProcess()).contains("R_Результаты экспертиз")){ %>
                <md:frame empty="false" readOnly="true"
                    frame_name="expertus" header="Проведение экспертиз" pupTaskId="<%=String.valueOf(idTask) %>" 
                    mdtaskid="<%=mdtaskid.toString() %>" />
            <%} %>
            <jsp:include flush="true" page="frame_departmentAgreement.jsp"/>
			<%
			if (taskJPA.getProcess() != null) {
			    for(String name : pupFacade.getL_AttributeList(taskJPA.getProcess().getProcessType().getIdTypeProcess())){
			        request.setAttribute(IConst_PUP.ATTRIBUTE_NAME, name);
			        String value = "";
			        for (com.vtb.domain.ExtendText et : task.getExtendTexts()){
			            if(et.getDescriptionWithPrefix().equals(name)){
			                value = et.getContext();
			            }
			        }
			        if (value==null || value.contains("null")){value="";}
                    request.setAttribute(IConst_PUP.ATTRIBUTE_VALUE, value);
                    %>
                        <jsp:include flush="true" page="frame_attribute.jsp"/>  
                    <%
			    }
			}
			%>
			<jsp:include flush="true" page="frame_funds.jsp"/>
			<jsp:include flush="true" page="frame_n6.jsp"/>
		<%if(!task.isSubLimit()){
		%>	
			<jsp:include flush="true" page="frame_versionsList.jsp"/>
		<% } %>
			<jsp:include flush="true" page="frame_comments.jsp"/>
			<% String mdFrameParams = "mdtaskid=" + mdtaskid.toString() + "&readOnly=" + readOnly + "&pupTaskId=" + String.valueOf(idTask); %>
			<input type="hidden" id="md_frame_params" value="<%=mdFrameParams%>" />
			<input type="hidden" id="idTask" value="<%=wsc.getIdCurrTask()%>" />
			<input type="hidden" id="tasktype" value="<%=taskJPA.getType()%>" />
			<%if(request.getParameter("idListProcess")!=null) { %>
			 <input type="hidden" id="idListProcess" value="<%=request.getParameter("idListProcess")%>" />
			<%} %>
		</td>
	</tr>
</table>

<%
// определение предыдущей одобренной версии заявки
	String mdtaskNumber = taskJPA.getNumberDisplayWithRoot();
	int dotPos = mdtaskNumber.indexOf('.');
	Long lastApproved = null;
	if (dotPos != -1) {
		String sublimits = mdtaskNumber.substring(dotPos + 1);
		mdtaskNumber = mdtaskNumber.substring(0, dotPos);
		lastApproved = taskFacadeLocal.findLastApprovedVersion(taskJPA.getMdtask_number());
		String[] sublimitNumbers = sublimits.split("-");
		int i = 0;
		while (i < sublimitNumbers.length && lastApproved != null) {
			// нахождение дочернего сублимита
			if (sublimitNumbers[i] == null || sublimitNumbers[i].isEmpty()) {
				i++;
				continue;
			}
			lastApproved = taskFacadeLocal.findSublimit(lastApproved, Long.parseLong(sublimitNumbers[i++]));
		}
	}
	else
		lastApproved = taskFacadeLocal.findLastApprovedVersion(taskJPA.getMdtask_number());
 %>
				<input type="hidden" id="lastApprovedVersion" name="lastApprovedVersion" 
						value="<%=Formatter.str(lastApproved) %>"/>
				<script type="text/javascript" src="scripts/compareApproved.js"></script>
				<script type="text/javascript">
				var prevApprovedDiffShown = false;
<%
	if (lastApproved != null){
%>
					//вызов функции для загрузки результатов сравнения блоков, не загружаемых асинхронно
					loadCompareResult('contractors');
					loadCompareResult('parameters');
					loadCompareResult('price_conditions');
					loadCompareResult('conditions');
					loadCompareResult('pipeline');

					document.getElementById('compareApprovedBlock').style.display="block";
					if (typeof (document.getElementById('compareApproved')) != "undefined") {
						document.getElementById('compareApproved').style.display="block";
					}
					function goToCompareApproved() {
						var objType = ($('#tasktype').val()!= 'Сделка') ? 'limit' : 'product';
						location.href = 'formCompare.jsp?objectType=' + objType 
								+ '&ids=<%=(lastApproved + "|" + mdtaskid)%>'
								+ '<%=(idTask != 0 ? "&idTask="+idTask : "")%>&current=1';
					}
<%
	}
%>
</script>
<input type="hidden" id="isWithComplete" name="isWithComplete" value="false">
</form>


<div id="editDocumentForm" title="Изменить документ" style="display: none;">
<input id="attach_unid" value="" type="hidden">
К заявке <%=taskJPA.getNumberDisplay() %>
<table class="regular" style="width: 700px">
<tr><td>Заголовок</td><td><input id="attach_title" value="" size="80"></td></tr>
<tr><td>Срок действия</td><td><input id="attach_period" value="" onFocus="displayCalendarWrapper('attach_period', '', false); return false;"></td></tr>
<tr><td>Группа документа</td><td>
<input id="attach_docGroup" type="hidden"><a href="javascript:;" onclick="$('#attach_docGroup_popup').dialog({draggable: false, width:500, modal: true});">
<span id="attach_docGroup_name">Выбрать</span></a>
</td></tr>
<tr><td>Тип документа</td><td>
<input id="attach_doctype" type="hidden"><a href="javascript:;" onclick="$('#attach_doctype_popup').dialog({draggable: false, width:700, modal: true});">
<span id="attach_doctype_name">Выбрать</span></a>
</td></tr>
</table>
<br /><a href="javascript:;" onclick="$.post('ajax/editAttach.html',{doctype:$('#attach_doctype').val(),group: $('#attach_docGroup').val(),unid: $('#attach_unid').val(), title:$('#attach_title').val(),exp:$('#attach_period').val()},refreshDocFrame);$('#editDocumentForm').dialog('close');">Изменить</a>
<a href="javascript:;" onclick="$('#editDocumentForm').dialog('close');">Отмена</a>

<div id="attach_docGroup_popup" title="Группа документа" style="display: none;"><ul>
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(0L)) {
if(!docGroup.isActive()){continue;}%>
<li><a href="javascript:;" onclick="$('.attach_doctype').hide();$('#attach_doctype<%=docGroup.getId() %>').show();$('#attach_docGroup').val('<%=docGroup.getId()%>');$('#attach_docGroup_name').html('<%=docGroup.getNAME_DOCUMENT_GROUP()%>');$('#attach_doctype').val('');$('#attach_doctype_name').html('Выбрать');$('#attach_docGroup_popup').dialog('close');">
<%=docGroup.getNAME_DOCUMENT_GROUP() %></a></li>
<%} %></ul></div>

<div id="attach_doctype_popup" title="Тип документа" style="display: none;">
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(0L)) {%>
<div id="attach_doctype<%=docGroup.getId()%>" class="attach_doctype"><ul>
<%for(ru.md.pup.dbobjects.DocumentTypeJPA type : docGroup.getTypes()){
if(!type.isActive() || !pupFacade.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){continue;}%>
<li><a href="javascript:;" onclick="$('#attach_doctype').val('<%=type.getId()%>');$('#attach_doctype_name').html('<%=type.getName()%>');$('#attach_doctype_popup').dialog('close');">
<%=type.getName() %></a></li>
<%} %>
</ul></div>
<%} %>
</div>

</div>


<div id="changeSPForm" title="Изменение нормативного срока" style="display: none;">
<div id="changeSPFormValueId">Критерии не загружены</div>
<br />
Ручной ввод срока: <input id="days" value=""> рабочих дней
<br />
Комментарий: <br />
<textarea rows="5" id="cmnt" onkeyup="checkemptyStPerCmnt()"></textarea>
<br /><a href="javascript:;" onclick="StPerChOnClick()" id="chStPerLink">изменить</a>
<a href="javascript:;" onclick="$('#changeSPForm').dialog('close');">Отмена</a>
<div id="emptyStPerCmnt" class="error">Необходимо заполнить поле комментарий</div>
<input id="grid" value="unknown" type="hidden">
</div>

<script id="newTargetTemplate" type="text/x-jquery-tmpl">
<tr><td>
<textarea name="main Иные цели" id="target${id}"></textarea>
<a href="javascript:;" 
onclick="$('#target_type_id').val('target${id}');$('#select_target_type').dialog({draggable: false, modal: true,width: 800});">
<img alt="выбрать из шаблона" src="style/dots.png"></a>
<input type="hidden" name="main Иные цели id" value="" id="target${id}id">
<input type="hidden" name="main Иные цели условие" value="" id="target${id}cond">
</td><td class="delchk"><input type="checkbox" name="main_otherGoalsIdChk"/></td></tr>
</script>
<div id="select_target_type" title="выбрать целевое назначение" style="display: none;width: 800px;">
<ul>
<%for(TargetType tt : targetTypeList){ %>
    <li><a href="javascript:;" onclick="$('#'+$('#target_type_id').val()).val('<%=tt.getName() %>');$('#'+$('#target_type_id').val()+'cond').val('<%=tt.getId() %>');$('#select_target_type').dialog('close');"><%=tt.getName() %></a></li>
<%} %>
</ul>
</div>


<script id="newIllegalTargetTemplate" type="text/x-jquery-tmpl">
<tr><td>
<textarea class="expand50-200" name="main Forbiddens" id="illegal_target${id}"></textarea>
<a href="javascript:;" 
onclick="$('#illegal_target_type_id').val('illegal_target${id}');$('#select_illegal_target_type').dialog({draggable: false, modal: true,width: 800});">
<img alt="выбрать из шаблона" src="style/dots.png"></a>
</td><td class="delchk"><input type="checkbox" name="main_forbiddensIdChk"/></td></tr>
</script>
<div id="select_illegal_target_type" title="выбрать запрещенную цель кредитования" style="display: none;width: 800px;">
<ul>
<%for(String s : TaskHelper.dict().getIllegalLendingTargets()){ %>
    <li><a href="javascript:;" onclick="$('#'+$('#illegal_target_type_id').val()).val('<%=s %>');$('#select_illegal_target_type').dialog('close');"><%=s %></a></li>
<%} %>
</ul>
</div>


<div id="select_pay_int" title="выбрать порядок уплаты процентов" style="display: none;width: 800px;">
<ul>
<%for(String s : TaskHelper.dict().getPayInt()){ %>
    <li><a href="javascript:;"  onclick="$('#pay_int').val('<%=s %>');$('#select_pay_int').dialog('close');"><%=s %></a></li>
<%} %>
</ul>
</div>

<div id="select_com_base" title="выбрать порядок расчета размера комиссии" style="display: none;width: 800px;">
<ul>
<%for(String s : TaskHelper.dict().getComBase()){ %>
    <li><a href="javascript:;"  onclick="$('#pay_int').val('<%=s %>');$('#select_com_base').dialog('close');"><%=s %></a></li>
<%} %>
</ul>
</div>

<%
Long loadTime = System.currentTimeMillis()-tstart;
request.setAttribute("loadTime", com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000));
 %>
				</div>
			</td></tr>
			<tr><td>
				<jsp:include flush="true" page="footer.jsp" />
			</td></tr>
			</table>
			<!-- Это календарик: -->
			<iframe width="174" height="189" name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;"></iframe>
		</body>
            <script language="javascript" src="scripts/date.js"></script>
            <script language="javascript" src="scripts/validate.js"></script>
            <script language="javascript" src="scripts/applicationScripts.js"></script>
            <script type="text/javascript" src="scripts/sign/MDLib2.js"></script>		
<%
} catch (Exception e) {
	LOGGER.log(Level.SEVERE, e.getMessage(), e);
	out.println("ERROR ON form.jsp:" + e.getMessage());
	wsc.setErrorMessage(e.getMessage());
	response.sendRedirect("errorPage.jsp");
}
%>
</html>
