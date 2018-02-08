<#macro block title id>
<table class="pane" id="section_${id}" style="width:99%">
    <thead onclick="toggleSection('${id}')" onselectstart="return false">
    <tr>
        <td>
            <img alt="Развернуть" src="style/toOpen.gif" align="middle" id="section_${id}_img">
            <span>${title}</span>
        </td>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td id="section_${id}_td" style="display: none">
            <#nested>
        </td>
    </tr>
    </tbody>
</table>
</#macro>

<#macro dateField name value editMode>
    <#if editMode>
        <input class="text date" name="${name}" id="${name}" value="${value}" />
    <#else>
        ${value}
    </#if>
</#macro>

<#macro textField name value editMode em="6">
    <#if editMode>
        <input class="text em${em}" name="${name}" value="${value}" />
    <#else>
        ${value}
    </#if>
</#macro>

<#macro suspendLimit name value editMode>
    <#if editMode>
    <select name="${name}" id="${name}">
        <option value="нет" <#if value?? && value == "нет">selected="selected"</#if>>нет</option>
        <option value="да" <#if value?? && value == "да">selected="selected"</#if>>да</option>
    </select>
    <#else>
    ${value!''}
    </#if>
</#macro>

<#macro decisionBodyField name value editMode>
<div id="${name}Div">
    <#if value??>
    <#list value as dec>
        <div>
            <textarea name="${name}" <#if !editMode>disabled</#if> id="${name}_${dec?index}">${dec!''}</textarea>
            <#if editMode>
                <a href="javascript:;" onclick="$(this).parent().remove();"><img src="theme/img/minus.png" alt="удалить"></a>
                <a href="javascript:;"
                   onclick="$('#fieldid').val('${name}_${dec?index}');$('#decisionBodyFieldDialog').dialog({draggable: false, modal: true,width: 700});">
                    <img src="style/dots.png" alt="выбрать из шаблона"></a>
            </#if>
        </div>
    </#list>
    </#if>
</div>
    <#if editMode><div><a href="javascript:;" onclick="$('#${name}Template').tmpl({nextid:getNextId()}).appendTo('#${name}Div');">
        <img src="theme/img/plus.png" title="+" />
    </a></div></#if>
<script id="${name}Template" type="text/x-jquery-tmpl">
<div><textarea name="${name}" id="${r"${nextid}"}"></textarea><a href="javascript:;" onclick="$(this).parent().remove();"><img src="theme/img/minus.png" alt="удалить"></a>
<a href="javascript:;"
   onclick="$('#fieldid').val('${r"${nextid}"}');$('#decisionBodyFieldDialog').dialog({draggable: false, modal: true,width: 700});">
   <img src="style/dots.png" alt="выбрать из шаблона"></a>
</div>
</script>
</#macro>

<#macro decisionBlock namePrefix value editMode>
<tr><td style="width: 350px">Дата принятия решения</td><td>
    <@dateField name="${namePrefix}DecisionDate" value=(value.decisionDate?date)!"" editMode=editMode />
</td></tr>
<tr><td style="width: 350px">Кем принято решение</td><td>
    <@decisionBodyField name="${namePrefix}DecisionBody" value=value.decisionBody!'' editMode=model["editMode"] />
</td></tr>
<tr><td style="width: 350px">Номер протокола</td><td>
    <@textField name="${namePrefix}ProtocolNo" value=value.protocolNo!'' editMode=model["editMode"] />
    <#if value.filename?? && value.filename!=""><span><a href="downloadFile.html?id=${value.idDecision!''}">${value.filename}</a></span></#if>
    <#if editMode>
        <div id="upfile_${namePrefix}_file_div">
            <input type="file" id="upfile_${namePrefix}_file">
            <input type="hidden" id="upfile_${namePrefix}_file_decid" value="${value.idDecision!''}" />
        </div>
    </#if>
