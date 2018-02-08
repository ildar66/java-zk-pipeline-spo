<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.masterdm.compendium.domain.Department"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="ru.masterdm.compendium.domain.MqSettings"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.User"%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
	Logger LOGGER = Logger.getLogger(this.getClass().getName());

	Department dep = null;
	MqSettings depSettings = null;
	String pageLink = "UploadFileWAS.jsp";
	try {
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		User operator = compenduim.getUser(new User(wsc.getIdUser().intValue()));
		dep = compenduim.getDepartment(new Department(operator.getDepartmentID()));

		depSettings = compenduim.getMqSettingsForDepartment(dep.getId());
	} catch(Exception e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
		e.printStackTrace();
	}
	
	if (depSettings != null) {
		request.setAttribute("ipDomain",depSettings.getFileHostIp());
	}
	LOGGER.info("UPLOAD_URL: " + pageLink);
%>
<jsp:include page="<%=pageLink%>"/>
