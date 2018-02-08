delete from ATTRIBUTES_REQUIRED where id_attributes_required in (620, 621, 710, 711);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (620, 'Отлагательные условия использования средств', 'Отлагательное условие использования кредитных средств (в таблице)', 'Условия. Прочие условия', 0, 'othCondType2_body', 'ОУК_Условие');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (621, null, 'Отлагательные условия использования кредитных средств (таблица)', 'Условия. Прочие условия', 0, 'otherConditionType2', 'ОТЛАГАТЕЛЬНЫЕ_УСЛОВИЯ_КРЕДИТ');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (710, 'Отлагательные условия заключения сделки', 'Отлагательное условие заключения сделки (в таблице)', 'Условия. Прочие условия', 0, 'othCondType1_body', 'ОУС_Условие');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (711, null, 'Отлагательные условия заключения сделки (таблица)', 'Условия. Прочие условия', 0, 'otherConditionType1', 'ОТЛАГАТЕЛЬНЫЕ_УСЛОВИЯ_СДЕЛКИ');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required >= 1718 and id_attributes_required <=1722;
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1718, null, 'Для печати вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_premiumForPrint', 'ФА_Вознаграждение_ДляПечати');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1719, null, 'Наименование вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_premiumType', 'ФА_Вознаграждение_Тип');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1720, null, 'Сумма вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_premiumTypeValue', 'ФА_Вознаграждение_Сумма');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1721, null, 'Валюта вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_premiumcurr', 'ФА_Вознаграждение_Валюта');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1722, null, 'Формула вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'fact_premiumtext', 'ФА_Вознаграждение_Формула');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required >= 1768 and id_attributes_required <=1772;
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1768, null, 'Сумма и валюта для вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_premiumForPrint', 'ФА2_Вознаграждение_ДляПечати');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1769, null, 'Наименование вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_premiumType', 'ФА2_Вознаграждение_Тип');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1770, null, 'Сумма вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_premiumTypeValue', 'ФА2_Вознаграждение_Сумма');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1771, null, 'Валюта вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_premiumcurr', 'ФА2_Вознаграждение_Валюта');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1772, null, 'Формула вознаграждения(таблица факт.значений процентной ставки)', 'Стоимостные условия Сделки. Процентная ставка', 0, 'factShort_premiumtext', 'ФА2_Вознаграждение_Формула');
commit;