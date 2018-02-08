<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.LimitTree"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="org.uit.director.action.AbstractAction" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="java.util.logging.*"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
Logger LOGGER = Logger.getLogger("frame_sublimit_jsp");
try {
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	LOGGER.info("loading page "+request.getRequestURI());
	boolean readOnly = !TaskHelper.isEditMode("сублимиты",request);
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	Task parenttask = TaskHelper.findTask(request);
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext((HttpServletRequest)request);
	ArrayList sublimits = processor.findTaskByParent(parenttask.getId_task(), false, true);
	if (attrs != null) {
	%>
	<table class="pane sublimit" id="section_Сублимиты">
		<thead onclick="doSection('Сублимиты')" onselectstart="return false">
			<tr>
				<td id="sublimit_header" <%=(sublimits.size()==0)?"class=\"empty\"":"" %>>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Сублимиты</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
			<tr>
				<td vAlign=top>
					<table class="regular" id="idTableSubLimits" name="idTableSubLimits">
						<thead>
							<tr>
								<th>Номер</th>
								<th>Группы компаний</th>
								<th class="contractor">Контрагенты</th>
								<th>Вид лимита</th>
								<th>Сумма</th>
								<th>Срок</th>
								<th>Статус CRM</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
						<%if(!readOnly){ %>
							<tr style="display:none"><!-- пустая строка -->
								<td class="delchk">
									<input type="hidden" name="active_sublimit" value="0" />
									<input type="checkbox" name="idTableSubLimits_chk"  />
								</td>
							</tr>
						<%
						}
						for (int i=0; i<parenttask.getMain().getLimitTreeList().size(); i++) {
							LimitTree element = (LimitTree) parenttask.getMain().getLimitTreeList().get(i);
							// формируем task с требуемыми параметрами. 
							Task task = new Task(Long.parseLong(element.getReferenceId()));
							task.getHeader().setCrmstatus(element.getCrmstatus());
						%>
							<tr><td style="width:10%;">
							<a href="<%=TaskHelper.getSublimitURL(request, readOnly, task)%>" ><%=element.getNumber() %></a></td>
							<td style="width:15%;"><%=Formatter.str(element.getCompaniesGroup())%></td>
							<td style="width:20%;" class="contractor"><%=Formatter.str(element.getOrganization())%></td>
							<td align="center" style="width:15%;"><%=Formatter.str(element.getLimitVid())%></td>
							<td align="right" style="width:15%;"><%=(element.getSum()==null)?"не установлена": Formatter.format(element.getSum()) + " " + element.getCurrency()%></td>
							<% String prd = ""; 
								if ((element.getPeriod() != null) && (element.getPeriod().intValue() != 0)) prd = Formatter.toMoneyFormat(element.getPeriod()) + " дн.";
								else if (element.getValidTo() != null) prd = "до " + Formatter.format(element.getValidTo());
								else prd = ""; // "нет срока"							
							%>
							<td align="center" style="width:15%;"><%=prd%></td>
							<td style="width:10%;"><%=element.getCrmstatus()%></td>
							<%if(!readOnly){ %>
							<td class="delchk">
								<input type="hidden" name="active_sublimit" value="<%=task.getId_task() %>" />
								<input type="checkbox" name="idTableSubLimits_chk"  />
							</td>
							<%} %>
							</tr>
						<%} %>
						</tbody>
						<tfoot>
							<tr>
								<td colspan=8 class="add">
									<%if(!readOnly){ %>
										<button class="add" iconClass="flatScreenIcon" id="b1sublimit" onclick="document.getElementById('sublimit').value = <%=parenttask.getId_task() %>;submitData(false); return false;"></button>
										<input type="hidden" id="sublimit" name="sublimit" value="0">
										<button onclick="DelRow('idTableSubLimits', 'idTableSubLimits_chk'); return false;" class="del"></button>
									<%} %>
								</td>
							</tr>
						</tfoot>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
	<%
	}
} catch (Exception e) {
	LOGGER.severe("ERROR ON frame_sublimit.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
