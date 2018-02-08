package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.VtbObject;
import com.vtb.exception.MappingException;

public class SequenceMapper extends JDBCMapper<VtbObject> {

	@Override
	protected VtbObject createImpl(Connection conn, VtbObject domainObject)
			throws SQLException, MappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VtbObject findByPrimaryKeyImpl(Connection conn,
			VtbObject domainObjectWithKeyValues) throws SQLException,
			MappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void removeImpl(Connection conn, VtbObject domainObject)
			throws SQLException, MappingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateImpl(Connection conn, VtbObject anObject)
			throws SQLException, MappingException {
		// TODO Auto-generated method stub

	}

	public ArrayList<VtbObject> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
