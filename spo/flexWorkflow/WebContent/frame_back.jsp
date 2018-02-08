<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String viewtype = request.getParameter("viewtype");
Long idMdtask = TaskHelper.getIdMdTask(request);
WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
MdTask task = TaskHelper.getMdTask(request);
String typeListSessionParam = (String) request.getSession().getAttribute("typeListAttr");
if (typeListSessionParam == null) {
    typeListSessionParam = "all";
}

TaskInfo taskInfo = TaskHelper.getCurrTaskInfo(request);
if(request.getParameter("idListProcess")!=null){taskInfo=null;}
boolean isSublimit = task.getIdPupProcess()==null || task.getIdPupProcess().longValue()==0;
String redirecturl_original="form.jsp?mdtaskid="+idMdtask;
if(request.getParameter("monitoringmode")!=null){redirecturl_original+="&monitoringmode="+request.getParameter("monitoringmode");}
if(request.getParameter("ced_id")!=null){redirecturl_original+="&ced_id="+request.getParameter("ced_id");}
if(request.getParameter("ced_type")!=null){redirecturl_original+="&ced_type="+request.getParameter("ced_type");}
if(request.getParameter("ced_idBpmsEntity")!=null){redirecturl_original+="&ced_idBpmsEntity="+request.getParameter("ced_idBpmsEntity");}
if(taskInfo!=null){
    redirecturl_original="task.context.do?id="+taskInfo.getIdTask().toString();
    if(request.getParameter("mdtask")!=null){
        redirecturl_original += "&mdtask="+request.getParameter("mdtask");
    }
}
else if (request.getParameter("viewtype") != null) {
    redirecturl_original="form.jsp?viewtype=" + request.getParameter("viewtype") + "&mdtask="+idMdtask;
}
%>
<script type="text/javascript">
    function goBack(url) {
      globalurl=url;
        <%if (request.getParameter("newsublimit") != null) {%>
            if(confirm('Вы не сохранили вновь созданный сублимит! Отказаться от создания сублимита (ОК) или продолжить редактирование (отмена)?'))
            {location.href=url;}
            return false;
        <%} else { %>
                if(changed){
                    $('#exitConfirm').dialog({draggable: false,width: 400});
                    return false;
                }
            location.href=url;
        <%}%>
    }
</script>
<input type="hidden" value="<%=redirecturl_original%>" name="redirecturl" id="redirecturl">
<input type="hidden" value="<%=redirecturl_original%>" id="redirecturl_original">
<div title="Заявка изменена. Сохранить изменения?" style="display: none;" id="exitConfirm">
    <%if(task.isSublimit()){ %>
    <button onclick="$('#redirecturl').val(globalurl);submitData(false);return false;" >Сохранить и вернуться к редактированию лимита</button>
    <% } else { %>
    <button onclick="$('#redirecturl').val(globalurl);submitData(false);return false;" >Сохранить и продолжить</button>
    <%} %><br /><br />
    <button onclick="location.href=globalurl;return false;">Продолжить без сохранения</button>
    <button onclick="$('#exitConfirm').dialog('close');">Отмена</button>
</div>
<%
    if(request.getParameter("from") == null) {//если пришли из email рассылки, то кнопку вообще не нужно выводить
if (!isSublimit) { //это не сублимит
    if (request.getParameter("dash")!=null){%>
        <script type="text/javascript">
            function dashBack() {
                goBack('<%=ru.md.controller.DashboardTaskListController.getDashListBackUrl(request)%>');
            }
        </script>
    <button id="btnReturnToDocs" onclick="dashBack();return false;">Вернуться к списку заявок</button>
    <%} else {
    if (request.getParameter("readonly") != null) { // вызываем со страницы Отзыв заявки%>
    <button id="btnReturnToDocs"
        onclick="goBack('returnToTaskList.html?strutsAction=refuseOperationList&department=-1&typeList=<%=typeListSessionParam%><%if (task.getIdPupProcess() != null) { %>&idPupProcess=<%=task.getIdPupProcess()%><%}%><%if (taskInfo != null && taskInfo.getIdTask() != null) { %>&idTask=<%=taskInfo.getIdTask()%><%}%>');return false;">Вернуться к списку заявок</button>
    <%} else {%>
            <button id="btnReturnToDocs" <%if(request.getParameter("monitoringmode")!=null){%>style="display: none" <%}%>
            onclick="goBack('returnToTaskList.html?strutsAction=showTaskList&typeList=<%=typeListSessionParam%><%=(viewtype!=null && viewtype.equals("projectteam"))?"&projectteam=true":"" %><%=request.getParameter("paused")==null?"":"&paused=true" %>&idDepartment=<%=wsc.getCurrentUserInfo().getDepartament().getId() %><%if (task.getIdPupProcess() != null) { %>&idPupProcess=<%=task.getIdPupProcess()%><%}%><%if (taskInfo != null && taskInfo.getIdTask() != null) { %>&idTask=<%=taskInfo.getIdTask()%><%}%>');return false;">Вернуться к списку заявок</button>
    <%}
    }
} else { //это саблимит
    if (request.getParameter("id") != null && request.getParameter("readonly") == null) {//режим редактирования %>
        <button id="btnReturnToDocs" onclick="goBack('task.context.do?id=<%=request.getParameter("id")%>');return false;">Вернуться к редактированию лимита</button>
    <% } else {
    if(request.getParameter("mdtask") != null) {
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        TaskJPA taskJPA = taskFacadeLocal.getTask(task.getIdMdtask());
    %>
        <button id="btnReturnToDocs" onclick="goBack('form.jsp?mdtask=<%=taskJPA.getParentMdtaskId()%>');return false;">Вернуться к просмотру лимита</button>
    <%}}
}
        }
%>