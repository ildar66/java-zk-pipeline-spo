update mdtask t set t.id_limit_type=null where t.id_limit_type not in(
select l.id_limit_type from limit_type l) and t.id_limit_type is not null
/
alter table MDTASK add constraint MDTASK_FK24 foreign key (ID_LIMIT_TYPE) references limit_type (ID_LIMIT_TYPE)
/
