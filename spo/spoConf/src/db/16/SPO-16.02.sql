ALTER TABLE DEPOSIT 
ADD (ID_PERSON NUMBER );

ALTER TABLE DEPOSIT
ADD CONSTRAINT DEPOSIT_CD_PERSON_FK1 FOREIGN KEY
(
  ID_PERSON 
)
REFERENCES CD_PERSON
(
  ID_PERSON 
)
ENABLE;
ALTER TABLE DEPOSIT  
MODIFY (ID_CRMORG NULL);
