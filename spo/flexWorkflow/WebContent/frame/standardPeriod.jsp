<%@page import="com.vtb.domain.StandardPeriod"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.spo.ejb.StandardPeriodBeanLocal" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
	long tstart = System.currentTimeMillis();

TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);

ru.md.pup.dbobjects.UserJPA user = TaskHelper.getCurrentUser(request);
TaskJPA task=taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
boolean isCanEditStandardPeriod = user.isCanEditStandardPeriod(task.getProcess().getProcessType().getIdTypeProcess());
ArrayList<StandardPeriod> splist = spLocal.getStandartPeriodReport(task.getId());
%>
Актуальная версия контроля сроков <%=task.getActiveStandardPeriodVersion().toString() %>
<table class="regular">
<tbody>
    <tr><th>Этап</th><th>Нормативный срок (дни)</th><th>Фактический срок (дни)</th>
    <th>Критерий дифференциации срока</th><th>Исполнители,роли</th></tr>
<%for(StandardPeriod speriod : splist){%>
	<tr>
	<td><%=speriod.getStageName() %></td>
	<td align="center"><span id="standardPeriodValue<%=speriod.getStageId()%>"><%=speriod.getStandardPeriodDisplay() %></span>
	<%if(speriod.isCanEditPeriod() && isCanEditStandardPeriod){ %>
	<br /><a onclick="showChangeSPForm('<%=speriod.getStageId()%>')" href="javascript:;">Редактировать нормативный срок</a>
	<%}%>
	<br /><%=speriod.getWhoChangeDisplay() %>
	</td>
	<td align="center" <%=speriod.isExpired()?"class='expired'":"" %>><%=speriod.getFactPeriodDisplay() %></td>
	<td><%=speriod.getCriteria() %></td>
	<td><%=speriod.getUsersDisplay() %></td>
	</tr>
<%} %>
</tbody>
</table>
<% Long loadTime = System.currentTimeMillis()-tstart;
out.println("<div style=\"color:gray\"><em>Время формирования секции (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em></div>");
%>
