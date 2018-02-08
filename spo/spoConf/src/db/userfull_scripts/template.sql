insert into roles (id_role,name_role,id_type_process,active,is_admin)
select roles_seq.nextval,'Секретарь',id_type_process,1,0 from type_process where 
DESCRIPTION_PROCESS='Крупный бизнес ГО' and id_type_process not in
(select id_type_process from roles where name_role like 'Секретарь');
commit;
