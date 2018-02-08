<%@page import="ru.md.controller.N6List"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
try {
	Task task=TaskHelper.findTask(request);
	%>
	<table class="pane" id="section_n6">
		<thead onclick="n6Frame('<%=task.getId_task()%>')" onselectstart="return false">
			<tr>
				<td <%=N6List.isHasFunds(task.getId_task())?"":"class=\"empty\"" %>>
					<div>
						<img alt="+" src="style/toOpen.gif"  align="middle" id="n6Img">
						<span>Заявки на Н6</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<div id="n6FrameDiv"></div>
				</td>
			</tr>
		</tbody>
	</table>
	<%
} catch (Exception e) {
	out.println("ERROR ON frame_n6.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>