--liquibase formatted sql

--changeset apavlenko:spo-17.20-gruppenfuhrer logicalFilePath:spo-17.20-gruppenfuhrer endDelimiter:/ 
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD trader_approve char(1)');
PKG_DDL_UTILS.EXECUTE_STRING('alter table MDTASK modify trader_approve default ''n''');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD trader_approve_user number');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD trader_approve_date TIMESTAMP(6)');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD trader_approve_skip char(1)');
END;
/
update mdtask t set t.trader_approve_skip='y' where t.id_pup_process in (select p.id_process from processes p where p.id_status=4)
/
