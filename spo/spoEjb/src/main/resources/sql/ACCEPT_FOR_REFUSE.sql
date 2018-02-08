--id_user={0}, id_department={1}
SELECT distinct t.id_task,t.id_process
FROM tasks t 
inner join mdtask m on m.id_pup_process=t.id_process
inner join  processes p on t.id_process=p.id_process
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT
inner join stages s on t.id_stage_to=s.id_stage 
inner join users executor on executor.id_user=t.id_user
inner join user_in_role eur on eur.id_user=executor.id_user and eur.status='''Y'''
inner join stages_in_role sr on sr.id_stage=t.id_stage_to
WHERE t.id_status = 2 and sr.id_role=eur.id_role
and executor.id_department in (select dp.id_department_child from departments_par dp CONNECT BY PRIOR id_department_child=id_department_par
START WITH dp.id_department_par ={1} union select {1} from dual)
and (
eur.id_role in (select rn.role_child from user_in_role bur inner join role_nodes rn on rn.role_parent=bur.id_role where bur.id_user={0} and bur.status='''Y''')
or
exists (select 1 from role_nodes rn where rn.role_parent=eur.id_role) 
and eur.id_role in (select bur.id_role from user_in_role bur where bur.id_user={0} and bur.status='''Y''')
)