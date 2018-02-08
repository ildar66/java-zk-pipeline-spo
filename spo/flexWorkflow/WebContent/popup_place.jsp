<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.persistence.PlaceClientRecordMapper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<% long tstart=System.currentTimeMillis();%>
<html:html>
<head>
<title>Изменение места проведения сделки</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup" onload="document.getElementById('branch').focus()">
<div align="left" style="font-size: 22px;"><b>Изменение места проведения сделки</b></div>
<% 
String idMdtask = request.getParameter("idmdtask");
String mainLink = "popup_place.jsp?idmdtask=" + (request.getParameter("idmdtask") == null ? "" : request.getParameter("idmdtask"));
Map<String,Object> filter =  new HashMap<String,Object>();
if (request.getParameter("idmdtask") != null && !request.getParameter("idmdtask").isEmpty())
	filter.put("idmdtask", request.getParameter("idmdtask"));
List<java.lang.String> categories = SBeanLocator.singleton().getPlaceClientRecordMapper().getClientCategories(filter);
%>
<form action="" id="listForm">
<div>
<br/>
<input type="hidden" name="idmdtask" id="idmdtask"	value="<%=request.getParameter("idmdtask")%>">
Филиал/ГО <input type="text" name="branch" id="branch"
	class="text" style="width: 522px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("branch") == null ? "" : request.getParameter("branch").replaceAll("\"","")%>'>
<br />
Обслуживающее подразделение <input type="text" name="servicedepartment" id="servicedepartment"
	class="text" style="width: 386px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("servicedepartment") == null ? "" : request.getParameter("servicedepartment")%>'>
<br />
Номер <input type="text" name="crmid" id="crmid"
	class="text" style="width: 551px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("crmid") == null ? "" : request.getParameter("crmid")%>'>
<br />
Название <input type="text" name="organization" id="organization"
	class="text" style="width: 532px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("organization") == null ? "" : request.getParameter("organization")%>'>
<br />
ИНН <input type="text" name="inn" id="inn"
	class="text" style="width: 564px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("inn") == null ? "" : request.getParameter("inn")%>'>
<br />
Тип клиента <select name="typefilter" style="width: 517px;">
	<option value="">все типы клиентов</option>
	<option value="Бывший клиент"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals("Бывший клиент") ? "selected" : ""%>>Бывший
	клиент</option>
	<option value="Клиент"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals( "Клиент") ? "selected" : ""%>>Клиент</option>
	<option value="Проспект"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals("Проспект") ? "selected" : ""%>>Проспект</option>
</select>
<br />
Категория клиента <select name="categoryfilter" style="width: 477px;">
		<option value="">все категории</option>
		<%
			if (categories != null)
		    for (String category: categories) {
		%>
			<option value="<%=category%>"
				<%=request.getParameter("categoryfilter") != null && request.getParameter("categoryfilter").equals(category) ? "selected" : ""%>><%=category%>
			</option>
		<%
			}
		%>
</select>
<br />
<!--  <a onclick="document.body.style.cursor='wait'"
	class="button" href="<%=mainLink%>">найти</a>-->
  <button type="submit">найти</button>
<a href="popup_place.jsp?idmdtask=<%=idMdtask%>">Очистить
форму поиска</a>

<div class="paging">
	<table  style="margin: auto;width: auto;">
	<tr style="font-size: 15px;">      
<%
	int pageSize = 15;
	List<String> filterNames = Arrays.asList("branch", "servicedepartment", "crmid", "organization", "inn", "typefilter", "categoryfilter");
	for (String filterName:filterNames)
		if (request.getParameter(filterName) != null && !request.getParameter(filterName).isEmpty()) filter.put(filterName, request.getParameter(filterName));
	
	String pagenum = request.getParameter("pagenum") == null ? "1" : request.getParameter("pagenum");
	//list
	List<ru.md.domain.PlaceClientRecord> list = SBeanLocator.singleton().getPlaceClientRecordMapper().getPlaceKzPage(filter, pageSize * (new Integer(pagenum) - 1), pageSize);
	//count
	Long totalCount = SBeanLocator.singleton().getPlaceClientRecordMapper().getPlaceKzPageTotalCount(filter);
%>
	<td> Найдено <b><%=totalCount%></b>, показывается по <%=pageSize%>.&nbsp;</td>
<%
    Long pageCount = 1 + (totalCount - 1) / pageSize;
        int curr = new Integer(pagenum).intValue();
        String pagename = (curr - 1) * pageSize + 1 + "&#150;" + pageSize * curr;
        String link = "popup_place.jsp?idmdtask=" + (request.getParameter("idmdtask") == null ? "" : request.getParameter("idmdtask"))
        											 + (request.getParameter("allversions") == null ? "" : ("&allversions=" + request.getParameter("allversions")));
		for (String filterName:filterNames) {
			if (request.getParameter(filterName) != null && !request.getParameter(filterName).isEmpty())		
                link += ("&" + filterName + "=" + request.getParameter(filterName));
		}
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

<table style="width: 100%;">
<tr>
	<td style="font-size: 20px;">
		<b>&nbsp;Место проведения сделки &nbsp;&nbsp;</b>
	</td>
	<td style="font-size: 20px;">
		<b>&nbsp;Привязываемая клиентская запись</b>
	</td>
</tr>	
</table>
<table class="regular">
	<thead>
		<tr>
			<th>Филиал/ГО</th>
			<th>Обслуживающее<br/>подразделение</th>
			<th>Номер</th>
			<th>Название</th>
			<th>ИНН</th>
			<th>Тип<br/>клиента</th>
			<th>Категория<br/>клиента</th>
		</tr>
	</thead>
	<tbody>
		<%
		    for (ru.md.domain.PlaceClientRecord placeKz : list) {
		%>
		<tr>
			<td><%=placeKz.getName()%></td>
			<td><%=placeKz.getDivision()%></td>
			<td style="border-left: thin solid;border-left-color: rgb(153, 187, 221);"><%=placeKz.getCrmId()%></td>
			<td>
				<a href="javascript:Go('<%=placeKz.getId()%>', '<%=Formatter.strWeb(placeKz.getName())%>', '<%=placeKz.getCrmId()%>')">
					<%=placeKz.getOrganizationName()%>
				</a>
			</td>
			<td><%=placeKz.getInn()%></td>
			<td><%=placeKz.getClientType()%></td>
			<td><%=placeKz.getClientCategory()%></td>
		</tr>
		<%
		    }
		%>
	</tbody>
</table>

<%
Long loadTime = System.currentTimeMillis()-tstart;
out.println("<div style=\"color:gray\"><em>Время формирования страницы (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em></div>");
%>
</form>
<script language="javascript">

function Close() {
    var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if (outform != null) {
        document.body.style.cursor="wait";
        window.opener.focus();
    }
    window.close();
}
function Go(placeId, placeName, crmId) {
	var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if (outform != null) {
        outform['selectedPlaceId'].value = placeId;
        outform['selectedPlaceName'].value = placeName;
        outform['selectedCRMId'].value = crmId;
    }
    Close();
    onMySelect();
}
function onMySelect(){
        if (opener.execScript) {
            opener.execScript('onSelectPlace()'); //for IE
        } else {
           eval('self.opener.onSelectPlace()'); //for Firefox
        }
}
</script>

</body>
</html:html>