create table withdraw
(
  id          number not null,
  id_mdtask   number not null,
  id_trance   number,
  sum         NUMBER(38,5),
  currency    CHAR(3),
  usedatefrom date,
  usedateto   date,
  month       number(2),
  quarter     number(2),
  year        number(2),
  hyear       number(2)
)
;
comment on table withdraw
  is 'выдача';
comment on column withdraw.id_mdtask
  is 'сделка';
comment on column withdraw.id_trance
  is 'транш';
comment on column withdraw.sum
  is 'сумма';
comment on column withdraw.currency
  is 'валюта';
comment on column withdraw.usedatefrom
  is 'использовать с';
comment on column withdraw.usedateto
  is 'использовать по';
comment on column withdraw.month
  is 'месяц';
comment on column withdraw.quarter
  is 'квартал';
comment on column withdraw.year
  is 'Год';
comment on column withdraw.hyear
  is 'полугодие';
alter table withdraw
  add constraint withdraw_pk primary key (ID);
alter table withdraw
  add constraint withdraw_fk1 foreign key (ID_MDTASK)
  references mdtask (ID_MDTASK) on delete cascade;
alter table withdraw
  add constraint withdraw_fk2 foreign key (ID_TRANCE)
  references trance (ID) on delete set null;
CREATE SEQUENCE WITHDRAW_SEQ;
insert into roles (id_role,name_role,id_type_process,active,is_admin)
select roles_seq.nextval,'Секретарь',id_type_process,1,0 from type_process where 
DESCRIPTION_PROCESS='Крупный бизнес ГО' and id_type_process not in
(select id_type_process from roles where name_role like 'Секретарь');
