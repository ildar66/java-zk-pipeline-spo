<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<br />
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

String mdtaskid=request.getParameter("mdtaskid");
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
if(pupFacade.isCurrentUserInProjectTeam(Long.valueOf(mdtaskid)) && request.getParameter("pupTaskId")!=null &&!request.getParameter("pupTaskId").equals("0")
        && pupFacade.getTaskStatus(Long.valueOf(request.getParameter("pupTaskId")))==2){%>
<textarea rows="7" name="active_decision" onkeyup="fieldChanged(this)"><%=taskFacade.getTask(Long.valueOf(mdtaskid)).getActive_decision() %></textarea>
<%} else {
    String s = taskFacade.getTask(Long.valueOf(mdtaskid)).getActive_decision();
if(s.isEmpty()){s="поле не заполнено";}%>
<%=s %>
<%}%>