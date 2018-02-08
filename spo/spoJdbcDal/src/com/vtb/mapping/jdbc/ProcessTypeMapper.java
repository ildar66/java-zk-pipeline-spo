/**
 * 
 */
package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.ProcessType;
import com.vtb.exception.MappingException;

/**
 * @author Admin
 *
 */
public class ProcessTypeMapper extends JDBCMapper<ProcessType> implements com.vtb.mapping.ProcessTypeMapper {
	
	protected static final String _findAllProcesTypes = "select p.Id_Type_Process, p.description_process from TYPE_PROCESS p ";
	protected final String findProcesTypesById = "select p.Id_Type_Process, p.description_process from TYPE_PROCESS p" 
		+ " where p.ID_TYPE_PROCESS = ? ";
	protected static final String findByNameSqlString = "SELECT p.ID_TYPE_PROCESS id, p.DESCRIPTION_PROCESS description FROM "
		+ " TYPE_PROCESS p WHERE LOWER(p.DESCRIPTION_PROCESS) like LOWER(?)";
	/* (non-Javadoc)
	 * @see com.vtb.mapping.jdbc.JDBCMapper#createImpl(java.sql.Connection, com.vtb.domain.VtbObject)
	 */
	@Override
	protected ProcessType createImpl(Connection conn, ProcessType domainObject) throws SQLException, MappingException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vtb.mapping.jdbc.JDBCMapper#findByPrimaryKeyImpl(java.sql.Connection, com.vtb.domain.ProcessType)
	 */
	@Override
	protected ProcessType findByPrimaryKeyImpl(Connection conn, ProcessType domainObjectWithKeyValues) throws SQLException, MappingException {
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement(findProcesTypesById);
			ps.setLong(1, domainObjectWithKeyValues.getId().longValue());
			ResultSet rs = ps.executeQuery();
			// one record only 
			if (rs.next()){
				ProcessType pType = new ProcessType(rs.getInt("Id_Type_Process"));
				pType.setDescription(rs.getString("description_process"));
				return pType;
			} else 
				throw new MappingException ("Can't get right data in ProcessTypeMapper.findByPrimaryKeyImpl");			
		}catch(Exception e){
			throw new MappingException(e, e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.vtb.mapping.jdbc.JDBCMapper#removeImpl(java.sql.Connection, com.vtb.domain.ProcessType)
	 */
	@Override
	protected void removeImpl(Connection conn, ProcessType domainObject) throws SQLException, MappingException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.vtb.mapping.jdbc.JDBCMapper#updateImpl(java.sql.Connection, com.vtb.domain.ProcessType)
	 */
	@Override
	protected void updateImpl(Connection conn, ProcessType anObject) throws SQLException, MappingException {
		// TODO Auto-generated method stub

	}

	public ArrayList<ProcessType> getProcessTypes() throws MappingException{
		ArrayList<ProcessType> result = new ArrayList<ProcessType>(); 
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = getConnection();
			ps = conn.prepareStatement(_findAllProcesTypes);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				ProcessType pType = new ProcessType(rs.getInt("Id_Type_Process"));
				pType.setDescription(rs.getString("description_process"));
				result.add(pType);
			}
		}catch(Exception e){
			throw new MappingException(e, e.getMessage());
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.vtb.mapping.ProcessTypeMapper#findByName(java.lang.String, java.lang.String)
	 */
	public ArrayList<ProcessType> findByName(String name, String orderBy) throws MappingException {
		ArrayList<ProcessType> list = new ArrayList<ProcessType>();
		ProcessType vo = null;
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
			//System.out.println("sql = " + sb.toString());
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

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected ProcessType activate(ResultSet rs) throws SQLException {
		ProcessType vo = new ProcessType(((java.math.BigDecimal) rs.getObject(1)).intValue(), rs.getString(2).trim());
		// ownershipFormType.setIsActive(("Y".equals(rs.getString(5)) ?
		// Boolean.TRUE : Boolean.FALSE));
		return vo;
	}
	
	/* (non-Javadoc)
	 * @see com.vtb.mapping.Mapper#findAll()
	 */
	public ArrayList<ProcessType> findAll() throws MappingException {
		return getProcessTypes();
	}
}
