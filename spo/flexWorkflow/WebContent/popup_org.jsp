<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<%@ page import="com.vtb.domain.OrgSearchParam" %>
<% long tstart=System.currentTimeMillis();%>
<html:html>
<head>
<title>Выбор юридического лица</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup" onload="document.getElementById('filter').focus()">
<%OrgSearchParam orgSearchParam = new OrgSearchParam(request);%>
<% if(request.getParameter("back2ek")!=null){%>
    <button type="submit" onclick="back2ek();return false;">Назад</button>
<%} else {
orgSearchParam.saveCookies(response);
}%>
<% HttpServletRequest rq = request;
   if(request.getParameter("ek")==null || request.getParameter("ek")!=null && request.getParameter("ek").equals("all")){%><h1>Выбор контрагента</h1><%} %>
<% if(request.getParameter("ek")!=null && request.getParameter("ek").equals("only")){%><h1>Выбор контрагента</h1><%} %>
<% if(request.getParameter("ek")!=null && !request.getParameter("ek").equals("only")&& !request.getParameter("ek").equals("all")){%><h1>Выбор Клиентской записи</h1><%} %>
<%
if (request.getParameter("mainorg") != null) {
	String id = request.getParameter("mainorg");
    OrgJPA mainOrg = TaskHelper.dict().getOrg(id);
    if(mainOrg.getIdUnitedClient()!=null){id = TaskHelper.dict().getOrg(mainOrg.getIdUnitedClient()).getId();}
	orgSearchParam.setGroup(SBeanLocator.singleton().compendium().getEkById(id).getGroupname());
}
%>
<form action="" id="listForm">
<div>
Название <input type="text" name="filter" id="filter"
	class="text" style="width: 522px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=orgSearchParam.getName()%>'>
<br />
	<% if(request.getParameter("ek")!=null && request.getParameter("ek").equals("only")){%>
Группа компаний <input type="text" name="filtergroup" id="filtergroup"
	class="text" style="width: 522px;"
	<%if (rq.getParameter("mainorg") != null || rq.getParameter("filtergroup2") != null || rq.getParameter("onMySelect")!=null && rq.getParameter("onMySelect").startsWith("changeMainOrganisation")) { %>disabled="disabled" <%} %>
	title="Введите часть названия и нажмите Enter"
	value='<%=orgSearchParam.getGroup()%>'>
<% if (request.getParameter("mainorg") != null) {%><input type="hidden" name="mainorg" value="<%=request.getParameter("mainorg")%>"> <%} %>
<% if(request.getParameter("filtergroup2")!=null){%>
	<br /> или	<input type="text" class="text" style="width: 522px;" disabled="disabled" value='<%=request.getParameter("filtergroup2")%>'>
<%}%>
<br /><%}%>
Номер <input type="text" name="numberfilter" class="text"
	style="width: 250px;"
	value="<%=orgSearchParam.getNumber()%>">
ИНН <input type="text" name="innfilter" class="text"
	style="width: 250px;"
	value="<%=orgSearchParam.getInn()%>">
<select name="typefilter" style="width: 588px;">
	<option value="">все типы клиентов</option>
	<option value="Бывший клиент"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals("Бывший клиент") ? "selected" : ""%>>Бывший
	клиент</option>
	<option value="Клиент"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals( "Клиент") ? "selected" : ""%>>Клиент</option>
	<option value="Проспект"
		<%=request.getParameter("typefilter") != null && request.getParameter("typefilter").equals("Проспект") ? "selected" : ""%>>Проспект</option>
