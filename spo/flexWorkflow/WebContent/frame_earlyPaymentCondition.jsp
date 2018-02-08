<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.spo.dbobjects.DependingLoanJPA"%>
<%@page isELIgnored="true" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.EarlyPayment" %>
<%@page import="ru.masterdm.compendium.domain.spo.EarlyPaymentCondition" %>
<%@page import="ru.masterdm.compendium.domain.spo.EarlyPaymentDict" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<script language="JavaScript" src="scripts/delTwoRows.js"></script>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");
	boolean readOnly = !TaskHelper.isEditMode("Условия досрочного погашения",request);
	Task task=TaskHelper.findTask(request);
	CompendiumSpoActionProcessor compenduim = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	EarlyPaymentDict[] earlyPaymentDictList = compenduim.findEarlyPaymentDictList("","");
	EarlyPaymentCondition[] earlyPaymentConditionList = compenduim.findEarlyPaymentConditionList("","");
	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
	boolean isCanEditFund = TaskHelper.isCanEditFund(request);
	%>
<%if(!readOnly || isCanEditFund){ %>
	<input type="hidden" id="earlyPaymentTemplate" name="earlyPaymentTemplate" value="">
<%} %>
	
<script type="text/javascript">
$().ready(function() {
  $('#earlyPaymentSelectTemplateDiv').jqm();
  onEarly_payment_prohibitionClick();
});
</script>
<div id="earlyPaymentSelectTemplateDiv" class="jqmWindow" style="height:75%;overflow:auto">
<%for(EarlyPaymentDict epd : earlyPaymentDictList){ %>
<a href="javascript:;" class="jqmClose" 
onclick="$('#'+$('#earlyPaymentTemplate').val()).val('<%=epd.getName() %>');">
<%=epd.getName() %></a><br />
<%} %>
<hr>
<a href="#" class="jqmClose">Закрыть</a>
</div>


<script id="earlyPaymentTrTemplate" type="text/x-jquery-tmpl"> 
<tr>
	<td>
		<select name="Разрешение досроч.погаш." onchange="fieldChanged(this);">
			<option value=""></option>
			<%for(EarlyPaymentCondition epc : earlyPaymentConditionList){  %>
				<option value="<%=epc.getId().toString() %>"><%=epc.getName() %></option>
			<%} %>
		</select>
	</td>
	<td>
		<select name="Взимание комиссии досроч.погаш." onchange="fieldChanged(this);">
			<option value=""></option>
			<option value="Y">взимается</option>
			<option value="N">не взимается</option>
		</select>
	</td>
	<td>
		<textarea id="EarlyPayment${id}" name="Условие досрочного погашения" class="autosize" style="width:98%;" onkeyup="fieldChanged(this)"></textarea>
	</td>
	<td class="delchk" <%if(task.isOpportunity()){ %> rowspan="2" <%} %>><input type="checkbox" name="idEarlyPayment_chk" /></td>
