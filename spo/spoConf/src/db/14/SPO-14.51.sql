delete from ATTRIBUTES_REQUIRED where id_attributes_required in (504);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (504, 'd_zalog_desc', 'Наименование и характеристики предмета залога (таблица Залоги)', 'Залоги', 0, 'dl_zalogDescription', 'З_Наименование_Характеристики');
