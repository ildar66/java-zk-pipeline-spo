<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.spo.StopFactor" %>
<%@page import="ru.masterdm.compendium.domain.spo.StopFactorType" %>
<%@page import="com.vtb.domain.TaskStopFactor" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%
	AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	CompendiumSpoActionProcessor compenduim = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	// параметр по типу надо передать
	StopFactorType type = new StopFactorType("d");   
	StopFactorType found = compenduim.findStopFactorType(type);
	List stopFactorList = compenduim.findStopFactorByTypePage(found, 0, 0, " c.stopFactor ").getList();
	boolean readOnlyClient = !TaskHelper.isEditMode("Стоп-факторы ППКЗ",request);
	Task task=TaskHelper.findTask(request);
	ArrayList taskStopFactorList = task.getTaskStopFactor3List();
	String roClient="";
	if (readOnlyClient) roClient="DISABLED";	
	boolean isEmpty = (taskStopFactorList.size()==0);
	if (attrs != null) {
%>

	<tr>
	  <td vAlign=top>
		<% if (stopFactorList.size()!=0) {
		%>
		<%if (!readOnlyClient) { %>
			<input type="hidden" id="Секция_stop_factors_3" name="Секция_stop_factors_3" value="YES" />
		<% } %>
			<table class="pane stop_factors_3" id="section_stop_factors_3">
				<thead onclick="doSection('stop_factors_3')" onselectstart="return false">
					<tr>
						<td <%=(isEmpty)?"class=\"empty\"":"" %>>
							<div>
								<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
								<span>Лист оценки стоп-факторов подразделения подготовки кредитных заявок</span>
							</div>
						</td>
					</tr>
				</thead>
				<tbody style="display:none">
				<%try{ %>
					<tr>
						<td vAlign=top>
							<table class="regular">
								<tbody>
								<%try{ %>
									<tr style="display:none">
										<td>название</td>
										<td><%if(!readOnlyClient){ %>
											<input type="checkbox" name="stopfactors3" 
											value="hidden" checked />
											<%} %>
										</td>
									</tr>
									<!-- берутся из справочника -->
									<% 
									//ищем что нового добавили в справочник
									for (int dictindex=0; dictindex < stopFactorList.size(); dictindex++){
										StopFactor dictStopFactor = (StopFactor)stopFactorList.get(dictindex);
										String checked="";
										for (int taskindex=0;taskindex<taskStopFactorList.size();taskindex++){
										TaskStopFactor tsf=(TaskStopFactor)taskStopFactorList.get(taskindex);
											if(dictStopFactor.getId().equals(tsf.getStopFactor().getId())&&tsf.isFlag())checked = "checked";
										}
										%>
										<tr>
											<td style="width:1em">
												<input type="checkbox" name="stopfactors3" <%=roClient %>
												value="<%=dictStopFactor.getId()%>" <%=checked %>
												onclick="fieldChanged(this)" />
											</td>
											<td><%=dictStopFactor.getDescription() %></td>
										</tr>
									<%} %>
								<%} catch (Exception e) {out.println("Ошибка в секции frame_stopFactors3.jsp:" + e.getMessage());e.printStackTrace();} %>
								</tbody>
							</table>
						</td>
					</tr>
				<%} catch (Exception e) {out.println("Ошибка в секции frame_stopFactors3.jsp:" + e.getMessage());e.printStackTrace();} %>
				</tbody>
			</table>
		<%} %>
	  </td>
	</tr>
<% }%>
