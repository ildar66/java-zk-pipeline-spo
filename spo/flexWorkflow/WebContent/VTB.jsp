<!DOCTYPE html>
<%@ page contentType="text/html; charset=utf-8" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Список заявок</title>
	<link rel="stylesheet" href="style/style.css" />
	<script type="text/javascript" src="scripts/loading.js"></script>
</head>
<body onBeforeUnload="loading()">
<jsp:include page="header_and_menu.jsp" />
<jsp:include page="taskTable.jsp"/>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>
