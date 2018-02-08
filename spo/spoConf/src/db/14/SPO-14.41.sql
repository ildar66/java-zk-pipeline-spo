CREATE TABLE PREMIUM 
(
  ID NUMBER NOT NULL 
, ID_MDTASK NUMBER NOT NULL 
, TYPE NUMBER NOT NULL 
, VAL NUMBER(12, 5) 
, CURR CHAR(3) 
, TEXT VARCHAR2(2000) 
, CONSTRAINT PREMIUM_PK PRIMARY KEY 
  (
    ID 
  )
  ENABLE 
);

ALTER TABLE PREMIUM
ADD CONSTRAINT PREMIUM_CD_PREMIUM_TYPE_FK1 FOREIGN KEY
(
  TYPE 
)
REFERENCES CD_PREMIUM_TYPE
(
  ID_PREMIUM 
)
ENABLE;

ALTER TABLE PREMIUM
ADD CONSTRAINT PREMIUM_MDTASK_FK1 FOREIGN KEY
(
  ID_MDTASK 
)
REFERENCES MDTASK
(
  ID_MDTASK 
)
ENABLE;

COMMENT ON COLUMN PREMIUM.TYPE IS 'Вознаграждения';

COMMENT ON COLUMN PREMIUM.VAL IS 'величина';

COMMENT ON COLUMN PREMIUM.CURR IS 'валюта';

COMMENT ON COLUMN PREMIUM.TEXT IS 'формула';
CREATE SEQUENCE PREMIUM_SEQ;
COMMENT ON COLUMN MDTASK.PREMIUMTYPE IS 'deprecated';

COMMENT ON COLUMN MDTASK.PREMIUMVALUE IS 'deprecated';

COMMENT ON COLUMN MDTASK.PREMIUMCURR IS 'deprecated';

COMMENT ON COLUMN MDTASK.PREMIUMTEXT IS 'deprecated';
insert into PREMIUM (id,id_mdtask,type,val,curr,text) 
select PREMIUM_seq.nextval,id_mdtask,premiumtype,premiumvalue,premiumcurr,premiumtext from mdtask where premiumtype is not null;
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (1722, 1723, 1773, 1774); 
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1722, null, 'Индикативная ставка + Ставка размещения (таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_rate4Print', 'ФА_ИндикативнаяИСтавкаРазмещения');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1773, null, 'Индикативная ставка + Ставка размещения (таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_rate4Print', 'ФА2_ИндикативнаяИСтавкаРазмещения');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1723, null, 'КТР для Сделки (таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_rate11', 'ФА_КТР_Сделка');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1774, null, 'КТР для Сделки (таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_rate11', 'ФА2_КТР_Сделка');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (1718, 1719, 1720, 1721, 1768, 1769, 1770, 1771, 2100, 2101, 2102, 2103, 2104, 2105);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2100, null, 'Вознаграждения (таблица)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premiumList', 'Вознаграждения');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2101, null, 'Для печати вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premium_premiumForPrint', 'ВЗНГ_Вознаграждение_ДляПечати');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2102, null, 'Наименование вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premium_premiumType', 'ВЗНГ_Вознаграждение_Тип');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2103, null, 'Сумма вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premium_premiumTypeValue', 'ВЗНГ_Вознаграждение_Сумма');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2104, null, 'Валюта вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premium_premiumCurr', 'ВЗНГ_Вознаграждение_Валюта');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (2105, null, 'Текст вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка. Вознаграждения', 0, 'premium_premiumText', 'ВЗНГ_Вознаграждение_Текст');
commit;
