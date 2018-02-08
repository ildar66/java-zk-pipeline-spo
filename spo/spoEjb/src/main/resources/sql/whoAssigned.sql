--проверка назначена ли заявка и на кого по idtask
select a.id_user_to,a.id_role,a.id_user_from from tasks t
inner join process_events pe on pe.id_process=t.id_process
inner join assign a on a.id_process_event=pe.id_process_event
where t.id_task={0} 
and exists (select 1 from stages_in_role sr where sr.id_role=a.id_role and sr.id_stage=t.id_stage_to)
--у пользователя до сих пор есть эта роль
and exists (select 1 from  user_in_role ur where ur.id_role = a.id_role and ur.id_user=a.id_user_to and lower(status) = '''y''')
--пользователь активен
and exists (select 1 from users u where u.id_user=a.id_user_to and u.is_active=1)
