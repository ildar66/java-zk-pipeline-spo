--liquibase formatted sql

--changeset ebekkauer:spo-17.58-VTBSPO-316 logicalFilePath:spo-17.58-VTBSPO-316 endDelimiter:/
ALTER TABLE PROCENT MODIFY PAY_INT VARCHAR2(2000)
/

--changeset apavlenko:spo-17.58-VTBSPO-408 logicalFilePath:spo-17.58-VTBSPO-408 endDelimiter:/ runOnChange:true
--сделки из access со статусом Одобрено в Мигрирована
update ATTRIBUTES a set VALUE_VAR='Мигрирована'
where a.VALUE_VAR='Одобрено'
and a.ID_VAR in (select ID_VAR from VARIABLES v where v.name_var like 'Статус')
and a.ID_PROCESS in (select t.ID_PUP_PROCESS from mdtask t where t.is_imported is not null)
/
--сделки из access со статусом Акцептован в Одобрено
update ATTRIBUTES a set VALUE_VAR='Одобрено'
where a.VALUE_VAR='Акцептован'
and a.ID_VAR in (select ID_VAR from VARIABLES v where v.name_var like 'Статус')
and a.ID_PROCESS in (select t.ID_PUP_PROCESS from mdtask t where t.is_imported is not null)
/
--и статус детализации "Принято в СПО без изменений"
update mdtask t
set t.STATUSRETURN=(select max(FB_SPO_RETURN_ID) from CRM_STATUS_RETURN where STATUS_RETURN like 'Принято в СПО без изменений')
where t.is_imported is not null
and exists (select * from ATTRIBUTES a where a.ID_PROCESS=t.ID_PUP_PROCESS and a.VALUE_VAR='Одобрено' and a.ID_VAR in (select ID_VAR from VARIABLES v where v.name_var like 'Статус'))
/
