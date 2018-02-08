ALTER TABLE R_PRODUCT_GROUP_MDTASK  
MODIFY (NAME VARCHAR2(2000 BYTE) )
/
BEGIN
  PKG_DDL_UTILS.ADD_TABLE_COLUMN('R_ORG_MDTASK_SUPPLY', 'DELETE_DATE', 'TIMESTAMP(6)');
END;
/
BEGIN
  PKG_DDL_UTILS.ADD_TABLE_COLUMN('R_ORG_MDTASK_SUPPLY', 'CREATE_DATE', 'TIMESTAMP(6)  DEFAULT sysdate');
END;
/
update R_ORG_MDTASK_SUPPLY set CREATE_DATE=TO_DATE('19770101', 'yyyymmdd')
/
CREATE OR REPLACE TRIGGER TRG_SUPPLY_GARANT_DEL
BEFORE DELETE ON GARANT FOR EACH ROW
DECLARE
  VAR_ID_CONTRACTOR_TYPE NUMBER;
BEGIN
  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'GARANT';
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg=:OLD.org and id_person is null
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg is null and id_person=:OLD.id_person
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
END;
/
CREATE OR REPLACE TRIGGER TRG_SUPPLY_DEPOSIT_DEL
BEFORE DELETE ON DEPOSIT FOR EACH ROW
DECLARE
  VAR_ID_CONTRACTOR_TYPE NUMBER;
BEGIN
  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'DEPOSIT';
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg=:OLD.id_crmorg and id_person is null
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg is null and id_person=:OLD.id_person
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
END;
/
CREATE OR REPLACE TRIGGER TRG_SUPPLY_WARRANTY_DEL
BEFORE DELETE ON WARRANTY FOR EACH ROW
DECLARE
  VAR_ID_CONTRACTOR_TYPE NUMBER;
BEGIN
  SELECT ID_CONTRACTOR_TYPE INTO VAR_ID_CONTRACTOR_TYPE FROM CONTRACTOR_TYPE WHERE KEY = 'WARRANTY';
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg=:OLD.org and id_person is null
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
  update r_org_mdtask_supply set delete_date=sysdate where id_mdtask=:OLD.id_mdtask and id_crmorg is null and id_person=:OLD.id_person
  and id_r in (select id_r from contractor_type_supply where ID_CONTRACTOR_TYPE=VAR_ID_CONTRACTOR_TYPE);
END;
/
CREATE OR REPLACE FORCE VIEW V_CPS_DEAL_CONTRACTOR AS 
SELECT
--все контрагенты - и кредитные, и обеспечительные; только юридические лица
Z.ID_R,
Z.ID_CRMORG,
Z.ID_PERSON,
Z.ID_MDTASK,
Z.DELETE_DATE,
NVL (
(SELECT ORGANIZATION_NAME FROM CRM_ORGANIZATION WHERE ID_ORG = Z.ID_CRMORG AND Z.ID_CRMORG IS NOT NULL),
(SELECT LTRIM(P.LAST_NAME || ' ') || LTRIM(P.NAME || ' ') || P.MIDDLE_NAME FROM CD_PERSON P WHERE ID_PERSON = Z.ID_PERSON)
) NAME
FROM R_ORG_MDTASK_SUPPLY Z
ORDER BY ID_R;
