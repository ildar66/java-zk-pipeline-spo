ALTER TABLE mdtask 
ADD (TITLE VARCHAR2(512 CHAR) );
ALTER TABLE FACTPERCENT 
ADD (INDCONDITION VARCHAR2(2000) );
COMMENT ON COLUMN FACTPERCENT.INDCONDITION IS 'индивидуальные условия';
ALTER TABLE PROCENT 
ADD (PRICEINDCONDITION VARCHAR2(2000) );
COMMENT ON COLUMN PROCENT.PRICEINDCONDITION IS 'индивидуальные условия';
