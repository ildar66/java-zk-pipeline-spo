package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.DepositorFinStatus;
import com.vtb.exception.MappingException;

public class DepositorFinStatusMapper extends JDBCMapper<DepositorFinStatus> {
	private static final Logger LOGGER = Logger.getLogger(DepositorFinStatusMapper.class.getName());

	@Override
	protected DepositorFinStatus createImpl(Connection conn,
			DepositorFinStatus domainObject) throws SQLException,
			MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected DepositorFinStatus findByPrimaryKeyImpl(Connection conn,
			DepositorFinStatus domainObjectWithKeyValues) throws SQLException,
			MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void removeImpl(Connection conn, DepositorFinStatus domainObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	@Override
	protected void updateImpl(Connection conn, DepositorFinStatus anObject)
			throws SQLException, MappingException {
		throw new MappingException("Illegal operation for this object ");
	}

	public ArrayList<DepositorFinStatus> findAll() throws MappingException {
        String CMD_FIND="select id, status from  " 
        	+ " depositor_fin_status order by id";
        ArrayList<DepositorFinStatus> list = new ArrayList<DepositorFinStatus>();
        Connection conn = null;
        DepositorFinStatus vo = null;
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            ps = conn.prepareStatement(CMD_FIND);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vo = new DepositorFinStatus(rs.getLong("id"), 
                        rs.getString("status"));
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
