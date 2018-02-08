<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page isELIgnored="true" %>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="ru.md.spo.ejb.DictionaryFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@ page import="ru.md.spo.dbobjects.IndrateMdtaskJPA" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
Logger LOGGER = Logger.getLogger("percentStavka.jsp");
boolean readOnly = !TaskHelper.isEditMode("Стоимостные условия",request);
Task task=TaskHelper.getTask4PriceCondition(Long.valueOf(request.getParameter("mdtaskid")),request.getParameter("monitoringmode")!=null);
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
DictionaryFacadeLocal dictFacade = com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacade.getTask(task.getId_task());
CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>
<%if (!readOnly || isCanEditFund) { %><input type="hidden" name="percentStavka_section" value="YES" /><% } %>
<table id="testPercentStavkaTableId" class="regular leftPadd" style="width: 99%;">
    <tr><th id="productWithPeriod" style="width: 50%;">Срок сделки разделен на периоды</th>
    <td style="width: 50%;">
    <%if(!readOnly){ %><a href="javascript:;" onclick="addPeriod();indratesSync()">добавить период</a><%} %>
    <table class="regular" id="periods"><thead><tr><th>Номер</th><th>с даты</th><th>по дату</th><th></th></tr></thead><tbody>
    <%int i=1;
    for(ru.md.spo.dbobjects.FactPercentJPA per : taskJPA.getFactPercents()){
    if(per.getTranceId()!=null){continue;}//значит ставка не для периода, а для транша %>
    <tr class="FactPercentClass period<%=per.getId().toString()%>"><td id="compare_prodprice_period<%=per.getId()%>" ><%=i++ %></td>
    <td id="compare_prodprice_period<%=per.getId()%>_startdate" ><md:calendarium name="percentFactDate1" value="<%=Formatter.format(per.getStart_date())%>" readonly="<%=readOnly %>"  id="" /></td>
    <td id="compare_prodprice_period<%=per.getId()%>_enddate" ><md:calendarium name="percentFactDate2" value="<%=Formatter.format(per.getEnd_date())%>" readonly="<%=readOnly %>" id="" />
    </td><td><%if(!readOnly && i>2){ %><button class="del" onclick="delPeriod('period<%=per.getId().toString()%>');return false"></button><%} %></td></tr>
    <%} %>
    </tbody>
    <tfoot><tr><td colspan="4" id="compare_list_prodprice_period" class="compare-list-removed" style="border-top: 1px;"></td></tr></tfoot>
    </table>
    </td></tr>
    <tr>
        <th>Тип ставки:</th>
        <td id="compare_prodprice_fixed">
            <input type="checkbox" value="y" name="interest_rate_fixed" id="interest_rate_fixed" <%=readOnly?"disabled=\"disabled\"":""%> onClick="onRateTypeFixedChange();fieldChanged(this)" <%=taskJPA.isInterestRateFixed()?"checked":""%>> фиксированная<br />
            <input type="checkbox" value="y" name="interest_rate_derivative" id="interest_rate_derivative" <%=readOnly?"disabled=\"disabled\"":""%> class="interest_rate_derivative" onClick="indratesSync();onRateTypeFixedChange();fieldChanged(this)" <%=taskJPA.isInterestRateDerivative()?"checked":""%>> плавающая<br />
        </td>
    </tr>
    <tr class="floatOnly"><th>Индикативная ставка</th>
        <td id="compare_prodprice_indrate">
            <table class="regular" id="indrateTable">
                <tbody>
                <%for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()){
                    if(indrate.getIdFactpercent()!=null || !taskJPA.isInterestRateDerivative()){continue;}%>
                <tr><td><select name="indRate" onchange="indratesSync();recalculatePercentRate();recalculateFondRate();fieldChanged(this);$('#rate_ind_rate_span<%=indrate.getId()%>').text($('option:selected',this).text())" <%=readOnly && !isCanEditFund ?"DISABLED":"" %>>
                    <%for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){ %>
                    <option value="<%=fpar.getId() %>" <%=fpar.getId().equals(indrate.getIndrate())?"SELECTED":"" %>
                            ><%=fpar.getText() %></option>
                    <%} %>
                </select>
                    <md:inputMoney name="indRate_value" value="<%=Formatter.format(indrate.getValue()) %>" styleClass="money" readonly="<%=readOnly%>" /> % годовых
                    <p>Применяется с <md:input name="indRateReason_usefrom" value="<%=Formatter.format(indrate.getUsefrom()) %>" readonly="<%=readOnly %>" styleClass="text date"
                                               onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                    <p>Основание: <%if(readOnly){ %><%=indrate.getReason()%><%}else{ %><textarea cols="40" class="expand50-200" name="indRateReason"><%=indrate.getReason()%></textarea><%} %></p>
                <%if (!readOnly || isCanEditFund) {%><button class="del" onclick="$('#rate_ind_rate_span<%=indrate.getId()%>').parent().parent().remove();$(this).parent().remove();indratesSync();return false"></button><%}%></td></tr><%}%>
                </tbody>
                <tfoot>
                <%if (!readOnly || isCanEditFund) {%>
                <tr><td colspan=2 class="add">
                    <button onclick="$('#indrateTableTemplate').tmpl().appendTo('#indrateTable > TBODY');add_rate_ind_rate('',getNextId());calendarInit();return false;" class="add"></button>
                </td></tr><%}%>
                </tfoot>
            </table>
        </td>
    </tr>
    <tr><th>Ставка зафиксирована</th>
    <td id="compare_prodprice_isfixed"><input type="checkbox" <%=readOnly?"disabled=\"disabled\"":""%>
               name="fixrate_percent" <%=taskJPA.isFixrate()?"checked=\"checked\"":"" %> value="y"></td>
    </tr>
    <tr><th>Дата фиксации процентной ставки</th>
    <td id="compare_prodprice_fixrate_date"><%=com.vtb.util.Formatter.format(taskJPA.getFixratedate()) %></td>
    </tr>
    <tr><th>Решение о понижении ставки</th>
    <td id="compare_prodprice_ratedesc_decision"><%if(readOnly && !isCanEditFund){ %><%=taskJPA.getRate_desc_decision() %><%}else{ %>
    <select name="fundDown" onchange="fieldChanged(this)"><option value="0"></option>
        <%for(ru.md.spo.dbobjects.FundDownJPA fundDown : taskFacade.findFundDown()){ %>
        <option value="<%=fundDown.getId() %>" <%=fundDown.equals(taskJPA.getFundDown())?"selected":"" %>
        ><%=fundDown.getText() %></option>
        <%} %>
    </select>
    <%} %></td>
    </tr>
    <tr align="left"><th>Компенсирующий спрэд за фиксацию процентной ставки</th><td id="compare_prodprice_rate5">
        <md:input name="rate5" value="<%=Formatter.format(taskJPA.getRate5()) %>" readonly="<%=readOnly && !isCanEditFund %>" id="percentFactrate5" onChange="fieldChanged(this)" styleClass="money"/> % годовых
        <%if(!readOnly || isCanEditFund){ %>
        <a href="javascript:;" class="dialogActivator" dialogId="StavspredTemplateDiv"
            onclick="$('#StavspredID').val('percentFactrate5');">
        <img alt="выбрать" src="style/dots.png"></a><%} %>
    </td></tr>
    <tr align="left"><th>Компенсирующий спрэд за досрочное погашение</th><td id="compare_prodprice_rate6">
        <md:input name="rate6" value="<%=Formatter.format(taskJPA.getRate6()) %>" readonly="<%=readOnly && !isCanEditFund%>" id="percentFactrate6"  onChange="fieldChanged(this)" styleClass="money"/> % годовых
        <%if(!readOnly || isCanEditFund){ %>
        <a href="javascript:;" class="dialogActivator" dialogId="EarlyRepaymentTemplateDiv"
            onclick="$('#StavspredID').val('percentFactrate6');">
        <img alt="выбрать" src="style/dots.png"></a><%} %>
    </td></tr>
    <tr align="left"><th>Покрытие прямых расходов</th><td id="compare_prodprice_rate7">
    <md:input name="rate7" value="<%=Formatter.format(taskJPA.getRate7()) %>" 
    readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate();fieldChanged(this)" id="rate7"  styleClass="money"/> % годовых
    <%if(!readOnly || isCanEditFund){ %>
        <a href="javascript:;" class="dialogActivator" dialogId="StavDefrayalExesDirectTemplateDiv"
            onclick="$('#StavspredID').val('rate7');">
        <img alt="выбрать" src="style/dots.png"></a><%} %></td></tr>
    <tr align="left"><th>Покрытие общебанковских расходов</th><td id="compare_prodprice_rate8"><md:input name="rate8" id="rate8"
    value="<%=Formatter.format(taskJPA.getRate8()) %>" readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate();fieldChanged(this)" styleClass="money"/>  % годовых
    <%if(!readOnly || isCanEditFund){ %>
        <a href="javascript:;" class="dialogActivator" dialogId="StavDefrayalExesCommonTemplateDiv"
            onclick="$('#StavspredID').val('rate8');">
        <img alt="выбрать" src="style/dots.png"></a><%} %></td></tr>
    <%-- %>    
    <tr align="left"><th>Комиссия за выдачу</th><td id="compare_prodprice_rate9"><md:inputMoney name="rate9" value="<%=Formatter.format(taskJPA.getRate9()) %>" 
    readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="rate9" /> % годовых</td></tr>
    <tr align="left"><th>Комиссия за сопровождение</th><td id="compare_prodprice_rate10"><md:inputMoney name="rate10" value="<%=Formatter.format(taskJPA.getRate10()) %>" 
    readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="rate10" /> % годовых</td></tr>
    <% --%>
