select distinct t.id_process from tasks t inner join processes p on p.id_process=t.id_process
inner join mdtask m on m.ID_PUP_PROCESS=p.ID_PROCESS
inner join project_team pt on pt.id_mdtask=m.id_mdtask
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT 
{6}
where pt.id_user={0}
