<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.md.persistence.UserMapper"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page contentType="text/html; charset=utf-8" %>
<%@page import="java.util.List"%>
<%@page import="java.util.logging.Logger"%>
<%@ page import="ru.md.pup.dbobjects.UserJPA" %>
<%@ page import="ru.md.controller.UserListController" %>
<%@ page import="ru.md.controller.DashboardTaskListController" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page isELIgnored="false"%>
<%long tstart=System.currentTimeMillis();
    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    WorkflowSessionContext wsc;
    try {
        wsc = AbstractAction.getWorkflowSessionContext(request);
    } catch (Exception e) {
        response.sendRedirect("/errorPage.jsp");
        return;
    }
    boolean isAdmin = wsc.isAdmin();
    ru.md.domain.User user = ((UserMapper)SBeanLocator.singleton().getBean("userMapper")).getUserByLogin(AbstractAction.getUserLogin(request));
    List<String> currentUserRoles = ((UserMapper)SBeanLocator.singleton().getBean("userMapper")).userAllRoles(wsc.getIdUser());
    String urlAllProcess = "showTaskList.do?typeList=all&idDepartment=";%>
	<link href="theme/stylesheet.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" href="style/jqModal.css" />
	<link rel="stylesheet" href="style/style.css" />
	<link rel="stylesheet" href="style/jquery-ui-1.10.4.custom.css" />
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.4.custom.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/datepicker-ru.js"></script>
	<script type="text/javascript" src="scripts/jqModal.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
	<script type="text/javascript" src="scripts/menu.js"></script>
	<%
	    String stTypeList = request.getParameter("typeList");
	    String stСlassReport = request.getParameter("classReport");
	    String stСlassReportPar1 = request.getParameter("par1");
	    String stPageName = (request.getAttribute("javax.servlet.forward.servlet_path") != null) ? request
	            .getAttribute("javax.servlet.forward.servlet_path").toString() : "";
	    String pageaddress = request.getRequestURI();
	    //LOGGER.info(pageaddress);
	%>
