insert into CPS_SECTION_ROLE_SPO_MAPPING 
select CPS_SECTION_ROLE_SPO_MAP_SEQ.nextval,m.section_role_id,'Клиентский менеджер поддерживающего подразделения' from CPS_SECTION_ROLE_SPO_MAPPING m where m.spo_role_name like 'Клиентский менеджер'
and not exists (select 1 from CPS_SECTION_ROLE_SPO_MAPPING where spo_role_name like 'Клиентский менеджер поддерживающего подразделения');
commit;
ALTER TABLE MDTASK ADD (RATE2 NUMBER(20, 5) )
/
COMMENT ON COLUMN MDTASK.RATE2 IS 'Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера'
/
