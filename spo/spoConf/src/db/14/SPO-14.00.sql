create or replace view v_cc_doc as
select t.id_mdtask, a."UNID",a."FILENAME",a."FILEDATA",a."FILETYPE",a."ID_OWNER",a."OWNER_TYPE",a."WHO_ADD",a."DATE_OF_ADDITION",a."DATE_OF_EXPIRATION",a."ISACCEPTED",a."WHOACCEPTED",a."DATE_OF_ACCEPT",a."SIGNATURE",a."ID_APPL",a."ID_GROUP",a."FORCC",a."CONTENTTYPE" from appfiles a inner join mdtask t on to_char(t.id_pup_process) = a.id_owner
     where a.owner_type=0 and a.fileurl is null
       and lower(a.forcc)='y'
/
