<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="com.vtb.domain.ProcessSearchParam"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="RU" />
<%ProcessSearchParam processSearchParam = new  ProcessSearchParam(request,false); 
%>
<%if(request.getParameter("searchCurrency")!=null&&!request.getParameter("searchCurrency").equals("all")){%>
            <strong>Показаны только заявки в валюте <%=request.getParameter("searchCurrency") %>!</strong>
<%}%>
        <thead>
            <tr>
                <th><a href="#" onClick="showSearch()">поиск</a></th>
                <th>№</th>
                <th>Версия</th>
                <th>Контрагент</th>
                <th>Группа компаний</th>
                <th>Сумма</th>
                <th title="Валюта">Вал.</th>
                <th title="Тип заявки">Тип</th>
                <th title="Приоритет">Приор.</th>
                <th>Статус</th>
                <th>Инициирующее подразделение</th>
                <th>Тип процесса</th>
            </tr>
        
            <tr id="fullSearchForm" <%=processSearchParam.showFilter()?"":"style=\"display:none\"" %>>
                <td>
                    <button type="submit">найти</button>
                    <button onclick="clearForm(this.form);submit();" name="clear" class="button clear">очистить </button>
                </td>
                <td><input name="searchNumber" type="text" style="width:10em;" 
                    value="<%=processSearchParam.getNumber()==null?"":processSearchParam.getNumber() %>"></td>
                <td>
                    <%if(request.getParameter("favorite") != null || request.getParameter("closed") != null) { %>
                        <input type="checkbox" id="searchLastVersion" value="y" name="searchLastVersion" <%if(processSearchParam.isShowOnlyLastVersionTask()){ %>checked="checked"<%} %> onclick="if($(this).prop('checked')){$(this).val('y');}else{$(this).val('n');}" /> Последние
                    <%} %>
                </td>
                <td><input name="searchContractor" type="hidden" value="<%=processSearchParam.getContractor()==null?"":processSearchParam.getContractor() %>">
                    <input type="hidden" id="SPOcontractorID" name="IDSPO_Contractors0" value=""/>
                    <%String orgname="не выбрано";
                    if(processSearchParam.getContractor()!=null){
                    	orgname = TaskHelper.dict().getOrg(processSearchParam.getContractor()).getOrganizationName();
                    }
                     %>
                    <input onclick='return openDialog("popup_org.jsp?formName=processGrid&ek=all&mode=inprocess&fieldNames=IDSPO_Contractors0|selectedName|searchContractor", "organizationLookupList", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");' type="text" class="text" readonly="true" name="selectedName" value='<%=orgname %>' />
                </td>
                <td></td>
                <td>
                    от<input name="searchSumFrom" type="text"
                    value="<%=Formatter.format(processSearchParam.getSumFrom()) %>">
                    <br />до<input name="searchSumTo" type="text"
                    value="<%=Formatter.format(processSearchParam.getSumTo()) %>"></td>
                <td><select name="searchCurrency">
                        <option value="all"></option>
                        <% for(String currency : TaskHelper.dict().findCurrencyList()){ %>
<option value="<%=currency %>"<%=(processSearchParam.getCurrency()!=null&&processSearchParam.getCurrency().equals(currency))?"selected":"" %>><%=currency %></option><%} %>
                    </select
                ></td>
                <td><input name="searchType" type="text"
                    value="<%=processSearchParam.getType()==null?"":processSearchParam.getType() %>">
                    <select name="searchShowImportOnly">
                        <option value=""></option>
                        <option value="access" <%if(processSearchParam.getShowImportOnly().equals("access")){ %>selected<%} %>>заявки из access</option>
                        <option value="bm" <%if(processSearchParam.getShowImportOnly().equals("bm")){ %>selected<%} %>>заявки БМ</option>
                    </select>
                </td>
                <td><select name="searchPriority">
                        <option value="all"></option>
                        <option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("высокий"))?"selected":"" %> value="высокий">высокий</option>
                        <option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("средний"))?"selected":"" %> value="средний">средний</option>
                        <option <%=(processSearchParam.getPriority()!=null&&processSearchParam.getPriority().equals("низкий"))?"selected":"" %> value="низкий">низкий</option>
                    </select
                ></td>
                <td><input name="searchStatus" type="text"
                    value="<%=processSearchParam.getStatus()==null?"":processSearchParam.getStatus() %>"><br />
                    <%if(request.getParameter("paused")==null){ %>
                    <input type="checkbox"   <%if(processSearchParam.isHideApproved()){ %>checked<%} %> id="searchHideApproved"
                    onclick="if($('#searchHideApproved').prop('checked')){$('#searchHideApprovedh').val('y');}else{$('#searchHideApprovedh').val('n');}">
                    <input type="hidden" name="searchHideApproved" id="searchHideApprovedh" <%if(processSearchParam.isHideApproved()){ %>value="y" <%}else{ %> value="n" <%} %>>
                Скрыть одобренные заявки<%} %>
                    </td>
                <td><input name="searchInitDepartment" type="text"
                    value="<%=processSearchParam.getInitDepartment()==null?"":processSearchParam.getInitDepartment() %>"></td>
                <td><select name="searchProcessType">
                        <option value="all"></option>
                        <%for(ru.md.pup.dbobjects.ProcessTypeJPA processType : TaskHelper.pup().findProcessTypeList()){ %>
<option value="<%=processType.getIdTypeProcess().toString() %>"<%=(processSearchParam.getProcessTypeID()!=null&&Long.valueOf(processSearchParam.getProcessTypeID()).equals(processType.getIdTypeProcess()))?"selected":"" %>><%=processType.getDescriptionProcess() %></option>
<%} %>
                    </select
                ></td>
            </tr>
        </thead>
