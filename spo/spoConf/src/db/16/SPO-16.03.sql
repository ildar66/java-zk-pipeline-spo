CREATE OR REPLACE FORCE VIEW V_SPO_PRODUCT ("PRODUCTID", "NAME", "FAMILY", "IS_ACTIVE", "KEY") AS 
SELECT productid, NAME, family, IS_ACTIVE, KEY FROM crm_product
where TRIM(LOWER(family)) in ('кредитование', 'банковские гарантии', 'документарные операции', 'структурное финансирование')
union all
SELECT productid, NAME, family, 0 as IS_ACTIVE, KEY FROM crm_product
where TRIM(LOWER(family)) not in ('кредитование', 'банковские гарантии', 'документарные операции', 'структурное финансирование');
insert into r_mdtask_othergoals (id_target,id_mdtask,descr)
select R_MDT_otherGoals_SEQ.nextval as id,t.id_mdtask,c.text from r_mdtask_target t inner join crm_target_type c on c.itemn_id=t.crm_target;
commit;