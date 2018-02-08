--liquibase formatted sql

--changeset apavlenko:spo-17.22-VTBSPO-175 logicalFilePath:spo-17.22-VTBSPO-175 endDelimiter:/ 
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('alter table MDTASK add ced_approve_login VARCHAR2(128)');
PKG_DDL_UTILS.EXECUTE_STRING('alter table MDTASK add ced_approve_date TIMESTAMP(6)');
END;
/
comment on column MDTASK.ced_approve_login is 'кто утвердил заявку для КОД'
/
comment on column MDTASK.ced_approve_date  is 'когда утвердили заявку для КОД'
/

--changeset apavlenko:spo-17.35-commission_value logicalFilePath:spo-17.35-commission_value endDelimiter:/
alter table COMMISSION modify commission_value NUMBER(38,5)
/
--changeset apavlenko:spo-17.22-fine-description logicalFilePath:spo-17.22-fine-description endDelimiter:/ 
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('alter table FINE modify description VARCHAR2(4000)');
PKG_DDL_UTILS.EXECUTE_STRING('alter table FINE modify FINE_VALUE_TEXT VARCHAR2(4000)');
END;
/
