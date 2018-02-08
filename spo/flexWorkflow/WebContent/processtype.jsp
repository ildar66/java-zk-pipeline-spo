<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.uit.director.contexts.WPC"%>
<%@ page contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="ru.md.pup.dbobjects.ProcessTypeJPA"%>
<%@page import="ru.md.crm.dbobjects.ProductQueueJPA"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.model.ActionProcessorFactory" %>
<%@page import="com.vtb.model.TaskActionProcessor" %>
<%@page import="ru.md.spo.ejb.CrmFacadeLocal" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Импорт</title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<jsp:include page="header_and_menu.jsp" />
<%
WorkflowSessionContext wsc = null;
try {
		wsc = AbstractAction.getWorkflowSessionContext(request);
	} catch(Exception e) {
		response.sendRedirect("/errorPage.jsp");
		return;
}
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
            .getActionProcessor("CompendiumCrm");
String id=request.getParameter("id");
HashMap<Integer,String> processList = null;
CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
ProductQueueJPA q = crmFacadeLocal.getProductQueueById(request.getParameter("id"));
Long userid = wsc.getIdUser();
if (request.getParameter("user")!=null && !request.getParameter("user").equals("null")){
    String user = request.getParameter("user"); 
    userid = Long.valueOf(user);
}
Set<Long> set = processor.getProcessTypeList(compenduim.findOrganization(q.getAccount().getId()),userid);
%>
<h1>Параметры клиента</h1>
<table class="regular">
<tr><td>Название</td><td><%=q.getAccount().getName() %></td></tr>
<tr><td>категория клиента</td><td><%=q.getAccount().getFbAccount().getCategory() %></td></tr>
<tr><td>закрепление клиента за корпоративным блоком</td><td><%=q.getAccount().getFbAccount().getCorp_block() %></td></tr>
<tr><td>точка обслуживания</td><td><%=q.getAccount().getREGION() %></td></tr>
</table>
<h1>Не удалось автоматически выбрать бизнес-процесс по которому загружать заявку.</h1>
Проверьте, пожалуйста, правила загрузки в справочнике "Соответствие процессов характеристикам клиентов".<br />
<%if(set.size()==0){ %>под условия заявки не подходит ни один бизнес-процесс<%} %>
<%if(set.size()>1){ %>неоднозначный выбор бизнес-процесса для заявки.<%} %>
<h2>Загрузить по процессу</h2>
<%
if(set.size()==0){
for (ProcessTypeJPA tp : pupFacadeLocal.getProcessTypeForUser(wsc.getIdUser(), null)){
    set.add(tp.getIdTypeProcess());
}}
 %>
<%for(Long processId : set){ %>
<a href="loadProduct.do?id=<%=request.getParameter("id") %>&process=<%=processId %>&user=<%=request.getParameter("user")%>">
<%=WPC.getInstance().getTypeProcessById(processId.intValue()).getNameTypeProcess() %></a><br />
<%} %>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>