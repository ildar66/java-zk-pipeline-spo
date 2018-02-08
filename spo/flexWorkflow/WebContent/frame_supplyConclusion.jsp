<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
try {
	%>

	<table class="pane supply_conclusion" id="section_Заключение по обеспечению">
		<thead onclick="doSection('Заключение по обеспечению')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Заключение по обеспечению</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<h3>Сведения о предмете залога</h3>
					<pup:textarea name="Сведения о предмете залога"/>
					<h3>Анализ приемлемости залога</h3>
					<pup:textarea name="Анализ приемлемости залога"/>
					<h3>Условия залоговой сделки</h3>
					<pup:textarea name="Условия залоговой сделки"/>
				</td>
			</tr>
		</tbody>
	</table>
	<%
} catch (Exception e) {
	out.println("ERROR ON frame_supplyConclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
	