</select><br>
<button type="submit">найти</button>
<%String  mainorg_link = request.getParameter("mainorg") != null?"&mainorg="+request.getParameter("mainorg"):"";%>
<a href="popup_org.jsp?ek=<%=request.getParameter("ek")%>&mode=<%=request.getParameter("mode")%>&formName=<%=request.getParameter("formName")%><%=mainorg_link %>&fieldNames=<%=request.getParameter("fieldNames")%>&onMySelect=<%=request.getParameter("onMySelect")%>">Очистить
форму поиска</a></div>
<div class="paging">
<%
Map<String,Object> filter =  orgSearchParam.getHashMap();
boolean kzMode = false;
if(request.getParameter("filtergroup2")!=null) filter.put("group2",request.getParameter("filtergroup2").toLowerCase());
if (request.getParameter("typefilter") != null && !request.getParameter("typefilter").isEmpty()) filter.put("type", request.getParameter("typefilter"));
if (request.getParameter("mode") != null && !request.getParameter("mode").isEmpty()) filter.put("mode", request.getParameter("mode"));
//проверить может текущий пользователь из филиала
String depName = TaskHelper.pup().getCurrentUser().getDepartment().getShortName();
if(depName.startsWith("Ф-л") || depName.startsWith("РОО")){
	filter.put("depName", depName);
}
if (request.getParameter("ek") != null && !request.getParameter("ek").equals("only") && !request.getParameter("ek").equals("all")) {
	filter.put("ek", request.getParameter("ek"));
	kzMode = true;
}
int pageSize = 15;
String pagenum = request.getParameter("pagenum") == null ? "1" : request.getParameter("pagenum");
List<ru.md.domain.Org> list = (request.getParameter("ek") != null && request.getParameter("ek").equals("only")) ?
		SBeanLocator.singleton().compendium().getEkPage(filter, pageSize * (new Integer(pagenum) - 1), pageSize)
		:SBeanLocator.singleton().compendium().getKzPage(filter, pageSize * (new Integer(pagenum) - 1), pageSize);
Long totalCount = (request.getParameter("ek") != null && request.getParameter("ek").equals("only"))?
		SBeanLocator.singleton().compendium().getEkPageTotalCount(filter)
		:SBeanLocator.singleton().compendium().getKzPageTotalCount(filter);
%> Найдено <b><%=totalCount%></b>, показывается по <%=pageSize%>. <%

    Long pageCount = 1 + (totalCount - 1) / pageSize;
        int curr = new Integer(pagenum).intValue();
        String pagename = (curr - 1) * pageSize + 1 + "&#150;" + pageSize * curr;
        String link = "popup_org.jsp?p=p"
				+ orgSearchParam.getUrlParam()
                + "&mode=" + (request.getParameter("mode") == null ? "" : request.getParameter("mode"))
                + (Formatter.str(request.getParameter("filtergroup2")).isEmpty()?"":"&filtergroup2=" + Formatter.str(request.getParameter("filtergroup2")))
                + "&typefilter=" + (request.getParameter("typefilter") == null ? "" : request.getParameter("typefilter"))
                + (request.getParameter("mainorg") == null ? "" : "&mainorg="+request.getParameter("mainorg"))
                + "&formName=" + request.getParameter("formName")
                + "&ek=" + request.getParameter("ek")
                + (Formatter.str(request.getParameter("back2ek")).isEmpty() ? "" : "&back2ek=" + request.getParameter("back2ek"))
                + "&fieldNames=" + request.getParameter("fieldNames")
                + "&onMySelect=" + request.getParameter("onMySelect")
                + "&nameId=" + Formatter.str(request.getParameter("nameId"))
                + "&contractorid=" + Formatter.str(request.getParameter("contractorid"))
                + "&pagenum=";
        if (curr > 1) {
%> <a onclick="document.body.style.cursor='wait'"
	class="button" href="<%=link%><%=curr - 1%>">&larr;</a> <%
     }
 %> <span
	class="selected"><%=pagename%></span> <%
     if (curr < pageCount) {
 %> <a
	onclick="document.body.style.cursor='wait'" class="button"
	href="<%=link%><%=curr + 1%>">&rarr;</a> <%
     }
 %>
</div>
<table class="regular">
	<thead>
		<tr>
			<th>Номер</th>
			<th>Название</th>
			<th>ИНН</th>
			<th>Тип клиента</th>
			<% if(kzMode){%><th>Филиал/ГО</th><%} %>
			<% if(kzMode || request.getParameter("ek")==null || request.getParameter("ek").equals("all")){%><th>Обслуживающее подразделение</th><%} %>
			<% if(request.getParameter("ek")!=null && request.getParameter("ek").equals("only")){%><th>Группа Компаний</th><%} %>
		</tr>
	</thead>
	<tbody>
		<%
		    for (ru.md.domain.Org org : list) {
		%>
		<tr>
			<td><%=org.getId()%></td>
			<td><a
				href="javascript:Go('|<%=Formatter.strWeb(org.getName())%>|<%=org.getId()%>', '<%=org.getRegionId()%>')"><%=org.getName()%></a>
			</td>
			<td><%=org.getInn()%></td>
			<td><%=org.getClientType()%></td>
			<% if(kzMode){%><td><%=org.getRegion()==null? "":org.getRegion()%></td><%} %>
			<% if(kzMode || request.getParameter("ek")==null || request.getParameter("ek").equals("all")){%><td><%=org.getDivision()==null?"":org.getDivision()%></td><%} %>
			<% if(request.getParameter("ek")!=null && request.getParameter("ek").equals("only")){%><td><%=org.getGroupname()%></td><%} %>
		</tr>
		<%
		    }
		%>
	</tbody>
