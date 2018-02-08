begin
  PKG_DDL_UTILS.ADD_TABLE_COLUMN('DOCUMENT_GROUP', 'IS_ACTIVE', 'NUMBER(1) default 1 not null');
end;
/
begin
  PKG_DDL_UTILS.ADD_TABLE_COLUMN('DOCUMENTS_TYPE', 'IS_ACTIVE', 'NUMBER(1) default 1 not null');
end;
/
begin
  PKG_DDL_UTILS.ADD_TABLE_COLUMN('R_DOCUMENT_GROUP', 'IS_ACTIVE', 'NUMBER (1) default 1 not null');
end;
/
