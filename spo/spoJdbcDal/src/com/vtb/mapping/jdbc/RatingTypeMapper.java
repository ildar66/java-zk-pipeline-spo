package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.RatingType;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class RatingTypeMapper extends JDBCMapper<RatingType> implements com.vtb.mapping.RatingTypeMapper {
	protected static final String findByNameSqlString = "SELECT ID_RATING_TYPE id, NAME_RATING_TYPE name FROM "
			 + " RATING_TYPE WHERE LOWER(NAME_RATING_TYPE) like LOWER(?)";

	protected static final String _loadString = "SELECT ID_RATING_TYPE id, NAME_RATING_TYPE name FROM "
			 + " RATING_TYPE WHERE ID_RATING_TYPE = ?";

	protected static final String _createString = "INSERT INTO " 
			+ " RATING_TYPE (ID_RATING_TYPE, NAME_RATING_TYPE) VALUES (?, ?)";

	protected static final String _removeString = "DELETE FROM " 
			+ " RATING_TYPE  WHERE ID_RATING_TYPE = ?";

	protected static final String _storeString = "UPDATE " 
			+ " RATING_TYPE  SET NAME_RATING_TYPE = ? WHERE ID_RATING_TYPE = ?";

	@Override
	protected RatingType createImpl(Connection conn, RatingType domainObject) throws SQLException, MappingException {
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
	protected RatingType findByPrimaryKeyImpl(Connection conn, RatingType domainObjectWithKeyValues) throws SQLException,
			MappingException {
		RatingType ratingType = null;
		Integer ratingTypeId = domainObjectWithKeyValues.getId();
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, ratingTypeId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			ratingType = activate(rs);
		}
		return ratingType;
	}

	@Override
	protected void removeImpl(Connection conn, RatingType domainObject) throws SQLException, MappingException {
		Integer aId = null;
		if (domainObject instanceof RatingType) {
			aId = domainObject.getId();
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
	protected void updateImpl(Connection conn, RatingType domainObject) throws SQLException, MappingException {
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

	public ArrayList<RatingType> findByName(String name, String orderBy) throws MappingException {
		ArrayList<RatingType> list = new ArrayList<RatingType>();
		RatingType ratingType = null;
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
				ratingType = activate(rs);
				list.add(ratingType);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected RatingType activate(ResultSet rs) throws SQLException {
		RatingType ratingType = new RatingType(((BigDecimal) rs.getObject(1)).intValue(), rs.getString(2));
		// ratingType.setRatingType(rs.getString(2).trim());
		// ratingType.setMsPassword(rs.getString(3));
		return ratingType;
	}

	public ArrayList<RatingType> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