</table>
<h3>Прогнозные значения <img src="theme/img/icon-refresh.gif" alt="обновить"
onclick="$('#percentCRM').html('Загружается...');loadPercentCRM()"></h3>
<div id="percentCRM">Загружается...</div>
<jsp:include flush="true" page="percentFact.jsp"/>
<script language="javaScript">
$(document).ready(function() {
    loadPercentCRM();
    onRateTypeFixedChange();
    updateProductWithPeriod();
    jQuery("textarea[class*=expand]").TextAreaExpander();
});
var riskStepupFactorHash = {};
<%for(ru.md.dict.dbobjects.RiskStepupFactorJPA r : ru.md.helper.TaskHelper.dict().findRiskStepupFactor()){%>
riskStepupFactorHash['<%=r.getItem_id()%>'] = '<%=r.getText()%>';
<%}%>
</script>

<script id="indrateTablePeriodTemplate" type="text/x-jquery-tmpl">
<tr><td><select name="indRate${id}" onchange="indratesSync();recalculatePercentRate();recalculateFondRate();fieldChanged(this)" <%=readOnly && !isCanEditFund ?"DISABLED":"" %>><option></option>
                            <%for(FloatPartOfActiveRate fpar : TaskHelper.getIndRateOptions(taskJPA.getId())){ %><option value="<%=fpar.getId() %>"><%=fpar.getText() %></option><%} %>
                        </select>
                            <md:inputMoney name="indRate_value${id}" value="" styleClass="money" readonly="<%=readOnly%>" /> % годовых
                            <p>Применяется с <md:input name="indRateReason_usefrom${id}" value="" readonly="<%=readOnly %>" styleClass="text date"
                                                       onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                            <p>Основание: <textarea  class="expand50-200" name="indRateReason${id}"></textarea></p>
                        </td></tr>
