<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>


<html:html xhtml="true">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Загрузка бизнес-процесса</title>
	<link rel="stylesheet" href="style/style.css" />
</head>

<body class="soria">
	<jsp:include page="header_and_menu.jsp" />
	<h1>Загрузка бизнес-процесса</h1>
	<html:form action="/uploadProcess" enctype="multipart/form-data" method="post" focus="uploadFile">
		<table class="fields">
			<tr>
				<th>Файл для загрузки:</th>
				<td><input type="file" class="file" name="uploadFile" size="50" value="" onKeyDown="this.blur()" onContextMenu="return false;"/></td>
			</tr>
			<tr>
				<th>Загрузить обновлением:</th>
				<td><html:checkbox property="isUpdate"></html:checkbox></td>
			</tr>
			<tr>
				<th></th>
				<td><button type="submit" name="Submit">Загрузить</button></td>
			</tr>
		</table>
	</html:form>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html:html>
