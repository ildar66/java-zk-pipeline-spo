<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=utf-8"%>
<%@page isELIgnored="true" %>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="java.util.Date"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@ page import="ru.masterdm.spo.utils.Formatter" %>
<%@page import="com.vtb.util.ApplProperties"%>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.jsp.tag.IConst_PUP" %>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html>
<% long tstart = System.currentTimeMillis();
	if(request.getAttribute("startTime")==null)
		request.setAttribute("startTime", tstart);
Logger LOGGER = Logger.getLogger(this.getClass().getName());
Long mdtaskid=TaskHelper.getIdMdTask(request);
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	boolean readOnly = false;
	if (request.getParameter(IConst_PUP.READONLY) != null) {
		readOnly = true;
	}
try {TaskHelper.isEditMode(null, request);//side effect
	MdTask task = TaskHelper.getMdTask(request);
	String pupTaskId = TaskHelper.getCurrPupTaskId(request);
	%>
		<head>
			<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
		    <base target="_self" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<link rel="stylesheet" href="style/style.css" />
			<link rel="stylesheet" href="style/jqModal.css" />
			<link rel="stylesheet" href="style/jquery-ui-1.10.4.custom.css" />
			<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
			<!--[if lt IE 9]>
			<script src="scripts/es5-shim.min.js"></script>
			<script src="scripts/json2.min.js"></script>
			<![endif]-->
			<script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.4.custom.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/datepicker-ru.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.cookie.js"></script>
			<script type="text/javascript" src="scripts/jqModal.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
			<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.autosize.min.js"></script>
			<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>
			<script src="scripts/vendor/react.min.js"></script>
			<script src="scripts/vendor/react-dom.min.js"></script>
			<script src="scripts/vendor/redux.min.js"></script>
			<script src="scripts/vendor/react-redux.min.js"></script>
			<script src="scripts/vendor/browser.min.js"></script>
			<script type="text/javascript" src="scripts/form.js"></script>
			<script type="text/javascript" src="scripts/loading.js"></script>
			<script type="text/javascript" src="scripts/dialogHandler.js"></script>
			<script type="text/javascript" src="scripts/favoriteSwitcher.js"></script>
			<script type="text/javascript" src="scripts/angular.js"></script><!-- Версия 1.1.5 аж 2012 года чтобы работало в сраном IE7, как мы все его ненавидим -->
			<script type="text/javascript" src="scripts/app.js?<%=ApplProperties.getVersion()%>"></script>
			<script type="text/javascript" src="scripts/tiny_mce/tiny_mce.js"></script>
			<script type="text/javascript" src="scripts/tiny_mce/settings.js"></script>
			<title>Заявка <%=task.getMdtaskNumber()%></title>
		</head>
		<body class="soria" style="width:99%" onBeforeUnload="loading()" id="ng-app" ng-app="spoModule">
			<table style="border-collapse:collapse;min-width: 1210px;" width="100%">
			<tr><td>
				<jsp:include page="headerVTB.jsp" />
			</td></tr>
			<tr><td>
				<div id="all">
					<form id="variables" name="variables" action="updateVariables.do" method="POST">
						<input type="hidden" id="mdtaskid" value="<%=mdtaskid%>" name="mdtaskid" />
						<input type="hidden" id="IS_EXTERNAL" value="<%=TaskHelper.isExternal(request)%>" />
						<input type="hidden" id="ID_INSTANCE" value="<%=Formatter.str(task.getIdInstance())%>" />
						<input type="hidden" id="dash_mode" value="<%=request.getParameter("dash")==null?"false":"true"%>" />
						<div  style="display:none">
							<input type="hidden" name="viewType" id="viewType" value="<%=request.getParameter("viewtype") %>"/>
							<input id="supplyid" value="" type="hidden"/>
							<input type="hidden" name="isOnlyUpdate" value="true" /> 
							<input type="hidden" name="isEditMode" value="false" /> 
							<input type="checkbox" name="check0"  checked id="check" style="visibility: hidden" />
							<input type="hidden" name="user0" value="<%=TaskHelper.pup().getCurrentUser().getIdStr()%>" id="user" />
							<input type="hidden" value="<%=Formatter.format(new Date())%>" name="data0" id="data" />
							<input type="hidden" name="sign0" id="sign" />
							<input type="hidden" id="punitiveMeasureTemplate">
							<input type="hidden" id="target_type_id">
							<input type="hidden" id="illegal_target_type_id">
							<input type="hidden" id="rate2NoteDic">
							<input type="hidden" id="targetGroupLimitTypeChoose">
							<input type="hidden" id="save_action" name="save_action" value="">
							<input type="hidden" id="docgroupid">
							<input type="hidden" id="tabcode">
							<% String mdFrameParams = "mdtaskid=" + mdtaskid.toString() + "&readOnly=" + readOnly + "&pupTaskId=" + pupTaskId;
							if(request.getParameter("ced_id")!=null){mdFrameParams+="&ced_id="+request.getParameter("ced_id");}
							if(request.getParameter("monitoringmode")!=null){mdFrameParams+="&monitoringmode="+request.getParameter("monitoringmode");}
							%>
							<input type="hidden" id="md_frame_params" value="<%=mdFrameParams%>" />
							<input type="hidden" id="idTask" value="<%=pupTaskId%>" />
							<input type="hidden" id="tasktype" value="<%=task.getTasktype()%>" />
							<%if(request.getParameter("monitoringmode")!=null){ %>
							<input type="hidden" value="<%=request.getParameter("monitoringmode")%>" name="monitoringmode">
							<%} %>
							<%if((task.isSublimit()||task.isLimit()) && TaskHelper.isEditMode("сублимиты",request)){ %>
							<input type="hidden" value="y" id="section_sublimit_edit">
							<%} %>
							<%if(task.getIdPupProcess() != null){%>
								<input type="hidden" value="<%=task.getIdPupProcess()%>" id="idProcess">
							<%}%>
							<input id="ptuserId" type="hidden"><input id="ptuserFIO" type="hidden"><input id="section" type="hidden">
							<input type="hidden" name="selectedName" id="selectedName" value="selectedName" />
							<input type="hidden" name="CRMID" id="CRMID" value="CRMID" />
							<input type="hidden" name="selectedID" id="selectedID" value="CRMID" />
							<input type="hidden" name="contractorPlaceId" id="contractorPlaceId" />
							<script type="text/javascript">var sublimitEditMode = <%=!readOnly%>;</script>
						</div>
						
						<jsp:include flush="true" page="frame_operationInfo.jsp"/>
						<jsp:include flush="true" page="frame_aboutApplication.jsp"/>
						<!-- Заголовок заявки -->
						<jsp:include flush="true" page="frame_headerApplication.jsp"/>
						<!-- Кнопки сохранить/вернуться к списку заявок... -->
						<jsp:include flush="true" page="frame_controlPanel.jsp"/>
						<!-- Запрос экспертиз ... -->
						<% if (!task.isSublimit()) {//не сублимит %>
						<jsp:include flush="true" page="frame_requestExpertise.jsp"/>
						<%}%>
<!-- началось основное содержимое -->
<div id="errorMessage" class="error" style="display:none;">error</div>
<a href="javascript:;" onclick="show_hide_menu()">
<img src="style/images/menu_hide.png" style="position: absolute; left:271px;top:400px;z-index: 1;" id="show_hide_menu_img">
</a>
<!-- Таблица для разбивки страницы на 2 колонки, параметр clear:both; нужен чтобы закончить обтекание с Информации по операции -->
<table id="menuAndContentTable" class="columns"  ng-controller="TabController as tabsCntr" width="100%">
	<tr>
		<!-- Левая колонка. Меню -->
		<td valign="top" id="section_menu_td">
			<img src="theme/img/plus.png" title="Нажмите, чтобы отобразить секции" ng-click="tabsCntr.expandTabs()" /> <img src="theme/img/minus.png" title="Нажмите, чтобы скрыть секции" ng-click="tabsCntr.collapseTabs()" />
			<div id="section_menu" style="display: none"><!-- включится после загрузки самой страницы -->
				<div id="section_message"></div>
				<table class="pane">
					<thead ng-repeat="tab in tabs_view" >
						<tr style="height: 22px;cursor:pointer" ng-click="tabsCntr.setTabView('{{tab.code}}')">
							<td ng-class="{current:tabsCntr.isSet('{{tab.code}}')}" id="menu_{{tab.code}}">
							<div class="{{tab.classes}}">{{tab.name}}</div>
						</td></tr>
					</thead>
					<thead><tr style="height: 22px"><td id="menu_hrdiv"></td></tr></thead>
					<thead ng-repeat="tab in tabs" >
						<tr style="height: 22px;cursor:pointer" ng-click="tabsCntr.setTab('{{tab.code}}')">
							<td ng-class="{current:tabsCntr.isSet('{{tab.code}}')}" id="menu_{{tab.code}}">
							<div class="{{tab.classes}}">{{tab.name}}</div>
						</td></tr>
						<tr ng-show="tabsCntr.isSet('{{tab.code}}')"><td class="current">
							<ul>
								<li style="cursor:pointer" ng-repeat="subtab in tab.subtabs" onclick="$('#{{tab.subtabs_id}}').tabs('option','active', '{{subtab.id}}')">{{subtab.name}}</li>
							</ul>
						</td></tr>
					</thead>
				</table>
			</div>
			<div title="Заявка изменена. Сохранить изменения?" style="display: none;" id="setViewConfirm">
				<button onclick="$('#setViewConfirm').dialog('close');" ng-click="tabsCntr.openTabView('true')">Сохранить</button>
				<button onclick="$('#setViewConfirm').dialog('close');" ng-click="tabsCntr.openTabView('false')">Не сохранять</button>
				<button onclick="$('#setViewConfirm').dialog('close');">Отмена</button>
			</div>
		</td>
		<!-- Правая колонка. Данные -->
		<td valign="top" id="section_data_td">
		    <div id="section_data">
				<div id="isEmptyTab" ng-show="tabsCntr.isEmptyTab()"><h1>Для отображения секций сделайте выбор в меню</h1></div>
				<div ng-repeat="tabv in tabs_view" id="{{tabv.code}}header" ng-show="tabsCntr.isSet('{{tabv.code}}')">
					<br /><table class="pane paner"><thead><tr ng-click="tabsCntr.setTab('{{tabv.code}}')"><td>{{tabv.name}}</td></tr></thead></table>
					<div id="{{tabv.code}}" class="data_div">Идет загрузка данных <img src="theme/img/loading.gif" alt="..."></div>
				</div>
				<div id="contractorheader" ng-show="tabsCntr.isSet('contractor')">
					<table class="pane paner"><thead><tr ng-click="tabsCntr.setTab('contractor')"><td>Заемщики</td></tr></thead></table>
					<div id="contractor" class="data_div"><jsp:include flush="true" page="frame_contractor.jsp"/></div>
				</div>
				<div id="conclusionheader" ng-show="tabsCntr.isSet('conclusion')">
					<table class="pane paner"><thead><tr ng-click="tabsCntr.setTab('conclusion')"><td>Решение уполномоченного органа</td></tr></thead></table>
					<div id="conclusion" class="data_div"><jsp:include flush="true" page="frame/conclusion.jsp"/></div>
				</div>
				<div ng-repeat="tab in tabs | filter:{ code: '!contractor'}| filter:{ code: '!conclusion'}" id="{{tab.code}}header" ng-show="tabsCntr.isSet('{{tab.code}}')">
					<br /><table class="pane paner {{tab.classes}}"><thead><tr ng-click="tabsCntr.setTab('{{tab.code}}')"><td>{{tab.name}}</td></tr></thead></table>
					<div id="{{tab.code}}" class="data_div">Идет загрузка данных <img src="theme/img/loading.gif" alt="..."></div>
				</div>
			</div>


		</td>
	</tr>
</table>
<%if(request.getParameter("ced_id") == null && request.getParameter("monitoringmode") == null){%>
<jsp:include flush="true" page="frame_list_task.jsp"/>
<%}%>
<input type="hidden" id="isWithComplete" name="isWithComplete" value="false">


						<%
							TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
							ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
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
							/*loadCompareResult('contractors');
							loadCompareResult('parameters');
							loadCompareResult('price_conditions');
							loadCompareResult('conditions');
							loadCompareResult('pipeline');*/
							//кнопка показать отличия только на форме редактирования
							document.getElementById('compareApprovedBlock').style.display="inline";
							if (typeof (document.getElementById('compareApproved')) != "undefined") {
								document.getElementById('compareApproved').style.display="block";
							}
							function goToCompareApproved() {
								var objType = ($('#tasktype').val()!= 'Сделка') ? 'limit' : 'product';
								location.href = 'formCompare.jsp?objectType=' + objType
								+ '&ids=<%=(lastApproved + "|" + mdtaskid)%>'
								+ '<%=(wsc.getIdCurrTask() != 0 ? "&idTask="+wsc.getIdCurrTask() : "")%>&current=1';
							}
							<%
                                }
                            %>
						</script>


</form>
				</div>
			</td></tr>
			<tr><td>
				<jsp:include flush="true" page="footer.jsp" />
			</td></tr>
			<tr><td>
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
<div id="frame_popup"></div>
<script type="text/javascript">
	$(document).ready(function() {
		$('#frame_popup').load('frame_popup.jsp?'+$('#md_frame_params').val());
	});
</script>
</html>
