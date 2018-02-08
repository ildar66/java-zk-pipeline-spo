<h1>Сделки</h1>
login: ${model["res"].login}<br /> 
totalCount: ${model["res"].totalCount}<br /> 
<ul>
<#list model["res"].list as task>
  <li>${task.number}: ${task.mainContractorName}: ${task.sum!''}: ${task.productTypeName!''}. 
  ${task.boolean}</li>
</#list>
</ul>
