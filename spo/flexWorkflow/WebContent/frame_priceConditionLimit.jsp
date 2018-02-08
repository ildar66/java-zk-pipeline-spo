<%@page import="ru.md.spo.dbobjects.CdPremiumTypeJPA"%>
<%@page import="ru.md.spo.dbobjects.CdCreditTurnoverPremiumJPA"%>
<%@page import="ru.md.spo.dbobjects.CdRiskpremiumJPA"%>
<%@page isELIgnored="true" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.Commission" %>
<%@page import="com.vtb.domain.TaskProcent" %>
<%@page import="ru.masterdm.compendium.domain.crm.CommissionType" %>
<%@page import="ru.masterdm.compendium.domain.spo.StandardPriceCondition" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	boolean readOnly =!TaskHelper.isEditMode("Стоимостные условия",request); 
	Task task=TaskHelper.findTask(request);
	ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
	TaskProcent taskProcent = task.getTaskProcent();
	ArrayList fineList = task.getFineList();
	ArrayList commissionList = task.getCommissionList();
	ArrayList<StandardPriceCondition> stList =  taskProcent.getStandardPriceConditionList();
	
	CompendiumCrmActionProcessor processor = 
        (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
    CommissionType[] comTypesList = processor.findComissionTypeList("", "c.name");
    
	boolean isEmpty = ((taskProcent.getRiskpremium() == null) || ((taskProcent.getRiskpremium() != null) && taskProcent.getRiskpremium().equals(0.0D)))
					&& (stList.size()==0) && (commissionList.size()==0)
					&& (fineList.size()==0) && Formatter.str(taskProcent.getCapitalPay()).equals(""); 
	boolean isCanEditFund = TaskHelper.isCanEditFund(request);
	%>
	<input type="hidden" id="punitiveMeasureTemplate" value="">
	<input type="hidden" id="commissionCounter" value="<%commissionList.size();%>">
	
<script id="newPremiumTemplate" type="text/x-jquery-tmpl">
        <tr id="PremiumJPA${id}"><td>
                                    <select name="premiumtype" id="premiumtype${id}"  
                                    onchange="onPremiumTypeClick('${id}');fieldChanged(this);">
                                    <option value="0"></option>
                                    <%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.SUBLIMIT)){ %>
                                    <option value="<%=pt.getId()%>" 
                                    ><%=pt.getPremium_name()%></option>
                                    <%} %>
                                    </select>
                                </td><td id="premiumSizeTr${id}" style="display:none">
                                   <span id="premiumSize${id}"></span>     
                                    <md:inputMoney name="premiumvalue" value="" 
                                        id="premiumvalue" readonly="false"  styleClass="text money" idsfx="${id}"
                                        onBlur="input_autochange(this,'money');fieldChanged(this);" />
                                    <md:currency id="premiumcurr" name="premiumcurr" readonly="false" 
                                        value="" idsfx="${id}"
                                        withoutprocent="true" />
                                    <md:currency id="premiumcurrpercent" name="premiumcurrpercent" readonly="false" 
                                        value="" idsfx="${id}"  
                                        withoutprocent="false" />
                                    <textarea id="premiumtext${id}" name="premiumtext" onkeyup="fieldChanged(this);"></textarea>
                                </td>
                                <td><a href="javascript:;" onclick="$('#PremiumJPA${id}').remove()">удалить</a></td>
                                </tr>
</script>
<script id="newCommissionTemplate" type="text/x-jquery-tmpl">
<tr>
<td style="width:20%">
	<md:comissionType style="width:20em;" readonly="false" name="limit_Наименование комиссии"  value="" id="${IDPATTERN}"  
      onChange="changeCommissionDescription(this.id, this.options[this.selectedIndex].value);fieldChanged();" />
</td>
<td style="width:40%">
	<textarea name="limit_Сумма - описание комиссии" style="width:95%;" id="comTypeRes_${nextid}" onkeyup="fieldChanged(this);"></textarea>
</td>
<td style="width:20%">
		<md:PatternPaidPercent readonly="false" name="limit_Порядок уплаты процентов Комиссии" value="-1" style="" onChange="fieldChanged();"/>
