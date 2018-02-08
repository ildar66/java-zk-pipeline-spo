<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page isELIgnored="true" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="com.vtb.domain.TaskCurrency" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.TaskProduct" %>
<%@page import="ru.masterdm.compendium.domain.crm.Product" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Forbidden" %>
<%@page import="ru.md.domain.OtherGoal" %>
<%@page import="com.vtb.domain.Main" %>
<%@page import="ru.md.domain.TargetGroupLimit" %>
<%@page import="ru.md.domain.TargetGroupLimitType" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@ page import="ru.md.domain.dict.CommonDictionary" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="ru.md.domain.dict.CrossSell" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");%>
<%  TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
    try {
		String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
		CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
		
		String guaranteeFamily = "Банковские гарантии";
		String creditFamily    = "Кредитование";
		
		Task task=TaskHelper.findTask(request);
		Long idTask = task.getId_task();
		ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(idTask);
		
		Main main = task.getMain();
		
		boolean readOnly = !TaskHelper.isEditMode("Основные параметры",request);
		String ro = (readOnly) ? "DISABLED" : "";
		
        ArrayList<OtherGoal> otherGoals = task.getMain().getOtherGoals();
        ArrayList<Forbidden> forbiddens = task.getMain().getForbiddens();
		boolean isCanEditFund = TaskHelper.isCanEditFund(request);
		
		List<TargetGroupLimit> targetGroupLimits = ru.masterdm.spo.utils.SBeanLocator.singleton().mdTaskMapper().getTargetGroupLimits(idTask);
		main.setTargetGroupLimits(targetGroupLimits);
%>
	<script language="javascript">
		/* show/hide exchangeratediv if found */ 		
		function showHideExchangeDivRate(show) {
			try {
				if (show) document.getElementById('exchangeratediv').style.display = ''; 					
				else document.getElementById('exchangeratediv').style.display = 'none';
			} catch (Err) {} 
		} 
		$(document).ready(function() {
// при загрузке инициализируем также и сроки с датами (пользователь может удалить в процессе редактирования, пусть призагрузке автоматически рассчитывается).
// и вызовем его сразу, чтобы корректно проинициализировать все.
    enableDisableSums();
    computeOpportunityPeriodAndDate();
    showHideProductValues();
});
	</script>
	<input type="hidden" id="taskId_param" value="<%=task.getId_task()%>" />
	<input type="hidden" id="readOnlyOpportunityParam" value="<%=(readOnly && !isCanEditFund) %>" />
			<%if (!readOnly || isCanEditFund) { %>
				<input type="hidden" id="Секция_основные параметры" name="Секция_основные параметры" value="YES" />
			<% } %>

<script>
	$(function() {
		$( "#tabs_opportunity_param" ).tabs();
		restoreTab('tabs_opportunity_param');
		jQuery("textarea[class*=expand]").TextAreaExpander();
	});
</script>

<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>


<div id="tabs_opportunity_param">
	<ul>
		<li><a href="#tabs-1" onclick="storeTab('tabs_opportunity_param',0)">Основные параметры</a></li>
		<li><a href="#tabs-2" onclick="storeTab('tabs_opportunity_param',1)">Целевое назначение</a></li>
		<li id="li_frame_trance"><a href="#tabs-3" onclick="storeTab('tabs_opportunity_param',2)">График использования</a></li>
	</ul>
	<div id="tabs-1">
		<table class="regular leftPadd" id="opParamSection_firstColumnRestricted">
			<tr style="display: <%=task.isCrossSell()?"table-row":"none"%>"><th>Вид продукта (сделки)</th>
				<td colspan="3" style="width: 50%;">
                    <%if(readOnly){%>
                    <% for(CrossSell cs : SBeanLocator.singleton().compendium().getCrossSellTypes()) { %>
                      <%=(taskJPA.getCrossSellType()!=null && taskJPA.getCrossSellType().equals(cs.getId()))?cs.getName():""%>
                    <% } %>
                    <%}else{%>
					<select name="cross_sell_type">
						<option></option>
						<% for(CrossSell cs : SBeanLocator.singleton().compendium().getCrossSellTypes()) { %>
						<option value="<%=cs.getId()%>"
                        <%=(taskJPA.getCrossSellType()!=null && taskJPA.getCrossSellType().equals(cs.getId()))?"selected":""%>
                        ><%=cs.getName()%></option>
						<% } %>
					</select>
                    <%}%>
				</td>
			</tr>
			<tr style="display: <%=task.isCrossSell()?"none":"table-row"%>">
				<th style="width: 50%;">Вид продукта (сделки)</th>
				<%  String productid = null;
					if (task.getHeader().getOpportunityTypes().size()!=0){
						TaskProduct ot=(TaskProduct)task.getHeader().getOpportunityTypes().get(0);
						productid = (ot==null ? null :ot.getId());
					}
				%>
				<td colspan="3" style="width: 50%;">
					<div id="span_credit_opp_type">
						<label id="compare_param_opptype" class="compare-elem">
							<md:ProductType value="<%=productid %>" task="<%=task.getId_pup_process() %>" name="Вид кредитной сделки" id="Вид кредитной сделки" readonly="<%=readOnly && !isCanEditFund %>"
											onChange="changeProductValues(); checkParentAllowed(this);"/></label>
						<input type="hidden" id="currentProductId" value="<%=productid %>">
						<br/>
					</div>

					<% boolean isGuarantee = main.isGuaranteeType();
						String isGuaranteeStr =  isGuarantee ? "YES" : "NO";
					%>
					<input type="hidden" id="isGuarantee" name="isGuarantee" value="<%=isGuaranteeStr%>"></input>
					<br><span id="opPrmIsLimitIssue">
									<input type="hidden" id="isLimitIssueRO" name="isLimitIssueRO" value="n">
								    <input type="checkbox" id="isLimitIssue" name="isLimitIssue" value="YES" <%=ro %>
										<%= main.isLimitIssue()? "checked" : "" %> onclick="fieldChanged(this);enableDisableSums()">Кредитная линия с лимитом выдачи</input>
								</span>
								<span id="opPrmIsDebtLimit">
									<input type="hidden" id="isDebtLimitRO" name="isDebtLimitRO" value="n">
									<br/><input type="checkbox" id="isDebtLimit" name="isDebtLimit" value="YES" <%=ro %>
									<%= main.isDebtLimit()? "checked" : "" %>  onclick="fieldChanged(this);enableDisableSums()">Кредитная линия с лимитом задолженности</input>
								</span>
								<span id="opIrregularSpan">
									<br/><input type="checkbox" id="opIrregular" name="opIrregular" value="YES" <%=ro %>
									<%= main.isIrregular()? "checked" : "" %>  onclick="OnOpIrregularClick()">Нестандартная сделка</input>
								</span>
				</td>

				<%if (!readOnly || isCanEditFund) {
					Product product = new Product(null, null, guaranteeFamily, true);
					ArrayList<Product> productList = compenduimCRM.findProductList(product, "c.name");
					for(int i = 0; i< productList.size(); i++) { 	%>
				<input type="hidden" name="opPrFamily_<%=guaranteeFamily%>" value="<%=productList.get(i).getProductID()%>" />
				<% }
					product = new Product(null, null, creditFamily, true);
					productList = compenduimCRM.findProductList(product, "c.name");
					for(int i = 0; i< productList.size(); i++) { 	%>
				<input type="hidden" name="opPrFamily_<%=creditFamily%>" value="<%=productList.get(i).getProductID()%>" />
				<% }
				} %>
			</tr>
			<% String product_name_tr_style = main.isIrregular()?"":"display:none";%>
			<tr id="product_name_tr" style="<%=product_name_tr_style%>"><th>Вид продукта (сделки) Нестандарт</th>
				<td id="compare_prodparam_productname"> <md:input name="product_name" style="width: 99%;" readonly="<%=readOnly %>" value="<%=main.getProduct_name() %>" id="product_name"/> </td>
			</tr>

			<tr id="guaranteeFieldsContract" <% if(!isGuarantee){%>style="display: none"<%} %>>
				<th class="first">Контракт</th>
				<td colspan="3">
					<% if(readOnly){ %>
					<span name="Контракт"><%=Formatter.str(main.getContract())%></span>
					<% }else{ %>
					<textarea name="Контракт" onkeyup="fieldChanged(this)"><%=Formatter.str(main.getContract())%></textarea>
					<% } %>
				</td>
			</tr>
			<tr id="guaranteeFieldsWarrantyItem" <% if(!isGuarantee){%>style="display: none"<%} %>>
				<th>Предмет гарантии</th>
				<td colspan="3">
					<% if(readOnly){ %>
					<span name="Предмет_гарантии"><%=Formatter.str(main.getWarrantyItem())%></span>
					<% }else{ %>
					<textarea name="Предмет_гарантии" onkeyup="fieldChanged(this)"><%=Formatter.str(main.getWarrantyItem())%></textarea>
					<% } %>
				</td>
			</tr>
			<tr id="guaranteeFieldsBeneficiary" <% if(!isGuarantee){%>style="display: none"<%} %>>
				<th>Бенефициар</th>
				<td colspan="3">
					<% if(readOnly){ %>
					<span name="Бенефициар"><%=Formatter.str(main.getBeneficiary())%></span>
					<% }else{ %>
					<textarea name="Бенефициар" onkeyup="fieldChanged(this)"><%=Formatter.str(main.getBeneficiary())%></textarea>
					<% } %>
				</td>
			</tr>
			<tr id="guaranteeFieldsBeneficiaryOGRN" <% if(!isGuarantee){%>style="display: none"<%} %>>
				<th>ОГРН</th>
				<td colspan="3">
					<% if(readOnly){ %>
					<span><%=Formatter.str(main.getBeneficiaryOGRN())%></span>
					<% }else{ %>
					<textarea name="БенефициарОГРН" onkeyup="fieldChanged(this)"><%=Formatter.str(main.getBeneficiaryOGRN())%></textarea>
					<% } %>
				</td>
			</tr>
			<tr>
				<th>Планируемая дата подписания Кредитного соглашения</th>
				<td colspan="3" id="compare_prodparam_proposed_signdate">
					<%String proposedDateSigningAgreement = Formatter.str(main.getProposedDateSigningAgreement()); %>
					<md:input name="Планируемая дата подписания Кредитного соглашения" value="<%=proposedDateSigningAgreement %>" readonly="<%=readOnly %>"
							  styleClass="text date"
							  id="proposedDateSigningAgreement" addition="<%=dateAddition%>"
							  onChange="input_autochange(this,'date');computeOpportunityPeriodAndDate();recalculateGraphPaymentDate();" />
				</td>
			</tr>
			<tr>
				<th>Категория Сделки - Проектное финансирование</th>
				<td id="compare_param_project_fin">
					<input type="checkbox"  id="main_projectFin_chk" name="main_projectFin_chk" <%if (task.getMain().isProjectFin()) {%>checked="checked"<%}%>
						   <%if (readOnly) {%>disabled="disabled"<%}%>
						   onclick="if(this.checked){document.getElementById('main_projectFin').value='y';} else {document.getElementById('main_projectFin').value='n';}; checkParentAllowed(this);fieldChanged(this)" >
					<input type="hidden" id="main_projectFin" name="main Категория Сделок - Проектное финансирование"
						   value="<%=task.getMain().isProjectFin() ? "y" : "n"%>" >
				</td>
			</tr>
			<tr>
				<th>Валюта, в которой могут проводиться операции</th> 
				<td>
					<%if (readOnly) {
						for (TaskCurrency taskCurrency : task.getCurrencyList()) {
					%><label id="compare_param_currency_<%=taskCurrency.getCurrency().getCode()%>"><%=taskCurrency.getCurrency().getCode()%></label>&nbsp;&nbsp;&nbsp;<%
						}
					} else {
						for (String currency : SBeanLocator.singleton().getCurrencyMapper().getCurrencies()) {
							boolean checked = task.haveCurrency(currency);
							if(!checked && !ru.md.domain.Currency.isPopularCurrency(currency)) continue;
							String removeCurrency = ru.md.domain.Currency.isPopularCurrency(currency)?"":("removeCurrency('"+currency+"')");
				%>
					<label id="compare_param_currency_<%=currency%>"><input type="checkbox" name="main_currencyList"
					    value="<%=currency%>" <%=checked ? "checked" : ""%> <%=ro%> onkeyup="fieldChanged(this)"
						onClick="fieldChanged(this);checkParentAllowed(this);<%=removeCurrency%>" /> <%=currency%></label>
					<%
						}
					}
					%>
					<label id="compare_list_param_currency" class="compare-list-removed"></label>
					<%if (!readOnly) {%>
					<br /><br />
					<select id="currency_list" onchange="addCurrency()">
						<option></option>
						<%for (String currency : SBeanLocator.singleton().getCurrencyMapper().getCurrencies()) {
						if(!ru.md.domain.Currency.isPopularCurrency(currency) && !task.haveCurrency(currency)){%>
						<option value="<%=currency%>"><%=currency%></option>
						<%}}%>
					</select>
					<%}%>
				</td>
			</tr>
			<tr>
				<th>Срок сделки</th>
				<td colspan="3" >
					<div id="compare_param_period"><span class="podpis">срок</span><br>
						<%String mdtask_period=Formatter.str(main.getPeriod());
							if (mdtask_period.equals("0")) mdtask_period = "";  %>
						<md:inputInt
								id="mdtask_period" name="Срок действия сделки" value="<%=mdtask_period %>" style="width:6em;" readonly="<%=readOnly && !isCanEditFund %>"
								onBlur="input_autochange(this,'digitsSpaces');computeOpportunityPeriodAndDate();" />

					<%if(readOnly && !isCanEditFund){ %><%=main.getPeriodDimension() %><%}else{ %>
					<select name="periodDimension" id="periodDimension" onchange="computeOpportunityPeriodAndDate();">
						<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
						<option <%if(periodDimension.equals(main.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
						<%} %>
					</select>
					<%} %>
					</div>
					<label id="compare_prodparam_validto">до даты<br>
						<%String mdtask_date=Formatter.str(main.getValidto()); %>
						<md:input name="Дата действия сделки" value="<%=mdtask_date %>" readonly="<%=readOnly && !isCanEditFund %>" styleClass="text date"
								  id="mdtask_date" addition="<%=dateAddition%>"
								  onFocus="displayCalendarWrapper('mdtask_date', '', false); return false;"
								  onChange="input_autochange(this,'date');$('#periodDimension').val('');$('#mdtask_period').val('');" />
					</label><br />
					<label id="compare_prodparam_periodcomment" style="clear:both">Комментарий по сроку сделки<br>
						<%	String mdtask_descr = Formatter.str(main.getPeriodComment());
							if(readOnly){ %>
						<span><%=mdtask_descr%></span>
						<%}else{ %>
						<textarea name="Комментарий по сроку сделки" onkeyup="fieldChanged(this)"><%=mdtask_descr%></textarea>
						<%} %>
					</label>
					<img src="" style="width:30em; height:0">
				</td>
			</tr>
			<tr id="limit_sum_div" <% if(main.isLimitIssue() || main.isDebtLimit()){%>style="display: none"<%} %>>
				<th>Сумма сделки</th>
				<td colspan="3" id="compare_param_sum">
					<md:input id="limit_sum" name="Сумма сделки" styleClass="money" onBlur="input_autochange(this,'money')"
							  value="<%=Formatter.format(main.getSum())%>" readonly="<%=readOnly && !isCanEditFund %>" />
					<md:currency readonly="<%=readOnly && !isCanEditFund %>" value="<%=main.getCurrency2().getCode() %>"
								 id="currency_Sum" name="Валюта сделки" withoutprocent="true"
								 onChange="if(this.options[this.selectedIndex].value!='RUR'){showHideExchangeDivRate(true);}else{showHideExchangeDivRate(false);};syncCurrency(this.selectedIndex);" />
				</td>
			</tr>
			<tr id="limitIssue_div" <% if(!main.isLimitIssue()){%>style="display: none"<%} %>>
				<th>Сумма лимита выдачи</th>
				<td colspan="3" id="compare_param_limit_sum">
							<span style="white-space:nowrap;">
								<md:input id="limitIssue" name="Сумма лимита выдачи" styleClass="money"
										  value="<%=Formatter.format(main.getLimitIssueSum()) %>" readonly="<%=readOnly && !isCanEditFund %>"
										  onBlur="input_autochange(this,'money'); syncSums('limitIssue')" />
								<%	if(!readOnly || (readOnly && (main.getLimitIssueSum()!= null))){ %>
									<md:currency readonly="<%=readOnly %>" value="<%=main.getCurrency2().getCode() %>"
												 id="currency_LimitIssue" name="Валюта сделки" withoutprocent="true"
												 onChange="if(this.options[this.selectedIndex].value!='RUR'){showHideExchangeDivRate(true);}else{showHideExchangeDivRate(false);};syncCurrency(this.selectedIndex);" />
								<%}%>
							</span>
				</td>
			</tr>
			<tr id="debtLimit_div" <% if(!main.isDebtLimit()){%>style="display: none"<%} %>>
				<th>Сумма лимита задолженности</th>
				<td colspan="3" id="compare_param_debt_limit_sum">
							<span style="white-space:nowrap;">
								<md:input id="debtLimit" name="Сумма лимита задолженности" styleClass="money"
										  value="<%=Formatter.format(main.getDebtLimitSum()) %>" readonly="<%=readOnly && !isCanEditFund %>"
										  onBlur="input_autochange(this,'money');syncSums('debtLimit')" />
								<%	if(!readOnly || (readOnly && (main.getDebtLimitSum()!= null))){ %>
									<md:currency readonly="<%=readOnly %>" value="<%=main.getCurrency2().getCode() %>"
												 id="currency_DebtLimit" name="Валюта сделки" withoutprocent="true"
												 onChange="if(this.options[this.selectedIndex].value!='RUR'){showHideExchangeDivRate(true);}else{showHideExchangeDivRate(false);};syncCurrency(this.selectedIndex);" />
								<%}%>
							</span>
				</td>
			</tr>
			
			<tr>
				<th>Контроль целевого использования</th>
				<td>
					<table id="target_type_control" class="target_type_control" style="width: 100%;">
						<thead>
							<tr>
								<th style="width: 45%;">Назначение</th>
								<th style="width: 15%;">Сумма до</th>
								<th style="width: 40%;">Комментарии</th>
							<%if(!readOnly){%>	
								<th></th>
							<%} %>
							</tr>						
						</thead>
						<tbody>
						<% for (int groupIndex = 0; groupIndex < targetGroupLimits.size(); groupIndex++) { 
							 TargetGroupLimit targetGroupLimit = targetGroupLimits.get(groupIndex); %>
							<tr>
								<td>
									<table id="target_type_control_type_<%=groupIndex%>" class="target_type_control">
										<tbody>
										<%if(targetGroupLimit.getTargetGroupLimitTypes() != null && !targetGroupLimit.getTargetGroupLimitTypes().isEmpty())
											for(int limitTypeIndex = 0; limitTypeIndex < targetGroupLimit.getTargetGroupLimitTypes().size(); limitTypeIndex++) {
												TargetGroupLimitType targetGroupLimitType = targetGroupLimit.getTargetGroupLimitTypes().get(limitTypeIndex); %>										
											<tr>
												<td>
													<%if(!readOnly){%>
										                <textarea
										                	id="targetGroupLimitType_<%=groupIndex%>_<%=limitTypeIndex%>"
										                	name="targetGroupLimitType_<%=targetGroupLimit.getId()%>_name"
										                	style="width:98%;" 
										                	class="expand50-200"
										                    onkeyup="fieldChanged(this);" ><%=(targetGroupLimitType.getTargetTypeName()==null)?"":targetGroupLimitType.getTargetTypeName()%></textarea>
														<a href="javascript:;" 
											                class="dialogActivator" 
											                dialogId="select_target_group_limit_type"
											                onclick="$('#targetGroupLimitTypeChoose').val('targetGroupLimitType_<%=groupIndex%>_<%=limitTypeIndex%>');"><img alt="выбрать из списка целевых назначений" src="style/dots.png"></a>
														<input type="hidden" 
															name="targetGroupLimitType_<%=targetGroupLimit.getId()%>_id" 
															value="<%=targetGroupLimitType.getId()==null?"":targetGroupLimitType.getId().toString()%>" />
														<input type="hidden"
															id="targetGroupLimitType_<%=groupIndex%>_<%=limitTypeIndex%>_id_target" 
															name="targetGroupLimitType_<%=targetGroupLimit.getId()%>_id_target" 
															value="<%=targetGroupLimitType.getIdTarget()==null?"":targetGroupLimitType.getIdTarget().toString()%>" />																								                								                    
										            <%} else {%>
										                <span><%=(targetGroupLimitType.getTargetTypeName()==null)?"":targetGroupLimitType.getTargetTypeName()%></span>
										            <%} %>
												</td>
											<%if(!readOnly){%>	
												<td class="delchk">
													<input type="checkbox" name="targetGroupLimitTypeChk"/>
												</td>
											<%} %>										
											</tr>
										<%} %>
										</tbody>
										
										<%if(!readOnly){%>	
										<tfoot>
											<tr>
												<td colspan="2" class="add" style="border-top: 1px;">
													<button onclick='var limitTypeNextId=getNextId(); $( "#newTargetGroupLimitType" ).tmpl({id:limitTypeNextId, limitGuid:"<%=targetGroupLimit.getId()%>"}).appendTo( "#target_type_control_type_<%=groupIndex%>  > TBODY" ); dialogHandler(); $("#targetGroupLimitType"+limitTypeNextId).TextAreaExpander(); return false;' class="add"></button>
													<button onclick="DelRowWithLast('target_type_control_type_<%=groupIndex%>', 'targetGroupLimitTypeChk'); return false;" class="del"></button>
												</td>
											</tr>
										</tfoot>
										<%} %>
									</table>
								</td>
								<td>
									<span>
										<md:input id="targetGroupLimitAmount" 
											name="targetGroupLimitAmount"
											styleClass="money"
											value="<%=Formatter.format(targetGroupLimit.getAmount()) %>" readonly="<%=readOnly %>"
											onBlur="input_autochange(this,'money'); " /> <br />
										<%	if(!readOnly) { %>
											<md:currencyParent readonly="<%=readOnly %>"
												value="<%=targetGroupLimit.getAmountCurrency() %>"
												id="targetGroupLimitAmountCurrency" 
												name="targetGroupLimitAmountCurrency"
												parentTask="<%=task.getId_task()%>" 
												withoutprocent="true"
												with_empty_field="true" />
											<input type="hidden" 
												name="targetGroupLimitId" 
												value="<%=targetGroupLimit.getId()==null?"":targetGroupLimit.getId().toString()%>" />
											<input type="hidden" 
												name="targetGroupLimitGuid" 
												value="targetGroupLimitType_<%=targetGroupLimit.getId()%>" />
										<% } %>
									</span>							
								</td>
								<td>
									<%if(!readOnly){%>
						                <textarea 
						                	id="targetGroupLimitNote"
						                	name="targetGroupLimitNote"
						                	style="width: 100%;" 
						                	class="expand50-200"
						                    onkeyup="fieldChanged(this);" ><%=(targetGroupLimit.getNote()==null)?"":targetGroupLimit.getNote()%></textarea>
						            <%} else {%>
						                <span><%=(targetGroupLimit.getNote()==null)?"":targetGroupLimit.getNote()%></span>
						            <%} %>							
								</td>
							<%if(!readOnly){%>
								<td class="delchk">
									<input type="checkbox" name="targetGroupLimitChk"/>
								</td>
							<%} %>
							</tr>
							
						<% } %>						
						</tbody>

						<%if(!readOnly){%>
						<tfoot>
							<tr>
								<td colspan="4" class="add" style="border-top: 1px;">
									<button onclick='var typeControlNextId=getNextId(); $( "#newTargetTypeControl" ).tmpl({id:typeControlNextId}).appendTo( "#target_type_control > TBODY" ); dialogHandler(); $("#targetGroupLimitNote"+typeControlNextId).TextAreaExpander(); fieldChanged();return false;' class="add"></button>
									<button onclick="DelRowWithLast('target_type_control', 'targetGroupLimitChk'); return false;" class="del"></button>
								</td>
							</tr>
						</tfoot>
						<%} %>
					</table>
					
					<span class="comment_header">Комментарий:</span>
					<span style="display:block;">
		    		<%if(!readOnly){%>
		                <textarea 
		                	id="targetTypeControlNote"
		                	name="targetTypeControlNote"
		                	style="width:98%;" 
		                	class="expand50-200"
		                    onkeyup="fieldChanged(this);" ><%=(taskJPA.getTargetTypeControlNote()==null)?"":taskJPA.getTargetTypeControlNote()%></textarea>
		            <%} else {%>
		                <span><%=(taskJPA.getTargetTypeControlNote()==null)?"":taskJPA.getTargetTypeControlNote()%></span>
		            <%} %>
		            </span>					
				</td>			
			</tr>
						
			<tr>
				<th>Срок использования</th>
				<td  colspan="3">
					<%String useperiod=Formatter.str(main.getUseperiod());
						if (useperiod.equals("0")) useperiod = "";  %>
					<label id="compare_prodparam_useperiod">срок<br>
						<md:inputInt
								id="op_use_period" name="Период использования сделки" value="<%=useperiod %>" style="width:6em;" readonly="<%=readOnly && !isCanEditFund %>"
								onBlur="input_autochange(this,'digitsSpaces');clearDates('op_use_period','termOfUse')";
						/> дней
					</label>
					<label id="compare_prodparam_usedate">до даты<br>
						<%String dateuse=Formatter.str(main.getUsedate()); %>
						<md:input name="Дата использования сделки" value="<%=dateuse %>" readonly="<%=readOnly && !isCanEditFund %>" styleClass="text date"
								  id="termOfUse" addition="<%=dateAddition%>"
								  onFocus="displayCalendarWrapper('termOfUse', '', false); return false;"
								  onChange="input_autochange(this,'date');clearDates('termOfUse','op_use_period')";/>
					</label><br />
					<label id="compare_prodparam_useperiodtype" style="clear:both">Комментарий по сроку использования<br>
						<%	String descr = Formatter.str(main.getUseperiodtype());
								if(readOnly){ %>
						<span><%=descr%></span>
						<%}else{ %>
						<textarea name="Комментарий по сроку использования сделки" onkeyup="fieldChanged(this)"><%=descr%></textarea>
						<%} %>
					</label>
				</td>
			</tr>
			<tr>
				<th>Категория качества ссуды</th>
				<td colspan="3" id="compare_param_qualitycategory">
							<span id="quality_category_span">не ниже: &nbsp;
							<md:Quality_category name="Категория качества ссуды" style="width:4em;font-size:0.75em;"
												 value="<%=task.getGeneralCondition().getQuality_category() %>" readonly="<%=readOnly %>" styleClass=""/></span>
				</td>
			</tr>
			<tr>
				<th>Описание категории качества</th>
				<td colspan="3" id="compare_param_qualitycategory_descr">
					<%if(readOnly){ %>
					<span><%=task.getGeneralCondition().getQuality_category_desc()==null?"":task.getGeneralCondition().getQuality_category_desc() %></span>
					<%}else{ %>
					<textarea name="Описание категории качества" onkeyup="fieldChanged(this)"><%=task.getGeneralCondition().getQuality_category_desc()==null?"":task.getGeneralCondition().getQuality_category_desc() %></textarea>
					<%} %>
				</td>
			</tr>
			<%if(taskJPA.isDocumentary()){ %>
			<tr>
				<th>Источник формирования покрытия для осуществления платежа по аккредитиву</th>
				<td colspan="3">
					<%if(!readOnly){ %><a href="javascript:;" onclick="$('#acredetivSourcePaymentSelectTemplateDiv').jqmShow();"><%} %>
					<span id="acredetiv_source_name"><%=taskJPA.getAcredetivSourcePaymentName() %></span>
					<%if(!readOnly){ %></a><input type="hidden" name="acredetiv_source" id="acredetiv_source"
												  value="<%=taskJPA.getAcredetivSourcePayment()==null?"":taskJPA.getAcredetivSourcePayment().getId().toString()%>"><%} %>
				</td>
			</tr>
			<%} %>
		</table>
	</div>
	<div id="tabs-2">
		<%try{ %>
		<table id="main_otherGoalsId" class="regular">
			<tbody>
			<%
				for (int j = 0; j < otherGoals.size(); j++) {
					OtherGoal otherGoal = otherGoals.get(j);
			%>
			<tr>
				<td id="compare_param_othergoal_<%=j %>">
					<%  if (!readOnly || isCanEditFund) { %>
					<textarea name="main Иные цели" id="target<%=j%>"><%=otherGoal.getGoal()%></textarea>
					<a href="javascript:;" class="dialogActivator" dialogId="select_target_type"
					   onclick="$('#target_type_id').val('target<%=j%>');">
						<img alt="выбрать из шаблона" src="style/dots.png"></a>
					<input type="hidden" name="main Иные цели id" value="<%=otherGoal.getIdTarget()%>" id="target<%=j%>id">
					<input type="hidden" name="main Иные цели условие" value="<%=otherGoal.getCrmTargetTypeId()%>" id="target<%=j%>cond">
					<% } else { %>
					<span><%=otherGoal.getGoal()%></span>
					<% } %>
				</td>
				<% if (!readOnly || isCanEditFund) { %>
				<td class="delchk">
					<input type="checkbox" name="main_otherGoalsIdChk"/>
				</td>
				<% } %>
			</tr>
			<% } %>
			</tbody>
			<tfoot>
			<tr>
				<td id="compare_list_param_othergoal" class="compare-list-removed" colspan=2></td>
			</tr>
			<%
				if ((!readOnly || isCanEditFund)) {
			%>
			<tr>
				<td colspan=2 class="add">
					<button onclick='$( "#newTargetTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_otherGoalsId > TBODY" ); dialogHandler(); return false;' class="add"></button>
					<button onclick="DelRowWithLast('main_otherGoalsId', 'main_otherGoalsIdChk'); return false;" class="del"></button>
				</td>
			</tr>
			<%
				}
			%>
			</tfoot>
		</table>
		<%} catch (Exception e) {out.println("Ошибка в секции  frame_opportunityParam.jsp раздел целевое назначение:" + e.getMessage());e.printStackTrace();} %>
		<p><br /><b>Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)</b><br /><br /></p>
		<%try{ %>
		<table id="main_forbiddensId" class="regular">
			<tbody>
			<%
				for (int j = 0; j < forbiddens.size(); j++) {
					Forbidden forbidden = forbiddens.get(j);
			%>
			<tr>
				<td id="compare_param_forbidden_<%=j %>">
					<%  if (!readOnly || isCanEditFund) { %>
					<textarea class="expand50-200" onkeyup="fieldChanged(this)" name="main Forbiddens" id="illegal_target<%=j%>"><%=forbidden.getGoal()%></textarea>
					<a href="javascript:;" class="dialogActivator" dialogId="select_illegal_target_type"
					   onclick="$('#illegal_target_type_id').val('illegal_target<%=j%>');">
						<img alt="выбрать из шаблона" src="style/dots.png"></a>					
					<% } else { %>
					<span><%=forbidden.getGoal()%></span>
					<% } %>
				</td>
				<% if (!readOnly || isCanEditFund) { %>
				<td class="delchk">
					<input type="checkbox" name="main_forbiddensIdChk"/>
				</td>
				<% } %>
			</tr>
			<% } %>
			</tbody>
			<tfoot>
			<tr>
				<td id="compare_list_param_forbidden" class="compare-list-removed" colspan=2></td>
			</tr>
			<%
				if ((!readOnly || isCanEditFund)) {
			%>
			<tr>
				<td colspan=2 class="add">
					<button onclick='$( "#newIllegalTargetTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_forbiddensId > TBODY" ); dialogHandler(); jQuery("textarea[class*=expand]").TextAreaExpander(); fieldChanged();return false;' class="add"></button>
					<button onclick="DelRowWithLast('main_forbiddensId', 'main_forbiddensIdChk'); return false;" class="del"></button>
				</td>
			</tr>
			<%
				}
			%>
			</tfoot>
		</table>
		<%} catch (Exception e) {out.println("Ошибка в секции  frame_opportunityParam.jsp раздел целевое назначение:" + e.getMessage());e.printStackTrace();} %>
	</div>
	<div id="tabs-3">
		<jsp:include flush="true" page="frame_trance.jsp"/>
	</div>
</div>

<div id="acredetivSourcePaymentSelectTemplateDiv" class="jqmWindow" style="height:75%;overflow:auto">
    <ol>
            <li><a href="javascript:;" class="jqmClose" 
                onclick="$('#acredetiv_source').val('');$('#acredetiv_source_name').html('не выбран');">
            не выбран</a></li>
        <%for(ru.md.spo.dbobjects.CdAcredetivSourcePaymentJPA ac : taskFacadeLocal.findAcredetivSourcePayment()){ %>
            <li><a href="javascript:;" class="jqmClose" 
                    onclick="$('#acredetiv_source').val('<%=ac.getId() %>');$('#acredetiv_source_name').html('<%=ac.getNameSource()%>');">
                    <%=ac.getNameSource() %></a></li>
        <%}%>
    </ol>
    <hr>
<a href="#" class="jqmClose">Закрыть</a>
</div>



<script type="text/javascript">
$().ready(function() {
    $('#acredetivSourcePaymentSelectTemplateDiv').jqm();
	trance_graphOnClick();
	loadCompareResult('parameters');
});
</script>
<%} catch (Exception e) {out.println("Ошибка в секции  frame_opportunityParam.jsp:" + e.getMessage());e.printStackTrace();} %>
