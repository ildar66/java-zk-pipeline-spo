package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.PunitiveMeasure;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class PunitiveMeasureMapper extends JDBCMapper<PunitiveMeasure> implements com.vtb.mapping.PunitiveMeasureMapper {
	protected static final String findByNameSqlString = "SELECT ID_MEASURE id, NAME_MEASURE name FROM "
			 + " PUNITIVE_MEASURE WHERE LOWER(NAME_MEASURE) like LOWER(?)";

	protected static final String _loadString = "SELECT ID_MEASURE id, NAME_MEASURE name FROM "
			 + " PUNITIVE_MEASURE WHERE ID_MEASURE = ?";

	protected static final String _createString = "INSERT INTO " 
			+ " PUNITIVE_MEASURE (ID_MEASURE, NAME_MEASURE) VALUES (?, ?)";

	protected static final String _removeString = "DELETE FROM " 
			+ " PUNITIVE_MEASURE  WHERE ID_MEASURE = ?";

	protected static final String _storeString = "UPDATE " 
			+ " PUNITIVE_MEASURE  SET NAME_MEASURE = ? WHERE ID_MEASURE = ?";

	@Override
    protected PunitiveMeasure createImpl(Connection conn, PunitiveMeasure domainObject) throws SQLException, MappingException {
        Integer id = null;
        String name = null;
        id = domainObject.getId();
        name = domainObject.getName();
        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, name);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return domainObject;
        else
            // failed
            throw new DuplicateKeyException("Create Failed " + domainObject);
    }

	@Override
	protected PunitiveMeasure findByPrimaryKeyImpl(Connection conn, PunitiveMeasure domainObjectWithKeyValues) throws SQLException,
			MappingException {
		PunitiveMeasure punitiveMeasure = null;
		Integer punitiveMeasureId = null;
		if (domainObjectWithKeyValues instanceof PunitiveMeasure) {
			punitiveMeasureId = ((PunitiveMeasure) domainObjectWithKeyValues).getId();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, punitiveMeasureId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			punitiveMeasure = activate(rs);
		}
		return punitiveMeasure;
	}

	@Override
	protected void removeImpl(Connection conn, PunitiveMeasure domainObject) throws SQLException, MappingException {
		Integer aId = domainObject.getId();
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
    protected void updateImpl(Connection conn, PunitiveMeasure domainObject) throws SQLException, MappingException {
        Integer id = null;
        String name = null;
        id = domainObject.getId();
        name = domainObject.getName();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, name);
        ps.setObject(2, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

	public ArrayList<PunitiveMeasure> findByName(String name, String orderBy) throws MappingException {
		ArrayList<PunitiveMeasure> list = new ArrayList<PunitiveMeasure>();
		PunitiveMeasure punitiveMeasure = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer(findByNameSqlString);
		try {
			conn = getConnection();
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				sb.append(" order by " + orderBy);
			}
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				punitiveMeasure = activate(rs);
				list.add(punitiveMeasure);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected PunitiveMeasure activate(ResultSet rs) throws SQLException {
		PunitiveMeasure punitiveMeasure = new PunitiveMeasure(((BigDecimal) rs.getObject(1)).intValue(), rs
				.getString(2));
		// punitiveMeasure.setPunitiveMeasure(rs.getString(2).trim());
		// punitiveMeasure.setMsPassword(rs.getString(3));
		return punitiveMeasure;
	}

	public List<PunitiveMeasure> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
