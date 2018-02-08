--liquibase formatted sql

--changeset apavlenko:spo-18.14-VTBSPO-1107 logicalFilePath:spo-18.14-VTBSPO-1107 endDelimiter:/
create table SPO_SUM_HISTORY
(
  ID_SUM_HISTORY        number not null,
  sum       number,
  currency  char(3),
  change_date      timestamp,
  id_mdtask number not null
)
/
alter table SPO_SUM_HISTORY add constraint SPO_SUM_HISTORY_PK primary key (ID_SUM_HISTORY)
/
alter table SPO_SUM_HISTORY
  add constraint SPO_SUM_HISTORY_FK1 foreign key (ID_MDTASK)
  references mdtask (ID_MDTASK) on delete cascade
/
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
(SELECT MIN(COM.END_STATUS_DATE)
  FROM CED_CREDIT_ENSURING_DOC CED JOIN CED_COMMON_DEAL_CONCLUSION COM ON COM.ID_COMMON_DEAL_CONCLUSION = CED.ID_COMMON_DEAL_CONCLUSION
 WHERE CED.IS_MAIN_CED_NOT_ADDITIONAL = 1
   AND COM.STATUS = 'CED_DRAWING_UP_COMPLETE'
   AND CED.CREDIT_DEAL_NUMBER = t.mdtask_number) as fix_date, --фиксация подписания Кредитного соглашения в модуле «КОД»
   (SELECT MIN(COM.END_STATUS_DATE)
  FROM CED_COMMON_DEAL_CONCLUSION COM JOIN DP_PAYMENT DP ON DP.ID_COMMON = COM.ID_COMMON_DEAL_CONCLUSION
 WHERE COM.STATUS = 'PAYMENT_COMPLETED'
   AND COM.ID_MDTASK IN (SELECT ID_MDTASK FROM MDTASK WHERE MDTASK_NUMBER = t.mdtask_number)) as tranche,--«Выданные ден. средства»
(select min(e.date_event) from task_events e
inner join tasks tt on tt.id_task=e.id_task
inner join stages s on s.id_stage=tt.id_stage_to
where s.description_stage like 'Формирование Предварительных параметров Лимита/условий Сделки'
and e.id_task_type_event=1 and tt.id_process=t.id_pup_process) as struct, --Анализ и структурирование
(select min(e.date_event) from task_events e
inner join tasks tt on tt.id_task=e.id_task
inner join stages s on s.id_stage=tt.id_stage_to
where s.description_stage like 'Акцепт перечня экспертиз'
and e.id_task_type_event=3 and tt.id_process=t.id_pup_process) as exper --Проведение экспертиз
from mdtask t
inner join process_events pe on pe.id_process=t.id_pup_process and pe.id_process_type_event=1
order by t.mdtask_number desc
/
BEGIN
PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('SPO_SUM_HISTORY', 'ID_SUM_HISTORY', 'SPO_SUM_HISTORY_SEQ');
END;
/
--changeset slysenkov:VTBSPO-1112-2 logicalFilePath:VTBSPO-1112-2 endDelimiter:/
BEGIN
PKG_DDL_UTILS.ADD_TABLE_COLUMN('TASKCOMMENT', 'COMMENT_BODY_HTML', 'CLOB');
END;
/
COMMENT ON COLUMN TASKCOMMENT.COMMENT_BODY_HTML IS 'Форматированный комментарий'
/
update TASKCOMMENT set COMMENT_BODY_HTML = COMMENT_BODY
/
--changeset apavlenko:spo-18.14-VTBSPO-1113 logicalFilePath:spo-18.14-VTBSPO-1113 endDelimiter:/
create table spo_standard_period_report
(  id_mdtask NUMBER not null,
mdtask_number VARCHAR2(200),--Номер заявки
org VARCHAR2(700),--Контрагент
task_sum NUMBER(38,5),--Сумма
currency char(3),--Валюта
task_type VARCHAR2(200),--Тип заявки
status  VARCHAR2(200),--Статус
init_dep VARCHAR2(700),--Инициирующее подразделение
client_manager VARCHAR2(700),--Клиентский менеджер
proguct_manager VARCHAR2(700),--Продуктовый менеджер
structurator VARCHAR2(700),--Структуратор
analist VARCHAR2(700),--Кредитный аналитик
stage_name VARCHAR2(700),--Название этапа
start_date TIMESTAMP(6),--Начало
end_date TIMESTAMP(6),--Конец
period NUMBER,--Норматив
cmnt VARCHAR2(2000),--Комментарии по стадии
cmnt_user VARCHAR2(200),--ФИО разместившего комментарии
cmnt_dep VARCHAR2(200)--Подразделение разместившего комментарии
)
/
create or replace view v_spo_standard_period_report as
select id_mdtask, mdtask_number, org, task_sum, currency, task_type, status, init_dep, client_manager, proguct_manager, structurator, analist, stage_name, start_date, end_date, period, cmnt, cmnt_user, cmnt_dep from spo_standard_period_report
/
--changeset apavlenko:spo-18.14-VTBSPO-1113-2 logicalFilePath:spo-18.14-VTBSPO-1113-2 endDelimiter:/
alter table SPO_STANDARD_PERIOD_REPORT modify cmnt VARCHAR2(4000)
/
--changeset apavlenko:spo-18.14-VTBSPO-1113-3 logicalFilePath:spo-18.14-VTBSPO-1113-3 endDelimiter:/
alter table SPO_STANDARD_PERIOD_REPORT modify CMNT_USER VARCHAR2(4000)
/
--changeset apavlenko:spo-18.14-VTBSPO-1113-4 logicalFilePath:spo-18.14-VTBSPO-1113-4 endDelimiter:/
create or replace view v_spo_standard_period as
select id_mdtask, status, init_dep, stage_name, start_date, end_date, period, cmnt, cmnt_user, cmnt_dep from spo_standard_period_report
/
--changeset apavlenko:spo-18.14-dashboard logicalFilePath:spo-18.14-dashboard endDelimiter:/
create table spo_dashboard_status
(
  id_status number not null,
  status    varchar2(300) not null,
  task_type varchar2(300) not null
)
/
comment on table spo_dashboard_status
  is 'статусы заявки для отчета pipeline'
