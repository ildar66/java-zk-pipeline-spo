update attributes_required set description='Срок действия сделки',template_code_rus='Срок_действия_сделки' where id_attributes_required=57;
insert into ATTRIBUTES_REQUIRED (ID_ATTRIBUTES_REQUIRED, NAME, DESCRIPTION, GROUP_NAME, IS_REQUIRED, TEMPLATE_CODE, TEMPLATE_CODE_RUS)
values (58, 'Срок действия сделки размерность', 'Срок действия сделки размерность', 'Основные параметры Сделки', 0, 'main_periodDimension', 'Срок_действия_сделки_размерность');
commit;
