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
	<table class="pane risk_conclusion" id="section_Заключение по рискам">
		<thead onclick="doSection('Заключение по рискам')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Заключение по рискам</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<h3>Связанные контрагенты</h3>
					<pup:textarea name="Связанные контрагенты"/>
					<h3>Анализ параметров  сделки</h3>
					<pup:textarea name="Анализ параметров сделки"/>
					<h3>Рекомендации по проведению сделки</h3>
					<pup:textarea name="Рекомендации по проведению сделки"/>
					<h3>Резюме заключения по рискам</h3>
					<pup:textarea name="Резюме заключения по рискам"/>
				</td>
			</tr>
		</tbody>
	</table>
	<%
	}
} catch (Exception e) {
	out.println("ERROR ON frame_riskConclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>