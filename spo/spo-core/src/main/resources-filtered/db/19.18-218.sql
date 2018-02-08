--liquibase formatted sql

--changeset apavlenko:spo-19.18-VTBSPO-1606-3 logicalFilePath:spo-19.18-VTBSPO-1606-3 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('appfiles', 'kz_backup', 'VARCHAR2(64)');
END;
/
update appfiles set kz_backup=ID_OWNER where OWNER_TYPE=1
and not exists (select 1 from crm_ek where id=ID_OWNER)
/
update appfiles set ID_OWNER=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ID_OWNER)
where OWNER_TYPE=1 and exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ID_OWNER and ID_UNITED_CLIENT is not null)
/
--changeset apavlenko:spo-19.18-VTBSPO-1605-kz_backup logicalFilePath:spo-19.18-VTBSPO-1605-kz_backup endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('deposit', 'kz_backup', 'CHAR(12)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('garant', 'kz_backup', 'CHAR(12)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('warranty', 'kz_backup', 'CHAR(12)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('R_ORG_MDTASK', 'kz_backup', 'CHAR(12)');
END;
/
update deposit set kz_backup=ID_CRMORG where not exists (select 1 from crm_ek where id=ID_CRMORG)
/
update garant set kz_backup=ORG where not exists (select 1 from crm_ek where id=ORG)
/
update warranty set kz_backup=ORG where not exists (select 1 from crm_ek where id=ORG)
/
update R_ORG_MDTASK set kz_backup=ID_CRMORG where not exists (select 1 from crm_ek where id=ID_CRMORG)
/
--changeset apavlenko:spo-19.18-VTBSPO-1605 logicalFilePath:spo-19.18-VTBSPO-1605 endDelimiter:/
update deposit set ID_CRMORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG)
where exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG and ID_UNITED_CLIENT is not null)
/
update garant set ORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ORG)
where exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ORG and ID_UNITED_CLIENT is not null)
/
update warranty set ORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ORG)
where exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ORG and ID_UNITED_CLIENT is not null)
/
--changeset apavlenko:spo-19.18-VTBSPO-1605-R_ORG_MDTASK_SUPPLY logicalFilePath:spo-19.18-VTBSPO-1605-R_ORG_MDTASK_SUPPLY endDelimiter:/
drop index R_ORG_MDTASK_SUPPLY_INX
/
create index R_ORG_MDTASK_SUPPLY_INX on R_ORG_MDTASK_SUPPLY (ID_MDTASK, ID_CRMORG, ID_PERSON)
/
update R_ORG_MDTASK_SUPPLY set ID_CRMORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG)
where exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG and ID_UNITED_CLIENT is not null)
/
--changeset apavlenko:spo-19.18-VTBSPO-1604 logicalFilePath:spo-19.18-VTBSPO-1604 endDelimiter:/
update R_ORG_MDTASK set ID_CRMORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG)
where exists (select 1 from CRM_FINANCE_ORG  where ID_ORG=ID_CRMORG and ID_UNITED_CLIENT is not null)
/
--changeset apavlenko:spo-19.18-VTBSPO-1607 logicalFilePath:spo-19.18-VTBSPO-1607 endDelimiter:/
update CD_PLEDGER p set p.ID_ORG=(select max(ID_UNITED_CLIENT) from CRM_FINANCE_ORG f where f.ID_ORG=p.ID_ORG)
where exists (select 1 from CRM_FINANCE_ORG f where p.ID_ORG=f.ID_ORG and ID_UNITED_CLIENT is not null)
and p.is_person=0
/
--changeset apavlenko:spo-19.18-VTBSPO-1633 logicalFilePath:spo-19.18-VTBSPO-1633 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_OBJECT('spo_client_info',
                             'create table spo_client_info(
                                    id CHAR(12) not null,
                                    pub VARCHAR2(64)
                                )');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-2 logicalFilePath:spo-19.18-VTBSPO-1633-2 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'status', 'VARCHAR2(64)');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-3 logicalFilePath:spo-19.18-VTBSPO-1633-3 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'security_last', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'security_validto', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'security_text', 'VARCHAR2(3000)');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-5 logicalFilePath:spo-19.18-VTBSPO-1633-5 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_OBJECT('spo_client_info_limit',
                             'create table spo_client_info_limit(
                                    id CHAR(12) not null,
                                    ID_MDTASK NUMBER(38) not null,
                                    protocol VARCHAR2(100),
                                    sublimit VARCHAR2(100),
                                    decisionmaker VARCHAR2(2000),
                                    decision_date TIMESTAMP(6),
                                    validto_date TIMESTAMP(6)
                                )');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-6 logicalFilePath:spo-19.18-VTBSPO-1633-6 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'GROUP_DECISION_DATE', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'GROUP_RATING', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'GROUP_PROTOCOL_NO', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'GROUP_PROTOCOL_DOC', 'BLOB');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'GROUP_RATING_REVIEW', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'RATING_METHOD', 'VARCHAR2(300)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'RATING', 'VARCHAR2(300)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'RATING_SCALE', 'VARCHAR2(300)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'DECISION_DATE', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'PROTOCOL_NO', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'PROTOCOL_DOC', 'BLOB');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'RATING_REVIEW', 'VARCHAR2(30)');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-7 logicalFilePath:spo-19.18-VTBSPO-1633-7 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_OBJECT('SPO_CLIENT_INFO_DECISION_BODY',
                             'create table SPO_CLIENT_INFO_DECISION_BODY(
                                    id CHAR(12) not null,
                                    decisionmaker VARCHAR2(2000),
                                    type VARCHAR2(20)
                                )');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-8 logicalFilePath:spo-19.18-VTBSPO-1633-8 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'SUSPEND', 'NUMBER(1) default 0');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'SUSPEND_LIMIT_LOAN', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'SUSPEND_LIMIT_INVEST', 'VARCHAR2(30)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'SUSPEND_DECISION', 'VARCHAR2(3000)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'SUSPEND_DECISION_DATE', 'DATE');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-9 logicalFilePath:spo-19.18-VTBSPO-1633-9 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'CORP_BLOCK', 'VARCHAR2(3000)');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-10 logicalFilePath:spo-19.18-VTBSPO-1633-10 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_OBJECT('SPO_CLIENT_INFO_DOC',
                             'create table SPO_CLIENT_INFO_DOC(
                                    id CHAR(12) not null,
                                    FILENAME VARCHAR2(2000),
                                    CONTENTTYPE VARCHAR2(2000),
                                    FIELDTYPE VARCHAR2(2000),
                                    FILEDATA BLOB
                                )');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-12 logicalFilePath:spo-19.18-VTBSPO-1633-12 endDelimiter:;
