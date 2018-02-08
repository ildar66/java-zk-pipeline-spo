update condition_types set name='Критерий и устанавливаемый размер кредитовых оборотов' where id_type=6;
insert into condition (id_condition,name,id_type,standard,name_ced)
select condition_seq.nextval,c.description,6,1,c.description from cd_credit_turnover_criterium c;
commit;
