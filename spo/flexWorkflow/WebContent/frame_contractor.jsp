<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page isELIgnored="true" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.util.Formatter"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.domain.ContractorType" %>
<%@ page import="ru.md.domain.TaskKz" %>
<%@ page import="ru.md.domain.Org" %>
<%@ page import="java.util.*" %>
<%@ page import="org.uit.director.tasks.TaskInfo" %>
<%response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");
%>
<!--Выводит информацио о контрагентах-->
<!--Эта страница развивалась долго эволюционно. Требования менялись часто. Поэтому код превратился в спагетти-->
<!--Нам очень важна производительность страницы-->
<script language="javascript">
window.document.body.onunload = onUnloadCheck;
dialogArray = new Array();	//дочерние окна
function onUnloadCheck()	{
	//закрываем дочерние окна:
	if(dialogArray != null){
		for(var i = 0; i < dialogArray.length; i++){
			if(dialogArray[i]) {
				try {
					dialogArray[i].close();
				} catch (err) {
				}
			}
		}
	}
}

function openDialog(hrefStr, name, prop){
	var wnd = window.open(hrefStr, name, prop);
	dialogArray[dialogArray.length]=wnd;
	wnd.focus();
	return false;
}

$(function() {
	updateOrg();
	$.cookie("org_search_param_inn","");
	$.cookie("org_search_param_group","");
	$.cookie("org_search_param_name","");
	$.cookie("org_search_param_number","");
});
$(function() {
	updateOrg();
});
$(document).ready(function() {
	loadCompareResult('contractors');
});
</script>
<%
//диалог "справочник организаций":
	boolean readOnly = !TaskHelper.isEditMode("Сведения о контрагентах",request);
	boolean changeMainOrgEnable =TaskHelper.taskFacade().getGlobalSetting("changeMainOrgEnable").equalsIgnoreCase("true");
	MdTask mdtask = TaskHelper.getMdTask(request);
	List<ru.md.domain.ContractorType> contractorTypeList = SBeanLocator.singleton().compendium().findContractorTypeList();
	List<TaskKz> kzList = SBeanLocator.singleton().compendium().getTaskKzByMdtask(mdtask.getIdMdtask());
	Set<String> groupNames = new HashSet<String>();
