BEGIN
    PKG_DDL_UTILS.RECREATE_SEQUENCE_FOR_TABLE('r_org_mdtask','ID_R', 'r_org_mdtask_seq');
END;
/
