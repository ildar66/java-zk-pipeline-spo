CREATE OR REPLACE PROCEDURE SPO_DEPOSIT_SYNC
is
BEGIN
  UPDATE CD_PLEDGER set IS_ACTIVE=0 where IS_PERSON=0 and ID_ORG not in
  (select d.id_crmorg from deposit d where id_crmorg is not null);
  UPDATE CD_PLEDGER set IS_ACTIVE=1 where IS_PERSON=0 and ID_ORG in
  (select d.id_crmorg from deposit d where id_crmorg is not null);
  INSERT INTO CD_PLEDGER(ID_PLEDGER,IS_PERSON,PLEDGER_CODE,ID_PERSON,ID_ORG,IS_ACTIVE)
  SELECT CD_PLEDGER_SEQ.nextval,0,id_crmorg,null,id_crmorg,1 FROM (select distinct id_crmorg from deposit where id_crmorg is not null and id_crmorg not in (select ID_ORG from CD_PLEDGER where ID_ORG is not null));
  
  UPDATE CD_PLEDGER set IS_ACTIVE=0 where IS_PERSON=1 and ID_PERSON not in
  (select d.ID_PERSON from deposit d where ID_PERSON is not null);
  UPDATE CD_PLEDGER set IS_ACTIVE=1 where IS_PERSON=1 and ID_PERSON in
  (select d.ID_PERSON from deposit d where ID_PERSON is not null);
  INSERT INTO CD_PLEDGER(ID_PLEDGER,IS_PERSON,PLEDGER_CODE,ID_PERSON,ID_ORG,IS_ACTIVE)
  SELECT CD_PLEDGER_SEQ.nextval,1,ID_PERSON,ID_PERSON,null,1 FROM (select distinct ID_PERSON from deposit where ID_PERSON is not null and ID_PERSON not in (select ID_PERSON from CD_PLEDGER where ID_PERSON is not null));
END;
/
