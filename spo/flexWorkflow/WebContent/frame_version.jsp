<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.TaskVersion" %>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
try {
	SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	Task task=TaskHelper.findTask(request);
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	ArrayList<TaskVersion> versionList = processor.findTaskVersion(task.getId_task());
	String what = "сделки";
	if(task.isLimit()){what = "лимита";}
	if(task.isSubLimit()){what = "сублимита";}
%>
		<table class="pane other_condition" id="section_Версии заявки">
		<thead onclick="doSection('Версии заявки')" onselectstart="return false">
			<tr>
				<td <%=versionList.size()==0?"class=\"empty\"":"" %>>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>История изменения параметров <%=what %></span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<h3>Версии <%=what %></h3>
	<table class="regular">
	<thead>
		<tr>
			<th>Роль пользователя, создавшего версию</th>
			<th>ФИО пользователя, создавшего версию</th>
			<th>№ версии</th>
			<th>Дата создания версии</th>
			<th>Наименование операции, на которой создана версия</th>
		</tr>
	</thead>
	<tbody>
	<%int c=0;
	for(TaskVersion version : versionList){ 
	c++;%>
		<tr>
			<td><%=version.getRole() %></td>
			<td><%=version.getUserName() %></td>
			<td><a target="_blank" href="getVersion.do?versionid=<%=version.getVersion().toString() %>"><%=c %></a></td>
			<td><%=df.format(version.getDate()) %></td>
			<td><%=version.getStage() %></td>
		</tr>
	<%} %>
	</tbody>
	</table>
	</td>
			</tr>
		</tbody>
	</table>
	<%
} catch (Exception e) {
	out.println("ERROR ON frame_version.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>