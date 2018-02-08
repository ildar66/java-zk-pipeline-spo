<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page isELIgnored="true" %>
<%@page import="com.vtb.domain.Trance"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>

<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

boolean readOnly = request.getParameter("readOnly").equals("true");
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");

%>
<h1>Расчетные значения</h1>
<div id="percentRating">
<%int i=1;
for(ru.md.spo.dbobjects.FactPercentJPA per : taskJPA.getFactPercents()){
    String id = "percentFact" + per.getId().toString();
    if(per.getTranceId()!=null){continue;}//значит ставка не для периода, а для транша
%>
    <div class="period<%=per.getId().toString()%>">
        <h4 class="periodHeader">Период №<%=i++ %> с <%=Formatter.format(per.getStart_date()) %> по <%=Formatter.format(per.getEnd_date()) %></h4>
            <table class="regular">
                <tr class="floatOnly"><th>Индикативная ставка</th><td class="indRate">
                <%for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){ 
                    if(fpar.getId().equals(taskJPA.getInd_rate())){%>
                    <%=fpar.getText() %>
                <%}} %>
                </td></tr>
                <%String onChange="$('#"+id+"fondrate').val($('#"+id+"rating_fondrate').val())"; %>
                <tr><th>Ставка фондирования</th><td><md:inputMoney name="rating_fondrate" value="<%=Formatter.format(per.getRating_fondrate()) %>" 
                readonly="<%=readOnly %>" id="<%=id %>" idsfx="rating_fondrate" onChange="<%=onChange %>" /> % годовых</td></tr>
                <tr><th>Премия за кредитный риск</th><td><md:inputMoney name="rating_riskpremium" value="<%=Formatter.format(per.getRating_riskpremium()) %>" 
                readonly="<%=readOnly %>" /> % годовых</td></tr>
                <%onChange="$('#"+id+"rate3').val($('#"+id+"rating_rate3').val())"; %>
                <tr><th>Плата за экономический капитал</th><td><md:inputMoney name="rating_rate3" id="<%=id %>" idsfx="rating_rate3" 
                onChange="<%=onChange %>" value="<%=Formatter.format(per.getRating_rate3()) %>" 
                readonly="<%=readOnly %>" /> % годовых</td></tr>
                <tr align="left"><th>Повыш. коэфф. за риск</th><td>
                <md:RiskStepupFactor readonly="<%=readOnly %>" name="rating_riskStepupFactor" value="<%=per.getrating_RiskStepupFactorID() %>" id="<%=id%>" idsfx="rating_riskStepupFactor"/>
                </td></tr>
                <%onChange="$('#"+id+"rate11').val(parseFloat($('#"+id+"rating_с1').val().replace(',', '.'))*parseFloat($('#"+id+"rating_с2').val().replace(',', '.')))"; %>
                <tr><th>Коэффициент С1</th><td><md:inputMoney name="rating_с1" value="<%=Formatter.format(per.getRating_c1()) %>" 
                readonly="<%=readOnly %>" id="<%=id %>" idsfx="rating_с1" onChange="<%=onChange %>"/></td></tr>
                <tr><th>Коэффициент С2</th><td><md:inputMoney name="rating_с2" value="<%=Formatter.format(per.getRating_c2()) %>" 
                readonly="<%=readOnly %>" id="<%=id %>" idsfx="rating_с2" onChange="<%=onChange %>"/></td></tr>
                <tr><th>Расчетная ставка</th><td><md:inputMoney name="rating_calc" value="<%=Formatter.format(per.getRating_calc()) %>" 
                readonly="<%=readOnly %>"/> % годовых</td></tr>
            </table>
        </div>
<%} %>
</div>

