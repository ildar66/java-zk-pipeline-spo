<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="ru.masterdm.spo.utils.SBeanLocator"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@ page import="ru.masterdm.spo.utils.Formatter" %>
<html:html>
<head>
<title>Выбор лимита/сублимита</title>
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
<script type="text/javascript" src="scripts/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="scripts/tiny_mce/settings.js"></script>
<script type="text/javascript" src="scripts/form.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup">
<% long tstart=System.currentTimeMillis();%>
<h1>Выбор лимита/сублимита</h1>
<form name="variables">
<div>
<%String dialogProperty = "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes";
String inn=request.getParameter("inn");
String orgid=request.getParameter("org");
String orgname="Не выбрано";
if(orgid!=null){
    try{
        orgname=SBeanLocator.singleton().compendium().getOrgById(orgid).getName();
		if(orgname == null){orgname="Не выбрано";}
    } catch (Exception e){}
}
	orgname = Formatter.strWeb(orgname);
 %>
ИНН <input value="<%=inn==null?"":inn %>" name="inn" id="inn"> 
Контрагент <input value="<%=orgname %>" readonly="readonly" id="orgname"
onclick='return openDialog("popup_org.jsp?formName=variables&ek=all&fieldNames=org|orgname|org&mode=inlimit", "organizationLookupList", "<%= dialogProperty%>");'> 
<input type="submit" value="Найти"> 
<input type="submit" value="Очистить фильтр" onclick="$('#inn').val('');">
</div>
<%
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
java.util.List<Long> list = taskFacade.findLimitByOrg(orgid, inn);
if(list.size()>0){%>
<jsp:include page="frame/inLimitTable.jsp"></jsp:include>
<script type="text/javascript">
	parent_class = "";
	tree_level = 0;
	selectLimitMode = true;
	currentSublimit = '';
	sublimitEditMode = false;
	$(document).ready(function() {
		<%for(Long mdtaskid : list){%>
		$.post('ajax/limittree.do', {id: '<%=mdtaskid%>',child:'false'},addinLimitRow);
		<%}%>
	});
</script>
<%}else{  %>
Не найдено ни одного лимита для выбранных условий
<%} %>
<input type="hidden" id="org" value="<%=request.getParameter("org")%>" name="org">
<input type="hidden" id="scriptParam" value="<%=request.getParameter("script")%>" name="script">
<input type="hidden" id="formNameParam" value="<%=request.getParameter("formName")%>" name="formName">
<input type="hidden" id="fieldNamesParam" value="<%=request.getParameter("fieldNames")%>" name="fieldNames">
</form>
	<%
				Long loadTime = System.currentTimeMillis()-tstart;
				out.println("<div style=\"color:gray\"><em>Время формирования страницы (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em></div>");
		if (request.getParameter("org")!=null){%><div style="color:gray"><em>Для контрагента ID=<%=request.getParameter("org")%></em></div><%}%>
</body>
</html:html>