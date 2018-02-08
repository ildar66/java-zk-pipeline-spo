<%@page isELIgnored="true" %>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.vtb.domain.OtherCondition" %>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
    Task task=TaskHelper.findTask(request);
	Long idMdTask = task.getId_task();
	TaskJPA taskJPA = TaskHelper.taskFacade().getTask(idMdTask);
	String dtypecode="dttypecode";
	String dialogProperty = "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes";
	boolean readOnly = !TaskHelper.isEditMode("R_Залоги",request);
	int sz = task.getSupply().getDeposit().size();
ru.masterdm.compendium.model.CompendiumCrmActionProcessor compenduimCRM = (ru.masterdm.compendium.model.CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
ru.masterdm.compendium.domain.crm.Ensuring[] listEnsuring = compenduimCRM.findEnsuringList(new ru.masterdm.compendium.domain.crm.Ensuring(true),"c.text");
String toDate4newSupply = taskJPA.getToDate4newSupplyFormated("d");
String period4newSupply = taskJPA.getPeriod4newSupplyFormated("d");
ArrayList<OtherCondition> otherlist = task.getOtherCondition();
%>
<%if(!readOnly){ %><input name="section_depositor" type="hidden"><%} %>
<script id="DepositorTemplatePerson" type="text/x-jquery-tmpl">
<a href="PersonInfo.jsp?id=${crmid}" target=_blank>${orgname}</a>
<input type="hidden" name="d_contractor" value=''/>
<input type="hidden" name="d_person" value='${crmid}'/>
</script>
<script id="DepositorTemplateOrg" type="text/x-jquery-tmpl">
<a href="clientInfo.html?id=${crmid}&mdtask=<%=idMdTask%>" target="_blank">${orgname}</a>
<span class="rating">${rating}</span><input type="hidden" name="d_contractor" value="${crmid}"/>
<input type="hidden" name="d_person" value=''/>
</script>
<script id="newDepositTemplate" type="text/x-jquery-tmpl">
<tr>
<td style="width: 15%;" id="orgtd${nextid}"></td>
<td style="width: 85%;">
	<div style="display: none;"><label>Эмитент ценных бумаг<br />
		<span id="d_issuer_span${nextid}"></span>
		<a href="popup_org.jsp?formName=variables&fieldNames=selectedID|selectedName|CRMID&ek=only&onMySelect=d_changeIssuer('${nextid}');" 
		onclick='return openDialog(this.href, "organizationLookupList", "<%= dialogProperty%>");'>
		<img src="style/dots.png" alt="изменить эмитента"></a>
		<input type="hidden" name="d_issuer" id="d_issuer${nextid}" value=''/></label><br />
	</div>
	<table class="provisionPledgesFirst" style="margin-bottom: 20px; width: 100%;">
		<tr>
			<td colspan="2">
				<label><input type="radio" value="y" name="d_main${crmid}">Основное Обеспечение &nbsp;
				<input type="radio" value="n" checked="checked" name="d_main${crmid}">Дополнительное обеспечение</label> &nbsp;
				<label><input type="checkbox" value="y" name="d_posled${crmid}">Послед. залог</label>
			</td>
		</tr>
		<tr>
			<th>Вид залога</th>
			<td>
				<select name="d_type" style="width: 95%;">
					<option value=""> </option>
					<option value="Залог">Залог</option>
					<option value="Заклад">Заклад</option>
					<option value="Ипотека">Ипотека</option>
					<option value="Передача векселя с залоговым индоссаментом">Передача векселя с залоговым индоссаментом</option>
				</select>
			</td>
		</tr>
		<tr>
			<th>Предмет залога</th>
			<td>
				<select name="d_zalogObject" style="width: 95%;">
				  <option value=""> </option>
					<%for(ru.masterdm.compendium.domain.crm.Ensuring ens : listEnsuring){%>
				  <option value="<%=ens.getId() %>"><%=ens.getText() %></option>
				 <%} %>
				</select>
				<br /></div>
			</td>
		</tr>
		<tr>
			<th>Наименование<br /> и характеристики<br /> предмета залога</th>
			<td>
				<textarea name="d_zalog_desc"></textarea>
			</td>
		</tr>
		<tr>
			<th>Порядок определения<br /> рыночной стоимости</th>
			<td>
				<textarea name="d_orderDescription"></textarea>
			</td>
		</tr>
		<tr>
			<th>Коэффициент<br /> залогового дисконтирования</th>
			<td>
				<input name="d_discount" value="" styleClass="money" onBlur="input_autochange(this,'money')" />
			</td>
		</tr>
		<tr>
			<th>Описание<br /> залоговой сделки</th>
			<td>
				<textarea name="d_oppDescription"></textarea>
			</td>
		</tr>
		<tr>
			<th>Группа<br /> обеспечения</th>
			<td>
				<a href="javascript:;" class="dialogActivator" dialogId="supplySelection" onClick="$('#supplyid').val('${nextid}')">
				<span id="sp${nextid}">не выбрана</span><input type="hidden" id="${nextid}" name="d_SupplyType" value="-1"></a>
				</span>
			</td>
		</tr>
		<tr>
			<th>Условия<br /> страхования</th>
			<td>
				<textarea name="d_cond"></textarea>
			</td>
		</tr>
		<tr>
			<td></td>
		</tr>
		</table>
		<table class="provisionPledgesSecond" style="margin-top: 20px; margin-bottom: 20px; width: 100%;">
			<tr>
				<th>Рыночная стоимость<br /> предмета залога (руб.)</th>
				<th>Ликвидационная стоимость<br /> предмета залога (руб.)</th>
				<th>Залоговая стоимость (руб.)</th>
			</tr>
			<tr>
				<td>
					<input name="d_zalogMarket" style="width: 95%;  value="" styleClass="money" onBlur="input_autochange(this,'money')" />
				</td>
				<td>
					<input name="d_zalogTerminate" style="width: 95%;" value="" styleClass="money" onBlur="input_autochange(this,'money')" />
				</td>
				<td>
					<input name="d_zalog" style="width: 95%;" value="" Class="money" onBlur="input_autochange(this,'money')" />
				</td>
			</tr>
			<tr>
				<th>Категория обеспечения<br /> (уровень ликвидности залога)</th>
				<th>Степень обеспечения, % </th>
				<th>Максимально возможная доля<br /> необеспеченной части сублимита</th>
			</tr>
			<tr>
				<td>
					<md:LiquidityLevel readonly="false" name="d_LIQUIDITY_LEVEL" value=""/>
				</td>
				<td>
					<md:inputMoney name="dsupplyvalue" readonly="false" value=""/>
				</td>
				<td>
					<md:input onBlur="input_autochange(this,'money')" name="d_maxpart" readonly="<%=readOnly%>" value="" styleClass="money"/>
				</td>
			</tr>
			<tr>
				<th>Срок залога</th>
				<th>Удельный вес вида залога</th>
				<th>Финансовое состояние залогодателя</th>
			</tr>
			<tr>
				<td>
					<md:inputInt name="dperiod" value="<%=period4newSupply%>" style="width:6em;" readonly="<%=readOnly %>" onBlur="input_autochange(this,'digitsSpaces');" />
					<select name="dperiodDimension">
					<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
					<option <%if(periodDimension.equals(taskJPA.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
					<%} %>
					</select>
					по дату <md:calendarium name="dtodate" readonly="false" value="<%=toDate4newSupply%>" id="${nextid}w"/>
				</td>
				<td>
					<md:input onBlur="input_autochange(this,'money')" name="d_weight" readonly="<%=readOnly%>" value="" styleClass="money"/>
				</td>
				<td>
					<md:DepositorFinStatus readonly="false" name="d_DEPOSITOR_FIN_STATUS" value=""/>
				</td>
			</tr>
	</table>						
</td>
<td style="width: 20px;"></td>
<td class="delchk"><input type="checkbox" name="idTablesSupplyDChk" /></td>
</tr>
</script>
	<%try{ %>
			<h3>Залоги</h3>
			<table id="idTablesSupplyD" style="width: 100%;">
				<thead>
					<tr>
						<th style="width:auto">Залогодатель</th>
						<th>Описание залога</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<%try{ %>
				<%
					int key = 0;
					for(com.vtb.domain.Deposit d : task.getSupply().getDeposit()){
					key++;
					dtypecode+="q";
					String zalogMarket = d.getZalogMarket()==null?"":d.getZalogMarket().toString();
					String discount = d.getDiscount()==null?"":d.getDiscount().toString();
					String zalogTerminate = d.getZalogTerminate()==null?"":d.getZalogTerminate().toString();
					String zalog = d.getZalog()==null?"":d.getZalog().toString();
					String accountid = d.getOrg().getAccountid();
					if(d.getPerson().getId()!=null&&d.getPerson().getId().longValue()!=0) {
					    accountid = d.getPerson().getId().toString();
					}
					String rating = processor.getRating(accountid).getRating();
					%>
					<tr>
						<td id="compare_supply_depositor<%=key%>" style="width: 15%;">
							<%if(d.getPerson().getId()!=null&&d.getPerson().getId().longValue()!=0){ %>
								<a href="PersonInfo.jsp?id=<%=d.getPerson().getId().toString() %>" 
									target=_blank><%=d.getPerson().getLastName() %></a>
							<%}else{ %>
								<a href="/<%=ApplProperties.getwebcontextFWF() %>/clientInfo.html?id=<%=accountid %>&mdtask=<%=idMdTask%>"
									target=_blank><%=SBeanLocator.singleton().compendium().getEkNameByOrgId(accountid) %></a>
								<% if((rating !=null) && (!rating.equals(""))){ %><span class="rating">Рейтинг <%=rating%></span><%} %>
								<%} %>
							<%if(!readOnly){ %><input type="hidden" name="d_contractor" value='<%=accountid%>'/>
								<input type="hidden" name="d_person" value='<%= (d.getPerson().getId()!=null) ? d.getPerson().getId().toString() : ""%>'/>
							<%} %>
						</td>
						<td style="width: 85%;">
							<table class="provisionPledgesFirst">
								<tr>
									<td colspan="2">
										<label id="compare_supply_depositor<%=key%>_main"><input type="radio" value="y" onclick="fieldChanged();"
										<%=d.isMain()?"checked=\"checked\"":"" %>  
										<%if(readOnly){%>disabled="disabled"<%}%> 
										name="d_main<%=accountid%>">Основное Обеспечение &nbsp;
										<input type="radio" value="n" onclick="fieldChanged();"
										<%=!d.isMain()?"checked=\"checked\"":"" %>  
										<%if(readOnly){%>disabled="disabled"<%}%> 
										name="d_main<%=accountid%>">Дополнительное обеспечение
										</label>
										&nbsp;
											<label id="compare_supply_depositor<%=key%>_posled">
											<input type="checkbox" value="y" <%=d.isPosled()?"checked=\"checked\"":"" %>  
												<%if(readOnly){%>disabled="disabled"<%}%> name="d_posled<%=accountid%>">
											Послед. залог
										</label>
									</td>
								</tr>
							    <tr id="compare_supply_depositor<%=key%>_type">
							        <th>Вид залога</th>
							        <td>
										<%if(readOnly){ %><span><%=d.getType() %></span>
										<%}else{ %>
											<select name="d_type" onclick="fieldChanged();" style="width: 95%;">
												<option value="" <%=d.getType().equals("")?"selected":"" %>> </option>
												<option value="Залог" <%=d.getType().equals("Залог")?"selected":"" %>>Залог</option>
												<option value="Заклад" <%=d.getType().equals("Заклад")?"selected":"" %>>Заклад</option>
												<option value="Ипотека" <%=d.getType().equals("Ипотека")?"selected":"" %>>Ипотека</option>
												<option value="Передача векселя с залоговым индоссаментом" <%=d.getType().equals("Передача векселя с залоговым индоссаментом")?"selected":"" %>>Передача векселя с залоговым индоссаментом</option>
											</select>
										<%} %>
									</td>
							    </tr>
							    <tr id="compare_supply_depositor<%=key%>_zalogobject">
							        <th>Предмет залога</th>
							        <td>
										<%if(readOnly){ %>
										<span><%=d.getZalogObject().getText() %></span>
										<%}else{ %>
										<select name="d_zalogObject" onclick="fieldChanged();" style="width: 95%;">
											<option value="" <%=d.getZalogObject().getId()!=null&&d.getZalogObject().getId().equals("")?"selected":"" %>> </option>
											<%for(ru.masterdm.compendium.domain.crm.Ensuring ens : listEnsuring){%>
												<option value="<%=ens.getId() %>" <%=d.getZalogObject().getId()!=null&&d.getZalogObject().getId().trim().equals(ens.getId().trim())?"selected":"" %>><%=ens.getText() %></option>
											<%} %>
										</select>
										<%} %>
										<div style="display: none;">
											<label>Эмитент ценных бумаг<br />
												<%if(readOnly){ %>
												<span><%=d.getIssuer().getAccount_name() %></span>
												<%}else{ %>
												<span id="d_issuer_span<%=dtypecode %>"><a href="/<%=ApplProperties.getwebcontextFWF() %>/clientInfo.html?id=<%=d.getIssuer().getAccountid()%>&mdtask=<%=idMdTask%>"
													target=_blank><%=d.getIssuer().getAccount_name() %></a></span>
													<a href="popup_org.jsp?formName=variables&fieldNames=selectedID|selectedName|CRMID&ek=only&onMySelect=d_changeIssuer('<%=dtypecode %>');" 
													onclick='return openDialog(this.href, "organizationLookupList", "<%= dialogProperty%>");'>
													<img src="style/dots.png" alt="изменить эмитента"></a>
													<input type="hidden" name="d_issuer" id="d_issuer<%=dtypecode %>" value='<%=d.getIssuer().getAccountid()%>'/>
												<%} %>
											</label>
										<br /></div>
									</td>
								</tr>
								<tr id="compare_supply_depositor<%=key%>_zalogdesc">
							        <th>Наименование<br /> и характеристики<br /> предмета залога</th>
							        <td>
										<%if(readOnly){ %><span><%=d.getZalogDescription() %></span><%}else{ %>
										<textarea onchange="fieldChanged();" name="d_zalog_desc" style="width: 95%;"><%=d.getZalogDescription() %></textarea><%} %>
									</td>
							    </tr>
							    <tr id="compare_supply_depositor<%=key%>_orderdesc">
							        <th>Порядок определения<br /> рыночной стоимости</th>
							        <td>
										<%if(readOnly){ %><span><%=d.getOrderDescription() %></span>
										<%}else{ %><textarea onchange="fieldChanged();" name="d_orderDescription" style="width: 95%;"><%=d.getOrderDescription() %></textarea><%} %>
									</td>
							    </tr>
								<tr id="compare_supply_depositor<%=key%>_discount">
							        <th>Коэффициент<br /> залогового дисконтирования</th>
							        <td>
										<md:input name="d_discount" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(discount)%>" styleClass="money" onBlur="input_autochange(this,'money')" />
									</td>
								</tr>
								<tr id="compare_supply_depositor<%=key%>_oppdesc">
							        <th>Описание<br /> залоговой сделки</th>
								    <td>
										<%if(readOnly){ %><span><%=d.getOppDescription() %></span>
										<%}else{ %><textarea onchange="fieldChanged();" name="d_oppDescription"><%=d.getOppDescription() %></textarea><%} %>
									</td>
							    </tr>
							    <tr id="compare_supply_depositor<%=key%>_ob">
							        <th>Группа<br /> обеспечения</th>
							        <td>
										<span class="newsupply"><md:SupplyType code="<%=dtypecode %>" readonly="<%=readOnly%>" name="d_SupplyType" 
										value="<%=(d.getOb()==null ||d.getOb().getId() == null || d.getOb().getId().longValue() == 0L || d.getOb().getId().longValue() == -1L)?null:d.getOb().getId().toString() %>"/></span>
									</td>
							    </tr>
							    <tr id="compare_supply_depositor<%=key%>_cond">
							        <th>Условия<br /> страхования</th>
							        <td>
										<%if(readOnly){ %><span><%=d.getCond() %></span><%}else{ %>
										<textarea onchange="fieldChanged();" name="d_cond"><%=d.getCond() %></textarea><%} %>
									</td>
								</tr>
					<tr>
						<td></td>
					</tr>
				</table>
								<table class="provisionPledgesSecond" style="margin-top: 20px; width: 100%;">
								<tr>
									<th id="compare_supply_depositor<%=key%>_zalogmarket">Рыночная стоимость<br /> предмета залога (руб.)</th>
									<th id="compare_supply_depositor<%=key%>_zalogterminate">Ликвидационная стоимость<br /> предмета залога (руб.)</th>
									<th id="compare_supply_depositor<%=key%>_zalog">Залоговая стоимость (руб.)</th>
								</tr>
								<tr>
									<td>
										<md:input name="d_zalogMarket" style="width: 95%;" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(zalogMarket)%>" styleClass="money" onBlur="input_autochange(this,'money')" />
									</td>
									<td>
										<md:input onBlur="input_autochange(this,'money')" name="d_zalogTerminate" style="width: 95%;" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(zalogTerminate) %>" styleClass="money"/>
									</td>
									<td>
										<md:input name="d_zalog" onBlur="input_autochange(this,'money')" style="width: 95%;" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(zalog) %>" styleClass="money"/>
									</td>
								</tr>
								<tr>
									<th id="compare_supply_depositor<%=key%>_liqlevel">Категория обеспечения<br /> (уровень ликвидности залога)</th>
									<th id="compare_supply_depositor<%=key%>_supplyvalue">Степень обеспечения, % </th>
									<th id="compare_supply_depositor<%=key%>_maxpart">Максимально возможная доля<br /> необеспеченной части сублимита</th>
								</tr>
								<tr>
									<td>
										<md:LiquidityLevel readonly="<%=readOnly%>" name="d_LIQUIDITY_LEVEL" value="<%=d.getLiquidityLevel().getId().toString() %>"/>
									</td>
									<td>
										<md:inputMoney name="dsupplyvalue" readonly="<%=readOnly %>" value="<%=Formatter.format(d.getSupplyvalue()) %>"/>
									</td>
									<td>
										<md:input onBlur="input_autochange(this,'money')" name="d_maxpart" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(d.getMaxpart()) %>" styleClass="money"/>
									</td>
								</tr>
								<tr>
									<th id="compare_supply_depositor<%=key%>_period">Срок залога</th>
									<th id="compare_supply_depositor<%=key%>_weight">Удельный вес вида залога</th>
									<th id="compare_supply_depositor<%=key%>_finstatus">Финансовое состояние залогодателя</th>
								</tr>
								<tr>
									<td>
										<md:inputInt 
											name="dperiod" value="<%=d.getPeriodFormated() %>" style="width:6em;" readonly="<%=readOnly %>" 
											onBlur="input_autochange(this,'digitsSpaces');" />
										<%if(readOnly){ %><%=d.getPeriodDimension() %><%}else{ %>
											<select name="dperiodDimension">
											<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
												<option <%if(periodDimension.equals(d.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
											<%} %>
											</select>
										<%} %>
										<span id="compare_supply_depositor<%=key%>_todate">
											по дату <md:calendarium name="dtodate" readonly="<%=readOnly %>" value="<%=Formatter.format(d.getTodate()) %>" id=""/>
										</span>
									</td>
									<td>
										<md:input onBlur="input_autochange(this,'money')" name="d_weight" readonly="<%=readOnly%>" 
										value="<%=Formatter.toMoneyFormat(d.getWeightBD()) %>" styleClass="money"/>
									</td>
									<td>
										<md:DepositorFinStatus readonly="<%=readOnly%>" name="d_DEPOSITOR_FIN_STATUS" value="<%=d.getDepositorFinStatus().getId().toString()%>"/>
									</td>
								</tr>
							</table>
						</td>
						<%
						if (!readOnly) {
						%>
							<td><br /></td>
							<td class="delchk"><input type="checkbox" name="idTablesSupplyDChk" /></td>
						<%
						}
						%>
					</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<%} %>
				<%} catch (Exception e) {	out.println("Ошибка в секции  frame_depositor.jsp:" + e.getMessage());	e.printStackTrace();} %>
	<%-- 	<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>  --%>
	</tbody>
				<%
					if (!readOnly) {
				%>
				<tfoot>
					<tr>
						<td colspan=3>
							<button onmouseover="Tip('Добавить юр.лицо залогодатель')" onmouseout="UnTip()" 
                                onclick="openDialogAddSupply('n','d'); return false;">+ юр. лицо</button>
                            <button onmouseover="Tip('Добавить физ.лицо залогодатель')" onmouseout="UnTip()"  
                                onclick="openDialogAddSupply('y','d'); return false;">+ физ. лицо</button>
						</td>
						<td class="add">
							<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
							onclick="DelTableRow('idTablesSupplyD', 'idTablesSupplyDChk'); return false;" class="del"></button>
						</td>
					</tr>
				</tfoot>
				<%
					}
				%>
			</table>
			<div class="compare-list-removed" id="compare_list_supply-depositor"></div>
			
			<h3>Индивидуальные условия залоговых сделок</h3><br />
 			<table id="tableSupplyD_special_condition" class="add" style="width: 100%;">
                <tbody>
                <% for (OtherCondition otherCondition : otherlist) {
                    if(otherCondition.getSupplyCode()!=null && otherCondition.getSupplyCode().equals("d")){ %>
                    <tr><td id="compare_depositor_condition<%=otherCondition.getId()%>">
					<%if(!readOnly){ %><textarea name="d_special_condition" onchange="fieldChanged(this);"><%} %><%=otherCondition.getBody() %><%if(!readOnly){ %></textarea><%} %>
					</td>
					<%if (!readOnly) {%><td><br /></td><td class="delchk"><input type="checkbox" name="tableSupplyD_special_conditionChk" /></td><%}%>
					</tr>
                <%}} %>
                </tbody>
                <tfoot>
						<tr>
							<td colspan=3 class="compare-list-removed" id="compare_list_depositor_condition"></td>
						</tr>
                <%if (!readOnly) {%>
                    <tr>
                        <td colspan=3 class="add">
                            <button onmouseover="Tip(getToolTip('Добавить условие'))" onmouseout="UnTip()" 
                            onclick='$("#newtableSupplyD_special_conditionTemplate").tmpl().appendTo("#tableSupplyD_special_condition > TBODY");return false' class="add"></button>
                            &nbsp;
                            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
                            onclick="DelTableRow('tableSupplyD_special_condition', 'tableSupplyD_special_conditionChk'); return false;" class="del"></button>
                        </td>
                    </tr>
                <%} %>
                </tfoot>
            </table>
            <script id="newtableSupplyD_special_conditionTemplate" type="text/x-jquery-tmpl">
                <tr><td><textarea name="d_special_condition" onchange="fieldChanged(this);"></textarea></td>
                <td class="delchk"><input type="checkbox" name="tableSupplyD_special_conditionChk" /></td></tr>
            </script>
			
			<div style="display: none;">
			<h3>Дополнительные атрибуты</h3>
			<table id="supply_d_keyvalue" class="regular">
				<thead>
					<tr>
						<th>Наименование атрибута</th>
						<th>Значение атрибута</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<%try{ %>
						<%if(!readOnly){%>
							<tr style="display:none">
								<td><textarea name="d_key" class="nonverified"></textarea></td>
								<td><textarea name="d_value" class="nonverified"></textarea></td>
								<td class="delchk" style="width:auto;">
									<input type="checkbox" name="supply_d_keyvalueChk"/>
								</td>
							</tr>
						<%}
						Iterator it = task.getSupply().getDepositKeyValue().entrySet().iterator();
						while(it.hasNext()) {
							String key = ((Entry<String,String>)it.next()).getKey();
							String value = task.getSupply().getDepositKeyValue().get(key);
							if(key==null)key="";
							if(value==null)value="";
						%>
							<tr>
								<td>
									<%if(!readOnly){%>
										<textarea name="d_key"><%=key%></textarea>
									<%} else {%>
										<span><%=key%></span>
									<%}%>
								</td>
								<td>
									<%if(!readOnly){%>
										<textarea name="d_value"><%=value%></textarea>
									<%} else {%>
										<span><%=value%></span>
									<%}%>
								</td>
								<%
								if (!readOnly) {
								%>
								<td class="delchk" style="width:auto;">
									<input type="checkbox" name="supply_d_keyvalueChk"/>
								</td>
								<%
								}
								%>
							</tr>
						<%
						}
						%>
					<%} catch (Exception e) {	out.println("Ошибка в секции  frame_depositor.jsp:" + e.getMessage());	e.printStackTrace();} %>
				</tbody>
				<%
				if ((!readOnly)) {
				%>
					<tfoot>
						<tr>
							<td colspan=3 class="add">
								<button onmouseover="Tip(getToolTip('Добавить атрибут'))" onmouseout="UnTip()" onclick="AddRowToTable('supply_d_keyvalue'); return false;" class="add"></button>
								<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRow('supply_d_keyvalue', 'supply_d_keyvalueChk'); return false;" class="del"></button>
							</td>
						</tr>
					</tfoot>
				<%
				}
				%>
			</table>
			</div>
	<%} catch (Exception e) {	out.println("Ошибка в секции  frame_depositor.jsp:" + e.getMessage());	e.printStackTrace();} %>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
		displayPrevApprovedDiff();
</script>