<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<%@ page import="java.util.List" %>

<html:html>
<head>
<title>Выбор клиентской записи вручную</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup">
<h1>Выбор клиентской записи вручную</h1>
<form action="" id="listForm">
<input type="hidden" name="ek" value="<%=request.getParameter("ek")%>">
<input type="hidden" name="dep_name" value="<%=request.getParameter("dep_name")%>">
<input type="hidden" name="place2" value="<%=request.getParameter("place2")%>">
	<% String navigation=request.getParameter("navigation");
	if(navigation==null){navigation="0";}%>
<input type="hidden" name="navigation" id="navigation" value="<%=navigation%>">
	<%//начитать сам список
		int curr = Integer.parseInt(navigation);
		String place2name = "";
		if(request.getParameter("place2")!=null && !request.getParameter("place2").isEmpty()
				&&!request.getParameter("place2").equals("0") ) {
			place2name =ru.masterdm.spo.utils.SBeanLocator.singleton().getDepartmentMapper().getById(Long.valueOf(request.getParameter("place2"))).getCrmName();
		}
		List<OrgJPA> list = TaskHelper.dict().findOrganization4EK(request.getParameter("dep_name"),
				request.getParameter("ek"),place2name).getLeft();
        long right = curr * 10 + 10;
		if(right>list.size())right=list.size();
	%>
	<div class="paging">
	Всего: <%=list.size() %>,
	<%if(curr>0){ %><a class="button" onClick="$('#navigation').val('<%=(curr-1) %>');$('#listForm').submit()" href="#">&larr;</a><%} %>
	<%if(list.size()>0){ %><%=(curr * 10 + 1 + " &#150; " + right) %><%} %>
	<%if((curr+1)*10 < list.size()){ %><a class="button" onClick="$('#navigation').val('<%=(curr+1) %>');$('#listForm').submit()" href="#">&rarr;</a><%} %>
</div>
<table class="regular">
	<thead>
		<tr>
			<th>Название</th>
			<th>ИНН</th>
			<th>КПП</th>
			<th>Категория клиента</th>
			<th>Филиал/ГО</th>
			<th>Обслуживающее подразделение</th>
		</tr>
	</thead>
	<tbody>
	    <%
			for (int i= curr * 10;i<right;i++) {
				OrgJPA org = list.get(i);
	    %>
		<tr>
			<td><a
				href="javascript:Go('<%=Formatter.strWeb(org.getOrganizationName())%>','<%=org.getId()%>')"><%=org.getOrganizationName()%></a>
			</td>
			<td><%=org.getInn()%></td>
			<td><%=org.getKpp()%></td>
			<td><%=org.getClientType()%></td>
			<td><%=org.getDepartment()==null?"":org.getDepartment()%></td>
			<td><%=org.getDivision()==null?"":org.getDivision()%></td>
		</tr>
		<%
		    }
		%>
	</tbody>
</table>
</form>
<script language="javascript">
function Go(nameOrg,idOrg) {
	var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if(outform != null) {
        outform['kz'].value = idOrg;
        outform['kz2'].value = nameOrg;
    }
    Close();
    onMySelect();
}
function Close(){
    var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if(outform != null) {
        document.body.style.cursor="wait";
        window.opener.focus();
    }
    window.close();
}
function onMySelect(){
        if (opener.execScript) {
            opener.execScript('showKZ()'); //for IE
        } else {
           eval('self.opener.showKZ()'); //for Firefox
        }
}
    </script>
</body>
</html:html>