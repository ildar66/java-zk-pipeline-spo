<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.masterdm.compendium.value.Page"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<%@page import="ru.masterdm.compendium.domain.User"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<html:html>
<head>
<title>Персоны</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup">
	Выбрать пользователя
	<%
	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		boolean hideDepertmentSelect=false;
		Long department = new Long(-1);
		try {  department = new Long(request.getParameter("department")); } catch (Exception e) {} 
		if (request.getParameter("depfilter")!=null && !request.getParameter("depfilter").equals("-1")){
		    try {  department = new Long(request.getParameter("depfilter")); } catch (Exception e) {}
		}

		User filter = new User();
		if (department.intValue()!=-1)filter.setDepartmentID(department.intValue());
		ru.masterdm.compendium.value.Name name = new ru.masterdm.compendium.value.Name();
		if (request.getParameter("name2") != null) name.setFirst(request.getParameter("name2"));
		if (request.getParameter("name1") != null) name.setLast(request.getParameter("name1"));
		if (request.getParameter("name3") != null) name.setMiddle(request.getParameter("name3"));
		filter.setName(name);
		if (request.getParameter("email") != null) filter.setEMail(request.getParameter("email"));
		boolean ptMode = request.getParameter("projectTeamMode") != null 
		    && request.getParameter("projectTeamMode").equals("true");
		boolean etMode = request.getParameter("expertTeamMode") != null 
		    && request.getParameter("expertTeamMode").equals("true");
		String pagenum = request.getParameter("pagenum") == null ? "1" : request.getParameter("pagenum");
		String rolesSQL = "";
		java.util.List<String> roles = TaskHelper.dict().findProjectTeamRoles();
		if (request.getParameter("section")!=null && request.getParameter("section").equals("m")){
		    roles = TaskHelper.dict().findMiddleOfficeRoles();
		}
		for (String roleName : roles) {
			if (rolesSQL!="") rolesSQL += ", ";
			rolesSQL += "'"+roleName+"'";
		}
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
				TaskFacadeLocal.class);
		Long typeP = null;
		if (request.getParameter("mdtaskid") != null && request.getParameter("mdtaskid") != ""
				&& request.getParameter("mdtaskid") != "null") {
			try {
				typeP = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")))
						.getProcess().getProcessType().getIdTypeProcess();
			}
			catch (Exception e) {}
		}
		if (request.getParameter("processTypeId") != null && request.getParameter("processTypeId") != ""
				&& request.getParameter("processTypeId") != "null") {
			try {
				typeP = Long.valueOf(request.getParameter("processTypeId"));
			}
			catch (Exception e) {}
		}
		if (request.getParameter("section") != null && request.getParameter("section").equals("p")) {
			if (typeP != null) {
				if (!TaskHelper.getCurrentUser(request).isStructurator(typeP)) {
					hideDepertmentSelect = true;
					rolesSQL = "''";
					if (TaskHelper.getCurrentUser(request).hasRole(typeP,
							"Руководитель клиентского подразделения")) {
						rolesSQL += ", 'Клиентский менеджер'";
						if (department.intValue() == -1)
							filter.setDepartmentID(TaskHelper.getCurrentUser(request).getDepartment()
									.getIdDepartment().intValue());
					}
					if (TaskHelper.getCurrentUser(request).hasRole(typeP,
							"Руководитель поддерживающего клиентского подразделения")) {
						rolesSQL += ", 'Клиентский менеджер поддерживающего подразделения'";
						if (department.intValue() == -1)
							filter.setDepartmentID(TaskHelper.getCurrentUser(request).getDepartment()
									.getIdDepartment().intValue());
					}
					if (TaskHelper.getCurrentUser(request).hasRole(typeP,
							"Руководитель продуктового подразделения")) {
						rolesSQL += ", 'Продуктовый менеджер'";
						if (department.intValue() == -1)
							filter.setDepartmentID(TaskHelper.getCurrentUser(request).getDepartment()
									.getIdDepartment().intValue());
					}
				}
			}
		}
		boolean edCond = request.getParameter("formName") != null
				&& request.getParameter("formName").equals("editConditions");
		if (edCond) {
			rolesSQL = "''";
			if (request.getParameter("section") != null
					&& request.getParameter("section").equals("structMO"))
				rolesSQL = "'Руководитель структуратора (за МО)', 'Структуратор (за МО)'";
			if (request.getParameter("section") != null
					&& request.getParameter("section").equals("struct"))
				rolesSQL = "'Руководитель структуратора', 'Структуратор'";
		}
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
				.getActionProcessor("Compendium");
		Page p = compenduim.findUserPage(filter, 15 * (new Integer(pagenum) - 1), 15, "u.SURNAME",
				(ptMode || edCond) ? rolesSQL : null, typeP);
