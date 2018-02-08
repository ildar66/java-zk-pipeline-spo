<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>

<%
try {
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	AttributeStruct attr = null;
	String attrValue = null;
	
	if (attrs != null) {
	%>
	<table class="pane reliability_conclusion" id="section_Резюме заключения о благонадежности">
		<thead onclick="doSection('Резюме заключения о благонадежности')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Резюме заключения о благонадежности</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<pup:textarea name="Резюме заключения о благонадежности"/>
				</td>
			</tr>
		</tbody>
	</table>
	<%
	}
} catch (Exception e) {
	out.println("ERROR ON frame_reliabilityConclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}	
%>