</table>
<%
Long loadTime = System.currentTimeMillis()-tstart;
out.println("<div style=\"color:gray\"><em>Время формирования страницы (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em></div>");
	if (kzMode){%><div style="color:gray"><em>Для ЕК SLXID=<%=request.getParameter("ek")%></em></div><%}%>
<input type="hidden" name="formName" id="formName"	value="<%=request.getParameter("formName")%>">
<input type="hidden" name="nameId" id="nameId"	value="<%=Formatter.str(request.getParameter("nameId"))%>">
<input type="hidden" name="contractorid" id="contractorid"	value="<%=Formatter.str(request.getParameter("contractorid"))%>">
<% if(request.getParameter("back2ek")!=null){%><input type="hidden" name="back2ek" value="<%=Formatter.str(request.getParameter("back2ek"))%>"><%}%>
	<input
	type="hidden" value="<%=request.getParameter("fieldNames")%>"
	name="fieldNames" id="fieldNames"><input
		type="hidden" value="<%=request.getParameter("ek")%>"
		name="ek" id="ek"><input
		type="hidden" value="<%=request.getParameter("mode")%>"
		name="mode" id="mode"> <input type="hidden"
	value="<%=request.getParameter("onMySelect")%>" name="onMySelect"
	id="onMySelect"></form>
<script language="javascript">
function Go(strval, placeId) {
	var thisform = document.forms['listForm'];
	if (placeId != null && placeId != 'null')
		ChangeContractorPlaceId(placeId);
	<%if(!Formatter.str(request.getParameter("nameId")).isEmpty()){%>
	values = strval.split('|');
	window.opener.document.getElementById('<%=request.getParameter("nameId")%>').value = unescape(values[1]);
	window.opener.document.getElementById('<%=request.getParameter("contractorid")%>').value = unescape(values[2]);
	Close();
	<%} else {%>
	if (thisform.formName.value == 'null'){
		var names = thisform.fieldNames.value.split('|'), values = strval.split('|');
		for(var i in names) {
			if(names[i].length > 0){
				window.opener.document.getElementById(unescape(names[i])).value = unescape(values[i]);
			}
		}
	}
	var outform = window.opener.document.forms[thisform.formName.value];
	if(outform != null)
	{
		var names = thisform.fieldNames.value.split('|'), values = strval.split('|');
		for(var i in names) {
			if(names[i].length > 0){
				outform[unescape(names[i])].value = unescape(values[i]);
				//alert("name="+names[i]+", value="+values[i]);
				//alert(outform[unescape(names[i])].value);
			}
		}
	}
	Close();
	onMySelect();
	<%}%>
}
function ChangeContractorPlaceId(placeId) {
	var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if (outform != null && outform['contractorPlaceId'] != null && placeId != null)
        outform['contractorPlaceId'].value = placeId;
}
function Close(){
    var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms[thisform.formName.value];
    if(outform != null) {
        document.body.style.cursor="wait";
        window.opener.focus();
    }
    window.close();
}
function onMySelect(){
    if($('#onMySelect').val() != ""){
        if (opener.execScript) {
            opener.execScript($('#onMySelect').val()); //for IE
        } else {
           eval('self.opener.' + $('#onMySelect').val()); //for Firefox
        }
    }
}
function back2ek(){
	Close();
	if (opener.execScript) {
		opener.execScript("<%=request.getParameter("back2ek")%>"); //for IE
	} else {
		eval("self.opener.<%=request.getParameter("back2ek")%>"); //for Firefox
	}
}
    </script>
</body>
</html:html>