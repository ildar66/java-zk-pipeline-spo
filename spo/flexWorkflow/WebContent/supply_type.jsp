<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.dict.dbobjects.SupplyTypeJPA" %>
<%@page import="java.util.List" %>
<html:html>
<head>
<title>supply_type</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
Выбрать группу обеспечения:
<table class="pane" border="1" style="width: 750px;">
<THEAD><TR><td>группа обеспечения</td></TR></THEAD>
<TBODY>
<tr><td onClick="document.getElementById(document.getElementById('supplyid').value).value='-1';document.getElementById('sp'+document.getElementById('supplyid').value).innerHTML=document.getElementById('spansupply0').innerHTML;$.fancybox.close()"><span id="spansupply0">не выбрана</span></td></tr>
<%
List<SupplyTypeJPA> allst = ru.md.helper.TaskHelper.dict().findSupplyType();
for(int i=0;i<allst.size();i++){
SupplyTypeJPA supplyType=(SupplyTypeJPA)allst.get(i);
%>
<tr><td onClick="document.getElementById(document.getElementById('supplyid').value).value='<%=supplyType.getId().toString() %>';document.getElementById('sp'+document.getElementById('supplyid').value).innerHTML=document.getElementById('spansupplyvalue<%=supplyType.getId().toString() %>').innerHTML;$.fancybox.close()"><span id="spansupplyvalue<%=supplyType.getId().toString() %>"><%=supplyType.getName() %></span></td></tr>
<%} %>
</TBODY>
</table>
</body>
</html:html>