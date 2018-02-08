<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
try {
	Task task=TaskHelper.findTask(request);
	%>
	<table class="pane" id="section_fund">
		<thead onclick="fundFrame('<%=task.getId_task()%>')" onselectstart="return false">
			<tr>
				<td <%=ru.md.controller.FundList.isHasFunds(task.getId_task())?"":"class=\"empty\""%>>
					<div>
						<img alt="+" src="style/toOpen.gif"  align="middle" id="fundImg">
						<span>Заявки на фондирование</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<div id="fundFrameDiv"></div>
				</td>
			</tr>
		</tbody>
	</table>
	<%
} catch (Exception e) {
	out.println("ERROR ON frame_funds.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>