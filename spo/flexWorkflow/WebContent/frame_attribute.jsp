<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="java.util.logging.*"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%Logger LOGGER = Logger.getLogger("frame_attribute_jsp");
	try {
		String tynyMCEconst="<P><BR mce_bogus=\"1\"></P>";//это пустое поле в tynyMCE
		String attributeName = (String) request.getAttribute(IConst_PUP.ATTRIBUTE_NAME);
		String attributeValue = (String) request.getAttribute(IConst_PUP.ATTRIBUTE_VALUE);
		Task task = TaskHelper.findTask(request);
		AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
		boolean readOnly = !TaskHelper.isEditMode(attributeName,request);
		if(!(readOnly&&((attributeValue==null)||(attributeValue.equals(""))||(attributeValue.equals(tynyMCEconst))))){
%>
	<table class="pane attribute" id="section_<%=attributeName%>">
		<thead onclick="doSection('<%=attributeName %>')" onselectstart="return false">
			<tr>
				<td <%=((attributeValue==null)||(attributeValue.equals(""))||(attributeValue.equals(tynyMCEconst)))?"class=\"empty\"":"" %>>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span><%=attributeName.substring(2)%></span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody  style="display:none">
			<tr>
				<td>
					<div>
						<%if(readOnly) { %>
						<span><%= attributeValue %></span>
						<%}else{ %>
						<textarea class="advanced_textarea" name="attribute<%= attributeName %>" onkeyup="fieldChanged(this)"><%= attributeValue %></textarea>
						<%} %>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<%}
		} catch (Exception e) {
			LOGGER.severe("ERROR ON frame_attribute.jsp:" + e.getMessage());
			e.printStackTrace();
		}
	%>
	
