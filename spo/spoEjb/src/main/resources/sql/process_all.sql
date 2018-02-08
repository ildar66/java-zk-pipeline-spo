select p.id_process
from processes p
inner join mdtask m on m.ID_PUP_PROCESS=p.ID_PROCESS
left outer join DEPARTMENTS d on d.ID_DEPARTMENT=m.INITDEPARTMENT
{6}
where (
--Для всех показываем заявки, в которых лично пользователь выполнял операцию
exists (select 1 from tasks t where t.id_user={0} and p.id_process=t.id_process )
--для больших аудиторов вообще все заявки по БП
or p.id_type_process in ({5})
--Для всех показываем заявки где пользователь в проектной команде
or m.id_mdtask in (select pt.id_mdtask from project_team pt where pt.id_user={0})
or m.id_mdtask in (select pt.id_mdtask from EX_PROJECT_TEAM pt where pt.id_user={0})
--только для руководителей. Заявки, хотя бы одна операция по которым выполнялась в подразделении пользователя или ниже
or p.id_type_process in ({4})
and exists (select 1 from tasks t where p.id_process=t.id_process and t.ID_DEPARTMENT in
(select {2}
from dual UNION
SELECT distinct dp.id_department_child as ID_DEPARTMENT
FROM departments_par dp START WITH dp.id_department_par = {2} CONNECT BY PRIOR dp.id_department_child = dp.id_department_par))
--или если люди из его подразделения или ниже входят в проектную команду. Не обязательно его подчиненные
or p.id_type_process in ({3}) and exists
(select 1
from project_team pt inner join users u on pt.ID_USER=u.ID_USER
where pt.ID_MDTASK=m.id_mdtask and u.ID_DEPARTMENT={2})
)
