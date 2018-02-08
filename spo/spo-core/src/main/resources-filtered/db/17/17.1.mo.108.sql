UPDATE condition_types ct SET ct.name='Основание для досрочного истребования, приостановления использования' WHERE ct.key='USING_STOPPAGE'
/
update CPS_DEAL_CONDITION c set id_condition_type=(select id_type from condition_types where key='USING_STOPPAGE')
where c.id_condition_type in
(select id_type from condition_types where key='EARLY_ENFORCEMENT')
/
update condition c set id_type=(select id_type from condition_types where key='USING_STOPPAGE')
where c.id_type in
(select id_type from condition_types where key='EARLY_ENFORCEMENT')
/
delete from condition_types where key='EARLY_ENFORCEMENT'
/
