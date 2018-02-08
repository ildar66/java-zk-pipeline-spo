<table class="regular" style="width: 99%; padding-left: 0px;">
	<tr>
		<th style="width: 10px;">№</th>
		<th style="width: 16%;">Тип</th>
		<th style="width: 16%;">Категория</th>
		<th style="width: 16%;">Сумма фондир.</th>
		<th style="width: 16%;">Период выдачи</th>
		<th style="width: 16%;">Статус</th>
		<th style="width: 16%;">Заявка действительна до</th>
	</tr>
	<#list funds as f>
	<tr>
		<td style="width: 10px;"><a target="_blank" href="/Funding/request/form/fundingrequest/VIEW/FUNDING_REQUEST-${f.id}/reserved">
		${f.id}</a></td>
		<td>${f.type!""}</td>
		<td>${f.category!""}</td>
		<td class="number"><NOBR>${f.amount!""}</NOBR> ${f.amountCurrency!""}</td>
		<td>с ${f.paymentPeriodStartDate?string("dd.MM.yyyy")} по ${f.paymentPeriodEndDate?string("dd.MM.yyyy")}</td>
		<td>${f.status!""}</td>
		<td>${f.expirationDate?string("dd.MM.yyyy")}</td>
	</tr>
	</#list>
</table>