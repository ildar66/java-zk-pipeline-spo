<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.domain.MdTask"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.util.Formatter" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.masterdm.compendium.domain.crm.StatusReturn" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.spo.SpoStatusReturn" %>
<%@page import="ru.masterdm.compendium.domain.cc.CcResolutionStatus"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
/*подсекция должна быть доступна для редактирования на форме операции БП по Сделке если одновременно выполняются условия:
атрибут Коллегиальный = Y (если решение принято Уполномоченным органом)
атрибут Decision= 1*/
MdTask mdtask = TaskHelper.getMdTask(request);
    Task task=TaskHelper.findTask(request);
    CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
    ru.masterdm.compendium.model.CompendiumSpoActionProcessor compenduimSPO = (ru.masterdm.compendium.model.CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
    StatusReturn[] statusReturnList = compenduim.findStatusReturn(null);
    ArrayList<CcResolutionStatus> list = compenduimSPO.findCcResolutionStatusList(new CcResolutionStatus(task.getCcStatus().getStatus().getId(),null,null,null),null);
    CcResolutionStatus status = null;
    for (CcResolutionStatus s : list) status = s;
boolean editMode = pupFacade.getPUPAttributeValue(mdtask.getIdPupProcess(), "Decision").equals("1")
    && pupFacade.getPUPAttributeValue(mdtask.getIdPupProcess(), "Коллегиальный").equalsIgnoreCase("y")
        && TaskHelper.isEditMode(null,request);
%>
<script>
    $(function() {
        dialogHandler();
        $( "#decision_tabs" ).tabs();
        restoreTab('decision_tabs');
    });
</script>
<div id="decision_tabs">
    <ul>
        <%if(TaskHelper.showSectionReturnStatus(task) || TaskHelper.showSectionReturnStatusCC(task)){%><li><a href="#decision_tabs-1" onclick="storeTab('decision_tabs',0)">Статус Решения по заявке</a></li><%} %>
        <%if(TaskHelper.showSpecialDecision(mdtask)){%><li><a href="#decision_tabs-0" onclick="storeTab('decision_tabs',1)">Особые условия решения</a></li><%}%>
    </ul>
    <%if(TaskHelper.showSpecialDecision(mdtask)){%>
    <div id="decision_tabs-0">
        <%if(editMode){ %><input type="hidden" name="section_decision"><%} %>
        <table class="regular"><tbody>
        <tr><th>Требуется формирование Дополнительного соглашения</th>
            <td><input type="checkbox" value="1" name="additional_contract_decision" <%if(mdtask.isAdditionalContract()){%>checked<%}%> <%if(!editMode){ %>disabled="disabled"<%} %>>
            </td></tr>
        <tr><th>Решение влияет на мониторинг Сделки</th>
            <td><input type="checkbox" value="1" name="product_monitoring_decision" <%if(mdtask.isProductMonitoring()){%>checked<%}%> <%if(!editMode){ %>disabled="disabled"<%} %>>
            </td></tr>
        </tbody>
        </table>
    </div><%}%>
    <div  id="decision_tabs-1">
        <%if(TaskHelper.showSectionReturnStatus(task)){%>
        <table style="width: 98%">
            <tr><td><label>дата принятия решения<br />
                <md:calendarium name="refuse_date" id="refuse_date" readonly="<%=!editMode %>"
                                value="<%=Formatter.format(task.getTaskStatusReturn().getDateReturn()) %>"/>
            </label>
            </td></tr>
            <%
                Long userid = task.getTaskStatusReturn().getIdUser();
                String authorizedPerson = "";
                TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
                ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
                if (taskJPA.getAuthorizedPerson()!=null){
                    authorizedPerson = taskJPA.getAuthorizedPerson().getDisplayName();
                }
                if(authorizedPerson=="" && userid!=null && userid>0l){
                    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                    authorizedPerson = pupFacadeLocal.getUser(userid).getFullName() + " (" +
                            pupFacadeLocal.getUser(userid).getDepartment().getShortName() + ")";
                }
                if(authorizedPerson!=""){
            %>
            <tr><td><label>
                Уполномоченное лицо: <%=authorizedPerson %>
            </label></td></tr>
            <%} %>
            <tr><td><label>Статус решения (детализация)<br />
                <%if(editMode){ %>
                <select name="StatusReturn" id="StatusReturn">
                    <%for(StatusReturn statusReturn:statusReturnList){ %>
                    <option value="<%=statusReturn.getId() %>"><%=statusReturn.getDescription() %></option>
                    <%} %>
                </select>
                <%}else{ %>
                <%=task.getTaskStatusReturn().getStatusReturn().getDescription() %><%} %></label>
            </td></tr><tr><td>
            Комментарий к решению<br />
                <%if(editMode){ %>
			<textarea rows="15" name="StatusReturnText" id="StatusReturnText" class="advanced_textarea" style="width: 99%;"><%} %>
			<%=task.getTaskStatusReturn().getStatusReturnText() %>
			<%if(editMode){ %></textarea><%} %>
        </td></tr></table>
        <%} %>
        <%if(TaskHelper.showSectionReturnStatusCC(task)){%>
        <table style="width: 98%">
            <tr><td><label>дата принятия решения<br />
                <%=Formatter.format(task.getCcStatus().getMeetingDate()) %></label>
            </td></tr>
            <tr><td><label>Решение уполномоченного органа<br />
                <%=task.getCcStatus().getStatus().getName() %> (<%=status.getCategoryName() %>)</label>
            </td></tr><tr><td>
                <%//решение окончательное
            if(task.getCcStatus().getStatus().getCategoryId().longValue()!=2){ %>
            <tr><td><label>Статус решения (детализация)<br />
                <%
                    ArrayList<SpoStatusReturn> statusCCReturnList = compenduimSPO.findSpoStatusReturnList(
                            new CcResolutionStatus(task.getCcStatus().getStatus().getId(),null,null,null),null);
                    if(statusCCReturnList.size()==0){
                 /*если соответствия не найдено вовсе, то из справочника «Статусы возврата заявки»
                 допускается выбор тех значений,
                 которых нет в «Справочнике соответствия статусов возврата из КК».*/
                        ArrayList<CcResolutionStatus> allCcResolutionStatus=compenduimSPO.findCcResolutionStatusList(null,null);
                        statusCCReturnList = compenduimSPO.findSpoStatusReturnList(new SpoStatusReturn(),null);
                        for(CcResolutionStatus ccs: allCcResolutionStatus){
                            ArrayList<SpoStatusReturn> rem = compenduimSPO.findSpoStatusReturnList(ccs,null);
                            statusCCReturnList.removeAll(rem);
                        }
                    }
                %>
                <select name="SPOStatusReturn" id="SPOStatusReturn" onchange="SPOStatusReturnChange()" <%=editMode?"":"disabled" %>>
                    <%if(statusCCReturnList.size()>1){ %><option value="0">не выбрано</option><%} %>
                    <%
                        String id=task.getTaskStatusReturn().getStatusReturn().getId();
                        if (id == null) id = "";
                        for(SpoStatusReturn statusReturn:statusCCReturnList){
                    %>
                    <option value="<%=statusReturn.getId() %>"
                            <%=id.trim().equals(statusReturn.getId().toString().trim())?"selected":"" %>>
                        <%=statusReturn.getName() %></option>
                    <%} %>
                </select>
            </label>
            </td></tr>
            <%} %>
        </table>
        <%if(false && status.getCategoryId().longValue()!=2){ %>
        <script language="javascript">
            function SPOStatusReturnChange() {
                try {
                    id=document.getElementById('SPOStatusReturn').value
                    document.getElementById('btnRegister').disabled = id=='0'
                    if (id=='0'){ $('#btnRegister').addClass('disabled');} else {$('#btnRegister').removeClass('disabled');}
                } catch (Err) {}
            }

            SPOStatusReturnChange();
        </script>
        <%}else{ %>
        <script language="javascript">
            function SPOStatusReturnChange() {}
        </script>
        <%}%>
        <%} %>
    </div>
</div>

