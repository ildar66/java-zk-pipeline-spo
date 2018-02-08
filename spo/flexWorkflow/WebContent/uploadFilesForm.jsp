<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.spo.util.Config"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import = "com.vtb.value.*"%> 

<html:html>
<head>
	<title>Добавление документов</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<script type="text/javascript" src="scripts/form/nodeIterator.js"></script>
	<script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body class="soria" style="width:768px">
	<input type="hidden" name="id_appl" value="<bean:write name="UploadFilesForm" property="id"/>"/>
	<input type="hidden" name="id_group" value="<bean:write name="UploadFilesForm" property="type"/>"/>

	<button id="btnUpload" onclick="doUpload()">Прикрепить</button>&nbsp;
	<button id="btnClose" onclick="doClose(false)">Закрыть</button>
	<table class="regular" id="tblFiles">
		<tr>
			<th style="width:463px">Наименование</th>
			<th style="width:80px">Срок действия</th>
			<th style="width:205px">Файл</th>
		</tr>

		<%
			int n = 1;
			int i = 0; 
			ArrayList fileTypes = (ArrayList)request.getAttribute(BeanKeys.FILE_TYPES_NAME);
		%>
		<tr class="separator">
			<td colspan=3>
				Другой документ
			</td>
		</tr>
		<tr tabIndex="<%=n%>">
			<td align="left"  style="width:463px">
				<input type="hidden" name="fileGroupId" id="filegroupid" value="">
				<input type="hidden" name="fileGroup" id="filegroup" value="">
				<input type="hidden" name="fileIdType" id="fileIdType" value="0">
				<textarea style="width:98%" name="fileType" id="filetype"></textarea>
			</td>
			<td style="width:80px" align="center">
				<input type="text" name="file_expdate" id="fileExpDate_<%=n%>" value="" onFocus="displayCalendarWrapper('fileExpDate_<%=n%>', '', false); return false;">
			</td>
			<td style="width:205px" style="vertical-align:bottom">
				<iframe id="uploadFrame" name="uploadFrame" tabindex="<%=n++%>" style="width:200px;height:36px" frameborder="0" scrolling="auto" src="/<%=ApplProperties.getwebcontextFWF() %>/attachment/UploadFile.jsp" onload="uploadFile(this)"></iframe>
			</td>
		</tr>
		<logic:iterate id="fileGroup" name="<%=BeanKeys.FILE_GROUPS_LIST %>" type="com.vtb.domain.DocumentGroup" indexId="groupIndexId">
			<%	Integer counter = (Integer) pageContext.getAttribute("groupIndexId"); %>
			<tr>
				<td colspan="3" style="padding:0">
				<table class="pane" id="section_uploadFilesRow<%=groupIndexId.intValue()%>">
					<thead onclick="doSectionMine('uploadFilesRow<%=groupIndexId.intValue()%>');showHideElementsInTable('tblFiles', 'uploadFilesRow<%=groupIndexId.intValue()%>');">
						<tr>
							<td>
								<div align="left">
									<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
									<span><%=fileGroup.getName_document_group() %></span>
								</div>
							</td>
						</tr>
					</thead>
					<tbody style="display:none"></tbody>
				</table>
				</td>
			</tr>
			<logic:iterate id="fileType" collection="<%=fileTypes.get(i++)%>"  type="com.vtb.domain.DocumentsType">
				<tr tabIndex="<%=n%>" class="uploadFilesRow<%=groupIndexId.intValue()%>" style="display:none">
					<td bgcolor="white" align="left" style="width:463px">
						<input type="hidden" name="fileGroupId" id="filegroupid" value="<%=fileGroup.getId_document_group() %>">
						<input type="hidden" name="fileGroup" id="filegroup" value="<%=fileGroup.getName_document_group() %>">
						<input type="hidden" name="fileType" id="filetype" value="<%=fileType.toString()%>">
						<input type="hidden" name="fileIdType" id="fileIdType" value="<%=fileType.getId()%>">
						<%=fileType.toString()%>
					</td>
					<td style="width:80px" align="center">
						<input type="text" name="file_expdate" id="fileExpDate_<%=n%>" value="" onFocus="displayCalendarWrapper('fileExpDate_<%=n%>', '', false); return false;">
					</td>
					<td style="vertical-align:bottom; width:205px">
						<iframe id="uploadFrame" name="uploadFrame" tabindex="<%=n++%>" style="width:200px;height:36px" frameborder="0" scrolling="auto" src="/<%=ApplProperties.getwebcontextFWF() %>/attachment/UploadFile.jsp" onload="uploadFile(this)"></iframe>
					</td>
				</tr>
			</logic:iterate>
		</logic:iterate>
	</table>
