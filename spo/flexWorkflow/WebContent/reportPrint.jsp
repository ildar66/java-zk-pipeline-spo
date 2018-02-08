<%@ page import="org.uit.director.action.AbstractAction" %>
<%@ page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@ page import="org.uit.director.report.WorkflowReport" %>
<%@ page pageEncoding="Cp1251" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>

<html:html xhtml="true">

    <head>
        <title></title>
        <style type="text/css">@import url( "resources/stylesheet.css" );
        BODY {
        . SCROLLBAR-FACE-COLOR : #EBF0F8;
        . SCROLLBAR-HIGHLIGHT-COLOR : #CACAFB;
        . SCROLLBAR-SHADOW-COLOR : #000066;
        . SCROLLBAR-ARROW-COLOR : #FFFF00;
        . SCROLLBAR-BASE-COLOR : #000077;
        . scrollbar-dark-shadow-color : #000066;
        . scrollbar-3d-light-color : #000066;
        } </style>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    </head>

    <body>
    <script language="javascript" src="resources/sort.js"></script>

    <br>

    <%
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) {
            %><script> document.location = "start.do"; </script> <%
            return;
        }
        WorkflowReport reportInstance = wsc.getReport();
    %><%
String results = reportInstance.getReportHTML();
results = results.replaceAll("<input type=\"submit\" value=\"Изменить\">", "");
results = results.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать документ\" width=\"17px\" hight=\"17px\">", "");
results = results.replaceAll("<img src=\"resources/activity.minus.gif\" border=\"0\" alt=\"Аннулировать страницу\" width=\"17px\" hight=\"17px\">", "");
results = results.replaceAll("<img src=\"resources/activity.plus.gif\" border=\"0\" alt=\"Добавить страницу\" width=\"17px\" hight=\"17px\">","");
results = results.replaceAll("<img src=\"resources/activity.plus.gif\" border=\"0\" alt=\"Добавить документ\" width=\"17px\" hight=\"17px\">", "");
%>

<%=results%>

    <center><a href="javascript:history.go(-1)">Назад</a></center>
    </body>

</html:html>


