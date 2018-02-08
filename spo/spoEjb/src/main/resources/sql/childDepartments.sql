id_department in (select dp.id_department_child from departments_par dp CONNECT BY PRIOR id_department_child=id_department_par
START WITH dp.id_department_par ={1} union select {1} from dual)