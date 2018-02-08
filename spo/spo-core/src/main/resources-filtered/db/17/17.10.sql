--liquibase formatted sql

--changeset apavlenko:spo-17.10-VTBSPO-127-definition logicalFilePath:spo-17.10-VTBSPO-127-definition endDelimiter:/ 
alter table MDTASK add ( definition_temp clob )
/
update MDTASK set definition_temp=definition
/
alter table MDTASK drop column definition
/
alter table MDTASK rename column definition_temp to definition
/
