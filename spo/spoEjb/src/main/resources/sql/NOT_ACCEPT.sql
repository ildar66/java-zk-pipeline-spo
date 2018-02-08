SELECT distinct t.id_task,t.id_process FROM
(select * from user_in_role where id_user={0} and lower(status) = '''y''') ur inner join stages_in_role sr on ur.id_role = sr.id_role
inner join (select * from tasks where id_status = 1 and {2})  t on sr.id_stage = t.id_stage_to
inner join mdtask m on m.id_pup_process=t.id_process
inner join stages s on t.id_stage_to=s.id_stage
inner join  processes p on t.id_process=p.id_process
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT
where 1=1 
