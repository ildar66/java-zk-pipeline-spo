<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
try {
	boolean readOnly = !TaskHelper.isEditMode("Сделка в рамках лимита",request);
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	AttributeStruct attr = null;
	String attrValue = null;
	if ((attrs != null)&&(false)) {
	%>
		<div id="inLimit">
			<pup:checkbox name="Сделка в рамках лимита" onClick="showAboutLimit()" />Проводится в рамках лимита
			<script language="javaScript">
				function showAboutLimit() {
					document.getElementById('aboutLimit').style.display = (document.getElementById('aboutLimit').style.display == 'none' ? '' : 'none');
				}
			</script>
		</div>
	<%
	}
} catch (Exception e) {
	out.println("Ошибка в секции inLimit_deal.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>