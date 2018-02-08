declare
    counter int;
begin
    select count(*) into counter from user_tab_columns where lower(table_name) = 'garant' and lower(column_name) = 'period';
    if counter = 0 then
    begin
        execute immediate 'alter table garant add period number(38)';
        execute immediate 'comment on column garant.period is ''Срок гарантии в днях'' ';
    end;
    end if;
end;
/
CREATE OR REPLACE VIEW V_SPO_PRODUCT AS
SELECT productid, NAME, family, IS_ACTIVE, KEY FROM crm_product
where TRIM(LOWER(family)) in ('кредитование', 'банковские гарантии', 'документарные операции')
order by family, name;
commit;
declare
    counter int;
begin
    select count(*) into counter from user_tab_columns where lower(table_name) = 'mdtask' and lower(column_name) = 'changed_conditions';
    if counter = 0 then
    begin
        execute immediate 'alter table mdtask add changed_conditions varchar2(512)';
        execute immediate 'comment on column mdtask.changed_conditions is ''Измененные и дополненные условия (для сделки)'' ';
    end;
    end if;
end;
/
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (575, 576, 577, 720); 
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (575, null, 'Срок гарантии в днях (таблица Гарантии)', 'Гарантии', 0, 'gl_period', 'Г_Срок_в_днях');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (577, null, 'Срок гарантии по (дата)', 'Гарантии', 0, 'gl_todate', 'Г_Срок_по');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (576, null, 'Срок гарантии c (дата)', 'Гарантии', 0, 'gl_fromdate', 'Г_Срок_с');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (720, null, 'Измененные и дополненные условия', 'Условия', 0, 'main_changedConditions', 'Условия_Измененные_дополненные');
commit;
declare
  l_nullable varchar2(1);
begin
  select nullable into l_nullable  from user_tab_columns where lower(table_name) = 'request_log' and  lower(column_name) = 'subject';
  if l_nullable = 'N' then
    execute immediate 'alter table request_log modify (subject null)';
  end if;
end;
/
declare
    counter int;
begin
    select count(*) into counter from user_objects where lower(object_name) = 'r_request_log_recepient';
    if counter = 0 then 
    begin
        execute immediate
            'create table r_request_log_recepient
            (
              id_log      number(38) not null,
              id_recepient number(38) not null
            )
            ';
      execute immediate
        'comment on table r_request_log_recepient is ''Список получателей для запроса'' ';
      execute immediate
        'alter table r_request_log_recepient
              add constraint r_rlr_pk primary key (id_log, id_recepient)';
      execute immediate
        'alter table r_request_log_recepient
              add constraint r_rlr_fk01 foreign key (id_log)
              references request_log (id_log)';
      execute immediate
        'alter table r_request_log_recepient
              add constraint r_rlr_fk02 foreign key (id_recepient)
              references users (id_user)';
   end;
   end if;
end;
/
update request_log c  set c.subject = ' '  where c.subject is null
/
update request_log c  set c.body = ' '  where c.body is null
/
declare
    counter int;
begin
    select count(*) into counter from r_request_log_recepient;
    if counter = 0 then 
    begin
        delete from request_log where id_recepient is null;

        insert into r_request_log_recepient
			select id_log, d.id_recepient 
			from 
			  (select distinct (c.id_log * 1000000000 +  d.id_recepient) as filtered_values, c.id_log, d.id_recepient
			        from request_log d,
			         ( select min(c.id_log) as id_log, c.id_mdtask, c.id_sender, c.subject, c.body
			           from request_log c
			           group by c.id_mdtask, c.id_sender, c.subject, c.body
			           order by 1
			         ) c
			         where d.id_mdtask = c.id_mdtask and d.id_sender = c.id_sender and d.subject = c.subject and d.body = c.body) 
			    d;

		delete from request_log c
        where c.id_log not in (select distinct id_log from r_request_log_recepient);
        
        commit;
    end;
    end if;
end;
/
