--liquibase formatted sql

--changeset apavlenko:spo-17.14-ek logicalFilePath:spo-17.14-ek endDelimiter:/ 
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD SUBPLACE number(38)');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE R_ORG_MDTASK ADD ID_CLIENT_ROW CHAR(12)');
END;
/
