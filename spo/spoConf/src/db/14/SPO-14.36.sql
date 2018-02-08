create table expert_team
(
  id_mdtask number not null,
  id_user   number,
  id        number
)
/
comment on table expert_team is 'экспертная команда'
/
alter table expert_team add constraint expert_team_pk primary key (ID)
/
alter table expert_team add constraint expert_team_fk1 foreign key
(ID_MDTASK)
references mdtask (ID_MDTASK) on delete cascade
/
alter table expert_team add constraint expert_team_fk2 foreign key
(ID_USER)
references users (ID_USER) on delete cascade
/
create sequence expert_team_seq
/
ALTER TABLE EXPERT_TEAM ADD (EXPNAME VARCHAR2(300) )
/
COMMENT ON COLUMN EXPERT_TEAM.EXPNAME IS 'название экспертизы'
/
