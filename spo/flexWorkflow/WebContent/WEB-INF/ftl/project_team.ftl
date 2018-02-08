<h1>Проектная команда по заявке номер ${model["mdtask"].mdtaskNumber} версия ${model["mdtask"].version}</h1>
<table class="add regular">
    <tbody>
    <tr><th>Имя</th><th>Подразделение</th>
        <th>Роли</th>
        <th>Выполнение операций</th>
    </tr>
<#list model["teamusers"] as team>
<#list team.roles as r>
<tr>
    <#if r?is_first>
        <td rowspan="${team.roles?size}">${team.name}</td>
        <td rowspan="${team.roles?size}">${team.department}</td>
    </#if>
    <td>${r.name}</td>
    <td><#if r.flag><input type="checkbox" checked disabled></#if></td>
</tr>
</#list>
</#list>
    </tbody>
</table>