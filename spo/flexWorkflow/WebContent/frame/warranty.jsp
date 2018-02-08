<%@page isELIgnored="true" %>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.masterdm.compendium.domain.spo.PunitiveMeasure"%>
<%@page import="ru.md.spo.dbobjects.PunitiveMeasureJPA"%>
<%@page import="com.vtb.domain.Fine"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="java.util.Iterator" %>
<%@page import="com.vtb.domain.OtherCondition" %>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=TaskHelper.findTask(request);
	Long idMdTask = task.getId_task();
	TaskJPA taskJPA = TaskHelper.taskFacade().getTask(idMdTask);
String warrantypecode="warrantypecode";
boolean readOnly = !TaskHelper.isEditMode("R_Поручительство",request);
String toDate4newSupply = taskJPA.getToDate4newSupplyFormated("w");
String period4newSupply = taskJPA.getPeriod4newSupplyFormated("w");
ArrayList<OtherCondition> otherlist = task.getOtherCondition();
%>
<%if(!readOnly){ %><input type="hidden" name="section_warranty" value="true"><%} %>
	<%try{ %>
			<h3>Поручители</h3>
			<table id="idTablesWarranty" class="add" style="width: 100%;">
				<thead>
					<tr>
						<th style="width:auto">Поручитель</th>
						<th>Описание поручительства</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<%try{
					int number = 0;
					for(com.vtb.domain.Warranty w : task.getSupply().getWarranty()){
						number++;
						warrantypecode+="q";
						String guid = w.getGuid();
						String accountid = w.getOrg().getAccountid();
						String rating = processor.getRating(accountid).getRating();
						String htmlName = "compare_supply_warranty"+number;
						%>
						<tr>
							<td id="<%=htmlName%>" style="width: 15%;">
								<%if(w.getPerson().getId()!=null&&w.getPerson().getId().longValue()!=0){ %>
								<a href="PersonInfo.jsp?id=<%=w.getPerson().getId().toString() %>" 
								target=_blank><%=w.getPerson().getLastName() %></a>
								<%}else{ %>
								<a href="/<%=ApplProperties.getwebcontextFWF() %>/clientInfo.html?id=<%=accountid %>&mdtask=<%=idMdTask%>"
								target=_blank><%=SBeanLocator.singleton().compendium().getEkNameByOrgId(accountid) %></a>
									<% if((rating != null)&& (!rating.equals(""))) {%>
									<span class="rating">Рейтинг Поручителя <%=rating%></span><%} %>
								<%} %>
								<%if(!readOnly){ %><input type="hidden" name="w_contractor" value='<%=accountid%>'/>
								<input type="hidden" name="w_person" value='<%= (w.getPerson().getId()!=null) ? w.getPerson().getId().toString() : ""%>'/>
								<input type="hidden" name="w_guid" value='<%=guid%>'/>
								<%} %>
							</td>
							<td style="width: 85%;">
								<table class="provisionPledgesSecond" style="width: 100%;">
							    <tr>
							        <th>Основное<br /> Обеспечение</th>
							        <th>Дополнительное<br /> Обеспечение</th>
									<th>На всю сумму<br /> обязательств </th>
							        <th>Предел <br />ответственности</th>
									<th>Финансовое состояние<br /> поручителя</th>
							    </tr>
							    <tr>
							        <td id="<%=htmlName%>_main">
										<input type="radio" value="y"
										<%=w.isMain()?"checked=\"checked\"":"" %>
										<%if(readOnly){%>disabled="disabled"<%}%>  onclick="fieldChanged();" name="w_main<%=guid%>">
									</td>
									<td>
										 <input type="radio" value="n"
										<%=!w.isMain()?"checked=\"checked\"":"" %> onclick="fieldChanged();"
										<%if(readOnly){%>disabled="disabled"<%}%> 
										name="w_main<%=guid%>">
									</td>
									<td id="<%=htmlName%>_fullsum">
										<input type="checkbox" value="y" <%=w.isFullSum()?"checked=\"checked\"":"" %>  
										<%if(readOnly){%>disabled="disabled"<%}%> name="w_FullSum<%=guid%>" id="w_FullSum<%=guid%>" onclick="w_FullSum_change('<%=guid %>')">
									</td>
									<td id="<%=htmlName%>_sum">
										<md:inputMoney name="w_sum" readonly="<%=readOnly%>" value="<%=Formatter.toMoneyFormat(w.getSum()) %>" styleClass="money"
											onBlur="input_autochange(this,'money')" />
										<%if(!readOnly){ %>
											<select name="w_cur" onchange="fieldChanged();">
												<option value="<%=task.getMain().getCurrency2().getCode() %>"
												<%=w.getCurrency().getCode().equalsIgnoreCase(task.getMain().getCurrency2().getCode())?"selected":"" %>><%=task.getMain().getCurrency2().getCode() %></option>
												<%if(!task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){ %>
												<option value="RUR" <%=w.getCurrency().getCode().equalsIgnoreCase("RUR")?"selected":"" %>>RUR</option>
												<%} %>
											</select>
										<%}else{ %>
											<span><%=w.getCurrency().getCode() %></span>
										<%} %>
									</td>
									<td id="<%=htmlName%>_DEPOSITOR_FIN_STATUS">
										<md:DepositorFinStatus readonly="<%=readOnly%>" name="w_DEPOSITOR_FIN_STATUS" value="<%=w.getDepositorFinStatus().getId().toString()%>"/>
									</td>
								</tr>
							</table>
							<table style="width: 100%; margin-top: 5px; margin-bottom: 5px;">
								<tr>
									<td>
										<label>
											<b>Распределение ответственности:</b>
										</label>
											<%Iterator<String> it=com.vtb.domain.Warranty.ResponsibilityValues.keySet().iterator();
											while(it.hasNext()){
												String key = it.next();
												String name = com.vtb.domain.Warranty.ResponsibilityValues.get(key);%>
												<label id="<%=htmlName%>_resp<%=key %>">
												<input type="checkbox" value="<%=key %>" name="w_resp<%=guid%>" onchange="fieldChanged();"
												id="w_resp<%=guid%><%=key %>"
												<%if(readOnly){%>disabled="disabled"<%}%> 
												<%=w.getResponsibility().contains(key)?"checked=\"checked\"":"" %>
												>
												<%=name %></label>&nbsp;
											<%}%>
										<label id="compare_list_supply_warranty<%=number %>_resp" class="compare-list-removed"></label>
									</td>
								</tr>	
							</table>		
							<table class="provisionPledgesSecond" style="width: 100%;">
								<tr>
									<th>Вид поручительства</th>
									<th>Срок поручительства</th>
									<th>Категория обеспечения</th>
									<th>Степень обеспечения</th>
								</tr>
								<tr>
									<td id="<%=htmlName%>_kind">
										<md:WarrantyKindTag name="w_kind" value="<%=w.getKind() %>" readonly="<%=readOnly %>" />
									</td>
									<td id="<%=htmlName%>_period">
										<md:inputInt 
												name="wperiod" value="<%=w.getPeriodFormated() %>" style="width:6em;" readonly="<%=readOnly %>" 
												onBlur="input_autochange(this,'digitsSpaces');" />
											<%if(readOnly){ %><%=w.getPeriodDimension() %><%}else{ %>
												<select name="wperiodDimension">
												<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
													<option <%if(periodDimension.equals(w.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
												<%} %>
												</select>
											<%} %>
										по дату <md:calendarium name="wtodate" readonly="<%=readOnly %>" value="<%=Formatter.format(w.getTodate()) %>" id=""/>
									</td>
									<td id="<%=htmlName%>_LIQUIDITY_LEVEL">
										<md:LiquidityLevel readonly="<%=readOnly%>" name="w_LIQUIDITY_LEVEL" value="<%=w.getLiquidityLevel().getId().toString() %>"/>
									</td>
									<td id="<%=htmlName%>_supplyvalue">
										<md:inputMoney name="wsupplyvalue" readonly="<%=readOnly %>" value="<%=Formatter.format(w.getSupplyvalue()) %>"/>
									</td>
								</tr>
							</table>
							<br />
								<h3>Штрафные санкции к Поручителю</h3>
							<br />
							<table id="testShtrafiTableId<%=guid %>" class="regular add" style="width: 100%; display: block !ipportant; ">
<thead><tr><th style="width: 24%;">Наименование санкции <br />(Тип штрафной санкции)</th><th style="width: 24%;">Величина санкции (неустойки, <br /> штрафа, пени и т.д.), Валюта / %</th>
<th style="width: 24%;">Период оплаты</th><th style="width: 24%;">Увеличивает ставку <br /> по сделке</th><th style="width: 1%"></th></tr></thead>
<tbody>
<%try{
for (Fine fine : w.getFineList()) {
String cl = "PunitiveMeasure"+fine.getId().toString();
String cl2=cl+" text money";
String sumname="fine_value"+guid;
String curname="fine_currency"+guid;
String valPunitiveMeasureId = "valPunitiveMeasure"+fine.getId().toString();
String curPunitiveMeasureId = "curPunitiveMeasure"+fine.getId().toString();
String displayValueText = !fine.getDescription().isEmpty()?"display:inline":"display:inline";
String display = fine.getDescription().isEmpty()?"display:inline":"display:inline";
String fine_period_name = "fine_period"+guid;
String htmlNameFine = "compare_warranty_fine" + fine.getId();
%>
    <tr>
        <td id="<%=htmlNameFine%>">
            <%if(!readOnly){%>
                <textarea id="PunitiveMeasure<%=fine.getId().toString() %>" name="Штрафные санкции<%=guid %>" style="width:98%;" 
                    onkeyup="fieldChanged(this);"><%=fine.getPunitiveMeasure()%></textarea>
                <a href="javascript:;" class="dialogActivator" dialogId="punitiveMeasureWarrantySelectTemplateDiv" onclick="punitiveMeasureSelectTemplate('PunitiveMeasure<%=fine.getId().toString() %>','punitiveMeasureWarrantySelectTemplateDiv');">
                    <img alt="выбрать из шаблона" src="style/dots.png" /></a>
            <%} else {%>
                <span><%=(fine.getPunitiveMeasure()==null)?"":fine.getPunitiveMeasure()%></span>
            <%} %>
       </td>
       <td>
         <div id="<%=htmlNameFine%>_desc" class="compare-elem">
           <%if(readOnly){ %><%=fine.getDescription() %><%}else{ %>
           <textarea id="DescPunitiveMeasure<%=fine.getId().toString() %>" name="fine_value_text<%=guid%>" style="<%=displayValueText %>"><%=fine.getDescription() %></textarea>
           <%} %>
         </div>
           <input id="dPunitiveMeasure<%=fine.getId().toString() %>" name="descPunitiveMeasure<%=guid %>" type="hidden" value="<%=fine.getDescription() %>">
         <div id="<%=htmlNameFine%>_value"  class="compare-elem">
           <md:inputMoney name="<%=sumname %>" styleClass="<%=cl2 %>" style="<%=display %>"
               id="<%=valPunitiveMeasureId %>"
               value="<%=fine.getFormattedValue()%>" readonly="<%=readOnly%>" />
         </div>
         <div id="<%=htmlNameFine%>_curr" class="compare-elem">
           <md:currency readonly="<%=readOnly %>" value="<%=fine.getCurrencyCode() %>" id="<%=curPunitiveMeasureId %>" with_empty_field="true"
               name="<%=curname %>" withoutprocent="false" styleClass="<%=cl %>" style="<%=display %>" with365="true" />
         </div>
           <input type="hidden" name="fine_id_punitive_measure<%=guid %>" value="<%=Formatter.str(fine.getId_punitive_measure())%>" id="idDictPunitiveMeasure<%=fine.getId().toString() %>">
       </td>
       <td id="<%=htmlNameFine%>_period">
           <md:inputInt name="<%=fine_period_name %>" readonly="<%=readOnly%>" value="<%=fine.getFormattedPeriod() %>"/>
           <select name="fine_periodtype<%=guid %>" <%=(readOnly ? "DISABLED" : "") %> onChange="fieldChanged(this)">
               <option value=""></option>
               <option value="workdays" <%if(fine.getPeriontype().equals("workdays")){ %>selected<%} %>>рабочих дней</option>
               <option value="alldays" <%if(fine.getPeriontype().equals("alldays")){ %>selected<%} %>>календарных дней</option>
           </select>
       </td>
       <td id="<%=htmlNameFine%>_rate_enlarge"><input type="checkbox" <%if(fine.isProductRateEnlarge()){ %>checked="checked"<%} %> <%if ((readOnly)) {%>DISABLED<%} %>
       onclick="if(this.checked){$(this).parent().find('input[name=fine_productrate<%=guid %>]').val('y');}else{$(this).parent().find('input[name=fine_productrate<%=guid %>]').val('n');}" />
       <input name="fine_productrate<%=guid %>" type="hidden" value="<%=fine.isProductRateEnlarge()?"y":"n" %>" >
       </td>
        <%if ((!readOnly)) {%><td class="delchk"><input type="checkbox" name="testShtrafiRowChk<%=guid %>"/></td><%}%>
    </tr>
<%}} catch (Exception e) {out.println("Ошибка в секции  warrenty.jsp:" + e.getMessage());e.printStackTrace();} %>
</tbody>
<tfoot>
	<tr>
		<td class="compare-list-removed" id="compare_list_warranty_fine" colspan=5 style="border-top: 1px;"></td>
	</tr>
<%if ((!readOnly)) {%>
    <tr>
        <td class="add" colspan="5" >
            <button onmouseover="Tip(getToolTip('Добавить штрафную санкцию'))" onmouseout="UnTip()" 
            onclick="insertPunitiveMeasureTR('<%=guid %>','punitiveMeasureWarrantySelectTemplateDiv'); dialogHandler(); return false;" class="add"></button>
            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRowWithLast('testShtrafiTableId<%=guid %>', 'testShtrafiRowChk<%=guid %>'); return false;" class="del"></button>
        </td>
    </tr>
<%}%>
</tfoot>
</table>
							
		<table style="width: 100%;">
			<tr>
				<td id="<%=htmlName%>_add">
					Дополнительные обязательства по Поручителю &nbsp;
					<%	if(!readOnly){%><textarea name="w_add" style="width: 99%;" onkeyup="fieldChanged(this)"><%=w.getAdd() %></textarea>
					<%	} else{%><span><%=w.getAdd() %></span><%}%>
				</td>
			</tr>
			<tr>
				<td id="<%=htmlName%>_type">
					<label>Группа обеспечения <br />
						<md:SupplyType code="<%=warrantypecode %>" readonly="<%=readOnly%>" name="w_type" value="<%=(w.getOb()==null)?null:w.getOb().getId().toString() %>"/>
						<%
						if(w.getOb()!=null && w.getOb().getId()!=null&&!w.getOb().getId().equals("0")) {
						try{
							String coefId = "w_CoefTR1_";
							if(w.getPerson().getId()!=null&&w.getPerson().getId().longValue()!=0) coefId =  coefId + w.getPerson().getId().toString();
							else coefId = coefId + accountid;
							%>
						<%}catch(Exception e){} } %>
					</label>
				</td>
			</tr>		
		</table>
			<br />
			<br /><div id="<%=htmlName%>_desc" style="display: none;"><label>Описание поручительства<br />
				<%	if(!readOnly){%><textarea name="w_desc"><%=w.getDescription() %></textarea>
				<%	} else{%><span><%=w.getDescription() %></span><%}%></label></div>
			</td>
			
			<%	if (!readOnly) {%>
			<td>
				<br />
			</td>
				<td class="delchk"><input type="checkbox" name="idTablesWarrantyChk" /></td>
			<%	}%>
		</tr>
	<%} %>
<%} catch (Exception e) {	out.println("Ошибка в секции  frame_Warranty.jsp:" + e.getMessage());	e.printStackTrace();} %>
</tbody>
	<%	if (!readOnly) {%>
		<tfoot>
			<tr>
				
				<td colspan="3">
                             <button onmouseover="Tip(getToolTip('Добавить юр. поручителя'))" onmouseout="UnTip()" 
                             onclick="openDialogAddSupply('n','w'); return false;">+ юр. лицо</button>
                             <button onmouseover="Tip(getToolTip('Добавить физ. поручителя'))" onmouseout="UnTip()"  
                             onclick="openDialogAddSupply('y','w'); return false;">+ физ. лицо</button>
                         </td>

				<td class="add">
					<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelTableRow('idTablesWarranty', 'idTablesWarrantyChk'); return false;" class="del"></button>
				</td>
			</tr>
		</tfoot>
	<%	}%>
</table>

			<div class="compare-list-removed" id="compare_list_supply_warranty"></div>
			
			<h3>Индивидуальные условия поручительства</h3><br />
            <table id="tableSupplyW_special_condition" class="add" style="width: 100%;">
                <tbody>
                <% for (OtherCondition otherCondition : otherlist) {
                    if(otherCondition.getSupplyCode()!=null && otherCondition.getSupplyCode().equals("w")){ %>
                    <tr><td id="compare_warranty_condition<%=otherCondition.getId()%>">
                    <%if(!readOnly){ %><textarea name="w_special_condition" style="width: 650px;" onchange="fieldChanged(this);"><%} %><%=otherCondition.getBody() %><%if(!readOnly){ %></textarea><%} %>
                    </td>
                    <%if (!readOnly) {%><td class="delchk"><input type="checkbox" name="tableSupplyW_special_conditionChk" /></td><%}%>
                    </tr>
                <%}} %>
                </tbody>
                <tfoot>
					<tr>
						<td class="compare-list-removed" id="compare_list_warranty_condition" colspan="3"></td>
					</tr>
                <%if (!readOnly) {%>
                    <tr>
                        <td colspan="3" class="add">
                            <button  onmouseover="Tip(getToolTip('Добавить условие'))" onmouseout="UnTip()"
                            onclick='$("#newtableSupplyW_special_conditionTemplate").tmpl().appendTo("#tableSupplyW_special_condition > TBODY");return false' class="add"></button>
                            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
                            onclick="DelTableRow('tableSupplyW_special_condition', 'tableSupplyW_special_conditionChk'); return false;" class="del"></button>
                        </td>
                    </tr>
                <%} %>
                </tfoot>
            </table>
            <script id="newtableSupplyW_special_conditionTemplate" type="text/x-jquery-tmpl">
                <tr><td><textarea name="w_special_condition" onchange="fieldChanged(this);"></textarea></td>
                <td class="delchk"><input type="checkbox" name="tableSupplyW_special_conditionChk" /></td></tr>
            </script>
	<%} catch (Exception e) {	out.println("Ошибка в секции  frame_Warranty.jsp:" + e.getMessage());	e.printStackTrace();} %>
<div id="punitiveMeasureWarrantySelectTemplateDiv" title="Выбрать санкцию" style="display: none;">
	<ol>
		<%for(PunitiveMeasureJPA pm : taskFacadeLocal.findPunitiveMeasure(PunitiveMeasure.SanctionType.GUARANTORSANCTION.getDescription())){ %>
		    <li>
		        <a href="javascript:;" class="disable-decoration"
		           onclick="onPunitiveMeasureSelectTemplateClick('<%=pm.getName_measure() %>','<%=pm.getSumdesc()%>','<%=pm.getId()%>','<%=pm.getSumFormated()%>','<%=pm.getCurrency()%>')">
		           <%=pm.getName_measure() %>
		        </a>
		    </li>
		<%} %>
	</ol>
</div>

<script id="newWarrantyTemplate" type="text/x-jquery-tmpl">
<tr>
	<td style="width: 15%;" id="orgtd${id}">
	</td>
	<td style="width: 85%;">
		<table class="provisionPledgesSecond" style="width: 100%;">
			<tr>
				<th>Основное<br /> Обеспечение</th>
				<th>Дополнительное<br /> Обеспечение</th>
				<th>На всю сумму<br /> обязательств </th>
				<th>Предел <br />ответственности</th>
				<th>Финансовое состояние <br />поручителя</th>
			</tr>
			<tr>
				<td>	
					<input type="radio" value="y" name="w_main${guid}">
				</td>
				<td>
					<input type="radio" value="n" checked name="w_main${guid}">
				</td>
				<td>
					<input type="checkbox" value="y" name="w_FullSum${guid}" id="w_FullSum${guid}" onclick="w_FullSum_change('${guid}')">
				</td>
				<td>
					<md:inputMoney name="w_sum" readonly="<%=readOnly%>" value="" styleClass="money" onBlur="input_autochange(this,'money')" />
					<select name="w_cur" >
						<option value="<%=task.getMain().getCurrency2().getCode() %>"><%=task.getMain().getCurrency2().getCode() %></option>
						<%if(!task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR")){ %>
						<option value="RUR">RUR</option>
						<%} %>
					</select>
				</td>
				<td>
					<md:DepositorFinStatus readonly="<%=readOnly%>" name="w_DEPOSITOR_FIN_STATUS" value=""/>
				</td>
			</tr>
		</table>
		<table style="width: 100%; margin-top: 5px; margin-bottom: 5px;">
			<tr>
				<td>
					<label>
						<b>Распределение ответственности:</b>
					</label>
					<%Iterator<String> it=com.vtb.domain.Warranty.ResponsibilityValues.keySet().iterator();
					while(it.hasNext()){
						String key = it.next();
						String name = com.vtb.domain.Warranty.ResponsibilityValues.get(key);%>
						<input type="checkbox" value="<%=key %>" name="w_resp${guid}"
						id="w_resp${guid}<%=key %>"
						>
						<%=name %> &nbsp;
					<%}%>
				</td>
			</tr>	
		</table>		
		<table class="provisionPledgesSecond" style="width: 100%;">
			<tr>
				<th>Вид поручительства</th>
				<th>Срок поручительства</th>
				<th>Категория обеспечения</th>
				<th>Степень обеспечения</th>
			</tr>
			<tr>
				<td>
					<md:WarrantyKindTag name="w_kind" value="" readonly="<%=readOnly %>" />
				</td>
				<td>
					<md:inputInt name="wperiod" value="<%=period4newSupply%>" style="width:6em;" readonly="false" onBlur="input_autochange(this,'digitsSpaces');" />
					<select name="wperiodDimension">
					<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
						<option <%if(periodDimension.equals(taskJPA.getPeriodDimension())){ %> selected<%} %>><%=periodDimension %></option>
					<%} %>
					</select>
					по дату <md:calendarium name="wtodate" readonly="false" value="<%=toDate4newSupply%>" id="${guid}t"/></label>
				</td>
				<td>
					<md:LiquidityLevel readonly="<%=readOnly%>" name="w_LIQUIDITY_LEVEL" value=""/>
				</td>
				<td>
					<md:inputMoney name="wsupplyvalue" readonly="false" value=""/>
				</td>
			</tr>
		</table>
		<br />
			<h3>Штрафные санкции к Поручителю</h3>
		<br />
		<table id="testShtrafiTableId${guid}" class="regular" style="width: 100%;">
			<thead><tr><th style="width: 24%;">Наименование санкции <br />(Тип штрафной санкции)</th><th style="width: 24%;">Величина санкции (неустойки, <br /> штрафа, пени и т.д.), Валюта / %</th>
