ALTER TABLE FINE 
ADD (ID_PUNITIVE_MEASURE NUMBER );
ALTER TABLE FINE
ADD CONSTRAINT FINE_PUNITIVE_MEASURE_FK1 FOREIGN KEY
(
  ID_PUNITIVE_MEASURE 
)
REFERENCES PUNITIVE_MEASURE
(
  ID_MEASURE 
)
ENABLE;
