--liquibase formatted sql

--changeset apavlenko:spo-app-version logicalFilePath:spo-app-version endDelimiter:/ failOnError:true runAlways:true runOnChange:true
UPDATE CD_SYSTEM_MODULE SET CURRENT_VERSION  = '${module.version}' WHERE KEY = 'CPPS'
/
COMMIT
/
