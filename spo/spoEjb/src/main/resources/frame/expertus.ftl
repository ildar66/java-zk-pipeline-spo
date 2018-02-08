<table class="regular">
<tbody>
    <tr><th style="width: 25%;">Экспертиза</th><th style="width: 12.5%;">начало</th><th style="width: 12.5%;">окончание</th><th style="width: 25%;">эксперт</th><th style="width: 25%;">экспертная группа</th></tr>
    <#list expertus as exp>
    <tr><td>${exp.name}</td><td>${exp.dataStart}</td><td>${exp.dataEnd}</td><td>
      <#if exp.user??>
         <#if exp.user.login!='_cptbreak'>
         <a class="fancy" href="roleslist.jsp?login=${exp.user.login}">
         </#if>
         ${exp.user.fullName}
         <#if exp.user.login!='_cptbreak'>
         </a>
         </#if>
      </#if>
    </td>
    <#if exp.rowspan!=0>
    <td rowspan="${exp.rowspan}">
        <#list exp.group as gr><ul>
            <li>${gr.fullName} 
            <#if exp.canEdit>
                <a href="javascript:;" onclick="delExpertTeam('${gr.idStr}','${exp.name}');">Исключить</a>
            </#if>
            </li>
        </ul></#list>
        <#if exp.canEdit>
        <a onclick="AddExpertTeam('${exp.name}')" href="javascript:;"><img alt="+" src="theme/img/plus.png"></a>
        </#if>
    </td>
    </#if>
    </tr>
    </#list>
</tbody>
</table>
<input id="etuserId" type="hidden"><input id="etuserFIO" type="hidden"><input id="expname" type="hidden">
<script>
    $(document).ready(function() {
        fancyClassSubscribe();
    });
</script>