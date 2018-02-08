CREATE OR REPLACE VIEW V_SPO_PRODUCT AS
SELECT productid, NAME, family, IS_ACTIVE, KEY FROM crm_product
where TRIM(LOWER(family)) in ('кредитование', 'банковские гарантии', 'документарные операции')
/
alter table WARRANTY add fromdate date
/
alter table WARRANTY add todate date
/
alter table WARRANTY add supplyvalue NUMBER(38,5)
/
comment on column WARRANTY.fromdate is 'срок с даты'
/
comment on column WARRANTY.todate is 'срок по дату'
/
comment on column WARRANTY.supplyvalue is 'Степень обеспечения'
/
alter table garant add fromdate date
/
alter table garant add todate date
/
alter table garant add supplyvalue NUMBER(38,5)
/
comment on column garant.fromdate is 'срок с даты'
/
comment on column garant.todate is 'срок по дату'
/
comment on column garant.supplyvalue is 'Степень обеспечения'
/
alter table deposit add fromdate date
/
alter table deposit add todate date
/
alter table deposit add supplyvalue NUMBER(38,5)
/
comment on column deposit.fromdate is 'срок с даты'
/
comment on column deposit.todate is 'срок по дату'
/
comment on column deposit.supplyvalue is 'Степень обеспечения'
/
create table r_period_obkind
(
id_ob_kind number not null,
id_factpercent number not null,
supplyvalue NUMBER(38,5),
id number not null
)
/
comment on table r_period_obkind is 'связь группу обеспечения с периодом'
/
comment on column r_period_obkind.id_ob_kind is 'Группа обеспечения'
/
comment on column r_period_obkind.id_factpercent is 'период'
/
comment on column r_period_obkind.supplyvalue is 'Степень обеспечения'
/
alter table r_period_obkind add constraint r_period_obkind_pk primary key (ID)
/
alter table r_period_obkind add constraint r_period_obkind_fk1 foreign key (ID_FACTPERCENT) references factpercent (ID) on delete cascade
/
