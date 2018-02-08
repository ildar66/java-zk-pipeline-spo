<%@page isELIgnored="true"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
TaskJPA task = taskFacade.getTask(Long.valueOf(request.getParameter("mdtaskid")));
TaskJPA root = task;
if(task.isProduct()) root = task.getParent();
while(root!=null && root.getParent()!=null){root=root.getParent();}
String parentid = task.getParent()==null?"0":task.getParent().getId().toString();
	if (task.isProduct()) {
	%> <label>Сделка проводится в рамках лимита: <span><%=task.getParent() != null ? "Да" : "Нет"%></span>
	</label> <%}%>
	
<%if(root!=null){ %>
<a href="javascript:;" onclick="refreshSublimitFrame()">Обновить секцию</a><br>
<jsp:include page="inLimitTable.jsp"></jsp:include>
<script type="text/javascript">
var selectLimitMode = false;
var currentSublimit = '<%=task.getId().toString()%>';
var inLimit = '<%=parentid%>';
$(document).ready(function() {
    var param = {id: '<%=root.getId()%>',child:'false'};
    $.post('ajax/limittree.do',param,addinLimitRow);//подгрузить аяксом
});
</script>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "")
		loadCompareResult('in_limit');
</script>
<%}%>