comment on column SPO_CLIENT_INFO_DOC.id
is 'id клиента';
comment on column SPO_CLIENT_INFO_DOC.filename
is 'имя файла';
comment on column SPO_CLIENT_INFO_DOC.contenttype
is 'например application/octet-stream';
comment on column SPO_CLIENT_INFO_DOC.fieldtype
is 'код поля';
comment on column SPO_CLIENT_INFO_DOC.filedata
is 'содержание';
alter table SPO_CLIENT_INFO_DOC
    add constraint SPO_CLIENT_INFO_DOC_PK primary key (ID, FIELDTYPE);
alter table SPO_CLIENT_INFO_DOC
    add constraint SPO_CLIENT_INFO_DOC_FK1 foreign key (ID)
references crm_ek (ID) on delete cascade;
comment on column SPO_CLIENT_INFO_LIMIT.id
is 'id клиента';
comment on column SPO_CLIENT_INFO_LIMIT.id_mdtask
is 'id заявки';
comment on column SPO_CLIENT_INFO_LIMIT.protocol
is 'Номер протокола';
comment on column SPO_CLIENT_INFO_LIMIT.sublimit
is 'Наличие лимита/сублимита на Инвестиционные операции';
comment on column SPO_CLIENT_INFO_LIMIT.decisionmaker
is 'Кем принято решение';
comment on column SPO_CLIENT_INFO_LIMIT.decision_date
is 'Дата решения о лимите';
comment on column SPO_CLIENT_INFO_LIMIT.validto_date
is 'Дата истечения срока сделок';
alter table SPO_CLIENT_INFO_LIMIT
    add constraint SPO_CLIENT_INFO_LIMIT_PK primary key (ID, ID_MDTASK);