%>
	<table id="section_Контрагенты" style="width: 100%;">
		<tbody>
		<%try{ %>
			<tr>
				<td vAlign=top>
					<table class="regular" id="idTableContractor" name="idTableContractor">
						<thead>
							<tr>
								<th style="width: 150px;">Тип Заемщика</th>
								<th>Наименование Заемщика</th>
								<th>Класс Заемщика</th>
								<th>Отрасль экономики СРР</th>
								<th>Регион СРР</th>
								<th>Рейтинг <br /> кредитного подразделения</th>
								<th>Рейтинг <br /> подразделения рисков</th>
								<th>Утверждённый <br /> рейтинг</th>
								<th>Рейтинг ПКР</th>
								<th>Прикр. док.</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
						<%if(mdtask.isPipelineProcess() && mdtask.getProjectName()!=null){%>
						<tr>
							<td></td><td><md:input name="mdtaskprojectName" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectName()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectClass" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectClass()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectIndustry" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectIndustry()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectRegion" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectRegion()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectRating1" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectRating1()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectRating2" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectRating2()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectRating3" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectRating3()) %>" styleClass="text" style="width:85%" /></td>
							<td><md:input name="projectRating4" readonly="<%=readOnly %>" value="<%=Formatter.str(mdtask.getProjectRating4()) %>" styleClass="text" style="width:85%" /></td>
							<td></td><td></td>
						</tr>
						<%} %>
	<!--вывод таблицы для контрагентов-->
							<%
							for (TaskKz kz : kzList) {
								String accountid=kz.getKzid();
								String accountname = SBeanLocator.singleton().getDictService().getEkNameByOrgId(accountid);
								if(accountname == null)
									try{accountname = SBeanLocator.singleton().compendium().getOrgById(accountid).getName();}
									catch (Exception e){}
								String contractorTypeName = "contractorType" + accountid;
								String ekid = TaskHelper.dict().getOrg(accountid).getIdUnitedClient()==null?accountid:TaskHelper.dict().getOrg(accountid).getIdUnitedClient();
								Org ek = SBeanLocator.singleton().compendium().getEkById(ekid);
								if(ek!= null && ek.getGroupname()!=null && !ek.getGroupname().isEmpty())
									groupNames.add(ek.getGroupname());
								%>
							<tr class="org <%=(kz.isMainOrg())?"main":"" %>" id="<%=accountid%>">
							  <% try{ %>
								<td style="width:5em;white-space:nowrap" id="ClientType<%=accountid%>">
									<%if(kz.isMainOrg() && !(mdtask.isPipelineProcess() && mdtask.getProjectName()!=null) ){
										%>
									    <input type="hidden" id="mainOrgID" value="<%=accountid%>">
									    <input type="hidden" id="ekID" value="<%=ekid%>">
									    <%if(TaskHelper.taskFacade().getMainBorrowerChangeLog(mdtask.getIdMdtask()).size()>0){ %>
										<input type="checkbox" checked="checked" disabled="disabled"> <a href="javascript:;" onclick="openMainBorrowerChangeReport();return false;">Основной заемщик</a><br />
									    <%}else{ %>
										<input type="checkbox" checked="checked" disabled="disabled"> Основной заемщик<br />
										<%} %>
										<%for(ContractorType ct : contractorTypeList){
											boolean checked = SBeanLocator.singleton().compendium().getContractorTypeIdByIdR(kz.getIdR()).contains(ct.getId());
											if(ct.getId().longValue()==1)continue;
											if(readOnly && !checked)continue;%>
											<label id="compare_contractor<%=kz.getIdR().toString()%>_type<%=ct.getId().toString()%>">
											<input <%=(checked)?"checked=\"checked\"":"" %>
											<%=readOnly?"disabled=\"disabled\"":"" %> 
											type="checkbox" name="<%=contractorTypeName %>" onclick="fieldChanged(this)"
											value="<%=ct.getId().toString() %>" > <%=ct.getName() %></label><br />
										<%}
									}else{ %>
									<md:ContractorType name="<%=contractorTypeName %>"
									value="<%=kz.getIdR().toString()%>" readonly="<%=readOnly %>" />
									<%} %>
									<label id="compare_list_contractor<%=accountid%>_type" class="compare-list-removed"></label>
								</td>
								<td id="ClientName<%=accountid%>">
									<a href="clientInfo.html?id=<%=accountid%>&mdtask=<%=mdtask.getIdMdtask().toString()%>" target="_blank">
									<%=accountname%></a>
									<%if(!readOnly){ %>
									<input type="hidden" name="IDCRM_Contractors" value='<%=accountid%>'/>
									<input type="hidden" name="IDCRM_Contractors_idr" value='<%=kz.getIdR()%>'/>
									<%} 
									boolean fkr = false;
									if(ru.md.spo.util.Config.enableIntegration()){
										try{
										    fkr=ru.masterdm.integration.ServiceFactory.getService(ru.masterdm.integration.monitoring.MonitoringService.class).isFkrExcludeWrongIdentifiedByContractor(accountid);
										}catch (Throwable e) {
	                                       System.out.println("fkr ERROR ON frame_contractor.jsp 1:" + e.getMessage());
									        e.printStackTrace();
									    }
								    }
								    if(fkr){%>
								        <a href="/km-web/fkr/list/contractor/<%=accountid%>"><span class="error">ФКР</span></a>
								    <%} %>
									<% if(kz.isMainOrg() && changeMainOrgEnable && mdtask.isMainOrgChangebleB() && mdtask.getIdPupProcess()!=null
											&& (TaskHelper.getCurrStageName(request).equalsIgnoreCase("Дополнение заявки") || TaskHelper.getCurrStageName(request).equalsIgnoreCase("Определение проектной команды"))
											&& !mdtask.isPipelineProcess() && !readOnly){%>
									<br /><br /><button onclick='changeMainOrganisationStage1();return false;' style="font-size: 10px">Изменить&nbsp;заемщика</button>
									<%} %>
								</td>
								<td id="ClientCategory<%=accountid%>"></td>
								<td id="Branch<%=accountid%>"></td>
								<td id="Region<%=accountid%>"></td>
								<td id="Ratingcalculated<%=accountid%>"></td>
								<td id="Ratingexp<%=accountid%>"></td>
								<td id="RatingApproved<%=accountid%>"></td>
								<td id="RatingPKR<%=accountid%>"> <md:input name="ratingPKR" readonly="<%=readOnly %>" value="<%=Formatter.str(kz.getRatingpkr()) %>" onChange="fieldChanged(this)" styleClass="text" style="width:85%" /> </td>
								<td ALIGN=RIGHT id="Attach<%=accountid%>"></td>
								<td class="delchk"><%
								if (!readOnly) {
								%>
									<input type="checkbox" name="idTableContractor_chk" <%=(!kz.isMainOrg() || mdtask.isPipelineProcess() && mdtask.getProjectName()!=null)?"":"disabled=\"disabled\"" %> />
								<%
								}
								%>
								</td>
							  <%
                              } catch (Exception e) {out.println("Ошибка в секции frame_contractor.jsp 2:" + e.getMessage());e.printStackTrace();}
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
									<td colspan="11" class="add" style="border-top: 1px;">
										<button onmouseover="Tip(getToolTip('Добавить контрагента'))" onmouseout="UnTip()" 
										onclick='addOrganisationStage1();return false;' class="add"></button>
										&nbsp;
										<button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" 
										onclick="DelRowWithLast('idTableContractor', 'idTableContractor_chk'); return false;" class="del"></button>
									</td>
								</tr>
							</tfoot>
						<%
						}
						%>
					</table>