</td></tr>
</#macro>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <title>Информация о контрагенте</title>
    <link rel="stylesheet" href="style/style.css" />
    <link rel="stylesheet" href="style/jquery-ui-1.10.3.custom.min.css" />
    <link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
    <script language="javascript" src="scripts/date.js"></script>
    <script language="javascript" src="scripts/applicationScripts.js"></script>
    <script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.3.custom.min.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery.cookie.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
    <script language="javascript" src="scripts/form/frame.js"></script>
    <script type="text/javascript" src="scripts/sign/mdSignature.js"></script>
    <script language="javascript" src="scripts/form/attach.js"></script>
</head>
<body class="soria">
<form method="post" id="form" action="clientInfo.html" target="_self" enctype="application/x-www-form-urlencoded">
    <input name="id" type="hidden" value="${model["id"]!''}">
    <input name="clientInfo" type="hidden" value="clientInfo">
    <#if model["mdtask"]??>
        <input name="mdtask" type="hidden" value="${model["mdtask"]!''}">
    </#if>
    <input id="fieldid" type="hidden" value="">
<div class="controlPanel">
<#if model["editMode"]>
    <button iconClass="flatScreenIcon" onclick="save();return false;" id="saveBtn">
        Сохранить
    </button>
    &nbsp;
</#if>
    <button iconClass="flatScreenIcon" onclick="window.close()">
        Закрыть
    </button>
<span id="msg">${model["msg"]!''}</span>
</div>

<div style="display: block;">
    <h1 class="contractor"><span class="podpis">Информация о едином клиенте</span> ${model["org"].name!''}
        <a href="/km-web/fkr/list/contractor/${model["id"]!''}" target="_blank"><span class="error">События</span></a>
    </h1>
    <h2>Основная информация</h2>
    <div>
        <table class="regular">
            <tr><td>Наименование</td><td>${model["org"].name!''}</td></tr>
            <#if model["org"].ogrn??><tr><td>ОГРН</td><td>${model["org"].ogrn!''}</td></tr></#if>
            <#if model["org"].inn??><tr><td>ИНН</td><td>${model["org"].inn!''}</td></tr></#if>
            <tr><td>Публичная компания</td><td>
                <#if model["editMode"]>
                <select name="pub">
                    <option value=""></option>
                    <option value="да" <#if model["clientInfo"].pub?? && model["clientInfo"].pub == "да">selected="selected"</#if>>да</option>
                    <option value="нет" <#if model["clientInfo"].pub?? && model["clientInfo"].pub == "нет">selected="selected"</#if>>нет</option>
                </select>
                <#else>
                ${model["clientInfo"].pub!''}
                </#if>
            </td></tr>
            <tr><td>Клиентское подразделение</td><td><@textField name="corpBlock" value=model["clientInfo"].corpBlock!'' editMode=model["editMode"] em="30" /></td></tr>
            <tr><td>Резидент</td><td>${model["org"].resident!''}</td></tr>
            <tr><td>Статус сделок</td><td>
                <#if model["editMode"]>
                <select name="status">
                <#list model["dealStatusList"] as status>
                    <option value="${status!''}" <#if model["clientInfo"].status?? &&model["clientInfo"].status == status>selected="selected"</#if>>${status!''}</option>
                </#list>
                </select>
                <#else>
                ${model["clientInfo"].status!''}
                </#if>
                    <a href="/km-web/fkr/list/contractor/${model["id"]!''}" target="_blank">
                <img src="style/time.png" alt="История изменения статуса сделки" title="История изменения статуса сделки" /></a>
            </td></tr>
        </table>
    </div>

<@block title="Группа, в состав которой входит организация" id="group">
    <table class="regular"><thead><tr><th>Имя группы</th><th>Тип группы</th><th>Описание</th></tr>
        </thead><tbody><tr><td style="width: 350px">${model["org"].groupname!''}</td><td></td><td></td></tbody>
    </table>
