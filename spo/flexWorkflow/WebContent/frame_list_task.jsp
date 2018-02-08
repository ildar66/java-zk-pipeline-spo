<!DOCTYPE HTML>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.masterdm.spo.utils.Formatter" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%@page isELIgnored="true" %>

<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");%>
<%
Map<String, String> sectionMap = new LinkedHashMap<String, String>();
sectionMap.put("accept","Операции в работе");
sectionMap.put("perform","Назначенные мне");
sectionMap.put("starred","Избранные");
sectionMap.put("project_team","Работа проектной команды");
Long userID = TaskHelper.getCurrentUser(request).getIdUser();
%>
		<div id="tasklist_tabs" ng-controller="ListController as listCntr" >
			<a href="javascript:;" onclick="show_hide_task_list_onclick()"><img src="style/images/hide_tasklist.png" id="hide_task_list_btn" style="position: absolute; left:650px;top:2px;z-index: 1;"></a>
			<ul>
				<% for(String section : sectionMap.keySet()){ %>
				<li><a href="#tabs-<%=section%>" ng-click="listCntr.openTaskTab('<%=section%>')"><%=sectionMap.get(section) %></a><span id="tabs-<%=section%>-loading" class="tabs-loading" style="display:none;"><img src="style/images/loading.gif"></span></li>
				<%} %>
			</ul>
			<% for(String section : sectionMap.keySet()){
			boolean allMode = section.equals("starred") || section.equals("project_team");%>
			<div id="tabs-<%=section%>" class="tabs_section">
				<div style="text-align: center;">
					<a class="button" href="javascript:;" ng-click="listCntr.toPage(0,'<%=section%>')" ng-show="tasks_pagenum_hash['<%=section%>'] > 1">|&lt;&lt;</a>
				<a class="button" href="javascript:;" ng-click="listCntr.toPage(tasks_pagenum_hash['<%=section%>']-1,'<%=section%>')" ng-show="tasks_pagenum_hash['<%=section%>'] > 0">&larr;</a>
				Всего {{tasks_hash['<%=section%>'].count}} заявок, страница {{tasks_pagenum_hash['<%=section%>'] + 1}} из {{tasks_hash['<%=section%>'].pageCount}}.
				<a class="button" href="javascript:;" ng-click="listCntr.toPage(tasks_pagenum_hash['<%=section%>']+1,'<%=section%>')" ng-show="tasks_pagenum_hash['<%=section%>']+1 < tasks_hash['<%=section%>'].pageCount">&rarr;</a>
				<a class="button" href="javascript:;" ng-click="listCntr.toPage(tasks_hash['<%=section%>'].pageCount-1,'<%=section%>')" ng-show="tasks_pagenum_hash['<%=section%>']+2 < tasks_hash['<%=section%>'].pageCount">&gt;&gt;|</a>
					<span style="padding-left:5em"><input id="search<%=section%>" style="width:280px">
						<button type="submit" onclick="return false" ng-click="listCntr.toPage(0,'<%=section%>')">Найти</button>
						<button type="submit" onclick="$('#search<%=section%>').val('');return false" ng-click="listCntr.toPage(0,'<%=section%>')">Очистить</button>
					</span>
				</div>
				<table class="regular taskLineList">
					<thead>
					<tr>
						<th width="120px"><a href="#" onClick="showHideSearch('<%=section%>'); return false;">поиск</a></th>
						<th title="Номер заявки" width="40px">№</th>
						<th width="30px" title="Версия заявки">Вер.</th>
						<th width="220px">Контрагент <input id="fastsearchContractor<%=section%>" style="width:70px"> <img src="theme/img/view.png" valign="middle" title="Найти"
						    onclick='$("#fullSearchForm<%=section%>").show();return openDialog("popup_org.jsp?ek=only&filter="+$("#fastsearchContractor<%=section%>").val()+"&nameId=selectedName<%=section%>&contractorid=searchContractor<%=section%>", "organizationLookupList", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");'>
							<img src="theme/img/delete.gif" valign="middle" title="Очистить" onclick="$('#fastsearchContractor<%=section%>').val('')"></th>
						<th width="80px">Сумма</th>
						<th width="20px" title="Валюта">Вал.</th>
						<th width="40px" title="Тип заявки">Тип</th>
						<th width="220px">Статус</th>
						<%if(!allMode){%><th width="220px">Текущая операция</th><%}%>
						<%--<th width="40px">Инициирующее подразделение</th>--%>
						<th width="180px">Тип процесса</th>
					</tr>
					<tr id="fullSearchForm<%=section%>" class="search_row" style="display:none">
						<td><button type="submit" onclick="return false" ng-click="listCntr.toPage(0,'<%=section%>')">Найти</button>
							<button type="submit" onclick="clearSearchFilter('<%=section%>');return false"
									ng-click="listCntr.toPage(0,'<%=section%>')">Очистить</button>
						</td>
						<td><input id="searchNumber<%=section%>" type="text"></td>
						<td></td>
						<td><input id="searchContractor<%=section%>" type="hidden">
							<input onclick='return openDialog("popup_org.jsp?ek=only&nameId=selectedName<%=section%>&contractorid=searchContractor<%=section%>", "organizationLookupList", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");' type="text" class="text" readonly="true" id="selectedName<%=section%>" value="не выбрано" /></td>
						<td class="sum_filter_with_header"><div class="sum_filter_with_header"><span class="sum_filter_header">от</span><input id="searchSumFrom<%=section%>" class="searchSum" type="text"></div><div class="sum_filter_with_header"><span class="sum_filter_header">до</span><input id="searchSumTo<%=section%>" class="searchSum" type="text"></div></td>
						<td><select id="searchCurrency<%=section%>">
							<option value="all"></option>
							<% for(String currency : TaskHelper.dict().findCurrencyList()){ %>
							<option value="<%=currency %>"><%=currency %></option><%} %>
						    </select></td>
						<td><input id="searchType<%=section%>" type="text"></td>
						<td><input id="searchStatus<%=section%>" type="text"></td>
						<%if(!allMode){%><td><input id="searchCurrOperation<%=section%>" type="text"></td><%}%>
						<td><select id="searchProcessType<%=section%>">
							<option value="all"></option>
							<%for(ru.md.pup.dbobjects.ProcessTypeJPA processType : TaskHelper.pup().findProcessTypeList()){ %>
							<option value="<%=processType.getIdTypeProcess().toString() %>"><%=Formatter.cut(processType.getDescriptionProcess(), 25) %></option>
							<%} %>
						</select></td>
					</tr>
					</thead>
					<tbody>
				        <tr ng-repeat="task in tasks_hash['<%=section%>'].taskLineList">
							<td>
								<%if(section.equals("accept")){%><a ng-show="task.idTask != '<%=request.getParameter("id")%>'" href="javascript:;" ng-click="listCntr.refuseTask({{task.idTask}})"><img src="style/not_take.png" alt="отказаться"></a><%}%>
								<%if(section.equals("perform")){%><a onclick="takeTask('{{task.idTask}}')" href="javascript:;" title="Начать работу — откроется заявка для редактирования"><img src="style/take.png" alt="взять"></a><%}%>
								<a target="_blank" href="rprt.do?rp=as&id={{task.idProcess}}&mdtaskId={{task.idMDTask}}" title="Посмотреть активные операции"
										><img src="style/in_progress.png" alt="Активные операции"></a>
								<a href="rprt.do?rp=hr&p={{task.idProcess}}" target="_blank"
								   title="Посмотреть хронологию выполнения операций по этой заявке"><img src="style/time.png" alt="хронология"></a>
								<a target="_blank" href="rprt.do?rp=sh&id={{task.idProcess}}" title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"></a>
							<%if(!allMode){%>
								<img src="style/fav.png"   alt="избранная заявка" mdTaskId="{{task.idMDTask}}" favorite="1" ng-show="task.favorite" />
								<img src="style/unfav.png" alt="избранная заявка" mdTaskId="{{task.idMDTask}}" favorite="0" ng-hide="task.favorite" />
							<%}else{%>
								<img ng-show="task.favorite" mdTaskId="{{task.idMDTask}}" userId="<%=userID%>" favorite="1" src="style/fav.png"   style="cursor: pointer" class="favorite" onclick='$.post("ajax/favoriteSwitcher.html", {mdTaskId: $(this).attr("mdTaskId"), userId: $(this).attr("userId")}, imgSwitcher);' />
								<img ng-hide="task.favorite" mdTaskId="{{task.idMDTask}}" userId="<%=userID%>" favorite="0" src="style/unfav.png" style="cursor: pointer" class="favorite" onclick='$.post("ajax/favoriteSwitcher.html", {mdTaskId: $(this).attr("mdTaskId"), userId: $(this).attr("userId")}, imgSwitcher);' />
							<%}%>
							</td>
							<%if(!allMode){%><td><a href="javascript:;" onclick="otherTask('{{task.idTask}}')">{{task.numberZ}}</a></td><%}else{%>
							<td><a href="javascript:;" onclick="otherMdTask('{{task.idMDTask}}')">{{task.numberZ}}</a></td>
							<%}%>
							<td>{{task.version}}</td>
							<td title="{{task.contractors}}" class="dotted">{{task.contractors | limitTo: 37}}</td><td align="right">{{task.sum}}</td><td>{{task.currency}}</td>
							<td>{{task.processType}}</td><td title="{{task.status}}" class="dotted">{{task.status | limitTo: 35}}</td>
							<%if(!allMode){%><td title="{{task.nameStageTo}}" class="dotted">{{task.nameStageTo | limitTo: 35}}</td><%}%>
							<%--<td>{{task.department | limitTo: 35}}</td>--%>
							<td title="{{task.descriptionProcess}}" class="dotted">{{task.descriptionProcess | limitTo: 25}}</td>
						</tr>
					</tbody>
				</table>
			</div>
			<%} %>
		</div>
