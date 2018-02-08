<html>
    <head>
        <title>Тестовая страница</title>
        <link href="theme/stylesheet.css" rel="stylesheet" type="text/css"/>
		<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
		<link rel="stylesheet" href="style/jqModal.css" />
		<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="scripts/jqModal.js"></script>
		<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
		<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
		<script type="text/javascript" src="scripts/menu.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.6/Chart.bundle.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/flot/0.8.3/jquery.flot.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/flot/0.8.3/jquery.flot.categories.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/flot/0.8.3/jquery.flot.stack.js"></script>
    </head>
    <body><br /><br /><br />
        <h1>Тестовая страница</h1>
        Текущий пользователь: ${model["user"]}
        <br />
<pre>
${model["msg"]}
</pre>
    <div>
        idMdtask: <input id="mdtask" value="26844">
        <button onclick="window.location = 'test.html?mdtask='+$('#mdtask').val()">Показать объект для отчёта</button>
    </div>
        <ul>
        <li><a href="start.do">вернуться на главную</a></li>
        <li><a href="notifyTest.jsp">рассылка уведомлений</a></li>
        <li><a href="lorem.txt">lorem 30 Кбайт</a></li>
        <li><a href="test.jsp">spring контекст через jsp тест</a></li>
        <li><a href="popup_org.jsp?ek=only">список единых клиентов</a></li>
        </ul>

        Список активных валют (myBatisMapper): ${model["currency"]}

    <!--[if IE 7]>
    <p>У вас браузер IE7.</p>
    <![endif]-->
    <!--[if IE 8]>
    <p>У вас браузер IE8.</p>
    <![endif]-->
    <!--[if IE 9]>
    <p>У вас браузер IE9.</p>
    <![endif]-->
    <div id="browser"></div>
    <script>
        $( "#browser" ).html( "Версия браузера: <span>" +
         $.browser.version + "</span>" );
    </script>

    </body>
</html>