</@block>
<@block title="Информация о лимите" id="limit">
    <table class="regular">
        <@decisionBlock namePrefix="limit" value=model["limitDecision"] editMode=model["editMode"] />
        <tr><td style="width: 350px">Наличие лимита/сублимита на Инвестиционные операции</td><td>
            <#if model["editMode"]>
                <select name="sublimit" id="sublimit" onchange="sublimitOnChange()">
                    <option value=""></option>
                    <option value="есть" <#if model["clientInfo"].sublimit?? && model["clientInfo"].sublimit == "есть">selected="selected"</#if>>есть</option>
                    <option value="отсутствует" <#if model["clientInfo"].sublimit?? && model["clientInfo"].sublimit == "отсутствует">selected="selected"</#if>>отсутствует</option>
                </select>
            <#else>
            ${model["clientInfo"].sublimit!''}
            </#if>
        </td></tr>
        <tr><td style="width: 350px">Дата истечения срока сделок <img src="theme/img/info.jpg"
   title="Указывается дата, до истечения которой могут заключаться сделки в рамках Лимита/ Сублимитов" /> </td><td>
            <@dateField name="validtoDate" value=(model["clientInfo"].validtoDate?date)!"" editMode=model["editMode"] />
        </td></tr>
    </table>
</@block>

<@block title="Информация рейтинге" id="rating">
    <table class="regular">
        <tr><td colspan="2" style="background-color: lightgrey;text-align: center !important">ГРУППА КОМПАНИЙ</td></tr>
        <tr><td colspan="2" class="subTitle">Решение об утверждении/подтверждении рейтинга</td></tr>
        <tr><td style="width: 350px">Значение утверждённого рейтинга Группы</td><td>
            <#if model["editMode"]><input class="text em6" name="groupRating" value="${model["clientInfo"].groupRating!''}" />
            <#else>${model["clientInfo"].groupRating!''}</#if>
        </td></tr>
        <tr><td style="width: 350px">Рейтинг по мастер-шкале</td><td>
            <@textField name="ratingScaleGroup" value=model["clientInfo"].ratingScaleGroup!'' editMode=model["editMode"] em="30" />
        </td></tr>
        <@decisionBlock namePrefix="group" value=model["groupDecision"] editMode=model["editMode"] />
        <tr><td colspan="2"  class="subTitle">Решение о пересмотре рейтинга</td></tr>
        <tr><td style="width: 350px">Необходимость пересмотра рейтинга</td><td>
            <#if model["editMode"]>
                <select name="groupRatingReview">
                    <option value=""></option>
                    <option value="да" <#if model["clientInfo"].groupRatingReview?? && model["clientInfo"].groupRatingReview == "да">selected="selected"</#if>>да</option>
                    <option value="нет" <#if model["clientInfo"].groupRatingReview?? && model["clientInfo"].groupRatingReview == "нет">selected="selected"</#if>>нет</option>
                </select>
            <#else>
            ${model["clientInfo"].groupRatingReview!''}
            </#if>
        </td></tr>
        <@decisionBlock namePrefix="groupReview" value=model["groupDecisionReview"] editMode=model["editMode"] />
        <tr><td colspan="2" style="background-color: lightgrey;text-align: center !important">КЛИЕНТ</td></tr>
        <tr><td style="width: 350px">Отрасль экономики</td><td>${model["org"].industry!''}</td></tr>
        <tr><td style="width: 350px">Методика ранжирования</td><td>
            <@textField name="ratingMethod" value=model["clientInfo"].ratingMethod!'' editMode=model["editMode"] em="30" />
        </td></tr>
        <tr><td colspan="2"  class="subTitle">Решение об утверждении/подтверждении рейтинга</td></tr>
        <tr><td style="width: 350px">Значение утверждённого рейтинга клиента</td><td>
            <#if model["editMode"]><input class="text em6" name="rating" value="${model["clientInfo"].rating!''}" />
            <#else>${model["clientInfo"].rating!''}</#if>
        </td></tr>
        <tr><td style="width: 350px">Рейтинг по мастер-шкале</td><td>
            <@textField name="ratingScale" value=model["clientInfo"].ratingScale!'' editMode=model["editMode"] em="30" />
        </td></tr>
        <@decisionBlock namePrefix="client" value=model["clientDecision"] editMode=model["editMode"] />
        <tr><td colspan="2"  class="subTitle">Решение о пересмотре рейтинга</td></tr>
        <tr><td style="width: 350px">Необходимость пересмотра рейтинга</td><td>
            <#if model["editMode"]>
                <select name="ratingReview">
                    <option value=""></option>
                    <option value="да" <#if model["clientInfo"].ratingReview?? && model["clientInfo"].ratingReview == "да">selected="selected"</#if>>да</option>
                    <option value="нет" <#if model["clientInfo"].ratingReview?? && model["clientInfo"].ratingReview == "нет">selected="selected"</#if>>нет</option>
                </select>
            <#else>
            ${model["clientInfo"].ratingReview!''}
            </#if>
        </td></tr>
        <@decisionBlock namePrefix="clientReview" value=model["clientDecisionReview"] editMode=model["editMode"] />
    </table>
