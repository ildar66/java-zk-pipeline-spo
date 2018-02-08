alter table MDTASK drop constraint MDTASK_FK24;
alter table MDTASK add constraint MDTASK_FK24 foreign key (ID_LIMIT_TYPE) references LIMIT_TYPE (ID_LIMIT_TYPE) on delete set null;