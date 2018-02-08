<%@page isELIgnored="true" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.md.helper.TaskHelper" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
    TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
    ru.md.spo.dbobjects.TaskJPA task=taskFacade.getTask(Long.valueOf(request.getParameter("mdtaskid")));
	boolean readOnly = !TaskHelper.isEditMode("R_Обеспечение",request);
%>
<%if(!readOnly){ %><input type="hidden" name="show_section_promissory_note" value="true"><%} %>
<script id="newPromissoryNoteTemplate" type="text/x-jquery-tmpl"> 
<tr>
	<td style="width: 70%; text-align: center;">
	<textarea rows="9" cols="15" name="promissory_note_holder" style="margin-left: 5px;" onkeyup="fieldChanged(this)"></textarea>
	</td>
	<td style="width: 29%;">
	<div style="width: 300px; margin: 5px; margin-bottom: 10px;">
	<label>Номинал (вексельная сумма) векселя Банка<br />
	<md:input name="promissory_note_val" readonly="false" value="" 
	styleClass="money" onBlur="input_autochange(this,'money')" /><br />
	</label><br />
	<label>Валюта векселя<br />
	<md:currency readonly="<%=readOnly %>" value="" 
	id="currency_promissory_note" name="currency_promissory_note" withoutprocent="true" /><br />
	</label><br />
	<label>Процентная оговорка по векселю Банка<br /><br />
	<md:input name="promissory_note_per" readonly="<%=readOnly%>" 
	value="" styleClass="money" onBlur="input_autochange(this,'money')" /><br />
	</label><br />
	<label>Срок платежа по векселю Банка<br />
	<md:input name="promissory_note_date" value="" readonly="<%=readOnly %>" 
	styleClass="text date" id="promissory_note_date${nextid}" addition="<%=dateAddition%>" 
	onFocus="displayCalendarWrapper('promissory_note_date${nextid}', '', false); return false;" 
	onChange="input_autochange(this,'date');" /><br />
	</label><br />                            
	<label>Место платежа по векселю Банка<br /><br />
	<textarea name="promissory_note_place" style="margin-bottom: 5px;"></textarea><br />
	</label><br />
	<div>
	</td>
	<td class="delchk" style="width: 1%;"><input type="checkbox" name="promissory_noteChk" /></td>
</tr>
</script>
	<%try{ %>
			<h3>Вексель</h3>
			<table id="idTable_promissory_note" class="add"  style="width: 100%;">
				<thead>
					<tr>
						<th style="width: 70%;">Векселедержатель векселя Банка</th>
						<th style="width: 29%;">Описание</th>
						<th style="width: 1%;"></th>
					</tr>
				</thead>
				<tbody>
				<%try{ %>
				<%for(ru.md.spo.dbobjects.PromissoryNoteJPA pn : task.getPromissoryNotes()){
					String htmlName = "compare_supply_promissory"+pn.getId();
				%>
					<tr>
						<td id="<%=htmlName%>" style="width:auto">
							<%if(readOnly){ %><%=pn.getHolder() %><%} else { %>
    							<textarea rows="9" cols="15" onkeyup="fieldChanged(this)" name="promissory_note_holder" style="margin-left: 5px;"><%=pn.getHolder() %></textarea>
							<%} %>
						</td>
						<td>
							<div style="width: 300px; margin: 5px; margin-bottom: 10px;">
								<div id="<%=htmlName%>_val">
		                            <label>Номинал (вексельная сумма) векселя Банка<br />
		                                <md:input name="promissory_note_val" readonly="<%=readOnly%>" 
		                                value="<%=Formatter.toMoneyFormat(pn.getVal())%>" 
		                                styleClass="money" onBlur="input_autochange(this,'money')" /><br />
		                            </label>
								</div>
		                            <br />
								<div id="<%=htmlName%>_currency">
		                            <label>Валюта векселя<br />
		                                <md:currency readonly="<%=readOnly %>" value="<%=pn.getCurrency() %>" 
		                                id="currency_promissory_note" name="currency_promissory_note" withoutprocent="true" /><br />
		                            </label>
								</div>
		                            <br />
								<div id="<%=htmlName%>_per">
		                            <label>Процентная оговорка по векселю Банка<br />
		                                <md:input name="promissory_note_per" readonly="<%=readOnly%>" 
		                                value="<%=Formatter.toMoneyFormat(pn.getPerc())%>" 
		                                styleClass="money" onBlur="input_autochange(this,'money')" /><br />
		                            </label>
								</div>
								<br />
								<div id="<%=htmlName%>_date">
		                            <label>Срок платежа по векселю Банка<br />
		                                <md:input name="promissory_note_date" value="<%=Formatter.str(pn.getMaxdate()) %>" readonly="<%=readOnly %>" 
		                                    styleClass="text date" id="promissory_note_date<%=pn.getId().toString() %>" addition="<%=dateAddition%>" 
		                                    onFocus="displayCalendarWrapper('promissory_note_date<%=pn.getId().toString() %>', '', false); return false;" 
		                                    onChange="input_autochange(this,'date');" />
		                            </label>
								</div>
		                            <br />
								<div id="<%=htmlName%>_place">
									<label>Место платежа по векселю Банка<br />
									<%if(readOnly){ %><span><%=pn.getPlace() %></span>
									<%}else{ %><textarea name="promissory_note_place" onkeyup="fieldChanged();" style="margin-bottom: 5px;"><%=pn.getPlace() %></textarea><%} %>
									</label>
								</div>
								<br />
							</div>	
						</td>
						<%
						if (!readOnly) {
						%>
							<td class="delchk"><input type="checkbox" name="promissory_noteChk" /></td>
						<%
						}
						%>
					</tr>
				<%} %>
				<%} catch (Exception e) {	out.println("Ошибка в секции  promissory_note.jsp:" + e.getMessage());	e.printStackTrace();} %>
				</tbody>
				<tfoot>
					<tr>
						<td class="compare-list-removed" id="compare_list_supply_promissory" colspan=3 ></td>
					</tr>
				<%
					if (!readOnly) {
				%>
					<tr>
						<td colspan=3 class="add">
							<button  
							onclick="AddPromissoryNote(); return false;" class="add"></button>
							<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
							onclick="DelTableRow('idTable_promissory_note', 'promissory_noteChk'); return false;" class="del"></button>
						</td>
					</tr>
				<%}%>
				</tfoot>
			</table>
	<%} catch (Exception e) {	out.println("Ошибка в секции  promissory_note.jsp:" + e.getMessage());	e.printStackTrace();} %>
