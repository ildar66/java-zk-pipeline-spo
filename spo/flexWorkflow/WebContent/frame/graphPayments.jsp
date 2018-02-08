<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.helper.TaskHelper"%>
<%@page isELIgnored="true" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.PaymentSchedule"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<script type="text/javascript">
$(pipelineHandlerGraphPay);
$(document).ajaxComplete(pipelineHandlerGraphPay);
function pipelineHandlerGraphPay() {
	$('select').each(function() {
		if (!(typeof $(this).parent().attr('id') === "undefined")) {
		   if($(this).parent().attr('id').match('compare_graph_curr\\d+') ) {     
                var id = $(this).parent().attr('id').replace('compare_graph_curr', 'comBaseID'); 
                var imageId = $(this).parent().attr('id').replace('compare_graph_curr', 'comBaseImageID'); 
                if ($(this).val() == '%') { 
                    $('#' + id).prop("readonly", false); 
                    $('#' + imageId).css("visibility", "visible");
                    $('#' + imageId).css("display", "block"); 
                } else { 
                    $('#' + id).prop("readonly", true); 
                    $('#' + imageId).css("visibility", "hidden");
                    $('#' + imageId).css("display", "none"); 
                    $('#' + id).val(''); 
                    $('#' + id).removeClass('edited'); 
                } 
            } 
            if($(this).parent().attr('id').match('compare_graph_currgeneratedID?\\d+') ) {     
                var id = $(this).parent().attr('id').replace('compare_graph_curr', 'comBase'); 
                var imageId = $(this).parent().attr('id').replace('compare_graph_curr', 'comBaseImage'); 
                if ($(this).val() == '%') { 
                    $('#' + id).prop("readonly", false); 
                    $('#' + imageId).css("visibility", "visible");
                } else { 
                    $('#' + id).prop("readonly", true); 
                    $('#' + imageId).css("visibility", "hidden");
                    $('#' + id).val(''); 
                    $('#' + id).removeClass('edited'); 
                } 
            } 
		}
	});
	jQuery("textarea[class*=expand]").TextAreaExpander();
}
</script>
<%response.addHeader("Pragma", "no-cache");
    response.addHeader("Expires", "-1");
    response.addHeader("Cache-control", "no-cache");
