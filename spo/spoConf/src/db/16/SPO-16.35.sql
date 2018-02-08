alter table INTEREST_PAY add cmnt VARCHAR2(1024);
comment on column INTEREST_PAY.cmnt is 'комментарий';
alter table PRINCIPAL_PAY add cmnt VARCHAR2(1024);
comment on column PRINCIPAL_PAY.cmnt is 'комментарий';
alter table FINE add productrate char(1) default 'n' not null;
comment on column FINE.productrate
  is 'признак Увеличивает ставку по сделке';