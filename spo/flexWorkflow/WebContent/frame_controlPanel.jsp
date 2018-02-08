<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="org.uit.director.db.dbobjects.Attribute"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="java.util.Iterator"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.persistence.UserMapper" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.md.domain.ReportTemplate" %>
<%@ page import="ru.md.domain.User" %>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@ page import="ru.md.domain.dict.CommonDictionary" %>
<%@ page import="ru.md.domain.dict.ProcessType" %>
<%@page import="ru.md.persistence.PupMapper"%>
<%@ page import="ru.md.pup.dbobjects.UserJPA" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    try {
		Long idMdtask = TaskHelper.getIdMdTask(request);
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		MdTask task = TaskHelper.getMdTask(request);
		List<String> currentUserRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userRoles(wsc.getIdUser(), task.getIdTypeProcess());
		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		boolean isPipelineReadonly = pupFacadeLocal.isPipelineReadonly(idMdtask);

		boolean readOnly = !TaskHelper.isEditMode("", request) && isPipelineReadonly;
		boolean isCanEditFund = TaskHelper.isCanEditFund(request);
		TaskInfo taskInfo = TaskHelper.getCurrTaskInfo(request);
		ru.md.pup.dbobjects.TaskInfoJPA taskInfoJPA = taskInfo==null?null:pupFacadeLocal.getTask(taskInfo.getIdTask());
		AttributesStructList attrs = taskInfo==null?null:taskInfo.getAttributes();
		if(request.getParameter("idListProcess")!=null){taskInfo=null;}
		boolean isSublimit = task.getIdPupProcess()==null || task.getIdPupProcess().longValue()==0;
		String status = pupFacadeLocal.getPUPAttributeValue(task.getIdPupProcess(), "Статус");

        PupMapper pupMapper = SBeanLocator.singleton().getPupMapper();
        List<CommonDictionary<Long>> processTypes = pupMapper.getPipelineProcessTypes(pupFacadeLocal.getCurrentUser().getIdUser());
        boolean isPipelineProcess = task.getProcessType() == ProcessType.PIPELINE;

%>
<link rel="stylesheet" href="style/jqModal.css" />
	<script type="text/javascript" src="scripts/tooltip.js"></script>
	<script type="text/javascript" src="scripts/wz_tooltip/wz_tooltip.js"></script>
	<script type="text/javascript">
	    function decline() {
	       location.href = 'decline.accept.do?taskId=<%=(taskInfo != null) ? taskInfo.getIdTask().toString() : ""%>';
	    }
	    function approve() {
           location.href = 'approve.accept.do?taskId=<%=(taskInfo != null) ? taskInfo.getIdTask().toString() : ""%>';
        }
		function ClientCancelling() {
			if(confirm("Отказ клиента завершит процесс. Продолжить?")) {
				document.getElementsByName('Статус')[0].value = 'Отказ клиента';
				window.location='refusal.do?idTask=<%=(taskInfo != null) ? taskInfo.getIdTask().toString() : ""%>'
			}
		}
		function buttonClick(attr) {
			document.getElementsByName(attr)[0].value = true;
			submitData(true);
		}
		function exportClick(attr) {
            if (conclusionValid()){
                document.getElementsByName(attr)[0].value = true;
                document.getElementsByName('export2cc')[0].value = true;
                submitData(true);
            }
		}
	</script>
<input type="hidden" value="<%=readOnly && !isCanEditFund%>" id="formReadonly">
<input type="hidden" value="<%=isPipelineReadonly%>" id="pipelineReadonly">
<input name="Отказать" style="display:none"/>
<div id="goToDiv" class="jqmWindow" style="height:100px;overflow:auto">
<h1>Заявка изменена. Сохранить изменения?</h1><br />
<button onclick="$('#redirecturl').val(globalurl);submitData(false);return false;" >Сохранить</button>
<button onclick="location.href=globalurl;return false;">Не сохранять</button>
<hr>
<a href="#" class="jqmClose">Отмена</a>
</div>

<div title="Предупреждение!" style="display: none;" id="mainContractorConfirm">
	В дальнейшем изменение основного заемщика не будет доступно. Продолжить?<br /><br /><br />
	<div align="middle"><button onclick="submitData(true);return false;" >Да</button>
		<button onclick="$('#mainContractorConfirm').dialog('close');">Нет</button></div>
</div>
	<div title="Печатные формы" style="display: none;" id="print_report">
	    <ul>
	    <%for (ReportTemplate rt : SBeanLocator.singleton().mdTaskMapper().getReportTemplateList()){ %>
		<li> 
		    <a href="reportPrintFormRenderAction.do?__report=<%=rt.getFilename() %>&mdtaskId=<%=idMdtask %>&__reportType=PRINT_FORM_WORD&__format=doc&__reportingEngine=<%=rt.getReportingEngine()%>" title="Скачать документ">
		    <%if (rt.getReportingEngine()) {%>
		        <font color="darkblue">
		    <%}%>
		        <%=rt.getTemplateName() %>
		    <%if (rt.getReportingEngine()) {%>
		        </font>
		    <%}%>
		    </a>
		</li>
		<%} %>
	    </ul>
	</div>
	<div id="fader" style="width:7em;background-color:#eee;padding:0.7em 2em;margin:1em; text-align:center">Загружается...</div>
	<div id="controlPanel" style="display:none;">
		<jsp:include flush="true" page="frame_back.jsp"/>
		    <%
			//кнопку сохранить выводим для редактирования или когда редактируем pipeline
			//или при редактировании с целью фондирования из представления "Проектная команда"
			if ((!readOnly && request.getParameter("readonly") == null || !isPipelineReadonly || isCanEditFund) && request.getParameter("dash")==null){
			%>
				<button iconClass="flatScreenIcon" id="b1save" onclick="submitData(false);return false;">Сохранить</button>
		<%
			if(task.isImportedAccess() && currentUserRoles.contains(UserJPA.ACCESS_DOWNLOAD)&&!status.equals("Обработан")&&!status.equals("Одобрено")){%>
		<button iconClass="flatScreenIcon" onclick="$('#status').val('Обработан');submitData(false);return false;">Отправить на акцепт МО</button>
		<%}
			if(task.isImportedAccess() && currentUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL) &&status.equals("Обработан")){%>
		<button iconClass="flatScreenIcon" onclick="$('#status').val('Мигрирована');submitData(false);return false;">Возврат на доработку</button>
		<button iconClass="flatScreenIcon" onclick="$('#status').val('Одобрено');submitData(false);return false;">Акцепт</button>
		<%}
			if(task.isImportedBM() && currentUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL) && !status.equals("Одобрено")){%>
			<button iconClass="flatScreenIcon" onclick="$('#status').val('Одобрено');submitData(false);return false;">Акцепт</button>
		<%}%>
		<%}%>
		<%if(request.getParameter("monitoringmode")!=null){%>
			<%if(TaskHelper.isEditMode("",request)){
			%>
				<%if(TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request)).getMonitoringMode().equals("Редактирование ставки")){%>
            		<button onclick="$('#save_action').val('Направить на акцепт');submitData(false);return false;">Направить на акцепт</button>
				<%} else {%>
		            <button onclick="$('#save_action').val('Акцептовать');submitData(false);return false;">Акцептовать</button>
		            <button onclick="$('#save_action').val('Отправить на доработку');submitData(false);return false;">Отправить на доработку</button>
		            <button onclick="$('#save_action').val('Отказать в акцепте');submitData(false);return false;">Отказать в акцепте</button>
				<%}%>
			<%} else {
			Long monitoringUserWorkId=TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request)).getMonitoringUserWorkId();%>
		        <%if(monitoringUserWorkId!=null && !monitoringUserWorkId.equals(wsc.getCurrentUserInfo().getIdUser())){
					User user = ((UserMapper)SBeanLocator.singleton().getBean("userMapper")).getUserById(monitoringUserWorkId);%>
		<div style="color: red">Операция в работе у пользователя <%=user.getFullName()%> (<%=user.getDepname()%>)</div>
		        <%}%>
		        <%if(monitoringUserWorkId==null && TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request)).getMonitoringPriceUserId()!=null
						&&TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request)).getMonitoringPriceUserId().equals(wsc.getCurrentUserInfo().getIdUser())){%>
				<div style="color: red">Вы не можете выполнять данную операцию: действует "контроль второй руки"</div>
		        <%}%>
		    <%}%>
		<%}%>
		<%if(task.isImported() && currentUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL) &&status.equals("Одобрено")){%>
		<button iconClass="flatScreenIcon" onclick="$('#status').val('Мигрирована');document.variables.submit();return false;">Редактировать</button>
		<%}%>
		<input type="hidden" name="status" id="status">
		<%if(request.getParameter("ced_id")==null){ %>
			<button title="Открыть список печатных форм" <%if(request.getParameter("monitoringmode")!=null){%>style="display: none" <%}%>
    onclick="$('#print_report').dialog({draggable: false,modal: true,width: 900});return false">Печатные формы</button>
            <%} else{
            if(TaskHelper.isCanAcceptCedProduct(request)){ %>
            <button onclick="$('#save_action').val('ced_approve');submitData(false);return false;">Сохранить и акцептовать</button>
            <%} %>
            <div style="display: none;">
            <button onclick="$('#save_action').val('ced');submitData(false);return false;">Завершить операцию</button>
            <button onclick="window.showModalDialog('/ced/pages/processGraph.jsf?id=<%=request.getParameter("ced_id")%>&type=<%=request.getParameter("ced_type")%>&idBpmsEntity=<%=request.getParameter("ced_idBpmsEntity")%>','','');return false">Открыть схему процесса</button>
            </div>
            <%} %>
        <%if(TaskHelper.isShowRefuseMo(request) && task.getIdPupProcess()!=null){%>
        <a href="refuse_form.jsp?mo=true&processType=<%=task.getIdTypeProcess() %>&mdtaskid=<%=task.getIdMdtask()%>" class="refuse button popup" id="refuselink">Отказ клиента</a> &nbsp;
        <%}%>
			<%
			        if (attrs != null && taskInfo != null) {
			            try {
			                if (!taskInfo.getDateOfTakingStr().equalsIgnoreCase("-")
			                		&& taskInfoJPA!=null && taskInfoJPA.isInProgress()
			                        && !isSublimit) {//не выводим для сублимитов
			                        if(TaskHelper.showNextButton(taskInfo.getNameStageTo(), taskInfo.getIdProcess())
                                        && !isPipelineProcess){
										TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
										TaskJPA taskJPA = taskFacadeLocal.getTask(task.getIdMdtask());
										String nextOnClick="submitData(true);return false;";
										if(taskFacadeLocal.getGlobalSetting("changeMainOrgEnable").equalsIgnoreCase("true")
												&& taskJPA.isMainOrgChangeble()
												&& taskInfo != null && taskInfo.getNameStageTo() != null && taskInfo.getNameStageTo().equalsIgnoreCase("Дополнение заявки")){
											nextOnClick="$('#mainContractorConfirm').dialog({draggable: false,width: 400});return false;";
										}
			%>
							<button <%if (TaskHelper.blockNextButton(request)) {%>disabled="disabled" class="disabled"<%}%> id="btnRegister"
									title="Переход на следующую операцию в бизнес-процессе"
							onclick="<%=nextOnClick%>">Далее</button>
			                        <%}%>
                                    
                            <%if (isPipelineProcess && !task.isCrossSell()) {%>
                                <button title="<%if (processTypes.size() > 0) {%>Передать в СПО<%} else {%>Нет прав для передачи в СПО<%}%>"
                                        <%if (processTypes.size() == 0) {%>disabled="disable"<%}%>
                                        onclick="goBack('CreateApplication.jsp?mdTaskId=<%=task.getIdMdtask()%>');return false">Передать в СПО</button>
                            <%}%>
							         <input name="refuseMode" id="refuseMode" type="hidden" value="false" /><%
							             Object anyAttr;
			                             Attribute attr;
			                             Iterator it = attrs.getIterator();
			                             int index = 0;
			                             while (it.hasNext()) {
			                                 anyAttr = it.next();
			                                 if (anyAttr instanceof AttributeStruct) {
			                                     attr = ((AttributeStruct) anyAttr).getAttribute();
												 if (attr.isPermissionAdditionView()) {
			                                         String typeElement = attr.getAddition();
			                                         if (request.getParameter("readonly") == null && typeElement.startsWith("0") &&request.getParameter("idListProcess")==null
			                                             && attr.getName().equalsIgnoreCase("Отказать")
			                                             && !pupFacadeLocal.getPUPAttributeBooleanValue(task.getIdPupProcess(), "Требуется обновление списка дополнительных экспертиз")) {
							         %>
									<a href="refuse_form.jsp?without_form=true&processType=<%=task.getIdTypeProcess() %>" class="refuse button popup" id="refuselink">Отказать</a> &nbsp;
								<%
								    					continue;
								                     }
					                                 if (request.getParameter("readonly") == null && typeElement.startsWith("0") &&request.getParameter("idListProcess")==null
					                                   && attr.getName().equalsIgnoreCase("Одобрить")
					                                   && !pupFacadeLocal.getPUPAttributeBooleanValue(task.getIdPupProcess(), "Требуется обновление списка дополнительных экспертиз")) {
								%>
									<input name="Одобрить" style="display:none"/>
									<button onclick="checkBeforeAccept();calendarInit();return false;" id="acceptlink">Одобрить</button>
									<script type="text/javascript">
										function goAcceptLink() {
											var cachebuster = Math.round(new Date().getTime() / 1000);
											$.fancybox({
												'transitionIn' : 'elastic',
												'transitionOut' : 'elastic',
												'speedIn' : 600,
												'speedOut' : 200,
												'overlayShow' : true,
												'zoomOpacity' : true,
												'zoomSpeedIn' : 500,
												'zoomSpeedOut' : 500,
												'hideOnContentClick' : false,
												'frameWidth' : 800,
												'frameHeight' : 600,
												'showCloseButton' : true,
												'href' : 'refuse_form.jsp?without_form=true&accept=true&processType=<%=taskInfo.getIdTypeProcess() %>'+'&cb=' +cachebuster
											});
											calendarInit();
										}
									</script>
								<%
								  						continue;
					                                }
					                                if ((request.getParameter("readonly") == null) && typeElement.startsWith("0")
					                                && !attr.getName().equalsIgnoreCase("Отказать") && !attr.getName().equalsIgnoreCase("Одобрить")&& !attr.getName().equalsIgnoreCase("Экспертиза ПРР")) {
								%>
									<button id="btn<%=index%>" onclick="document.body.style.cursor='progress';flagValidateVariablesOnOperationCompletion=false;buttonClick('<%=attr.getName()%>')"><%=attr.getName()%></button>
									<input name="<%=attr.getName()%>" style="display:none"/>
								<%
								    					index++;
					                                }
					                                if ((request.getParameter("readonly") == null) && "2".equalsIgnoreCase(typeElement)
					                                    && true) {
								%>
									<button id="btnCC" onclick="exportClick('<%=attr.getName()%>');return false"><%=attr.getName()%></button>
									<input name="export2cc" style="display:none"/>
									<input name="<%=attr.getName()%>" style="display:none"/>
								<%
								    				}
								    				if (request.getParameter("readonly") == null &&request.getParameter("idListProcess")==null
					                                   && attr.getName().equalsIgnoreCase("Экспертиза ПРР")
					                                   && task.getIdPupProcess()!=null ) {%>
					                                   <br /><br /><button id="startPrrBtn" onclick="$.post('ajax/prrStart.html',{idProcess: '<%=task.getIdPupProcess() %>'},onPrrStart);return false"
					                                   title="Запуск экспертизы подразделением по анализу рыночных рисков"
					                                   <%if(pupFacadeLocal.getPUPAttributeValue(task.getIdPupProcess(), "Экспертиза ПРР").equals("1")){ %>disabled="disabled" class="disabled"<%} %>
					                                   ><%=attr.getName()%></button>
					                                <%}
					                            }
					                        }
					                    }
					                }
					            } catch (Exception e) {
					                e.printStackTrace();
					            }
								%>
		<%
		    }
		%>
		<%if(taskInfo!=null && pupFacadeLocal.getTask(taskInfo.getIdTask()).getIdStatus().intValue()==2 &&taskInfo.getNameStageTo()!=null && (taskInfo.getNameStageTo().equals("Получение проекта Справки и изменений к Кредитному решению")||taskInfo.getNameStageTo().startsWith("Консолидация замечаний"))){ %>
		<br /><br /><button onClick="$('#pauseProcessPopup').jqmShow();calendarInit();return false;">Приостановить работы по заявке</button>
		<%} %>
		<%if(task.isPaused() && (currentUserRoles.contains("Структуратор") || currentUserRoles.contains("Руководитель структуратора"))){ %>
		<button onClick="$('#resumeProcessPopup').jqmShow();calendarInit();return false;">Возобновить работы по заявке</button>
		<button onClick="$('#pauseProcessPopup').jqmShow();calendarInit();return false;">Изменить срок</button>
		<a href="refuse_form.jsp?mdtaskid=<%=idMdtask %>" class="refuse button popup" id="refuselink">Отказать</a>
		<%} %>
		<div id='compareApprovedBlock' name='compareApprovedBlock' style='display:none'>
			<button type="button" id='compareApprovedButton' onclick="prevApprovedDiff()">Показать отличия</button>
		</div>
	</div>