boolean readOnly = !TaskHelper.isEditMode("R_Стоимостные условия",request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
ArrayList<PaymentSchedule> paymentScheduleList = task.getPaymentScheduleList();
ArrayList<com.vtb.domain.Trance> tranceList = task.getTranceList();
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
if(tranceList.size()==0){
    tranceList= new ArrayList<com.vtb.domain.Trance>();
    com.vtb.domain.Trance trance = new com.vtb.domain.Trance();
    tranceList.add(trance);
}
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>
<div id="graphPaymentsId" style="width: 99%;">
<%for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){
    if(fpar.getId().equals(taskJPA.getInd_rate())){%>
<input type="hidden" id="indrateoriginal" value="<%=fpar.getText()%>">
<%}} %>
<input type="hidden" id="ratetypefixedoriginal" value="<%=taskJPA.isFixed()?"true":"false"%>">
<input type="hidden" id="select_com_base_target_id" value="">
<%
int i=1;
for(com.vtb.domain.Trance trance : tranceList){ 
    String id = trance.getId()==null?"":trance.getId().toString();
    if(task.getTranceList().size()>0){
%>
<h4>Транш №<%=i++ %></h4>
<%  } %>
<table id="graphPaymentsTableId<%=id %>" class="regular graphPaymentsTable paddingTable" style="width: 100%;">
    <thead>
    <tr>
        <th style="width: 1%;">№</th>
        <th style="width: 15%;">Сумма платежа</th>
        <th class="graphPaymentsTableFirstDate" style="width: 10%;">Период оплаты (с даты)</th>
    	<th class="pmn_notfondrate" style="width: 10%;">Период оплаты (по дату)</th>
    	<th class="pmn_fondrate">Срок периода, дн.</th>
    	<th class="pmn_fondrate">Ставка фондирования<br />по периоду платежа</th>
        <%if(!taskJPA.isProduct()){ %><th style="width: 20%;">Описание периода оплаты</th><%} %>
    	<th class="pmn_combase" style="width: 45%;">Порядок расчета</th>
        <th style="width: 1%;"></th>
    </tr>
	</thead>
    <tbody>
    <%try{
        for (int j=0; j<paymentScheduleList.size(); j++) {
        PaymentSchedule pm = (PaymentSchedule)paymentScheduleList.get(j);
        //TODO фильтр по траншу
        if(!id.isEmpty()&& (pm.getTranceId()==null||!id.equals(pm.getTranceId().toString()))){continue;}
        String dateid=id+"startdatepm"+pm.getId();
        String periodid="period"+id+"startdatepm"+pm.getId();
        String fondrateid="fondrate"+id+"startdatepm"+pm.getId();
        String dateOnChange="onUpdateGraphPaymentDate('"+dateid+"')";
        %>
            <tr>
                <td style="text-align: center;"></td>
                <td><input name="pmntrid" value="<%=id%>" type="hidden">
                    <span id="compare_graph_sum<%=pm.getId()%>" class="compare-elem"><md:inputMoney name="pmn Сумма платежа"
                          value="<%=pm.getAmountAsString()%>" readonly="<%=readOnly && !isCanEditFund%>" 
                          styleClass="money sum" onBlur="input_autochange(this,'money');" /></span>&nbsp;
                    <span id="compare_graph_curr<%=pm.getId()%>" class="compare-elem"><md:currency name="pmn валюта" 
                          readonly="<%=readOnly && !isCanEditFund%>" onChange="pipelineHandlerGraphPay()" value="<%=pm.getCurrency().getCode()%>"/></span>
                </td>
                <td id="compare_graph_datefrom<%=pm.getId()%>">
                    <md:input name="pmn Период оплаты (с даты)" value="<%=pm.getFromDateFormatted()%>" readonly="<%=readOnly && !isCanEditFund%>"
                        id="<%=dateid %>" onChange="<%=dateOnChange %>" 
                        onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onkeyup="fieldChanged(this)" 
                        styleClass="text date datefrom" onBlur="input_autochange(this,'date')" />
                </td>
                <td id="compare_graph_date<%=pm.getId()%>" class="pmn_notfondrate">
                    <md:input name="pmn Период оплаты (по дату)"  value="<%=pm.getToDateFormatted()%>" readonly="<%=readOnly && !isCanEditFund%>" 
                        onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onkeyup="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
                </td>
                <td id="compare_graph_period<%=pm.getId()%>" class="pmn_fondrate">
                    <md:input name="pmnperiod"  value="<%=pm.getPeriodStr() %>" readonly="<%=readOnly && !isCanEditFund%>" 
                        onkeyup="fieldChanged(this)" styleClass="text days" id="<%=periodid %>"/>
                </td>
                <td id="compare_graph_fondrate<%=pm.getId()%>" class="pmn_fondrate">
                    <md:inputMoney name="pmn_fondrate" value="<%=com.vtb.util.Formatter.format(pm.getFONDRATE())%>" readonly="<%=readOnly && !isCanEditFund%>" 
                        styleClass="money fondrate" onBlur="input_autochange(this,'money')" id="<%=fondrateid %>" 
                        onChange="recalculateFondRate();$(this).next('input').val('y');$(this).next().next('span').show()" />
                        <input type="hidden" name="pmn_fondrate_manual" value="<%=pm.isManualFondrate()?"y":"n"%>">
                        <span class="exclamation_mark" style="display: <%=pm.isManualFondrate()?"inline":"none"%>;"> !</span>
                </td>
                <%if(!taskJPA.isProduct()){ %><td  id="compare_graph_pmn_desc<%=pm.getId()%>">
                    <%if(!readOnly){ %>
                    <textarea name="pmn_desc" onkeyup="fieldChanged(this)" class="autosize"><%=pm.getDesc()%></textarea>
                    <%}else{ %><%=pm.getDesc() %><%} %>
                </td><%} %>
                <td id="compare_graph_combase<%=pm.getId()%>" class="pmn_combase">
	                <%if(!(readOnly && !isCanEditFund)){ %>
	                    <textarea class="expand50-200 com_base_target_name" onkeyup="fieldChanged(this)" name="pmn Порядок расчета" maxlength="4000" id="comBaseID<%=pm.getId()%>"
	                              class="autosize"><%=pm.getComBase()%></textarea>
	                    <a id="comBaseImageID<%=pm.getId()%>" href="javascript:;" class="dialogActivator" dialogId="select_com_base" onclick="document.getElementById('select_com_base_target_id').value = 'comBaseID<%=pm.getId()%>';fieldChanged()">
					    <img alt="выбрать из шаблона" src="style/dots.png"></a>          
	                <%}else{ %><%=pm.getComBase()%><%} %>
                </td>
                <%if (!readOnly || isCanEditFund) {%><td class="delchk" style="width:auto; padding: 2px !important;"><input type="checkbox" name="idGraphPaymentsTableChk<%=id %>"/></td><%}%>
            </tr>
        <%}
        }catch (Exception e) {out.println("Ошибка в секции  graphPayments.jsp:" + e.getMessage());e.printStackTrace();} %>
    </tbody>
    <%if (!readOnly || isCanEditFund) {%>
    <tfoot>
        <tr>
            <td colspan="6" class="add" style="border-top: 1px;">
                <button onclick="AddGraphPayments('<%=id %>'); dialogHandler(); pipelineHandlerGraphPay(); return false;" class="add"></button>
                <button onclick="DelRowWithLast('graphPaymentsTableId<%=id %>', 'idGraphPaymentsTableChk<%=id %>'); return false;" class="del"></button>
            </td>
        </tr>
    </tfoot>
    <%}%>
    </table>
<%} %>

