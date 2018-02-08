<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page import="org.uit.director.action.AbstractAction" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%@page import="com.vtb.domain.Comment" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.md.pup.dbobjects.TaskInfoJPA" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javascript" src="scripts/tiny_mce/settings.js"></script>

 
<br />
<%
	try {
		Task task=TaskHelper.findTask(request);
		String pupTaskId = request.getParameter("pupTaskId");
		TaskInfoJPA taskInfo = (pupTaskId==null || pupTaskId.equals("0"))?null:TaskHelper.pup().getTask(Long.valueOf(pupTaskId));
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy");
%>

<div class="comments">
	<%
		for (int i = 0; i < task.getComment().size(); i++) {
			Comment comment = (Comment) task.getComment().get(i);
			if (comment.getBodyHtml() != null) {
				String reason = "Обоснование изменения параметров:";
				String body = comment.getBody();
				String bodyHtml = comment.getBodyHtml();
				String stageName = comment.getStagename() != null ? "с операции " + comment.getStagename() : null;
				if (body.startsWith(reason)) {
					body = body.substring(reason.length());
					stageName = reason;
				}
	%>
		</br>
		<span class="author" style="font-weight: bold;"><%=comment.getAuthor().getName() %></span>
		<span class="date" style="font-weight: bold;"><%=df.format(new Date(comment.getWhen().getTime())) %></span>
		<%if(stageName!=null){
		%><span class="stage" style="font-weight: bold;"><%=stageName %></span><%
		} %>
		<table id="idTableContract" style="Width: 99%;">
    		<tr><td>
				<div class="body"><%=bodyHtml%></div>
    		</td></tr>
		</table>		
	<%
			}}
		if(taskInfo!=null && taskInfo.getIdStatus()!=null && taskInfo.getIdStatus().intValue()==2
				|| TaskHelper.isEditMode("Комментарии",request)){
			String stageto=taskInfo!=null?String.valueOf(taskInfo.getStage().getIdStage()):"";
	%>
	<br/>Новый комментарий:
	<textarea name="comment" class="advanced_textarea"></textarea>
	<input type="hidden" name="comment_stage" value="<%=stageto %>">
	<input type="hidden" name="comment_author" value="<%=wsc.getIdUser() %>">
	<%} %>
</div>
<%
	} catch (Exception e) {
		out.println("ERROR ON frame_comments.jsp:" + e.getMessage());
		e.printStackTrace();
	}
%>