alter table SPO_CLIENT_INFO_LIMIT
    add constraint SPO_CLIENT_INFO_LIMIT_FK1 foreign key (ID_MDTASK)
references mdtask (ID_MDTASK) on delete cascade;
alter table SPO_CLIENT_INFO_LIMIT
    add constraint SPO_CLIENT_INFO_LIMIT_FK2 foreign key (ID)
references crm_ek (ID) on delete cascade;
comment on column SPO_CLIENT_INFO_DECISION_BODY.id
is 'id клиента';
comment on column SPO_CLIENT_INFO_DECISION_BODY.decisionmaker
is 'Кем принято решение';
comment on column SPO_CLIENT_INFO_DECISION_BODY.type
is 'код поля';
alter table SPO_CLIENT_INFO_DECISION_BODY
    add constraint SPO_CLIENT_INFO_DEC_BODY_FK1 foreign key (ID)
references crm_ek (ID) on delete cascade;
comment on column SPO_CLIENT_INFO.id
is 'id клиента';
comment on column SPO_CLIENT_INFO.pub
is 'Публичная компания';
comment on column SPO_CLIENT_INFO.status
is 'Статус сделок';
comment on column SPO_CLIENT_INFO.security_last
is 'Дата последнего заключения безопасности';
comment on column SPO_CLIENT_INFO.security_validto
is 'Дата окончания действия заключения безопасности';
comment on column SPO_CLIENT_INFO.security_text
is 'Примечание безопасности';
comment on column SPO_CLIENT_INFO.group_rating
is 'Значение утверждённого рейтинга Группы';
comment on column SPO_CLIENT_INFO.group_rating_review
is 'Необходимость пересмотра рейтинга группы';
comment on column SPO_CLIENT_INFO.rating_method
is 'Методика ранжирования';
comment on column SPO_CLIENT_INFO.rating
is 'Значение утверждённого рейтинга клиента';
comment on column SPO_CLIENT_INFO.rating_scale
is 'Рейтинг по мастер-шкале';
comment on column SPO_CLIENT_INFO.rating_review
is 'Необходимость пересмотра рейтинга клиента';
comment on column SPO_CLIENT_INFO.suspend
is 'Приостановление';
comment on column SPO_CLIENT_INFO.suspend_limit_loan
is 'Приостановление лимита (кредитные и документарные операции)';
comment on column SPO_CLIENT_INFO.suspend_limit_invest
is 'Приостановление инвестиционного лимита';
comment on column SPO_CLIENT_INFO.suspend_decision
is 'Приостановление Результат решения';
comment on column SPO_CLIENT_INFO.suspend_decision_date
is 'Приостановление Дата принятия решения';
comment on column SPO_CLIENT_INFO.corp_block
is 'Клиентское подразделение';
alter table SPO_CLIENT_INFO
    add constraint SPO_CLIENT_INFO_PK primary key (ID);
alter table SPO_CLIENT_INFO
    add constraint SPO_CLIENT_INFO_FK1 foreign key (ID)
references crm_ek (ID) on delete cascade;
--changeset apavlenko:spo-19.18-VTBSPO-1633-13 logicalFilePath:spo-19.18-VTBSPO-1633-13 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DOC rename to SPO_CLIENT_INFO_DECISION');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO_DECISION', 'DECISION_DATE', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO_DECISION', 'PROTOCOL_NO', 'VARCHAR2(30)');
END;
/
delete from SPO_CLIENT_INFO_DECISION
/
comment on table SPO_CLIENT_INFO_DECISION is 'решение'
/
comment on column SPO_CLIENT_INFO_DECISION.PROTOCOL_NO is 'Номер протокола'
/
comment on column SPO_CLIENT_INFO_DECISION.DECISION_DATE is 'Дата принятия решения'
/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION drop column id');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION add id_decision number not null');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION drop constraint SPO_CLIENT_INFO_DOC_PK cascade');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION add constraint SPO_CLIENT_INFO_DECISION_PK primary key (id_decision)');
    PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('spo_dash_user_settings', 'settings_id', 'spo_dash_user_settings_seq');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-14 logicalFilePath:spo-19.18-VTBSPO-1633-14 endDelimiter:/