<%if(!taskJPA.isProduct()){ %>
    <div id="compare_graph_pmn_order">
Порядок погашения задолженности<br />
<%if(!readOnly){ %>
<textarea rows="5" name="pmn_order" onkeyup="fieldChanged(this)" class="autosize" style="width: 100%;"><%=taskJPA.getPmnOrder() %></textarea>
<%}else{ %><%=taskJPA.getPmnOrder() %><%} %>
    </div>
<%} %>

<div class="compare-list-removed" id="compare_list_graph"></div>
</div>
<script language="javaScript">
$().ready(function() {
<%if(!readOnly|| isCanEditFund){%>recalculatePercentRate();<%}%>
    onAmortized_loanClick();
    $('#graphPaymentTemplate').jqm();
    $('.autosize').autosize();
});
	if ($('#lastApprovedVersion').val() != "")
		loadCompareResult('graph');
</script>

<script id="graphPaymentTemplate" type="text/x-jquery-tmpl">
<tr><td></td>
    <td><input name="pmntrid" value="${trid}" type="hidden">
        <input value="${sum}" onblur="input_autochange(this,'money')" name="pmn Сумма платежа" class="money sum">&nbsp;
        <span id="compare_graph_curr${id}" class="compare-elem">
            <md:currency onChange="pipelineHandlerGraphPay()" name="pmn валюта" readonly="<%=readOnly && !isCanEditFund%>" value=""/>
        </span>
    </td>
    <td>
        <md:input name="pmn Период оплаты (с даты)" value="" readonly="<%=readOnly && !isCanEditFund%>" id="startdatepm${id}" onChange="onUpdateGraphPaymentDate('startdatepm${id}')" 
            onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onkeyup="fieldChanged(this)" styleClass="text date datefrom" onBlur="input_autochange(this,'date')" />
    </td>
    <td class="pmn_notfondrate">
        <md:input name="pmn Период оплаты (по дату)"  value="" readonly="<%=readOnly && !isCanEditFund%>" 
            onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onkeyup="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
    </td>
    <td class="pmn_fondrate"><md:input name="pmnperiod" id="periodstartdatepm${id}" value="" readonly="<%=readOnly && !isCanEditFund%>" onkeyup="fieldChanged(this)" styleClass="text days" /></td>
    <td class="pmn_fondrate">
        <md:inputMoney name="pmn_fondrate" value="" readonly="<%=readOnly && !isCanEditFund%>" onChange="recalculateFondRate();$(this).next('input').val('y');$(this).next().next('span').show()"
            styleClass="money fondrate" onBlur="input_autochange(this,'money')" id="fondratestartdatepm${id}" />
            <input type="hidden" name="pmn_fondrate_manual" value="n"><span class="exclamation_mark" style="display: none;"> !</span>
    </td>
    <%if(!taskJPA.isProduct()){ %><td>
                    <textarea onkeyup="fieldChanged(this)" name="pmn_desc" class="autosize"></textarea>
                </td><%} %>
	<td class="pmn_combase">
		<%if(!(readOnly && !isCanEditFund)){ %>
            <textarea class="expand50-200 com_base_target_name" onkeyup="fieldChanged(this)" maxlength="4000" name="pmn Порядок расчета" styleClass="autosize" id="comBase${id}"/>
			<a id="comBaseImage${id}" href="javascript:;" class="dialogActivator" dialogId="select_com_base" onclick="document.getElementById('select_com_base_target_id').value = 'comBase${id}'">
	        <img alt="выбрать из шаблона" src="style/dots.png"></a>
        <%}%>
    </td>
    <td class="delchk" style="width:auto;"><input type="checkbox" name="idGraphPaymentsTableChk${trid}"/></td>
</tr>
</script>
