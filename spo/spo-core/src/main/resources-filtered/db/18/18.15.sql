--liquibase formatted sql

--changeset apavlenko:spo-18.15-VTBSPO-1162 logicalFilePath:spo-18.15-VTBSPO-1162 endDelimiter:/
alter table APPFILES add who_sign number
/
alter table APPFILES add date_of_sign date
/
comment on column APPFILES.who_sign is 'кто подписал. Это не всегда тот, кто добавил'
/
comment on column APPFILES.date_of_sign is 'дата подписания'
/
update APPFILES set who_sign = WHO_ADD
/
update APPFILES set date_of_sign = DATE_OF_ADDITION
/

--changeset pmasalov:VTBSPO-1181 logicalFilePath:VTBSPO-1181 endDelimiter:/
begin
for r in (select * from user_indexes i where i.index_name in ('SPO_SUM_HISTORY_I1','SPO_SUM_HISTORY_I2')) loop
execute immediate 'drop index ' || r.index_name;
end loop;
end;
/
create index spo_sum_history_i1 on spo_sum_history(save_date, id_status, id_mdtask) compress 2 tablespace spoindx
/
create index spo_sum_history_i2 on spo_sum_history(status_date) compress tablespace spoindx
/
--changeset apavlenko:spo-18.15-v_spo_task_timing-3 logicalFilePath:spo-18.15-v_spo_task_timing-3 endDelimiter:;
create or replace view v_spo_task_timing as
select t.mdtask_number,t.version,t.id_mdtask,pe.date_event as create_date,
 (select max(date_event) from process_events e
     inner join attributes a on a.id_process=e.id_process
     inner join variables v on v.id_var=a.id_var
 where e.id_process_type_event=4 and e.id_process=t.id_pup_process
       and v.name_var='Статус' and a.value_var='Отказано') as refuse_date,
 (select max(date_event) from process_events e
     inner join attributes a on a.id_process=e.id_process
     inner join variables v on v.id_var=a.id_var
 where e.id_process_type_event=4 and e.id_process=t.id_pup_process
       and v.name_var='Статус' and a.value_var='Одобрено') as accept_date,
 trunc((SELECT MIN(COM.END_STATUS_DATE)
        FROM CED_CREDIT_ENSURING_DOC CED JOIN CED_COMMON_DEAL_CONCLUSION COM ON COM.ID_COMMON_DEAL_CONCLUSION = CED.ID_COMMON_DEAL_CONCLUSION
        WHERE CED.IS_MAIN_CED_NOT_ADDITIONAL = 1
              AND COM.STATUS = 'CED_DRAWING_UP_COMPLETE'
              AND CED.CREDIT_DEAL_NUMBER = t.mdtask_number)) +9/24 as fix_date, --фиксация подписания Кредитного соглашения в модуле «КОД»
 trunc((SELECT MIN(COM.END_STATUS_DATE)
        FROM CED_COMMON_DEAL_CONCLUSION COM JOIN DP_PAYMENT DP ON DP.ID_COMMON = COM.ID_COMMON_DEAL_CONCLUSION
        WHERE COM.STATUS = 'PAYMENT_COMPLETED'
              AND COM.ID_MDTASK IN (SELECT ID_MDTASK FROM MDTASK WHERE MDTASK_NUMBER = t.mdtask_number))) + 9/24 as tranche,--«Выданные ден. средства»
 (select min(e.date_event) from task_events e
     inner join tasks tt on tt.id_task=e.id_task
     inner join stages s on s.id_stage=tt.id_stage_to
 where (s.description_stage like 'Формирование Предварительных параметров Лимита/условий Сделки'
        or s.description_stage like 'Изменение параметров Лимита/условий сделки'
        or s.description_stage like 'Структурирование и формирование проекта Кредитного решения'
       )
       and e.id_task_type_event=1 and tt.id_process=t.id_pup_process) as struct, --Анализ и структурирование
 (select min(e.date_event) from task_events e
     inner join tasks tt on tt.id_task=e.id_task
     inner join stages s on s.id_stage=tt.id_stage_to
 where s.description_stage like 'Акцепт перечня экспертиз%'
       and e.id_task_type_event=3 and tt.id_process=t.id_pup_process) as exper --Проведение экспертиз
from mdtask t
    inner join process_events pe on pe.id_process=t.id_pup_process and pe.id_process_type_event=1
where not exists (select 1 from attributes a inner join variables v on v.id_var=a.id_var where a.id_process=t.id_pup_process and v.name_var='Статус' and a.value_var='Ошибочно заведенная заявка')
order by t.mdtask_number desc;
