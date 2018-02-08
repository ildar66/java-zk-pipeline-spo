По сделке с ${mainOrg} № <a href="${baseurl}/form.jsp?mdtaskid=${id_mdtask}&from=email">${numberDisplay}</a>
обновлена информация в секциях:
<ul>
    <#list changedSection as section>
    <li>${section}</li>
    </#list>
</ul>

