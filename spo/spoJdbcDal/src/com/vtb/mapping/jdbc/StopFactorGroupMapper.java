package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vtb.domain.VtbObject;
import com.vtb.exception.MappingException;

public class StopFactorGroupMapper extends JDBCMapperExt<VtbObject> {

    @Override
    protected List<VtbObject> findAllImpl(Connection conn) throws SQLException, MappingException {

        return null;
    }

    @Override
    protected void insertImpl(Connection conn, VtbObject anObject) throws SQLException, MappingException {

    }

    @Override
    protected VtbObject createImpl(Connection conn, VtbObject domainObject) throws SQLException, MappingException {

        return null;
    }

    @Override
    protected VtbObject findByPrimaryKeyImpl(Connection conn, VtbObject domainObjectWithKeyValues) throws SQLException, MappingException {

        return null;
    }

    @Override
    protected void removeImpl(Connection conn, VtbObject domainObject) throws SQLException, MappingException {

    }

    @Override
    protected void updateImpl(Connection conn, VtbObject anObject) throws SQLException, MappingException {

    }

}
