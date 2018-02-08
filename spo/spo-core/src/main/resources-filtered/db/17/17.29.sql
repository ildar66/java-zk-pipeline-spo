--liquibase formatted sql

--changeset apavlenko:spo-17.29-VTBSPO-183 logicalFilePath:spo-17.29-VTBSPO-183 endDelimiter:/ runOnChange:true
create or replace view v_spo_org as
select o.id_org as CRMID,organization_name as ORGANIZATIONNAME,client_category as clientcategory,inn,kpp,industry as industryname,ogrn,
case when o.is_active = lower('y') then 1 else 0 end as is_active,
org_type as TYPE,region as DEPARTMENT,division,ID_UNITED_CLIENT
from crm_organization o
inner join crm_finance_org f on o.id_org=f.id_org
/
--changeset apavlenko:spo-17.29-VTBSPO-183-main_org_changeble logicalFilePath:spo-17.29-VTBSPO-183-main_org_changeble endDelimiter:/ runOnChange:true
alter table MDTASK add main_org_changeble CHAR(1) default 'n'
/
comment on column MDTASK.main_org_changeble is 'Контрагент неокончательный и может быть изменен'
/
--changeset apavlenko:spo-17.29-VTBSPO-183-order_disp logicalFilePath:spo-17.29-VTBSPO-183-order_disp endDelimiter:/ runOnChange:true
alter table R_ORG_MDTASK add order_disp NUMBER(38)
/
--changeset apavlenko:spo-17.29-VTBSPO-183-log logicalFilePath:spo-17.29-VTBSPO-183-log endDelimiter:/
create table main_borrower_change_log
(
  id_mdtask number not null,
  old_org   char(12),
  new_org   char(12),
  userid    number not null,
  log_date      timestamp(6) not null
)
/
alter table main_borrower_change_log
  add constraint main_borrower_change_log_fk1 foreign key (ID_MDTASK)
  references mdtask (ID_MDTASK) on delete cascade
/
alter table main_borrower_change_log
  add constraint main_borrower_change_log_fk2 foreign key (OLD_ORG)
  references crm_organization (ID_ORG) on delete cascade
/
alter table main_borrower_change_log
  add constraint main_borrower_change_log_fk3 foreign key (NEW_ORG)
  references crm_organization (ID_ORG) on delete cascade
/
alter table main_borrower_change_log
  add constraint main_borrower_change_log_fk4 foreign key (USERID)
  references users (ID_USER) on delete cascade
/
--changeset apavlenko:spo-17.29-VTBSPO-183-stages-update logicalFilePath:spo-17.29-VTBSPO-183-stages-update endDelimiter:/
update stages s set s.DESCRIPTION_STAGE='Определение проектной команды' where s.DESCRIPTION_STAGE='Дополнение заявки. Определение проектной команды' and s.ID_TYPE_PROCESS in
(select tp.ID_TYPE_PROCESS from TYPE_PROCESS tp where tp.DESCRIPTION_PROCESS in ('Крупный бизнес ГО','Крупный бизнес ГО (Структуратор за МО)'))
/
--changeset apavlenko:spo-17.42-crm_org_index logicalFilePath:spo-17.42-crm_org_index endDelimiter:/  runOnChange:true
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('DROP INDEX CRM_ORGANIZATION_INDEX1');
PKG_DDL_UTILS.EXECUTE_STRING('CREATE INDEX CRM_ORGANIZATION_INDEX2 ON CRM_ORGANIZATION (organization_name)');
PKG_DDL_UTILS.EXECUTE_STRING('DROP INDEX crm_finance_org_INDEX1');
PKG_DDL_UTILS.EXECUTE_STRING('CREATE INDEX crm_finance_org_INDEX2 ON crm_finance_org (ID_UNITED_CLIENT)');
PKG_DDL_UTILS.EXECUTE_STRING('CREATE INDEX crm_finance_org_i1 ON crm_finance_org (ID_UNITED_CLIENT, IS_ACTIVE)');
END;
/
--changeset apavlenko:spo-17.46-main_org logicalFilePath:spo-17.46-main_org endDelimiter:/  runOnChange:true
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD main_org char(12)');
END;
/
--changeset apavlenko:spo-17.46-SPO_CONTRACTOR_SYNC logicalFilePath:spo-17.46-SPO_CONTRACTOR_SYNC endDelimiter:/  runOnChange:true
CREATE OR REPLACE PROCEDURE SP_INSERT_CONTRACTOR(PAR_ID_R NUMBER, PAR_ID_MDTASK NUMBER, PAR_ID_CRMORG VARCHAR2, PAR_ID_PERSON NUMBER, PAR_ID_CONTRACTOR_TYPE NUMBER)
IS
  VAR_ID_CREDIT_DEAL_CONTRACTOR NUMBER;