%><br />
	<form action="" id="listForm">
		<div>
			<%if(request.getParameter("department")==null 
	    || request.getParameter("department").equals("-1") || request.getParameter("department").equals("null")){%>
			<div <%if(hideDepertmentSelect){ %> style="display: none" <%} %>>
				Подразделение: <select name="depfilter"><option value="-1">все
						<%ArrayList<Long> go = new ArrayList<Long>();
	go.add(1l);
	for(ru.md.pup.dbobjects.DepartmentJPA d : pupFacadeLocal.findDepartmentsForUser(typeP,rolesSQL)){
	%>
					
					<option value="<%=d.getIdDepartment() %>"
						<%=request.getParameter("depfilter")!=null&&request.getParameter("depfilter").equals(d.getIdDepartment().toString())?"selected":"" %>
					>
						<%=d.getShortName() %>
						<%} %>
					</select><br />
			</div>
			<%} %>
			Фамилия <input type="text" name="name1" id="name1" class="text" style="width: 522px;"
				title="Введите часть названия и нажмите Enter"
				value='<%=request.getParameter("name1") == null ? "" : request.getParameter("name1").replaceAll("\"","&quot;")%>'
			> <br /> Имя <input type="text" name="name2" class="text" style="width: 150px;"
				value="<%=request.getParameter("name2") == null ? "" : request.getParameter("name2")%>"
			> Отчество <input type="text" name="name3" class="text" style="width: 150px;"
				value="<%=request.getParameter("name3") == null ? "" : request.getParameter("name3")%>"
			> email <input type="text" name="email" class="text" style="width: 150px;"
				value="<%=request.getParameter("email") == null ? "" : request.getParameter("email")%>"
			> <br />
			<button type="submit">найти</button>
			<a
				href="popup_users.jsp?formName=<%=request.getParameter("formName")%>&fieldNames=<%=request.getParameter("fieldNames")%>&projectTeamMode=<%=request.getParameter("projectTeamMode")%>&onMySelect=<%=request.getParameter("onMySelect")%>&department=<%=request.getParameter("department")%>&reportmode=true&mdtaskid=<%=request.getParameter("mdtaskid")%>&processTypeId=<%=request.getParameter("processTypeId")%>&section=<%=request.getParameter("section")%>"
			>Очистить форму поиска</a>
		</div>
		<div class="paging">
			Найдено <b><%=p.getTotalCount()%></b>, показывается по 15.<%
	    int pageCount = 1 + (p.getTotalCount() - 1) / 15;
	        int curr = new Integer(pagenum).intValue();
	        String pagename = (curr - 1) * 15 + 1 + "&#150;" + 15 * curr;
	        String link = 
	        "popup_users.jsp?name1=" + (request.getParameter("name1") == null ? "" : request.getParameter("name1"))
	                + "&name2=" + (request.getParameter("name2") == null ? "" : request.getParameter("name2"))
	                + "&email=" + (request.getParameter("email") == null ? "" : request.getParameter("email"))
	                + "&reportmode=true"
	                + "&formName=" + request.getParameter("formName")
	                + "&section=" + request.getParameter("section")
	                + "&mdtaskid=" + request.getParameter("mdtaskid")
	                + "&processTypeId=" + request.getParameter("processTypeId")
	                + "&fieldNames=" + request.getParameter("fieldNames")
	                + "&department=" + request.getParameter("department")
	                + "&depfilter=" + request.getParameter("depfilter")
	                + "&onMySelect=" + request.getParameter("onMySelect")
	                + "&projectTeamMode=" + request.getParameter("projectTeamMode")
	                + "&pagenum=";
	        if (curr > 1) {
	%>
			<a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%><%=curr - 1%>">&larr;</a>
			<%
	     }
	 %>
			<span class="selected"><%=pagename%></span>
			<%
	     if (curr < pageCount) {
	 %>
			<a onclick="document.body.style.cursor='wait'" class="button" href="<%=link%><%=curr + 1%>">&rarr;</a>
			<%
	     }
	 %>
		</div>
		<table class="regular">
			<thead>
				<tr>
					<th>Фамилия</th>
					<th>Имя</th>
					<th>Отчество</th>
					<th>email</th>
				</tr>
			</thead>
			<tbody>
				<%
	            for (int i = 0; i < p.getSize(); i++) {
	            ru.masterdm.compendium.custom.UserTO user = (ru.masterdm.compendium.custom.UserTO)p.getList().get(i);
	            String js = "javascript:Go('"+user.getVo().getId()+"|"+user.getVo().getName().getFIO().replaceAll("\"","&quot;")+"')"; 
	        %>
				<tr>
					<td><a href="<%=js%>"><%=user.getVo().getName().getLast()%></a></td>
					<td><%=user.getVo().getName().getFirst() %></td>
					<td><%=user.getVo().getName().getMiddle() %></td>
					<td><%=user.getVo().getEMail() %></td>
					<%if(ptMode){ %><td><button onclick="<%=js%>">добавить в команду</button></td>
					<%} %>
				</tr>
				<%} %>
			</tbody>
		</table>
		<input type="hidden" name="formName" id="formName" value="<%=request.getParameter("formName")%>">
		<input type="hidden" name="projectTeamMode" value="<%=request.getParameter("projectTeamMode")%>">
		<input type="hidden" name="section" value="<%=request.getParameter("section")%>"> <input
			type="hidden" name="mdtaskid" value="<%=request.getParameter("mdtaskid")%>"
		> <input type="hidden" name="processTypeId" value="<%=request.getParameter("processTypeId")%>">
		<input type="hidden" name="reportmode" value="<%=request.getParameter("reportmode")%>"> <input
			type="hidden" name="department" value="<%=request.getParameter("department")%>"
		> <input type="hidden" value="<%=request.getParameter("fieldNames")%>" name="fieldNames"
			id="fieldNames"
		> <input type="hidden" value="<%=request.getParameter("fieldNameIdUser")%>"
			name="fieldNameIdUser" id="fieldNameIdUser"
		> <input type="hidden" value="<%=request.getParameter("onMySelect")%>" name="onMySelect"
			id="onMySelect"
		>
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
	    
	    // set id_user field of the form
	    var idUser = document.getElementById('fieldNameIdUser');
		try {
			outform[idUser.value].value = values[0];
		} catch (Err) {}
	    
	    Close();
	    onMySelect();
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
</script>
</body>
</html:html>