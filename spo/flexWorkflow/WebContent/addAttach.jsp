<!DOCTYPE HTML>
<%@page import="com.vtb.util.Formatter"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <base target="_self">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Добавление документов</title>
    
    <script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>

	<%  String cryptoIssuers = SBeanLocator.singleton().getCompendiumMapper().getCryptoIssuers();
		if (cryptoIssuers == null)
			cryptoIssuers = "";	%>
	<script type="text/javascript">
		var cryptoIssuers = "";
		cryptoIssuers = "<%=cryptoIssuers%>";
	</script>
	<script type="text/javascript" src="scripts/sign/mdSignature.js?<%=ApplProperties.getVersion()%>"></script>
    <script type="text/javascript" src="scripts/form/attach.js?<%=ApplProperties.getVersion()%>"></script>

    <link rel="stylesheet" href="style/style.css" />
    <link type="text/css" rel="stylesheet" href="/compendium/calendar/dhtmlgoodies_calendar.css">
    <script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>
    
	<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
	<script src="scripts/jquery/jquery_file_upload/js/vendor/jquery.ui.widget.js"></script>
	
	<!-- The Templates plugin is included to render the upload/download listings -->
	<script src="scripts/jquery/jquery_file_upload/js/tmpl.min.js"></script>
<!-- 	TODO заменить на jquery.tmpl.min.js -->
	
	<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
	<script src="scripts/jquery/jquery_file_upload/js/jquery.iframe-transport.js"></script>
	
	<!-- The basic File Upload plugin -->
	<script src="scripts/jquery/jquery_file_upload/js/jquery.fileupload.js"></script>
	
	<!-- The File Upload processing plugin -->
	<script src="scripts/jquery/jquery_file_upload/js/jquery.fileupload-process.js"></script>
	
	<!-- The File Upload validation plugin -->
	<script src="scripts/jquery/jquery_file_upload/js/jquery.fileupload-validate.js"></script>
	
	<!-- The File Upload user interface plugin -->
	<script src="scripts/jquery/jquery_file_upload/js/jquery.fileupload-ui.js"></script>
	
	<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE 8 and IE 9 -->
	<!--[if (gte IE 8)&(lt IE 10)]>
	<script src="js/cors/jquery.xdr-transport.js"></script>
	<![endif]-->	   
	
	<!--[if lte IE 9]>
		<style type="text/css">
			table#attachTable thead tr th.remove 			{width: 1em;}	
		</style>
	<![endif]-->
	
</head>
<body>
<!--[if lte IE 9]>
<div id="ieDiv" style="cursor: pointer; background: rgb(255, 255, 225);font-family: tahoma, arial; font-size: 11px; text-decoration: none; color: rgb(0, 0, 0);" 
onmouseout="JavaScript: $('#ieDiv').css('background', '#FFFFE1');$('#ieDiv').css('color', '#000000');" 
onmouseover="JavaScript: $('#ieDiv').css('background', '#0A246A');$('#ieDiv').css('color', '#FFFFFF');"
onclick="JavaScript: $('#ieDiv').hide()">
<img align="absmiddle" src="style/images/icon-notice.gif" alt="IEwarning">
Ваша версия браузера не поддерживается. Некоторые функции могут работать некорректно.<br />
Обратитесь в службу поддержки пользователей для обновления браузера по тел. 1-11-11. В случае невозможности обновления браузера, просим написать обращение на службу сопровождения СПКП (soprovozhdenie_spkp@msk.vtb.ru)
</div>
<![endif]-->

<div id="fileUpload">
<h2 class="header_center">Добавить документы
	<%
	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	String mdtaskid = request.getParameter("mdtaskid");
		boolean showReason = false;
		if(mdtaskid!=null && !mdtaskid.isEmpty()){
			showReason = SBeanLocator.singleton().mdTaskMapper().getById(Long.valueOf(mdtaskid)).getIdInstance()!=null;
		}
	if(request.getParameter("ownertype")!=null && request.getParameter("ownertype").equals("0")){
	    String ownerid = request.getParameter("owner");
	    ru.md.spo.dbobjects.TaskJPA task = ownerid.startsWith("mdtaskid")?taskFacadeLocal.getTask(Long.valueOf(ownerid.replaceAll("mdtaskid", ""))):taskFacadeLocal.getTaskByPupID(Formatter.parseLong(ownerid));
	    out.println("к заявке "+task.getNumberDisplay());
	}
	%>
</h2>
    <form id="fileupload" action="upload.do" method="POST" enctype="multipart/form-data">
        <h3 class="header_center">
        Группа документа: 
        
	<%if(request.getParameter("t") != null){ %>
		    <%=pupFacadeLocal.getDocumentGroup(Formatter.parseLong(request.getParameter("g"))).getNAME_DOCUMENT_GROUP() %>
	<%} else { %>
		без группы
    <%} %>
    	</h3>
    
<div id="errorFileList" style="display:none;">
	При загрузке файлов произошла ошибка.
