<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.SpecialCondition" %>
<%@page import="java.util.ArrayList" %>
<%
Task task=TaskHelper.findTask(request);
boolean editmode=TaskHelper.isEditMode("Особые условия",request);
ArrayList<SpecialCondition> specialConditionList=task.getSpecialCondition();
 %>
<table class="pane condition" id="section_Специальные условия">
		<thead onclick="doSection('Специальные условия')" onselectstart="return false">
			<tr>
				<td <%=(specialConditionList.size()==0)?"class=\"empty\"":"" %>>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Особые условия сделки, не указанные в решении Уполномоченного органа</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<table id="idSpecialCondition" class="regular add">
						<%
						if (editmode) {
						%>
							<tfoot>
								<tr>
									<td colspan="3" class="add">
										<button onclick="AddRowToTable('idSpecialCondition'); return false;" class="add"></button>
										
										<button onclick="DelRow('idSpecialCondition', 'idSpecialCondition_chk'); return false;" class="del"></button>
									</td>
								</tr>
							</tfoot>
						<%
						}
						%>
						<thead>
							<tr>
								<th>Формулировка условия</th>
								<th>Тип условия</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<%if(editmode){ %>
								<tr style="display:none;">
									<td><textarea name="SpecialConditionBody" class="nonverified" style="width:48%;" onkeyup="fieldChanged(this)"></textarea></td>
									<td>
										<select name="SpecialConditionType" class="nonverified" onchange="fieldChanged(this)">
											<option selected value=""></option>
											<option value="Является Отлагательным условием заключения Сделки">Является Отлагательным условием заключения Сделки</option>
											<option value="Является Отлагательным условием использования">Является Отлагательным условием использования</option>
											<option value="Является Дополнительным условием">Является Дополнительным условием</option>
											<option value="Иное">Иное</option>
										</select>
									</td>
									<td class="delchk">
										<input type="checkbox" name="idSpecialCondition_chk"/>
									</td>
								</tr>
							<%}for(SpecialCondition sc : specialConditionList){%>
								<tr>
									<%if (editmode) { %>
									<td><textarea name="SpecialConditionBody" style="width:98%;" onkeyup="fieldChanged(this)"><%=sc.getBody() %></textarea></td>
									<td>
									    <select name="SpecialConditionType" onchange="fieldChanged(this)">
									    	<option <%=sc.getType()!=null&&sc.getType().equals("")?"selected":""%> value=""></option>
											<option <%=sc.getType()!=null&&sc.getType().equals("Является Отлагательным условием заключения Сделки")?"selected":""%> value="Является Отлагательным условием заключения Сделки">Является Отлагательным условием заключения Сделки</option>
											<option <%=sc.getType()!=null&&sc.getType().equals("Является Отлагательным условием использования")?"selected":""%> value="Является Отлагательным условием использования">Является Отлагательным условием использования</option>
											<option <%=sc.getType()!=null&&sc.getType().equals("Является Дополнительным условием")?"selected":""%> value="Является Дополнительным условием">Является Дополнительным условием</option>
											<option <%=sc.getType()!=null&&sc.getType().equals("Иное")?"selected":""%> value="Иное">Иное</option>
										</select>
									</td>
									<td class="delchk">
										<input type="checkbox" name="idSpecialCondition_chk"/>
									</td>
									<%}else{%>
										<td><%=sc.getBody() %></td>
										<td><%=sc.getType() %></td>
									<%}%>
								</tr>
							<%} %>
						</tbody>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