</td>
<td style="width:20%">
    <md:inputMoney name="limit_commiss_value" styleClass="text money"
    value="0" readonly="<%=readOnly%>" onkeyup="fieldChanged();"/>
    <md:currency readonly="<%=readOnly %>" value="" withyearprocent="true"
    name="limit_commiss_currency" withoutprocent="false" />
</td>
<td class="delchk"><input type="checkbox" name="idCommissionTableChk"/></td>
</tr>
</script>
	<script language="javascript">
			$(document).ready(function() {
				fancyClassSubscribe();
				loadCompareResult('price_conditions');
			});

		function insertPremiumTR(){
		    fieldChanged();
		    var id=getNextId();
		    $("#newPremiumTemplate").tmpl({id:id}).appendTo( "#PremiumJPATable" );
		}
		function insertCommissionTR(){
			fieldChanged();
			nextid=document.getElementById('commissionCounter').value + 'x';
			document.getElementById('commissionCounter').value=nextid;
			insertedId = 'comType' + nextid +'';
			var param = {
					nextid:nextid, 
					IDPATTERN:insertedId
			};
			$("#newCommissionTemplate").tmpl(param).appendTo( "#idCommissionTable > TBODY" );
		}
	
		function changeCommissionDescription(selectIdx, chosenCommissionIdx) {
			noprefix = selectIdx.replace("comType","");
			commissionDescription = document.getElementById('comType_'+chosenCommissionIdx).value;
			document.getElementById('comTypeRes_'+noprefix).value = commissionDescription;
		}
	</script>
