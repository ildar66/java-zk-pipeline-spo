/*
 * Created on 24.07.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.custom.OperatorToRoleTO;
import com.vtb.domain.Role;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchRoleException;

/**
 * @author IShafigullin
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RoleMapper extends JDBCMapper<Role> implements com.vtb.mapping.RoleMapper {

	protected static final String findByNameSqlString = "SELECT ID_ROLE id, NAME_ROLE name, ID_TYPE_PROCESS, ACTIVE FROM "
			+ " roles WHERE LOWER(NAME_ROLE) matches LOWER(?)";

	protected static final String _loadString = "SELECT ID_ROLE id, NAME_ROLE name, ID_TYPE_PROCESS, ACTIVE FROM roles ";

	private static final String deleteLink_SQL = "DELETE FROM USER_IN_ROLE WHERE ID_ROLE = ? AND ID_USER in (select id_operator from operator where login= ?) ";

	private static final String addLink_SQL = "INSERT INTO USER_IN_ROLE(ID_ROLE, ID_USER) select ? as ID_ROLE,id_operator as ID_USER from operator where login=? ";

	private static final String setStatusLink_SQL = "UPDATE USER_IN_ROLE SET STATUS = ? WHERE ID_ROLE = ? AND ID_USER in (select id_operator from operator where login= ?)";

	private static final String _setAllStatusLink = "UPDATE USER_IN_ROLE SET STATUS = ? WHERE ID_USER in (select id_operator from operator where login= ?)";
	
	/**
	 * 
	 */
	public RoleMapper() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.jdbc.JDBCMapper#createImpl(java.sql.Connection,
	 *      com.vtb.domain.VtbObject)
	 */
	protected Role createImpl(Connection conn, Role domainObject) throws SQLException, MappingException {
		throw new MappingException("createImpl not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.jdbc.JDBCMapper#findByPrimaryKeyImpl(java.sql.Connection,
	 *      com.vtb.domain.Role)
	 */
	protected Role findByPrimaryKeyImpl(Connection conn, Role domainObjectWithKeyValues) throws SQLException,
			MappingException {
		throw new MappingException("findByPrimaryKeyImpl not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.jdbc.JDBCMapper#removeImpl(java.sql.Connection,
	 *      com.vtb.domain.Role)
	 */
	protected void removeImpl(Connection conn, Role domainObject) throws SQLException, MappingException {
		throw new MappingException("removeImpl not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.jdbc.JDBCMapper#updateImpl(java.sql.Connection,
	 *      com.vtb.domain.Role)
	 */
	protected void updateImpl(Connection conn, Role anObject) throws SQLException, MappingException {
		throw new MappingException("updateImpl not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#findById(java.lang.String)
	 */
	public Role findById(String roleId) throws NoSuchRoleException {
		throw new NoSuchRoleException("findById not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.Mapper#findAll()
	 */
	public ArrayList<Role> findAll() throws MappingException {
		throw new MappingException("findAll not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#findByName(java.lang.String)
	 */
	public ArrayList<Role> findByName(String roleName) throws MappingException {
		throw new MappingException("findByName not valid for this type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#findByName(java.lang.String,
	 *      java.lang.String)
	 */
	public ArrayList<Role> findByName(String roleName, String orderBy) throws MappingException {
		ArrayList<Role> list = new ArrayList<Role>();
		Role role = null;
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				ps = conn.prepareStatement(findByNameSqlString + " order by " + orderBy);
			} else {
				ps = conn.prepareStatement(findByNameSqlString);
			}
			ps.setString(1, roleName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				role = activate(rs);
				list.add(role);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected Role activate(ResultSet rs) throws SQLException {
		Role role = new Role(((BigDecimal) rs.getObject(1)).intValue(), rs.getString(2).trim());
		role.setProcessTypeID(((BigDecimal) rs.getObject(3)).intValue());
		// role.setComment((rs.getString(3) != null) ? rs.getString(3).trim() :
		// "");
		// role.setSortOrder((Integer) rs.getObject(4));
		// role.setTaskComment((rs.getString(5) != null) ?
		// rs.getString(5).trim() : "");
		return role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#findOperatorToRoleAccessList(java.lang.Integer,
	 *      java.lang.String)
	 */
	public ArrayList<OperatorToRoleTO> findOperatorToRoleAccessList(String operatorKey, String orderBy) throws MappingException {
		ArrayList<OperatorToRoleTO> ret = new ArrayList<OperatorToRoleTO>();
		OperatorToRoleTO roleTO = null;
		Connection conn = null;
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer(
					"SELECT roles.ID_ROLE id, roles.NAME_ROLE name, roles.ID_TYPE_PROCESS, roles.ACTIVE, type_process.DESCRIPTION_PROCESS FROM roles, type_process ");
			sb
					.append(" WHERE roles.ID_TYPE_PROCESS = type_process.ID_TYPE_PROCESS AND roles.ACTIVE = 1 AND ID_ROLE NOT IN (SELECT ID_ROLE FROM USER_IN_ROLE WHERE ID_USER = ?)");
			// append order by clause:
			if (orderBy != null && !orderBy.equals(""))
				sb.append(" order by " + orderBy);
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.setObject(1, operatorKey);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				roleTO = new OperatorToRoleTO(activate(rs));
				roleTO.setProcessName(rs.getString(5));
				ret.add(roleTO);
			}
			return ret;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findOperatorToRoleAccessList code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#findOperatorToRoleList(java.lang.Integer,
	 *      java.lang.String)
	 */
	public ArrayList<OperatorToRoleTO> findOperatorToRoleList(String operatorKey, String orderBy) throws MappingException {
		ArrayList<OperatorToRoleTO> ret = new ArrayList<OperatorToRoleTO>();
		OperatorToRoleTO roleTO = null;
		Connection conn = null;
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer(
					"SELECT roles.ID_ROLE id, roles.NAME_ROLE name, roles.ID_TYPE_PROCESS, roles.ACTIVE, user_in_role.STATUS, type_process.DESCRIPTION_PROCESS FROM roles, user_in_role, type_process,operator ");
			sb
					.append(" WHERE roles.ID_ROLE = user_in_role.ID_ROLE AND roles.ID_TYPE_PROCESS = type_process.ID_TYPE_PROCESS AND user_in_role.ID_USER = operator.id_operator AND operator.login= ? ");
			// append order by clause:
			if (orderBy != null && !orderBy.equals(""))
				sb.append(" order by " + orderBy);
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.setObject(1, operatorKey);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				roleTO = new OperatorToRoleTO(activate(rs));
				roleTO.setStatus(rs.getString(5));
				roleTO.setProcessName(rs.getString(6));
				ret.add(roleTO);
			}
			return ret;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findOperatorToRoleList code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#addLinkOperatorToRole(java.lang.Integer,
	 *      String)
	 */
	public void addLinkOperatorToRole(String operatorKey, String roleKey) throws MappingException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(addLink_SQL);
			st.setString(1, roleKey);
			st.setString(2, operatorKey);
			if (st.executeUpdate() != 1) {
				String err = "addLinkOperatorToRole.operatorKey=" + operatorKey + " failed";
				throw new MappingException(err);
			}
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			if (se.getErrorCode() == -268) {
				throw new MappingException(se, "Данная связь присутствует!");
			}
			throw new MappingException(se, "SQLException addLinkOperatorToRole code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#deleteLinkOperatorToRole(java.lang.Integer,
	 *      String)
	 */
	public void deleteLinkOperatorToRole(String operatorKey, String roleKey) throws MappingException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(deleteLink_SQL);
			st.setString(1, roleKey);
			st.setString(2, operatorKey);
			if (st.executeUpdate() != 1) {
				String err = "deleteLinkOperatorToRole.operatorKey=" + operatorKey + " failed";
				throw new MappingException(err);
			}
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException deleteLinkOperatorToRole code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	public void setStatusLinkOperatorToAllRoles(String operatorKey, String status) throws MappingException {
		Connection conn = null;
		PreparedStatement st = null;
		try{
			conn = getConnection();
			st = conn.prepareStatement(_setAllStatusLink);
			st.setString(1, status);
			st.setString(2, operatorKey);
			
			if (st.executeUpdate() < 0) {
				throw new MappingException("setStatusLinkOperatorToAllRoles failed. key = " + operatorKey);
			}
		} catch(Exception e){
			e.printStackTrace();
			throw new MappingException(e, "SQLException: " + e.getMessage());
		} finally {
			close(conn);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vtb.mapping.RoleMapper#setStatusLinkOperatorToRole(java.lang.Integer,
	 *      String, String status)
	 */
	public void setStatusLinkOperatorToRole(String operatorKey, String roleKey, String status) throws MappingException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(setStatusLink_SQL);
			st.setString(1, status);
			st.setString(2, roleKey);
			st.setString(3, operatorKey);

			if (st.executeUpdate() != 1) {
				String err = "setStatusLinkOperatorToRole.operatorKey=" + operatorKey + " failed";
				throw new MappingException(err);
			}
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException setStatusLinkOperatorToRole code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	public ArrayList<OperatorToRoleTO> findOperatorToRoleAccessList(String operatorKey, String aProcessID, String orderBy)
			throws MappingException {
		ArrayList<OperatorToRoleTO> ret = new ArrayList<OperatorToRoleTO>();
		OperatorToRoleTO roleTO = null;
		Connection conn = null;
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer(
					"SELECT roles.ID_ROLE id, roles.NAME_ROLE name, roles.ID_TYPE_PROCESS, roles.ACTIVE, type_process.DESCRIPTION_PROCESS ");
			sb.append("FROM roles, type_process ");
			sb.append("WHERE roles.ID_TYPE_PROCESS = type_process.ID_TYPE_PROCESS AND roles.ACTIVE = 1 ");
			sb.append("AND ID_ROLE NOT IN (SELECT ID_ROLE FROM USER_IN_ROLE, operator WHERE operator.id_operator=USER_IN_ROLE.id_user and operator.login= ?) ");
			// append processID clause:
			if (aProcessID != null && !aProcessID.trim().equalsIgnoreCase("ALL"))
				sb.append(" AND type_process.ID_TYPE_PROCESS = ? ");			
			// append order by clause:
			if (orderBy != null && !orderBy.equals(""))
				sb.append(" order by " + orderBy);
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.setObject(1, operatorKey);
			if (aProcessID != null && !aProcessID.trim().equalsIgnoreCase("ALL")){
				ps.setObject(2, aProcessID);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				roleTO = new OperatorToRoleTO(activate(rs));
				roleTO.setProcessName(rs.getString(5));
				ret.add(roleTO);
			}
			return ret;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findOperatorToRoleAccessList code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}
}