<div id="pauseProcessPopup" class="jqmWindow">
<div>
<h1>Установление срока</h1>
Комментарий<br />
<textarea id="pauseProcessCmnt" rows="5" cols="20" onkeyup="checkemptypauseProcessCmnt()"></textarea>
Приостановление до: <md:calendarium name="noName" id="pauseDate" readonly="false" 
value="<%=com.vtb.util.Formatter.format(new java.util.Date()) %>"/>
<hr>
<a href="#" id="pauseConfirm" onclick="pauseProcess();">Подтвердить</a>
<a href="#" class="jqmClose">Отмена</a>
<div id="emptyPauseCmnt" class="error">Необходимо заполнить поле комментарий</div>
</div></div>

<div id="resumeProcessPopup" class="jqmWindow"><div>
Обоснование возобновления заявки<br />
<textarea id="resumeProcessCmnt" rows="5" cols="20" onkeyup="checkemptyresumeProcessCmnt()"></textarea>
<hr>
<a href="#" id="resumeConfirm" onclick="resumeProcess();">Подтвердить</a>
<a href="#" class="jqmClose">Отмена</a>
<div id="emptyResumeCmnt" class="error">Необходимо заполнить поле комментарий</div>
</div></div>
<%      
    } catch (Exception e) {
        out.println("Ошибка в секции controlPanel.jsp:" + e.getMessage());
        e.printStackTrace();
    }
