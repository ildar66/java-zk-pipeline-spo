<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page isELIgnored="true" %>
<%@page import="ru.md.spo.dbobjects.CdPremiumTypeJPA"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.dbobjects.CdRiskpremiumJPA"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.md.domain.ContitionTemplate"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.util.Formatter"%>
<%@ page import="ru.md.spo.dbobjects.IndrateMdtaskJPA" %>
<%@ page import="ru.md.controller.PriceController" %>
<%@ page import="com.vtb.domain.Task" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

    boolean readOnly = !TaskHelper.isEditMode("Стоимостные условия", request);
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
Task task=TaskHelper.getTask4PriceCondition(Long.valueOf(request.getParameter("mdtaskid")), request.getParameter("monitoringmode") != null);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
String premiumStyle=taskJPA.isDocumentary()?"":"style=\"display:none\"";
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>

<%if(!readOnly || isCanEditFund) { %><input type="hidden" value="y" name="percentFact_section"><%} %>
<script id="newPeriodTemplate" type="text/x-jquery-tmpl">
<tr class="FactPercentClass ${id}"><td>новый</td>
    <td><md:calendarium name="percentFactDate1" value="" readonly="<%=readOnly %>"  id="start${id}" /></td>
    <td><md:calendarium name="percentFactDate2" value="" readonly="<%=readOnly %>" id="end${id}" />
    </td><td><button class="del" onclick="delPeriod('${id}')"></button></td></tr>
</script>
<div id="rate4SelectTemplateDiv" title="Выбрать ставку размещения" style="display: none;">
    <ol>
        <%for(ContitionTemplate t : ru.masterdm.spo.utils.SBeanLocator.singleton().getCompendiumMapper().findConditionTemplate(7L)){ %>
            <li>
                <a href="javascript:;" class="disable-decoration"
                    onclick="$('#'+$('#loanRateTemplateHiddenInput').val()).val('<%=t.getName()%>');">
                    <%=t.getName() %></a>
            </li>
        <%}%>
    </ol>
</div>

<input type="hidden" id="loanRateTemplateHiddenInput" name="loanRateTemplateHiddenInput" value="">

