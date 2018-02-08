<table style="border-top: 1px solid #acf; border-right: 1px solid #acf; border-collapse: collapse; margin:2px;">
<tr>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">№ и версия заявки</th>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Тип заявки</th>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Основной заемщик (ИНН)</th>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Сумма/срок</th>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Название операции и этапа</th>
    <#if 1 < tableFormat>
        <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Отклонение от нормативного срока, рабочих дней</th>
        <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">ФИО исполнителя</th>
    </#if>
    <th style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; background-color: #eef5ff; font-size:100%; color:#666; text-align:center;">Последний комментарий по заявке</th>
</tr>
<#list tasks as task>
<tr>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;"><a href="${task.url}">${task.number}</a></td>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">${task.type}</td>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">${task.org}</td>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">${task.sum}, ${task.period}</td>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">Операция: ${task.stagename}. Этап: ${task.standardperiodname}</td>
    <#if 1 < tableFormat>
        <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">${task.overrun}</td>
        <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">${task.executor}</td>
    </#if>
    <td style="border-left:1px solid #acf; border-bottom:1px solid #acf; padding:3px 5px; font-size:100%;">
        <#if task.comment??>
        ${task.comment.author}, ${task.comment.commenttime}, ${task.comment.text}
        </#if>
    </td>
</tr>
</#list>
</table>