</@block>

<@block title="Информация о Приостановлении" id="pause">
    <table class="regular">
        <tr><td colspan="2" style="background-color: lightgrey;text-align: center !important">Информация об инвестиционном лимите</td></tr>
        <tr><td style="width: 350px">Приостановление инвестиционного лимита</td><td>
            <@suspendLimit name="suspendLimitInvest" value=model["clientInfo"].suspendLimitInvest!'' editMode=model["editMode"] />
        </td></tr>
        <tr><td style="width: 350px">Источник Приостановления</td><td></td></tr>
        <@decisionBlock namePrefix="suspendLimitInvest" value=model["suspendLimitInvestDecision"] editMode=model["editMode"] />
        <tr><td style="width: 350px">Дата ввода</td><td>
            <@dateField name="suspendLimitInvestDate" value=(model["clientInfo"].suspendLimitInvestDate?date)!"" editMode=model["editMode"] />
        </td></tr>
        <tr><td colspan="2" style="background-color: lightgrey;text-align: center !important">Информация о лимите</td></tr>
        <tr><td style="width: 350px">Приостановление лимита (кредитные и документарные операции)</td><td>
            <@suspendLimit name="suspendLimitLoan" value=model["clientInfo"].suspendLimitLoan!'' editMode=model["editMode"] />
        </td></tr>
        <tr><td style="width: 350px">Источник Приостановления</td><td></td></tr>
        <@decisionBlock namePrefix="suspendLimitLoan" value=model["suspendLimitLoanDecision"] editMode=model["editMode"] />
        <tr><td style="width: 350px">Дата ввода</td><td>
            <@dateField name="suspendLimitLoanDate" value=(model["clientInfo"].suspendLimitLoanDate?date)!"" editMode=model["editMode"] />
        </td></tr>
    </table>
</@block>

<!--<@block title="Изменения статуса использования" id="statusChange"></@block>-->

<@block title="Информация о заключении безопасности" id="security">
    <table class="regular">
        <tr>
            <td style="width: 350px">Дата последнего заключения безопасности</td>
            <td>
                <#if model["editMode"]><input class="text date" name="securityLast" value="${(model["clientInfo"].securityLast?date)!""}"
                                              id="securityLast" onchange="securityLastOnChange()"/><#else>${(model["clientInfo"].securityLast?date)!""}</#if>
            </td>
        </tr>
        <tr>
            <td>Дата окончания действия заключения безопасности</td>
            <td><#if model["editMode"]><input class="text date" name="securityValidto" id="securityValidto" value="${(model["clientInfo"].securityValidto?date)!""}" />
            <#else>${(model["clientInfo"].securityValidto?date)!""}</#if>

            </td>
        </tr>
        <tr>
            <td>Примечание</td>
            <td><#if model["editMode"]>
                <textarea rows="6" name="securityText">${model["clientInfo"].securityText!''}</textarea>
            <#else>${model["clientInfo"].securityText!''}</#if>
            </td>
        </tr>
    </table>