<h3>Фактические значения</h3>
<div id="percentFact">
	<%int i=1;
    for(ru.md.spo.dbobjects.FactPercentJPA per : taskJPA.getFactPercents()){
        String id = "percentFact" + per.getId().toString();
        String indRate_value_name = "indRate_value"+id;
        String indRateReason_usefrom_name = "indRateReason_usefrom"+id;
        if(per.getTranceId()!=null){continue;}//значит ставка не для периода, а для транша
    %>
		<div class="period<%=per.getId().toString()%>">
		    <input type="hidden" class="percentFactID" name="percent_fact_id" value="<%=id %>" />
			<h4 class="periodHeader">Период №<%=i++ %> с <%=Formatter.format(per.getStart_date()) %> по <%=Formatter.format(per.getEnd_date()) %></h4>
			<table class="regular leftPadd" style="width: 99%;">
                <tr class="periodOnly">
                    <th style="width: 50%;">Тип ставки:</th>
                    <td id="compare_prodprice_fixed" style="width: 50%;">
                        <input type="checkbox" value="y" name="interest_rate_fixed<%=id %>" <%=readOnly?"disabled=\"disabled\"":""%> class="interest_rate_fixed"  onClick="onRateTypeFixedChange();fieldChanged(this)" <%=per.isInterestRateFixed()?"checked":""%>> фиксированная<br />
                        <input type="checkbox" value="y" name="interest_rate_derivative<%=id %>" <%=readOnly?"disabled=\"disabled\"":""%> class="interest_rate_derivative" onClick="onRateTypeFixedChange();fieldChanged(this)" <%=per.isInterestRateDerivative()?"checked":""%>> плавающая<br />
                    </td>
                </tr>
			    <tr class="periodOnly floatOnly<%=id %>"  align="left" style="display:none"><th>Индикативная ставка</th><td>
                    <table class="regular" id="indrateTable<%=id %>">
                        <tbody>
                        <%for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()){
                        if(indrate.getIdFactpercent()==null || !indrate.getIdFactpercent().equals(per.getId()) || !per.isInterestRateDerivative()){continue;}%>
                        <tr><td><select name="indRate<%=id %>"
                                        onchange="recalculatePercentRate();recalculateFondRate();fieldChanged(this);$('#rate_ind_rate_span<%=indrate.getId()%>').text($('option:selected',this).text())" <%=readOnly && !isCanEditFund ?"DISABLED":"" %>>
                            <option></option>
                            <%for(FloatPartOfActiveRate fpar : TaskHelper.getIndRateOptions(taskJPA.getId())){ %>
                            <option value="<%=fpar.getId() %>" <%=fpar.getId().equals(indrate.getIndrate())?"SELECTED":"" %>
                                    ><%=fpar.getText() %></option>
                            <%} %>
                        </select>
                            <md:inputMoney name="<%=indRate_value_name %>" value="<%=Formatter.format(indrate.getValue()) %>" styleClass="money" readonly="<%=readOnly%>" /> % годовых
                            <p>Применяется с <md:input name="<%=indRateReason_usefrom_name %>" value="<%=Formatter.format(indrate.getUsefrom()) %>" readonly="<%=readOnly %>" styleClass="text date"
                                                       onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                            <p>Основание: <%if(readOnly){ %><%=indrate.getReason()%><%}else{ %><textarea  class="expand50-200" name="indRateReason<%=id %>"><%=indrate.getReason()%></textarea><%} %></p>
                        <%if (!readOnly || isCanEditFund) {%><button class="del" onclick="$('#rate_ind_rate_span<%=indrate.getId()%>').parent().parent().remove();$(this).parent().remove();indratesSync();return false"></button><%}%></td></tr>
                        <%}%>
                        </tbody>
                        <tfoot>
                        <%if (!readOnly || isCanEditFund) {%>
                        <tr><td colspan=2 class="add">
                            <button onmouseover="Tip('Добавить')" onmouseout="UnTip()" onclick="$('#indrateTablePeriodTemplate').tmpl({id:'<%=id %>'}).appendTo('#indrateTable<%=id %> > TBODY');add_rate_ind_rate('<%=id %>',getNextId());calendarInit();return false;" class="add"></button>
                        </td></tr><%}%>
                        </tfoot>
                    </table>
			    </td></tr>
			    <tr align="left"><th style="width: 50%">Расчетная ставка</th><td id="<%=id%>calcRate" style="width: 50%"><%=Formatter.format(per.getCalcRate1()) %> % годовых</td></tr>
			    <tr align="left"><th>Расчетная защищенная ставка</th><td id="<%=id%>calcRateProtected"><%=Formatter.format(per.getCalcRate2()) %> % годовых</td></tr>
			    <tr align="left"><th class="periodFondRateTH">Ставка фондирования<%=taskJPA.isAmortized_loan()?" с амортизацией":"" %></th><td id="compare_prodprice_period<%=per.getId()%>_fondrate">
			    <md:inputMoney name="fondrate" value="<%=Formatter.format(per.getFondrate()) %>" styleClass="money periodFondRate"
			    readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate();$(this).next('input').val('y');$(this).next().next('span').show();$('.periodFondRate').val($(this).val());" id="<%=id%>" idsfx="fondrate"  /><input type="hidden" name="period_fondrate_manual" value="<%=per.isManualFondrate()?"y":"n"%>">
                        <span class="exclamation_mark" style="display: <%=per.isManualFondrate()?"inline":"none"%>;"> !</span> % годовых</td></tr>
			    <tr align="left"><th>Тип премии за кредитный риск</th><td id="compare_prodprice_period<%=per.getId()%>_riskpremium_type">
			        <%if(!readOnly){ %><a class="dialogActivator" dialogId="riskpremiumTypeTemplate" onclick="$('#percentID').val('<%=id %>');" href="javascript:;"><%} %>
                    <span id="riskpremium_type_name<%=id%>"><%=per.getRiskpremiumTypeDisplay() %></span><%if(!readOnly){ %></a><%} %>
                    <input name="riskpremiumtype" id="riskpremium_type<%=id %>" value="<%=per.getRiskpremiumtypeID()%>" type="hidden">
			    </td></tr>
			    <tr id="riskpremium_change_tr<%=id %>" <%=per.showRiskpremiumChange()?"":"style=\"display:none\"" %> align="left">
			    <th id="riskpremium_change_name<%=id%>">Величина изменения</th>
			        <td id="compare_prodprice_period<%=per.getId()%>_riskpremium_change"><md:inputMoney name="riskpremium_change" value="<%=Formatter.format(per.getRiskpremium_change()) %>" 
                readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate();fieldChanged(this)" id="<%=id%>" idsfx="riskpremium_change" /> % годовых</td></tr>
			    <tr align="left"><th>Премия за кредитный риск</th><td id="compare_prodprice_period<%=per.getId()%>_riskpremium">
			    <md:inputMoney name="riskpremium" value="<%=Formatter.format(per.getRiskpremium()) %>" 
			    readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate()" id="<%=id%>" idsfx="riskpremium" /> % годовых
			    </td></tr>
			    <tr align="left"><th>Индивидуальные условия</th><td id="compare_prodprice_period<%=per.getId()%>_indcond">
			    <%if(readOnly){ %><%=per.getIndcondition()%><%}else{ %><textarea name="factPercentIndCondition"><%=per.getIndcondition()%></textarea><%} %>
                </td></tr>
			    <tr align="left"><th>Плата за экономический капитал</th><td id="compare_prodprice_period<%=per.getId()%>_rate3"><md:inputMoney name="rate3" id="<%=id%>" idsfx="rate3"
			    value="<%=Formatter.format(per.getRate3()) %>" readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate()" /> % годовых</td></tr>
			    <tr align="left"><th>Повыш. коэфф. за риск</th><td id="compare_prodprice_period<%=per.getId()%>_riskstepupfactor">
			    <md:RiskStepupFactor readonly="<%=readOnly && !isCanEditFund%>" name="riskStepupFactor" value="<%=per.getRiskStepupFactorID() %>" id="<%=id%>" idsfx="riskStepupFactor"/>
			    </td></tr>
			    <tr align="left"><th>Ставка размещения</th><td>
			    <div id="compare_prodprice_period<%=per.getId()%>_rate4" class="compare-elem"><md:inputMoney name="rate4" id="<%=id%>" idsfx="rate4" 
			    value="<%=Formatter.format(per.getRate4()) %>" readonly="<%=readOnly && !isCanEditFund %>" onChange="recalculatePercentRate()" /> % годовых<br /></div>
			    <div id="compare_prodprice_period<%=per.getId()%>_rate4desc" class="compare-elem">
			    <%if(readOnly){ %><%=per.getRate4Desc() %>
			    <%}else{ %>
			    	<textarea name="rate4desc" onkeyup="fieldChanged(this)" rows="5" id="rate4desc<%=id%>"><%=per.getRate4Desc() %></textarea>
			    	<a href="javascript:;" class="dialogActivator" dialogId="rate4SelectTemplateDiv"
									   onclick="$('#loanRateTemplateHiddenInput').val('rate4desc<%=id%>');">
									   <img alt="выбрать из шаблона" src="style/dots.png"></a>
			    <%} %></div>
                    <div class="rate4fixedOnly rate4fixedOnlyDiv<%=id %>">
                    <p>Применяется с <md:input name="rate4_usefrom" value="<%=Formatter.format(per.getUsefrom()) %>" readonly="<%=readOnly %>" styleClass="text date"
                                               onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                    <p>Основание: <%if(readOnly){ %><%=per.getReason()%><%}else{ %><textarea class="expand50-200" name="rate4_reason"><%=per.getReason()%></textarea><%} %></p>
                    </div>
			    </td></tr>
                <%for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()){
                    if(indrate.getIdFactpercent()==null || !indrate.getIdFactpercent().equals(per.getId()) || !per.isInterestRateDerivative()){continue;}
                    String name = "rate_ind_rate_"+id;
                %>
                <tr align="left" class="periodOnly floatOnly<%=id %>"><th>Надбавка к плавающей ставке <span id="rate_ind_rate_span<%=indrate.getId()%>"><%=TaskHelper.getIndRateNameById(indrate.getIndrate())%></span></th>
                    <td><md:inputMoney name="<%=name %>" value="<%=Formatter.format(indrate.getRate()) %>"
                                       readonly="<%=readOnly && !isCanEditFund %>" /> % годовых</td></tr>
                <%}%>
                <%if(i==2){
                    for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()){
                    if(indrate.getIdFactpercent()!=null || !taskJPA.isInterestRateDerivative()){continue;}
                %>
                <tr align="left" class="noneperiodOnly floatOnly"><th>Надбавка к плавающей ставке <span id="rate_ind_rate_span<%=indrate.getId()%>"><%=TaskHelper.getIndRateNameById(indrate.getIndrate())%></span></th>
                    <td><md:inputMoney name="rate_ind_rate" value="<%=Formatter.format(indrate.getRate()) %>"
                                       readonly="<%=readOnly && !isCanEditFund %>" /> % годовых</td></tr>
                <%}}%>
			    <tr align="left" id="<%=id%>effRateTr" class="effRateTr<%=i%>"><th>Эффективная ставка</th><td id="<%=id%>effRate"><%=Formatter.format(per.getCalcRate3(PriceController.getComissionSum(taskJPA.getId()))) %> % годовых</td></tr>
			    <tr align="left"><th>КТР</th><td id="compare_prodprice_period<%=per.getId()%>_rate11">
			    	<md:inputMoney name="rate11" value="<%=Formatter.format(per.getRate11()) %>" 
			    readonly="<%=readOnly && !isCanEditFund %>" id="<%=id %>" idsfx="rate11" /></td></tr>
			    <tr align="left"><th>Обеспечение по периоду</th><td id="compare_prodprice_period<%=per.getId()%>_supply">
			    <%if(readOnly){ %><%=per.getSupply()%><%}else{ %><textarea onkeyup="fieldChanged(this)" name="supply"><%=per.getSupply()%></textarea><%} %>
			    </td></tr>

			    <tr align="left" <%=premiumStyle %>><th>Вознаграждения</th><td>
			        <select name="premiumtype" id="premiumtype<%=per.getId()%>" <%=readOnly?"disabled":"" %> 
                        onchange="onPremiumTypeClick('<%=per.getId()%>')">
                        <option value="0"></option>
                    <%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.PRODUCT)){ %>
                        <option value="<%=pt.getId()%>" <%=pt.equals(per.getPremiumType())?"selected":"" %>
                        ><%=pt.getPremium_name()%></option>
                    <%} %>
                    </select>
			    </td></tr>
			    <tr align="left" <%=premiumStyle %> id="premiumSizeTr<%=per.getId()%>"><th>Размер вознаграждения</th><td>
    			    <span id="premiumSize<%=per.getId()%>"></span> 
                    <md:inputMoney name="premiumvalue" value="<%=Formatter.toMoneyFormat(per.getPremiumvalue()) %>" 
                        id="premiumvalue" idsfx="<%=per.getId().toString() %>" readonly="<%=readOnly %>"  styleClass="text money" 
                        onBlur="input_autochange(this,'money')" />
                    <md:currency id="premiumcurr" idsfx="<%=per.getId().toString() %>"  name="premiumcurr" readonly="<%=readOnly %>" 
                        value="<%=per.getPremiumcurr() %>" 
                        withoutprocent="true" />
                    <md:currency id="premiumcurrpercent" idsfx="<%=per.getId().toString() %>" name="premiumcurrpercent" readonly="<%=readOnly %>" 
                        value="<%=per.getPremiumcurr() %>" 
                        withoutprocent="false" />
                    <textarea id="premiumtext<%=per.getId().toString() %>" name="premiumtext" <%=readOnly?"disabled":"" %>><%=per.getPremiumtext() %></textarea>
		    </td></tr>
			    <tr align="left" <%=premiumStyle %>><th>Порядок уплаты процентов по кредиту/кредитной линии<br /> с лимитом выдачи на цели формирования покрытия<br /> для осуществления платежей по аккредитивам</th><td>ежемесячно</td></tr>
			</table>
		</div>
	<%}%>
