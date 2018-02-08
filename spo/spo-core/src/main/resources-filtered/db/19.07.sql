--liquibase formatted sql

--changeset slysenkov:spo-19.07-VTBSPO-1238-2 logicalFilePath:spo-19.07-VTBSPO-1238-2 endDelimiter:/
insert into report_template(id_template, template_name, type, filename, system, file_extension, mime_type, cc_resolution, is_active)
     select report_template_seq.nextval, 'Отчёт PDF для dashboard', 'PRINT_FORM_EXCEL', 'dashboard_report_4pdf_xlsx', 'DASHBOARD', 'xlsx', 'application/vnd.ms-excel', 1, 1 from dual
      where not exists (select 1
                          from report_template
                         where filename = 'dashboard_report_4pdf_xlsx'
                           and is_active = 1)
/
--changeset apavlenko:spo-19.01-VTBSPO-1251-1 logicalFilePath:spo-19.01-VTBSPO-1251-1 endDelimiter:/
delete from attributes where id_attr in(
    select a.id_attr from attributes a
        inner join variables v on a.id_var=v.id_var
    where v.name_var='Отказать'
          and exists (select 1 from attributes a2
    where a2.id_var=a.id_var and a2.id_process=a.id_process and a2.id_attr<a.id_attr)
)
/
--changeset apavlenko:spo-19.07-VTBSPO-1230-2 logicalFilePath:spo-19.07-VTBSPO-1230-2 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_OBJECT('spo_dash_user_settings',
                             'create table spo_dash_user_settings(
                                    user_id number not null,
                                    dash_setting VARCHAR2(4000)
                                )');
    PKG_DDL_UTILS.ADD_CONSTRAINT('spo_dash_user_settings_pk', 'alter table spo_dash_user_settings add constraint spo_dash_user_settings_pk primary key (user_id)');
END;
/
--changeset apavlenko:spo-19.07-VTBSPO-1230-3 logicalFilePath:spo-19.07-VTBSPO-1230-3 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_dash_user_settings', 'setting', 'CLOB');
END;
/
COMMENT ON COLUMN spo_dash_user_settings.setting IS 'Пользовательские настройки dashboards'
/
update spo_dash_user_settings set setting = dash_setting
/
--changeset apavlenko:spo-19.07-VTBSPO-1290 logicalFilePath:spo-19.07-VTBSPO-1290 endDelimiter:/
update spo_dashboard_status set status = 'Новые' where id_status = 1
/
update spo_dashboard_status set status = 'Одобренные' where id_status = 2
/
update spo_dashboard_status set status = 'Выборка' where id_status = 3
/
update spo_dashboard_status set status = 'Отказанные' where id_status = 4
/
update spo_dashboard_status set status = 'Новые' where id_status = 5
/
update spo_dashboard_status set status = 'Одобренные' where id_status = 14
/
update spo_dashboard_status set status = 'Отказанные' where id_status = 15
/
update spo_dashboard_status set status = 'Новые' where id_status = 7
/
update spo_dashboard_status set status = 'Одобренные' where id_status = 8
/
update spo_dashboard_status set status = 'Отказанные' where id_status = 9
/
update spo_dashboard_status set status = 'Новые' where id_status = 6
/
update spo_dashboard_status set status = 'Одобренные' where id_status = 10
/
update spo_dashboard_status set status = 'Отказанные' where id_status = 11
/
--changeset apavlenko:spo-19.07-VTBSPO-1293 logicalFilePath:spo-19.07-VTBSPO-1293 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_dash_user_settings', 'report_name', 'VARCHAR2(400)');
END;
/
COMMENT ON COLUMN spo_dash_user_settings.report_name IS 'Название отчёта'
/
alter table SPO_DASH_USER_SETTINGS drop constraint SPO_DASH_USER_SETTINGS_PK cascade
/
--changeset apavlenko:spo-19.07-VTBSPO-1293-2 logicalFilePath:spo-19.07-VTBSPO-1293-2 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_dash_user_settings', 'pub', 'NUMBER(1)');
END;
/
COMMENT ON COLUMN spo_dash_user_settings.pub IS 'Общий отчёт'
/
