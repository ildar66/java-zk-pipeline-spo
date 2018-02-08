and not exists --нет назначенных, у которых есть права работать над этой задачей
(select 1 from stages_in_role sr  inner join (select * from user_in_role where lower(status) = 'y') ur on ur.id_role = sr.id_role 
inner join users u on u.id_user=ur.id_user 
WHERE sr.id_stage = t.id_stage_to and t.id_department in
(select u.id_department from dual union select dp.id_department_child from departments_par dp CONNECT BY PRIOR id_department_child=id_department_par
START WITH dp.id_department_par =u.id_department)--задача может быть в департаменте ниже того пользователя
and u.is_active = 1 
and exists (select 1 from assign a inner join process_events pe on a.id_process_event=pe.id_process_event where pe.id_process=t.id_process 
and a.id_user_to=u.id_user and id_role=ur.id_role))
