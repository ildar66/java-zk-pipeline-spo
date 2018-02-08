<br />
<table class="regular leftPadd" style="width: 99%;">
    <tr><th style="width: 50%;">Плановая  Дата  Выборки</th><td style="width: 50%;">${model["pipeline"].plan_date}</td></tr>
    <tr><th>Статус ${model["type"]!''}</th><td>${model["pipeline"].status!''}</td></tr>
    <tr><th>Вероятность Закрытия</th><td>${model["pipeline"].close_probability!''} %</td></tr>
    <tr><th>Ручное управление стадиями</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].statusManual = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Маржа, %</th><td>${model["pipeline"].margin!''}</td></tr>
    <tr><th>Применимое Право</th><td>${model["pipeline"].law!''}</td></tr>
    <tr><th>География</th><td>${model["pipeline"].geography!''}</td></tr>
    <tr><th>Обеспечение</th><td>${model["pipeline"].supply!''}</td></tr>
    <tr><th>Цель Финансирования</th>
        <td><ul><#list model["pipeline_fin_target"] as ft>
            <li>${ft}</li>
        </#list>
        </ul>
        </td></tr>
    <tr><th>Описание Сделки</th><td>${model["pipeline"].description!''}</td></tr>
    <tr><th>Комментарии по Статусу Сделки, Следующие Шаги</th><td>${model["pipeline"].cmnt!''}</td></tr>
    <tr><th>Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США</th><td>${model["pipeline"].addition_business!''}</td></tr>
    <tr><th>Возможность Синдикации</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].syndication = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Комментарии по Синдикации</th><td>${model["pipeline"].syndication_cmnt!''}</td></tr>
    <tr><th>Средневзвешенный Срок Погашения (WAL)</th><td>${model["pipeline"].wal!''} мес.</td></tr>
    <tr><th>Минимальная Ставка (Hurdle Rate) </th><td>${model["pipeline"].hurdle_rate!''} %</td></tr>
    <tr><th>Маркап</th><td>${model["markup"]!''} %</td></tr>
    <tr><th>PCs: Кеш, млн. дол. США</th><td>${model["pipeline"].pc_cash!''}</td></tr>
    <tr><th>PCs: Резервы, млн. дол. США</th><td>${model["pipeline"].pc_res!''}</td></tr>
    <tr><th>PCs: Деривативы, млн. дол. США</th><td>${model["pipeline"].pc_der!''}</td></tr>
    <tr><th>PCs: Всего, млн. дол. США</th><td>${model["pipeline"].pc_total!''}</td></tr>
    <tr><th>Выбранный Объём Линии в Валюте Сделки</th><td>${model["pipeline"].line_count!''}</td></tr>
    <tr><th>Публичная Сделка</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].pub = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Приоритет Менеджмента</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].priority = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Новый Клиент</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].new_client = "y"> checked="checked"</#if>></td></tr>
    <tr><th>ТЭФ Импорт/ Экспорт</th><td>${model["pipeline"].flow_investment!''}</td></tr>
    <tr><th>Тип сделки ТЭФ</th><td>${model["mdTask"].pipeline.tradeFinanceName!''}</td></tr>
    <tr style="display : none"><th>Рейтинг Клиента</th><td>${model["pipeline"].rating!''}</td></tr>
    <tr><th>Коэффициент Типа Сделки</th><td>${model["pipeline"].factor_product_type!''} %</td></tr>
    <tr><th>Коэффициент по Сроку Погашения</th><td>${model["pipeline"].factor_period!''}</td></tr>
    <tr><th>Фондирующая Компания</th><td>${model["pipeline"].contractor!''}</td></tr>
    <tr><th>Выдающий Банк</th><td>${model["pipeline"].vtb_contractor!''}</td></tr>
    <tr><th>Трейдинг Деск</th><td>${model["pipeline"].trade_desc!''}</td></tr>
    <tr><th>Пролонгация</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].prolongation = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Не показывать в отчетах</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].hideinreport = "y"> checked="checked"</#if>></td></tr>
    <tr><th>Не показывать в пайплайне на трейдерс митинг</th><td><input type="checkbox" disabled="disabled"<#if model["pipeline"].hideinreporttraders?? && model["pipeline"].hideinreporttraders = "y"> checked="checked"</#if>></td></tr>
</table>
<script>
    $(document).ready(function() {
        loadCompareResult('pipeline');
    });
</script>