--liquibase formatted sql

--changeset svaliev:spo-17.57-VTBSPO-324 logicalFilePath:spo-17.57-VTBSPO-324 endDelimiter:/
CREATE TABLE MDTASK_FAVORITE (
    MDTASK_FAVORITE_ID NUMBER(38) NOT NULL,
    MDTASK_ID          NUMBER(38) NOT NULL,
    USER_ID            NUMBER(38) NOT NULL
)
/

ALTER TABLE MDTASK_FAVORITE ADD CONSTRAINT MDTASK_FAVORITE_PK PRIMARY KEY (MDTASK_FAVORITE_ID)
/
ALTER TABLE MDTASK_FAVORITE ADD CONSTRAINT MDTASK_FAVORITE_MDTASK_ID_FK FOREIGN KEY (MDTASK_ID) REFERENCES MDTASK (ID_MDTASK)
/
ALTER TABLE MDTASK_FAVORITE ADD CONSTRAINT MDTASK_FAVORITE_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS (ID_USER)
/
ALTER TABLE MDTASK_FAVORITE ADD CONSTRAINT MDTASK_FAVORITE_UQ UNIQUE (MDTASK_ID, USER_ID)
/

CREATE SEQUENCE MDTASK_FAVORITE_SEQ
/