comment on table SPO_CLIENT_INFO is 'карточка клиента'
/
comment on table SPO_CLIENT_INFO_DECISION_BODY is 'карточка клиента. Кем принято решение'
/
comment on table spo_client_info_limit is 'карточка клиента. Информация о лимите'
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-15 logicalFilePath:spo-19.18-VTBSPO-1633-15 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION drop constraint SPO_CLIENT_INFO_DOC_FK1');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION drop column id');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column group_decision_date');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column group_protocol_no');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column group_protocol_doc');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'group_decision', 'number');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO add constraint SPO_CLIENT_INFO_FK2 foreign key (group_decision) references SPO_CLIENT_INFO_DECISION (id_decision) on delete set null');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-16 logicalFilePath:spo-19.18-VTBSPO-1633-16 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION drop column FIELDTYPE');
    PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('SPO_CLIENT_INFO_DECISION', 'id_decision', 'SPO_CLIENT_INFO_DECISION_SEQ');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-17 logicalFilePath:spo-19.18-VTBSPO-1633-17 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'client_decision', 'number');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-18 logicalFilePath:spo-19.18-VTBSPO-1633-18 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('delete from SPO_CLIENT_INFO_DECISION_BODY');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION_BODY drop constraint SPO_CLIENT_INFO_DEC_BODY_FK1');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION_BODY drop column id');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION_BODY drop column TYPE');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION_BODY add id_decision number');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO_DECISION_BODY add constraint SPO_CLIENT_INFO_DB_FK1 foreign key (ID_DECISION) references SPO_CLIENT_INFO_DECISION (ID_DECISION) on delete cascade');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-19 logicalFilePath:spo-19.18-VTBSPO-1633-19 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'client_decision_review', 'number');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'group_decision_review', 'number');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-20 logicalFilePath:spo-19.18-VTBSPO-1633-20 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'RATING_SCALE_GROUP', 'VARCHAR2(300)');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column decision_date');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column protocol_no');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column protocol_doc');
END;
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-21 logicalFilePath:spo-19.18-VTBSPO-1633-21 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'sublimit', 'VARCHAR2(100)');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('spo_client_info', 'validto_date', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'limit_decision', 'number');
    PKG_DDL_UTILS.EXECUTE_STRING('drop table spo_client_info_limit');
END;
/
comment on column SPO_CLIENT_INFO.sublimit
is 'Наличие лимита/сублимита на Инвестиционные операции'
/
comment on column SPO_CLIENT_INFO.validto_date
is 'Дата истечения срока сделок'
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-22 logicalFilePath:spo-19.18-VTBSPO-1633-22 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'SUSPEND_LIMIT_INVEST_DECISION', 'number');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'SUSPEND_LIMIT_LOAN_DECISION', 'number');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'SUSPEND_LIMIT_LOAN_DATE', 'DATE');
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('SPO_CLIENT_INFO', 'SUSPEND_LIMIT_INVEST_DATE', 'DATE');
END;
/
comment on column SPO_CLIENT_INFO.SUSPEND_LIMIT_LOAN_DATE
is 'Дата ввода. Информация о лимите'
/
comment on column SPO_CLIENT_INFO.SUSPEND_LIMIT_INVEST_DATE
is 'Дата ввода. Информация об инвестлимите'
/
--changeset apavlenko:spo-19.18-VTBSPO-1633-23 logicalFilePath:spo-19.18-VTBSPO-1633-23 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column SUSPEND');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column SUSPEND_DECISION_DATE');
    PKG_DDL_UTILS.EXECUTE_STRING('alter table SPO_CLIENT_INFO drop column SUSPEND_DECISION');
END;
/
