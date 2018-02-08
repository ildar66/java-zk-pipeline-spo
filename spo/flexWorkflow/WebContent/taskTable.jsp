<%@page contentType="text/html; charset=utf-8"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.md.helper.TaskPage" %>
<%@page import="ru.md.spo.loader.TaskLine" %>
<%@page import="com.vtb.util.Formatter"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.logging.*"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<fmt:setLocale value="RU" />
<script language="JavaScript" src="scripts/applicationScripts.js"></script>
<script language="JavaScript" src="scripts/form/frame.js"></script>
<script language="JavaScript" src="scripts/favoriteSwitcher.js"></script>

<form id="processGrid" name="processGrid" method="post">
<%Logger logger = Logger.getLogger("task_table_jsp");
try {
	long tstart=System.currentTimeMillis();
	TaskPage taskpage = TaskHelper.getTaskPage(request);
	logger.info("*** total TaskHelper.getTaskPage "+(System.currentTimeMillis()-tstart));
%>
<div id="spisokZayavok">
<!-- заголовок  таблицы -->
<h1><%=taskpage.getH1(request) %></h1>
<input type="hidden" id="navigation" name="navigation" value="" />
<table class="regular">
<%	if (taskpage.isAllMode()) {%>
        <jsp:include flush="true" page="taskTableAllSearchForm.jsp" />
<%} else {%>
		<jsp:include flush="true" page="taskTableStageSearchForm.jsp" />
<%}%>
<!-- тело таблицы -->
<tbody>
<div class="paging">
<%
Long processesOnPage = Long.valueOf(ru.md.spo.util.Config.getProperty("PROCESSES_ON_PAGE"));
Long curr = taskpage.getCurr();
Long right = processesOnPage * (curr+1);
if(right > taskpage.getCount()) {right = taskpage.getCount();}
%>
Всего: <%=taskpage.getCount() %>. 
<%if(curr>0){ %><a onClick="$('#navigation').val('0');$('#processGrid').submit()" href="#">в начало</a>
<a onClick="$('#navigation').val('<%=(curr-1) %>');$('#processGrid').submit()" href="#">назад</a>
<%if(curr>1){ %><a onClick="$('#navigation').val('<%=(curr-2) %>');$('#processGrid').submit()" href="#"><%=(curr-1) %></a><%} %>
<a onClick="$('#navigation').val('<%=(curr-1) %>');$('#processGrid').submit()" href="#"><%=(curr) %></a>
<%} %>
<%if(taskpage.getCount()>0){ %><%=(curr + 1) %><%} %>
<%if((curr+1)*processesOnPage < taskpage.getCount()){ %>
<a onClick="$('#navigation').val('<%=(curr+1) %>');$('#processGrid').submit()" href="#"><%=(curr + 2) %></a>
<%if((curr+2)*processesOnPage < taskpage.getCount()){ %><a onClick="$('#navigation').val('<%=(curr+2) %>');$('#processGrid').submit()" href="#"><%=(curr + 3) %></a><%} %>
<%if((curr+3)*processesOnPage < taskpage.getCount() && curr<2){ %><a onClick="$('#navigation').val('<%=(curr+3) %>');$('#processGrid').submit()" href="#"><%=(curr + 4) %></a><%} %>
<%if((curr+4)*processesOnPage < taskpage.getCount() && curr<1){ %><a onClick="$('#navigation').val('<%=(curr+4) %>');$('#processGrid').submit()" href="#"><%=(curr + 5) %></a><%} %>
<a onClick="$('#navigation').val('<%=(curr+1) %>');$('#processGrid').submit()" href="#">вперед</a>
<a onClick="$('#navigation').val('<%=(taskpage.getCount()/processesOnPage) %>');$('#processGrid').submit()" href="#">в конец</a><%} %>
</div>

<%if (taskpage.isAllMode() && request.getParameter("favorite") == null){%>
     <script language="JavaScript">
        favoriteSwitcher("img.favorite", imgSwitcher);
    </script>
<%}%>

<%if (taskpage.isAllMode() && request.getParameter("favorite") != null){%>
    <script language="JavaScript">
        favoriteSwitcher("img.favorite", refreshCurrentPage);
    </script>
<%}%>

<%
// Цикл по всем операциям (заявкам), попадающим под условия фильтрации.
// Обработка каждой заявки
for (TaskLine taskLine : taskpage.getTaskLineList()) {
String id=taskLine.getIdMDTask().toString();
String status = taskLine.getStatus();
if(!TaskHelper.getCcMapStatus4List(taskLine.getIdMDTask()).isEmpty()) {status = TaskHelper.getCcMapStatus4List(taskLine.getIdMDTask());}
%>
<tr class="<%=taskLine.getTrClass()%>">
<td  style="min-width:80px">
	<%
if(taskLine.isShowAcceptButton()) {
	%><a onClick="onClickAcceptButton('<%=taskLine.getIdTask()%>')" 
href="#" title="Начать работу — откроется заявка для редактирования"><img src="style/take.png" alt="взять"></a>
<%
}%>
<%if (taskpage.getTypeList().equals("accept")){%>
<a onClick="onClickRefuseButton('<%=taskLine.getIdTask()%>')" href="#"><img src="style/not_take.png" alt="отказаться"></a><%
}%>
<a target="_blank" href="rprt.do?rp=as&id=<%=taskLine.getNumberpup()%>&mdtaskId=<%=taskLine.getIdMDTask()%>" title="Посмотреть активные операции"
	><img src="style/in_progress.png" alt="Активные операции"></a>
<a href="rprt.do?rp=hr&p=<%=taskLine.getIdProcess()%>" target="_blank" 
title="Посмотреть хронологию выполнения операций по этой заявке"><img src="style/time.png" alt="хронология"></a>
<a target="_blank" href="rprt.do?rp=sh&id=<%=taskLine.getIdProcess()%>" title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"></a>

<%if (!taskpage.isAllMode() && taskLine.isFavorite()){%>
    <img src="style/fav.png" alt="избранная заявка">
<%}%>

<%if (taskpage.isAllMode() && request.getParameter("favorite") == null){%>
    <%if (taskLine.isFavorite()) {%>
        <img mdTaskId="<%=taskLine.getIdMDTask()%>" userId="<%=taskLine.getIdUser()%>" favorite="1" src="style/fav.png" style="cursor: pointer" class="favorite" />
    <%} else { %>
        <img mdTaskId="<%=taskLine.getIdMDTask()%>" userId="<%=taskLine.getIdUser()%>" favorite="0" src="style/unfav.png" style="cursor: pointer" class="favorite" />
    <%} %>
<%}%>

<%if (taskpage.isAllMode() && request.getParameter("favorite") != null){%>
    <img mdTaskId="<%=taskLine.getIdMDTask()%>" userId="<%=taskLine.getIdUser()%>" favorite="1" src="style/fav.png" style="cursor: pointer" class="favorite" />
<%}%>

<%if(taskLine.isShowFundingCreateLink()){ %>
<a target="_blank" href="/Funding/request/form/fundingrequest/CREATE/FUNDING_REQUEST/<%=taskLine.getIdMDTask() %>" title="Создание заявки на фондирование"><img src="style/funding.png" alt="Создание заявки на фондирование"
></a>
<%} %>
<%if(taskLine.isShowFundingCreateN6Link()){ %>
<a target="_blank" href="/Funding/n6request/form/n6requestform/CREATE/N6_REQUEST/<%=taskLine.getIdMDTask() %>" title="Создание заявки на Н6"><img src="style/N6.png" alt="Создание заявки на Н6"
></a>
<%} %>
<%if(taskLine.isShowDealConclusionCreateLink()){ %>
<a target="_blank" href="/ced/pages/dealConclusion.jsf?idCreditDeal=<%=taskLine.getIdMDTask() %>&action=CREATE" title="Создать запрос на заключение КОД"><img src="style/dealConclusionCreate.png" alt="Создать запрос на заключение КОД"
></a>
<%} %>
<%if(taskLine.isShowPreDealConclusionCreateLink()){ %>
<a target="_blank" href="/ced/pages/dealConclusion.jsf?idCreditDeal=<%=taskLine.getIdMDTask() %>&action=CREATE&isPreDealConclusion=true" title="Создать запрос на предварительное оформление КОД"><img src="style/preDealConclusionCreate.png" alt="Создать запрос на предварительное оформление КОД"
></a>
<%} %>
<%if(taskLine.isShowCed(request)){ %>
<a href="#" onclick="showCedTr('<%=id%>')" title="Отображение связанных запросов КОД">
<img id="cedImg<%=id %>" src="style/ced.png" alt="ced"></a>
<%} %>
<%
	if (taskLine.isShowEditConditionLink()) {
%> 
<a class="edit-conditions" href="popup_selectEditProcess.jsp?idTask=<%=taskLine.getIdMDTask() %>" title="Изменить условия"><img src="style/editConditions.png" alt="Изменить условия"></a>
<% } %>
</td>
<%if(!taskpage.isAllMode()){//не выводим для всех заявок %>
<td>
    <%=taskLine.getAssignedMessage() %>
<%
if (taskpage.isShowAssignableUsers()) {%> 
<select id="idUser<%=taskLine.getIdTask()%>" name="idUser<%=taskLine.getIdTask()%>" class="user"
onclick="assignUserOnClick(<%=taskLine.getIdTask()%>)" onchange="assignUserSelectChange(<%=taskLine.getIdTask()%>)">
<option value="">выбрать исполнителя</option>
<option value="load">Загружается...</option>
</select>
<a style="display:none" id="btnAssign<%=taskLine.getIdTask()%>" 
onClick="assignLink(<%=taskLine.getIdTask()%>,'<%=taskpage.getTypeList()%>')" href="#">Назначить</a>
<%} else { out.println(taskpage.getNameIspoln()); }%>
</td><%} %>
<td>
	<a href="<%=taskLine.getUrl() %>" title="Посмотреть заявку"><%=taskLine.getNumberZ()%></a>
<%if(taskLine.isHasFunds()){ %>
<br /><a href="#" onclick="fundPane('<%=id%>')">
<img id="fundImg<%=id %>" src="theme/img/expand.jpg" alt="+"> Заявки на фондирование</a>
<%} %>
<%if(taskLine.isHasN6()){ %>
<br /><a href="#" onclick="n6Pane('<%=id%>')">
<img id="n6Img<%=id %>" src="theme/img/expand.jpg" alt="+"> Заявки на Н6</a>
<%} %>
</td>
<td align="center"><%=taskLine.getVersion() %></td>
<td><%=taskLine.getContractors()%></td>
<td><%=taskLine.getGroup()%></td>
<td class="number"><%=Formatter.format(taskLine.getSumOrig())%></td>
<td><%=taskLine.getCurrency() %></td>
<td><%=taskLine.getProcessType()%></td>
<td><%=taskLine.getPriority()%></td>
<td><%=status%></td>
<%if (!taskpage.isAllMode()) {%>
<td><a target="_blank" href="rprt.do?rp=sh&id=<%=taskLine.getIdProcess()%>"
title="Посмотреть путь заявки по всем операциям"><%=taskLine.getNameStageTo()%></a>
</td>
<%}%>
<td><%=taskLine.getDepartment()%></td>
<td><%=taskLine.getDescriptionProcess()%></td>
</tr>
	<%if(taskLine.isHasFunds()){ %>
         <tr style="display:none;">
         <td colspan="9" style="padding:0">
         <b>Заявки на фондирование</b>
         <div id="fund<%=id %>"></div>
         </td></tr>
     <%} %>
	<%if(taskLine.isHasN6()){ %>
         <tr style="display:none;">
         <td colspan="9" style="padding:0">
         <b>Заявки на Н6</b>
         <div id="n6<%=id %>"></div>
         </td></tr>
     <%} %>
     <%if(taskLine.isShowCed(request)){ %>
         <tr style="display:none;">
         <td colspan="9" style="padding:0">
         <b>связанные запросы КОД</b>
         <div id="ced<%=id %>"></div>
         </td></tr>
     <%} %>
<%}//конец цикла по заявкам	%>
		</tbody>
	</table>
</div>
<%
logger.info("*** total tasktable render time "+(System.currentTimeMillis()-tstart));
} catch (Exception e) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	pw.write("Ошибка на странице TaskTable.jsp:" + e.getMessage()+"<br /><a class=\"supply\" href=\"#StackTrace\">Показать полную информацию об ошибке<a><br />");
	pw.write("Попробуйте обновить страницу.");
	pw.write("<script type=\"text/javascript\">location.replace(\"showTaskList.do?typeList="
	   +request.getParameter("typeList")
	   +"\");</script>");
	pw.write("<div style=\"display:none\"><div id=\"StackTrace\">");
	e.printStackTrace(pw);
	pw.write("</div></div>");
	out.println(sw.toString());
	logger.log(Level.SEVERE, e.getMessage(), e);
	e.printStackTrace();
}
%>
</form>
