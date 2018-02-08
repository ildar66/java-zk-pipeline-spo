package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.OperationType;
import com.vtb.exception.MappingException;

public class OperationTypeMapper  extends JDBCMapper<OperationType> {

	@Override
	protected OperationType createImpl(Connection conn,
			OperationType domainObject) throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected OperationType findByPrimaryKeyImpl(Connection conn,
			OperationType domainObjectWithKeyValues) throws SQLException,
			MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void removeImpl(Connection conn, OperationType domainObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void updateImpl(Connection conn, OperationType anObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	public ArrayList<OperationType> findAll() throws MappingException {
		ArrayList<OperationType> list = new ArrayList<OperationType>();
		OperationType ot = null;
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("select id, name from CR_SDELKA_TYPE where DELETED ='0'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	ot = new OperationType(rs.getLong("id"), rs.getString("name"));
                list.add(ot);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findAll code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
	}

}
