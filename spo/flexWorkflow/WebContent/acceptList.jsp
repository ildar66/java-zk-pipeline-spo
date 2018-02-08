<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page isELIgnored="false"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page
	import="org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper"%>
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<title>Акцепты операций</title>
<link rel="stylesheet" href="style/style.css" />
</head>
<body class="soria">
<script type="text/javascript">
    function clickLink(value) {
        document.getElementById('page').value = value;
        document.getElementById('processGrid').submit();
    }
</script>
<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<script language="JavaScript" src="scripts/form.js"></script>
<jsp:include page="header_and_menu.jsp" />
<%
    WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
%>

<jsp:useBean id="acceptListBean" scope="request"
	class="ru.md.bean.AcceptListBean">
	<jsp:setProperty name="acceptListBean" property="userId"
		value="${workflowContext.idUser}" />
</jsp:useBean>

<c:set target="${acceptListBean}" property="pageNum"
	value="${param['page']}"></c:set>
<c:set var="pageCount" value="${acceptListBean.pageCount}" />
<c:set var="startPosition" value="${acceptListBean.startPosition}" />

<h1>Акцепты операций</h1>
<form id="processGrid" name="processGrid" method="post"><input
	type="hidden" id="page" name="page" value="" />
<div class="paging">Всего заявок:
${acceptListBean.totalCount};&nbsp;страниц:
${pageCount}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <c:if
	test="${startPosition ne 1}">
	<a onclick="clickLink(1);" href="#">Первая</a>&nbsp;
    <a onclick="clickLink(${startPosition - 1});" href="#">Назад</a>&nbsp;
    </c:if> <c:forEach begin="1" end="${pageCount}" varStatus="status">
	<c:if test="${status.index ne startPosition}">
		<a onclick="clickLink(${status.index});" href="#">${status.index}</a>
	</c:if>
	<c:if test="${status.index eq startPosition}">
		<span class="selected">${status.index}</span>
	</c:if>
</c:forEach> <c:if test="${startPosition ne pageCount}">
         &nbsp;<a onclick="clickLink(${startPosition + 1});" href="#">Вперед</a>&nbsp;
         <a onclick="clickLink(${pageCount});" href="#">Последняя</a>
</c:if></div>
<table class="regular">
	<thead>
		<tr>
			<th>&nbsp;</th>
			<th>Дата поступления</th>
			<th>№ заявки</th>
			<th>Контрагент</th>
			<th>Сумма</th>
			<th title="Валюта">Вал.</th>
			<th title="Тип заявки">Тип</th>
			<th title="Приоритет">Приор.</th>
			<th>Статус</th>
			<th>Инициирующее подразделение</th>
			<th>Тип процесса</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="accept" items="${acceptListBean.acceptListPage.list}"
			varStatus="status">
			<c:set target="${acceptListBean}" property="accept" value="${accept}" />
			<c:set var="task" value="${acceptListBean.task}" />
			<c:choose>
				<c:when test='${(status.index) % 2 eq 0}'>
					<c:set var="class" value="b" />
				</c:when>
				<c:otherwise>
					<c:set var="class" value="a" />
				</c:otherwise>
			</c:choose>
			<tr class="${class}">
				<td><a target="_blank" href="${acceptListBean.activeStageUrl}"
					title="Посмотреть активные операции"> <img
					src="style/in_progress.png" alt="Активные операции"> </a> <a
					href="report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=${task.id_pup_process}"
					title="Посмотреть хронологию выполнения операций по этой заявке">
				<img src="style/time.png" alt="хронология"></a> <br />
				<a target="_blank"
					href="plugin.action.do?class=<%=ViewProcessWrapper.class.getName()%>&idProcess=${task.id_pup_process}"
					title="Посмотреть путь заявки по всем операциям"><img
					src="style/shema.png" alt="схема"></a></td>
				<td><fmt:formatDate value="${accept.initDate}"
					pattern="dd.MM.yyyy" var="initDate" /> ${initDate}</td>
				<%-- %>	
				<td><a target="mdtaskprintform"
					href="print_form.do?mdtask=${task.id_task}">${task.numberDisplay}</a></td>
				<% --%>
				<td><a href="task.context.do?id=${accept.taskInfo.idTask}">${task.numberDisplay}</a></td>
				<td><c:forEach var="contractor" items="${task.contractors}">
                        ${contractor.org.account_name}<br />
				</c:forEach></td>
				<td class="number">${acceptListBean.taskSum}</td>
				<td>${task.main.currency2.code}</td>
				<td>${task.header.processType}</td>
				<td>${task.header.priority}</td>
				<td>${task.header.status}</td>
				<td>${task.header.startDepartment.shortName}</td>
				<td>${task.main.descriptionProcess}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</form>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>