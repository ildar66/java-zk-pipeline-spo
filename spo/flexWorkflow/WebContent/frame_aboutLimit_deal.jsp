<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="ru.md.jsp.tag.PUP_EXT"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>

<%
try {
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	boolean readOnly = !TaskHelper.isEditMode("Сведения о лимите",request);
	AttributeStruct attr = null;
	String attrValue = null;
	if ((attrs != null)&&(false)) {
		int assertionCount = PUP_EXT.getValuesCountByAttributeName(attrs,"Утвержден номер");
		%>
		
<%@page import="ru.md.helper.TaskHelper"%><div id="aboutLimit">
			<table class="pane about_limit_deal" id="section_Сведения о лимите/сублимите">
				<thead onclick="doSection('Сведения о лимите/сублимите')" onselectstart="return false">
					<tr>
						<td>
							<div>
								<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
								<span>Сведения о лимите/сублимите</span>
							</div>
						</td>
					</tr>
				</thead>
				<tbody style="display:none">
					<tr>
						<td vAlign=top>
							Лимит
							<table class="position">
								<tr>
									<td style="white-space:nowrap;">
										<pup:input name="Лимит наименование" title="наименование лимита"/>
									</td>
								</tr>
							</table>
							Сублимит
							<table class="position">
								<tr>
									<td style="white-space:nowrap;">
										<pup:input name="Сублимит номер" title="номер сублимита" style="width:2em;"/>
										<pup:input name="Сублимит наименование" title="наименование сублимита" style="width:28.5em;"/>
									</td>
								</tr>
							</table>
							Утверждён
							<table class="position" id="approve_dealTableId">
								<tbody>
									<tr>
										<td style="white-space:nowrap;">
											<pup:input name="Утвержден орган" title="утвердивший орган" style="width:20em;"/>
											<pup:input name="Утвержден дата" title="дата" style="width:6em;"/>
											<pup:input name="Утвержден номер" title="номер протокола" style="width:4em;"/>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<script language="JavaScript">
			//этот name берётся из frane_inLimit_deal.jsp.
			if (document.getElementsByName('Сделка в рамках лимита')[0].value == 'FALSE') {
				document.getElementById('aboutLimit').style.display = 'none'
			}
		</script>
		<%
	}
} catch (Exception e) {
	out.println("Ошибка в секции aboutLimit_deal.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
