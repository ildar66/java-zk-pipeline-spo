--liquibase formatted sql

--changeset apavlenko:spo-17.59-VTBSPO-338 logicalFilePath:spo-17.59-VTBSPO-338 endDelimiter:/ runAlways:true
update mdtask set STANDARD_PERIOD_VERSION=(select max(id_spv) from STANDARD_PERIOD_VERSION v
inner join processes p on v.ID_TYPE_PROCESS=p.ID_TYPE_PROCESS
where p.ID_PROCESS=ID_PUP_PROCESS and v.DATE_VERSION<(select DATE_EVENT from PROCESS_EVENTS where ID_PROCESS=ID_PUP_PROCESS and ID_PROCESS_TYPE_EVENT=1))
where STANDARD_PERIOD_VERSION is null and ID_PUP_PROCESS is not null
/
--changeset apavlenko:spo-17.59-VTBSPO-340 logicalFilePath:spo-17.59-VTBSPO-340 endDelimiter:/ runAlways:true
update attributes a set a.VALUE_VAR=a.VALUE_VAR||' в связи с изменениями'
where a.ID_PROCESS in
(select p.ID_PROCESS from PROCESSES p where p.ID_TYPE_PROCESS in
(select tp.ID_TYPE_PROCESS from TYPE_PROCESS tp where tp.DESCRIPTION_PROCESS in ('Изменение условий Крупный бизнес ГО','Изменение условий Крупный бизнес ГО (Структуратор за МО)')))
and a.ID_VAR in (select id_var from variables v where v.name_var like 'Требуется %_stage')
and a.VALUE_VAR is not null
and a.VALUE_VAR not like '% в связи с изменениями'
/
--changeset apavlenko:spo-18.00-repair-main-org logicalFilePath:spo-18.00-repair-main-org endDelimiter:/ runAlways:true
update mdtask t set t.MAIN_ORG=(select f.ID_UNITED_CLIENT from crm_finance_org f inner join r_org_mdtask r on r.ID_CRMORG=f.ID_ORG where r.id_mdtask=t.id_mdtask and r.ORDER_DISP=0)
where t.MAIN_ORG is null or t.MAIN_ORG not like 'L%'
/
--changeset apavlenko:spo-SPO_CONTRACTOR_SYNC logicalFilePath:spo-SPO_CONTRACTOR_SYNC endDelimiter:/ runOnChange:true
CREATE OR REPLACE PROCEDURE SPO_CONTRACTOR_SYNC(PAR_ID_MDTASK IN MDTASK.ID_MDTASK%TYPE)
IS
    --ver 1.01
    --author akirilchev@masterdm.ru
    CURRENT_DATE DATE := SYSDATE;

    VAR_ID_CONTRACTOR_TYPE NUMBER;
    BEGIN
        FOR R_ORG_MDTASK_ROW IN (SELECT *
                                 FROM R_ORG_MDTASK
                                 WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
            SP_INSERT_CONTRACTOR(R_ORG_MDTASK_ROW.ID_R, PAR_ID_MDTASK, R_ORG_MDTASK_ROW.ID_CRMORG, NULL, NULL);

            FOR REC IN (SELECT *
                        FROM R_CONTRACTOR_TYPE_MDTASK
                        WHERE ID_R = R_ORG_MDTASK_ROW.ID_R) LOOP
                SP_INSERT_CONTRACTOR(REC.ID_R, NULL, NULL, NULL, REC.ID_CONTRACTOR_TYPE);
            END LOOP;
        END LOOP;

        SELECT ID_CONTRACTOR_TYPE
        INTO VAR_ID_CONTRACTOR_TYPE
        FROM CONTRACTOR_TYPE
        WHERE KEY = 'WARRANTY';

        FOR REC IN (SELECT *
                    FROM WARRANTY
                    WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
            SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
        END LOOP;

        SELECT ID_CONTRACTOR_TYPE
        INTO VAR_ID_CONTRACTOR_TYPE
        FROM CONTRACTOR_TYPE
        WHERE KEY = 'GARANT';

        FOR REC IN (SELECT *
                    FROM GARANT
                    WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
            SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
        END LOOP;

        SELECT ID_CONTRACTOR_TYPE
        INTO VAR_ID_CONTRACTOR_TYPE
        FROM CONTRACTOR_TYPE
        WHERE KEY = 'DEPOSIT';

        FOR REC IN (SELECT *
                    FROM DEPOSIT
                    WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
            SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ID_CRMORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
        END LOOP;

        UPDATE R_ORG_MDTASK_SUPPLY S
        SET DELETE_DATE = SYSDATE
        WHERE DELETE_DATE IS NULL
              AND ID_MDTASK = PAR_ID_MDTASK
              AND NOT EXISTS (
                SELECT 1
                FROM SPO_ALL_CONTRACTOR
                WHERE ID_MDTASK = PAR_ID_MDTASK
                      AND
                      (
                          ID_CRMORG = S.ID_CRMORG
                          OR
                          ID_PERSON = S.ID_PERSON
                      )
        );

        UPDATE MDTASK T
        SET T.MAIN_ORG = (SELECT F.ID_UNITED_CLIENT
                          FROM CRM_FINANCE_ORG F INNER JOIN R_ORG_MDTASK R ON R.ID_CRMORG=F.ID_ORG
                          WHERE R.ID_MDTASK=T.ID_MDTASK
                                AND R.ORDER_DISP=0)
        WHERE T.ID_MDTASK=PAR_ID_MDTASK;
    END;
/
