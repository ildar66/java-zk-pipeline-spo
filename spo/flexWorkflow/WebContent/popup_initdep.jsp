<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page import="ru.masterdm.spo.utils.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.persistence.DepartmentMapper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<% long tstart=System.currentTimeMillis();%>
<html:html>
<head>
<title>Изменение инициирующего подразделения</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup" onload="document.getElementById('department').focus()">
<div align="left" style="font-size: 22px;"><b>Изменение инициирующего подразделения</b></div>
<% 
String idMdtask = request.getParameter("idmdtask");
String mainLink = "popup_initdep.jsp?idmdtask=" + (request.getParameter("idmdtask") == null ? "" : request.getParameter("idmdtask"));
Map<String,Object> filter =  new HashMap<String,Object>();
if (request.getParameter("idmdtask") != null && !request.getParameter("idmdtask").isEmpty())
	filter.put("idmdtask", request.getParameter("idmdtask"));
%>
<form action="" id="listForm">
<div>
<br/>
<input type="hidden" name="idmdtask" id="idmdtask"	value="<%=request.getParameter("idmdtask")%>">
Подразделение <input type="text" name="department" id="department"
	class="text" style="width: 522px;"
	title="Введите часть названия и нажмите Enter"
	value='<%=request.getParameter("department") == null ? "" : request.getParameter("department").replaceAll("\"","")%>'>
<br />
  <button type="submit">найти</button>
<a href="popup_initdep.jsp?idmdtask=<%=idMdtask%>">Очистить
форму поиска</a>

</div>

<div class="paging">
	<table  style="margin: auto;width: auto;">
	<tr style="font-size: 15px;">      
<%
	int pageSize = 15;
	List<String> filterNames = Arrays.asList("department");
	for (String filterName:filterNames)
		if (request.getParameter(filterName) != null && !request.getParameter(filterName).isEmpty()) filter.put(filterName, request.getParameter(filterName));
	
	String pagenum = request.getParameter("pagenum") == null ? "1" : request.getParameter("pagenum");
	//list
	List<ru.md.domain.Department> list = SBeanLocator.singleton().getDepartmentMapper().getInitialDepartmentPage(filter, pageSize * (new Integer(pagenum) - 1), pageSize);
	//count
	Long totalCount = SBeanLocator.singleton().getDepartmentMapper().getInitialDepartmentCount(filter);
%>
	<td> Найдено <b><%=totalCount%></b>, показывается по <%=pageSize%>.&nbsp;</td>
<%
    Long pageCount = 1 + (totalCount - 1) / pageSize;
        int curr = new Integer(pagenum).intValue();
        String pagename = (curr - 1) * pageSize + 1 + "&#150;" + pageSize * curr;
        String link = "popup_initdep.jsp?idmdtask=" + (request.getParameter("idmdtask") == null ? "" : request.getParameter("idmdtask"))
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

<table class="regular">
	<thead>
		<tr>
			<th>Подразделение</th>
		</tr>
	</thead>
	<tbody>
		<%
		    for (ru.md.domain.Department dep : list) {
		%>
		<tr>
			<td>
				<a href="javascript:Go('<%=dep.getId()%>', '<%=Formatter.strWeb(dep.getName())%>')">
					<%=dep.getName()%>
				</a>
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
function Go(departmentId, departmentName) {
	var thisform = document.forms['listForm'];
    var outform = window.opener.document.forms['variables'];
    if (outform != null) {
        outform['selectedDeartmentId'].value = departmentId;
        outform['selectedDeartmentName'].value = departmentName;
    }
    Close();
    onMySelect();
}
function onMySelect(){
        if (opener.execScript) {
            opener.execScript('onSelectDepartment()'); //for IE
        } else {
           eval('self.opener.onSelectDepartment()'); //for Firefox
        }
}
</script>

</body>
</html:html>