ALTER TABLE MDTASK ADD (TRANCE_LIMIT_USE CHAR(1 BYTE) DEFAULT 'n' );
ALTER TABLE MDTASK ADD (TRANCE_LIMIT_EXCESS CHAR(1 BYTE) DEFAULT 'n' );
ALTER TABLE MDTASK ADD (TRANCE_HARD_GRAPH CHAR(1 BYTE) DEFAULT 'n' );
COMMENT ON COLUMN MDTASK.TRANCE_LIMIT_USE IS 'Допускается использование недоиспользованного лимита';
COMMENT ON COLUMN MDTASK.TRANCE_LIMIT_EXCESS IS 'Допускается превышение лимита по графику';
COMMENT ON COLUMN MDTASK.TRANCE_HARD_GRAPH IS 'Жесткий график';
CREATE OR REPLACE FORCE VIEW V_SPO_PRODUCT ("PRODUCTID", "NAME", "FAMILY", "IS_ACTIVE", "KEY", "ACTUALID") AS 
SELECT productid, NAME, family, IS_ACTIVE, KEY, ACTUALID FROM crm_product
where TRIM(LOWER(family)) in ('кредитование', 'банковские гарантии', 'документарные операции', 'структурное финансирование')
union all
SELECT productid, NAME, family, 0 as IS_ACTIVE, KEY, ACTUALID FROM crm_product
where TRIM(LOWER(family)) not in ('кредитование', 'банковские гарантии', 'документарные операции', 'структурное финансирование')
/
