<%@page isELIgnored="true" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.Trance" %>
<%@page import="java.util.logging.*"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="java.util.Iterator"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.Currency"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%  
	response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");

	Logger LOGGER = Logger.getLogger("frame_trance_jsp");
	CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
	Currency[] currencyList = compenduim.findCurrencyList("",null);
	Task task=TaskHelper.findTask(request);
	boolean editMode = TaskHelper.isEditMode("Основные параметры",request);
	boolean isCanEditFund = TaskHelper.isCanEditFund(request);
	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
%><input name="trance_edit" type="hidden" value="<%=task.getMain().getCurrency2().getCode() %>">
	<div title="Комментарий по графику использования" style="display:none" id="trance_commentSelectTemplateDiv">
	<%Iterator<String> it = Trance.TranceComment.iterator();
	while(it.hasNext()){
       String tc = it.next();%>
		<a href="javascript:;" onclick="$('#trance_comment').val('<%=tc %>');$('#trance_commentSelectTemplateDiv').dialog('close');">
			<%=tc %></a><br /><br />
	<%} %>
	</div>
	<script language="javascript">
	$.ajaxSetup({cache: false});
	$(document).ready(function() {
	            $("a.trance_commentSelectTemplate").fancybox({
	                'transitionIn'  :   'elastic',
	                'transitionOut' :   'elastic',
	                'speedIn'       :   600, 
	                'speedOut'      :   200, 
	                'overlayShow'   :   true,
	                'zoomOpacity'           : true,
	                'zoomSpeedIn'           : 500,
	                'zoomSpeedOut'          : 500,
	                'hideOnContentClick': false,
	                'frameWidth': 800, 
	                'frameHeight': 600,
	                'showCloseButton': true
	            });
	            $('[name="trance_id"]').each(function(){
	                var tranceid=$(this).val();
                <%if(editMode || isCanEditFund){%>
	                $('#withdrawtablebody'+tranceid).load('ajax/withdraw.html?ro=false&mdtask=<%=taskJPA.getId()%>&tranceid='+tranceid,trance_period_format_change);
                <%}else{%>
	                $('#withdrawtablebody'+tranceid).load('ajax/withdraw.html?ro=true&mdtask=<%=taskJPA.getId()%>&tranceid='+tranceid,trance_period_format_change);
                <%}%>
	            });
                <%if(editMode || isCanEditFund){%>
	                $('#withdrawtablebody').load('ajax/withdraw.html?ro=false&tranceid=0&mdtask=<%=taskJPA.getId()%>',trance_period_format_change);
                <%}else{%>
	                $('#withdrawtablebody').load('ajax/withdraw.html?ro=true&tranceid=0&mdtask=<%=taskJPA.getId()%>',trance_period_format_change);
                <%}%>
	            tranceFlagControl();
	});
	</script>
	 
	<table class="pane" id="section_Транши">
		<tbody>
		<%try{ %>
			<tr><td>
			График использования траншей <input type="checkbox" <%=editMode||isCanEditFund?"":"disabled=\"disabled\""%>
               id="trance_graph" name="trance_graph" onclick="trance_graphOnClick()" 
               <%=taskJPA.isTrance_graph()?"checked=\"checked\"":"" %> value="y"><br />
			<div id="trance_limit_use_div">Допускается использование недоиспользованного лимита <input name="trance_limit_use" <%=taskJPA.isTrance_limit_use()?"checked=\"checked\"":"" %> type="checkbox" <%=editMode?"":"disabled=\"disabled\""%> value="y"><br /></div>
			<div id="trance_limit_excess_div">Допускается превышение лимита по графику <input name="trance_limit_excess" <%=taskJPA.isTrance_limit_excess()?"checked=\"checked\"":"" %> type="checkbox" <%=editMode?"":"disabled=\"disabled\""%> value="y"><br /></div>
			Жесткий график <input name="trance_hard_graph" id="trance_hard_graph" onclick="tranceFlagControl()" <%=taskJPA.isTrance_hard_graph()?"checked=\"checked\"":"" %> type="checkbox" <%=editMode?"":"disabled=\"disabled\""%> value="y"><br />
			Формат периода предоставления
			<select name="trance_period_format" id="trance_period_format" onchange="trance_period_format_change()" <%if(!editMode && !isCanEditFund){%>disabled="disabled"<%} %>>
			    <option value="date" <%if(taskJPA.getTrance_period_format().equals("date")){ %>selected="selected"<%} %>>Дата с… Дата по</option>
			    <option value="month" <%if(taskJPA.getTrance_period_format().equals("month")){ %>selected="selected"<%} %>>Месяц</option>
			    <option value="quarter" <%if(taskJPA.getTrance_period_format().equals("quarter")){ %>selected="selected"<%} %>>Квартал</option>
			    <option value="hyear" <%if(taskJPA.getTrance_period_format().equals("hyear")){ %>selected="selected"<%} %>>Полугодие</option>
			    <option value="year" <%if(taskJPA.getTrance_period_format().equals("year")){ %>selected="selected"<%} %>>Год</option>
			    <option value="period" <%if(taskJPA.getTrance_period_format().equals("period")){ %>selected="selected"<%} %>>Период от и до </option>
			</select>
			<div id="validateWithdrawPeriodError" class="error" style="display: none;">validateWithdrawPeriod placeholder</div>
			</td></tr>
			<tr><td id="trancetd"><%if(editMode || isCanEditFund){ %><div class="newtrance"><a href="javascript:;" onclick="$('#newTranceTemplate').tmpl({unid:getNextId()}).appendTo('#trancetd');">Добавить новый транш</a></div><br /><%} %>
			    <% int trancenumber=1;
			    if(taskJPA.isTrance_graph()){
				for(Trance trance :task.getTranceList()){%>
				<div id="trancediv<%=trance.getIdStr()%>" class="trancediv"><h3>Транш № <%=trancenumber++ %></h3><input type="hidden" name="trance_id" value="<%=trance.getIdStr()%>">
				<%if (editMode || isCanEditFund) { %><a href="javascript:;" onclick="$('#trancediv<%=trance.getIdStr()%>').remove();validateWithdrawPeriod()">Удалить транш № <%=(trancenumber-1) %></a><%} %>
				<table class="regular leftPadd" id="withdraw_table<%=trance.getIdStr()%>">
	                <thead><tr><th style="width: 50%">Сумма выдачи</th><th>период предоставления</th><th></th></tr></thead>
	                <tbody id="withdrawtablebody<%=trance.getIdStr()%>"></tbody>
	                <tfoot>
		                <tr>
		                	<td colspan=3 class="compare-list-removed" id="compare_list_prodparam_trance<%=trance.getIdStr()%>_withdrow"></td>
		                </tr>
	                <%if (editMode || isCanEditFund) { %><tr><td colspan=3 class="add">
	                                <button onclick="AddWithdraw('<%=trance.getIdStr()%>'); return false;" class="add"></button>
	                                <button onclick="DelRowWithLast('withdraw_table<%=trance.getIdStr()%>', 'trance_tableChk'); return false;" class="del"></button>
	                            </td></tr><%}%>
	                </tfoot>
    	         </table></div>
				<%}} %>
<div class="withouttrancediv">
<table class="regular" id="withdraw_table">
  <thead><tr><th style="width: 50%">Сумма выдачи</th><th>период предоставления</th><th></th></tr></thead>
  <tbody id="withdrawtablebody"></tbody>
  <tfoot>
		<tr>
			<td colspan=3 class="compare-list-removed" id="compare_list_prodparam_trance0_withdrow"></td>
		</tr>
  <%if (editMode || isCanEditFund) { %><tr><td colspan=3 class="add">
    <button onclick="AddWithdraw(''); return false;" class="add"></button>
    <button onclick="DelRowWithLast('withdraw_table', 'trance_tableChk'); return false;" class="del"></button>
  </td></tr><%}%>
  </tfoot>
</table></div>
<script id="newWithdrawTemplate" type="text/x-jquery-tmpl">
  <tr><td id="compare_prodparam_withdraw${unid}_sum">
  <select name="withdraw_sumScope">
  	<option value="noLess">Не менее</option>
  	<option value="noMore">Не более</option>
  	<option value=""></option>
  <select>
  <input class="money" onBlur="input_autochange(this,'money')" value="" name="withdraw_sum">
  <input type="hidden" name="withdraw_trance_id" value="${trid}">
  <select name="withdraw_cur"><option value=""></option>
                        <%for(Currency currency : currencyList){%>
                            <option value="<%=currency.getCode() %>"><%=currency.getCode() %></option>
                        <%}%>
  </select>
  </td>
  <td id="compare_prodparam_withdraw${unid}_period">
<span class="trance_date trance_all_format">
  c <input value="" id="withdraw_from${unid}" onfocus="displayCalendarWrapper('withdraw_from${unid}', '', false); return false;" name="withdraw_from" class="text date" onchange="validateWithdrawPeriod()">
  по <input value="" onfocus="displayCalendarWrapper('withdraw_to${unid}', '', false); return false;" id="withdraw_to${unid}" name="withdraw_to" class="text date" onchange="validateWithdrawPeriod()">
  </span>
  <select name="withdraw_month" class="trance_month trance_all_format" onchange="validateWithdrawPeriod()">
<%for(int i=1;i<13;i++){%>
    <option value="<%=i%>"><%=ru.masterdm.spo.utils.Formatter.getMonthNameRus(Long.valueOf(i))%></option>
<%}%>
  </select>
  <span class="trance_all_format trance_quarter"><select name="withdraw_quarter" onchange="validateWithdrawPeriod()">
    <option value="1">I</option>
    <option value="2">II</option>
    <option value="3">III</option>
    <option value="4">IV</option>
  </select> квартал</span>
  <span class="trance_all_format trance_hyear"><select name="withdraw_hyear" onchange="validateWithdrawPeriod()">
    <option value="1">I</option>
    <option value="2">II</option>
  </select> полугодие</span>
   <span class="trance_all_format trance_period">
    от 
	&nbsp;
	<input name="withdraw_fromPeriod" class="days" value=""></input>
	&nbsp; 
	<select name="withdraw_periodDimensionFrom"> 
		<option value="days">дн.</option>
		<option value="months">мес.</option>
		<option value="years">г./лет</option>
    </select>
	&nbsp;
	до
	&nbsp;
	<input name="withdraw_beforePeriod" class="days" value=""></input>
 	<select name="withdraw_periodDimensionBefore"> 
		<option value="days">дн.</option>
		<option value="months">мес.</option>
		<option value="years">г./лет</option>
    </select></span>
  <span class="trance_month trance_quarter trance_hyear trance_year trance_all_format">
  <input value="" name="withdraw_year" style="width:4em;" onkeyup="validateWithdrawPeriod()"> год</span>
  </td>
  <td class="delchk"><input type="checkbox" name="trance_tableChk"/></td>
  </tr>
</script>
<script id="newTranceTemplate" type="text/x-jquery-tmpl">
				<div id="trancediv${unid}" class="trancediv"><h3>Новый транш</h3><input type="hidden" name="trance_id" value="new${unid}">
                <a href="javascript:;" onclick="$('#trancediv${unid}').remove();validateWithdrawPeriod()">Удалить транш</a>
				<table class="regular" id="withdraw_tablenew${unid}">
	                <thead><tr><th style="width: 50%">Сумма выдачи</th><th>период предоставления</th><th></th></tr></thead>
	                <tbody></tbody>
                        <tfoot><tr><td colspan=3 class="add">
	                        <button onclick="AddWithdraw('new${unid}'); return false;" class="add"></button>
	                        <button onclick="DelRowWithLast('withdraw_tablenew${unid}', 'trance_tableChk'); return false;" class="del"></button>
	                    </td></tr></tfoot>
    	         </table></div>
</script>
		</td></tr>
		<tr><td id="compare_list_prodparam_trance" class="compare-list-removed"></td></tr>
		<tr><td>Комментарий по графику использования:<br />
			<%if(editMode){ %>
				<textarea name="trance_comment" id="trance_comment" onkeyup="fieldChanged(this)" rows="6"><%=task.getTranceComment() %></textarea><a 
					href="javascript:;" onclick="$('#trance_commentSelectTemplateDiv').dialog({draggable: false, modal: true});"><img alt="выбрать из шаблона" src="style/dots.png" /></a>
			<%}else{ %>
				<%=task.getTranceComment() %>
			<%} %>
		</td></tr>
		<%} catch (Exception e) {
			out.println("Ошибка в секции  frame_trance_jsp:" + e.getMessage());
			e.printStackTrace();
		} %>
		</tbody>
	</table>	 
