package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.vtb.domain.SupplyType;
import com.vtb.exception.MappingException;

public class SupplyTypeMapper  extends JDBCMapper<SupplyType> implements com.vtb.mapping.SupplyTypeMapper {
    private static final Logger LOGGER = Logger.getLogger(SupplyTypeMapper.class.getName());

    @Override
    protected SupplyType createImpl(Connection conn, SupplyType domainObject)
            throws SQLException, MappingException {
        throw new NotImplementedException();
    }

    @Override
    protected SupplyType findByPrimaryKeyImpl(Connection conn,
            SupplyType domainObjectWithKeyValues) throws SQLException,
            MappingException {
        throw new NotImplementedException();
    }

    @Override
    protected void removeImpl(Connection conn, SupplyType domainObject)
            throws SQLException, MappingException {
        throw new NotImplementedException();
        
    }

    @Override
    protected void updateImpl(Connection conn, SupplyType anObject)
            throws SQLException, MappingException {
        throw new NotImplementedException();
        
    }

    public ArrayList<SupplyType> findAll() throws MappingException {
        String CMD_FIND="select k.id, k.name as kname "
            +" from CR_OB_TYPE k where DELETED='0' order by k.name";
        ArrayList<SupplyType> list = new ArrayList<SupplyType>();
        Connection conn = null;
        SupplyType vo = null;
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            ps = conn.prepareStatement(CMD_FIND);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vo = new SupplyType(rs.getLong("id"), rs.getString("kname"));
                list.add(vo);
            }
            return list;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MappingException(e, "SQLException findAll code=" + e.getErrorCode());
        } finally {
            close(conn);
        }
    }

}
