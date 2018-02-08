--liquibase formatted sql

--changeset apavlenko:spo-17.41-VTBSPO-277 logicalFilePath:spo-17.41-VTBSPO-277 endDelimiter:/ 
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD pmn_order VARCHAR2(4000)');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE payment_schedule ADD pmn_desc VARCHAR2(4000)');
END;
/
/