</div>
<%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.PRODUCT)){ %>
    <span id="premiumTradeValue<%=pt.getId()%>" style="display: none;"><%=pt.getValue() %></span>
<%} %>
<script language="javaScript">
$().ready(function() {
<%if(!readOnly){%>
setTimeout("recalculatePercentRate()", 2000);
<%}%>

<%for(ru.md.spo.dbobjects.FactPercentJPA per : taskJPA.getFactPercents()){%>
    onPremiumTypeClick('<%=per.getId() %>');
<%}%>
    $('.autosize').autosize();
    showHidePriceConditionFields();
});
</script>

<script id="newPercentFactTemplate" type="text/x-jquery-tmpl">
<div class="${id}">
            <input type="hidden" class="percentFactID" name="percent_fact_id" value="${id}" />
            <h4 class="periodHeader">Период новый</h4>
            <table class="regular" style="width: 99% !important;">
                <tr class="periodOnly">
                    <th style="width: 50%">Тип ставки:</th>
                    <td id="compare_prodprice_fixed" style="width: 50%">
                        <input type="checkbox" value="y" name="interest_rate_fixed${id}" class="interest_rate_fixed" onClick="onRateTypeFixedChange();fieldChanged(this)" > фиксированная<br />
                        <input type="checkbox" value="y" name="interest_rate_derivative${id}" class="interest_rate_derivative" onClick="onRateTypeFixedChange();fieldChanged(this)"> плавающая<br />
                    </td>
                </tr>
                <tr class="periodOnly floatOnly${id}"  align="left" style="display:none"><th style="width: 50%">Индикативная ставка</th><td style="width: 50%">
                    <table class="regular" id="indrateTable${id}">
                        <tbody>
                        <tr style="display:none"><td><select name="indRate${id}" onchange="recalculatePercentRate();recalculateFondRate();fieldChanged(this)"><option></option>
                            <%for(FloatPartOfActiveRate fpar : TaskHelper.getIndRateOptions(taskJPA.getId())){ %><option value="<%=fpar.getId() %>"><%=fpar.getText() %></option><%} %>
                        </select></td></tr>
                        </tbody>
                        <tfoot>
                        <tr><td colspan=2 class="add">
                            <button onmouseover="Tip('Добавить')" onmouseout="UnTip()" onclick="AddRowToTable('indrateTable${id}');add_rate_ind_rate('${id}',getNextId());return false;" class="add"></button>
                            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRow('indrateTable${id}', 'indrateTableChk${id}'); return false;" class="del"></button>
                        </td></tr>
                        </tfoot>
                    </table>
			    </td></tr>
                <tr><th style="width: 50%">Расчетная ставка</th><td id="${id}calcRate" style="width: 50%">Нередактируемое поле. Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск + Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + Покрытие прямых расходов + Покрытие общебанковских расходов + Плата за экономический капитал</td></tr>
                <tr><th>Расчетная защищенная ставка</th><td id="${id}calcRateProtected">Нередактируемое поле. Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск + Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + Покрытие прямых расходов</td></tr>
                <tr><th class="periodFondRateTH">Ставка фондирования</th><td><md:inputMoney name="fondrate" value="" styleClass="periodFondRate money"
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate();$(this).next('input').val('y');$(this).next().next('span').show();$('.periodFondRate').val($(this).val());" id="${id}" idsfx="fondrate"  /><input type="hidden" name="period_fondrate_manual" value="y">
                        <span class="exclamation_mark" style="display: inline;"> !</span> % годовых</td></tr>

                <tr><th>Тип премии за кредитный риск</th><td>
                    <a class="dialogActivator" dialogId="riskpremiumTypeTemplate" onclick="$('#percentID').val('${id}');" href="javascript:;">
                    <span id="riskpremium_type_name${id}">не выбрана</span></a>
                    <input name="riskpremiumtype" id="riskpremium_type${id}" value="" type="hidden">
                </td></tr>
                <tr id="riskpremium_change_tr${id}" style="display:none">
                <th id="riskpremium_change_name${id}">Величина изменения</th>
                    <td><md:inputMoney name="riskpremium_change" value="" 
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="${id}" idsfx="riskpremium_change" /> % годовых</td></tr>

                <tr><th>Премия за кредитный риск</th><td><md:inputMoney name="riskpremium" value=">" 
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="${id}" idsfx="riskpremium" /> % годовых</td></tr>
				<tr align="left"><th>Индивидуальные условия</th><td>
			    <textarea name="factPercentIndCondition"></textarea>
                </td></tr>
                <tr><th>Плата за экономический капитал</th><td><md:inputMoney name="rate3" id="${id}" idsfx="rate3"
                value="" readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" /> % годовых</td></tr>
                <tr align="left"><th>Повыш. коэфф. за риск</th><td>
                <md:RiskStepupFactor readonly="<%=readOnly %>" name="riskStepupFactor" value="" id="${id}" idsfx="riskStepupFactor"/>
                </td></tr>
                <tr><th>Ставка размещения</th><td><md:inputMoney name="rate4" id="${id}" idsfx="rate4" 
                value="" readonly="<%=readOnly %>" onChange="recalculatePercentRate()" /> % годовых<br />
			    	<textarea name="rate4desc" class="autosize" id="rate4desc${id}"></textarea>
			    	<a href="javascript:;" class="dialogActivator" dialogId="rate4SelectTemplateDiv"
									   onclick="$('#loanRateTemplateHiddenInput').val('rate4desc${id}');">
									   <img alt="выбрать из шаблона" src="style/dots.png"></a>
									   <div class="rate4fixedOnly rate4fixedOnlyDiv${id}">
									   <p>Применяется с <md:input name="rate4_usefrom" value="" readonly="<%=readOnly %>" styleClass="text date"
                                                                  onFocus="displayCalendarWrapperNoId(this, '', false); return false;" onChange="input_autochange(this,'date');" /></p>
                    <p>Основание: <textarea class="expand50-200" name="rate4_reason"></textarea></p></div>
			    </td></tr>
                <tr><th>КТР</th><td><md:inputMoney name="rate11" value="" 
                readonly="<%=readOnly %>" /></td></tr>
                <tr id="${id}effRateTr"><th>Эффективная ставка</th><td id="${id}effRate">Рассчитывается по формуле: Ставка размещения + Комиссия за выдачу + Комиссия за сопровождение. Нередактируемое поле.</td></tr>
                <tr><th>Обеспечение по периоду</th><td>
                <textarea name="supply"></textarea>
                </td></tr>
                <tr align="left" <%=premiumStyle %>><th>Вознаграждения</th><td>
                    <select name="premiumtype" id="premiumtype${id}" 
                        onchange="onPremiumTypeClick('${id}')">
                        <option value="0"></option>
                    <%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.PRODUCT)){ %>
                        <option value="<%=pt.getId()%>"
                        ><%=pt.getPremium_name()%></option>
                    <%} %>
                    </select>
                </td></tr>
                <tr align="left" <%=premiumStyle %> id="premiumSizeTr${id}" style="display:none"><th>Размер вознаграждения</th><td>
                    <span id="premiumSize${id}"></span> 
                    <md:inputMoney name="premiumvalue" value="" 
                        id="premiumvalue" idsfx="${id}" readonly="<%=readOnly %>"  styleClass="text money" 
                        onBlur="input_autochange(this,'money')" />
                    <md:currency id="premiumcurr" idsfx="${id}"  name="premiumcurr" readonly="<%=readOnly %>" 
                        value="" withoutprocent="true" />
                    <md:currency id="premiumcurrpercent" idsfx="${id}" name="premiumcurrpercent" readonly="<%=readOnly %>" 
                        value=""  withoutprocent="false" />
                    <textarea id="premiumtext${id}" name="premiumtext"></textarea>
            </td></tr>
                <tr align="left" <%=premiumStyle %>><th>Порядок уплаты процентов по кредиту/кредитной линии<br /> с лимитом выдачи на цели формирования покрытия<br /> для осуществления платежей по аккредитивам</th><td>ежемесячно</td></tr>
            </table>
        </div>