</@block>

<@block title="Документы по контрагенту" id="doc">
    <div id="docdiv">Загрузка...</div>
</@block>
</div>
</form>
<script type="text/javascript">
    $('input.date').datepicker({
        dateFormat: 'dd.mm.yy',
        forceParse: false,
        changeMonth: true,
        changeYear: true,
        showWeek: true,
        firstDay: 1
    });
    function sublimitOnChange() {
        if($('#sublimit').val()=='есть') {
            $('#suspendLimitInvest').prop('disabled', false);
        } else {
            $('#suspendLimitInvest').val('нет');
            $('#suspendLimitInvest').prop('disabled', true);
        }
    }
    function securityLastOnChange() {
        var dateParts = $('#securityLast').val().split(".");
        var date = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
        date.setMonth(date.getMonth() + 3);
        $('#securityValidto').val($.datepicker.formatDate('dd.mm.yy', date));
    }
    function toggleSection(id){
        try{
            if ($('#section_'+id+'_td').css('display')=='none'){
                $('#section_'+id+'_td').show();
                $('#section_'+id+'_img').attr("src","style/toClose.gif");
            } else {
                $('#section_'+id+'_td').hide();
                $('#section_'+id+'_img').attr("src","style/toOpen.gif");
            }
        } catch (Err){
            alert(Err.description);
        }
    }
    function getNextId(){
        generatedID=generatedID+1;
        return 'generatedID'+generatedID;
    }
    var generatedID=0;
    $(document).ready(function() {
        //toggleSection('security');
        //toggleSection('rating');
        //toggleSection('pause');
        //toggleSection('limit');
        //toggleSection('statusChange');
        //toggleSection('doc');
        sublimitOnChange();
        var cachebuster = Math.round(new Date().getTime() / 1000);
        $('#docdiv').load('frame/documents.jsp?mdtaskid=${model["id"]!''}&pupTaskId=1'+'&cb=' +cachebuster+'&mdtask=${model["mdtask"]!''}');
    });
    function save(){
        //посмотреть есть ли хоть один не загруженный файл
        var i;
        var file2upload = '';
        for (i = 0; i < $('input[type=file]').size(); i++) {
            if ($('input[type=file]').get(i).files.length > 0) {
                file2upload = $('input[type=file]').get(i).id;
            }
        }
        if (file2upload != ''){//если есть, то загрузить один из них
            uploadFile(file2upload);
        } else {//если нет, то submit form
            $("#form").submit();
        }
        return false;
    }
    function uploadFile(id){
        $('#saveBtn').hide();
        var formData = new FormData();
        formData.append("decid", $('#'+id+'_decid').val());
        formData.append("userfile", $('#'+id).get(0).files[0]);
        $('#msg').text('идёт загрузка '+$('#'+id).get(0).files[0].name);
        var oReq = new XMLHttpRequest();
        oReq.open("POST", "upload.html", true);
        oReq.onload = function(oEvent) {
            if (oReq.status == 200) {
                //Вывести сообщение
                $('#msg').text('файл '+$('#'+id).get(0).files[0].name+' загружен');
                //очистить поле файл
                $('#'+id).val('');
                save();
            } else {
                //Вывести сообщение
                alert("Ошибка " + oReq.status + " произошла при попытки загрузки файла.");
            }
            $('#saveBtn').show();
        };
        oReq.send(formData);
        return false;
    }
</script>

<div id="decisionBodyFieldDialog" title="Кем принято решение" style="display: none;">
    <ul>
<#list model["decisionMakerList"] as item>
    <li><a href="javascript:;" onclick="$('#'+$('#fieldid').val()).text('${item}');$('#decisionBodyFieldDialog').dialog('close');">${item}</a></li>
</#list>
    </ul>
</div>

</body>
</html>