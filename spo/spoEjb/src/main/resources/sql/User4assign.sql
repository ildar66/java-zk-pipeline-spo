select u.id_user, min(rh.SPECIAL_LEVEL) as role_level, dh.special_level, dh.path_full as dep_name, u.surname, u.name, u.patronymic
from users u 
inner join user_in_role ur on ur.id_user=u.id_user and ur.status = '''Y'''
inner join roles r on r.id_role = ur.id_role and r.active = 1
inner join stages_in_role sr on sr.id_role=ur.id_role
inner join v_role_hierarchy rh on rh.ROLE_CHILD = ur.id_role
inner join departments_hierarchy dh on dh.id_department = u.id_department
where u.is_active = 1   
and u.id_department in (select dp.id_department_child from departments_par dp CONNECT BY PRIOR id_department_child=id_department_par
START WITH dp.id_department_par ={1} union select {1} from dual)
and ur.id_role in (select rn.role_child from role_nodes rn CONNECT BY PRIOR role_child=role_parent
START WITH role_parent in (select ur.id_role from user_in_role ur where ur.id_user={0} and ur.status='''Y''')
union select ur.id_role from user_in_role ur where ur.id_user={0} and ur.status='''Y'''
and ur.id_role in (select vrr.role_parent from  v_role_hierarchy vrr minus select null from dual))
and ur.status='''Y''' and sr.id_stage={2}
group by dh.special_level, dh.path_full, u.id_user, u.surname, u.name, u.patronymic
order by role_level, dh.special_level, dep_name, surname, u.name, patronymic


