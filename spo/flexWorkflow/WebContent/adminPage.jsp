<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
  <title>Управление бизнес-процессами</title>
  <link rel="stylesheet" href="style/style.css" />
</head>

<body class="soria">
	<jsp:include page="header_and_menu.jsp" />
	<h1>Управление бизнес-процессами</h1>
	<ol>
		<li><a href="uploadPage.jsp">Загрузка процесса</a></li>
		<li><a href="reload.do">Перезагрузка системы</a></li>
		<!--  <li><a href="controlActions.jsp">Контроль операций</a></li> -->
		<li><a href="deleteSchema.jsp">Удаление схем</a></li>
		<!--a href="checkWholeness.do"> 3. Проверка целостности системы </a> <br> <br-->
	</ol>
	<jsp:include flush="true" page="footer.jsp" />
  </body>
</html>