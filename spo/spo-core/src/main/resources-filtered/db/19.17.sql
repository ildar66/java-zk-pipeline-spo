--liquibase formatted sql

--changeset apavlenko:spo-19.17-VTBSPO-1330 logicalFilePath:spo-19.17-VTBSPO-1330 endDelimiter:/
update spo_sum_history h set h.product_name=(select name from CD_CROSS_SELL cs INNER JOIN MDTASK t on t.CROSS_SELL_TYPE=cs.id where t.id_mdtask=h.id_mdtask)
where h.id_status in (select t.id_status from SPO_DASHBOARD_STATUS t where t.task_type='cross-sell')
/
--changeset apavlenko:spo-19.17-VTBSPO-1339 logicalFilePath:spo-19.17-VTBSPO-1339 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_dash_user_settings', 'settings_id', 'number');
    PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('spo_dash_user_settings', 'settings_id', 'spo_dash_user_settings_seq');
END;
/
update spo_dash_user_settings set settings_id=spo_dash_user_settings_seq.nextval
/
--changeset apavlenko:spo-19.17-VTBSPO-1339-PK logicalFilePath:spo-19.17-VTBSPO-1339-PK endDelimiter:/
alter table SPO_DASH_USER_SETTINGS add constraint SPO_DASH_USER_SETTINGS_PK primary key (SETTINGS_ID)
/
--changeset apavlenko:spo-19.17-SPO_4CC_APPFILES-uniq logicalFilePath:spo-19.17-SPO_4CC_APPFILES-uniq endDelimiter:/
create table SPO_4CC_APPFILES_TMP
(
    fileid    VARCHAR2(128) not null,
    id_mdtask NUMBER(38) not null
)
/
insert into SPO_4CC_APPFILES_TMP(FILEID, ID_MDTASK)
    select distinct FILEID, ID_MDTASK from SPO_4CC_APPFILES
/
truncate table SPO_4CC_APPFILES
/
insert into SPO_4CC_APPFILES(FILEID, ID_MDTASK)
    select distinct FILEID, ID_MDTASK from SPO_4CC_APPFILES_TMP
/
alter table SPO_4CC_APPFILES
    add constraint SPO_4CC_APPFILES_UK1 unique (FILEID, ID_MDTASK)
/
drop table SPO_4CC_APPFILES_TMP
/
--changeset apavlenko:spo-19.17-VTBSPO-1356 logicalFilePath:spo-19.17-VTBSPO-1356 endDelimiter:/
ALTER TABLE PIPELINE ADD STATUS_MANUAL CHAR(1) DEFAULT 'n'
/
COMMENT ON COLUMN PIPELINE.STATUS_MANUAL IS 'Ручное управление стадиями'
/
--changeset apavlenko:spo-19.17-VTBSPO-1356-default logicalFilePath:spo-19.17-VTBSPO-1356-default endDelimiter:/
update PIPELINE set STATUS_MANUAL='y'
/
--changeset slysenkov:spo-19.17-VTBSPO-1331-2 logicalFilePath:spo-19.17-VTBSPO-1331-2 endDelimiter:/
begin
   pkg_ddl_utils.add_table_column('report_template', 'reporting_engine', 'number(1) default 0');
end;
/
update report_template
   set reporting_engine = 0
 where reporting_engine is null
