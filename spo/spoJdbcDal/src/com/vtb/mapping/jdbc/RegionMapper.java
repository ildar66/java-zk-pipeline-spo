package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.Region;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class RegionMapper extends JDBCMapper<Region> implements com.vtb.mapping.RegionMapper {
	protected static final String findByNameSqlString = "SELECT ID_REGION id, NAME_REGION name, REGION_RATING rating FROM "
			+ " REGIONS WHERE LOWER(NAME_REGION) like LOWER(?)";

	protected static final String _loadString = "SELECT ID_REGION id, NAME_REGION name, REGION_RATING rating FROM "
			+ " REGIONS WHERE ID_REGION = ?";

	protected static final String _createString = "INSERT INTO "
			+ " REGIONS (ID_REGION, NAME_REGION, REGION_RATING) VALUES (?, ?, ?)";

	protected static final String _removeString = "DELETE FROM "
			+ " REGIONS  WHERE ID_REGION = ?";

	protected static final String _storeString = "UPDATE "
			+ " REGIONS  SET NAME_REGION = ?, REGION_RATING = ? WHERE ID_REGION = ?";

	@Override
	protected Region createImpl(Connection conn, Region domainObject)
			throws SQLException, MappingException {
		Integer id = null;
		String name = null;
		String rating = null;
        id = domainObject.getId();
        name = domainObject.getName();
        rating = domainObject.getRating();
        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, name);
		ps.setObject(3, rating);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return domainObject;
		else
			// failed
			throw new DuplicateKeyException("Create Failed " + domainObject);
	}

	@Override
	protected Region findByPrimaryKeyImpl(Connection conn,
			Region domainObjectWithKeyValues) throws SQLException,
			MappingException {
		Region region = null;
		Integer regionId = null;
		if (domainObjectWithKeyValues instanceof Region) {
			regionId = ((Region) domainObjectWithKeyValues).getId();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, regionId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			region = activate(rs);
		}
		return region;
	}

	@Override
	protected void removeImpl(Connection conn, Region domainObject)
			throws SQLException, MappingException {
		Integer aId = null;
		if (domainObject instanceof Region) {
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
	protected void updateImpl(Connection conn, Region domainObject)
			throws SQLException, MappingException {
		Integer id = null;
		String name = null;
        String rating = null;

        id = domainObject.getId();
        name = domainObject.getName();
        rating = domainObject.getRating();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, name);
        ps.setObject(2, rating);
		ps.setObject(3, id);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return;
		else
			// failed
			throw new MappingException("Update Failed " + domainObject);
	}

	public ArrayList<Region> findByName(String name, String orderBy) throws MappingException {
		ArrayList<Region> list = new ArrayList<Region>();
		Region region = null;
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
				region = activate(rs);
				list.add(region);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code="
					+ se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected Region activate(ResultSet rs) throws SQLException {
		Region region = new Region(((BigDecimal) rs.getObject(1))
				.intValue(), rs.getString(2));
		region.setRating(rs.getString(3));
		// region.setMsPassword(rs.getString(3));
		return region;
	}

	public ArrayList<Region> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
