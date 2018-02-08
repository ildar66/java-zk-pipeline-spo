package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.vtb.domain.KeyValue;
import com.vtb.exception.MappingException;

public class KeyValueMapper extends JDBCMapper<KeyValue> {

	@Override
	protected KeyValue createImpl(Connection conn, KeyValue domainObject)
			throws SQLException, MappingException {
		throw new MappingException("not valid for this type");
	}

	@Override
	protected KeyValue findByPrimaryKeyImpl(Connection conn,
			KeyValue domainObjectWithKeyValues) throws SQLException,
			MappingException {
		KeyValue res=null;
		PreparedStatement ps = conn.prepareStatement("select key, value from keyvalue where key=?");
        ps.setObject(1, domainObjectWithKeyValues.getKey());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            res = new KeyValue(rs.getString("key"),rs.getString("value"));
        }
        return res;
	}

	@Override
	protected void removeImpl(Connection conn, KeyValue domainObject)
			throws SQLException, MappingException {
		throw new MappingException("not valid for this type");
	}

	@Override
	protected void updateImpl(Connection conn, KeyValue anObject)
			throws SQLException, MappingException {
		throw new MappingException("not valid for this type");
	}

	@Override
	public List<KeyValue> findAll() throws MappingException {
		throw new MappingException("not valid for this type");
	}

}
