package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.LiquidityLevel;
import com.vtb.exception.MappingException;

public class LiquidityLevelMapper extends JDBCMapper<LiquidityLevel> {
	private static final Logger LOGGER = Logger.getLogger(LiquidityLevelMapper.class.getName());
	
	@Override
	protected LiquidityLevel createImpl(Connection conn,
			LiquidityLevel domainObject) throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected LiquidityLevel findByPrimaryKeyImpl(Connection conn,
			LiquidityLevel domainObjectWithKeyValues) throws SQLException,
			MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void removeImpl(Connection conn, LiquidityLevel domainObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void updateImpl(Connection conn, LiquidityLevel anObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	public ArrayList<LiquidityLevel> findAll() throws MappingException {
		String CMD_FIND="select id, name from liquidity_level order by id";
        ArrayList<LiquidityLevel> list = new ArrayList<LiquidityLevel>();
        Connection conn = null;
        LiquidityLevel vo = null;
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            ps = conn.prepareStatement(CMD_FIND);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vo = new LiquidityLevel(rs.getLong("id"), 
                        rs.getString("name"));
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
