<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page isELIgnored="true" %>
<%
try {
    TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	TaskJPA task = taskFacade.getTask(TaskHelper.getIdMdTask(request));
	String type = task.getType();
	MdTask mdtask = TaskHelper.getMdTask(request);
	String ekname = mdtask.getEkname();
	if(ekname==null && task.getOrgList() != null && task.getOrgList().size() > 0){//по какой-то причине не синхронизировалось название ЕК. Будем делать долгий запрос.
		ekname = SBeanLocator.singleton().getDictService().getEkNameByOrgId(task.getOrgList().get(0).getId());
	}
    String projectName = mdtask.getProjectName() == null ? "" : mdtask.getProjectName();

	boolean isLastVersion = SBeanLocator.singleton().mdTaskMapper().isLastApprovedVersion(task.getId());
%>
		<h1 class="Zayavka">
            <%if (ekname != null) {%>
			    <%=ekname%>, 
            <%} else {%>
                <%=projectName%>,
                <input type="hidden" name="projectName" value="<%=projectName%>" />
            <%}%>
            <%=mdtask.getSumMillionWithCurrency()%>
		</h1>
<div style="text-align: center;"><span class="type"><%=type %></span> <%=task.getNumberDisplay() %>. Версия <%=task.getVersion() %>.
<%if(task.isLimit()||task.isSublimit()){
	if(TaskHelper.isEditMode("Основные параметры",request)){ %>
<input style="width:35em;white-space:nowrap" type="text" value="<%=task.getTitle()%>" name="title" onchange="fieldChanged();">
<%}else{ %>
<%=task.getTitle()%>
<%}}%></div>
<% if(request.getParameter("dash") != null){%>
    <div  style="text-align: center;">Атрибуты заявок содержат актуальные значения</div>
<%}%>
		<div id="versionError" style="color:red;font-weight: bold;
		<% if (isLastVersion || task.isSublimit()) {%>
			display:none;
		<% } else {%>
			display:block;
		<%}%>

		padding-left:15px;">Данная версия заявки не является последней!</div>
	<%
} catch (Exception e) {
	out.println("Ошибка в секции headerApplication:" + e.getMessage());
	e.printStackTrace();
}
%>