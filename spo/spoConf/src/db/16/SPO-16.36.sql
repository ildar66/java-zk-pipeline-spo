alter table MDTASK add active_decision VARCHAR2(2000);
comment on column MDTASK.active_decision is 'действующее решение';