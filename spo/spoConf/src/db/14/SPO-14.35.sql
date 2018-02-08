declare
 counter int;
 report_num int;
begin
 report_num := 216;
 select count(*) into counter from report_template where id_template = report_num;
 if counter = 0 then
     insert into report_template(id_template, template_name, type, filename, system, full_hierarchy, template_data)
       values(report_num, 'Шаблон утверждения документа', 'HIDDEN_REPORT', 'signature_report', 'СПО', 'N', empty_clob());
 else
     update report_template set template_data = empty_clob() where id_template = report_num;
 end if;
end;
/
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (3000, 3001);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (3000, null, 'ФИО руководителя, утвердившего документ и поставившего ЭЦП', 'Утверждение документа', 0, 'signature_fio', 'ЭЦП.ФИО');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (3001, null, 'Дата утверждения и подписи ЭЦП руководителя, утвердившего документ', 'Утверждение документа', 0, 'signature_date', 'ЭЦП.Дата');
commit;