/
comment on column report_template.reporting_engine is 'признак использования Apsose ReportingEngine'
/
create or replace view v_report_template as select t.* from report_template t where t.is_active = 1
/
--changeset apavlenko:spo-19.17-V_CLIENT_REPORT-2 logicalFilePath:spo-19.17-V_CLIENT_REPORT-2 endDelimiter:/
create or replace view v_client_report as
select (select p.id_type_process from mdtask t
inner join processes p on p.id_process=t.id_pup_process
where t.id_mdtask=r.id_mdtask) idTypeProcess,
(select tp.description_process from mdtask t
inner join processes p on p.id_process=t.id_pup_process
inner join type_process tp on tp.id_type_process=p.id_type_process
where t.id_mdtask=r.id_mdtask) typeProcess,
"ID_MDTASK","SAVE_DATE","MDTASK_NUMBER","VERSION","CATEGORY","STATE","TASK_TYPE","TASK_SUM","CURRENCY","CUR_RATE","RATE_DATE","SUM_RUB","SUM_USD",
"PERIOD_MONTH","MARGIN","PROFIT","WEEKS",
(select min(p.DATE_EVENT) from mdtask t
inner join process_events p on p.id_process=t.id_pup_process
where t.id_mdtask=r.id_mdtask) "CREATE_DATE",
"PROPOSED_DT_SIGNING","PLAN_DATE","UPDATE_DATE","INDUSTRY","ID_GROUP","GROUP_NAME","ID_ORG","ID_KZ",
"MAIN_ORG","KZ_NAME","SUPPLY_ORG","STATUS","CLOSE_PROBABILITY","PRODUCT_NAME","SUPPLY","ENSURINGS","TARGETS","DESCRIPTION","CMNT","ADDITION_BUSINESS",
"USE_PERIOD_MONTH","WAL","FIXED_FLOAT","BASE_RATE","FIXRATE","LOAN_RATE","COMISSION","PC_DER","PC_TOTAL","LINE_COUNT","AVAILIBLE_LINE_VOLUME","PUB",
"PRIORITY","NEW_CLIENT","FLOW_INVESTMENT","PRODUCT_MANAGER","ANALYST","CLIENT_MANAGER","STRUCTURATOR","GSS","CONTRACTOR","VTB_CONTRACTOR","TRADE_DESC","PROLONGATION","PROJECT_NAME"
from SPO_CLIENT_REPORT r
/
--changeset apavlenko:spo-19.17-V_CLIENT_REPORT-4 logicalFilePath:spo-19.17-V_CLIENT_REPORT-4 endDelimiter:;
delete from SPO_CLIENT_REPORT r;
insert into SPO_CLIENT_REPORT(ID_MDTASK, SAVE_DATE, MDTASK_NUMBER, VERSION, CATEGORY, STATE, TASK_TYPE, TASK_SUM, CURRENCY, CUR_RATE, RATE_DATE, SUM_RUB, SUM_USD,
PERIOD_MONTH, MARGIN, PROFIT, WEEKS, CREATE_DATE, PROPOSED_DT_SIGNING, PLAN_DATE, UPDATE_DATE, INDUSTRY, ID_GROUP, GROUP_NAME, ID_ORG, ID_KZ, MAIN_ORG, KZ_NAME, SUPPLY_ORG,
STATUS, CLOSE_PROBABILITY, PRODUCT_NAME, SUPPLY, ENSURINGS, TARGETS, DESCRIPTION, CMNT, ADDITION_BUSINESS, USE_PERIOD_MONTH, WAL, FIXED_FLOAT, BASE_RATE, FIXRATE, LOAN_RATE,
COMISSION, PC_DER, PC_TOTAL, LINE_COUNT, AVAILIBLE_LINE_VOLUME, PUB, PRIORITY, NEW_CLIENT, FLOW_INVESTMENT, PRODUCT_MANAGER, ANALYST, CLIENT_MANAGER, STRUCTURATOR, GSS,
CONTRACTOR, VTB_CONTRACTOR, TRADE_DESC, PROLONGATION, PROJECT_NAME)
select
h."ID_MDTASK","SAVE_DATE","MDTASK_NUMBER","VERSION",
(SELECT A.Value_Var FROM ATTRIBUTES A
INNER JOIN VARIABLES V ON V.ID_VAR=A.ID_VAR AND V.NAME_VAR LIKE 'Тип кредитной заявки'
WHERE a.id_process=t.id_pup_process) "CATEGORY",
s.status "STATE",
(SELECT A.Value_Var FROM ATTRIBUTES A
INNER JOIN VARIABLES V ON V.ID_VAR=A.ID_VAR AND V.NAME_VAR LIKE 'Тип кредитной заявки'
WHERE a.id_process=t.id_pup_process) "TASK_TYPE",
h.sum "TASK_SUM",h."CURRENCY",'' "CUR_RATE",'' "RATE_DATE",'' "SUM_RUB",'' "SUM_USD","PERIOD_MONTH",h."MARGIN","PROFIT","WEEKS",
(select min(p.DATE_EVENT) from mdtask t
inner join process_events p on p.id_process=t.id_pup_process
where t.id_mdtask=h.id_mdtask) "CREATE_DATE",
h."PROPOSED_DT_SIGNING",h."PLAN_DATE",
(SELECT MAX(TE.DATE_EVENT)
FROM TASK_EVENTS TE JOIN TASKS Ta ON Ta.ID_TASK = TE.ID_TASK
inner join mdtask m on m.id_pup_process=ta.id_process
WHERE m.id_mdtask=t.id_mdtask ) "UPDATE_DATE",
o.industry,
(select max(gc_id) from CRM_COMPANIESGROUP_LINKED where gc4_id=ek.id) "ID_GROUP",
ek.groupname "GROUP_NAME",
f.id_united_client "ID_ORG",
r.id_crmorg "ID_KZ",
ek.name "MAIN_ORG",
o.organization_name "KZ_NAME",
'' "SUPPLY_ORG",p."STATUS",h."CLOSE_PROBABILITY",t.product_name,"SUPPLY",'' "ENSURINGS",'' "TARGETS",p."DESCRIPTION",h."CMNT","ADDITION_BUSINESS",
h.use_period_month,h.WAL,'' "FIXED_FLOAT",'' "BASE_RATE",'' "FIXRATE",h."LOAN_RATE","COMISSION","PC_DER","PC_TOTAL",p."LINE_COUNT","AVAILIBLE_LINE_VOLUME",p.PUB,
p."PRIORITY","NEW_CLIENT","FLOW_INVESTMENT",'' "PRODUCT_MANAGER",'' "ANALYST",'' "CLIENT_MANAGER",'' "STRUCTURATOR",'' "GSS",
h."CONTRACTOR",h."VTB_CONTRACTOR",h."TRADE_DESC",h."PROLONGATION",t.project_name
from spo_sum_history h
inner join mdtask t on t.id_mdtask=h.id_mdtask
inner join SPO_DASHBOARD_STATUS s on s.id_status=h.id_status
left outer join pipeline p on p.ID_MDTASK=t.ID_MDTASK
left outer join r_org_mdtask r on r.id_mdtask=h.id_mdtask and r.order_disp=0
left outer join crm_organization o on o.id_org=r.id_crmorg
left outer join crm_finance_org f on f.id_org=r.id_crmorg
left outer join crm_ek ek on ek.id=f.id_united_client
where h.save_date >=to_date('01.01.2016', 'dd.mm.yyyy')
and not exists (select 1 from spo_sum_history h2 where h.id_mdtask=h2.id_mdtask and h.save_date < h2.save_date);