<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@page import="ru.md.spo.dbobjects.ProductGroupJPA"%>
<%@page isELIgnored="true" %>
<%@page import="ru.md.spo.dbobjects.OperDecisionJPA"%>
<%@page import="ru.md.spo.dbobjects.IndConditionJPA"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.spo.OperationDecision"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.TaskCurrency" %>
<%@page import="ru.md.domain.OtherGoal" %>
<%@page import="com.vtb.domain.Forbidden" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");%>
<%
	response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");
    try {
		String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
		Task task=TaskHelper.findTask(request);
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
		
		boolean readOnly = !TaskHelper.isEditMode("Основные параметры",request);
		String ro = (readOnly) ? "DISABLED": "";

		CompendiumSpoActionProcessor compenduim = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
		ArrayList<OtherGoal> otherGoals = task.getMain().getOtherGoals();
		ArrayList<Forbidden> forbiddens = task.getMain().getForbiddens();

    	OperationDecision[] vocOperationDecisionList = compenduim.findOperationDecisionList("%","c.name");
%>
<script>
    $(function() {
        $( "#limitparam_tabs" ).tabs();
        jQuery("textarea[class*=expand]").TextAreaExpander();
		restoreTab('limitparam_tabs');
    });
</script>
<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>
<div id="limitparam_tabs">
    <ul>
        <li><a href="#limitparam_tab1" onclick="storeTab('limitparam_tabs',0)">Основные параметры</a></li>
        <li><a href="#limitparam_tab2" onclick="storeTab('limitparam_tabs',1)">Группы видов сделок</a></li>
        <li><a href="#limitparam_tab3" onclick="storeTab('limitparam_tabs',2)">Целевое назначение</a></li>
        <li><a href="#limitparam_tab4" onclick="storeTab('limitparam_tabs',3)">Порядок принятия решения о провед. опер. в рамках субл.</a></li>
    </ul>
    <div id="limitparam_tab1">
    <table class="regular leftPadd">
					<tr id="limitType">
						<th style="width: 50%;">Вид Лимита/Сублимита</th>
						<td id="compare_limitparam_limittype" style="width: 50%;">
						<% String idLimitType = (task.getHeader().getIdLimitType() == null) ? "-1" : task.getHeader().getIdLimitType().toString();%>
						<%if (!readOnly) {%><input type="hidden" id="Секция_основные параметры" name="Секция_основные параметры" value="YES" />
						<%}else{%><input type="hidden" id="limitTypeHidden" value="<%=idLimitType %>" /><%} %>
							<md:LimitType name="Вид лимита" readonly="<%=readOnly %>" value="<%=idLimitType%>" style="width:30em" 
								onChange="onChangeLimitType();" id="limitType"/>
						</td>
					</tr>
					<tr>
						<th>Возобновляемый Лимит/Сублимит</th>
						<td id="compare_limitparam_renewable">
						<input type="checkbox" <%if (task.getMain().isRenewable()) {%>checked="checked"<%}%> 
							<%if (readOnly || (!task.getMain().isMayBeRenewable())) {%>disabled="disabled"<%}%>
							onclick="fieldChanged(this);if(this.checked){document.getElementById('main_renewable').value='y';} else {document.getElementById('main_renewable').value='n';}" >
						<input type="hidden" id="main_renewable" name="main Возобновляемый Лимит" 
							value="<%=task.getMain().isRenewable() ? "y" : "n"%>" >
						</td>
					</tr>
					<tr>
						<th>Категория Сделок - Проектное финансирование</th>
						<td id="compare_param_project_fin">
							<input type="checkbox" id="main_projectFin_chk" name="main_projectFin_chk" <%if (task.getMain().isProjectFin()) {%>checked="checked"<%}%> 
								<%if (readOnly) {%>disabled="disabled"<%}%>
								onclick="fieldChanged(this);if(this.checked){document.getElementById('main_projectFin').value='y';} else {document.getElementById('main_projectFin').value='n';};" >
							<input type="hidden" id="main_projectFin" name="main Категория Сделок - Проектное финансирование" 
								value="<%=task.getMain().isProjectFin() ? "y" : "n"%>" >
						</td>
					</tr>
					<tr>
						<th>Категория качества ссуды</th>
						<td id="compare_param_qualitycategory"><span id="quality_category_span">не ниже: &nbsp;
							<md:Quality_category name="Категория качества ссуды" style="width:4em;font-size:0.75em;" 
								value="<%=task.getGeneralCondition().getQuality_category() %>" readonly="<%=readOnly %>" /></span>
						</td>
					</tr>
				</table>

				<table class="regular leftPadd">
					<tr>
						<th style="width: 50%;">Cумма Лимита/Сублимита</th>
						<td vAlign=top style="clear:both;overflow:hidden; width: 50%;" id="compare_param_sum">
							<span style="white-space:nowrap;">
								<md:inputMoney id="limit_sum" name="Сумма лимита" styleClass="text money"
									value="<%=task.getMain().getFormattedSum()%>" readonly="<%=readOnly%>" 
									onBlur="input_autochange(this,'money')"
								/>
									<md:currency id="currency_Sum" readonly="<%=readOnly %>" value="<%=task.getMain().getCurrency2().getCode() %>" 
										name="Валюта" withoutprocent="true" 
										onChange="if(this.options[this.selectedIndex].value!='RUR'){showHideExchangeDivRate(true);}else{showHideExchangeDivRate(false);}" />
							</span>
						</td>
					</tr>
					<tr>
						<th>Контроль целевого <br /> использования</th>
						<td> 
							<%if(!readOnly){%>
                   				 <textarea name="targetTypeComment" style="width: 99%;" class="expand50-200"><%=(task.getMain().getTargetTypeComment()==null)?"":task.getMain().getTargetTypeComment()%></textarea>
               			    <%} else {%>
                            <span><%=(task.getMain().getTargetTypeComment()==null)?"":task.getMain().getTargetTypeComment()%></span>
                            <%}%>
						</td>
					</tr>
					<tr>
						<th>Валюта, в которой могут проводиться сделки</th> 
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
					<%
					    if(task.isLimit()){
					%>
						<tr>
							<th>Перераспределение остатков между Сублимитами</th>
							<td id="compare_limitparam_redistrib_residues">
								<input type="checkbox" <%if (task.getMain().isRedistribResidues()) {%>checked="checked"<%}%> 
									<%if (readOnly) {%>disabled="disabled"<%}%>
									onclick="fieldChanged(this);if(this.checked){document.getElementById('main_redistribResidues').value='y';} else {document.getElementById('main_redistribResidues').value='n';}" >
								<input type="hidden" id="main_redistribResidues" name="main Перераспределение остатков между Сублимитами" 
									value="<%=task.getMain().isRedistribResidues() ? "y" : "n"%>" >
							</td>
						</tr>
					<%
					    }
					%>
					<%
					    if (task.isSubLimit()) {
					%>
						<tr style="display: none;">
							<th>Дополнительная информация по сумме</th>
							<td>
								<%
								    if (readOnly) {
								%>
									<span><%=task.getMain().getExtraSumInfo() == null ? ""
                                : task.getMain().getExtraSumInfo()%></span>
								<%
								    } else {
								%>
									<textarea name="Дополнительная информация по сумме" onkeyup="fieldChanged(this)"><%=task.getMain().getExtraSumInfo() == null ? ""
                                : task.getMain().getExtraSumInfo()%></textarea>
								<%
								    }
								%>
							</td>
						</tr>
					<%
					    }
					%>
					<%if(task.isLimit()){ %>
					<tr>
						<th>Срок заключения сделок</th>
						<td id="compare_limitparam_mdtask_date" vAlign=top style="clear:both;overflow:hidden;">
							<md:input id="mdtask_date" name="Дата действия лимита" value="<%=Formatter.str(task.getMain().getValidto())%>" 
								addition="<%=dateAddition%>" readonly="<%=readOnly %>"
								onFocus="displayCalendarWrapper('mdtask_date', '', false); return false;" styleClass="text date" 
								onBlur="input_autochange(this,'date')" />
						</td>
					</tr>
					<%}else{ %>
					<tr>
						<th>Срок сделок до</th>
						<td vAlign=top style="clear:both;overflow:hidden;" id="compare_param_period">
							<md:input id="mdtask_perion" name="Срок действия лимита" value="<%=Formatter.str(task.getMain().getPeriod())%>" 
							readonly="<%=readOnly %>" onkeyup="fieldChanged(this)"/>
							<%if(readOnly){ %><%=task.getMain().getPeriodDimension() %><%}else{ %>
                                    <select name="periodDimension">
                                    <%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
                                        <option <%if(periodDimension.equals(task.getMain().getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
                                    <%} %>
                                    </select>
                                <%} %>
						</td>
					</tr>
					<%} %>
				</table>
						<table class="regular leftPadd">
							<tr><th colspan="3" style="text-align: center !important;" >Сроки сделок</th></tr>
							<tr><th style="width: 35%;">Группа вида сделки</th><th style="width: 15%;">до</th><th style="width: 50%;">Комментарий по сроку сделки</th></tr>
						<%for(ProductGroupJPA pg : taskJPA.getProductGroupList()){
						    String productTypePeriodName = "productTypePeriod"+pg.getId();%>
							<tr>
								<td style="width:30%;" id="compare_limitparam_prodperiod<%=pg.getId() %>"><%=(pg.getName()==null)?"": pg.getName()%></td>
								<td style="width:10%;" id="compare_limitparam_prodperiod<%=pg.getId() %>_period">
									<md:inputInt name="<%=productTypePeriodName %>" styleClass="text days" readonly="<%=readOnly%>"   
									    value="<%=Formatter.format(pg.getPeriod()) %>" onChange="fieldChanged(this)"/> дн.
								</td>
								<td style="width:60%;" id="compare_limitparam_prodperiod<%=pg.getId() %>_cmnt">
									<%if(readOnly){ %>
										<span><%=pg.getCmnt() %></span>
									<%}else{ %>
										<textarea name="productTypeCmnt<%=pg.getId() %>" 
										onkeyup="fieldChanged(this)"><%=pg.getCmnt() %></textarea>
									<%} %>
									<img src="" style="width:30em; height:0">
								</td>
							</tr>
						<%	}%>
						<tr>
							<td colspan="3" id="compare_list_limitparam_prodperiod" class="compare-list-removed" style="display: none;"></td>
						</tr>
						</table>
				
				
				<table class="regular">
					<tr style="display: none;">
						<th>Описание категории качества<!--[ОСТАВИТЬ ПОЛЕ? Уточнить!!!]--></th>
						<td>
							<%if(readOnly){ %>
								<span><%=task.getGeneralCondition().getQuality_category_desc()==null?"":task.getGeneralCondition().getQuality_category_desc() %></span>
							<%}else{ %>
								<textarea name="Описание категории качества" onkeyup="fieldChanged(this)"><%=task.getGeneralCondition().getQuality_category_desc()==null?"":task.getGeneralCondition().getQuality_category_desc() %></textarea>
							<%} %>
						</td>
					</tr>
					
					<%if(taskJPA.isDocumentary() && taskJPA.isSublimit()){ %>
					<tr>
                        <th style="width: 50%;">Источник формирования покрытия для осуществления платежа по аккредитиву</th>
                        <td style="width: 50%;">
                        <%if(!readOnly){ %><a href="javascript:;" onclick="$('#acredetivSourcePaymentSelectTemplateDiv').jqmShow();fieldChanged();"><%} %>
                            <span id="acredetiv_source_name"><%=taskJPA.getAcredetivSourcePaymentName() %></span>
                        <%if(!readOnly){ %></a><input type="hidden" name="acredetiv_source" id="acredetiv_source" 
                        value="<%=taskJPA.getAcredetivSourcePayment()==null?"":taskJPA.getAcredetivSourcePayment().getId().toString()%>"><%} %>
					    </td>
					</tr>
					<%} %>
					<tr style="display: none;">
						<th>Индивидуальные условия</th>
						<td>
						<table id="main_indConditionsTableId" class="regular">
								<tbody>
									<%for (IndConditionJPA cond : taskJPA.getIndConditions()) {%>
										<tr>
											<td>
												<%if(!readOnly){%>
													<textarea name="main_indConditions" style="width:98%;"
														onkeyup="fieldChanged(this)"><%=cond.getCondition()%></textarea>
												<%} else {%>
													<span><%=cond.getCondition()%></span>
												<%}%>
											</td>
											<%if (!readOnly) {  %><td class="delchk"><input type="checkbox" name="main_indConditionsChk"/></td><%}%>
										</tr>
									<%}%>
								</tbody>
								<%if ((!readOnly)) {%>
									<tfoot>
										<tr>
											<td colspan=2 class="add">
												<button onclick="AddIndCondition(); return false;" class="add"></button>
												<button onclick="DelRowWithLast('main_indConditionsTableId', 'main_indConditionsChk'); return false;" class="del" ></button>
											</td>
										</tr>
									</tfoot>
								<%}%>
							</table>
						</td>
					</tr>
				</table>
    </div>
    <div id="limitparam_tab2">
    
						    <div id="select_product_type" title="выбрать группу видов сделок" style="display: none">
								<ul>
									<%for(com.vtb.domain.ProductGroup pg : TaskHelper.dict().getProductGroupList()){ 
									     String cl = "allProductType ";
									     for(Long ltype : pg.getLimitTypes()){//список видов лимита
										    cl +="productType4lt"+ltype+" ";
										 }%>
									     
									     <li class="<%=cl%>">
									         <a href="javascript:;" class="disable-decoration" onclick="$('#'+$('#target_type_id').val()).val('<%=pg.getName() %>');">
									             <%=pg.getName() %>
									         </a>
									     </li>
									<%} %>
								</ul>
							</div>
							<script id="productGroupTemplate" type="text/x-jquery-tmpl">
<tr><td><textarea name="productGroup" id="productGroup${id}"></textarea>
<a href="javascript:;" class="dialogActivator" dialogId="select_product_type" 
onclick="$('#target_type_id').val('productGroup${id}');">
<img alt="выбрать из шаблона" src="style/dots.png"></a>
</td><td class="delchk"><input type="checkbox" name="productGroupTableChk"/></td></tr>
							</script>

						    <table id="productGroupTable" class="regular">
                                <tbody>
						    <%for(ProductGroupJPA pg : taskJPA.getProductGroupList()){ %>
						        <tr><td id="compare_limitparam_prodgroup<%=pg.getId()%>" >
						         <%  if (!readOnly) { %>
                                     <textarea name="productGroup" id="productGroup<%=pg.getId()%>"><%=(pg.getName()==null)?"": pg.getName()%></textarea>
                                     <a href="javascript:;" class="dialogActivator" dialogId="select_product_type" 
                                         onclick="$('#target_type_id').val('productGroup<%=pg.getId()%>');">
									   	 <img alt="выбрать из шаблона" src="style/dots.png"></a>
									   	 <input type="hidden" name="productGroupId" value="<%=pg.getId()%>">
                                 <% } else { %>
                                     <span><%=pg.getName()%></span>
                                 <% } %>
                                 </td>
                                 <% if (!readOnly) { %>
                                            <td class="delchk">
                                                <input type="checkbox" name="productGroupTableChk"/>
                                            </td>
                                            <% } %>
                                 </tr>
						    <%} %>
						    </tbody>
						    <tfoot>
										<tr><td class="compare-list-removed" id="compare_list_limitparam_prodgroup" style="display: none;" colspan=2></td></tr>
										<%
											if ((!readOnly)) {
										%>
                                    <tr>
                                        <td colspan=2 class="add" >
                                            <button onclick='$( "#productGroupTemplate" ).tmpl({id:getNextId()}).appendTo( "#productGroupTable > TBODY" ); dialogHandler(); return false;' class="add"></button>
                                            <button onclick="DelRowWithLast('productGroupTable', 'productGroupTableChk'); return false;" class="del" ></button>
                                   </td>
                                </tr>
                            <%}%>
                 </tfoot>
						    </table>
						
    </div>
    <div id="limitparam_tab3">
    <table id="main_otherGoalsId" class="regular">
                                <tbody>
                                    <%
                                        for (int j = 0; j < otherGoals.size(); j++) {
                                            OtherGoal otherGoal = otherGoals.get(j);
                                    %>
                                        <tr>
                                            <td id="compare_param_othergoal_<%=j %>">
                                                <%  if (!readOnly) { %>
                                                    <textarea name="main Иные цели" id="target<%=j%>"><%=otherGoal.getGoal()%></textarea>
                                                    <a href="javascript:;" class="dialogActivator" dialogId="select_target_type"
                                                    onclick="$('#target_type_id').val('target<%=j%>');">
									   					<img alt="выбрать из шаблона" src="style/dots.png"></a>
									   				<input type="hidden" name="main Иные цели id" value="<%=otherGoal.getIdTarget()%>" id="target<%=j%>id" />	
									   				<input type="hidden" name="main Иные цели условие" value="<%=otherGoal.getCrmTargetTypeId()%>" id="target<%=j%>cond" />
                                                <% } else { %>
                                                    <span><%=otherGoal.getGoal()%></span>
                                                <% } %>
                                            </td>
                                            <% if (!readOnly) { %>
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
                                    if ((!readOnly)) {
                                %>
                                        <tr>
                                            <td colspan=2 class="add">
                                                <button onclick='$( "#newTargetTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_otherGoalsId > TBODY" ); dialogHandler(); return false;' class="add"></button>
                                                <button onclick="DelRowWithLast('main_otherGoalsId', 'main_otherGoalsIdChk'); return false;" class="del" ></button>
                                            </td>
                                        </tr>
                                <%
                                    }
                                %>
                                </tfoot>
                            </table>
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
					<%  if (!readOnly) { %>
					<textarea class="expand50-200" name="main Forbiddens" id="illegal_target<%=j%>"><%=forbidden.getGoal()%></textarea>
					<a href="javascript:;" class="dialogActivator" dialogId="select_illegal_target_type"
					   onclick="$('#illegal_target_type_id').val('illegal_target<%=j%>');">
						<img alt="выбрать из шаблона" src="style/dots.png"></a>					
					<% } else { %>
					<span><%=forbidden.getGoal()%></span>
					<% } %>
				</td>
				<% if (!readOnly) { %>
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
				if ((!readOnly)) {
			%>
			<tr>
				<td colspan=2 class="add">
					<button onclick='$( "#newIllegalTargetTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_forbiddensId > TBODY" ); dialogHandler(); jQuery("textarea[class*=expand]").TextAreaExpander(); return false;' class="add"></button>
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
    <div id="limitparam_tab4">
        <table id="main_operationDecisionTableId" class="regular" style="width: 100%;">
								<tbody>
									<% int operDecisionCount = 0;
										for (OperDecisionJPA od : taskJPA.getOperDecision()) {%>
										<tr><td id="compare_limitparam_operdecision<%=operDecisionCount%>">
											<input type="hidden" name="main_operationDecisionId" value="<%=od.getId()%>">
										    <table class="regular" style="width: 100%">
										    <tr><th>Решения о/об:</th></tr>
										    <tr><td>
										        <table class="regular" style="width: 100%" id="operationDecisionDesc<%=od.getId()%>">
										            <%for(ru.md.spo.dbobjects.OperDecisionDescriptionJPA desc : od.getDescriptions()) {%>
										            <tr><td id="compare_limitparam_operdecision<%=operDecisionCount%>_descr_<%=desc.getId() %>">
												<%if(!readOnly){%>
													<textarea id="OperationDecision<%=desc.getId() %>" name="main_operationDecisionList_desc<%=od.getId()%>" style="width:98%;"
														onkeyup="fieldChanged(this)" cols="40" class="expand50-200"
														><%=Formatter.str(desc.getDescr())%></textarea>
													<a href="javascript:;"
														onclick="fieldChanged(this);operationDecisionSelectTemplate('OperationDecision<%=desc.getId() %>');">
														<img alt="выбрать из шаблона" src="style/dots.png">
													</a>
												<%} else {%>
													<span><%=Formatter.str(desc.getDescr())%></span>
												<%}%>
										            </td>
										            <%if (!readOnly) {  %><td class="delchk"><input type="checkbox" name="operationDecisionDesc<%=od.getId()%>Chk"/></td><%}%>
										            </tr>
										            <%} %>
										            <tfoot>
															<tr>
																<td colspan=2 id="compare_list_limitparam_operdecision<%=operDecisionCount%>_descr" class="compare-list-removed"></td>
															</tr>
										            <%if (!readOnly) {  %>
														<tr>
															<td colspan=2 class="add">
																<button onclick="fieldChanged(this);$( '#OperationDecisionDescTemplate' ).tmpl({id:getNextId(),od:<%=od.getId()%>}).appendTo( '#operationDecisionDesc<%=od.getId()%>' ); return false;" class="add"></button>
																<button onclick="fieldChanged(this);DelRowWithLast('operationDecisionDesc<%=od.getId()%>', 'operationDecisionDesc<%=od.getId()%>Chk'); return false;" class="del" ></button>
															</td>
														</tr>
													<%}%>
														</tfoot>
										        </table>
										    </td></tr>
										    <tr><th>принимаются:</th></tr>
										    <tr><td id="compare_limitparam_operdecision<%=operDecisionCount%>_accepted">
										        <%if(!readOnly){%>
													<textarea name="main_operationDecisionList_accepted" style="width:98%;"
														onkeyup="fieldChanged(this)" cols="40" rows="5"><%=Formatter.str(od.getAccepted())%></textarea>
												<%} else {%>
													<span><%=od.getAccepted()%></span>
												<%}%>
										    </td></tr>
										    <tr><th>особенности принятия решений:</th></tr>
										    <tr><td id="compare_limitparam_operdecision<%=operDecisionCount%>_specials">
										        <%if(!readOnly){%>
													<textarea name="main_operationDecisionList_specials" style="width:98%;"
														onkeyup="fieldChanged(this)" cols="40" rows="5"><%=Formatter.str(od.getSpecials())%></textarea>
												<%} else {%>
													<span><%=od.getSpecials()%></span>
												<%}%>
										    </td></tr>
										    </table>
										</td>
										<%if (!readOnly) {  %>
												<td class="delchk">
													<input type="checkbox" name="main_operationDecisionIdChk"/>
												</td>
										<%}%>
									    </tr>
									<%operDecisionCount=operDecisionCount+1;
										}%>
								</tbody>
								<tfoot>
									<tr>
										<td colspan=3 id="compare_list_limitparam_operdecision" class="compare-list-removed"></td>
									</tr>
								<%
								if ((!readOnly)) {
								%>
										<tr>
											<td colspan=3 class="add">
												<button onclick="fieldChanged(this);AddRowToTableOperationDecision(); return false;" class="add"></button>
												<button onclick="fieldChanged(this);DelRowWithLast('main_operationDecisionTableId', 'main_operationDecisionIdChk'); return false;" class="del" ></button>
											</td>
										</tr>
								<%
								}
								%>
								</tfoot>
							</table>
    </div>
</div>

	<input type="hidden" id="operationDecisionTemplate" value="">
	<script language="javascript">
		/* show/hide exchangeratediv if found */ 		
		function showHideExchangeDivRate(show) {
			try {
				if (show) document.getElementById('exchangeratediv').style.display = ''; 					
				else document.getElementById('exchangeratediv').style.display = 'none';
			} catch (Err) {} 
		} 
		$().ready(function() {
            $('#operationDecisionSelectTemplateDiv').jqm();
            $('#acredetivSourcePaymentSelectTemplateDiv').jqm();
			with_sublimitOnClick();
			loadCompareResult('parameters');
        });
	</script>
	
<div id="operationDecisionSelectTemplateDiv" class="jqmWindow" style="height:75%;overflow:auto">
<%for (OperationDecision od : vocOperationDecisionList) {%>
	<a href="javascript:;" class="jqmClose"
	onclick="$('#'+$('#operationDecisionTemplate').val()).val('<%=od.getName()%>')">
	<%=od.getName()%></a><br /><br />
<%}%>
<hr>
<a href="#" class="jqmClose">Закрыть</a>
</div>
	<input type="hidden" id="taskId_param" value="<%=task.getId_task()%>" />
				
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
<script id="OperationDecisionDescTemplate" type="text/x-jquery-tmpl">
<tr><td><textarea id="OperationDecision${id}" name="main_operationDecisionList_desc${od}" style="width:98%;"
onkeyup="fieldChanged(this)" cols="40" class="expand50-200"></textarea>
<a href="javascript:;" onclick="fieldChanged(this);operationDecisionSelectTemplate('OperationDecision${id}');">
<img alt="выбрать из шаблона" src="style/dots.png"></a></td>
<td class="delchk"><input type="checkbox" name="operationDecisionDesc${od}Chk"/></td></tr>
</script>

<script id="OperationDecisionTemplate" type="text/x-jquery-tmpl">
<tr><td><input type="hidden" name="main_operationDecisionId" value="${id}">
<table class="regular" style="width: 99%">
<tr><th>Решения о/об:</th></tr>
<tr><td>
<table class="regular" style="width: 99%" id="operationDecisionDesc${id}">
<tfoot><tr><td colspan=2 class="add">
<button onclick="fieldChanged(this);$( '#OperationDecisionDescTemplate' ).tmpl({id:getNextId(),od:'${id}'}).appendTo( '#operationDecisionDesc${id}' ); return false;" class="add"></button>
<button onclick="fieldChanged(this);DelRowWithLast('operationDecisionDesc${id}', 'operationDecisionDesc${id}Chk'); return false;" class="del" ></button>
</td></tr></tfoot></table></td></tr>
<tr><th>принимаются:</th></tr><tr><td>
<textarea name="main_operationDecisionList_accepted" style="width:98%;"
onkeyup="fieldChanged(this)" cols="40" rows="5"></textarea>
</td></tr>
<tr><th>особенности принятия решений:</th></tr>
<tr><td>
<textarea name="main_operationDecisionList_specials" style="width:98%;"
onkeyup="fieldChanged(this)" cols="40" rows="5"></textarea>
</td></tr>
</table>
</td>
<td class="delchk"><input type="checkbox" name="main_operationDecisionIdChk"/></td>
</tr>
</script>
<script id="indConditionTemplate" type="text/x-jquery-tmpl">
<tr><td>
<textarea name="main_indConditions" style="width:98%;" onkeyup="fieldChanged(this)"></textarea>
</td><td class="delchk"><input type="checkbox" name="main_indConditionsChk"/></td></tr>
</script>
	<%
	    } catch (Exception e) {
	        out.println("ERROR ON frame_limitParam.jsp:" + e.getMessage());
	        e.printStackTrace();
	    }
	%>
