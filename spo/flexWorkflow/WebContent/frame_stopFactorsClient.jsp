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
	CompendiumSpoActionProcessor compenduim = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	// параметр по типу надо передать
	StopFactorType type = new StopFactorType("c");   
	StopFactorType found = compenduim.findStopFactorType(type);
	List stopFactorList = compenduim.findStopFactorByTypePage(found, 0, 0, " c.stopFactor ").getList();
	boolean readOnlyClient = !TaskHelper.isEditMode("Стоп-факторы Клиент",request);
	Task task=TaskHelper.findTask(request);
	ArrayList taskClientStopFactorList = task.getTaskClientStopFactorList();
	String roClient="";
	if (readOnlyClient) roClient="DISABLED";	
%>
		<% if (stopFactorList.size()!=0) {
		%>
		<%if (!readOnlyClient) { %>
			<input type="hidden" id="Секция_stop_factors_Client" name="Секция_stop_factors_Client" value="YES" />
		<% } %>
		<h3>Лист оценки стоп-факторов клиентского менеджера</h3>
				<%try{ %>
							<table class="regular" style="Width: 99%;">
								<tbody>
								<%try{ %>
									<tr style="display:none">
										<td>название</td>
										<td><%if(!readOnlyClient){ %>
											<input type="checkbox" name="stopfactorsClient" 
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
										if(dictStopFactor.getDescription()==null){continue;}
										for (int taskindex=0;taskindex<taskClientStopFactorList.size();taskindex++){
										TaskStopFactor tsf=(TaskStopFactor)taskClientStopFactorList.get(taskindex);
											if(dictStopFactor.getId().equals(tsf.getStopFactor().getId())&&tsf.isFlag())checked = "checked";
										}
										%>
										<tr>
											<td style="width:1em">
												<input type="checkbox" name="stopfactorsClient" <%=roClient %>
												value="<%=dictStopFactor.getId()%>" <%=checked %>
												onclick="fieldChanged(this)" />
											</td>
											<td><%=dictStopFactor.getDescription() %></td>
										</tr>
									<%} %>
								<%} catch (Exception e) {out.println("Ошибка в секции frame_stopFactorsClient.jsp:" + e.getMessage());e.printStackTrace();} %>
								</tbody>
							</table>
				<%} catch (Exception e) {out.println("Ошибка в секции frame_stopFactorsClient.jsp:" + e.getMessage());e.printStackTrace();} %>
		<%} %>