</tr>
<%if(task.isOpportunity()){ %>
	<tr>		
		<th colspan="2">За сколько дней Заемщик должен уведомить Банк о досрочном погашении</th>
		<td>
			<input type="text" name="Количество дней до предупреждения банка" value="5" style="width: 80px;"/>
			&nbsp;
			<select name="Тип периода">
					<option value=""></option>
					<option value="alldays">календарных дней</option>
					<option value="workdays">рабочих дней</option>
			</select>
		</td>
	</tr>
<%} %>	
</script>


		<%try{ %>
				<%if(task.isOpportunity()){ %>
				<div>С запретом <input name="early_payment_prohibition" id="early_payment_prohibition" 
				type="checkbox" <%=taskJPA.isEarly_payment_prohibition()?"checked":"" %> 
				<%=(readOnly && !isCanEditFund)?"disabled":"" %> onclick="onEarly_payment_prohibitionClick();fieldChanged(this);" ></div>
				<div id="early_payment_prohibition_period">Срок запрета
					<select name="early_payment_prohibition_period" <%=readOnly && !isCanEditFund?"disabled":"" %> onchange="fieldChanged(this);"><option></option>
					<%for(DependingLoanJPA loan:  taskFacadeLocal.findDependingLoan(task.getMain().getCurrency2().getCode(),task.getMain().getPeriodInDay())){ %>
					<option value="<%=loan.getId().toString()%>" <%=loan.getId().equals(taskJPA.getEarly_payment_proh_per())?"selected":"" %>
					><%=loan.getDays_ban_to().toString()%> дней</option>
					<%} %>
					</select>
				</div>
				<%} %>
				<table id="idEarlyPayment" class="regular add" style="width: 99% !important;">
						<thead>
							<tr>
								<th>Условие досрочного погашения</th>
								<th>Комиссия</th>
								<th style="width: 450px;">Комментарий</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
						<%try{ %>
						<%for (int j=0; j<task.getEarlyPaymentList().size(); j++) {
								EarlyPayment ep = (EarlyPayment)task.getEarlyPaymentList().get(j);
								int number = j+1;
								%>
								<tr>
										<%if(!readOnly || isCanEditFund){ %>
										
										<td id="compare_earlypaymentcondition_permission<%=number%>" >
											<select name="Разрешение досроч.погаш." onchange="fieldChanged(this);">
													<option <%=(ep.getPermission()==null)?"selected":"" %> value=""></option>
												<%for(EarlyPaymentCondition epc : earlyPaymentConditionList){  %>
													<option <%=(ep.getPermission()!=null&&ep.getPermission().equals(epc.getId().toString()))?"selected":"" %> 
														value="<%=epc.getId().toString() %>"><%=epc.getName() %></option>
												<%} %>
											</select>
										</td>
										<td id="compare_earlypaymentcondition_commission<%=number%>" >
											<select name="Взимание комиссии досроч.погаш." onchange="fieldChanged(this);">
												<option value=""></option>
												<option <%=(ep.getCommission()!=null&&ep.getCommission().equals("Y"))?"selected":"" %> value="Y">взимается</option>
												<option <%=(ep.getCommission()!=null&&ep.getCommission().equals("N"))?"selected":"" %> value="N">не взимается</option>
											</select>
										</td>
										<td id="compare_earlypaymentcondition_EarlyPayment<%=number%>" >
											<textarea id="EarlyPayment<%=j %>" name="Условие досрочного погашения" class="autosize" style="width:98%;" onkeyup="fieldChanged(this)"><%=ep.getCondition() %></textarea>
										</td>
										<%}else{ %>
											<td><%=ep.getPermission()==null?"":compenduim.findEarlyPaymentCondition(new EarlyPaymentCondition(new Long(ep.getPermission()))).getName()%></td>
											<td>  <%=(ep.getCommission()!=null)?"комиссия":"" %>
												<%=(ep.getCommission()!=null&&ep.getCommission().equals("Y"))?"взимается":"" %>
											<%=(ep.getCommission()!=null&&ep.getCommission().equals("N"))?"не взимается":"" %></td>
											<td><span style="width:98%;"><%=ep.getCondition() %></span></td>
										<%} %>
										
									<%
									if (!readOnly || isCanEditFund) {
									%>
										<td class="delchk" <%if(taskJPA.getType()=="Сделка"){ %> rowspan="2"<%} %>>
											<input type="checkbox" name="idEarlyPayment_chk"/>
										</td>
									<%
									}
									%>
								</tr>
								<%if(task.isOpportunity()){ %>
								<tr>
									<%if(!readOnly || isCanEditFund){ %>
									<th colspan="2">За сколько дней Заемщик должен уведомить Банк о досрочном погашении</th>
									<td id="compare_earlypaymentcondition_periodType<%=number%>">
										<input name="Количество дней до предупреждения банка" value="<%=(ep.getDaysBeforeNotifyBank()==null)?"":ep.getDaysBeforeNotifyBank() %>" style="width: 80px;"/> 
										&nbsp;
										<select name="Тип периода">
											    <option value=""></option>
												<option value="alldays" <%if(ep.getPeriodType()!=null && ep.getPeriodType().equals("alldays")){ %>selected<%} %>>календарных дней</option>
												<option value="workdays" <%if(ep.getPeriodType()!=null && ep.getPeriodType().equals("workdays")){ %>selected<%} %>>рабочих дней</option>
										</select>	
									</td>
									<%}else{ %>
									<th colspan="2">За сколько дней Заемщик должен уведомить Банк о досрочном погашении</th>
									<td> <%=(ep.getDaysBeforeNotifyBank()==null)?"":ep.getDaysBeforeNotifyBank() %>
										&nbsp;
										<select disabled="disabled">
											    <option value=""></option>
												<option value="alldays" <%if(ep.getPeriodType()!=null && ep.getPeriodType().equals("alldays")){ %>selected<%} %>>календарных дней</option>
												<option value="workdays" <%if(ep.getPeriodType()!=null && ep.getPeriodType().equals("workdays")){ %>selected<%} %>>рабочих дней</option>
										</select>	
									</td>
									<%} %>
								</tr>
								<%} %>
							<%
							}
							%>
						<%} catch (Exception e) {	out.println("Ошибка в секции  frame_earlyPaymentCondition.jsp:" + e.getMessage());	e.printStackTrace();} %>
						</tbody>
						<%
						if (!readOnly || isCanEditFund) {
						%>
						 <tfoot> 
								<tr>
									<td colspan="4" class="add" style="border-top: 1px;">
										<button onmouseover="Tip(getToolTip('Добавить условие'))" onmouseout="UnTip()" onclick="AddRowToTableEarlyPayment(); return false;" class="add"></button>
										&nbsp;
										<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelTwoRowWithLast('idEarlyPayment', 'idEarlyPayment_chk'); return false;" class="del"></button>
									</td>
								</tr>
						 </tfoot> 
						<%
						}
						%>
					</table>
					<div class="compare-list-removed" id="compare_list_earlypaymentcondition"></div>

		<%} catch (Exception e) {	out.println("Ошибка в секции  frame_earlyPaymentCondition.jsp:" + e.getMessage());	e.printStackTrace();} %>
