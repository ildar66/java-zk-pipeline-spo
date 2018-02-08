<#if officialNumber??>По КС №${officialNumber}&nbsp;</#if>
<a href="${baseUrl}/clientInfo.html?id=${mdTask.mainOrganization.id}&mdtask=${mdTask.idMdtask}">${mdTask.mainOrganization.name}</a>&nbsp;
(сделка №${mdTask.mdtaskNumber} - версия №${mdTask.version}) изменена процентная ставка<br />
Необходимо утвердить изменения (<a href="${baseUrl}/form.jsp?mdtaskid=${mdTask.idMdtask}&monitoringmode=true">Акцепт изменений</a>)