/
comment on column spo_dashboard_status.task_type
  is 'тип заявки'
/
alter table spo_dashboard_status
  add constraint spo_dashboard_status_pk primary key (ID_STATUS)
/
insert into spo_dashboard_status (id_status, status, task_type)
values (1, 'Новые сделки', 'product')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (2, 'Заключенные сделки', 'product')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (3, 'Выданные ден. средства', 'product')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (4, 'Потерянные сделки', 'product')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (5, 'Новые лимиты', 'limit')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (6, 'Новые кросс-селл', 'cross-sell')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (7, 'Новые вейверы', 'waiver')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (8, 'заключенные вейверы', 'waiver')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (9, 'потерянные', 'waiver')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (10, 'заключенные сделки', 'cross-sell')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (11, 'потерянные', 'cross-sell')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (12, 'Анализ и структурирование', 'limit')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (13, 'Проведение экспертиз', 'limit')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (14, 'Одобрено', 'limit')
/
insert into spo_dashboard_status (id_status, status, task_type)
values (15, 'Отказано', 'limit')
/
--changeset apavlenko:spo-18.14-SPO_SUM_HISTORY-2 logicalFilePath:spo-18.14-SPO_SUM_HISTORY-2 endDelimiter:/
drop table SPO_SUM_HISTORY
/
create table SPO_SUM_HISTORY
(
  ID_SUM_HISTORY        number not null,
  sum       number,
  currency  char(3),
  id_mdtask number not null
)
/
alter table SPO_SUM_HISTORY add constraint SPO_SUM_HISTORY_PK primary key (ID_SUM_HISTORY)
/
alter table SPO_SUM_HISTORY
  add constraint SPO_SUM_HISTORY_FK1 foreign key (ID_MDTASK)
  references mdtask (ID_MDTASK) on delete cascade
/
alter table SPO_SUM_HISTORY add status_date timestamp not null
/
alter table SPO_SUM_HISTORY add id_status number not null
/
alter table SPO_SUM_HISTORY add save_date date  not null
/
alter table SPO_SUM_HISTORY add id_department number
/
alter table SPO_SUM_HISTORY add is_documentary number(1)
/
alter table SPO_SUM_HISTORY add is_credit_deal number(1)
/
comment on column SPO_SUM_HISTORY.sum is 'сумма'
/
comment on column SPO_SUM_HISTORY.currency is 'валюта'
/
comment on column SPO_SUM_HISTORY.status_date is 'Дата смены статуса'
/
comment on column SPO_SUM_HISTORY.id_status is 'статус'
/
comment on column SPO_SUM_HISTORY.save_date is 'дата актуальности данных'
/
comment on column SPO_SUM_HISTORY.id_department is 'подразделение'
/
comment on column SPO_SUM_HISTORY.is_documentary is 'документарная заявка'
/
comment on column SPO_SUM_HISTORY.is_credit_deal is 'кредитная заявка'
/
alter table SPO_SUM_HISTORY
  add constraint SPO_SUM_HISTORY_FK2 foreign key (ID_STATUS)
  references spo_dashboard_status (ID_STATUS) on delete cascade
/
alter table SPO_SUM_HISTORY
  add constraint SPO_SUM_HISTORY_FK3 foreign key (ID_DEPARTMENT)
  references departments (ID_DEPARTMENT) on delete cascade
