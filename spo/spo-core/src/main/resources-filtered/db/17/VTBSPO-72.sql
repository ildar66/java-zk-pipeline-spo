BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD VERSION NUMBER(38,0) DEFAULT 1');
END;
/
COMMENT ON COLUMN MDTASK.VERSION IS 'номер версии'
/
UPDATE MDTASK M SET M.VERSION=1
/
COMMIT
/
