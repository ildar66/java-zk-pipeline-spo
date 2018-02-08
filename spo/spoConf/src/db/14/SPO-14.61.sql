CREATE TABLE PIPELINE 
(
  ID_MDTASK NUMBER 
, PLAN_DATE TIMESTAMP(6) 
, STATUS VARCHAR2(200) 
, CLOSE_PROBABILITY NUMBER(38, 5) 
, LAW VARCHAR2(40) 
, GEOGRAPHY VARCHAR2(20) 
, SUPPLY VARCHAR2(20) 
, DESCRIPTION VARCHAR2(2000) 
, CMNT VARCHAR2(2000) 
, ADDITION_BUSINESS VARCHAR2(2000) 
, SYNDICATION CHAR(1) DEFAULT 'n' 
, SYNDICATION_CMNT VARCHAR2(2000) 
, WAL NUMBER 
, HURDLE_RATE NUMBER(38, 5) 
, MARKUP NUMBER(38, 5) 
, PC_CASH NUMBER(38, 5) 
, PC_RES NUMBER(38, 5) 
, PC_DER NUMBER(38, 5) 
, PC_TOTAL NUMBER(38, 5) 
, LINE_COUNT NUMBER(38, 5) 
, PUB CHAR(1) DEFAULT 'n' 
, PRIORITY CHAR(1) DEFAULT 'n' 
, NEW_CLIENT CHAR(1) DEFAULT 'n' 
, FLOW_INVESTMENT VARCHAR2(20) 
, RATING VARCHAR2(20) 
, FACTOR_PRODUCT_TYPE NUMBER(38, 5) 
, FACTOR_PERIOD NUMBER(38, 5) 
, CONTRACTOR VARCHAR2(200) 
, VTB_CONTRACTOR VARCHAR2(200) 
, TRADE_DESC VARCHAR2(200) 
, PROLONGATION CHAR(1) DEFAULT 'n' 
);

COMMENT ON COLUMN PIPELINE.PLAN_DATE IS 'Плановая  Дата  Выборки';

COMMENT ON COLUMN PIPELINE.STATUS IS 'Статус Сделки';

COMMENT ON COLUMN PIPELINE.CLOSE_PROBABILITY IS 'Вероятность Закрытия';

COMMENT ON COLUMN PIPELINE.LAW IS 'Применимое Право';

COMMENT ON COLUMN PIPELINE.GEOGRAPHY IS 'География';

COMMENT ON COLUMN PIPELINE.SUPPLY IS 'Обеспечение';

COMMENT ON COLUMN PIPELINE.DESCRIPTION IS 'Описание Сделки, Включая Структуру, Деривативы и т.д.';

COMMENT ON COLUMN PIPELINE.CMNT IS 'Комментарии по Статусу Сделки, Следующие Шаги';

COMMENT ON COLUMN PIPELINE.ADDITION_BUSINESS IS 'Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США';

COMMENT ON COLUMN PIPELINE.SYNDICATION IS 'Возможность Синдикации';

COMMENT ON COLUMN PIPELINE.SYNDICATION_CMNT IS 'Комментарии по Синдикации';

COMMENT ON COLUMN PIPELINE.WAL IS 'Средневзвешенный Срок Погашения (WAL)';

COMMENT ON COLUMN PIPELINE.HURDLE_RATE IS 'Минимальная Ставка (Hurdle Rate) ';

COMMENT ON COLUMN PIPELINE.MARKUP IS 'Маркап';

COMMENT ON COLUMN PIPELINE.PC_CASH IS 'PCs: Кеш, млн. дол. США';

COMMENT ON COLUMN PIPELINE.PC_RES IS 'PCs: Резервы, млн. дол. США';

COMMENT ON COLUMN PIPELINE.PC_DER IS 'PCs: Деривативы, млн. дол. США';

COMMENT ON COLUMN PIPELINE.PC_TOTAL IS 'PCs: Всего, млн. дол. США';

COMMENT ON COLUMN PIPELINE.LINE_COUNT IS 'Выбранный Объём Линии, млн. дол. США';

COMMENT ON COLUMN PIPELINE.PUB IS 'Публичная Сделка';

COMMENT ON COLUMN PIPELINE.PRIORITY IS 'Приоритет Менеджмента';

COMMENT ON COLUMN PIPELINE.NEW_CLIENT IS 'Новый Клиент';

COMMENT ON COLUMN PIPELINE.FLOW_INVESTMENT IS 'Сделка Flow / Investment';

COMMENT ON COLUMN PIPELINE.RATING IS 'Рейтинг Клиента';

COMMENT ON COLUMN PIPELINE.FACTOR_PRODUCT_TYPE IS 'Коэффициент Типа Сделки';

COMMENT ON COLUMN PIPELINE.FACTOR_PERIOD IS 'Коэффициент по Сроку Погашения';

COMMENT ON COLUMN PIPELINE.CONTRACTOR IS 'Фондирующая Компания';

COMMENT ON COLUMN PIPELINE.VTB_CONTRACTOR IS 'Контрагент со стороны Группы ВТБ';

COMMENT ON COLUMN PIPELINE.TRADE_DESC IS 'Трейдинг Деск';

COMMENT ON COLUMN PIPELINE.PROLONGATION IS 'Пролонгация';
CREATE TABLE PIPELINE_FIN_TARGET 
(
  ID_MDTASK NUMBER NOT NULL 
, VAL VARCHAR2(2000) 
);

COMMENT ON TABLE PIPELINE_FIN_TARGET IS 'цель финансирования';

COMMENT ON COLUMN PIPELINE_FIN_TARGET.VAL IS 'цель финансирования';