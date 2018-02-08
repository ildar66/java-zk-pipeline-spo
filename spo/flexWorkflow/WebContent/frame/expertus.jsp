<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
%>
<%=pupFacadeLocal.getExpertHtmlReport(Long.valueOf(request.getParameter("mdtaskid")))%>
<a href="javascript:;" onclick="reloadExpertTeam()">Обновить</a>
