<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body>
<table class="pane" border="1" style="width: 600px;">
<TBODY>
<#list model["list"] as d>
<tr><td><a href="javascript:;" onclick="go('${d.id}','${d.name?replace('"',",")}')">${d.name}</a></td></tr>
</#list>
</TBODY>
</table>
</body>
<script language="javascript">
function go(id,name){
<#if model["onlyInitialDep"]=='true'>
    //Проверить
    //Записи справочника не могут содержать одинаковых значений в полях «Операция» и «Инициирующее подразделение».
    //по routeid ищем название операции. И находим нет ли уже такого инит департамента для таких операций
    var stageName = window.opener.$('#route${model["routeid"]}_Tr > td:first-child > input:first-child').val();
    //если нашли, то ругаемся и не добавляем
    if(window.opener.$('input[value="'+stageName+'"]').parent().parent().find('div > input[value="'+id+'"]').size()>0){
        alert('Для операции "'+stageName+'" уже задано правило с инициирующим подразделением '+name);
    } else {
        window.opener.nextval = window.opener.nextval +1;
        window.opener.$("#newRouteInitDepTemplate").tmpl( {id:id,name:name,nextval:window.opener.nextval,routeid:${model["routeid"]}}).appendTo( "#route${model["routeid"]}_initDepTd" );
    }
<#else>
    window.opener.$('#route${model["routeid"]}_defaultDepartment').val(id);
    window.opener.$('#route${model["routeid"]}_defaultDepartmentName').html(name);
</#if>
    window.opener.focus();
    window.close();
}
</script>