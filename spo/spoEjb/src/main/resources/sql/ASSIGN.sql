SELECT distinct t.id_task, t.id_process FROM
(select id_stage_to, id_task, id_process, id_department, id_type_process, id_status from tasks)  t
inner join stages_in_role sr on sr.id_stage = t.id_stage_to
inner join (select * from  user_in_role where id_user={0} and lower(status) = '''y''') ur on ur.id_role = sr.id_role
inner join processes p on t.id_process=p.id_process
inner join mdtask m on m.id_pup_process=t.id_process
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT
inner join stages s on t.id_stage_to=s.id_stage
where (p.id_status=1 or p.id_status=2) AND t.id_status = 1
and exists (select 1 from (select id_process_event, id_role from assign where id_user_to ={0}) a 
inner join process_events pe on pe.id_process_event=a.id_process_event  where id_role = ur.id_role and pe.id_process=t.id_process)
and not exists (select 1
                  from tasks wt join SPO_134_NOTIFICATION n on wt.id_stage_to = n.id_wait_stage
                 where wt.id_process = m.id_pup_process
                   and n.id_stage = t.id_stage_to
                   and wt.id_status in (1, 2))
