<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

<%@page import="java.util.logging.Logger"%><html:html>
<head>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<title>index</title>
</head>
<body topmargin="0" rightmargin="10" bottommargin="0" leftmargin="10">
<%
	Logger LOGGER = Logger.getLogger(this.getClass().getName());
	LOGGER.info("START UPLOAD: UploadFileWAS");
%>
<script language="JavaScript" type="text/javascript">
	function click(btn) 
	{
		
	}
</script>

<html:form action="/actionUploadFile" method="post" enctype="multipart/form-data">
	<input type="file" onKeyDown="this.blur()" onContextMenu="return false;" style="valign:'top';font-size:8pt; width:100%" name="attachment">
	<input type="hidden" name="fileGroupId" value="">
	<input type="hidden" name="fileGroup" value="">
	<input type="hidden" name="filetype" value="">
	<input type="hidden" name="fileIdType" value="">
	<input type="hidden" name="id_appl" value="0">
	<input type="hidden" name="id_group" value="">
	<input type="hidden" name="url_WAS" value="0">
	<input type="hidden" name="file_expdate" value="">
	<input type="hidden" name="signature" value="">
	<input type="submit" name="btnSubmit" style="display: none" value="Submit" onclick="click(this)">
</html:form>

</body>
</html:html>