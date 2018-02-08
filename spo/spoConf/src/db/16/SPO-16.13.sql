BEGIN
  PKG_DDL_UTILS.EXECUTE_STRING('DROP TRIGGER TRG_SUPPLY_GARANT_DEL');
  PKG_DDL_UTILS.EXECUTE_STRING('DROP TRIGGER TRG_SUPPLY_DEPOSIT_DEL');
  PKG_DDL_UTILS.EXECUTE_STRING('DROP TRIGGER TRG_SUPPLY_WARRANTY_DEL');
END;
/
CREATE OR REPLACE FORCE VIEW SPO_ALL_CONTRACTOR AS 
select id_mdtask,id_crmorg,null as id_person from r_org_mdtask r
union all select id_mdtask,org as id_crmorg,id_person from garant
union all select id_mdtask,id_crmorg,id_person from DEPOSIT
union all select id_mdtask,org,id_person from WARRANTY
/
CREATE or replace PROCEDURE SPO_CONTRACTOR_SYNC(ID_MDTASK_PARAM IN MDTASK.ID_MDTASK%TYPE)
IS
BEGIN
  update r_org_mdtask_supply set delete_date=sysdate 
  where delete_date is null and id_mdtask=ID_MDTASK_PARAM and id_person is null and id_crmorg  not in
  (select id_crmorg from SPO_ALL_CONTRACTOR where id_mdtask=ID_MDTASK_PARAM and id_crmorg is not null);
  update r_org_mdtask_supply set delete_date=sysdate 
  where delete_date is null and id_mdtask=ID_MDTASK_PARAM and id_crmorg is null and id_person not in
  (select id_person from SPO_ALL_CONTRACTOR where id_mdtask=ID_MDTASK_PARAM and id_person is not null);
END SPO_CONTRACTOR_SYNC;
/