<%if(!readOnly){ %>
<input type="hidden" value="y" name="percentStavka_section">
<input type="hidden" value="y" name="commission_section">
<input type="hidden" value="y" name="graph_section">
<input type="hidden" value="y" name="fineList_section">
<%} %>	
		<%try{ %>
				<%if (!readOnly || isCanEditFund) { %>
						<input type="hidden" id="Секция_стоимостных условий" name="Секция_стоимостных условий" value="YES" />
				<% } %>
					<h3>Процентная ставка</h3>
					<table id="testPercentStavkaTableId" class="regular">
						<tbody>
						<%try{ %>
							<tr align="left">
								<td colspan="1"><b>Тип премии за кредитный риск</b></td>
								<td colspan="2" id="compare_limitprice_riskpremium_display">
								    <%if(!readOnly){ %>
								    <a class="dialogActivator" dialogId="riskpremiumTypeTemplate" onclick="fieldChanged(this);" href="javascript:;"><%} %>
								    <span id="riskpremium_type_name">
                                    <%=taskJPA.getRiskpremiumDisplay() %>
                                    </span><%if(!readOnly){ %></a><%} %>
                                    <input name="limit_riskpremium" id="riskpremium_type" value="<%=taskJPA.getRiskpremiumID()%>" type="hidden">
                                </td>
							</tr>
							<tr align="left" id="riskpremium_change_tr" <%=taskJPA.showRiskpremiumChange()?"":"style=\"display:none\"" %>>
								<td colspan="1" id="riskpremium_change_name"><b>Величина изменения</b></td>
								<td colspan="2" id="compare_limitprice_riskpremium_change"><md:inputMoney name="riskpremium_change" value="<%=Formatter.format(taskJPA.getRiskpremium_change()) %>" 
                                        id="riskpremium_change" readonly="<%=readOnly %>"  styleClass="text money" onBlur="input_autochange(this,'money')" /> % годовых</td>
							</tr>
							<tr align="left">
	                            <td colspan="1"><b>Индивидуальные условия</b></td>
	                            <td colspan="2" id="compare_limitprice_ind_codition">
	                                <% if (readOnly) { %>
	                                    <div>
	                                        <%=Formatter.str(taskProcent.getPriceIndCondition()) %>
	                                    </div>                                
	                                <% } else { %>
	                                    <textarea rows="3" onkeyup="fieldChanged(this)" name="priceIndCondition"><%=Formatter.str(taskProcent.getPriceIndCondition()) %></textarea>
	                                <% } %>
	                            </td>
	                        </tr>
							<tr align="left">
								<td colspan="1"><b>Премия за кредитный риск</b></td>
								<td colspan="2" id="compare_limitprice_riskpremium_value">
									<md:inputMoney name="limit_Премия за риск" value="<%=taskProcent.getFormattedRiskpremium() %>" 
										readonly="<%=readOnly && !isCanEditFund %>"  styleClass="text money" onBlur="input_autochange(this,'money')" />
										% годовых
								</td>
							</tr>
							
							<tr align="left" style="display: none;">
								<td colspan="1"><b>Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера</b></td>
								<td colspan="2" id="compare_price_turnover_limit_depricated">
									<md:inputMoney name="limit_turnover" value="<%=Formatter.format(taskJPA.getTurnover()) %>" 
										readonly="<%=readOnly && !isCanEditFund%>"  styleClass="text money" onBlur="input_autochange(this,'money')" onChange="fieldChanged(this);" />
									%
									<%if(readOnly){ %>
									<%=taskJPA.getTurnoverPremiumDisplay() %>
									<%}else{ %>
									<select name="limit_turnover_premium" onchange=fieldChanged(this);"">
									<%for(CdCreditTurnoverPremiumJPA to: taskFacadeLocal.findCdCreditTurnoverPremium()){ %>
									<option value="<%=to.getId().toString()%>" 
									<%=(taskJPA.getTurnoverPremium()!=null&&to.getId().equals(taskJPA.getTurnoverPremium().getId()))?"selected":"" %> 
									><%=to.getDescription() %></option>
									<%} %>
									</select>
									<%} %>
								</td>
							</tr>
							
						<%if(taskJPA.isDocumentary() && taskJPA.isSublimit() || taskJPA.isLimit()){ %>
                        <tr align="left">
                            <td colspan="1"><b>Вознаграждения</b></td>
                            <td colspan="2">
		                            <%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.SUBLIMIT)){ %>
		                            <span id="premiumTradeValue<%=pt.getId()%>" style="display: none;"><%=pt.getValue() %></span>
		                            <%} %>
                            <table id="PremiumJPATable")>
                                <tr><th>Тип</th><th>Размер вознаграждения</th></tr>
                                <%for(ru.md.spo.dbobjects.PremiumJPA p : taskJPA.getPremiumList()){ %>
                                <tr id="PremiumJPA<%=p.getId() %>"><td id="compare_limitprice_premium<%=p.getId()%>">
		                            <select name="premiumtype" id="premiumtype<%=p.getId() %>" <%=readOnly?"disabled":"" %> 
		                            onchange="onPremiumTypeClick('<%=p.getId() %>');fieldChanged(this);">
		                            <option value="0"></option>
		                            <%for(CdPremiumTypeJPA pt : taskFacadeLocal.findRiskpremiumType(CdPremiumTypeJPA.Type.SUBLIMIT)){ %>
		                            <option value="<%=pt.getId()%>" <%=pt.equals(p.getPremiumType())?"selected":"" %>
		                            ><%=pt.getPremium_name()%></option>
		                            <%} %>
		                            </select>
                                </td><td id="premiumSizeTr<%=p.getId() %>">
	                               <span id="premiumSize<%=p.getId() %>"></span>     
	                                <md:inputMoney name="premiumvalue" value="<%=Formatter.toMoneyFormat(p.getVal()) %>" 
	                                    id="premiumvalue" readonly="<%=readOnly %>"  styleClass="text money" idsfx="<%= p.getId().toString()%>"
	                                    onBlur="input_autochange(this,'money');fieldChanged(this);" />
	                                <md:currency id="premiumcurr" name="premiumcurr" readonly="<%=readOnly %>" 
	                                    value="<%=p.getCurr() %>" idsfx="<%= p.getId().toString()%>"
	                                    withoutprocent="true" />
	                                <md:currency id="premiumcurrpercent" name="premiumcurrpercent" readonly="<%=readOnly %>" 
	                                    value="<%=p.getCurr() %>" idsfx="<%= p.getId().toString()%>"
	                                    withoutprocent="false" />
	                                <textarea id="premiumtext<%=p.getId() %>" name="premiumtext" onkeyup="fieldChanged(this);" <%=readOnly?"disabled":"" %>><%=p.getText() %></textarea>
                                </td>
                                <%if(!readOnly){ %>
                                <td><a href="javascript:;" onclick="$('#PremiumJPA<%=p.getId() %>').remove()">удалить</a></td>
                                <%} %>
                                </tr>
                                <%} %>
                                <tr>
                                	<td id="compare_list_limitprice_premium" class="compare-list-removed" colspan="2"></td>
                                </tr>
                            </table>
                            <%if(!readOnly){ %><a  href="javascript:;" onclick="insertPremiumTR()">Добавить вознаграждение</a><%} %>
                            </td>
                        </tr>
                        <%} %>
                        <tr align="left">
                            <td colspan="1" style="width: 50%;"><b>Порядок уплаты процентов по кредиту/кредитной линии с лимитом выдачи <br />на цели формирования покрытия для осуществления платежей  по аккредитивам</b></td>
                            <td colspan="2" style="width: 50%;">ежемесячно</td>
                        </tr>
							<tr align="left" style="display: none">
								<td colspan="1"><b>Стандартные стоимостные условия</b></td>
								<td colspan="2">
							   <!-- начало таблицы стандартных стоимостных условий -->
							   <table id ="idStandardPriceConditionTable" class="add">
								 <tbody>
								 <%try{
										for (int j=0; j<stList.size(); j++) {
										StandardPriceCondition cond = stList.get(j);
									%>
										<tr>
											<td>
												<md:stPriceCondition readonly="<%=readOnly %>" style="width:50em;" 
													name="Стандартные стоимостные условия" value="<%=cond.getId().toString()%>" /> 
											</td>
											<%if (!readOnly) {%>
											<td class="delchk"><input type="checkbox" name="idStandardPriceConditionTableChk"/></td>
											<%}%>
										</tr>
									<% }
                                 } catch (Exception e) {out.println("Ошибка в секции  frame_priceConditionLimit.jsp:" + e.getMessage());e.printStackTrace();} %>
								</tbody>
								<%if ((!readOnly)) {%>
								<tfoot>
									<tr>
										<td colspan=2 class="add">
											<button onclick="AddRowToStandardPriceConditionTable(); return false;" class="add"></button>
											<button onclick="DelRowWithLast('idStandardPriceConditionTable', 'idStandardPriceConditionTableChk'); return false;" class="del" style="margin-left: 5px !important;"></button>
										</td>
									</tr>
								</tfoot>
								<%}%>
							</table>
							<!-- конец таблицы стандартных стоимостных условий -->
							</td>
						  </tr>
						  <tr align="left" style="display: none;">
							<td colspan="1"><b>Описание процентной ставки и премии</b></td>
							<td colspan="2">
								<%if(!readOnly){ %>
									<textarea name="limit_Описание процентной ставки" style="width:95%;"
									><%=(taskProcent.getDescription()==null)?"":taskProcent.getDescription() %></textarea>
									<%}else{ %>
									<span style="width:95%;"
									><%=(taskProcent.getDescription()==null)?"":taskProcent.getDescription() %></span>
									<%} %>
							</td>
						</tr>
						<tr align="left">
							<td colspan="1"><b>Порядок уплаты процентов</b></td>
							<td colspan="2" id="compare_limitprice_pay_int">
								 <%if(!readOnly){%>
				                    <textarea name="pay_int" id="pay_int"><%=taskProcent.getPay_int()%></textarea>
				                    <a href="javascript:;" class="dialogActivator" dialogId="select_pay_int">
					                    <img alt="выбрать из шаблона" src="style/dots.png"></a>
				                <%} else {%>
				                    <span><%=taskProcent.getPay_int()%></span>
				                <%}%>
							</td>
						</tr>
						<tr align="left">
							<td colspan="1"><b>КТР</b></td>
							<td colspan="2" id="compare_limitprice_ktr">
							     <% if (readOnly && !isCanEditFund) { %>
                                    <div>
                                        <%=Formatter.str(taskProcent.getKTR()) %>
                                    </div>                                
                                <% } else { %>
                                    <input type="text" name="limit_KTR" value="<%=Formatter.str(taskProcent.getKTR()) %>" onkeyup="fieldChanged(this)" />
                                <% } %>
							</td>
						</tr>
						<% if (task.isSubLimit() || task.isLimit()) { %>
                        <tr align="left">
                            <td colspan="1"><b>Плата за экономический капитал</b></td>
                            <td colspan="2" id="compare_limitprice_capital_pay">
                                <% if (readOnly && !isCanEditFund) { %>
                                    <div>
                                        <%=Formatter.str(taskProcent.getCapitalPay()) %>
                                    </div>                                
                                <% } else { %>
                                    <textarea rows="3" onkeyup="fieldChanged(this)" id="sublimit_capitalPay" name="sublimit_capitalPay" %><%=Formatter.str(taskProcent.getCapitalPay()) %></textarea>
                                <% } %>
                            </td>
                        </tr>
                        <%} %>
						
						<%} catch (Exception e) {out.println("Ошибка в секции  frame_priceConditionLimit.jsp:" + e.getMessage());e.printStackTrace();} %>
						</tbody>
					</table>
					<h3>Комиссии</h3>
					<!-- вставим массив описаний для типов комиссий-->
					<input type="hidden" id="comType_-1" value=" ">
					<%for (int i=0; i<comTypesList.length; i++){
                		CommissionType entity = comTypesList[i];
					%>
						<input type="hidden" id="comType_<%=entity.getId()%>" value="<%=entity.getName()%>">
					<% }%>
					
					<table id ="idCommissionTable" class="add regular" style="width: 99%;">
						<thead>
							<tr>
								<th style="width:20%">Тип комиссии</th>
								<th style="width:40%">Описание комиссий</th>
								<th style="width:20%">Порядок уплаты комиссий</th>
								<th style="width:20%">Величина комиссии</th>
								<td></td>
							</tr>
						</thead>
						<tbody>
							<tr style="display:none"><!-- empty string  -->
								<td>1</td><td>2</td><td>3</td><td>4</td>
								<td class="delchk">
									<input type="checkbox" name="idCommissionTableChk"/>
								</td>
							</tr>
						<%try{ 
							for (int j=0; j<commissionList.size(); j++) {
							Commission commission = (Commission)commissionList.get(j);
							%>
							<tr>
								<td style="width:20%" id="compare_limitprice_comission<%=commission.getId()%>">
										<md:comissionType  readonly="<%=readOnly %>" style="width:20em;" name="limit_Наименование комиссии" 
										value="<%=commission.getName().getId() %>" 
										id="<%=Commission.generateComTypePrefix(j)%>"
										onChange="changeCommissionDescription(this.id, this.options[this.selectedIndex].value);fieldChanged(this);"
										/>
								</td>
								<td style="width:40%" id="compare_limitprice_comission<%=commission.getId()%>_descr">
									<%if(!readOnly){ %>
										<textarea name="limit_Сумма - описание комиссии" style="width:95%;" 
											id="comTypeRes_<%=j%>"  onkeyup="fieldChanged(this);"
										><%=(commission.getDescription() != null) ? commission.getDescription() : "" %></textarea>
									<%}else{ %>
										<span style="width:95%;"><%=(commission.getDescription() != null && !commission.getDescription().equals("null")) ? commission.getDescription() : "" %></span>
									<%} %>
									<%
										String commissionLimitPayPattern = 
											(commission.getCommissionLimitPayPattern() != null) ? Formatter.str(commission.getCommissionLimitPayPattern().getId()) : ""; 
									%>
								</td>
								<td style="width:20%" id="compare_limitprice_comission<%=commission.getId()%>_paypattern">
									<md:PatternPaidPercent readonly="<%=readOnly%>" name="limit_Порядок уплаты процентов Комиссии" 
									value="<%=commissionLimitPayPattern%>" onChange="fieldChanged();"
									style="" />
								</td>
								<td style="width:20%" id="compare_limitprice_comission<%=commission.getId()%>_value">
	                                    <md:inputMoney name="limit_commiss_value" styleClass="text money" onkeyup="fieldChanged();"
	                                    value="<%=commission.getFormattedValue()%>" readonly="<%=readOnly%>" />
    	                                <md:currency readonly="<%=readOnly %>" value="<%=commission.getCurrencyCode() %>"
                                        name="limit_commiss_currency" withoutprocent="false" withyearprocent="true" />
                                </td>
                                <td></td>
								<%
								if (!readOnly) {
								%>
								<td class="delchk">
									<input type="checkbox" name="idCommissionTableChk"/>
								</td>
								<%
								}
								%>
							</tr>
							<%
							}
							%>
						<%} catch (Exception e) {out.println("Ошибка в секции  frame_priceConditionLimit.jsp:" + e.getMessage());e.printStackTrace();} %>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5" class="compare-list-removed" id="compare_list_limitprice_comission"></td>
							</tr>
						<% if ((!readOnly)) { %>
							<tr>
								<td colspan="5" class="add">
									<button onmouseover="Tip(getToolTip('Добавить комиссию'))" onmouseout="UnTip()" onclick="insertCommissionTR(); return false;" class="add"></button>
									<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRow('idCommissionTable', 'idCommissionTableChk'); return false;" class="del"></button>
								</td>
							</tr>
						<%
						}
						%>
						</tfoot>
					</table>

					<h3>Санкции (неустойки, штрафы, пени и т.д.)</h3>
					<jsp:include flush="true" page="frame/fineList.jsp"/>
		<%} catch (Exception e) {out.println("Ошибка в секции  frame_priceConditionLimit.jsp:" + e.getMessage());e.printStackTrace();} %>
