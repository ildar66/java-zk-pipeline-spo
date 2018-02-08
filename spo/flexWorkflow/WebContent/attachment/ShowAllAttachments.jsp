<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.custom.AttachmentDisplay"%>
<script type="text/javascript">
	function returnListOfAddedFiles() {
		var list = window.open('/<%=ApplProperties.getwebcontextFWF() %>/uploadFiles.do','_blank');
		// ну и типа делаем что-то с этим самым списком...		
	}
	
	// проверка подписи всех выбранных (флажки) файлов
	function checkSignSelectedFiles() 	{
		var fileUrl, signature, checkResult;
		var urlColl, signColl, unidColl;
		try {
			//Get unids collection and other collection with params
			unidColl = document.getElementsByName('unid');
			urlColl = document.getElementsByName('linkFileType');
			signColl = document.getElementsByName('signature');
			//isFiles in collection
			if (unidColl) {
				for(i=0; i<unidColl.length; i++) {
					//file is checked (selected) for verification
					if (unidColl[i].checked) {
						fileUrl = urlColl[i].href;
						signature = signColl[i].value;
						checkResult = checkSignFile(fileUrl, signature);
						
						//Show result of verification for file
						if (checkResult==true)
							//Correct files
							unidColl[i].parentNode.style.backgroundColor="green"
						else
							//Uncorrect files
							unidColl[i].parentNode.style.backgroundColor="red"
					}
				}
			}
		}
		catch(err)	{ alert('ERROR in signSelectedFiles(): '+err.description); }
	}
	//Данная функция производит верификацию подписи
	//Входные параметры
	//fileUrl - url подписанного файла
	//signature - значение подписи файла
	//@return (true/false)- результат верификации подписи файла
	function checkSignFile(fileUrl, signature) {
		try {
			if (signature == '') {
				alert('Подпись отсутствует');
				return false;
			}
			return mdSignDataObject.certVerifySignURL(signature, fileUrl);
		} catch(err) {
			alert("Ошибка при проверке электронной подписи.\n\n'" + ((err.description == undefined) ? err : err.description) + "'\n, stack '" + err.stack + "'");
		}
		return false;
	}
</script>

<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>ShowAllAttachments</title>
	<link rel="stylesheet" href="/<%=ApplProperties.getwebcontextFWF() %>/style/style.css" />

</head>
<body class="fixed">
<!--  Проверка на наличие сообщений -->
	<logic:present scope="request" name="messages">
		<div onclick="this.style.display='none'">
			<div class="popup_cover"></div>
			<div class="popup">
				<!--  Цикл по каждому сообщению -->
				<logic:iterate id="message" scope="request" name="messages">
					<bean:write name="message" property="body"/>
					<br/>
				</logic:iterate>
			</div>
		</div>
	</logic:present>
<div class="all">
	<html:errors></html:errors>
	<html:form action="/AttachmentList" method="post" enctype="multipart/form-data">
		<%
			boolean editMode = request.getParameter("editmode")==null||request.getParameter("editmode").equalsIgnoreCase("true");
			boolean personMode =request.getParameter("type")!=null&&request.getParameter("type").equals("2");
		%>
		<button style="display:none" onclick="window.open('/<%=ApplProperties.getwebcontextFWF() %>/uploadFiles.do','_blank')">Добавить</button>
		<button id="idRemoveBtn" style="display:none" onclick="document.getElementById('action').value='remove';this.parentElement.submit();">Удалить</button>
		<button id="idRequestBtn" style="display:none" onclick="document.getElementById('action').value='request';this.parentElement.submit();"/>Запросить</button>
		<button id="idAcceptBtn" style="display:none" onclick="document.getElementById('action').value='accept';this.parentElement.submit();">Утвердить</button>
		<button id="idVerifyBtn" style="display:none" onclick="checkSignSelectedFiles();">Проверить</button>

		<html:hidden property="id" />
		<html:hidden property="type" />
		<input type="hidden" id="action" name="action" />

		<table class="regular" id="attachtable">
			<thead>
				<tr>
					<%if(editMode){ %><td style="width:5%"></td><%} %>
					<td style="width:15%">Группа</td>
					<td style="width:20%">Наименование документа</td>
					<td style="width:15%">Наименование файла</td>
					<%if(!personMode){ %><td style="width:5%">Передается на Кредитный Комитет</td><%} %>
					<td style="width:10%">Срок действия</td>
					<td style="width:15%">Добавил</td>
					<td style="width:15%">Утвердил</td>
				</tr>
			</thead>
			<tbody>
				<logic:present scope="request" name="attachments">
					<logic:iterate id="row" scope="request" name="attachments">
						<tr>
							<%
							if(editMode) {
							%>
								<td>
									<input type="checkbox" name="unid" value='<bean:write name="row" property="unid"></bean:write>'/>
									<input type="hidden" name="signature" value="<bean:write name="row" property="signature"/>" />
								</td>
							<%
							} 
							%>
							<td><bean:write name="row" property="filegroup"/></td>
							<td>
								<a name="linkFileType" href="<bean:write name="row" property="downloadLink"/>" target="_blank">
									<bean:write name="row" property="filetype"/>
								</a>
							</td>
							<td>
								<a name="linkFileName" href="<bean:write name="row" property="downloadLink"/>" target="_blank">
									<bean:write name="row" property="filename"/>
								</a>
							</td>
							<%if(!personMode){ %><td><input <%if(!editMode){ %>disabled<%} %> type="checkbox"  <bean:write name="row" property="forCCChecked"></bean:write> name="fileforCC" value="<bean:write name="row" property="unid"></bean:write>" /><%} %>
							</td>
							<td><bean:write name="row" format="dd.MM.yyyy" property="dateOfExpiration"/></td>
							<td><bean:write name="row" property="whoAddName"/>, <bean:write name="row" format="dd.MM.yyyy" property="dateOfAddition"/></td>
							<td><bean:write name="row" property="whoAcceptName"/>, <bean:write name="row" format="dd.MM.yyyy" property="dateOfAccept"/></td>
						</tr>
					</logic:iterate>
				</logic:present>
			</tbody>
		</table>
	</html:form>
</div>
</body>
</html:html>