/
alter table SPO_SUM_HISTORY modify sum NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add sum_rur NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add sum_usd NUMBER(38,5)
/
comment on column SPO_SUM_HISTORY.sum_rur is 'сумма в рублях'
/
comment on column SPO_SUM_HISTORY.sum_usd is 'сумма в долларах США'
/
--changeset apavlenko:spo-18.14-SPO_SUM_HISTORY-credit_documentary logicalFilePath:spo-18.14-SPO_SUM_HISTORY-credit_documentary endDelimiter:/
alter table SPO_SUM_HISTORY drop column is_documentary
/
alter table SPO_SUM_HISTORY drop column is_credit_deal
/
alter table SPO_SUM_HISTORY add credit_documentary NUMBER(1)
/
comment on column SPO_SUM_HISTORY.credit_documentary
  is '1 - кредитная, 2 - документарная, 0 - никакая'
/
--changeset apavlenko:spo-18.14-SPO_SUM_HISTORY-4 logicalFilePath:spo-18.14-SPO_SUM_HISTORY-4 endDelimiter:/
alter table SPO_SUM_HISTORY drop column id_department
/
alter table SPO_SUM_HISTORY drop column sum_rur
/
alter table SPO_SUM_HISTORY drop column sum_usd
/
--changeset apavlenko:spo-18.14-SPO_SUM_HISTORY-6 logicalFilePath:spo-18.14-SPO_SUM_HISTORY-6 endDelimiter:/
alter table SPO_SUM_HISTORY add period_month number
/
alter table SPO_SUM_HISTORY add margin VARCHAR2(2000)
/
alter table SPO_SUM_HISTORY add profit NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add wal number
/
alter table SPO_SUM_HISTORY add line_count NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add availible_line_volume NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add sum_probability NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add weeks number
/
alter table SPO_SUM_HISTORY add status_pipeline VARCHAR2(2000)
/
alter table SPO_SUM_HISTORY add product_name VARCHAR2(2000)
/
alter table SPO_SUM_HISTORY add loan_rate NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add trade_desc VARCHAR2(200)
/
alter table SPO_SUM_HISTORY add proposed_dt_signing TIMESTAMP(6)
/
alter table SPO_SUM_HISTORY add prolongation number(1)
/
alter table SPO_SUM_HISTORY add contractor VARCHAR2(200)
/
alter table SPO_SUM_HISTORY add vtb_contractor VARCHAR2(200)
/
alter table SPO_SUM_HISTORY add use_period_month number
/
alter table SPO_SUM_HISTORY add rate NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add pub number(1)
/
alter table SPO_SUM_HISTORY add cmnt VARCHAR2(2000)
/
alter table SPO_SUM_HISTORY add plan_date TIMESTAMP(6)
/
alter table SPO_SUM_HISTORY add sum_last NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add close_probability NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add comission NUMBER(38,5)
/
alter table SPO_SUM_HISTORY add groupname VARCHAR2(2000)
/
comment on column SPO_SUM_HISTORY.period_month  is 'срок, мес.'
/
comment on column SPO_SUM_HISTORY.margin  is 'маржа, %'
/
comment on column SPO_SUM_HISTORY.profit  is 'Ожидаемая доходность'
/
comment on column SPO_SUM_HISTORY.wal  is 'Средневзвешенный срок сделки (WAL), мес.'
/
comment on column SPO_SUM_HISTORY.line_count  is 'Выбранный объем линии'
/
comment on column SPO_SUM_HISTORY.availible_line_volume  is 'Объем линии, доступный для выборки'
/
comment on column SPO_SUM_HISTORY.sum_probability  is 'Сумма с учетом вероятности'
/
comment on column SPO_SUM_HISTORY.weeks  is 'недель в пайплайне'
/
comment on column SPO_SUM_HISTORY.status_pipeline  is 'Стадия'
/
comment on column SPO_SUM_HISTORY.product_name  is 'Вид сделки'
/
comment on column SPO_SUM_HISTORY.loan_rate  is 'Ставка фондирования, %'
/
comment on column SPO_SUM_HISTORY.trade_desc  is 'Трейдинг Деск'
/
comment on column SPO_SUM_HISTORY.proposed_dt_signing  is 'Плановая дата подписания КОД'
/
comment on column SPO_SUM_HISTORY.prolongation  is 'Пролонгация'
/
comment on column SPO_SUM_HISTORY.contractor  is 'Фондирующий Банк'
/
comment on column SPO_SUM_HISTORY.vtb_contractor  is 'Выдающий Банк'
/
comment on column SPO_SUM_HISTORY.use_period_month  is 'Срок использования, мес.'
/
comment on column SPO_SUM_HISTORY.rate  is '% ставка (1й период)'
/
comment on column SPO_SUM_HISTORY.pub  is 'Возможность залога в ЦБ (312-П)'
/
comment on column SPO_SUM_HISTORY.cmnt  is 'Комментарии'
/
comment on column SPO_SUM_HISTORY.plan_date  is 'Плановая даты выборки'
/
comment on column SPO_SUM_HISTORY.sum_last  is 'Оставшаяся сумма к выдаче с учетом вероятности'
/
comment on column SPO_SUM_HISTORY.close_probability  is 'Вероятность закрытия, %'
/
comment on column SPO_SUM_HISTORY.comission  is 'Комиссия за выдачу, % годовых'
/
comment on column SPO_SUM_HISTORY.groupname  is 'ГК осн.заемщика'
/
--changeset apavlenko:spo-18.14-dashboard-margin logicalFilePath:spo-18.14-dashboard-margin endDelimiter:/
delete from spo_sum_history
/
alter table SPO_SUM_HISTORY modify margin NUMBER(38,5)
/
alter table PIPELINE rename column margin to MARGIN_STRING
/
alter table PIPELINE add margin NUMBER(38,5)
/
--changeset apavlenko:spo-18.14-SPO_DASHBOARD_STATUS-orderdisp logicalFilePath:spo-18.14-SPO_DASHBOARD_STATUS-orderdisp endDelimiter:/
alter table SPO_DASHBOARD_STATUS add orderdisp number
/
comment on column SPO_DASHBOARD_STATUS.orderdisp is 'порядок сортировки'
/
update SPO_DASHBOARD_STATUS set orderdisp=ID_STATUS
/
--changeset pmasalov:VTBSPO-1148 logicalFilePath:VTBSPO-1148 endDelimiter:/
begin
  for r in (select * from user_indexes i where i.index_name = 'SPO_SUM_HISTORY_I1') loop
    execute immediate 'drop index ' || r.index_name;
  end loop;
