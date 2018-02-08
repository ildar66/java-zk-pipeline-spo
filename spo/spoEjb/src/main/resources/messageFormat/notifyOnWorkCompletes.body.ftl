<html>
    <body>
        По сделке №<a href="${baseUrl}/form.jsp?mdtaskid=${mdTask.idMdtask}">${mdTask.mdtaskNumber} версия ${mdTask.version}</a>
        (${mdTask.mainOrganization.name}) одобрены новые условия - версия №${mdTask.version}
        <br /><br />
        
        <#if mdTask.additionalContract>
            Требуется формирование Дополнительного соглашения
        <#else>
            Формирование Дополнительного соглашения не требуется
        </#if>
        
        <br />
        
        <#if mdTask.productMonitoring>
            Решение влияет на мониторинг Сделки
        <#else>
            Решение не влияет на мониторинг Сделки
        </#if>
    </body>
</html>