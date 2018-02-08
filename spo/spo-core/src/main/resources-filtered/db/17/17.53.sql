--liquibase formatted sql

--changeset apavlenko:spo-17.53-VTBSPO-278 logicalFilePath:spo-17.53-VTBSPO-278 endDelimiter:/ runOnChange:true
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD (MAIN_ORG_GROUP CHAR(12))');
END;
/
COMMENT ON COLUMN MDTASK.MAIN_ORG_GROUP IS 'Какая ГК была у основного заёмщика ЕК во время создания заявки'
/
update mdtask t set t.MAIN_ORG_GROUP=
(select max(l.GC_ID) from r_org_mdtask r
inner join CRM_FINANCE_ORG f on f.ID_ORG=r.ID_CRMORG
inner join CRM_COMPANIESGROUP_LINKED l on l.GC4_ID=f.ID_UNITED_CLIENT
where r.ID_MDTASK=t.ID_MDTASK and r.ORDER_DISP=0)
/
--changeset apavlenko:spo-17.55-settings logicalFilePath:spo-17.55-settings endDelimiter:/ runOnChange:true
insert into GLOBAL_SETTINGS(MNEMO,DESCRIPTION,value,system)
select 'traderApproveEnable','Отображать кнопку трейдера','false','СПО' from dual
where not exists (select * from GLOBAL_SETTINGS where mnemo='traderApproveEnable')
/
insert into GLOBAL_SETTINGS(MNEMO,DESCRIPTION,value,system)
select 'changeMainOrgEnable','Функциональность смены основного заёмщика','false','СПО' from dual
where not exists (select * from GLOBAL_SETTINGS where mnemo='changeMainOrgEnable')
/
update GLOBAL_SETTINGS set DESCRIPTION='Функциональность смены основного заёмщика' where mnemo='changeMainOrgEnable'
/
--changeset apavlenko:spo-17.56-VTBSPO-313 logicalFilePath:spo-17.56-VTBSPO-313 endDelimiter:/ runOnChange:true runAlways:true
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('alter table MDTASK add is_imported NUMBER(38)');
END;
/
--переименовать роль, но с проверкой на дубли
update roles r set r.name_role='Загрузчик из access' where r.name_role like 'загрузка из access'
and not exists (select 1 from roles r2 where r2.name_role like 'Загрузчик из access' and r.id_type_process=r2.id_type_process)
/
update roles r set r.name_role='Контролер загрузки из access' where r.name_role like 'контроль загрузки из access'
and not exists (select 1 from roles r2 where r2.name_role like 'Контролер загрузки из access' and r.id_type_process=r2.id_type_process)
/
insert into roles (id_role, name_role, id_type_process, active, is_admin)
select roles_seq.nextval,'Загрузчик из access',tp.id_type_process,1,0 from type_process tp where tp.description_process like 'Крупный бизнес ГО'
and not exists (select 1 from roles r where r.name_role like 'Загрузчик из access' and r.id_type_process=tp.id_type_process)
/
insert into roles (id_role, name_role, id_type_process, active, is_admin)
select roles_seq.nextval,'Контролер загрузки из access',tp.id_type_process,1,0 from type_process tp where tp.description_process like 'Крупный бизнес ГО'
and not exists (select 1 from roles r where r.name_role like 'Контролер загрузки из access' and r.id_type_process=tp.id_type_process)
/
update roles r set r.active=1 where r.name_role like 'Загрузчик из access'
/
update roles r set r.active=1 where r.name_role like 'Контролер загрузки из access'
/
