<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
try {
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	if (attrs != null) {
	%>
	<table class="pane reserve_conclusion" id="section_Заключение по резервированию">
		<thead onclick="doSection('Заключение по резервированию')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Заключение по резервированию</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<h3>Классификация предполагаемой сделки</h3>
					<pup:textarea name="Классификация предполагаемой сделки"/>
					<h3>Качество  обеспечения</h3>
					<pup:textarea name="Качество обеспечения"/>
					<h3>Резюме заключения по резервированию</h3>
					<pup:textarea name="Резюме заключения по резервированию"/>
				</td>
			</tr>
		</tbody>
	</table>
	<%
	}
} catch (Exception e) {
	out.println("ERROR ON frame_reserveConclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
