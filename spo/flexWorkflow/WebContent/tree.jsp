<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=utf-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title>Сделка в рамках лимита</title>
</head>
<body class="soria">
<link rel="stylesheet" href="style/style.css" />
<jsp:include page="header_and_menu.jsp" />
<h1>Сделка в рамках лимита</h1>
<table class="regular fixed">
    <col width="200px" /><col width="100px" /><col width="100px" /><col width="200px" /><col width="150px" />
    <thead>
        <tr>
        <th>Лимит/сублимит/сделка</th>
        <th>Номер</th>
        <th>контрагент</th>
        <th>вид</th>
        <th>сумма</th>
        </tr>
    </thead>
<tbody>
<tr><td><img onclick="toggle('l')" src="theme/img/expand.jpg" alt="+" id="l">Лимит</td><td>101</td><td>ООО Вектор</td><td>Кредитно документарный</td><td>1&nbsp;000&nbsp;000&nbsp;000&nbsp;р.</td></tr>
<tr class="ls lh"><td>&nbsp;&nbsp;<img src="theme/img/expand.jpg" alt="+" id="s1" onclick="toggle('s1')" class="lt">Сублимит</td><td>101.01</td><td>ООО Вектор</td><td>Кредитно документарный</td><td>500&nbsp;000&nbsp;000&nbsp;р.</td></tr>
<tr class="lh s1s s1h"><td>&nbsp;&nbsp;&nbsp;&nbsp;Сделка</td><td>495499</td><td>ООО Ромашка</td><td>кредитная линия</td><td>95&nbsp;000&nbsp;000&nbsp;р.</td></tr>
<tr class="ls lh"><td>&nbsp;&nbsp;<img src="theme/img/expand.jpg" alt="+" id="s2" onclick="toggle('s2')" class="lt">Сублимит</td><td>101.02</td><td>ООО Вектор</td><td>Кредитно документарный</td><td>500&nbsp;000&nbsp;000&nbsp;р.</td></tr>
<tr class="lh s2s s2h"><td>&nbsp;&nbsp;&nbsp;&nbsp;Сублимит</td><td>101.02.1</td><td>ООО Вектор</td><td>Кредитно документарный</td><td>300&nbsp;000&nbsp;000&nbsp;р.</td></tr>
<tr class="ls lh"><td>&nbsp;&nbsp;Сублимит</td><td>101.03</td><td>ООО Вектор</td><td>Кредитно документарный</td><td>500&nbsp;000&nbsp;000&nbsp;р.</td></tr>
</tbody>
</table>
<br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
<jsp:include flush="true" page="footer.jsp" />
<script type="text/javascript">
$(document).ready(function(){
$("#upperUserName, #bottomUserName, a.login").fancybox({
zoomSpeedIn: 0,
zoomSpeedOut:0,
frameWidth: 600,
frameHeight: 600,
'hideOnContentClick': false
});
$(".lh").hide();
});
function toggle(cl) {
    if($('#'+cl). attr("alt")=="+"){// закрыта, открываем
    	$('.'+cl+'s').show();
    	$('#'+cl).attr({src: "theme/img/collapse.jpg",alt: "-"});
	} else {// открыта, закрываем
		$('.'+cl+'h').hide();
		$('#'+cl).attr({src: "theme/img/expand.jpg",alt: "+"});
		$('.'+cl+'t').attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
</script>
</body>
</html>
