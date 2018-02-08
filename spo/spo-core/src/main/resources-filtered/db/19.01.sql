--liquibase formatted sql

--changeset apavlenko:spo-19.01-SPO_CLIENT_REPORT logicalFilePath:spo-19.01-SPO_CLIENT_REPORT endDelimiter:/
create table SPO_CLIENT_REPORT
(
ID_MDTASK	NUMBER(38),
SAVE_DATE	DATE,
MDTASK_NUMBER	VARCHAR2(200),
VERSION	VARCHAR2(200),
CATEGORY	VARCHAR2(700),
STATE	VARCHAR2(200),
TASK_TYPE	VARCHAR2(200),
TASK_SUM	NUMBER(38,5),
CURRENCY	CHAR(3),
CUR_RATE	NUMBER(38,5),
RATE_DATE	TIMESTAMP(6),
SUM_RUB	NUMBER(38,5),
SUM_USD	NUMBER(38,5),
PERIOD_MONTH	NUMBER,
MARGIN	VARCHAR2(2000),
PROFIT	NUMBER(38,5),
WEEKS	NUMBER(9),
CREATE_DATE	TIMESTAMP(6),
PROPOSED_DT_SIGNING	TIMESTAMP(6),
PLAN_DATE	TIMESTAMP(6),
UPDATE_DATE	TIMESTAMP(6),
INDUSTRY	VARCHAR2(2000),
ID_GROUP	CHAR(12),
GROUP_NAME	VARCHAR2(2000),
ID_ORG	CHAR(12),
ID_KZ	CHAR(12),
MAIN_ORG	VARCHAR2(2000),
KZ_NAME	VARCHAR2(2000),
SUPPLY_ORG	VARCHAR2(2000),
STATUS	VARCHAR2(2000),
CLOSE_PROBABILITY	NUMBER(38,5),
PRODUCT_NAME	VARCHAR2(2000),
SUPPLY	VARCHAR2(60),
ENSURINGS	VARCHAR2(2000),
TARGETS	VARCHAR2(2000),
DESCRIPTION	VARCHAR2(2000),
CMNT	VARCHAR2(2000),
ADDITION_BUSINESS	VARCHAR2(2000),
USE_PERIOD_MONTH	NUMBER,
WAL	NUMBER,
FIXED_FLOAT	VARCHAR2(200),
BASE_RATE	NUMBER(38,5),
FIXRATE	NUMBER(38,5),
LOAN_RATE	NUMBER(38,5),
COMISSION	NUMBER(38,5),
PC_DER	NUMBER(38,5),
PC_TOTAL	NUMBER(38,5),
LINE_COUNT	NUMBER(38,5),
AVAILIBLE_LINE_VOLUME	NUMBER(38,5),
PUB	CHAR(1),
PRIORITY	CHAR(1),
NEW_CLIENT	CHAR(1),
FLOW_INVESTMENT	VARCHAR2(20),
PRODUCT_MANAGER	VARCHAR2(2000),
ANALYST	VARCHAR2(2000),
CLIENT_MANAGER	VARCHAR2(2000),
STRUCTURATOR	VARCHAR2(2000),
GSS	VARCHAR2(2000),
CONTRACTOR	VARCHAR2(200),
VTB_CONTRACTOR	VARCHAR2(200),
TRADE_DESC	VARCHAR2(200),
PROLONGATION	CHAR(1),
PROJECT_NAME	VARCHAR2(200)
)
/
--changeset apavlenko:spo-19.01-V_CLIENT_REPORT logicalFilePath:spo-19.01-V_CLIENT_REPORT endDelimiter:/
create or replace view V_CLIENT_REPORT as
select * from SPO_CLIENT_REPORT
/
--changeset apavlenko:spo-19.01-spo_sum_history_dep logicalFilePath:spo-19.01-spo_sum_history_dep endDelimiter:/
create table spo_sum_history_dep
(
    spo_sum_history_id number not null,
    department_id      number not null
)
/
alter table spo_sum_history_dep add constraint spo_sum_history_dep_fk1 foreign key (SPO_SUM_HISTORY_ID)
references spo_sum_history (ID_SUM_HISTORY) on delete cascade
/
alter table spo_sum_history_dep add constraint spo_sum_history_dep_fk2 foreign key (DEPARTMENT_ID)
references departments (ID_DEPARTMENT) on delete cascade
/

