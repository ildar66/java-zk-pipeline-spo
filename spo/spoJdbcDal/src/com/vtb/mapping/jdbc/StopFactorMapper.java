package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.StopFactor;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class StopFactorMapper extends JDBCMapper<StopFactor> implements com.vtb.mapping.StopFactorMapper {
	protected static final String findByNameSqlString = "SELECT code, description, author FROM "
			 + " STOPFACTOR WHERE LOWER(description) like LOWER(?)";

	protected static final String _loadString = "SELECT code, description, author FROM " 
			+ " STOPFACTOR WHERE code = ?";

	protected static final String _createString = "INSERT INTO " 
			+ " STOPFACTOR (code, description, author) VALUES (?, ?, ?)";

	protected static final String _removeString = "DELETE FROM " 
			+ " STOPFACTOR  WHERE code = ?";

	protected static final String _storeString = "UPDATE " 
			+ " STOPFACTOR  SET description = ?, author=? WHERE code = ?";

	@Override
	protected StopFactor createImpl(Connection conn, StopFactor domainObject) throws SQLException, MappingException {
		String code = null;
		String description = null;
		String author=null;
		if (domainObject instanceof StopFactor) {
			code = domainObject.getCode();
			description = domainObject.getDescription();
			author = domainObject.getType();
		} else {
			// update fails
			throw new DuplicateKeyException("Create Failed " + domainObject);
		}
		PreparedStatement ps = conn.prepareStatement(_createString);
		ps.setObject(1, code);
		ps.setObject(2, description);
		ps.setObject(3, author);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return domainObject;
		else
			// failed
			throw new DuplicateKeyException("Create Failed " + domainObject);
	}

	@Override
	protected StopFactor findByPrimaryKeyImpl(Connection conn, StopFactor domainObjectWithKeyValues) throws SQLException,
			MappingException {
		StopFactor vo = null;
		String stopFactorId = null;
		if (domainObjectWithKeyValues instanceof StopFactor) {
			stopFactorId = ((StopFactor) domainObjectWithKeyValues).getCode();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, stopFactorId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			vo = activate(rs);
		}
		return vo;
	}

	@Override
	protected void removeImpl(Connection conn, StopFactor domainObject) throws SQLException, MappingException {
		String aId = null;
		if (domainObject instanceof StopFactor) {
			aId = domainObject.getCode();
		} else
			throw new MappingException("Removed Failed" + domainObject);
		PreparedStatement ps = conn.prepareStatement(_removeString);
		ps.setObject(1, aId);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return;
		else
			// failed
			throw new MappingException("Remove Failed " + domainObject);

	}

	@Override
	protected void updateImpl(Connection conn, StopFactor domainObject) throws SQLException, MappingException {
        String code = null;
        String description = null;

        code = domainObject.getCode();
        description = domainObject.getDescription();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, description);
        ps.setObject(2, domainObject.getType());
        ps.setObject(3, code);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

	public ArrayList<StopFactor> findByName(String description, String orderBy) throws MappingException {
        ArrayList<StopFactor> list = new ArrayList<StopFactor>();
		StopFactor vo = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer(findByNameSqlString);
		try {
			conn = getConnection();
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				sb.append(" order by " + orderBy);
			}
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, description);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				vo = activate(rs);
				list.add(vo);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected StopFactor activate(ResultSet rs) throws SQLException {
		StopFactor vo = new StopFactor(rs.getString(1), rs.getString(2), rs.getString(3));
		return vo;
	}

	public List<StopFactor> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
