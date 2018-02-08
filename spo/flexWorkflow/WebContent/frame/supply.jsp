<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.TaskProcent"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="com.vtb.domain.OrgSearchParam" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page isELIgnored="true" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
	(new OrgSearchParam(request)).clearCookies(response);
try{
	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	Task task=TaskHelper.findTask(request);
    ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
	TaskProcent tp = task.getTaskProcent();
	String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
	boolean readOnly = !TaskHelper.isEditMode("R_Обеспечение",request);
%>
<script>
	$(function() {
	    dialogHandler();
		$( "#supply_tabs" ).tabs();
		restoreTab('supply_tabs');
		$.cookie("org_search_param_inn","");
		$.cookie("org_search_param_group","");
		$.cookie("org_search_param_name","");
		$.cookie("org_search_param_number","");
	});
	function onSupplyExistCheckClick() {
		if($('#supply_exist_check').prop("checked")){
			$('#supplydiv').show();
			$('#supplyexist').val('y');
			$('.supply_exist_tabs').show();
		} else {
			$('#supplydiv').hide();
			$('#supplyexist').val('n');
			$('.supply_exist_tabs').hide();
		}
	}
    $(document).ready(function() {
        onSupplyExistCheckClick();
    });
</script>
<div id="OperationTypeSelectTemplateDiv" title="Выбрать тип операции" style="display: none;">
    <ol>
        <li>
            <a href="javascript:;" class="disable-decoration" onclick="$('#operationtype').val('');$('#operationtypename').text('не выбрано');">не выбрано</a>
        </li>
    
	    <%for(ru.md.dict.dbobjects.OperationTypeJPA op : TaskHelper.dict().findOperationType()){ %>
	        <li>
		        <a href="javascript:;" class="disable-decoration" onclick="$('#operationtype').val('<%=op.getId() %>');$('#operationtypename').text('<%=op.getName() %>');">
		            <%=op.getName() %>
		        </a>
	        </li>
	    <%} %>
	 </ol>
</div>
<div id="supply_tabs">
	<ul>
		<li><a href="#tabs-0" onclick="storeTab('supply_tabs',0)">Обеспечение</a></li>
		<li class="supply_exist_tabs"><a href="#tabs-1" onclick="storeTab('supply_tabs',1)">Залоги</a></li>
		<li class="supply_exist_tabs"><a href="#tabs-2" onclick="storeTab('supply_tabs',2)">Поручительство</a></li>
		<li class="supply_exist_tabs"><a href="#tabs-3" onclick="storeTab('supply_tabs',3)">Гарантии</a></li>
		<li class="supply_exist_tabs"><a href="#tabs-4" onclick="storeTab('supply_tabs',4)">Вексель</a></li>
	</ul>
	<div id="tabs-0"><div id="compare_supply_supply_exist_check">
		<input type="checkbox" name="supply_exist_check" <%if(task.getSupply().isExist()){ %>checked="checked"<%} %> <%if(readOnly){ %>disabled="disabled"<%} %>
			   onclick="onSupplyExistCheckClick();fieldChanged();" id="supply_exist_check">
		Обеспечение предусмотрено
		<input type="hidden" id="supplyexist" name="supplyexist" value="<%=task.getSupply().isExist()?"y":"n" %>" >
	</div>
		<div id="supplydiv" <%if(!task.getSupply().isExist()){ %>style="display:none"<%} %>>
			<div id="compare_supply_operationtype">
				<label>Тип операции (для целей определения транзакционного риска)<br>
					<input type="hidden" name="operationtype" id="operationtype" value="<%=task.getHeader().getOperationtype().getId().toString() %>">
					<%if(!readOnly){ %><a class="dialogActivator" href="javascript:;" dialogId="OperationTypeSelectTemplateDiv"><%} %>
						<span id="operationtypename"><%=task.getHeader().getOperationtype().getName() %></span>
						<%if(!readOnly){ %></a><%} %>
				</label>
			</div>
			<br /><br />
			<%if(readOnly&&task.getMain().getCurrency2()!=null&&!task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){ %>
			<div id="exchangeratediv"><label id="compare_supply_exchangerate">Применяемый курс при пересчете в руб.:<br><%=task.getMain().getFormatedExchangeRate() %></label></div>
			<%} %>
			<%if(!readOnly){ %>
			<div id="exchangeratediv"
				 <% if(task.getMain().getCurrency2()!=null&&task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){%>style="display: none"<%} %> >
				<label id="compare_supply_exchangerate">Применяемый курс при пересчете в руб.:<br>
					<md:inputMoney  readonly="false" name="exchangerate" styleClass="money"
									value="<%=task.getMain().getFormatedExchangeRate()%>"
									onBlur="input_autochange(this,'money')" />
				</label>
				<label id="compare_supply_exchangedate">Дата курса<br>
					<md:input name="exchangedate" value="" readonly="<%=readOnly %>" styleClass="text date"
							  id="exchangedate" addition="<%=dateAddition%>" value="<%=Formatter.format(taskJPA.getExchangedate()) %>"
							  onFocus="displayCalendarWrapper('exchangedate', '', false); return false;"
							  onChange="input_autochange(this,'date');"/>
				</label>
			</div>
			<%} %>
			<br /><br /><br />
			<%if(task.isOpportunity()){ %>
			<label id="compare_supply_additionsupply">Обеспечение из CRM<br>
				<span style="width:97%;"><%=(task.getSupply().getAdditionSupply().equals(""))?"не задано":task.getSupply().getAdditionSupply() %></span>
			</label>
			<table class="regular leftPadd">
				<tbody>
				<tr><th style="width: 50%; white-space:nowrap">Расчетный коэффициент транзакционного риска обеспечения</th>
					<td id="compare_supply_trriskc1"><md:inputMoney readonly="true" name = "Коэффициент транзакцион. риска С1" style="width:2em;"
																	value="<%=Formatter.toMoneyFormat(tp.getTrRiskC1())%>"/></td></tr>
				<tr><th style="width: 50%; white-space:nowrap">Дата расчета коэффициента</th>
					<td id="compare_supply_computedate"><md:input readonly="true" name = "Дата расчета стоимостных условий" style="width:2em;"
																  value="<%=tp.getComputeDateFormatted()%>"/></td></tr>
				<tr><th style="width:50%; white-space:nowrap">Фактический коэффициент транзакционного риска</th>
					<td id="compare_supply_cfact">
						<md:inputMoney name="supply_cfact" style="width: 10%;"readonly="<%=readOnly %>" onBlur="input_autochange(this,'money')"
									   value="<%=Formatter.toMoneyFormat(task.getSupply().getCfact()) %>" /></td></tr>
				</tbody>
			</table>
			<%} %>




			<input type="hidden" id="supply_code">
		</div></div>
	<div id="tabs-1"><jsp:include flush="true" page="depositor.jsp"/></div>
	<div id="tabs-2"><jsp:include flush="true" page="warranty.jsp"/></div>
	<div id="tabs-3"><jsp:include flush="true" page="garant.jsp"/></div>
	<div id="tabs-4"><jsp:include flush="true" page="promissoryNote.jsp"/></div>
</div>

				<script type="text/javascript">
					if ($('#lastApprovedVersion').val() != "")
						loadCompareResult('supply');
				</script>
				
<%
	} catch (Exception e) {
		out.println("Ошибка в секции frame_supply.jsp:" + e.getMessage());
		e.printStackTrace();
	}
%>