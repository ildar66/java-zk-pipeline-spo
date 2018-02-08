<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="com.vtb.domain.DepartmentAgreement" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<br />
<%
try {
	boolean readOnly = !TaskHelper.isEditMode("Справка согласования",request);
	Task task=TaskHelper.findTask(request);
	%>
					<table class="regular add" id="idDepAgreement">
						<thead>
							<th style="width:20%;">Наименование Экспертного подразделения</th>
							<th style="width:35%;">Отклоненные замечания и предложения</th>
							<th style="width:41%;">Комментарий (мотивы отклонения)</th>
							<th />
						</thead>
						<tbody>
							<%
							if (!readOnly) {
							%>
								<!-- прототип новой строки -->
								<tr style="display:none;">
									<td style="width:20%">
										<textarea name="Согласование подразделение" class="nonverified"></textarea>
									</td>
									<td style="width:35%">
										<textarea name="Согласование Замечания" class="nonverified"></textarea>
									</td>
									<td style="width:41%">
										<textarea name="Согласование Комментарий" class="nonverified"></textarea>
									</td>
									<td class="delchk">
										<input type="checkbox" name="idDepAgreementChk"/>
									</td>
								</tr>
								<!-- КОНЕЦ прототипа новой строки -->
							<%
							}
							for (int j=0; j<task.getDepartmentAgreements().size(); j++) {
							DepartmentAgreement da = (DepartmentAgreement)task.getDepartmentAgreements().get(j);
							%>
							<tr>
								<td style="width:20%">
									<%if(!readOnly){%>
										<textarea name="Согласование подразделение" <%if(readOnly){ %>readonly<%}%>><%=da.getDepartment() %></textarea>
									<%} else {%>
										<%=da.getDepartment() %>
									<%}%>
								</td>
								<td style="width:35%">
									<%if(!readOnly){%>
										<textarea name="Согласование Замечания" <%if(readOnly){ %>readonly<%}%>><%=da.getRemark() %></textarea>
									<%} else {%>
										<%=da.getRemark() %>
									<%}%>
								</td>
								<td style="width:41%">
									<%if(!readOnly){%>
										<textarea name="Согласование Комментарий" <%if(readOnly){ %>readonly<%}%>><%=da.getComment() %></textarea>
									<%} else {%>
										<%=da.getComment() %>
									<%}%>
								</td>
								<%
								if (!readOnly) {
								%>
									<td class="delchk">
										<input type="checkbox" name="idDepAgreementChk"/>
									</td>
								<%
								}
								%>
							</tr>
							<%
							}
							%>
						</tbody>
						<%
						if (!readOnly) {
						%>
							<tfoot>
								<tr>
									<td class="add" colspan=4>
										<button onmouseover="Tip(getToolTip('Добавить подразделение'))" onmouseout="UnTip()" onclick="AddRowToTable('idDepAgreement'); return false;" class="add"/></button>
										<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRow('idDepAgreement', 'idDepAgreementChk'); return false;" class="del"/></button>
									</td>
								</tr>
							</tfoot>
						<%
						}
						%>
					</table>

	<%
} catch (Exception e) {
	out.println("ERROR ON frame_departmentAgreement.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
