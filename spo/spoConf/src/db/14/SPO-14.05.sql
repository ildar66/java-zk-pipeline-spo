CREATE SEQUENCE r_period_obkind_seq
/
update factpercent p set p.rating_c1=p.rating_ktr where p.tranceid is not null
/
commit
/
