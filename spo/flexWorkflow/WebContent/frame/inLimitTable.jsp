<%@page isELIgnored="true"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<table class="regular fixed" id="inLimit" width="620px">
    <col width="170px" /><col width="100px" /><col width="120px" /><col width="120px" /><col width="60px" /><col width="60px" /><col width="20px" />
    <thead><tr><th>Лимит/сублимит/сделка</th><th>Номер</th><th>контрагент</th><th>сумма</th><th>срок</th><th id="inLimitCol6"></th><th></th></tr></thead>
    <tbody></tbody>
</table>
<div class="compare-list-removed" id="compare_list_in_limit"></div>
<script id="hasChildInLimitTemplate_true" type="text/x-jquery-tmpl">
${indent}<img id="img${id}" onclick="toggleLimit('${id}')" src="theme/img/expand.jpg" alt="+" lvl=${lvl}>&nbsp;${type} ${title}
</script>
<script id="hasChildInLimitTemplate_false" type="text/x-jquery-tmpl">
${indent}&nbsp;&nbsp;&nbsp;${type} ${title}
</script>
<script id="newInLimitTemplate" type="text/x-jquery-tmpl">
<tr class="${trcl} ${current}" id="tr${id}"><td id="td${id}">${type} ${title}</td><td id="nbr${id}">${number}</td><td id="org${id}">${org}</td>
<td id="sum${id}" class="number">${sum}</td><td id="period${id}" class="number">${period}</td><td id="sublimitadd${id}"></td><td id="sublimitdel${id}"></td></tr>
</script>
