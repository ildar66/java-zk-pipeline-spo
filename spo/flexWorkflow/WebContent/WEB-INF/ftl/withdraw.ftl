<#list model["list"] as w>
  <tr><td>
  <select name="withdraw_sumScope">
  	<option value="noLess" <#if (w.sumScope)??><#if w.sumScope = "noLess"> selected="selected" </#if></#if>>Не менее</option>
  	<option value="noMore" <#if (w.sumScope)??><#if w.sumScope = "noMore"> selected="selected" </#if></#if>>Не более</option>
  	<option value="" <#if !(w.sumScope)??> selected="selected" </#if>></option>
  <select>	
  
  <input class="money" onBlur="input_autochange(this,'money')" value="${w.sumFormated}" name="withdraw_sum">
  <input type="hidden" name="withdraw_trance_id" value="${w.idTrance!''}">
  <select name="withdraw_cur">
       <option value=""></option>
       <#list model["currency_list"] as cur>
           <option value="${cur}" <#if w.currency = cur> selected="selected"</#if>>${cur}</option>
       </#list>
  </select>
  </td>
  <td><span class="trance_date trance_all_format">
  c <input value="${w.from}" id="withdraw_from${w.id}" onfocus="displayCalendarWrapper('withdraw_from${w.id}', '', false); return false;" name="withdraw_from" class="text date" onchange="validateWithdrawPeriod()">
  по <input value="${w.to}" onfocus="displayCalendarWrapper('withdraw_to${w.id}', '', false); return false;" id="withdraw_to${w.id}" name="withdraw_to" class="text date" onchange="validateWithdrawPeriod()">
  </span>
  <select name="withdraw_month" class="trance_month trance_all_format" onchange="validateWithdrawPeriod()">
    <option value="1" <#if w.month = 1> selected="selected"</#if>>январь</option>
    <option value="2" <#if w.month = 2> selected="selected"</#if>>февраль</option>
    <option value="3" <#if w.month = 3> selected="selected"</#if>>март</option>
    <option value="4" <#if w.month = 4> selected="selected"</#if>>апрель</option>
    <option value="5" <#if w.month = 5> selected="selected"</#if>>май</option>
    <option value="6" <#if w.month = 6> selected="selected"</#if>>июнь</option>
    <option value="7" <#if w.month = 7> selected="selected"</#if>>июль</option>
    <option value="8" <#if w.month = 8> selected="selected"</#if>>август</option>
    <option value="9" <#if w.month = 9> selected="selected"</#if>>сентябрь</option>
    <option value="10" <#if w.month = 10> selected="selected"</#if>>октябрь</option>
    <option value="11" <#if w.month = 11> selected="selected"</#if>>ноябрь</option>
    <option value="12" <#if w.month = 12> selected="selected"</#if>>декабрь</option>
  </select>
  <span class="trance_all_format trance_quarter"><select name="withdraw_quarter" onchange="validateWithdrawPeriod()">
    <option value="1" <#if w.quarter = 1> selected="selected"</#if>>I</option>
    <option value="2" <#if w.quarter = 2> selected="selected"</#if>>II</option>
    <option value="3" <#if w.quarter = 3> selected="selected"</#if>>III</option>
    <option value="4" <#if w.quarter = 4> selected="selected"</#if>>IV</option>
  </select> квартал</span>
  <span class="trance_all_format trance_hyear"><select name="withdraw_hyear" onchange="validateWithdrawPeriod()">
    <option value="1" <#if w.hyear = 1> selected="selected"</#if>>I</option>
    <option value="2" <#if w.hyear = 2> selected="selected"</#if>>II</option>
  </select> полугодие</span>
   <span class="trance_all_format trance_period">
    от 
	&nbsp;
	<input name="withdraw_fromPeriod" class="days" value="${w.fromPeriod!''}"></input>
	&nbsp; 
	<select name="withdraw_periodDimensionFrom"> 
		<option value="days" <#if (w.periodDimensionFrom)??><#if w.periodDimensionFrom = "days"> selected="selected" </#if></#if>>дн.</option>
		<option value="months" <#if (w.periodDimensionFrom)??><#if w.periodDimensionFrom = "months"> selected="selected" </#if></#if>>мес.</option>
		<option value="years" <#if (w.periodDimensionFrom)??><#if w.periodDimensionFrom = "years"> selected="selected" </#if></#if>>г./лет</option>
    </select>
	&nbsp;
	до
	&nbsp;
	<input name="withdraw_beforePeriod" class="days" value="${w.beforePeriod!''}"></input>
 	<select name="withdraw_periodDimensionBefore"> 
		<option value="days" <#if (w.periodDimensionBefore)??><#if w.periodDimensionBefore = "days"> selected="selected" </#if></#if>>дн.</option>
		<option value="months" <#if (w.periodDimensionBefore)??><#if w.periodDimensionBefore = "months"> selected="selected" </#if></#if>>мес.</option>
		<option value="years" <#if (w.periodDimensionBefore)??><#if w.periodDimensionBefore = "years"> selected="selected" </#if></#if>>г./лет</option>
    </select> 
   </span>
  <span class="trance_month trance_quarter trance_hyear trance_year trance_all_format">
  <input value="${w.yearStr}" name="withdraw_year" style="width:4em;" onkeyup="validateWithdrawPeriod()"> год</span>
  </td>
  <td class="delchk"><input type="checkbox" name="trance_tableChk"/></td>
  </tr>
</#list>
