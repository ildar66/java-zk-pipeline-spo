<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
%>

<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%><jsp:useBean
	id="acceptorListBean" scope="request"
	class="ru.md.bean.AcceptorListBean">
	<jsp:setProperty name="acceptorListBean" property="taskId"
		value="${taskId}" />
</jsp:useBean>
<c:set var="acceptList" value="${acceptorListBean.acceptList}" />
<c:if test="${not empty acceptList}">
	<div class="content" id="naExpertizy">
	<div>
	<div>Заявку уже акцептовали:<br />
	<c:forEach var="accept" items="${acceptList}">
		<c:set var="user" value="${accept.user}" />
		<p><fmt:formatDate value="${accept.acceptDate}"
			pattern="dd.MM.yyyy" var="initDate" /> <a class="login"
			href="roleslist.jsp?login=${user.login}">${user.fieldFA}
		${user.fieldIM}</a></p>
	</c:forEach></div>
	</div>
	</div>
</c:if>