</div>

        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
        <div class="row fileupload-buttonbar">
            <div class="col-lg-7">
            	<div class="commands" style="display:none;">
	                <button 
		                id="hiddenUploadButton" 
		                type="submit" class="btn btn-primary start button">
	                    <i class="glyphicon glyphicon-upload"></i>
	                    <span>Добавить</span>
	                </button>
                </div>
                
                <!-- The fileinput-button span is used to style the file input field as button -->
                <span class="btn btn-success fileinput-button">
                    <i class="glyphicon glyphicon-plus"></i>
                    <span id="multipleFileHeader">
						Выбор файлов:
					</span>
                    <input id="multipleFiles" type="file" name="files[]" multiple title="Выбор файлов. Можно выбирать несколько файлов, зажимая CTRL или SHIFT." />
                </span>
                
                <input id="addUrlFile" type="file" style="display:none;" />
                
                <!-- The global file processing state -->
                <span class="fileupload-process"></span>
            </div>
            
            <span id="loading" style="display:none;">
				<img src="style/images/loading.gif" />
			</span>
			
            <!-- The global progress state -->
            <div class="col-lg-5 fileupload-progress fade">
                <!-- The global progress bar -->
                <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
                    <div class="progress-bar progress-bar-success" style="width:0%;"></div>
                </div>
                <!-- The extended global progress state -->
                <div class="progress-extended">&nbsp;</div>
            </div>
        </div>
        
        <!-- The table listing the files available for upload/download -->
        <table id="attachTable" class="regular ng-scope" role="presentation">
        	<thead>
        		<tr>
        			<th class="doc_type">Тип документа</th>
        			<th class="doc_expire_date">Срок действия</th>
        			<th class="title">Заголовок</th>
        			<th class="name">Файл или URL</th>
                    <%if(showReason){%>
        			<th class="name">Причина</th>
                    <%}%>
        			<th class="remove"></th>
        		</tr>
        	</thead>
        	<tbody class="files"></tbody>
        </table>

       	<table class="add_doc_buttons">
       		<tbody>
       			<tr>
       				<td class="left_buttons">
       					<button onclick="addUrlRow();" type="button" class="button" >Добавить ссылку url</button>   
       				</td>
       				<td class="right_buttons">
				        <button
				        	id="uploadButton" 
				        	type="submit" 
				        	style="float: right;"
				        	onclick="return check_has_no_document(this);"
				        	class="btn btn-primary start button button_disabled">
					        <i class="glyphicon glyphicon-upload"></i>
					        <span>Добавить</span>
				        </button>
       				</td>
       			</tr>
       		</tbody>
       	</table>
                   
<%if(request.getParameter("t") != null){ %>
	    <input type="hidden" id="group" name="group" value="<%=request.getParameter("g") %>">
	    <input type="hidden" id="type" name="type" value="<%=request.getParameter("t") %>">
	    <input type="hidden" id="type_name" name="type_name" value="<%=pupFacadeLocal.getDocumentType(Formatter.parseLong(request.getParameter("t"))).getName() %>">
	    <input type="hidden" id="group_name" name="group_name" value="<%=pupFacadeLocal.getDocumentGroup(Formatter.parseLong(request.getParameter("g"))).getNAME_DOCUMENT_GROUP() %>">
<%} %>
		
		<input type="hidden" id="owner_type" name="owner_type" value="<%=request.getParameter("ownertype") %>">
		<input type="hidden" id="owner" name="owner" value="<%=request.getParameter("owner") %>">
		<input type="hidden" id="isEdsRequiredSPO" value="<%=taskFacadeLocal.getGlobalSetting("isEdsRequiredSPO")%>" />
    </form>
    

<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
	<%if(request.getParameter("t") == null){ %>
			<input type="text" name="type_name">
	<%} else { %>
			<%=pupFacadeLocal.getDocumentType(Formatter.parseLong(request.getParameter("t"))).getName() %>
			<input type="hidden" name="type_name" value="<%=pupFacadeLocal.getDocumentType(Formatter.parseLong(request.getParameter("t"))).getName() %>" />
	<%} %>
		</td>
        <td>
			<input type="text" name="file_expdate" value="" onFocus="displayCalendarWrapperNoId(this, '', false); return false;" class="file_expdate">
		</td>
        <td>
			<input type="text" name="title"
				value="{%=file.name%}"
				size="20" style="valign: width:100%" />
		</td>

        <td>
            {% if (file.name) { %}
                <p class="name">{%=file.name%}</p>
            {% } else { %}
                <input onblur="urlChanged(this);" type="text" name="url" placeholder="Вставить URL" />
            {% } %}

			<input type="hidden" name="sign" value="">
        
            <strong class="error text-danger"></strong>
        </td>
        <%if(showReason){%>
        <td>
			<input type="text" name="reason" value="" size="20" style="valign: width:100%" />
		</td>
        <%}%>
        <td>

			
			<div style="display:">
	{% if (!i && !o.options.autoUpload) { %}
                <button class="btn btn-primary start" disabled style="display:none">
                    <i class="glyphicon glyphicon-upload"></i>
                    <span>Start</span>
                </button>
	{% } %}
			</div>
			
			<div class="cancel_and_filesize">
	{% if (!i) { %}
				<a href="#" onclick="reset_url_value(this);"><img class="cancel" src="theme/img/minus.png" alt="-" /></a>
	{% } %}

				<span class="size">Подсчитывается...</span>
            	<div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0">
					<div class="progress-bar progress-bar-success" style="width:0%;" />
				</div>
			</div>

        </td>
    </tr>
{% } %}
</script>


<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        <td>
			{%=file.type_name%}
		</td>
        <td>
			{%=file.file_expdate%}
		</td>
        <td>
			{%=file.title%}
		</td>

        <td>
            {% if (file.name) { %}
                <span class="name">{%=file.name%}</span>
            {% } else { %}
				<span class="url">{%=file.url%}</span>
            {% } %}

            {% if (file.error) { %}
                <span class="error"><span class="label label-danger">Ошибка. </span> {%=file.error%}</span>
            {% } %}
        </td>
        <td>
        </td>
    </tr>
{% } %}
</script>

<script type="text/javascript">
$(function () {
    'use strict';
    
    try {
    	changeHeaders();
	    addAttachInit();
    } catch(err) {
    	alert("js error on addattach on ready '" + ((err.description == undefined) ? err : err.description) + "''");
    }
});

</script>
</div>
</body>
</html>