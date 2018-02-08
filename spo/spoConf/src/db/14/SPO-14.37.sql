declare
 counter int;
 report_num int;
begin
 report_num := 216;
 select count(*) into counter from report_template where id_template = report_num;
 if counter = 0 then
     insert into report_template(id_template, template_name, type, filename, system, full_hierarchy, template_data)
       values(report_num, 'Шаблон утверждения документа', 'PRINT_FORM_WORD', 'signature_report', 'СПО', 'N', empty_clob());
 else
     update report_template set template_data = empty_clob(), type = 'PRINT_FORM_WORD' where id_template = report_num;
 end if;
end;
/
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (3000, 3001);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (3000, null, 'ФИО руководителя, утвердившего документ и поставившего ЭЦП', 'Утверждение документа', 0, 'signature_fio', 'ЭЦП.ФИО');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (3001, null, 'Дата утверждения и подписи ЭЦП руководителя, утвердившего документ', 'Утверждение документа', 0, 'signature_date', 'ЭЦП.Дата');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required = 102;
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (102, 'Плата за экономический капитал', 'Плата за экономический капитал', 'Стоимостные условия Лимита/Сублимита. Процентная ставка', 0, 'procent_capital_pay', 'Плата_ЭкономическийКапитал');
commit;
create or replace view v_role_hierarchy as
select *
from 
(select     r.id_type_process, tp.description_process,
            level - 1 as special_level,
            lpad(' ',8*(level-1))|| name_role as name,
            role_child, role_parent,
            rownum as sort_order
      from 
           (select  rn1.role_child, rn1.role_parent from role_nodes rn1 
            union 

            select distinct (n.role_parent) as role_child, null  as role_parent
               from role_nodes n
               where n.role_parent not in (select distinct (d.role_child) from role_nodes d)
            union

            select r.id_role as role_child, null as role_parent
              from roles r
              where r.id_role not in 
                (select distinct role_parent from role_nodes 
                 union select distinct role_child from role_nodes)
              and r.active = 1
     ) rn

      left join roles r on r.id_role = rn.role_child
      inner join type_process tp on tp.id_type_process = r.id_type_process
      start with rn.role_parent is null
      connect by rn.role_parent = prior  rn.role_child
      order siblings by name_role ) t
order by description_process, sort_order;
CREATE TABLE EX_PROJECT_TEAM 
(
  ID_MDTASK NUMBER NOT NULL 
, ID_USER NUMBER NOT NULL 
);

ALTER TABLE EX_PROJECT_TEAM
ADD CONSTRAINT EX_PROJECT_TEAM_MDTASK_FK1 FOREIGN KEY
(
  ID_MDTASK 
)
REFERENCES MDTASK
(
  ID_MDTASK 
)
ENABLE;

ALTER TABLE EX_PROJECT_TEAM
ADD CONSTRAINT EX_PROJECT_TEAM_USERS_FK1 FOREIGN KEY
(
  ID_USER 
)
REFERENCES USERS
(
  ID_USER 
)
ENABLE;

COMMENT ON TABLE EX_PROJECT_TEAM IS 'бывшие члены проектной команды';

