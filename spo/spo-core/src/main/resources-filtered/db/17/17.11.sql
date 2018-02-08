--liquibase formatted sql

--changeset apavlenko:spo-17.11-VTBSPO-150 logicalFilePath:spo-17.11-VTBSPO-150 endDelimiter:/ 
update condition_types t set t.name='Стоимостные условия (нестандартные)' where t.key='COST_DEAL_PARAMETER'
/
