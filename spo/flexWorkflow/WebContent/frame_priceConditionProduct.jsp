<%@page isELIgnored="true" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%response.addHeader("Pragma", "no-cache");
    response.addHeader("Expires", "-1");
    response.addHeader("Cache-control", "no-cache");%>
<%boolean ro =!TaskHelper.isEditMode("Стоимостные условия",request)|| request.getParameter("monitoringmode")!=null;
Task task=TaskHelper.findTask(request);
 %>
<script>
    $(function() {
        $( "#price_condition_product_tabs" ).tabs();
        restoreTab('price_condition_product_tabs');
    });
    $(document).ready(function() {
        $('#price_condition_product_tab-3').load('frame/commission.jsp?mdtaskid=<%=task.getId_task()%>&readOnly=<%=ro%>&pupTaskId=0&mdtask=0');
        loadCompareResult('price_conditions');
    });
</script>
<div id="price_condition_product_tabs">
    <ul>
        <li><a href="#price_condition_product_tab-2" onclick="storeTab('price_condition_product_tabs',0)">Процентная ставка</a></li>
        <li><a href="#price_condition_product_tab-3" onclick="storeTab('price_condition_product_tabs',1)">Комиссии/вознаграждения</a></li>
        <li><a href="#price_condition_product_tab-1" onclick="storeTab('price_condition_product_tabs',2)">Санкции (неустойки, штрафы, пени и т.д.)</a></li>
    </ul>
    <div id="price_condition_product_tab-1">
        <jsp:include flush="true" page="frame/fineList.jsp"/>
    </div>
    <div id="price_condition_product_tab-2">
        <jsp:include flush="true" page="frame/percentStavka.jsp"/>
    </div>
    <div id="price_condition_product_tab-3"></div>
</div>

