<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.md.persistence.PlaceHistoryMapper"%>
<% long tstart=System.currentTimeMillis();%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<html:html>
<head>
<%
String number = TaskHelper.taskFacade().getTask(Long.valueOf(request.getParameter("idmdtask"))).getNumberDisplay();
Long idTaskMain = TaskHelper.taskFacade().getTask(Long.valueOf(request.getParameter("idmdtask"))).getId();

Map<String,Object> filter =  new HashMap<String,Object>();

if (request.getParameter("idmdtask") != null && !request.getParameter("idmdtask").isEmpty())
	filter.put("idmdtask", request.getParameter("idmdtask"));
boolean allVersions = (request.getParameter("allversions") != null && request.getParameter("allversions").equals("true"));
if (allVersions)
	filter.put("allversions", "true");

//count
Long totalCount = SBeanLocator.singleton().getPlaceHistoryMapper().getPlaceHistoryCount(filter);
 %>
<title>История изменения места проведения сделки в Заявке № <%=number %></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body>
<form action="" id="listForm">
<div align="left" style="font-size: 22px;"><b>История изменения места проведения сделки в Заявке № <%=number %></b></div>

<input type="hidden" name="idmdtask" id="idmdtask"	value="<%=request.getParameter("idmdtask")%>">
<input type="hidden" name="allversions" id="allversions" value="">
<br/>
<table>
	<tr style="vertical-align:center">
		<td>
			<input type="checkbox" <%=(request.getParameter("allversions") != null && request.getParameter("allversions").equals("true"))?"checked":""%>
				onclick="document.getElementById('allversions').value = this.checked; document.forms['listForm'].submit();"/>
		</td>
		<td>
			 Показать историю с учетом всех версий
		</td>
	</tr>
</table>

<% if (totalCount == null || totalCount == 0) {%>
<br/>
нет истории изменений
<%} else {
	//list
	//List<ru.md.domain.PlaceHistory> list = SBeanLocator.singleton().getPlaceHistoryMapper().getPlaceHistory(filter);
%>

<div class="paging">
	<table  style="margin: auto;width: auto;">
	<tr style="font-size: 15px;">      
<%
	int pageSize = 15;
	String pagenum = request.getParameter("pagenum") == null ? "1" : request.getParameter("pagenum");
	List<ru.md.domain.PlaceHistory> list = SBeanLocator.singleton().getPlaceHistoryMapper().getPlaceHistoryPage(filter, pageSize * (new Integer(pagenum) - 1), pageSize);
%>
	<td> Найдено <b><%=totalCount%></b>, показывается по <%=pageSize%>.&nbsp;</td>
<%
    Long pageCount = 1 + (totalCount - 1) / pageSize;
        int curr = new Integer(pagenum).intValue();
        String pagename = (curr - 1) * pageSize + 1 + "&#150;" + pageSize * curr;
        String link = "placeChangeReport.jsp?idmdtask=" + (request.getParameter("idmdtask") == null ? "" : request.getParameter("idmdtask"))
        											 + (request.getParameter("allversions") == null ? "" : ("&allversions=" + request.getParameter("allversions")));
        link += "&pagenum=";
        if (curr > 1) {
%>
	<td><a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%>1">|&lt;&lt;</a></td>
	<td><a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%><%=curr - 1%>">&larr;</a></td>
<%
     }
%>
	<td><span class="selected"><%=pagename%></span></td>
<%
     if (curr < pageCount) {
%>
	<td><a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%><%=curr + 1%>">&rarr;</a></td>
	<td><a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%><%=pageCount%>">&gt;&gt;|</a></td>
<%
     }
%>
	</tr>
	</table>
</div>

<table class="regular" border="1">
	<thead>
		<tr>
			<th>Подразделение</th>
			<th>Автор</th>
			<th>Дата и время</th>
			<th>Версия</th>
		</tr>
	</thead>
	<tbody>
		<%
		    for (ru.md.domain.PlaceHistory history : list) {
		%>
		<tr <%=(history.getIdmdtask().equals(idTaskMain) ? " style='color:blue' " : "")%> >
			<td><%=history.getNewPlaceName()%></td>
			<td style="border-left: thin solid;border-left-color: rgb(153, 187, 221);">
				<%=history.getPerformerName()%>
			</td>
			<td style="border-left: thin solid;border-left-color: rgb(153, 187, 221);">
				<div style="text-align:center;">
					<%=Formatter.formatDateTime(history.getChangeDate())%>
				</div>
			</td>
			<td style="border-left: thin solid;border-left-color: rgb(153, 187, 221);">
				<div style="text-align:center;">
					<%=history.getVersion()%>
				</div>
			</td>
		</tr>
		<%
		    }
		%>
	</tbody>
</table>
<%
Long loadTime = System.currentTimeMillis()-tstart;
out.println("<div style=\"color:gray\"><em>Время формирования страницы (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em></div>");
}
%>
</form>
</body>
</html:html>
