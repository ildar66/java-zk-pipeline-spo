ALTER TABLE CPS_DEAL_CONDITION ADD (SUPPLY_CODE CHAR(1 CHAR) );
COMMENT ON COLUMN CPS_DEAL_CONDITION.SUPPLY_CODE IS 'Флаг, что условия относится к обеспечению. Если пустое, то не относится. g-гарантия, d- залоги, w - поручительство';
insert into CPS_SECTION_ROLE_SPO_MAPPING 
select CPS_SECTION_ROLE_SPO_MAP_SEQ.nextval,m.section_role_id,'Клиентский менеджер поддерживающего подразделения' from CPS_SECTION_ROLE_SPO_MAPPING m where m.spo_role_name like 'Клиентский менеджер'
and not exists (select 1 from CPS_SECTION_ROLE_SPO_MAPPING where spo_role_name like 'Клиентский менеджер поддерживающего подразделения');
commit;
ALTER TABLE MDTASK ADD (BENEFICIARY_OGRN VARCHAR2(200) );
COMMENT ON COLUMN MDTASK.BENEFICIARY_OGRN IS 'Поле «ОГРН» - текстовое поле, для Сделок по гарантиям, заполняется вручную.';