<script id="mainOrgTemplate" type="text/x-jquery-tmpl">
<tr class="org main" id="${id}">
<td style="width:5em;white-space:nowrap">
<input type="hidden" id="mainOrgID" value="${id}">
										<input type="checkbox" checked="checked" disabled="disabled"> <a href="javascript:;" onclick="openMainBorrowerChangeReport();return false;">Основной заемщик</a><br />
										<%for(ContractorType ct : contractorTypeList){
											if(ct.getId().longValue()==1)continue;%>
<input type="checkbox" name="contractorType${id}" value="<%=ct.getId().toString() %>" > <%=ct.getName() %><br />
										<%} %>
</td>
<td>
<a href="clientInfo.html?id=${id}&mdtask=<%=mdtask.getIdMdtask().toString()%>" target="_blank">${orgname}</a>
<input type="hidden" name="IDCRM_Contractors" value='${id}'/>
<input type="hidden" name="IDCRM_Contractors_idr" value=''/>
</td>
<td id="ClientCategory${id}"></td>
<td id="Branch${id}"></td>
<td id="Region${id}"></td>
<td id="Ratingcalculated${id}"></td>
<td id="Ratingexp${id}"></td>
<td id="RatingApproved${id}"></td>
<td id="RatingPKR${id}"> <md:input name="ratingPKR" readonly="false" value="" styleClass="text" /> </td>
<td ALIGN=RIGHT id="Attach${id}"></td>
<td class="delchk"><input type="checkbox" name="idTableContractor_chk" disabled="disabled" /></td>
</tr>
</script>
<script id="newOrgTemplate" type="text/x-jquery-tmpl">
<tr class="org" id="${id}">
<td style="width:5em;white-space:nowrap" id="ClientType${id}">
<%for(ContractorType ct : contractorTypeList){%>
<input type="checkbox" name="contractorType${id}" value="<%=ct.getId().toString() %>" > <%=ct.getName() %><br />
<%} %>
</td>
<td>
<a href="clientInfo.html?id=${id}&mdtask=<%=mdtask.getIdMdtask().toString()%>" target="_blank">${orgname}</a>
<input type="hidden" name="IDCRM_Contractors" value='${id}'/>
<input type="hidden" name="IDCRM_Contractors_idr" value=''/>
</td>
<td id="ClientCategory${id}"></td>
<td id="Branch${id}"></td>
<td id="Region${id}"></td>
<td id="Ratingcalculated${id}"></td>
<td id="Ratingexp${id}"></td>
<td id="RatingApproved${id}"></td>
<td id="RatingPKR${id}"> <md:input name="ratingPKR" readonly="false" value="" styleClass="text" /> </td>
<td ALIGN=RIGHT id="Attach${id}"></td>
<td class="delchk"><input type="checkbox" name="idTableContractor_chk" /></td>
</tr>
</script>
			</td>
		</tr>
		<tr>
			<td class="compare-list-removed" id="compare_list_contractor"></td>
		</tr>
			<tr>
				<td vAlign=top>
					<table class="regular" id="idTableContractorGroup" name="idTableContractorGroup" style="width: 99.7%;">
						<thead>
							<tr>
								<th style="width: 14%;">Наименование Группы</th>
								<th style="width: 14%;">Отрасль экономики СРР</th>
								<th style="width: 14%;">Регион СРР</th>
								<th style="width: 14%;">Рейтинг КО</th>
								<th style="width: 14%;">Экспертный рейтинг</th>
								<th style="width: 14%;">Расчетный рейтинг</th>
								<th style="width: 14%;">Дата рейтинга</th>
							</tr>
						</thead>
						<tbody>
						<%try{ %>
						<%for(String groupname : groupNames){ %>
						<tr><td><%=groupname %></td>
						<td></td><td></td><td></td><td></td><td></td><td></td></tr>
						<%} %>
						<%} catch (Exception e) {out.println("Ошибка в секции frame_contractor.jsp 3:" + e.getMessage());e.printStackTrace();} %>
						</tbody>
					</table>
				</td>
			</tr>
		<tr>
			<td class="compare-list-removed" id="compare_list_contractor_group"></td>
		</tr>
		<tr vAlign=top>
		<td id="compare_3faces">
		<%if(!mdtask.isProduct()){ %>
					<input type="checkbox" style="margin: 3px;" <%if(mdtask.is3faces()){ %>checked="checked"<%} %>
		<%if(readOnly){ %>disabled="disabled"<%} %>
		onclick="if(this.checked){document.getElementById('3faces').value='y';}else{document.getElementById('3faces').value='n';}" >
		Распространяется на третьи лица
		<input type="hidden" id="3faces" name="3faces" value="<%=mdtask.getFaces3() %>" >
		<%} %>
		
				</td>
			</tr>
		<tr>
			<td>
				<table class="regular" style="width: 100%;">
					<th style="width: 50%;">
						<b>Страновая принадлежность</b>
					</th>	
					<td id="compare_main_country" style="width: 50%;">	
					     <% if (readOnly) { %>
					           <%=Formatter.str(mdtask.getCountry()) %>
					     <% } else { %>
					         <input type="text" onkeyup="fieldChanged(this)" name="main_country" value="<%=Formatter.str(mdtask.getCountry())%>" style="width: 99%;"/>
					     <% } %>
					</td>
				</table>
			</td>	
		</tr>
		<%} catch (Exception e) {out.println("Ошибка в секции frame_contractor.jsp 4:" + e.getMessage());e.printStackTrace();} %>
		</tbody>
	</table>
