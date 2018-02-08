<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title>${model["title"]}</title>
</head>
<body>
<link href="theme/stylesheet.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="style/style.css" />
<table class="HeaderTable" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td><a href="./"><img src="theme/img/logo-vtb.gif" style="margin:8px;"/></a></td>
        <td class="Gradient"><img src="theme/img/gradient.jpg"></td>
        <td class="TitleCaption">Система предкредитной обработки</td>
    </tr>
</table>
<div id="spisokZayavok">
    <!-- заголовок  таблицы -->
    <h1>${model["title"]}</h1>
    <h2>Атрибуты заявок содержат актуальные значения</h2>
    <input type="hidden" id="statusids" value="${model["statusids"]}" />
    <input type="hidden" id="creditDocumentary" value="${model["creditDocumentary"]}" />
    <input type="hidden" id="branch" value="${model["branch"]}" />
    <input type="hidden" id="departments" value="${model["departments"]}" />
    <input type="hidden" id="tradingDeskSelected" value="${model["tradingDeskSelected"]}" />
    <input type="hidden" id="isTradingDeskOthers" value="${model["isTradingDeskOthers"]?c}" />
    <input type="hidden" id="from_param" value="${model["from_param"]}" />
    <input type="hidden" id="to_param" value="${model["to_param"]}" />
    <input type="hidden" id="taskType" value="${model["taskType"]}" />
    <input type="hidden" id="initPage" value="${model["initPage"]}" />
    <input type="hidden" id="initFilter" value='${model["initFilter"]}' />
    <input type="hidden" id="page" />

    <div id="root"></div>
    <div class="Copyright">Разработка ООО «Мастер Домино». 2008–2016 г., Версия ${model["appversion"]}</div>

    <script src="scripts/vendor/react.min.js"></script>
    <script src="scripts/vendor/react-dom.min.js"></script>
    <script src="scripts/vendor/redux.min.js"></script>
    <script src="scripts/vendor/react-redux.min.js"></script>
    <script src="scripts/vendor/browser.min.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
    <script type="text/babel" src="scripts/dash.js"></script>
    <script type="text/javascript">
        var currencyList = ${model["currencyList"]};
        var processTypeList = ${model["processTypeList"]};
    </script>
</div>
</body>
</html>