<%@page isELIgnored="true" %>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.OtherCondition" %>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	Task task=TaskHelper.findTask(request);
	Long idMdTask = task.getId_task();
	TaskJPA taskJPA = TaskHelper.taskFacade().getTask(task.getId_task());
	String garatttypecode="garatttypecode";
	boolean readOnly = !TaskHelper.isEditMode("R_Гарантии",request);
	String toDate4newSupply = taskJPA.getToDate4newSupplyFormated("g");
String period4newSupply = taskJPA.getPeriod4newSupplyFormated("g");
ArrayList<OtherCondition> otherlist = task.getOtherCondition();
%>
<%if(!readOnly){ %><input type="hidden" name="section_guarantee" value="true"><%} %>
<script id="GarantTemplatePerson" type="text/x-jquery-tmpl">
<a href="PersonInfo.jsp?id=${id}" target=_blank>${name}</a>
<input type="hidden" name="guarantee_contractor" value=''/>
<input type="hidden" name="guarantee_person" value='${id}'/>
</script>
<script id="GarantTemplateOrg" type="text/x-jquery-tmpl">
<a href="/<%=ApplProperties.getwebcontextFWF() %>/clientInfo.html?id=${id}&mdtask=<%=idMdTask%>" target=_blank>${name}</a>
<input type="hidden" name="guarantee_contractor" value='${id}'/>
<input type="hidden" name="guarantee_person" value=''/>
</script>
<script id="newGarantTemplate" type="text/x-jquery-tmpl">
<tr><td style="width: 15%;" id="orgtd${id}"></td>
<td style="width: 85%;">
	<table class="regular invisibleWhite" style="width: 98%;">
		<tr>
			<td>
				<label><input type="radio" value="y" name="guarantee_main${id}">&nbsp;Основное Обеспечение 
			</td>
			<td>
				<input type="radio" value="n" checked="checked" name="guarantee_main${id}">&nbsp;Дополнительное обеспечение
			</td>
			<td>
				<input type="checkbox" value="y" name="g_FullSum${id}" id="g_FullSum${id}" onclick="g_FullSum_change('${id}')">&nbsp;На всю сумму обязательств
			</td>
		</tr>
		<tr>
			<td>
				<label id="g_sum${id}">Сумма гарантии <br />
				<md:input name="guarantee_sum" id="guarantee_sum${id}" onBlur="input_autochange(this,'money')"  readonly="<%=readOnly%>" value="" styleClass="money"/>
				<select name="guarantee_cur" >
				<option value="<%=task.getMain().getCurrency2().getCode() %>"><%=task.getMain().getCurrency2().getCode() %></option>
				<%if(!task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){ %>
				    <option value="RUR">RUR</option>
				<%} %>
				</select>
				</label>
			</td>
			<td>
				<label>Категория обеспечения <br />
				<md:LiquidityLevel readonly="<%=readOnly%>" name="guarantee_LIQUIDITY_LEVEL" value=""/>
				</label>
			</td>
			<td>
				<label>Финансовое состояние гаранта <br />
				    <md:DepositorFinStatus readonly="<%=readOnly%>" name="guarantee_DEPOSITOR_FIN_STATUS" value=""/>
				</label>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<label>Группа обеспечения
				<md:SupplyType code="<%=garatttypecode %>${id}" readonly="<%=readOnly%>" name="guarantee_type" value=""/>
				</label>
			</td>
		</tr>
		<tr>
			<td>
				<label>Степень обеспечения, % <md:inputMoney name="gsupplyvalue" readonly="false" value=""/> </label>
			</td>
			<td colspan="2">	
				<label>Срок гарантии 
				<md:inputInt name="gperiod" value="<%=period4newSupply%>" style="width:6em;" readonly="false" onBlur="input_autochange(this,'digitsSpaces');" />
				<select name="gperiodDimension">
				<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
				    <option <%if(periodDimension.equals(taskJPA.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
				<%} %>
				</select>
				по дату <md:calendarium name="gtodate" readonly="false" value="<%=toDate4newSupply%>" id="${id}b"/></label>
				
				<div style="display: none;"><label>Описание гарантии<br />
				    <textarea name="guarantee_desc"></textarea>
				</label></div>
			</td>
		</tr>
	</table>		
</td>
<td class="delchk"><input type="checkbox" name="idTablesSupplyGarChk" /></td>
</tr>
</script>
	<%try{ %>
			<h3>Гаранты</h3>
			<table id="idTablesSupplyGar" class="add" style="width: 100%;">
				<thead>
					<tr>
						<th style="width:auto">Гарант</th>
						<th>Описание гарантии</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<%try{ %>
				<%for(com.vtb.domain.Guarantee guarantee : task.getSupply().getGuarantee()){
					garatttypecode+="q";
					String idMain = "";
					String accountid = guarantee.getOrg().getAccountid();
					String rating = processor.getRating(accountid).getRating();
					if(guarantee.getPerson().getId()!=null&&guarantee.getPerson().getId().longValue()!=0) 
					    idMain =  guarantee.getPerson().getId().toString();
					else idMain =  guarantee.getOrg().getAccountid();
					String idDate = "g_date"+idMain;
					String onFocus="displayCalendarWrapper('"+idDate+"', '', false); return false;";
					String idSum="guarantee_sum"+idMain;
					String htmlName = "compare_supply_garant"+guarantee.getCode();
					%>
					<tr>
						<td id="<%=htmlName%>" style="width: 15%;">
							<%if(guarantee.getPerson().getId()!=null&&guarantee.getPerson().getId().longValue()!=0){ %>
								<a href="PersonInfo.jsp?id=<%=guarantee.getPerson().getId().toString() %>" 
									target=_blank><%=guarantee.getPerson().getLastName() %></a>
							<%}else{ %>
								<a href="/<%=ApplProperties.getwebcontextFWF() %>/clientInfo.html?id=<%=accountid %>&mdtask=<%=idMdTask%>"
									target=_blank><%=SBeanLocator.singleton().compendium().getEkNameByOrgId(accountid) %></a>
								<% if((rating !=null) && (!rating.equals(""))){ %><span class="rating">Рейтинг Гаранта <%=rating%></span><%} %>
								<%} %>
							<%if(!readOnly){ %><input type="hidden" name="guarantee_contractor" value='<%=accountid%>'/>
								<input type="hidden" name="guarantee_person" value='<%= (guarantee.getPerson().getId()!=null) ? guarantee.getPerson().getId().toString() : ""%>'/>
							<%} %>
						</td>
						<td style="width: 85%;">
							<table class="regular invisibleWhite" style="width: 98%;"> 
								<tr>
									<td>
										<input type="radio" value="y"
										<%=guarantee.isMain()?"checked=\"checked\"":"" %>  
										<%if(readOnly){%>disabled="disabled"<%}%> onclick="fieldChanged();"
										name="guarantee_main<%=idMain%>">Основное Обеспечение
									</td>
									<td>
				                        <input type="radio" value="n"
		                                <%=!guarantee.isMain()?"checked=\"checked\"":"" %>  
		                                <%if(readOnly){%>disabled="disabled"<%}%> onclick="fieldChanged();"
		                                name="guarantee_main<%=idMain%>">Дополнительное обеспечение
									</td>
									<td>	
		                                <input type="checkbox" value="y" <%=guarantee.isFullSum()?"checked=\"checked\"":"" %>  
	                                    <%if(readOnly){%>disabled="disabled"<%}%> 
	                                    name="g_FullSum<%=idMain%>" id="g_FullSum<%=idMain%>" onclick="g_FullSum_change('<%=idMain%>')">
		                                На всю сумму обязательств
		                            </td>
		                        </tr>
		                        <tr>
		                            <td>
										<label id="g_sum<%=idMain%>" style="<%=guarantee.isFullSum()?"display:none":"" %>; clear:both" >
											<div id="<%=htmlName%>_sum">
										Сумма гарантии<br />
										<md:input name="guarantee_sum" id="<%=idSum %>" onBlur="input_autochange(this,'money')"  
												  readonly="<%=readOnly%>" value="<%=Formatter.format(guarantee.getSum()) %>" styleClass="money"/>
										<%if(!readOnly){ %>
										<select name="guarantee_cur" onchange="fieldChanged();">
											<option value="<%=task.getMain().getCurrency2().getCode() %>"
											<%=guarantee.getCurrency().getCode().equalsIgnoreCase(task.getMain().getCurrency2().getCode())?"selected":"" %>><%=task.getMain().getCurrency2().getCode() %></option>
											<%if(!task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){ %>
											<option value="RUR" <%=guarantee.getCurrency().getCode().equalsIgnoreCase("RUR")?"selected":"" %>>RUR</option>
											<%} %>
										</select>
										<%}else{ %>
										<%=guarantee.getCurrency().getCode() %>
										<%} %>
											</div>
										</label>
									</td>
									<td>	
										<div id="<%=htmlName%>_LIQUIDITY_LEVEL">
										<label>Категория обеспечения<br />
											<md:LiquidityLevel readonly="<%=readOnly%>" name="guarantee_LIQUIDITY_LEVEL" value="<%=guarantee.getLiquidityLevel().getId().toString() %>"/>
										</label>
										</div>
									</td>
									<td>
										<div id="<%=htmlName%>_DEPOSITOR_FIN_STATUS">
										<label>Финансовое состояние гаранта<br />
											<md:DepositorFinStatus readonly="<%=readOnly%>" name="guarantee_DEPOSITOR_FIN_STATUS" value="<%=guarantee.getDepositorFinStatus().getId().toString()%>"/>
										</label>
										</div>
									</td>
								</tr>
								<tr>	
									<td colspan="3">
										<div id="<%=htmlName%>_type">
										<label>Группа обеспечения
											<md:SupplyType code="<%=garatttypecode %>" readonly="<%=readOnly%>" name="guarantee_type" value="<%=(guarantee.getOb()==null)?null:guarantee.getOb().getId().toString() %>"/>
											<%
											if(false&&guarantee.getOb()!=null && guarantee.getOb().getId()!=null) {
												String coefId = "garant_CoefTR1_";
												if(guarantee.getPerson().getId()!=null&&guarantee.getPerson().getId().longValue()!=0) coefId =  coefId + guarantee.getPerson().getId().toString();
												else coefId = coefId + accountid;
												%>
												<br/><span id="<%=coefId%>">Коэффициент транзакционного риска по Гарантии <%=Formatter.str(guarantee.getTransRisk())%></span>
											<%} %>
										</label>
										</div>
									</td>	
								</tr>
								<tr>
									<td>
										<div id="<%=htmlName%>_supplyvalue">
										<label>Степень обеспечения, % <md:inputMoney name="gsupplyvalue" readonly="<%=readOnly %>" value="<%=Formatter.format(guarantee.getSupplyvalue()) %>"/> </label>
										</div>
									</td>	
									<td colspan="2">	
										<div id="<%=htmlName%>_period">
										<label>Срок гарантии
				                            <md:inputInt 
				                                    name="gperiod" value="<%=guarantee.getPeriodFormated() %>" style="width:6em;" readonly="<%=readOnly %>" 
				                                    onBlur="input_autochange(this,'digitsSpaces');" />
				                                <%if(readOnly){ %><%=guarantee.getPeriodDimension() %><%}else{ %>
				                                    <select name="gperiodDimension">
				                                    <%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
				                                        <option <%if(periodDimension.equals(guarantee.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
				                                    <%} %>
				                                    </select>
				                                <%} %>
				                                 по дату <md:calendarium name="gtodate" readonly="<%=readOnly %>" value="<%=Formatter.format(guarantee.getTodate()) %>" id=""/>
										</label>
										</div>
										&nbsp;
										<div style="display: none;">
										<div id="<%=htmlName%>_desc">
										<label>Описание гарантии<br />
											<%	if(!readOnly){%>
												<textarea name="guarantee_desc"><%=guarantee.getDescription() %></textarea>
											<%	} else{
												%><span><%=guarantee.getDescription() %></span>
											<%}%>
										</label>
										</div>
										</div>
									</td>
								</tr>
							</table>		
						</td>
						<%
							if (!readOnly) {
						%>
							<td class="delchk"><input type="checkbox" name="idTablesSupplyGarChk" /></td>
						<%
							}
						%>
					</tr>
				<%} %>
				<%} catch (Exception e) {	out.println("Ошибка в секции  frame_garant.jsp:" + e.getMessage());	e.printStackTrace();} %>
				</tbody>
				<%if (!readOnly) {%>
					<tfoot>
						<tr>
							<td colspan="2">
								<button onmouseover="Tip(getToolTip('Добавить юр. гаранта'))" onmouseout="UnTip()"  
								onclick="openDialogAddSupply('n','g'); return false;">+ юр. лицо</button>
								<button onmouseover="Tip(getToolTip('Добавить физ. гаранта'))" onmouseout="UnTip()"  
								onclick="openDialogAddSupply('y','g'); return false;">+ физ. лицо</button>
							</td>
							<td class="add">
								<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelTableRow('idTablesSupplyGar', 'idTablesSupplyGarChk'); return false;" class="del"></button>
							</td>
						</tr>
					</tfoot>
				<%
				}
				%>
			</table>
			
			<div class="compare-list-removed" id="compare_list_supply_garant"></div>
			
			<h3>Индивидуальные условия гарантии</h3><br />
            <table id="tableSupplyG_special_condition" class="add" style="width: 100%;">
                <tbody>
                <% for (OtherCondition otherCondition : otherlist) {
                    if(otherCondition.getSupplyCode()!=null && otherCondition.getSupplyCode().equals("g")){ %>
                    <tr><td id="compare_garant_condition<%=otherCondition.getId()%>">
                    <%if(!readOnly){ %><textarea name="g_special_condition" onchange="fieldChanged(this);"><%} %><%=otherCondition.getBody() %><%if(!readOnly){ %></textarea><%} %>
                    </td>
                    <%if (!readOnly) {%><td class="delchk"><input type="checkbox" name="tableSupplyG_special_conditionChk" /></td><%}%>
                    </tr>
                <%}} %>
                </tbody>
                <tfoot>
					<tr>
						<td class="compare-list-removed" id="compare_list_garant_condition" colspan="2"></td>
					</tr>
                <%if (!readOnly) {%>
                    <tr>
                        <td colspan=3 class="add">
                            <button  onmouseover="Tip(getToolTip('Добавить условие'))" onmouseout="UnTip()"
                            onclick='$("#newtableSupplyG_special_conditionTemplate").tmpl().appendTo("#tableSupplyG_special_condition > TBODY");return false' class="add"></button>
                            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
                            onclick="DelTableRow('tableSupplyG_special_condition', 'tableSupplyG_special_conditionChk'); return false;" class="del"></button>
                        </td>
                    </tr>
                <%} %>
                </tfoot>
            </table>
            <script id="newtableSupplyG_special_conditionTemplate" type="text/x-jquery-tmpl">
                <tr><td><textarea name="g_special_condition" onchange="fieldChanged(this);"></textarea></td>
                <td class="delchk"><input type="checkbox" name="tableSupplyG_special_conditionChk" /></td></tr>
            </script>

	<%} catch (Exception e) {	out.println("Ошибка в секции  frame_garant.jsp:" + e.getMessage());	e.printStackTrace();} %>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
		displayPrevApprovedDiff();
</script>