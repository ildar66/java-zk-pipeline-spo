delete from factpercent f where f.tranceid not in
(select t.id from trance t)
/
ALTER TABLE FACTPERCENT ADD CONSTRAINT FACTPERCENT_FK4 FOREIGN KEY(TRANCEID) REFERENCES TRANCE(ID) ON DELETE CASCADE ENABLE
/
CREATE OR REPLACE VIEW v_currency (CODE, text, IS_ACTIVE) AS 
  select crm.code, crm.code as text, crm.is_active
  from crm_currency crm left join cc_currency_order cc
    on crm.code = cc.code
      order by cc.code_order, crm.code
/
create or replace view v_spo_com_type as
select TEXT as name1,SHORT_TEXT as id1 from CRM_COM_TYPE
/
create or replace view v_spo_com_period as
select TEXT as name1,SHORT_TEXT as id1  from CRM_COM_PERIOD
/
create or replace view v_spo_com_base as
select TEXT as name1,SHORT_TEXT as id1  from crm_com_base
/
--отрасль больше не используется
CREATE OR REPLACE VIEW V_INDUSTRY ("INDUSTRY", "CORP_BLOCK", "FB_INDUSTRYID", "INDUSTRY_RATING", "INDUSTRY_TYPE") AS
select '','','','','' from dual
/
COMMENT ON COLUMN MDTASK.ID_INDUSTRY IS 'deprecated'
/