<div id="StandardPriceConditionDiv" class="jqmWindow" style="height:75%;overflow:auto">
Выбрать cтандартные стоимостные условия:
<table class="pane" border="1">
<THEAD><TR><td>Стандартные стоимостные условия</td></TR></THEAD>
<TBODY>
<tr><td class="jqmClose" onClick="$('#'+$('#supplyid').val()).val('0');$('#sp'+$('#supplyid').val()).html($('#spansupply0').html())"><span id="spansupply0">не выбрано</span></td></tr>
<%
CompendiumSpoActionProcessor processorSPO = 
                (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
StandardPriceCondition[] list = processorSPO.findStandardPriceConditionList("%", "c.name");
for(int i=0;i<list.length;i++){
StandardPriceCondition entity = (StandardPriceCondition)list[i];
%>
<tr><td class="jqmClose" onClick="$('#'+$('#supplyid').val()).val('<%=entity.getId().toString() %>');$('#sp'+$('#supplyid').val()).html($('#spansupply<%=entity.getId().toString() %>').html())"><span id="spansupply<%=entity.getId().toString() %>"><%=entity.getName() %></span></td></tr>
<%} %>
</TBODY>
</table>
<hr>
<a href="#" class="jqmClose">Закрыть</a>
</div>

<div id="riskpremiumTypeTemplate" title="Выбрать тип премии за кредитный риск" style="display: none;">
	<ul>
		<%for(CdRiskpremiumJPA to: taskFacadeLocal.findCdRiskpremium()){ %>
			<li>
			    <a class="disable-decoration" href="javascript:;" onclick="riskpremiumTypeTemplateClick('<%=to.getDescription()%>','<%=to.getValue()%>','<%=to.getId()%>','');">
			        <%=to.getDescription() %>
			    </a>
		    </li>
		<%} %>
	</ul>
</div>


<script type="text/javascript">
$().ready(function() {
  $('#StandardPriceConditionDiv').jqm();
  <%for(ru.md.spo.dbobjects.PremiumJPA p : taskJPA.getPremiumList()){ %>
  onPremiumTypeClick('<%=p.getId()%>');
  <%}%>
});
</script>