<c:set var="stPageName" value="<%=pageaddress %>" />
<jsp:useBean id="acceptListBean" scope="request" class="ru.md.bean.AcceptListBean" >
        <jsp:setProperty name="acceptListBean" property="userId" value="${workflowContext.idUser}" />
    </jsp:useBean>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
		<td>
		<jsp:include page="headerVTB.jsp" />
		<form>
			<table class="MainMenuTable" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="MainMenu">
						<div id="MainMenu">
							<ul>
								<li><a href="/compendium/">Кредитная система</a></li>
								<li><a href="/compendium/compendium.faces">Справочная система</a></li>
							</ul>
						</div>
					</td>
					<td class="ReverseGradient"><img src="theme/img/gradient2.jpg"></td>
					<td class="LoggedUser">
						<%
			            out.println(user.getDepname());
			            out.print("//" + "<a href=\"javascript:;\" onclick=\"$('#current_role_dialog').dialog({width:800,draggable: false})\">");
			            out.println(user.getFullName() + "</a>");
			            if (isAdmin)
			                out.println(" <em>(Администратор системы)</em>");
						%>
					</td>
				</tr>
			</table>
		</form>
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td id="LeftSideBar">


	<div class="LeftMenu">
		<div class="t"></div>
		<div class="m">
			<h4>Заявки</h4>
				<ul>
					<li class="not_worker_menu <%if (stTypeList != null && stTypeList.equalsIgnoreCase("noAccept")) {%> selected <%}%>">
						<a Id="tt-2ne" href="showTaskList.do?typeList=noAccept&resetFilter=true">ожидающие обработки <span class="taskcount" id="not_acceptcount"></span></a
					></li
					><li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("perform")) {%> class="selected" <%}%>>
					<a Id="tt-1ne" href="showTaskList.do?typeList=perform&resetFilter=true">назначенные мне <span class="taskcount" id="assigncount"></span></a
					></li>
					
					<li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("accept")) {%> class="selected" <%}%>>
						<a Id="tt-2ne" href="showTaskList.do?typeList=accept&resetFilter=true">операции в работе
						<span class="taskcount" id="acceptcount"></span></a
					></li>
					
					<li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all") && request.getParameter("favorite") != null) {%> class="selected" <%}%>>
                        <a href="showTaskList.do?typeList=all&favorite=true&resetFilter=true">избранные</a>
                    </li>
                    
					<li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all")
                        && request.getParameter("closed") == null && request.getParameter("projectteam") != null) {%> class="selected" <%}%>>
                        <a href="showTaskList.do?typeList=all&projectteam=true&resetFilter=true">работа проектной команды</a
                    ></li
					<%if(UserListController.isShowExpertteamList(currentUserRoles)){%>
					><li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all") 
                        && request.getParameter("closed") == null && request.getParameter("expertteam") != null) {%> class="selected" <%}%>>
                        <a href="showTaskList.do?typeList=all&expertteam=true&resetFilter=true">работа экспертного подразделения</a
                    ></li
					<%}%>
                    <%if(currentUserRoles.contains("Структуратор") || currentUserRoles.contains("Руководитель структуратора")){ %>
					><li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all") && request.getParameter("paused") != null 
                        && request.getParameter("closed") == null && request.getParameter("projectteam") == null) {%> class="selected" <%}%>>
                        <a href="showTaskList.do?typeList=all&paused=true&resetFilter=true">приостановленные заявки</a
                    ></li
                    <%} %>
					><li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all") && request.getParameter("closed") == null
					&& request.getParameter("projectteam") == null &&request.getParameter("expertteam") == null && request.getParameter("paused") == null
					&& request.getParameter("favorite") == null) {%> class="selected" <%}%>>
						<%
						    Integer idSelectedDepartment = (Integer) request.getAttribute("idDepartment");
						    //если мы сейчас не во всех департаментах, то показываем по умолчанию ссылку на текущий департамент пользователя
						    if (stTypeList == null || !stTypeList.equalsIgnoreCase("all") ||
						        stTypeList.equalsIgnoreCase("all") && request.getParameter("closed") != null ||
						        request.getParameter("projectteam") != null || request.getParameter("expertteam") != null) {
						        idSelectedDepartment = user.getIdDepartment().intValue();
						    }
						    String currentdepartment = idSelectedDepartment == null ? "всех подразделений" : TaskHelper.pup().getDepartmentById(Long.valueOf(idSelectedDepartment)).getShortName();
						    String urlAllProcessFilial = idSelectedDepartment == null ? urlAllProcess : urlAllProcess
						            + idSelectedDepartment.toString();
						    //Пожалуйста, не меняйте открытие и закрытие % > в следующих пяти строках. Иначе после ссылки a href появится ненужный пробел и поедет вёрстка в IE6
						%><a Id="tt-1ne" href="<%=urlAllProcessFilial%>&resetFilter=true">все заявки <span id="spdepartment"><%=currentdepartment%></span></a>
                        <a class="supply" href="popup_selectdeplist.jsp?root_department=<%=user.getIdDepartment()%>">(Выбрать подразделение)</a>
                        </li
                    ><li <%if (stTypeList != null && stTypeList.equalsIgnoreCase("all") && request.getParameter("closed") != null) {%> class="selected" <%}%>>
                        <a Id="tt999ne33" href="showTaskList.do?typeList=all&closed=true&resetFilter=true">завершенные заявки</a
                    ></li>
					<li <%if (pageaddress != null && pageaddress.contains("CreateApplication")) {%> class="selected" <%}%>>
						<a Id="tt999ne" href="CreateApplication.jsp">создание заявки</a
					></li>
				</ul>
			</div>
		<div class="b"></div>
	</div>
	<div class="LeftMenu">
		<div class="t"></div>
		<div class="m">
			<h4>Отчеты</h4>
			<ul>
			    <!--  
				<li <%if (pageaddress != null && pageaddress.endsWith("reportertest.jsp")) {%> class="selected" <%}%>>
					<a Id="testreporter" href="reportertest.jsp">reporter тест</a>
				-->
				<%if(DashboardTaskListController.showDashboardLink()){%>
				    <li><a Id="showtaskcrmpDashboards" href="/sporeport/index.zul" target="_blank">Dashboards</a></li>
				<%}%>
				<%if(currentUserRoles.contains(UserJPA.ACCESS_DOWNLOAD) || currentUserRoles.contains(UserJPA.ACCESS_DLD_CNTRL)){ %>
				<li <%if (pageaddress != null && pageaddress.endsWith("accesslist.jsp")) {%> class="selected" <%}%>>
					<a Id="showtaskaccessp" href="accesslist.jsp">загрузка из access</a></li>
				<%} %>
				<li <%if (pageaddress != null && pageaddress.endsWith("crmproductlist.jsp")) {%> class="selected" <%}%>>
					<a Id="showtaskcrmp" href="crmproductlist.jsp">сделки из CRM</a
				></li>
				<li <%if (stСlassReport != null
                && stСlassReport.equalsIgnoreCase("org.uit.director.report.mainreports.SearchProcessReport")
                && stСlassReportPar1 == null) {%> class="selected" <%}%>>
					<a href="#" onclick="window.location = 'report.do?classReport=org.uit.director.report.mainreports.SearchProcessReport'">поиск процессов по атрибутам</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportTaskReport.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportTaskReport.do">отчет по операциям</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportActiveStages.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportActiveStages.do">активные операции</a
				></li
				<%if(currentUserRoles.contains("Аудитор ДКАБ")){ %>
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/auditStagesReport.do")) {%> class="selected" <%}%>>
					<a href="auditStagesReport.do">аудит прохождения этапов</a
				></li
				<%} %>
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportDurationStages.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportDurationStages.do">сроки прохождения этапов</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportDurationExpertise.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportDurationExpertise.do">сроки проведения экспертиз</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportOrderStages.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportOrderStages.do">журнал прохождения заявки</a
				></li
				><li <%if (stСlassReport != null
                && stСlassReport.equalsIgnoreCase("org.uit.director.report.mainreports.UserWorksReport")) {%> class="selected" <%}%>>
					<a id="tt2ne" href="#" onclick="window.location = 'report.do?classReport=org.uit.director.report.mainreports.UserWorksReport'">работа подчиненных</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportRolesReport.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportRolesReport.do">переменные к операции</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportRolesByOperationReport.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportRolesByOperationReport.do">доступ роли к операции</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportUsersByReport.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportUsersByReport.do">роли пользователей</a
				></li
				><li <%if (stPageName != null && stPageName.equalsIgnoreCase("/reportRoleTree.do")) {%> class="selected" <%}%>>
					<a id="tt8ne" href="reportRoleTree.do">иерархия ролей</a
				></li
			></ul>
		</div>
		<div class="b"></div>
	</div>
	<div class="LeftMenu">
		<div class="t"></div>
		<div class="m">
			<h4>Статистика</h4>
			<script type="text/javascript" src="scripts/tooltip.js"></script>
			<script type="text/javascript" src="scripts/wz_tooltip/wz_tooltip.js"></script>
				<ul>
					<li <%if (stСlassReport != null
                && stСlassReport.equalsIgnoreCase("org.uit.director.report.mainreports.PerformanceReport")) {%> class="selected" <%}%>>
						<a id="tt3ne" href="#"  onclick="window.location = 'report.do?classReport=org.uit.director.report.mainreports.PerformanceReport'">производительность процессов</a
					></li
					><li <%if (stСlassReport != null
                && stСlassReport.equalsIgnoreCase("org.uit.director.report.mainreports.StageProcessReport")) {%> class="selected" <%}%>>
						<a id="tt5ne" href="#"  onclick="window.location = 'report.do?classReport=org.uit.director.report.mainreports.StageProcessReport'">работа на этапе</a
					></li
					><li <%if (stСlassReport != null
                && stСlassReport.equalsIgnoreCase("org.uit.director.report.mainreports.UserWeightReport")) {%> class="selected" <%}%>>
						<a id="tt6ne" href="#"  onclick="window.location = 'report.do?classReport=org.uit.director.report.mainreports.UserWeightReport'">распределение работы</a
					></li
				></ul>
			</div>
		<div class="b"></div>
	</div>
	<div class="LeftMenu">
		<div class="t"></div>
		<div class="m">
			<h4>Управление заявками</h4>
			<ul>
				<li <%if (stPageName != null && stPageName.equalsIgnoreCase("/direction.stages.do")) {%> class="selected" <%}%>>
					<a Id="tt10ne"  href="direction.stages.do">управление этапами</a
				></li
				><li <%if (pageaddress.contains("reassignList.jsp")) {%> class="selected" <%}%>>
					<a Id="tt13ne"  href="reassignList.jsp">переназначение исполнителя</a
				></li
				><li <%if (pageaddress.contains("standardPeriod.jsp")) {%> class="selected" <%}%>>
					<a href="standardPeriod.jsp">нормативные сроки</a
				></li
				><li <%if (pageaddress.contains("routeProcess.jsp")) {%> class="selected" <%}%>>
					<a href="routeProcess.jsp">настройка маршрутизации заявки</a
				></li>
					<li class="boss_menu <%if (stPageName != null && stPageName.equalsIgnoreCase("/refuseOperationList.do")) {%> selected <%}%>">
					<a Id="tt15ne"  href="refuseOperationList.do?department=-1">отзыв операции</a
				></li>
			</ul>
		</div>
		<div class="b"></div>
	</div>
	<%if (isAdmin) {%>
	<div class="LeftMenu">
		<div class="t"></div>
		<div class="m">
			<h4>Управление</h4>
			<ul>
				<li <%if (stPageName != null && stPageName.equalsIgnoreCase("/administration.do")) {%> class="selected" <%}%>>
					<a id="tt14ne" href="administration.do" >управление бизнес-процессами</a
				></li
                            ><li>
                                <a href="/compendium/compendium.faces" id="tt15ne">управление ролями</a
                            ></li
			></ul>
		</div>
		<div class="b"></div>
	</div>
	<%} %>
</td>
	<td id="MainContent">
	<table class="MainContent">
		<tr>
			<td class="lt"><img src="theme/img/1x1.gif"></td>
			<td class="t"></td>
			<td class="rt"><img src="theme/img/1x1.gif"></td>
		</tr>
		<tr>
			<td class="l"></td>
			<td class="c">
<%
LOGGER.info("*** total header_and_menu render time "+(System.currentTimeMillis()-tstart));
%>