--changeset pmasalov:spo-19.01-VTBSPO-1161 logicalFilePath:spo-19.01-VTBSPO-1161 endDelimiter:/
create or replace type cor_number_a as table of number
/
begin
  for r in (select * from user_indexes i where i.index_name = 'SPO_SUM_HISTORY_I1') loop
      execute immediate 'drop index ' || r.INDEX_NAME;
  end loop;
end;
/
create index spo_sum_history_i1 on spo_sum_history(save_date, id_mdtask, id_status) tablespace spoindx
/
--changeset pmasalov:spo-19.01-VTBSPO-1161-00 logicalFilePath:spo-19.01-VTBSPO-1161-00 endDelimiter:/
delete from SPO_SUM_HISTORY_DEP
/
--changeset pmasalov:spo-19.01-VTBSPO-1161-01 logicalFilePath:spo-19.01-VTBSPO-1161-01 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_CONSTRAINT('SPO_SUM_HISTORY_DEP_PK',
                            'alter table SPO_SUM_HISTORY_DEP add constraint SPO_SUM_HISTORY_DEP_PK primary key (SPO_SUM_HISTORY_ID, DEPARTMENT_ID) using index tablespace SPOINDX');
END;
/

--changeset slysenkov:spo-19.01-vtbspo-1137-6 logicalfilepath:spo-19.01-vtbspo-1137-6 enddelimiter:/
insert into report_template(id_template, template_name, type, filename, system, file_extension, mime_type, cc_resolution, is_active)
     select report_template_seq.nextval, 'Отчёт для dashboard', 'PRINT_FORM_EXCEL', 'dashboard_report_xlsx', 'DASHBOARD', 'xlsx', 'application/vnd.ms-excel', 1, 1 from dual
      where not exists (select 1
                          from report_template
                         where filename = 'dashboard_report_xlsx'
                           and is_active = 1)
/

--changeset apavlenko:spo-19.01-spo_sum_history_orgname logicalFilePath:spo-19.01-spo_sum_history_orgname endDelimiter:/
alter table SPO_SUM_HISTORY add orgname VARCHAR2(2000)
/
comment on column SPO_SUM_HISTORY.orgname is 'осн.заемщик'
/
--changeset apavlenko:spo-19.01-spo_sum_history_orgname-2 logicalFilePath:spo-19.01-spo_sum_history_orgname-2 endDelimiter:/
update SPO_SUM_HISTORY h set h.orgname=(select ek.name from mdtask t
    inner join crm_ek ek on ek.id=t.main_org
where t.id_mdtask=h.id_mdtask)
/
--changeset apavlenko:spo-19.01-spo_sum_history_indrate logicalFilePath:spo-19.01-spo_sum_history_indrate endDelimiter:/
create table spo_sum_history_indrate
(
    spo_sum_history_id number not null,
    RATE      NUMBER(38,5),
    indrate VARCHAR2(1000)
)
/
alter table spo_sum_history_indrate add constraint spo_sum_history_indrate_fk1 foreign key (SPO_SUM_HISTORY_ID)
references spo_sum_history (ID_SUM_HISTORY) on delete cascade
/
--changeset pmasalov:spo-19.01-VTBSPO-1175 logicalFilePath:spo-19.01-VTBSPO-1175 endDelimiter:/
begin
  for r in (select * from user_indexes i where i.index_name = 'CRM_FB_EXCHANGERATE_I1') loop
    execute immediate 'drop index ' || r.INDEX_NAME;
  end loop;
