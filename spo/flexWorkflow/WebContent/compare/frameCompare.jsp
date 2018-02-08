<%@page import="ru.md.helper.CompareHelper"%>
<%@page import="ru.md.compare.Result"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");

	String strIds = request.getParameter("ids");
	String objType = request.getParameter("objectType");
	String blockName = request.getParameter("name");
	String current = request.getParameter("current");
	Result res = CompareHelper.compare(strIds, objType, blockName, current, request);
%>
<table class="regular" id="tblCompare<%=blockName%>" name="tblCompare<%=blockName%>">
	<%
			for (int i = 0; i <= res.getHeaders().size(); i++) {
%>
	<col width="200px" />
	<%
			}
%>
	<thead>
		<tr>
			<th />
			<%
			for (String h : res.getHeaders()) {
%>
			<th><%=h%></th>
			<%
			}
%>
		</tr>
	</thead>
	<tbody>
		<%
			if (res != null && res.getResultObjects().size() > 0)
				for (int i = 0; i < res.getResultObjects().get(0).getResults().size(); i++) {
		%>
		<tr>
			<th><div
					<%-- 
						style='text-align:left;padding-left: 5px'
						
						<%=String.valueOf(res.getResultObjects().get(0).getResults().get(i).getLevel() * 10 + 5)%>   px'>			Иерархия
						<%=res.getResultObjects().get(0).getResults().get(i).getValue()%></div></th> 
					--%>
					style='text-align:left;padding-left: 5px'>
					<%=res.getResultObjects().get(0).getResults().get(i).getValue()%></div></th>
			<%
				for (int j = 1; j < res.getHeaders().size() + 1; j++)
							if (res.getResultObjects().get(j) != null
									&& res.getResultObjects().get(j).getResults().size() > i
									&& res.getResultObjects().get(j).getResults().get(i) != null) {
			%>
			<td
				style='text-align:center;<%=(res.getResultObjects().get(j).getResults().get(i).isWrong() ? "color:red"
									: "")%>'
			><%=res.getResultObjects().get(j).getResults().get(i).getValue()%></td>
			<%
				}
							else {
			%>
			<td></td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
	</tbody>
</table>