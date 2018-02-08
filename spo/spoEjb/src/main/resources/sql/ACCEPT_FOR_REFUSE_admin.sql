SELECT distinct t.id_task,t.id_process 
FROM tasks t 
inner join mdtask m on m.id_pup_process=t.id_process
inner join  processes p on t.id_process=p.id_process
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT
inner join stages s on t.id_stage_to=s.id_stage 
WHERE t.id_status = 2