<%i=1;
for(Trance trance : task.getTranceList()){
ru.md.spo.dbobjects.FactPercentJPA per = null;
for(ru.md.spo.dbobjects.FactPercentJPA taskper : taskJPA.getFactPercents()){
    if(taskper.getTranceId()!=null && taskper.getTranceId().longValue()==trance.getId().longValue()){per=taskper;}
}
if(per==null){//значит, для этого транша еще не было процентной ставки
	per = new ru.md.spo.dbobjects.FactPercentJPA();
    try{
    	for(ru.md.spo.dbobjects.FactPercentJPA p : taskJPA.getFactPercents()){
    		if(p.getTranceId()!=null){continue;}//значит ставка не для периода, а для транша
    		per.setRating_c1(p.getRating_c1());
    		per.setRating_c2(p.getRating_c2());
    	}
    } catch (Exception e) {}
}
per.setId(Long.valueOf(9000+i));
String id = "percentFactTrance" + per.getId().toString();
%>
<div class="period<%=per.getId().toString()%>">
    <h4>Транш №<%=i++ %> с <%=Formatter.format(trance.getUsedatefrom()) %> по <%=Formatter.format(trance.getUsedate()) %>
    на сумму <%=Formatter.format(trance.getSum()) %> <%=trance.getCurrency().getCode() %></h4>
    <table class="regular">
        <%String onChange="$('#"+id+"trfondrate').val($('#"+id+"trrating_fondrate').val())"; %>
        <tr><th>Ставка фондирования</th><td><md:inputMoney name="trrating_fondrate" value="<%=Formatter.format(per.getRating_fondrate()) %>" 
        readonly="<%=readOnly %>" id="<%=id %>" idsfx="trrating_fondrate" onChange="<%=onChange %>"/> % годовых</td></tr>
        <tr><th>Премия за кредитный риск</th><td><md:inputMoney name="trrating_riskpremium" value="<%=Formatter.format(per.getRating_riskpremium()) %>" 
        readonly="<%=readOnly %>" /> % годовых</td></tr>
        <%onChange="$('#"+id+"trrate3').val($('#"+id+"trrating_rate3').val())"; %>
        <tr><th>Плата за экономический капитал</th><td><md:inputMoney name="trrating_rate3" id="<%=id %>" idsfx="trrating_rate3"
        onChange="<%=onChange %>" value="<%=Formatter.format(per.getRating_rate3()) %>" 
        readonly="<%=readOnly %>" /> % годовых</td></tr>
        <tr align="left"><th>Повыш. коэфф. за риск</th><td>
                <md:RiskStepupFactor readonly="<%=readOnly %>" name="trrating_riskStepupFactor" value="<%=per.getrating_RiskStepupFactorID() %>" id="<%=id%>" idsfx="trrating_riskStepupFactor"/>
        </td></tr>
        <tr><th>Расчетная ставка</th><td><md:inputMoney name="trrating_calc" value="<%=Formatter.format(per.getRating_calc()) %>" 
        readonly="<%=readOnly %>"/> % годовых</td></tr>
        <%onChange="$('#"+id+"trrate11').val(parseFloat($('#"+id+"trrating_c1').val().replace(',', '.'))*parseFloat($('#"+id+"trrating_c2').val().replace(',', '.')))"; %>
        <tr><th>Коэффициент С1</th><td><md:inputMoney name="trrating_c1" value="<%=Formatter.format(per.getRating_c1()) %>" 
        readonly="<%=readOnly %>" id="<%=id %>" idsfx="trrating_c1" onChange="<%=onChange %>"/></td></tr>
        <tr><th>Коэффициент С2</th><td><md:inputMoney name="trrating_c2" value="<%=Formatter.format(per.getRating_c2()) %>" 
        readonly="<%=readOnly %>" id="<%=id %>" idsfx="trrating_c2" onChange="<%=onChange %>"/></td>
    </table>
</div>
<%}%>

<script id="newPercentRatingTemplate" type="text/x-jquery-tmpl">
    <div class="${id}">
        <h4 class="periodHeader">Период новый</h4>
            <table class="regular">
                <tr class="floatOnly"><th>Индикативная ставка</th><td class="indRate">
                <%for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){ 
                    if(fpar.getId().equals(taskJPA.getInd_rate())){%>
                    <%=fpar.getText() %>
                <%}} %>
                </td></tr>
                <tr><th>Ставка фондирования</th><td><md:inputMoney name="rating_fondrate" value="" readonly="<%=readOnly %>"/> % годовых</td></tr>
                <tr><th>Премия за кредитный риск</th><td><md:inputMoney name="rating_riskpremium" value="" readonly="<%=readOnly %>" /> % годовых</td></tr>
                <tr><th>Плата за экономический капитал</th><td><md:inputMoney name="rating_rate3" value="" readonly="<%=readOnly %>" /> % годовых</td></tr>
                <tr align="left"><th>Повыш. коэфф. за риск</th><td>
                <md:RiskStepupFactor readonly="<%=readOnly %>" name="rating_riskStepupFactor" value="" id="${id}" idsfx="rating_riskStepupFactor"/>
                </td></tr>
                <tr><th>Коэффициент С1</th><td><md:inputMoney name="rating_с1" value="" readonly="<%=readOnly %>"/></td></tr>
                <tr><th>Коэффициент С2</th><td><md:inputMoney name="rating_с2" value="" readonly="<%=readOnly %>"/></td></tr>
                <tr><th>Расчетная ставка</th><td><md:inputMoney name="rating_calc" value="" readonly="<%=readOnly %>"/> % годовых</td></tr>
            </table>
        </div>
</script>