%>
<script type="text/javascript">
function pauseProcess(){
    window.location = "pauseProcess.do?id="+$('#idProcess').val()+"&cmnt="+$('#pauseProcessCmnt').val()+"&pauseDate="+$('#pauseDate').val();
}
function resumeProcess(){
    window.location = "resumeProcess.do?id="+$('#idProcess').val()+"&cmnt="+$('#resumeProcessCmnt').val();
}
function checkemptypauseProcessCmnt(){
    if($("#pauseProcessCmnt").val()==''){
        $("#pauseConfirm").hide();
        $("#emptyPauseCmnt").show();
    } else {
        $("#pauseConfirm").show();
        $("#emptyPauseCmnt").hide();
    }
}
function checkemptyresumeProcessCmnt(){
    if($("#resumeProcessCmnt").val()==''){
        $("#resumeConfirm").hide();
        $("#emptyResumeCmnt").show();
    } else {
        $("#resumeConfirm").show();
        $("#emptyResumeCmnt").hide();
    }
}

$(document).ready(function() {
    $('#pauseProcessPopup').jqm();
    $('#resumeProcessPopup').jqm();
    checkemptypauseProcessCmnt();
    checkemptyresumeProcessCmnt();
    $("a.popup").fancybox({
        'transitionIn'  :   'elastic',
        'transitionOut' :   'elastic',
        'speedIn'       :   600, 
        'speedOut'      :   200, 
        'overlayShow'   :   true,
        'zoomOpacity'   :   true,
        'zoomSpeedIn'   :   500,
        'zoomSpeedOut'  :   500,
        'hideOnContentClick':   false,
        'frameWidth':       800, 
        'frameHeight':      600,
        'showCloseButton':  true
    });
});
</script>
<style>
.ui-dialog .ui-dialog-titlebar {background: #6b99d1; color: white;}
</style>
<div id="pipeline_result"></div>
