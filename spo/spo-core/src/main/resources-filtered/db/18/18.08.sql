--liquibase formatted sql

--changeset svaliev:spo-18.08-VTBSPO-1016 logicalFilePath:spo-18.08-VTBSPO-1016 endDelimiter:/
BEGIN
    PKG_DDL_UTILS.ADD_INDEX('MDTASK_NUM_ID_INX', 'CREATE INDEX MDTASK_NUM_ID_INX ON MDTASK(MDTASK_NUMBER, ID_MDTASK) COMPUTE STATISTICS');  
END;
/
--changeset akirilchev:spo-18.08-VTBSPO-1019-1034-indexes logicalFilePath:spo-18.08-VTBSPO-1019-1034-indexes endDelimiter:/
BEGIN
PKG_DDL_UTILS.ADD_INDEX('MDTASK_AUDIT_NUMBER_ID_INX', 'CREATE INDEX MDTASK_AUDIT_NUMBER_ID_INX ON MDTASK_AUDIT(MDTASK_NUMBER, ID_AUDIT) COMPUTE STATISTICS');
END;
/
BEGIN
PKG_DDL_UTILS.ADD_INDEX('INDRATE_MDTASK_ID_FP_INX', 'CREATE INDEX INDRATE_MDTASK_ID_FP_INX ON INDRATE_MDTASK(ID_MDTASK, ID_FACTPERCENT) COMPUTE STATISTICS');
END;
/
--changeset akirilchev:spo-18.08-VTBSPO-1019-1034-indexes-3 logicalFilePath:spo-18.08-VTBSPO-1019-1034-indexes-3 endDelimiter:GO
DECLARE
  PROCEDURE ADD_FOREIGN_KEY_INDEXES(PAR_PREFIX VARCHAR2, PAR_TABLE_NAME VARCHAR2, PAR_IS_EXECUTE NUMBER DEFAULT 0)
  AS
    CURSOR CUR IS
      SELECT 'CREATE INDEX '||SUBSTR(CONSTRAINT_NAME,1,28)||'_I ON '||TABLE_NAME||'('||
             CNAME1 || NVL2(CNAME2,','||CNAME2,NULL) ||
             NVL2(CNAME3,','||CNAME3,NULL) || NVL2(CNAME4,','||CNAME4,NULL) ||
             NVL2(CNAME5,','||CNAME5,NULL) || NVL2(CNAME6,','||CNAME6,NULL) ||
             NVL2(CNAME7,','||CNAME7,NULL) || NVL2(CNAME8,','||CNAME8,NULL)||')' AS QUERY_STRING
      FROM ( SELECT  B.TABLE_NAME,
               B.CONSTRAINT_NAME,
               MAX(DECODE( POSITION, 1, COLUMN_NAME, NULL )) CNAME1,
               MAX(DECODE( POSITION, 2, COLUMN_NAME, NULL )) CNAME2,
               MAX(DECODE( POSITION, 3, COLUMN_NAME, NULL )) CNAME3,
               MAX(DECODE( POSITION, 4, COLUMN_NAME, NULL )) CNAME4,
               MAX(DECODE( POSITION, 5, COLUMN_NAME, NULL )) CNAME5,
               MAX(DECODE( POSITION, 6, COLUMN_NAME, NULL )) CNAME6,
               MAX(DECODE( POSITION, 7, COLUMN_NAME, NULL )) CNAME7,
               MAX(DECODE( POSITION, 8, COLUMN_NAME, NULL )) CNAME8,
               COUNT(*) COL_CNT
             FROM (SELECT SUBSTR(TABLE_NAME,1,30) TABLE_NAME,
                          SUBSTR(CONSTRAINT_NAME,1,30) CONSTRAINT_NAME,
                          SUBSTR(COLUMN_NAME,1,30) COLUMN_NAME,
                     POSITION
                   FROM USER_CONS_COLUMNS ) A,
               USER_CONSTRAINTS B
             WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME
                   AND B.CONSTRAINT_TYPE = 'R'
             GROUP BY B.TABLE_NAME, B.CONSTRAINT_NAME
           ) CONS
      WHERE COL_CNT > ALL
            ( SELECT COUNT(*)
              FROM USER_IND_COLUMNS I
              WHERE I.TABLE_NAME = CONS.TABLE_NAME
                    AND I.COLUMN_NAME IN (CNAME1, CNAME2, CNAME3, CNAME4,
                                          CNAME5, CNAME6, CNAME7, CNAME8 )
                    AND I.COLUMN_POSITION <= CONS.COL_CNT
              GROUP BY I.INDEX_NAME
            )
            AND TABLE_NAME LIKE REPLACE(PAR_PREFIX, '_', '/_') || '%' ESCAPE '/'
                                                                             AND TABLE_NAME = NVL(UPPER(PAR_TABLE_NAME), TABLE_NAME)
      ORDER BY 1;
    BEGIN
      DBMS_OUTPUT.enable(NULL);

      FOR REC IN CUR LOOP
        IF NVL(PAR_IS_EXECUTE, 0) = 1 THEN
          BEGIN
            EXECUTE IMMEDIATE REC.QUERY_STRING;
          EXCEPTION
            WHEN OTHERS THEN DBMS_OUTPUT.put_line('Error: ' || REC.QUERY_STRING);
          END;
        ELSE
          DBMS_OUTPUT.PUT_LINE(REC.QUERY_STRING || ';');
        END IF;
      END LOOP;
    END ADD_FOREIGN_KEY_INDEXES;
