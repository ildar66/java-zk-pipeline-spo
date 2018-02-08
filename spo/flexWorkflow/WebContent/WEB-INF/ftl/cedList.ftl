<table class="regular">
<tr><th>№ запроса</th><th>Тип запроса</th><th>статус</th><th>документы</th></tr>
<#list model["list"] as c>
<tr><td>${c.id}</td><td>${c.type!''}</td><td>${c.status!''}</td>
<td>
<#if c.creditEnsuringDocuments?size &gt; 0>
<table class="regular">
<th>Тип документа</th><th>№ и дата документа</th><th>Действует до:</th>
<th>Контрагент</th><th>№ договора контрагента</th>
<#list c.creditEnsuringDocuments as d>
<tr><td>${d.type!''}</td><td>${d.docOfficialNumber!''}<#if d.docOfficialDate??> от ${d.docOfficialDate?string("dd.MM.yyyy")}</#if></td>
<td><#if d.expirationDate??>${d.expirationDate?string("dd.MM.yyyy")}</#if></td>
<td>${d.contractorName!''}</td><td>${d.docContractorNumber!''}</td></tr>
</#list>
</table>
</#if>
</td>
</tr>
</#list>
</table>
