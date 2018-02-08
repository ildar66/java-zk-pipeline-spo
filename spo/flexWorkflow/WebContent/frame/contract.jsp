<%@page isELIgnored="true"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@ page import="ru.md.helper.TaskHelper" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
TaskJPA task = taskFacade.getTask(Long.valueOf(request.getParameter("mdtaskid")));
boolean readOnly = !TaskHelper.isEditMode("Договоры",request);
%>
<h3>Договоры</h3>
<table id="idTableContract" style="Width: 99%;">
<tbody>
<% if (!readOnly){ %>
    <tr style="display:none;">
        <td style="Width: 99%;">
            <textarea name="contract" class="nonverified" style="Width: 99%;"></textarea>
        </td>
        <td class="delchk" style="text-align: center; padding: 2px;">
            <input type="checkbox" name="idTableContractChk"/>
        </td>
    </tr>
<%}
for(ru.md.spo.dbobjects.ContractJPA contract : task.getContracts()){%>
<tr>
        <td id="compare_contract<%=contract.getId() %>">
            <%if (!readOnly) {%><textarea name="contract" class="nonverified" style="Width: 99%;" onchange="fieldChanged(this);"><%=contract.getContract() %></textarea>
            <%}else{ %><%=contract.getContract()%> 
            <%} %>
        </td>
        <%if (!readOnly) {%><td class="delchk" style="padding: 2px; text-align: center;">
            <input type="checkbox" name="idTableContractChk"/>
        </td><%}%>
</tr>
<%} %>
</tbody>
<tfoot>
<%if (!readOnly) {%>
    <tr>
        <td colspan="2" class="add"  style="min-width: 800px;border-bottom:none">
            <button onmouseover="Tip('Добавить договор')" onmouseout="UnTip()" onclick="AddRowToTable('idTableContract'); fieldChanged();return false;" class="add"></button>
            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRowWithLast('idTableContract', 'idTableContractChk'); fieldChanged();return false;" class="del"></button>
        </td>
    </tr>
<%}%>
<tr>
    <td colspan="2" class="compare-list-removed add" id="compare_list_contract" style="border-bottom:none"></td>
</tr>
</tfoot>
</table>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "")
		loadCompareResult('contracts');
</script>
