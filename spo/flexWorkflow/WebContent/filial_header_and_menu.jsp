<%@page import="ru.masterdm.spo.integration.FilialTaskListFilter"%>
<%@page import="ru.masterdm.spo.integration.FilialTaskList"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page contentType="text/html; charset=utf-8" %>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.Set"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.Department"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="ru.masterdm.compendium.domain.User"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.masterdm.flexworkflow.logic.ejb.IFlexWorkflowIntegrationLocal" %>
<%@page import="ru.md.pup.dbobjects.UserJPA" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page isELIgnored="false"%>
<%
    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    WorkflowSessionContext wsc = null;
    try {
        wsc = AbstractAction.getWorkflowSessionContext(request);
    } catch (Exception e) {
        response.sendRedirect("/errorPage.jsp");
        return;
    }
    CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
    IFlexWorkflowIntegrationLocal flexWorkflowIntegration = com.vtb.util.EjbLocator.getInstance().getReference(IFlexWorkflowIntegrationLocal.class);
    UserJPA u = pupFacadeLocal.getUser(wsc.getIdUser());
    Set departmentChildren = WPC.getInstance().getAllChildrenOfDeparment(u.getDepartment().getIdDepartment());
    long productCount = flexWorkflowIntegration.getFilialTaskList(new FilialTaskListFilter()).getTotalCount();
    String urlAllProcess = "showTaskList.do?typeList=all&idDepartment=";%>
	<link href="theme/stylesheet.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<link rel="stylesheet" href="style/jqModal.css" />
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
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
	    LOGGER.info(pageaddress);
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
			            out.println(u.getDepartment().getShortName());
			            out.print("//" + "<a href=\"javascript:;\">");
			            out.println(u.getFullName() + "</a>");
			            if (wsc.isAdmin())
			                out.println(" <em>(Администратор системы)</em>");
			            if (compenduim.departmentTypeContains(
			                ru.masterdm.compendium.value.DepartmentTypeTypes.EXPERT,
			                u.getDepartment().getIdDepartment())){
			                    out.println(" <em>(Экспертное подразделение)</em>");}
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
			<ul>
				<li class="selected"><a href="CreateApplication.jsp?filialmode=true">Создать сделку</a
				></li><li><a href="<%=FilialTaskList.portalUrl%>">Сделки (<%=productCount %>)</a
				></li>
			</ul>
		</div>
		<div class="b"></div>
	</div>
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