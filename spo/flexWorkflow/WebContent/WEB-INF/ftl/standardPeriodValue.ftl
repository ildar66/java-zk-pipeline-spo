<?xml version="1.0" encoding="UTF-8" ?>
<root>
<#list model["res"] as v>
<spval><id>${v.idStr}</id><period>${v.formatedPeriod}</period><name>${v.name}</name></spval>
</#list>
</root>