<th style="width: 24%;">Период оплаты</th><th style="width: 24%;">Увеличивает ставку <br /> по сделке</th><th style="width: 1%"></th></tr></thead>
			<tbody>
			</tbody>
			<tfoot>
				<tr>
					<td class="add" colspan="5" style="border-top: 1px;">
						<button onmouseover="Tip(getToolTip('Добавить штрафную санкцию'))" onmouseout="UnTip()" 
						onclick="insertPunitiveMeasureTR('${guid}','punitiveMeasureWarrantySelectTemplateDiv'); dialogHandler(); return false;" class="add"></button>
						<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRowWithLast('testShtrafiTableId${guid}', 'testShtrafiRowChk${guid}'); return false;" class="del"></button>
					</td>
				</tr>
			</tfoot>
		</table>
		<table style="width: 100%;">
			<tr>
				<td>
					Дополнительные обязательства по Поручителю &nbsp;
					<textarea name="w_add" style="width: 99%;" onkeyup="fieldChanged(this)"></textarea>
				</td>
			</tr>
			<tr>
				<td>				
					Группа обеспечения <br />
					<md:SupplyType code="${guid}" readonly="<%=readOnly%>" name="w_type" value=""/>
					
				</td>
			</tr>		
		</table>
		<br />
		<br /><div style="display: none;"><label>Описание поручительства<br />
			<textarea name="w_desc"></textarea>
	</td>
	<td style="width: 20px;"></td>
	<td class="delchk"><input type="checkbox" name="idTablesWarrantyChk" /></td>			
</tr>	                       
</script>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
		displayPrevApprovedDiff();
</script>
