--liquibase formatted sql

--changeset apavlenko:spo-19.07-P12727 logicalFilePath:spo-19.07-P12727 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_INDEX('PROJECT_TEAM_FK1_I', 'CREATE INDEX PROJECT_TEAM_FK1_I ON PROJECT_TEAM(ID_MDTASK)');
    PKG_DDL_UTILS.ADD_INDEX('PROJECT_TEAM_FK2_I', 'CREATE INDEX PROJECT_TEAM_FK2_I ON PROJECT_TEAM(ID_USER)');
    PKG_DDL_UTILS.ADD_INDEX('FK_PSP_I', 'CREATE INDEX FK_PSP_I ON PROCESSES(ID_STATUS)');
    PKG_DDL_UTILS.ADD_INDEX('FK_PTP_I', 'CREATE INDEX FK_PTP_I ON PROCESSES(ID_TYPE_PROCESS)');
END;
/
--changeset apavlenko:spo-19.07-VTBSPO-1317-trade_finance logicalFilePath:spo-19.07-VTBSPO-1317-trade_finance endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('pipeline', 'trade_finance', 'number');
END;
/
--changeset apavlenko:spo-19.07-VTBSPO-1317-fk logicalFilePath:spo-19.07-VTBSPO-1317-fk endDelimiter:/
alter table CD_TRADE_FINANCE add constraint CD_TRADE_FINANCE_PK primary key (ID)
/
alter table PIPELINE add constraint PIPELINE_FK03 foreign key (TRADE_FINANCE)
references cd_trade_finance (ID) on delete set null
/
--changeset apavlenko:spo-19.07-VTBSPO-1315 logicalFilePath:spo-19.07-VTBSPO-1315 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_TABLE_COLUMN('pipeline', 'HIDEINREPORTTRADERS', 'CHAR(1)');
END;
/
alter table PIPELINE modify HIDEINREPORTTRADERS default 'n'
/
