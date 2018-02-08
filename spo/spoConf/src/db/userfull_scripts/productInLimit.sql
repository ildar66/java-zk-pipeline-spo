--связывание сделки с лимитом/сублимитом
--проверяем, что лимит есть и мы не ошиблись с номером
select t.id_mdtask from mdtask t where t.mdtask_number=203016;
--включаем сделку в этот лимит
update mdtask t set t.parentid=(select max(id_mdtask) from mdtask l where l.mdtask_number=203016)
where t.mdtask_number=203062;

--ВНИМАНИЕ
--нужно изменить 203016 на нужный номер лимита, а 203062 на нужный номер сделки, которая будет в рамках лимита