end;
/
create index crm_fb_exchangerate_i1 on crm_fb_exchangerate(activedate, currencycode) tablespace spoindx
/
--changeset apavlenko:spo-19.01-VTBSPO-1170-initdep logicalFilePath:spo-19.01-VTBSPO-1170-initdep endDelimiter:/
alter table SPO_SUM_HISTORY add initdepartment VARCHAR2(2000)
/
--changeset apavlenko:spo-19.01-VTBSPO-1188 logicalFilePath:spo-19.01-VTBSPO-1188 endDelimiter:/
alter table SPO_SUM_HISTORY add interest_rate_fixed NUMBER(1) default 0
/
alter table SPO_SUM_HISTORY add interest_rate_derivative NUMBER(1) default 0
/
--changeset apavlenko:spo-19.01-showDashboardLink logicalFilePath:spo-19.01-showDashboardLink endDelimiter:/
insert into GLOBAL_SETTINGS(MNEMO,DESCRIPTION,value,system)
select 'showDashboardLink','Показывать ссылку на Отчёты Pipeline в меню','false','СПО' from dual
where not exists (select * from GLOBAL_SETTINGS where mnemo='showDashboardLink')
/
--changeset apavlenko:spo-19.01-VTBSPO-1153 logicalFilePath:spo-19.01-VTBSPO-1153 endDelimiter:/
create table spo_cc_status_map
(
    cc_status_id number not null,
    spo_status   VARCHAR2(2000) not null
)
/
alter table spo_cc_status_map add constraint spo_cc_status_map_pk primary key (cc_status_id)
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (1, 'КК. Формирование пакета документов')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (2, 'КК. Формирование пакета документов')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (3, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (4, 'КК. Возвращен на доработку инициатору')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (5, 'КК. Ожидание включения в повестку заседания')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (6, 'КК. Включен в повестку заседания')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (7, 'КК. Оформление результатов голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (8, 'КК. Оформление результатов голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (9, 'КК. Оформление результатов голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (10, 'КК. Оформление результатов голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (12, 'КК. Отозван инициатором')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (13, 'КК. Вопрос закрыт')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (14, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (15, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (16, 'КК. На утверждении Председателем КК')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (17, 'КК. На утверждении членами КК')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (18, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (19, 'КК. Формирование пакета документов')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (20, 'КК. Отказано во включении в повестку')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (21, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (22, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (23, 'КК. Проверка корректности и комплектности секретарем')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (24, 'КК. Оформление результатов голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (25, 'КК. Ожидает включения в протокол заочного голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (26, 'КК. Включен в протокол заочного голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (29, 'КК. Формулировка решения по вопросу инициатором')
/
--changeset apavlenko:spo-19.01-VTBSPO-1153-2 logicalFilePath:spo-19.01-VTBSPO-1153-2 endDelimiter:/
delete from spo_cc_status_map where cc_status_id=26
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (26, 'КК. Ожидает включения в протокол заочного голосования')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (28, 'КК. Возвращен на доработку')
/
insert into spo_cc_status_map(cc_status_id,spo_status) values (27, 'КК. Включен в протокол заочного голосования')
/
--changeset apavlenko:spo-19.01-VTBSPO-1204 logicalFilePath:spo-19.01-VTBSPO-1204 endDelimiter:/
alter table MDTASK modify statusreturntext VARCHAR2(4000)
/
--changeset apavlenko:spo-19.01-VTBSPO-1212 logicalFilePath:spo-19.01-VTBSPO-1212 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('mdtask', 'question_group', 'number');
    PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('mdtask', 'question_group', 'question_group_seq');
END;
/
comment on column MDTASK.question_group  is 'группа вопроса'
/
--changeset apavlenko:spo-19.01-VTBSPO-1213 logicalFilePath:spo-19.01-VTBSPO-1213 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('mdtask', 'period_days', 'number');
END;
/
comment on column MDTASK.period_days  is 'срок в днях'
/
--changeset apavlenko:spo-19.01-VTBSPO-1213-old logicalFilePath:spo-19.01-VTBSPO-1213-old endDelimiter:/
update mdtask t set t.period_days=t.period where periodDimension='дн.'
/
update mdtask t set t.period_days=t.period*30 where periodDimension='мес.'
/
update mdtask t set t.period_days=t.period*365 where periodDimension='г./лет'
/
--changeset apavlenko:spo-19.01-spo_sum_history_dep-init logicalFilePath:spo-19.01-spo_sum_history_dep-init endDelimiter:/
INSERT INTO spo_sum_history_dep (DEPARTMENT_ID, SPO_SUM_HISTORY_ID)
select distinct u.id_department,h.id_sum_history
from spo_sum_history h
    inner join project_team pt on pt.id_mdtask=h.id_mdtask
    inner join users u on u.id_user=pt.id_user
where h.id_sum_history not in
      (select hd.spo_sum_history_id from spo_sum_history_dep hd)
/
--changeset apavlenko:spo-19.01-VTBSPO-1232 logicalFilePath:spo-19.01-VTBSPO-1232 endDelimiter:/
create table spo_4cc_appfiles
(
    fileid VARCHAR2(128) not null,
    ID_MDTASK	NUMBER(38) not null
)
/
alter table spo_4cc_appfiles add constraint spo_4cc_appfiles_fk1 foreign key (ID_MDTASK)
references MDTASK (id_mdtask) on delete cascade
/
alter table spo_4cc_appfiles add constraint spo_4cc_appfiles_fk2 foreign key (fileid)
references APPFILES (UNID) on delete cascade
/
insert into spo_4cc_appfiles(fileid,ID_MDTASK)
select d.UNID,d.ID_MDTASK from v_cc_doc d
/
create index spo_4cc_appfiles_i1 on spo_4cc_appfiles(ID_MDTASK) tablespace spoindx
/
create index spo_4cc_appfiles_i2 on spo_4cc_appfiles(fileid) tablespace spoindx
/
--changeset apavlenko:spo-19.01-VTBSPO-1232-V_CC_DOC logicalFilePath:spo-19.01-VTBSPO-1232-V_CC_DOC endDelimiter:/
CREATE OR REPLACE VIEW V_CC_DOC AS
SELECT T.ID_MDTASK, A.UNID,A.FILENAME, A.FILEURL, A.FILEDATA, NVL(A.TITLE, A.FILENAME) AS TITLE, COALESCE (D.NAME_DOCUMENT_TYPE, A.FILETYPE) FILETYPE,
    A.ID_OWNER, A.OWNER_TYPE, A.WHO_ADD, A.DATE_OF_ADDITION, A.DATE_OF_EXPIRATION, A.ISACCEPTED, A.WHOACCEPTED, A.DATE_OF_ACCEPT, A.SIGNATURE, A.ID_APPL, A.ID_GROUP, A.FORCC, A.CONTENTTYPE
FROM APPFILES A
INNER JOIN spo_4cc_appfiles T ON T.fileid = A.UNID
LEFT OUTER JOIN DOCUMENTS_TYPE D ON D.ID_DOCUMENT_TYPE = A.ID_DOCUMENT_TYPE
WHERE  A.WHO_DEL IS NULL
/
--changeset apavlenko:spo-19.01-VTBSPO-1215 logicalFilePath:spo-19.01-VTBSPO-1215 endDelimiter:/
alter table appfiles add reason VARCHAR2(4000)
/
--changeset apavlenko:spo-19.01-VTBSPO-1241 logicalFilePath:spo-19.01-VTBSPO-1241 endDelimiter:/
alter table SPO_SUM_HISTORY add branch VARCHAR2(2000)
/
--changeset apavlenko:spo-19.01-VTBSPO-1241-u logicalFilePath:spo-19.01-VTBSPO-1241-u endDelimiter:/
update spo_sum_history hu set hu.branch=
(select max(cr.branch) from spo_sum_history h
    inner join r_org_mdtask r on r.id_mdtask=h.id_mdtask and r.order_disp=0
    inner join cr_calc_history cr on cr.partnerid=r.id_crmorg
where cr.branch is not null and h.id_sum_history=hu.id_sum_history
      and not exists (select 1 from cr_calc_history cr2 where cr2.id>cr.id and cr.partnerid=cr2.partnerid))
/
--changeset apavlenko:spo-19.01-VTBSPO-1241-u-2 logicalFilePath:spo-19.01-VTBSPO-1241-u-2 endDelimiter:/
update spo_sum_history hu set hu.branch=
(select max(o.industry) from spo_sum_history h
    inner join r_org_mdtask r on r.id_mdtask=h.id_mdtask and r.order_disp=0
    inner join crm_organization o on r.id_crmorg=o.id_org
where o.industry is not null and o.industry not like 'Не определено' and h.id_sum_history=hu.id_sum_history)
/
--changeset apavlenko:spo-19.01-VTBSPO-1243 logicalFilePath:spo-19.01-VTBSPO-1243 endDelimiter:/
update SPO_DASHBOARD_STATUS t set t.status='В работе' where t.id_status=12
/
update SPO_DASHBOARD_STATUS t set t.status='deprecated' where t.id_status=13
/
--changeset apavlenko:spo-19.01-VTBSPO-1243-2 logicalFilePath:spo-19.01-VTBSPO-1243-2 endDelimiter:/
delete from spo_sum_history h where h.id_status=13 and exists (select 1 from spo_sum_history h12 where h12.id_status=12 and h.id_mdtask=h12.id_mdtask and h.save_date=h12.save_date)
/
update spo_sum_history h set h.id_status=12 where h.id_status=13
/
--changeset pmasalov:spo-19.01-VTBSPO-1243-3 logicalFilePath:spo-19.01-VTBSPO-1243-3 endDelimiter:/
delete SPO_DASHBOARD_STATUS t where t.id_status=13
/
