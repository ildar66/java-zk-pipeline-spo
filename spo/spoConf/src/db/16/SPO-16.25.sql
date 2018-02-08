BEGIN
PKG_DDL_UTILS.ADD_TABLE_COLUMN('MDTASK', 'TRANCE_GRAPH', 'CHAR(1)');
PKG_DDL_UTILS.ADD_TABLE_COLUMN('MDTASK', 'TRANCE_PERIOD_FORMAT', 'VARCHAR2(20)');
END;
/
COMMENT ON COLUMN MDTASK.TRANCE_GRAPH IS 'имеется ли график выдачи траншей';
COMMENT ON COLUMN MDTASK.TRANCE_PERIOD_FORMAT IS 'формат периода предоставления';
COMMIT;
ALTER TABLE WITHDRAW MODIFY (YEAR NUMBER(5, 0) );
