<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.masterdm.compendium.value.Page" %>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.spo.Person"%>
<%@page import="com.vtb.util.Formatter"%>


<html:html>
<head>
	<title>Выбор физического лица</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
	<link rel="stylesheet" href="style/style.css" />
	<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup" onload="document.getElementById('filter').focus()">
	<h1>Выбор физического лица</h1>
	<form action="" id="listForm">
		<div class="search">
			<input type="text" name="filter" id="filter" class="text" style="width: 500px;"
				title="Введите часть фамилии и нажмите Enter"
				value='<%=request.getParameter("filter")==null?"":request.getParameter("filter") %>'>
			<button type="submit">найти</button>
			<br><a href="popup_persons.jsp?formName=<%=request.getParameter("formName") %>&fieldNames=<%=request.getParameter("fieldNames") %>&onMySelect=<%=request.getParameter("onMySelect") %>">Очистить форму поиска</a>
		</div>
		<div class="paging">
			<%
			CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
			Person filter = new Person();
			if(request.getParameter("filter")!=null)filter.setLastName(request.getParameter("filter"));
			String pagenum = request.getParameter("pagenum")==null?"1":request.getParameter("pagenum");
			Page p = compenduimSPO.findPersonPage(filter,20*(new Integer(pagenum)-1),20,"c.lastName");
			%>
			Найдено <b><%=p.getTotalCount() %></b>, показывается по 20.
			<%
			int pageCount =1+(p.getTotalCount()-1)/ 20;
            int curr = new Integer(pagenum).intValue();
            String pagename=(curr-1)*20+1+"&#150;"+20*curr;
            String link="popup_persons.jsp?filter="+(request.getParameter("filter")==null?"":request.getParameter("filter"))+
                    "&formName="+request.getParameter("formName")+"&fieldNames="+request.getParameter("fieldNames")+
                    "&onMySelect="+request.getParameter("onMySelect")+"&pagenum=";
            if(curr>1){
            %>
            <!-- <button onclick="document.body.style.cursor='wait';document.location=<%=link %><%=curr-1 %>">&larr;</button> -->
            <a onclick="document.body.style.cursor='wait'" class="button" href="<%=link %><%=curr-1 %>">&larr;</a>
            <%} %>
            <span class="selected"><%=pagename %></span>
            <%if(curr<pageCount){ %>
            <a onclick="document.body.style.cursor='wait'" class="button" href="<%=link %><%=curr+1 %>">&rarr;</a>
            <%} %>
		</div>
		<table class="regular">
		<thead>
		<tr>
        <th>Имя</th>
        <th>Дата рождения</th>
        <th>Документ, удостоверяющий личность</th>
		</tr>
		</thead>
		<tbody>
			<%
			for(int i=0;i<p.getSize();i++){
				Person person = (Person) p.getList().get(i);
				String name = person.getLastName();
				if(person.getName().trim().length()>0)name+=" "+person.getName().trim().substring(0,1)+".";
				if(person.getMiddleName().trim().length()>0)name+=person.getMiddleName().trim().substring(0,1)+".";
				%>
				<tr><td>
					<a href="javascript:Go('|<%=name %>|<%=person.getId().toString() %>')"><%=name %></a>
				</td>
                <td><%=(person.getBirthday() != null) ? Formatter.format(person.getBirthday()) : ""%></td>
                <td><%=Formatter.str(person.getIdentityDocType().getName()) %> 
                серия <%=Formatter.str(person.getIdentityDocSeries())%>
                номер <%=Formatter.str(person.getIdentityDocNumber())%>
                </td>
				</tr>
			<%} %>
		</tbody>
		</table>
		<input type="hidden" value="<%=request.getParameter("fieldNames") %>" name="fieldNames" id="fieldNames">
		<input type="hidden" value="<%=request.getParameter("onMySelect") %>" name="onMySelect" id="onMySelect">
		<input type="hidden" name="formName" id="formName" value="<%=request.getParameter("formName")%>">
	</form>
	<script language="javascript">
function Go(strval) {
	var thisform = document.forms['listForm'];
	var outform = window.opener.document.forms[thisform.formName.value];
	if(outform != null)
	{
		var names = thisform.fieldNames.value.split('|'), values = strval.split('|');
		for(var i in names) {
			if(names[i].length > 0)
				outform[names[i]].value = unescape(values[i]);
		}
	}
	Close();
	onMySelect();
}
function Close(){
	var thisform = document.forms[0];
	var outform = window.opener.document.forms[0];
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
</script>
</body>
</html:html>