BEGIN
  VAR_ID_CREDIT_DEAL_CONTRACTOR := PAR_ID_R;
  
  IF PAR_ID_MDTASK IS NOT NULL AND (PAR_ID_CRMORG IS NOT NULL OR PAR_ID_PERSON IS NOT NULL) THEN
    IF VAR_ID_CREDIT_DEAL_CONTRACTOR IS NULL THEN
    SELECT MIN(ID_R)
    INTO VAR_ID_CREDIT_DEAL_CONTRACTOR
    FROM R_ORG_MDTASK S
    WHERE ID_MDTASK = PAR_ID_MDTASK AND ID_CRMORG = PAR_ID_CRMORG;
    END IF;

    IF VAR_ID_CREDIT_DEAL_CONTRACTOR IS NULL THEN
      SELECT R_ORG_MDTASK_SEQ.NEXTVAL
      INTO VAR_ID_CREDIT_DEAL_CONTRACTOR
      FROM DUAL;
    END IF;

    INSERT INTO R_ORG_MDTASK_SUPPLY(ID_R, ID_MDTASK, ID_CRMORG, ID_PERSON)
    SELECT VAR_ID_CREDIT_DEAL_CONTRACTOR ID_R, PAR_ID_MDTASK ID_MDTASK, PAR_ID_CRMORG ID_CRMORG, PAR_ID_PERSON ID_PERSON
    FROM DUAL
    WHERE NOT EXISTS(SELECT 1 FROM R_ORG_MDTASK_SUPPLY WHERE ID_MDTASK = PAR_ID_MDTASK AND (ID_CRMORG = PAR_ID_CRMORG OR ID_PERSON = PAR_ID_PERSON));
    
    SELECT MIN(ID_R)
    INTO VAR_ID_CREDIT_DEAL_CONTRACTOR
    FROM R_ORG_MDTASK_SUPPLY 
    WHERE ID_MDTASK = PAR_ID_MDTASK AND (ID_CRMORG = PAR_ID_CRMORG OR ID_PERSON = PAR_ID_PERSON);
  END IF;

  IF PAR_ID_CONTRACTOR_TYPE IS NOT NULL AND VAR_ID_CREDIT_DEAL_CONTRACTOR IS NOT NULL THEN
    INSERT INTO CONTRACTOR_TYPE_SUPPLY (ID_R, ID_CONTRACTOR_TYPE)
    SELECT VAR_ID_CREDIT_DEAL_CONTRACTOR, PAR_ID_CONTRACTOR_TYPE
    FROM DUAL
    WHERE NOT EXISTS(SELECT 1 FROM CONTRACTOR_TYPE_SUPPLY S WHERE S.ID_R = VAR_ID_CREDIT_DEAL_CONTRACTOR AND S.ID_CONTRACTOR_TYPE = PAR_ID_CONTRACTOR_TYPE);
  END IF;
END;
/
CREATE OR REPLACE PROCEDURE SPO_CONTRACTOR_SYNC(PAR_ID_MDTASK IN MDTASK.ID_MDTASK%TYPE)
IS
CURRENT_DATE DATE := SYSDATE;

VAR_ID_CONTRACTOR_TYPE NUMBER;
BEGIN
  FOR R_ORG_MDTASK_ROW IN (SELECT * FROM R_ORG_MDTASK WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
    SP_INSERT_CONTRACTOR(R_ORG_MDTASK_ROW.ID_R,  PAR_ID_MDTASK, R_ORG_MDTASK_ROW.ID_CRMORG, NULL, NULL);

    FOR REC IN (SELECT * FROM R_CONTRACTOR_TYPE_MDTASK WHERE ID_R = R_ORG_MDTASK_ROW.ID_R) LOOP
      SP_INSERT_CONTRACTOR(REC.ID_R, NULL, NULL, NULL, REC.ID_CONTRACTOR_TYPE);
    END LOOP;
  END LOOP;

  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'WARRANTY';
  FOR REC IN (SELECT * FROM WARRANTY WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
    SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
  END LOOP;

  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'GARANT';
  FOR REC IN (SELECT * FROM GARANT WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
    SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
  END LOOP;

  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'DEPOSIT';
  FOR REC IN (SELECT * FROM DEPOSIT WHERE ID_MDTASK = PAR_ID_MDTASK) LOOP
    SP_INSERT_CONTRACTOR(NULL, PAR_ID_MDTASK, REC.ID_CRMORG, REC.ID_PERSON, VAR_ID_CONTRACTOR_TYPE);
  END LOOP;

  UPDATE R_ORG_MDTASK_SUPPLY S
  SET DELETE_DATE = SYSDATE
  WHERE DELETE_DATE IS NULL AND ID_MDTASK = PAR_ID_MDTASK AND
  NOT EXISTS (
      SELECT 1
      FROM SPO_ALL_CONTRACTOR
      WHERE
      ID_MDTASK = PAR_ID_MDTASK
      AND
      (
           ID_CRMORG = S.ID_CRMORG
           OR
           ID_PERSON = S.ID_PERSON
      )
  );
  update mdtask t set t.MAIN_ORG=(select id_crmorg from r_org_mdtask r where r.id_mdtask=t.id_mdtask and r.ORDER_DISP=0) where t.ID_MDTASK=PAR_ID_MDTASK;
END;
/
--changeset apavlenko:spo-17.46-orderdisp-mainorg logicalFilePath:spo-17.46-orderdisp-mainorg endDelimiter:/
update r_org_mdtask r set r.ORDER_DISP=0 where r.ORDER_DISP is null and r.ID_R in
(select min(id_r) from r_org_mdtask r2 where r2.ID_MDTASK=r.ID_MDTASK)
/
update mdtask t set t.MAIN_ORG=(select id_crmorg from r_org_mdtask r where r.id_mdtask=t.id_mdtask and r.ORDER_DISP=0)
/
COMMIT
/