</body>
<script type="text/javascript">
	function signFile(fileName) {
		try {
			mdSignDataObject.initSign(true, false, cryptoIssuers);
		} catch (err) {
			return "err";
		}
		try {
			var sign = mdSignDataObject.certSignFile(fileName);
			if (sign == "") {
				alert('Файл не подписан!');
			}
			return sign;
		} catch (err) {
			alert("Ошибка при установке электронной подписи.\nЭлектронная подпись не будет поставлена.\n\n'" + ((err.description == undefined) ? err : err.description) + "', stack '" + err.stack + "'");
			return "err";
		}
	}
	function File(doc){
		try {
			var node, doc
			this.doc=doc
			this.action = null
			this.id='<bean:write name="UploadFilesForm" property="id"/>'
			this.group='<bean:write name="UploadFilesForm" property="type"/>'
			this.type=''
			this.fileGroup=''
			this.fileGroupId=''
			this.expDate=''
			this.signature=''
			this.fileIdType=''
			node=doc.all('uploadControl') || doc.all('attachment')
			this.filePath = getValue(node) || ''
		} catch(Err) {
		}

		// fills FormUploadFile object with data. This form object will be passed in the UploadFileWAS.jsp as a from result to ActionUploadFile action  
		this.prepare = function(){
			try {
				var node 

				node = getNode(this.doc, 'fileGroupId') || this.doc.all('fileGroupId')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика (fileGroupId)!'))
				setValue(node, this.fileGroupId)

				node = getNode(this.doc, 'fileGroup') || this.doc.all('fileGroup')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика (fileGroup)!'))
				setValue(node, this.fileGroup)
				
				node = getNode(this.doc, 'filetype') || this.doc.all('filetype')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#1]!'))
				setValue(node, this.type)

				node = getNode(this.doc, 'id_appl') || this.doc.all('id_appl')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#2]!'))
				setValue(node, this.id)

				node = getNode(this.doc, 'id_group') || this.doc.all('id_group')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#3]!'))
				setValue(node, this.group)

				node = getNode(this.doc, 'file_expdate') || this.doc.all('file_expdate')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#4]!'))
				setValue(node, this.expDate)
				
				node = getNode(this.doc, 'signature') || this.doc.all('signature')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#5]!'))
				setValue(node, this.signature)

				node = getNode(this.doc, 'fileIdType') || this.doc.all('fileIdType')
				if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#7]!'))
				setValue(node, this.fileIdType)
				
				this.action = getNode(this.doc, 'btnSubmit') || this.doc.all('btnSubmit')
				if (! this.action) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы загрузчика [#6]!'))
				return true
			} catch(Err) { 
				alert('prepare():\n'+Err.description)
			}
		}
		
		// submits file filles with data, to the 
		this.submit = function(){
			try {
				if (! this.action) return false
				this.action.fireEvent("onclick")
				this.action = null
				return true
			} catch(Err) {
			}
		}
	}
	var Uploads=new Array, Timer, Count=Uploads.length
	
	// fills File objects with data from DOM and adds them to Uploads stack. Save operation starts later (with Timer ) 
	function doUpload(){
		try {
			var node, doc, parent, file;
			var arr=document.getElementsByName('uploadFrame');
			for (var i=0; i<arr.length; i++){
				doc = arr(i).contentWindow.document
				parent = getParentNode(arr(i), 'TR')
				if (! parent) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы [#1]!'))

				file = new File(doc)
				if (file.filePath){
					with (file){
						node = getNode(parent, 'fileType')
						if (! node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы [#2]!'));
						type = getValue(node)
						
						node = getNode(parent, 'fileGroup')
						if (!node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы [fileGroup]!'));
						fileGroup=getValue(node)
						
						node = getNode(parent, 'fileGroupId')
						if (!node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы [fileGroupId]!'));
						fileGroupId=getValue(node)
						
						node = getNode(parent, 'fileIdType')
						if (!node) throw (new Error(ERR_USER_DEFINED,'Нарушена структура страницы [fileIdType]!'));
						fileIdType=getValue(node)
						
						node = getNode(parent, 'fileExpDate_'+parent.tabIndex)
						expDate = getValue(node) || ''
						signature = signFile(file.filePath)
						if (<%if(Config.getProperty("SIGN_FILE_REQURED").equals("1")){%>signature != "err" && <%}%>prepare()){
							Uploads.push(file);
						}
					}
				}
			}
			Uploads.reverse()
			Count=Uploads.length
			Timer = setTimeout('uploadFile()', 100)
		} catch(Err) {
			alert('doUpload\n'+Err.description)
			Uploads=new Array
		}
	}
	
	var wait=0;
	
	// pop file object form Uploads and try to call file.submit
	function uploadFile(frame) {
		try {
			var node, doc, file
			if (Timer) clearTimeout(Timer)
			if (wait==1) {
				wait=0;
				Uploads.pop()
				if (Uploads.length){
					Timer = setTimeout('uploadFile()', 0.1)
				} else {
					doClose(true)
				}
			} else {
				if (! Uploads.length){
					return true
				}
				file = Uploads[Uploads.length-1]
				if (! file.submit()){
					throw (new Error(ERR_USER_DEFINED,'Ошибка отправки данных.'))
				}
				wait=1;
			}
		} catch(Err) {
			alert('uploadFile\n'+Err.description)
		}
	}
	
	// should send data to the caller window??? or what???
	function doClose(reload) {
		try {
			window.returnValue=(reload ? Uploads : null)
			window.close()
		} catch(Err) {
			alert('doClose\n'+Err.description)
		}
	}
	
	function doSectionMine(id){
		try{
			var node, image, disp
			node=getNode(null, 'section_'+id, 'TABLE')
			if (!node) return false
			image=getNode(node, 'imgSection', 'IMG')
			node=node.tBodies[0]
			disp=(node.style.display=='none')
			if (image){
				image.src='style/'+((disp) ? 'toClose' : 'toOpen')+'.gif'
			}
			node.style.display=(disp) ? 'block' : 'none'
		} catch (Err){
			alert(Err.description)
		}
	}
	
</script>

<link type="text/css" rel="stylesheet" href="/compendium/calendar/dhtmlgoodies_calendar.css">
<script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>
</html:html>
