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
declare
    counter int;
begin
    select count(*) into counter from user_tab_columns where lower(table_name) = 'procent' and lower(column_name) = 'ktr';
    if counter = 0 then
    begin
        execute immediate 'alter table procent add ktr varchar2(128)';
        execute immediate 'comment on column procent.ktr is ''КТР (видимо, коэффициент транзакционного риска. Но строка, не число'' ';
    end;
    end if;
end;
/
declare
    counter int;
begin
    select count(*) into counter from user_tab_columns where lower(table_name) = 'mdtask' and lower(column_name) = 'country';
    if counter = 0 then
    begin
        execute immediate 'alter table mdtask add country varchar2(256)';
        execute immediate 'comment on column mdtask.country is ''Страновая принадлежность'' ';
    end;
    end if;
end;
/
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (101, 1673); 
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (101, null, 'КТР (таблица Стандартные стоимостные условия)', 'Стоимостные условия Лимита/Сублимита. Процентная ставка', 0, 'procent_KTR', 'КТР');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1673, null, 'Страновая принадлежность', 'Контрагенты', 0, 'main_country', 'Страновая_принадлежность');
commit;