</script>

<input type="hidden" id="percentID">
<div id="riskpremiumTypeTemplate" title="Выбрать тип премии за кредитный риск" style="display: none;">
	<ul>
		<%for(CdRiskpremiumJPA to: taskFacadeLocal.findCdRiskpremium()){ %>
		    <li>
		        <a class="disable-decoration" href="javascript:;" onclick="riskpremiumTypeTemplateClick('<%=to.getDescription()%>','<%=to.getValue()%>','<%=to.getId()%>',$('#percentID').val());fieldChanged();">
		            <%=to.getDescription() %>
		        </a>
		    </li>
		<%} %>
	</ul>
</div>










<%
    TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
    Task taskJDBC=processor.getTask(new Task(taskJPA.getId()));
i=1;
if((taskJDBC.getMain().isLimitIssue() || taskJDBC.getMain().isDebtLimit()) && taskJPA.isTrance_graph()){//только для них показываем транши
for(com.vtb.domain.Trance trance : taskJDBC.getTranceList()){
    ru.md.spo.dbobjects.FactPercentJPA per = null;
    for(ru.md.spo.dbobjects.FactPercentJPA taskper : taskJPA.getFactPercents()){
        if(taskper.getTranceId()!=null && taskper.getTranceId().longValue()==trance.getId().longValue()){per=taskper;}
    }
    Long compareId = (-1L) * i;
    if(per==null){
    	//значит, для этого транша еще не было процентной ставки
    	per = new ru.md.spo.dbobjects.FactPercentJPA();
    }
    else
    	compareId = per.getId();
    per.setId(Long.valueOf(9000+i));
    String id = "percentFactTrance" + per.getId().toString();
    String fondRateClass = "trance"+trance.getIdStr()+"fondRate  money trancefondrate";
    String htmlName = "compare_prodprice_trance" + compareId;
    %>
        <div class="period<%=per.getId().toString()%>">
            <h4>Транш №<%=i++ %></h4>
            <input type="hidden" name="trid" value="<%=trance.getIdStr()%>">
            <input type="hidden" class="trid" value="<%=id%>">
            <table class="regular leftPadd" style="width: 99%;">
                <tr><th class="periodFondRateTH" style="width: 50%;">Ставка фондирования<%=taskJPA.isAmortized_loan()?" с амортизацией":"" %></th><td id="<%=htmlName %>_fondrate" style="width: 50%;"><md:inputMoney name="trfondrate" value="<%=Formatter.format(per.getFondrate()) %>" 
                readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate();$(this).next('input').val('y');$(this).next().next('span').show()" id="<%=id%>" idsfx="trfondrate" styleClass="<%=fondRateClass%>" /><input type="hidden" name="trance_fondrate_manual" value="<%=per.isManualFondrate()?"y":"n"%>">
                        <span class="exclamation_mark" style="display: <%=per.isManualFondrate()?"inline":"none"%>;"> !</span> % годовых</td></tr>
                <tr><th>Тип премии за кредитный риск</th><td id="<%=htmlName %>_riskpremium_type">
                    <%if(!readOnly){ %><a class="dialogActivator" dialogId="riskpremiumTypeTemplate" onclick="$('#percentID').val('<%=id %>');" href="javascript:;"><%} %>
                    <span id="riskpremium_type_name<%=id%>"><%=per.getRiskpremiumTypeDisplay() %></span><%if(!readOnly){ %></a><%} %>
                    <input name="trriskpremiumtype" id="riskpremium_type<%=id %>" value="<%=per.getRiskpremiumtypeID()%>" type="hidden">
                </td></tr>
                <tr id="riskpremium_change_tr<%=id %>" <%=per.showRiskpremiumChange()?"":"style=\"display:none\"" %>>
                <th id="riskpremium_change_name<%=id%>">Величина изменения</th>
                    <td id="<%=htmlName %>_riskpremium_change"><md:inputMoney name="trriskpremium_change" value="<%=Formatter.format(per.getRiskpremium_change()) %>" 
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="<%=id%>" idsfx="trriskpremium_change" /> % годовых</td></tr>
                <tr><th>Премия за кредитный риск</th><td id="<%=htmlName %>_riskpremium">
                <md:inputMoney name="trriskpremium" value="<%=Formatter.format(per.getRiskpremium()) %>" 
                readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate()" id="<%=id%>" idsfx="trriskpremium" /> % годовых
                </td></tr>
                <tr><th>Плата за экономический капитал</th><td id="<%=htmlName %>_rate3"><md:inputMoney name="trrate3" id="<%=id%>" idsfx="trrate3"
                value="<%=Formatter.format(per.getRate3()) %>" readonly="<%=readOnly && !isCanEditFund %>"  onChange="recalculatePercentRate()" /> % годовых</td></tr>
                <tr align="left"><th>Повыш. коэфф. за риск</th><td id="<%=htmlName %>_riskstepupfactor">
                <md:RiskStepupFactor readonly="<%=readOnly && !isCanEditFund%>" name="trriskStepupFactor" value="<%=per.getRiskStepupFactorID() %>" id="<%=id%>" idsfx="trriskStepupFactor"/>
                </td></tr>
                <tr><th>КТР</th><td id="<%=htmlName %>_rate11"><md:inputMoney name="trrate11" value="<%=Formatter.format(per.getRate11()) %>" 
                readonly="<%=readOnly && !isCanEditFund %>" id="<%=id %>" idsfx="trrate11" /></td></tr>
                <tr><th>Расчетная ставка</th><td id="<%=id %>calcRate"><%=Formatter.format(per.getCalcRate1()) %> % годовых</td></tr>
                <tr><th>Расчетная защищенная ставка</th><td id="<%=id %>calcRateProtected"><%=Formatter.format(per.getCalcRate2()) %> % годовых</td></tr>
                <tr><th>Ставка размещения</th><td>
									<div id="<%=htmlName %>_rate4" class="compare-elem"><md:inputMoney name="trrate4" id="<%=id%>" idsfx="trrate4" 
											value="<%=Formatter.format(per.getRate4()) %>" readonly="<%=readOnly && !isCanEditFund %>" onChange="recalculatePercentRate()" /> % годовых</div><br />
									<div id="<%=htmlName %>_rate4desc" class="compare-elem">
			    <%if(readOnly){ %><%=per.getRate4Desc() %>
			    <%}else{ %>
			    	<textarea name="trrate4desc" rows="5" onkeyup="fieldChanged(this)" id="trrate4desc<%=id%>"><%=per.getRate4Desc() %></textarea>
			    	<a href="javascript:;" class="dialogActivator" dialogId="rate4SelectTemplateDiv"
									   onclick="$('#loanRateTemplateHiddenInput').val('trrate4desc<%=id%>');">
									   <img alt="выбрать из шаблона" src="style/dots.png"></a>
			    <%} %>
									</div>
                </td></tr>
                <tr><th>Эффективная ставка</th><td id="<%=id%>effRate"><%=Formatter.format(per.getCalcRate3(PriceController.getComissionSum(taskJPA.getId())))%> % годовых</td></tr>
                
                <tr align="left"><th>Компенсирующий спрэд за фиксацию процентной ставки</th><td id="<%=htmlName %>_rate5">
                    <input id="<%=id %>trrate5" name="trrate5" class="money" value="<%=Formatter.format(per.getRate5()) %>" type="text" <%if(readOnly && !isCanEditFund){%>readonly="readonly"<%} %>> % годовых
                    <%if(!readOnly || isCanEditFund){ %>
                    <a href="javascript:;" class="dialogActivator" dialogId="StavspredTemplateDiv"
                        onclick="$('#StavspredID').val('<%=id %>trrate5');">
                    <img alt="выбрать" src="style/dots.png"></a><%} %>
                </td></tr>
                <tr align="left"><th>Компенсирующий спрэд за досрочное погашение</th><td id="<%=htmlName %>_rate6">
	                <input id="<%=id %>trrate6" name="trrate6" class="money" value="<%=Formatter.format(per.getRate6()) %>" type="text" <%if(readOnly && !isCanEditFund){%>readonly="readonly"<%} %>> % годовых
        	        <%if(!readOnly || isCanEditFund){ %>
	                <a href="javascript:;" class="dialogActivator" dialogId="EarlyRepaymentTemplateDiv"
	                onclick="$('#StavspredID').val('<%=id %>trrate6');">
	                <img alt="выбрать" src="style/dots.png"></a><%} %>
                
                </td></tr>
                
                <tr align="left"><th>Покрытие прямых расходов</th><td class="rate7" id="<%=htmlName %>_rate7"><%=Formatter.format(taskJPA.getRate7()) %> % годовых</td></tr>
                <tr align="left"><th>Покрытие общебанковских расходов</th><td class="rate8" id="<%=htmlName %>_rate8"><%=Formatter.format(taskJPA.getRate8()) %> % годовых</td></tr>
                <%-- %>
                <tr align="left"><th>Комиссия за выдачу</th><td id="<%=htmlName %>_rate9"><md:inputMoney name="trrate9" value="<%=Formatter.format(per.getRate9()) %>" 
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="<%=id%>" idsfx="trrate9" /> % годовых</td></tr>
                <tr align="left"><th>Комиссия за сопровождение</th><td id="<%=htmlName %>_rate10"><md:inputMoney name="trrate10" value="<%=Formatter.format(per.getRate10()) %>" 
                readonly="<%=readOnly %>"  onChange="recalculatePercentRate()" id="<%=id%>" idsfx="trrate10" /> % годовых</td></tr>
                <% --%>
            </table>
        </div>
<%}}%>
