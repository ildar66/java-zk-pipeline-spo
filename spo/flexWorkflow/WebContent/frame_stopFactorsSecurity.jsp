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
	StopFactorType typeS = new StopFactorType("s");   
	StopFactorType foundS = compenduim.findStopFactorType(typeS);
	List stopSFactorList = compenduim.findStopFactorByTypePage(foundS, 0, 0, " c.stopFactor ").getList();
	Task taskS=TaskHelper.findTask(request);
	boolean readOnlySecurity = !TaskHelper.isEditMode("Стоп-факторы Безопасность",request);
	ArrayList taskSecurityStopFactorList = taskS.getTaskSecurityStopFactorList();
	String roSecurity="";
	if (readOnlySecurity) roSecurity="DISABLED";
%>
		<% if (stopSFactorList.size()!=0) {
		%>
		<%if (!readOnlySecurity) { %>
			<input type="hidden" id="Секция_stop_factors_Security" name="Секция_stop_factors_Security" value="YES" />
		<% } %>
		    <h3>Лист оценки стоп-факторов безопасности</h3>
				<%try{ %>
							<table class="regular" style="Width: 99%;">
								<tbody>
								<%try{ %>
									<tr style="display:none">
										<td>название</td>
										<td><%if(!readOnlySecurity){ %>
												<input type="checkbox" name="stopfactorsSecurity" 
												value="hidden" checked />
											<%} %>
										</td>
									</tr>
									<!-- берутся из справочника -->
									<% 
									//ищем что нового добавили в справочник
									for (int dictindex=0; dictindex < stopSFactorList.size(); dictindex++){
										StopFactor dictStopFactor = (StopFactor)stopSFactorList.get(dictindex);
										if(dictStopFactor.getDescription()==null){continue;}
										String checked="";
										for (int taskindex=0;taskindex<taskSecurityStopFactorList.size();taskindex++){
										TaskStopFactor tsf=(TaskStopFactor)taskSecurityStopFactorList.get(taskindex);
											if(dictStopFactor.getId().equals(tsf.getStopFactor().getId())&&tsf.isFlag())checked = "checked";
										}
										%>
										<tr>
											<td style="width:1em">
												<input type="checkbox" name="stopfactorsSecurity" <%=roSecurity %>
												value="<%=dictStopFactor.getId()%>" <%=checked %>
												onclick="fieldChanged(this)" />
											</td>
											<td><%=dictStopFactor.getDescription() %></td>
										</tr>
									<%} %>
								<%}catch(Exception e) {out.println("ERROR ON frame_stopFactorsSecurity.jsp:" + e.getMessage());e.printStackTrace();} %>
								</tbody>
							</table>
				<%}catch(Exception e) {out.println("ERROR ON frame_stopFactorsSecurity.jsp:" + e.getMessage());e.printStackTrace();} %>
		<%} %>

