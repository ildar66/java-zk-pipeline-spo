<%@page import="com.vtb.util.Formatter"%>
<%@page import="java.util.UUID"%>
<%@ page import="ru.md.controller.ConclusionController" %>
<%@ page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="com.vtb.util.ApplProperties" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
MdTask mdtask = TaskHelper.getMdTask(request);

String collegial = TaskHelper.pup().getPUPAttributeValue(mdtask.getIdPupProcess(), "Коллегиальный");
boolean readOnly = !TaskHelper.isEditMode("Решение по заявке",request);
try{
%>
<%if(!readOnly){ %><input type="hidden" name="section_conclusion" id="section_conclusion"><%} %>

<table class="regular leftPadd" style="width: 99%;"><tbody>
<tr><th style="width: 50%;">Решение принимает</th>
    <td style="width: 50%;"><input type="radio" name="Коллегиальный" value="Y" onclick="fieldChanged();updateRelatedData()" id="collegial" 
        <%=collegial.equals("Y")?"checked":"" %> <%if(readOnly){ %>disabled="disabled"<%}%>> Уполномоченный орган<br>
        <input type="radio" name="Коллегиальный" value="N" onclick="fieldChanged();updateRelatedData()" 
        <%=collegial.equals("N")?"checked":"" %> <%if(readOnly){ %>disabled="disabled"<%}%>> Уполномоченное лицо<br>
	</td></tr>
<tr><th>Желаемая дата рассмотрения</th>
<td><md:calendarium name="planmeetingDate" id="<%=UUID.randomUUID().toString() %>" readonly="<%=readOnly %>" value="<%=Formatter.format(mdtask.getMeetingProposedDate()) %>" />
</td></tr>

<%} catch (Exception e) {
	out.println("Ошибка в секции  frame_conclusion.jsp:" + e.getMessage());
	e.printStackTrace();
}%>
</tbody>
</table>

<div id="conclusion_root" class="cctr">загрузка...</div>
<script language="javascript">
    updateRelatedData();
    var allowedCommittees = <%=ConclusionController.allowedCommittees(mdtask.getInitdepartment())%>;
    var questionTypeList = <%=ConclusionController.getQuestionTypeListJson()%>;
    var pkrList = <%=ConclusionController.pkrList(mdtask.getIdPupProcess())%>;
    var questionInit = <%=ConclusionController.question(mdtask.getIdMdtask())%>;
    function assigneeAuthorityOptions() {//почистить от лишних опций
        for (var i = 0; i < $('select[name=assigneeAuthority]').size(); i++) {
            var currVal = $($('select[name=assigneeAuthority]')[i]).val();
            //добавить то, чего нет, но должно быть
            for (var j=0;j<allowedCommittees.length;j++)
                if($($('select[name=assigneeAuthority]')[i]).find("option[value='"+allowedCommittees[j].id+"']").size()==0)
                    $($('select[name=assigneeAuthority]')[i]).append($("<option></option>").attr("value",allowedCommittees[j].id).text(allowedCommittees[j].nominativeCaseName));
            //удалить что уже выбрано в других УО
            for (var j=0;j<assigneeAuthorityValues().length;j++){
                var otherVal = assigneeAuthorityValues()[j];
                if (currVal != otherVal)
                    $($('select[name=assigneeAuthority]')[i]).find("option[value='"+otherVal+"']").remove()
            }
            //удалить ccQuestionCopy == '0'
            if ($('select[name=assigneeAuthority]').size() > 1)
                for (var j=0;j<allowedCommittees.length;j++)
                    if (allowedCommittees[j].ccQuestionCopy == '0')
                        $($('select[name=assigneeAuthority]')[i]).find("option[value='"+allowedCommittees[j].id+"']").remove()
        }
    }
    function assigneeAuthorityValues() {
        var arr = [];
        for (var i = 0; i < $('select[name=assigneeAuthority] option:selected').size(); i++) {
            if ($('select[name=assigneeAuthority] option:selected')[i].value != '')
                arr.push($('select[name=assigneeAuthority] option:selected')[i].value);
        }
        return arr;
    }
    function conclusionButtons() {
        if ($('select[name=assigneeAuthority]').size() == 0){
            $('#conclusion_add').hide();
            return;
        }
        assigneeAuthorityOptions();
        $('.assigneeAuthoritySpan').hide();
        if ($('select[name=assigneeAuthority]').size() > 1){
            $('select[name=assigneeAuthority]').first().hide();
            $('.assigneeAuthoritySpan').first().text($('select[name=assigneeAuthority]').first().find('option:selected').text());
            $('.assigneeAuthoritySpan').first().show();
        } else {
            $('select[name=assigneeAuthority]').first().show();
        }
        var selected_id = $('select[name=assigneeAuthority] option:selected')[0].value;
        if(selected_id == '')
            $('#conclusion_add').hide();
        for (var i=0;i<allowedCommittees.length;i++)
            if(allowedCommittees[i].id == selected_id)
                if (allowedCommittees[i].ccQuestionCopy == '1'){
                    $('#conclusion_add').show();
                } else {
                    $('#conclusion_add').hide();
                }
    }
</script>
<script type="text/babel" src="scripts/conclusion.js?<%=ApplProperties.getVersion()%>"></script>