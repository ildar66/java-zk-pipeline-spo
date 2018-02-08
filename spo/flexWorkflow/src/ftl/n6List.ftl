<table class="regular" style="width: 99%;">
	<tr>
		<th style="width: 10px;">№</th>
		<th style="width: 32%;">Сумма сделки</th>
		<th style="width: 32%;">Плановые даты</th>
		<th style="width: 32%;">Статус</th>
	</tr>
	<#list funds as f>
	<tr>
		<td><a target="_blank" href="/Funding/n6request/form/n6requestform/VIEW/N6_REQUEST-${f.id}/reserved">
		${f.id}</a></td>
		<td class="number"><NOBR>${f.amount!""}</NOBR> ${f.amountCurrency!""}</td>
		<td>с ${f.plannedN6StartDate?string("dd.MM.yyyy")} по ${f.plannedN6EndDate?string("dd.MM.yyyy")}</td>
		<td>${f.status!""}</td>
	</tr>
	</#list>
</table>