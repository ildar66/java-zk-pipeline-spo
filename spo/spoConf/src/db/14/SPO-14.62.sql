ALTER TABLE PIPELINE 
ADD (HIDEINREPORT CHAR(1) );

COMMENT ON COLUMN PIPELINE.HIDEINREPORT IS 'не показывать в отчете';
ALTER TABLE PIPELINE  
MODIFY (HIDEINREPORT DEFAULT 'n' );
CREATE TABLE PIPELINE_HIDEINREPORT 
(
  ID_MDTASK NUMBER 
, DATE_EVENT TIMESTAMP(6) 
, FLAG CHAR 
);

ALTER TABLE PIPELINE_HIDEINREPORT
ADD CONSTRAINT PIPELINE_HIDEINREPORT_MDT_FK1 FOREIGN KEY
(
  ID_MDTASK 
)
REFERENCES MDTASK
(
  ID_MDTASK 
)
ENABLE;

COMMENT ON TABLE PIPELINE_HIDEINREPORT IS 'история событий проставления галочки "не показывать в отчете pipeline"';

COMMENT ON COLUMN PIPELINE_HIDEINREPORT.DATE_EVENT IS 'дата изменения галочки';

COMMENT ON COLUMN PIPELINE_HIDEINREPORT.FLAG IS 'значение галочки y или n';
create index PAYMENT_SCHEDULE_TRANCE_FK1_I on PAYMENT_SCHEDULE(TRANCE_ID);
create index MANAGER_FK3_I on MANAGER(ID_START_DEPARTMENT);
