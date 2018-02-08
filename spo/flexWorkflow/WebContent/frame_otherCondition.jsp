<%@page import="ru.md.domain.ContitionTemplate"%>
<%@page import="ru.md.spo.dbobjects.CdCreditTurnoverCriteriumJPA"%>
<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.masterdm.compendium.domain.spo.Condition" %>
<%@page import="ru.masterdm.compendium.value.ConditionTypes" %>
<%@page import="com.vtb.domain.OtherCondition" %>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	boolean readOnly = !TaskHelper.isEditMode("Прочие условия",request);
	Task task=TaskHelper.findTask(request);
	ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
	ArrayList otherlist = task.getOtherCondition();
	%>
<%if(!readOnly){ %>
<input type="hidden" id="otherTemplateHiddenInput" name="otherTemplateHiddenInput" value="">
<input type="hidden" id="otherTemplateHiddenInputId" value="">
<%} %>
<%for(ru.md.dict.dbobjects.ConditionTypeJPA type : TaskHelper.dict().findConditionTypes()){%>
<div id="condition<%=type.getId_type() %>SelectTemplateDiv" title="Выбрать значение" style="display: none;">
    <ol>
        <%for(ContitionTemplate t : ru.masterdm.spo.utils.SBeanLocator.singleton().getCompendiumMapper().findConditionTemplate(type.getId_type())){ %>
            <li>
                <a href="javascript:;" class="disable-decoration"
                    onclick="$('#'+$('#otherTemplateHiddenInput').val()).val('<%=t.getName()%>'); $('#'+$('#otherTemplateHiddenInputId').val()).val('<%=t.getId() %>');">
                    <%=t.getName() %></a>
            </li>
        <%}%>
    </ol>
</div>
<%} %>


		<%try{ %>

				<%for(ru.md.dict.dbobjects.ConditionTypeJPA type : TaskHelper.dict().findConditionTypes()){
				 %>
					<h4><%=type.getName() %></h4>
					<table id="condition<%=type.getId_type() %>Table"  style="width: 99%;">
						<tbody>
						<%try{ %>
						<% for (int i=0; i<otherlist.size(); i++) {
							OtherCondition c = (OtherCondition)otherlist.get(i);
							if(c.getType().equals(type.getId_type()) && c.getSupplyCode()==null){
							%>
							<tr>
								<td id="compare_othercondition_condition<%=c.getId()%>">
								<%
								if (!readOnly) {
								%>
									<input type="hidden" id="idCond_<%=type.getId_type() %>_<%=i %>" 
										   name="idCond_<%=type.getId_type() %>" value="<%=c.getIdCondition()%>">
								    <input type="hidden" name="condition<%=type.getId_type() %>id" value="<%=c.getId()%>">
									<textarea id="condition<%=type.getId_type() %>_<%=i %>" onchange="fieldChanged(this);" rows="6"
									          name="condition<%=type.getId_type() %>" onkeyup="fieldChanged(this)"><%=c.getBody() %></textarea>
									<%if(type.isHasDict()){ %>
									<a href="javascript:;" class="dialogActivator" dialogId="condition<%=type.getId_type() %>SelectTemplateDiv"
									   onclick="conditionSelectTemplate('<%=i %>','<%=type.getId_type() %>');">
									   <img alt="выбрать из шаблона" src="style/dots.png"></a>
									<%} %>
								<%
								}else{
								%>
								<%=c.getBody() %>
								<%
								}
								%>
								</td>
								<%
								if (!readOnly) {
								%>
								<td class="delchk">
									<input type="checkbox" name="condition<%=type.getId_type() %>TableChk"/>
								</td>
								<%
								}
								%>
							</tr>
							<%
							}}
							%>
						<%} catch (Exception e) {	out.println("Ошибка в секции  frame_otherCondition.jsp:" + e.getMessage());	e.printStackTrace();} %>
						</tbody>
						<tfoot>
							<tr>
								<td class="compare-list-removed" id="compare_list_othercondition_condition" colspan=2></td>
							</tr>
						<%
						if (!readOnly) {
						%>
							<tr>
								<td colspan="2" class="add">
									<button onmouseover="Tip(getToolTip('Добавить условие'))" onmouseout="UnTip()" onclick="AddRowToOtherTable('<%=type.getId_type() %>'); return false;" class="add"></button>
									<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRowWithLast('condition<%=type.getId_type() %>Table', 'condition<%=type.getId_type() %>TableChk'); return false;" class="del"></button>
								</td>
							</tr>
						<%
						}
						%>
						</tfoot>
					</table>
				<%} %>
				

		<%} catch (Exception e) {	out.println("Ошибка в секции  frame_otherCondition.jsp:" + e.getMessage());	e.printStackTrace();} %>

