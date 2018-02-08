<br />
<input value="${model["effrate"]}" id="original_effrate" type="hidden">
<table class="regular leftPadd" style="width: 99%;">
    <tr><th style="width: 50%;">Плановая  Дата  Выборки</th><td id="compare_pipeline_plandate" style="width: 50%;">
        <input value="${model["pipeline"].plan_date}" onfocus="displayCalendarWrapper('pipeline_plan_date', '', false); return false;" onchange="input_autochange(this,'date');" id="pipeline_plan_date" class="date"></td></tr>
    <tr><th>Стадия ${model["type"]!''}</th>
        <td id="compare_pipeline_status">
            <input style="width:40em;white-space:nowrap"  id="pipeline_status" value="${model["pipeline"].status!''}" readonly="readonly" type="hidden">
            <div id="pipeline_status_display">${model["pipeline"].status!''}</div>
            <a href="javascript:;" id="pipeline_statuses_change"
               onclick="$('#pipeline_status_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff',width: 700});"><img src="style/dots.png" alt="выбрать из шаблона"></a>
        </td></tr>
    <tr><th>Вероятность Закрытия</th><td id="compare_pipeline_closeprobability">
    <#if model["close_probability_can_edit"] = "y">
        <input style="width:6em;white-space:nowrap" value="${model["pipeline"].close_probability!''}" readonly="readonly" id="pipeline_close_probability" class="money3digits"> %
    </#if>
    <#if model["close_probability_can_edit"] = "n">
    ${model["pipeline"].close_probability!''} %
    </#if>
    </td></tr>
    <tr><th>Ручное управление стадиями</th><td><input type="checkbox" id="pipeline_statusManual" onclick="onPipelineSectionStatusManualChange()" <#if model["pipeline"].statusManual = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Маржа, %</th><td>
        <input style="width:6em;white-space:nowrap" value="${model["pipeline"].margin!''}" id="pipeline_margin" class="money2digits">
    </td></tr>
    <tr style="display: none;"><th>Применимое Право</th><td id="compare_pipeline_law"><input style="width:15em;white-space:nowrap" id="pipeline_law" value="${model["pipeline"].law!''}" onchange="fieldChanged(this)">&nbsp;<a href="javascript:;" onclick="$('#pipeline_law_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr style="display: none;"><th>География</th><td id="compare_pipeline_geography"><input style="width:15em;white-space:nowrap" id="pipeline_geography" value="${model["pipeline"].geography!'Россия'}" onchange="fieldChanged(this)"></td></tr>
    <tr><th>Обеспечение</th><td id="compare_pipeline_supply">
        <#assign mdTask_pipeline_supply = model["pipeline"].supply!''>
        <#if model["supplies"]?? && model["supplies"]?first??>
            <#assign mdTask_pipeline_supplies = model["supplies"]!''>
        </#if>
    
        <#if mdTask_pipeline_supplies??>
            <select id="pipeline_supply" onchange="fieldChanged(this)" style="width: 220px;">
                <option value="" />
                <#list mdTask_pipeline_supplies as supply>
                    <#if supply??>
                        <option <#if mdTask_pipeline_supply = supply.value>
                                    selected="selected"
                                </#if> 
                                value="${supply.value!''}">
                            ${supply.value!''}
                        </option>
                    </#if> 
                </#list>
            </select>
        <#else>
            ${mdTask_pipeline_supply!''}
        </#if>
    </td></tr>
    <tr><th>Цель Финансирования</th>
        <td id="pipeline_fin_target_td"><a href="javascript:;" onclick='$( "#pipeline_fin_target_template" ).tmpl({nextid:getNextId()}).appendTo( "#pipeline_fin_target_td" );fieldChanged();'>Добавить цель</a><br />
        <#list model["pipeline_fin_target"] as ft>
            <div id="compare_pipeline_target_${ft?replace(' ','')}"><textarea class="pipeline_fin_target" id="pipeline_fin_target${ft_index}" onchange="fieldChanged(this)">${ft}</textarea>&nbsp;<a href="javascript:;" onclick="idx='pipeline_fin_target${ft_index}';$('#pipeline_fin_target_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a>
                <a href="javascript:;" onclick="$('#pipeline_fin_target${ft_index}').parent().remove();fieldChanged();"><img src="theme/img/minus.png" alt="удалить"></a></div>
        </#list>
            <div id="compare_list_pipeline_target" class="compare-list-removed"></div>
        </td></tr>
    <tr><th>Описание Сделки</th><td id="compare_pipeline_descr"><textarea id="pipeline_description" onchange="fieldChanged(this)">${model["pipeline"].description!''}</textarea></td></tr>
    <tr><th>Комментарии по Статусу Сделки, Следующие Шаги</th><td id="compare_pipeline_cmnt"><textarea id="pipeline_cmnt" onchange="fieldChanged(this)">${model["pipeline"].cmnt!''}</textarea></td></tr>
    <tr><th>Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США</th><td id="compare_pipeline_additionbusiness"><textarea id="pipeline_addition_business" onchange="fieldChanged(this)">${model["pipeline"].addition_business!''}</textarea></td></tr>
    <tr style="display: none;"><th>Возможность Синдикации</th><td id="compare_pipeline_synd"><input type="checkbox" id="pipeline_syndication"<#if model["pipeline"].syndication = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr style="display: none;"><th>Комментарии по Синдикации</th><td id="compare_pipeline_syndcmnt"><textarea id="pipeline_syndication_cmnt" onchange="fieldChanged(this)">${model["pipeline"].syndication_cmnt!''}</textarea></td></tr>
    <tr><th>Средневзвешенный Срок Погашения (WAL)</th><td id="compare_pipeline_wal"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].wal!''}" onblur="input_autochange(this,'money2digits')" id="pipeline_wal" class="digitsSpaces"> мес.</td></tr>
    <tr style="display: none;"><th>Минимальная Ставка (Hurdle Rate) </th><td id="compare_pipeline_hurdlerate"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].hurdle_rate!''}" onblur="input_autochange(this,'money3digits');onChangeHurdleRate()" id="pipeline_hurdle_rate" class="money3digits"> %</td></tr>
    <tr style="display: none;"><th>Маркап</th><td id="compare_pipeline_markup">
        <input
        <#if model["type"] = "сделки">
                readonly="readonly"
        </#if>
                style="width:6em;white-space:nowrap" value="${model["markup"]!''}" onblur="input_autochange(this,'money3digits')" id="pipeline_markup" class="money3digits"> %</td>
    </tr>
    <tr style="display: none;"><th>PCs: Кеш, млн. дол. США</th><td id="compare_pipeline_pccash"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].pc_cash!''}" onblur="input_autochange(this,'money1digits')" id="pipeline_pc_cash" class="money1digits"></td></tr>
    <tr style="display: none;"><th>PCs: Резервы, млн. дол. США</th><td id="compare_pipeline_pcres"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].pc_res!''}" onblur="input_autochange(this,'money1digits')" id="pipeline_pc_res" class="money1digits"></td></tr>
    <tr><th>FX Rates, млн. дол. США</th><td id="compare_pipeline_pcder"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].pc_der!''}" onblur="input_autochange(this,'money1digits')" id="pipeline_pc_der" class="money1digits"></td></tr>
    <tr><th>Commodities, млн. дол. США</th><td id="compare_pipeline_pctotal"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].pc_total!''}" onblur="input_autochange(this,'money1digits')" id="pipeline_pc_total" class="money1digits"></td></tr>
    <tr><th>Выбранный Объём Линии в Валюте Сделки</th><td id="compare_pipeline_linecount"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].line_count!''}" onblur="input_autochange(this,'money1digits')" id="pipeline_line_count" class="money1digits"></td></tr>
    <tr><th>312 П</th><td id="compare_pipeline_pub"><input type="checkbox" id="pipeline_pub"<#if model["pipeline"].pub = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr><th>Приоритет Менеджмента</th><td id="compare_pipeline_priority"><input type="checkbox" id="pipeline_priority"<#if model["pipeline"].priority = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr><th>Новый Клиент</th><td id="compare_pipeline_newclient"><input type="checkbox" id="pipeline_new_client"<#if model["pipeline"].new_client = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr><th>ТЭФ Импорт/ Экспорт</th><td id="compare_pipeline_flowinvestment">
        <#assign mdTask_pipeline_flowInvestment = model["pipeline"].flow_investment!''>        
        <#if model["flowInvestmentValues"]?? && model["flowInvestmentValues"]?first??>
            <#assign mdTask_pipeline_flowInvestmentValues = model["flowInvestmentValues"]!''>
        </#if>

        <#if mdTask_pipeline_flowInvestmentValues??>
            <select id="pipeline_flow_investment" onchange="fieldChanged(this)">
                <option value="" />
                <#list mdTask_pipeline_flowInvestmentValues as fi>
                    <#if mdTask_pipeline_flowInvestmentValues??>
                        <option <#if mdTask_pipeline_flowInvestment?lower_case = fi?lower_case>
                                    selected="selected"
                                </#if> 
                                value="${fi?capitalize!''}">
                            ${fi?capitalize!''}
                        </option>
                    </#if> 
                </#list>
            </select>
        <#else>
            ${mdTask_pipeline_flowInvestment!''}
        </#if>
    </td></tr>
    <tr><th>Тип сделки ТЭФ</th><td>
        <input type="hidden" id="pipeline_trade_finance_id" value="${model["mdTask"].pipeline.tradeFinance!''}">
        <div id="mdTask_trade_finance_div">${model["mdTask"].pipeline.tradeFinanceName!''}</div>
        <a href="javascript:;" onclick="$('#pipeline_trade_finance_popup').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});">
            <img src="style/dots.png" alt="выбрать">
        </a>
    </td></tr>
    <tr style="display : none"><th>Рейтинг Клиента</th><td id="compare_pipeline_rating"><input style="width:15em;white-space:nowrap" id="pipeline_rating" value="${model["pipeline"].rating!''}" onchange="fieldChanged(this)"></td></tr>
    <tr style="display: none;"><th>Коэффициент Типа Сделки</th><td id="compare_pipeline_producttype"><input style="width:6em;white-space:nowrap" value="${model["pipeline"].factor_product_type!''}" onblur="input_autochange(this,'money2digitsOrInt')" id="pipeline_factor_product_type" class="money2digits">&nbsp;%&nbsp;<a href="javascript:;" onclick="$('#pipeline_idx').val('pipeline_factor_product_type');$('#pipeline_coeffs0_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr style="display: none;"><th>Коэффициент по Сроку Погашения</th><td id="compare_pipeline_factorperiod"><input  style="width:6em;white-space:nowrap" value="${model["pipeline"].factor_period!''}" onblur="input_autochange(this,'money2digitsOrInt')" id="pipeline_factor_period" class="money2digits">&nbsp;<a href="javascript:;" onclick="$('#pipeline_idx').val('pipeline_factor_period');$('#pipeline_coeffs1_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr><th>Фондирующий Банк</th><td id="compare_pipeline_contractor"><input  style="width:15em;white-space:nowrap" id="pipeline_contractor" value="${model["pipeline"].contractor!''}" onchange="fieldChanged(this)">&nbsp;<a href="javascript:;" onclick="$('#pipeline_idx').val('pipeline_contractor');$('#pipeline_funding_company_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr><th>Выдающий Банк</th><td id="compare_pipeline_vtbcontractor"><input style="width:15em;white-space:nowrap"  id="pipeline_vtb_contractor" value="${model["pipeline"].vtb_contractor!''}" onchange="fieldChanged(this)">&nbsp;<a href="javascript:;" onclick="$('#pipeline_idx').val('pipeline_vtb_contractor');$('#pipeline_funding_company_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr><th>Трейдинг Деск</th><td id="compare_pipeline_tradedesc"><input style="width:15em;white-space:nowrap" id="pipeline_trade_desc" value="${model["pipeline"].trade_desc!''}" onchange="fieldChanged(this)">&nbsp;<a href="javascript:;" onclick="$('#pipeline_trading_desc_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a></td></tr>
    <tr><th>Пролонгация</th><td id="compare_pipeline_prolongation"><input type="checkbox" id="pipeline_prolongation"<#if model["pipeline"].prolongation = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr><th>Не показывать в отчетах</th><td id="compare_pipeline_hideinreport"><input type="checkbox" id="pipeline_hideinreport"<#if model["pipeline"].hideinreport = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
    <tr><th>Не показывать в пайплайне на трейдерс митинг</th><td id="compare_pipeline_hideinreporttraders"><input type="checkbox" id="pipeline_hideinreporttraders"<#if model["pipeline"].hideinreporttraders = "y"> checked="checked"</#if> onchange="fieldChanged(this)"></td></tr>
</table>

<div id="pipeline_law_div" title="Применимое Право" style="display: none;">
    <#if model["laws"]?? && model["laws"]?first??>
        <#assign mdTask_pipeline_laws = model["laws"]>
    </#if>

    <#if mdTask_pipeline_laws??>
        <ul>
            <#list mdTask_pipeline_laws as law>
                <li>
                    <a href="javascript:;" onclick="$('#pipeline_law').val('${law.value!''}');fieldChanged();$('#pipeline_law_div').dialog('close');">
                        ${law.value!''}
                    </a>
                </li>
            </#list>
        </ul>
    </#if>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_law_div').dialog('close');">Закрыть</a>
</div>

<div id="pipeline_status_div" title="Статус Сделки" style="display: none;">
    <#if model["statuses"]?? && model["statuses"]?first??>
        <#assign mdTask_pipeline_statuses = model["statuses"]>
    </#if>

    <#if mdTask_pipeline_statuses??>
        <table>
            <tr><th>Вероятность закрытия,%</th><th>Название стадии</th><th>Описание стадии</th></tr>
            <#list mdTask_pipeline_statuses as status>
                <tr><td>${status.value}</td><td>
                    <a href="javascript:;" onclick="$('#pipeline_status').val('${status.name!''}');fieldChanged();$('#pipeline_close_probability').val('${status.value}');$('#pipeline_status_display').text('${status.name}');$('#pipeline_status_div').dialog('close');">
                    ${status.name!''}
                    </a>
                </td><td>${status.description!''}</td></tr>
            </#list>
        </table>
    </#if>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_status_div').dialog('close');">Закрыть</a>
</div>
<script language="javascript">
    onChangeHurdleRate();
    idx=0;
</script>
<input type="hidden" id="pipeline_idx" value="idx">
<script id="pipeline_fin_target_template" type="text/x-jquery-tmpl">
<div><textarea class="pipeline_fin_target" id="pipeline_fin_target${r"${nextid}"}"></textarea>&nbsp;<a href="javascript:;" onclick="idx='pipeline_fin_target${r"${nextid}"}';$('#pipeline_fin_target_div').dialog({draggable: false, modal: true, dialogClass: 'noTitleStuff'});"><img src="style/dots.png" alt="выбрать из шаблона"></a>
<a href="javascript:;" onclick="$('#pipeline_fin_target${r"${nextid}"}').parent().remove();"><img src="theme/img/minus.png" alt="удалить"></a></div>
</script>
<div id="pipeline_fin_target_div" title="Цель финансирования" style="display: none;">
    <ul>
    <#list model["pipeline_financial_goal"] as fg>
        <li><a href="javascript:;" onclick="$('#'+idx).val('${fg.name}');fieldChanged();$('#pipeline_fin_target_div').dialog('close');">${fg.name}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_fin_target_div').dialog('close');">Закрыть</a>
</div>

<div id="pipeline_trading_desc_div" title="Трейдинг Деск" style="display: none;">
    <ul>
    <#list model["pipeline_trading_desk"] as fg>
        <li><a href="javascript:;" onclick="$('#pipeline_trade_desc').val('${fg.name}');fieldChanged();$('#pipeline_trading_desc_div').dialog('close');">${fg.name}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_trading_desc_div').dialog('close');">Закрыть</a>
</div>

<div id="pipeline_trade_finance_popup" title="" style="display: none;">
    <ul>
    <#list model["tradeFinanceList"] as tf>
        <li><a href="javascript:;"
               onclick="$('#pipeline_trade_finance_id').val('${tf.id!''}');$('#mdTask_trade_finance_div').text('${tf.name}');$('#pipeline_trade_finance_popup').dialog('close');">
        ${tf.name}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_trade_finance_popup').dialog('close');">Закрыть</a>
</div>
<div id="pipeline_funding_company_div" title="Контрагент" style="display: none;">
    <ul>
    <#list model["pipeline_funding_company"] as fg>
        <li><a href="javascript:;" onclick="$('#'+$('#pipeline_idx').val()).val('${fg.name}');fieldChanged();$('#pipeline_funding_company_div').dialog('close');">${fg.name}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_funding_company_div').dialog('close');">Закрыть</a>
</div>

<div id="pipeline_coeffs0_div" title="Коэффициент типа сделки" style="display: none;">
    <ul>
    <#list model["pipeline_coeffs0"] as fg>
        <li><a href="javascript:;" onclick="$('#'+$('#pipeline_idx').val()).val('${fg.value}');fieldChanged();$('#pipeline_coeffs0_div').dialog('close');">${fg.value}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_coeffs0_div').dialog('close');">Закрыть</a>
</div>

<div id="pipeline_coeffs1_div" title="Коэффициент по сроку погашения" style="display: none;">
    <ul>
    <#list model["pipeline_coeffs1"] as fg>
        <li><a href="javascript:;" onclick="$('#'+$('#pipeline_idx').val()).val('${fg.value}');fieldChanged();$('#pipeline_coeffs1_div').dialog('close');">${fg.value}</a></li>
    </#list>
    </ul>
    <hr />
    <a href="javascript:;" onclick="$('#pipeline_coeffs1_div').dialog('close');">Закрыть</a>
</div>
<script>
    $(document).ready(function() {
        onPipelineSectionStatusManualChange();
        loadCompareResult('pipeline');
    });
    function onPipelineSectionStatusManualChange() {
        if($('#pipeline_statusManual').is(':checked'))
            $('#pipeline_statuses_change').show();
        else
            $('#pipeline_statuses_change').hide();
    }
</script>