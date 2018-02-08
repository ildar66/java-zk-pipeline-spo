<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
try {
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	if (attrs != null) {
	%>
	<table class="pane law_conclusion" id="section_Заключение юридическое">
		<thead onclick="doSection('Заключение юридическое')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Заключение юридическое</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<h3>Статус заемщика</h3>
					<pup:textarea name="Статус заемщика"/>
					<h3>Предмет деятельности заемщика</h3>
					<pup:textarea name="Предмет деятельности заемщика"/>
					<h3>Структура капитала</h3>
					<pup:textarea name="Структура капитала"/>
					<h3>Структура органов управления</h3>
					<pup:textarea name="Структура органов управления"/>
					<h3>Задолженность займы споры</h3>
					<pup:textarea name="Задолженность займы споры"/>
					<h3>Анализ величины сделки</h3>
					<pup:textarea name="Анализ величины сделки"/>
					<h3>Выводы</h3>
					<pup:textarea name="Выводы юр"/>
				</td>
			</tr>
		</tbody>
	</table>
	<%
	}
} catch (Exception e) {
	out.println("ERROR ON frame_lawConclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
