delete from ATTRIBUTES_REQUIRED t
where t.id_attributes_required in (30, 31);
update ATTRIBUTES_REQUIRED t
set t.template_code = 'otherGoals', t.name = 'main Иные цели', t.description = 'Целевое назначение (таблица)'
where t.id_attributes_required = 25;
update ATTRIBUTES_REQUIRED t
set t.template_code = 'og_name', t.description = 'Цель (таблица Целевое назначение)'
where t.id_attributes_required = 26;
commit;
update ATTRIBUTES_REQUIRED t
set t.template_code = 'procent_pay_int'
where t.id_attributes_required = 103;
update ATTRIBUTES_REQUIRED t
set t.template_code = 'interestPay_pay_int'
where t.id_attributes_required = 170;
commit;
update ATTRIBUTES_REQUIRED t
set t.description = 'Решения (таблица Порядок принятия решения)', t.template_code = 'odl_decisionListAsString', t.template_code_rus = 'ППР_Решения'
where t.id_attributes_required = 36;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (37, null, 'принимаются (таблица Порядок принятия решения)', 'Основные параметры лимита/Сублимита', 0, 'odl_accepted', 'ППР_Принимаются');
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (38, null, 'особенности принятия решения(таблица Порядок принятия решения)', 'Основные параметры лимита/Сублимита', 0, 'odl_specials', 'ППР_Особенности');
commit;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (23, null, 'название лимита/сублимита', 'Основные параметры лимита/Сублимита', 0, 'header_title', 'Наименование_лимита(сублимита)');
commit;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (1775, null, 'индивидуальные условия (таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_indcondition', 'ФА2_Инд_условия');
commit;
ALTER TABLE MDTASK ADD (PRODUCT_NAME VARCHAR2(300) );
COMMENT ON COLUMN MDTASK.PRODUCT_NAME IS 'отображаемый вид сделки';
CREATE TABLE R_PRODUCT_GROUP_MDTASK (ID NUMBER NOT NULL , ID_MDTASK NUMBER NOT NULL , NAME VARCHAR2(400));
COMMENT ON TABLE R_PRODUCT_GROUP_MDTASK IS 'список групп видов сделки для лимита';
COMMENT ON COLUMN R_PRODUCT_GROUP_MDTASK.ID_MDTASK IS 'ссылка на лимит';
COMMENT ON COLUMN R_PRODUCT_GROUP_MDTASK.NAME IS 'группа вида продукта с возможностью редактирования';
ALTER TABLE R_PRODUCT_GROUP_MDTASK ADD CONSTRAINT R_PRODUCT_GROUP_MDTASK_PK PRIMARY KEY (ID) ENABLE;
ALTER TABLE R_PRODUCT_GROUP_MDTASK ADD CONSTRAINT R_PRODUCT_GROUP_MDTASK_MD_FK1 FOREIGN KEY (ID_MDTASK) REFERENCES MDTASK (ID_MDTASK )ENABLE;
CREATE SEQUENCE R_PRODUCT_GROUP_MDTASK_SEQ;
ALTER TABLE MDTASK ADD (IRREGULAR CHAR(1 CHAR) DEFAULT 'n' );
COMMENT ON COLUMN MDTASK.IRREGULAR IS 'флаг Настандартная сделка';
update mdtask t set product_name=(select max(p.name) from r_mdtask_opp_type r
inner join crm_product p on p.productid=r.id_opp_type
where r.id_mdtask=t.id_mdtask);
commit;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (68, null, 'название вида сделки', 'Основные параметры Сделки', 0, 'main_product_name', 'название вида сделки');
commit;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (45, null, 'название групп видов сделки', 'Основные параметры лимита/Сублимита', 0, 'main_product_group_names', 'название групп видов сделки');
commit;

