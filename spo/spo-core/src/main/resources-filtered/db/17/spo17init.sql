BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('CREATE INDEX MDTASK_DELETED ON MDTASK (DELETED)');
PKG_DDL_UTILS.EXECUTE_STRING('ALTER TABLE MDTASK ADD TASKTYPE CHAR(1)');
END;
/
COMMENT ON COLUMN MDTASK.TASKTYPE IS 'l - лимит, p - сделка, s - саблимит'
/
UPDATE MDTASK T SET T.TASKTYPE='l' WHERE T.ID_PUP_PROCESS IN
(SELECT A.ID_PROCESS FROM ATTRIBUTES A 
INNER JOIN VARIABLES V ON V.ID_VAR=A.ID_VAR AND V.NAME_VAR LIKE 'Тип кредитной заявки'
WHERE A.VALUE_VAR LIKE 'Лимит')
/
UPDATE MDTASK T SET T.TASKTYPE='p' WHERE T.ID_PUP_PROCESS IN
(SELECT A.ID_PROCESS FROM ATTRIBUTES A 
INNER JOIN VARIABLES V ON V.ID_VAR=A.ID_VAR AND V.NAME_VAR LIKE 'Тип кредитной заявки'
WHERE A.VALUE_VAR LIKE 'Сделка')
/
COMMIT
/
BEGIN
PKG_DDL_UTILS.EXECUTE_STRING('CREATE INDEX MDTASK_TASKTYPE ON MDTASK (TASKTYPE)');
END;
/
