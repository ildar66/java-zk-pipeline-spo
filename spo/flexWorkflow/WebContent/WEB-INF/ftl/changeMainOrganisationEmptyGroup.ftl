<html>
    <head>
        <title>Смена основного заёмщика</title>
        <link href="theme/stylesheet.css" rel="stylesheet" type="text/css"/>
		<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
		<link rel="stylesheet" href="style/jqModal.css" />
		<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="scripts/jqModal.js"></script>
		<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script> 
		<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
		<script type="text/javascript" src="scripts/menu.js"></script>
    </head>
    <body>
    <table style="width:100%;height:100%;vertical-align:middle;text-align:center">
        <tr>
            <td>
                <div style="font-size:100%">По выбранной ранее ГК нет ЕК.
                    <br />Возможно, данная ГК была удалена в CRM Sales Logix. Вывести весь список</div>
                <br /><br />
                <div>
                    <button iconClass="flatScreenIcon" onclick="location.href = 'popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&onMySelect=changeMainOrganisationStage2()'">Да</button>
                    <button iconClass="flatScreenIcon" id="close" onclick="window.close()">Нет</button>
                </div>

            </td>
        </tr>
    </table>
    </body>
</html>