</script>
<script id="indrateTableTemplate" type="text/x-jquery-tmpl">
<tr><td><select name="indRate" onchange="indratesSync();recalculatePercentRate();recalculateFondRate();fieldChanged(this)" <%=readOnly && !isCanEditFund ?"DISABLED":"" %>><option></option>
                    <%for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){ %><option value="<%=fpar.getId() %>"><%=fpar.getText() %></option><%} %>
                </select><md:inputMoney name="indRate_value" value="" styleClass="money" readonly="false" /> % годовых
                    <p>Применяется с <md:input name="indRateReason_usefrom" value="" readonly="false" styleClass="text date"
                                               onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                    <p>Основание: <textarea class="expand50-200" cols="40" name="indRateReason"></textarea></p></td></tr>
</script>

<input id="StavspredID" type="hidden">
<div id="StavspredTemplateDiv" title="Выбрать компенсирующий спрэд за фиксацию процентной ставки" style="display: none">
    <ol>
        <%java.util.HashSet<String> set = new HashSet<String>();
        for(ru.md.spo.dbobjects.StavspredJPA spread : dictFacade.findStavspred(taskJPA.getCurrency(),taskJPA.getPeriod())){
                    if(spread.getStav_spred()==null || !spread.getUnit().equals(taskJPA.getCurrency())){continue;}
                    set.add(spread.getStavSpredFormatedInPercent());
        }
        for(String stavSpred : set){ %>
            <li>
            <a href="javascript:;" class="disable-decoration"
                    onclick="$('#'+$('#StavspredID').val()).val('<%=stavSpred %>');recalculatePercentRate();fieldChanged();">
                    <%=stavSpred %></a>
            </li>
        <%} %>
    </ol>
