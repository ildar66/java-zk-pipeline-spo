<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page
	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="com.vtb.value.BeanKeys"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Активные операции</title>
	<link rel="stylesheet" href="style/style.css" />
	<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
</head>
<body>
<jsp:include page="header_and_menu.jsp" />
				<h1>Активные операции</h1>
				<form action="reportPrintFormRenderAction.do" method="post" name="mainform" onload="showhide2(1);" target="_blank">
					<input type="hidden" name="<%=ReportTemplateParams.REPORT_MARK.getValue()%>" value="<bean:write name="<%=BeanKeys.REPORT_FILTER_FILE%>" />" />
					<table class="form">
						<tr>
							<th>
								Выбрать
							</th>
							<td>
								<input CHECKED type="radio" name="notused" value="off" onClick="showhide2(1);">номер заявки</input>
								<input type="radio" name="notused" value="on" onClick="showhide2(0);">тип процесса</input>
							</td>
						</tr>
						<tr id="showIdClaim">
							<th>
								Номер заявки
							</th>
							<td>
								<input type="text" name="<%=ReportTemplateParams.ID_CLAIM.getValue()%>" size="31">
							</td>
						</tr>
						<tr id="showTypeProcess">
							<th>
								Тип процесса
							</th>
							<td>
								<select name="<%=ReportTemplateParams.ID_TYPE_PROCESS.getValue()%>" disabled="disabled">
									<logic:iterate id="p" name="<%=BeanKeys.REPORT_PROCESSES%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								Подразделение
							</th>
							<td>
								<select name="<%=ReportTemplateParams.ID_DEPARTMENT.getValue()%>" 
								id="<%=ReportTemplateParams.ID_DEPARTMENT.getValue()%>" 
								WIDTH="450" STYLE="width: 450px">
									<logic:iterate id="p" name="<%=BeanKeys.REPORT_FILTER_DEPARMENTS%>" type="java.util.Map.Entry" scope="request">
										<option value="<bean:write name="p" property="key"/>"><bean:write name="p" property="value"/></option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr name="showOperations">
							<th>
								Показывать операции
							</th>
							<td>
								<input type="radio" id="corDeps1" name="<%=ReportTemplateParams.CORRRESPONDING_DEPS.getValue()%>" value="off" onClick="showhide();">только по данному подразделению</input>
								<input CHECKED type="radio" id="corDeps2" name="<%=ReportTemplateParams.CORRRESPONDING_DEPS.getValue()%>" value="on" onClick="showhide();">по всем подразделениям, где выполняется заявка</input>
							</td>
						</tr>
						<tr id="showUser">
							<th>
								Пользователь
							</th>
							<td>
								<input type="hidden" name="<%=ReportTemplateParams.USER_ID.getValue()%>" value=""/>
								<input
                                onclick="window.open('popup_users.jsp?reportmode=true&formName=mainform&fieldNames=userId|userFIO&department='+$('#p_idDepartment').val(), 'org','top=100, left=100, width=800, height=710');" 
                                type="text" class="text" readonly="true" name="userFIO" 
                                value="">  
							</td>
						</tr>
						<tr>
							<th>
								Просроченные операции
							</th>
							<td>
								<input type="radio" name="<%=ReportTemplateParams.IS_DELINQUENCY.getValue()%>" value="1">Просроченные</input>
								<input type="radio" name="<%=ReportTemplateParams.IS_DELINQUENCY.getValue()%>" value="2">Непросроченные</input>
								<input CHECKED type="radio" name="<%=ReportTemplateParams.IS_DELINQUENCY.getValue()%>" value="-1">Все</input>
							</td>
						</tr>  
						<tr>
							<th>
								Формат отчета
							</th>
							<td>
								<select name="<%=ReportTemplateParams.REPORT_FORMAT.getValue()%>">
									<option selected value="html">Отчет в WEB</option>
									<option value="doc">Отчет в MS Word</option>
								</select>
							</td>
						</tr>
						<tr>
							<th /><td><button type="submit">Сформировать отчет</button></td>
						</tr>
					</table>
				</form>
<jsp:include flush="true" page="footer.jsp" />
</body>

<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
<script>

// Показать \ спрятать отображение списка пользователей.
	function showhide ()
	{
		var style = document.getElementById("showUser").style
		if (style.display == "none")
			style.display = "";
		else {
			style.display = "none";
			document.getElementById("userId").value = "";
		}
	}	
	
// Показать \ спрятать отображение списка типов процессов.
	function showhide2(showflag)
	{
		var notused = document.getElementById("notused");
		var typeProcess = document.getElementById("p_idTypeProcess");
		var idClaim = document.getElementById("p_idClaim");
		var idDepartment = document.getElementById("p_idDepartment");
		var correspondingDeps = document.getElementById("correspondingDeps");
		var userFIO = document.getElementById("userFIO");
		
		var corDeps1 = document.getElementById("corDeps1");
		var corDeps2 = document.getElementById("corDeps2");
		//var showOperations = document.getElementById("showOperations");				
		
		if (showflag == "1")		{
		// был активен тип процесса
			typeProcess.disabled = true;
		//	idDepartment.disabled = true;
						
			idClaim.disabled = false; 
			idClaim.value = "";
			//correspondingDeps.value = "on";
			notused.value = "on";
									
		}	
		
		else {
			// был активен номер заявки
			idClaim.disabled = true;			
			idClaim.value = "";			
			//correspondingDeps.value = "on";			
			//userFIO.value = "";
			notused.value = "off";		
			
			typeProcess.disabled = false;	
		}
	}
</script>
</html:html>