end;
/
create index spo_sum_history_i1 on spo_sum_history(save_date) tablespace spoindx
/
--changeset pmasalov:VTBSPO-1148-02 logicalFilePath:VTBSPO-1148-02 endDelimiter:/
update spo_dashboard_status set status = 'Новые сделки' where id_status = 1
/
update spo_dashboard_status set status = 'Заключенные сделки' where id_status = 2
/
update spo_dashboard_status set status = 'Выборка' where id_status = 3
/
update spo_dashboard_status set status = 'Потерянные сделки' where id_status = 4
/
update spo_dashboard_status set status = 'Начало работы по заявке' where id_status = 5
/
update spo_dashboard_status set status = 'Анализ и структурирование' where id_status = 12
/
update spo_dashboard_status set status = 'Проведение экспертиз' where id_status = 13
/
update spo_dashboard_status set status = 'Одобрено' where id_status = 14
/
update spo_dashboard_status set status = 'Отказано' where id_status = 15
/
update spo_dashboard_status set status = 'Новые сделки' where id_status = 7
/
update spo_dashboard_status set status = 'Заключенные сделки' where id_status = 8
/
update spo_dashboard_status set status = 'Потерянные' where id_status = 9
/
update spo_dashboard_status set status = 'Новые сделки' where id_status = 6
/
update spo_dashboard_status set status = 'Заключенные сделки' where id_status = 10
/
update spo_dashboard_status set status = 'Потерянные' where id_status = 11
/
commit
/

--changeset slysenkov:spo-18.14-VTBSPO-1128-4 logicalFilePath:spo-18.14-VTBSPO-1128-4 endDelimiter:/
BEGIN
PKG_DDL_UTILS.ADD_OBJECT('SPO_134_NOTIFICATION',
'create table SPO_134_NOTIFICATION(
id_stage NUMBER not null,
id_wait_stage NUMBER not null
)');
PKG_DDL_UTILS.ADD_CONSTRAINT('SPO_134_NOTIFICATION_FK01', 'ALTER TABLE SPO_134_NOTIFICATION ADD CONSTRAINT SPO_134_NOTIFICATION_FK01 FOREIGN KEY (id_stage) REFERENCES STAGES (ID_STAGE)');
PKG_DDL_UTILS.ADD_CONSTRAINT('SPO_134_NOTIFICATION_FK02', 'ALTER TABLE SPO_134_NOTIFICATION ADD CONSTRAINT SPO_134_NOTIFICATION_FK02 FOREIGN KEY (id_wait_stage) REFERENCES STAGES (ID_STAGE)');
PKG_DDL_UTILS.ADD_FOREIGN_KEY_INDEXES('', 'SPO_134_NOTIFICATION', 1);
END;
/
COMMENT ON TABLE SPO_134_NOTIFICATION IS 'Таблица собирающих операций и ожидаемых операций для исключения нотификаций пока все ожидаемые операции не завершены'
/
