ALTER TABLE FACTPERCENT ADD (MANUAL_FONDRATE CHAR(1) );
COMMENT ON COLUMN FACTPERCENT.MANUAL_FONDRATE IS 'ручная Ставка фондирования';
ALTER TABLE PAYMENT_SCHEDULE ADD (PERIOD NUMBER(9, 0) );