</div>

<div id="EarlyRepaymentTemplateDiv" title="Выбрать компенсирующий спрэд за досрочное погашение" style="display: none">
    <ol>
	    <%if(!taskJPA.isEarly_payment_prohibition()){
	        for(ru.md.dict.dbobjects.EarlyRepaymentJPA spread : dictFacade.findEarlyRepayment(taskJPA.getCurrency(),taskJPA.getPeriod())){
	                    if(spread.getSpread()==null || !spread.getCurrency().equals(taskJPA.getCurrency())){continue;} %>
	            <li>
	            <a href="javascript:;" class="disable-decoration"
	                    onclick="$('#'+$('#StavspredID').val()).val('<%=spread.getSpredFormatedInPercent() %>');recalculatePercentRate();fieldChanged();">
	                    <%=spread.getSpredFormatedInPercent() %> (<%=Formatter.format(spread.getActivedate()) %>)</a>
	            </li>
	    <%}}else{
	        for(ru.md.spo.dbobjects.DependingLoanJPA spread : dictFacade.findDependingLoan(taskJPA.getCurrency(),taskJPA.getPeriod())){
	                    if(spread.getSpread()==null || !spread.getId_currency().equals(taskJPA.getCurrency())){continue;} 
	                    if(taskJPA.getEarly_payment_proh_per()==null || !spread.getId().equals(taskJPA.getEarly_payment_proh_per())){continue;} 
	                    %>
	            <li>
	            <a href="javascript:;" class="disable-decoration" 
	                    onclick="$('#'+$('#StavspredID').val()).val('<%=spread.getSpredFormatedInPercent() %>');recalculatePercentRate();">
	                    <%=spread.getSpredFormatedInPercent() %> (Срок моратория <%=spread.getDays_ban_to() %>)</a>
	            </li>
	    <%}} %>
    </ol>
</div>
<%
    String clientcategory = "";
    if(taskJPA.getOrgList().size()>0){
        clientcategory = taskJPA.getOrgList().get(0).getClientcategory();
    }
%>
<div id="StavDefrayalExesCommonTemplateDiv" title="Выбрать покрытие общебанковских расходов" style="display: none;">
    <ol>
        <%for(com.vtb.domain.StavDefrayalExes stav : 
            dictFacade.findStavDefrayalExes(com.vtb.domain.StavDefrayalExes.StavDefrayalExesType.COMMONBANK,
                    clientcategory)){%>
            <li>
            <a href="javascript:;" class="disable-decoration"
                    onclick="$('#'+$('#StavspredID').val()).val('<%=Formatter.format(stav.getStavvalue()) %>');recalculatePercentRate();fieldChanged();">
                    <%=Formatter.format(stav.getStavvalue()) %></a>
            </li>
        <%} %>
    </ol>
</div>

<div id="StavDefrayalExesDirectTemplateDiv" title="Выбрать покрытие прямых расходов" style="display: none">
    <ol>
        <%for(com.vtb.domain.StavDefrayalExes stav : 
            dictFacade.findStavDefrayalExes(com.vtb.domain.StavDefrayalExes.StavDefrayalExesType.DIRECT,
                    clientcategory)){%>
            <li>
            <a href="javascript:;" class="disable-decoration" 
                    onclick="$('#'+$('#StavspredID').val()).val('<%=Formatter.format(stav.getStavvalue()) %>');recalculatePercentRate();fieldChanged();">
                    <%=Formatter.format(stav.getStavvalue()) %></a>
            </li>
        <%} %>
    </ol>
</div>