BEGIN
  ADD_FOREIGN_KEY_INDEXES('', 'MDTASK_AUDIT', 1);
  ADD_FOREIGN_KEY_INDEXES('', 'FACTPERCENT_AUDIT', 1);
  ADD_FOREIGN_KEY_INDEXES('', 'INDRATE_MDTASK_AUDIT', 1);
  ADD_FOREIGN_KEY_INDEXES('', 'FACTPERCENT', 1);
  ADD_FOREIGN_KEY_INDEXES('', 'INDRATE_MDTASK', 1);
END;
GO
--changeset apavlenko:spo-18.08-VTBSPO-969-margin logicalFilePath:spo-18.08-VTBSPO-969-margin endDelimiter:/
alter table PIPELINE add margin VARCHAR2(2000)
/
comment on column PIPELINE.margin is 'Маржа'
/
--changeset apavlenko:spo-18.08-VTBSPO-969-status logicalFilePath:spo-18.08-VTBSPO-969-status endDelimiter:/
alter table PIPELINE modify status VARCHAR2(2000)
/
--changeset apavlenko:spo-18.08-TRADING_DESK logicalFilePath:spo-18.08-TRADING_DESK endDelimiter:;
insert into CD_PIPELINE_TRADING_DESK(ID, NAME) values(CD_PIPELINE_TRADING_DESK_SEQ.NEXTVAL,'CLFI/Cmds');
--changeset apavlenko:spo-18.08-VTBSPO-1048 logicalFilePath:spo-18.08-VTBSPO-1048 endDelimiter:/
update attributes set value_var='Передано в комитет' where id_attr in
(select a.id_attr from mdtask t
inner join processes p on p.id_process=t.id_pup_process
inner join type_process tp on tp.id_type_process=p.id_type_process
inner join ATTRIBUTES a on a.ID_PROCESS=t.ID_PUP_PROCESS
inner join VARIABLES v on v.ID_VAR=a.ID_VAR
where p.id_status=1 and a.value_var like 'Передано на %' and v.name_var='Статус' and a.value_var not like 'Передано на экспертиз%')
/

--changeset apavlenko:spo-18.08-VTBSPO-1061 logicalFilePath:spo-18.08-VTBSPO-1061 endDelimiter:/
alter table MDTASK modify statusreturntext VARCHAR2(3500)
/
--changeset akirilchev:spo-18.08-VTBSPO-1063-drop logicalFilePath:spo-18.08-VTBSPO-1063-drop endDelimiter:/
--preconditions onFail:CONTINUE onError:HALT
--precondition-sql-check expectedResult:1 SELECT CASE WHEN EXISTS(SELECT 1 FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME = 'MDTASK_FK25' AND R_CONSTRAINT_NAME = 'CD_QU_DURATION_PK') THEN 1 ELSE 0 END FROM DUAL
ALTER TABLE MDTASK DROP CONSTRAINT MDTASK_FK25
/

--changeset akirilchev:spo-18.08-VTBSPO-1063-add logicalFilePath:spo-18.08-VTBSPO-1063-add endDelimiter:/
--preconditions onFail:CONTINUE onError:HALT
--precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS(SELECT 1 FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME = 'CD_CROSS_SELL_PK') THEN 1 ELSE 0 END FROM DUAL
ALTER TABLE CD_CROSS_SELL ADD CONSTRAINT CD_CROSS_SELL_PK PRIMARY KEY (ID)
/

--changeset akirilchev:spo-18.08-VTBSPO-1063-add-2 logicalFilePath:spo-18.08-VTBSPO-1063-add-2 endDelimiter:/
--preconditions onFail:CONTINUE onError:HALT
--precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS(SELECT 1 FROM USER_TAB_COLS WHERE TABLE_NAME = 'MDTASK' AND COLUMN_NAME = 'CROSS_SELL_TYPE') THEN 1 ELSE 0 END FROM DUAL
ALTER TABLE MDTASK ADD CROSS_SELL_TYPE NUMBER
/

--changeset akirilchev:spo-18.08-VTBSPO-1063-add-fk logicalFilePath:spo-18.08-VTBSPO-1063-add-fk endDelimiter:/
--preconditions onFail:CONTINUE onError:HALT
--precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS(SELECT 1 FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME = 'MDTASK_FK25') THEN 1 ELSE 0 END FROM DUAL
ALTER TABLE MDTASK ADD CONSTRAINT MDTASK_FK25 FOREIGN KEY (CROSS_SELL_TYPE) REFERENCES CD_CROSS_SELL (ID) ON DELETE SET NULL
/

--changeset apavlenko:spo-18.08-edc_on logicalFilePath:spo-18.08-edc_on endDelimiter:/
update global_settings set value='1' where mnemo like 'edc_on'
/
