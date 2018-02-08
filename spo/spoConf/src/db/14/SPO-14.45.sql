delete from ATTRIBUTES_REQUIRED where id_attributes_required in (136, 137);
ALTER TABLE R_ORG_MDTASK ADD (RATINGPKR VARCHAR2(40) );
delete from ATTRIBUTES_REQUIRED where id_attributes_required in (1637,1674);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1637, '', 'Рейтинг_ПКР(в таблице Контрагенты)', 'Контрагенты', 1, 'co_ratingPKR', 'КО_Рейтинг_ПКР');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1674, '', 'Основной заемщик.Рейтинг_ПКР', 'Контрагенты', 1, 'mainBorrower_ratingPKR', 'Осн_заемщик_Рейтинг_ПКР');
commit;
