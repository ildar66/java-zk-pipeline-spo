package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.Report;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.util.Converter;

public class ReportMapper extends JDBCMapper<Report> implements com.vtb.mapping.ReportMapper {
	protected static final String findByNameSqlString = "SELECT reportID id, report_type type, description, rptdesign design, rptconfig config FROM "
			 + " REPORT WHERE LOWER(DESCRIPTION) like LOWER(?)";

	protected static final String _loadString = "SELECT reportID id, report_type type, description, rptdesign design, rptconfig config FROM "
			 + " REPORT WHERE reportID = ?";

	protected static final String _createString = "INSERT INTO " 
			+ " REPORT (reportID, report_type, description, rptDesign, rptConfig) VALUES (?, ?, ?, ?, ?)";

	protected static final String _removeString = "DELETE FROM " 
			+ " REPORT  WHERE reportID = ?";

	protected static final String _storeString = "UPDATE " 
			+ " REPORT  SET report_type = ?, description = ?, rptDesign = ?, rptConfig = ? WHERE reportID = ?";

	private static final String deleteLinkToProcess_SQL = "DELETE FROM R_REPORT_PTYPE WHERE REPORT_ID = ? ";
	private static final String addLinkToProcess_SQL = "INSERT INTO R_REPORT_PTYPE(REPORT_ID, TYPE_PROCESS_ID) VALUES (?,?) ";
	private static final String getProcess_SQL = "SELECT TYPE_PROCESS_ID FROM R_REPORT_PTYPE WHERE REPORT_ID = ? ";
	private static final String getProcesNames_SQL = "SELECT p.DESCRIPTION_PROCESS FROM R_REPORT_PTYPE r, TYPE_PROCESS p WHERE r.TYPE_PROCESS_ID = p.ID_TYPE_PROCESS AND r.REPORT_ID = ? ";

	@Override
	protected Report createImpl(Connection conn, Report domainObject) throws SQLException, MappingException {
		Integer id = null;
		String type = null;
		String description = null;
		String design = null;
        String config = null;
        id = domainObject.getId();
        type = domainObject.getType();
        description = domainObject.getDescription();
        design = domainObject.getDesign();
        config = domainObject.getConfig();
        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, type);
        ps.setObject(3, description);
		ps.setObject(4, design);
		ps.setObject(5, config);
		int rows = ps.executeUpdate();
		if (rows == 1) {
			addLinkToProcess(conn, ps, id, domainObject.getProcessIDs());
			return domainObject;
		} else
			// failed
			throw new DuplicateKeyException("Create Failed " + domainObject);
	}

	@Override
	protected Report findByPrimaryKeyImpl(Connection conn, Report domainObjectWithKeyValues) throws SQLException,
			MappingException {
		Report vo = null;
		Integer id = domainObjectWithKeyValues.getId();
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			vo = activate(rs);
			vo.setProcessIDs(getProcess(conn, ps, id));
		}
		return vo;
	}

	@Override
	protected void removeImpl(Connection conn, Report domainObject) throws SQLException, MappingException {
		Integer aId = null;
		if (domainObject instanceof Report) {
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
	protected void updateImpl(Connection conn, Report domainObject) throws SQLException, MappingException {
		Integer id = null;
		String type = null;
		String description = null;
		String design = null;
		String config = null;
		if (domainObject instanceof Report) {
			id = domainObject.getId();
			type = domainObject.getType();
			description = domainObject.getDescription();
			design = domainObject.getDesign();
			config = domainObject.getConfig();
		} else {
			// update fails
			throw new MappingException("Update Failed " + domainObject);
		}
		PreparedStatement ps = conn.prepareStatement(_storeString);
		ps.setObject(1, type);
		ps.setObject(2, description);
		ps.setObject(3, design);
		ps.setObject(4, config);

		ps.setObject(5, id);
		int rows = ps.executeUpdate();
		if (rows == 1) {
			deleteLinkToProcess(conn, ps, id);
			addLinkToProcess(conn, ps, id, domainObject.getProcessIDs());
			return;
		} else
			// failed
			throw new MappingException("Update Failed " + domainObject);
	}

	public ArrayList<Report> findByName(String description, String orderBy) throws MappingException {
		final ArrayList<Report> list = new ArrayList<Report>();
		Report vo = null;
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
				vo.setProcessNames(getProcesNames(conn, ps, vo.getId()));
				list.add(vo);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName type=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected Report activate(ResultSet rs) throws SQLException {
		Report vo = new Report(((BigDecimal) rs.getObject(1)).intValue(), rs.getString(2));
		vo.setDescription(rs.getString(3));
		vo.setDesign(rs.getString(4));
		vo.setConfig(rs.getString(5));
		return vo;
	}

	public ArrayList<Report> findAll() throws MappingException {
		return (ArrayList<Report>) findAllObjects();
	}

	/**
	 * @param conn
	 * @param st
	 * @param reportKey
	 * @param processKey
	 * @throws MappingException
	 */
	private void addLinkToProcess(Connection conn, PreparedStatement st, Integer reportKey, Integer[] processKeys)
			throws MappingException {
		try {
			if (processKeys == null) {
				return;
			}
			st = conn.prepareStatement(addLinkToProcess_SQL);
			for (Integer i : processKeys) {
				st.setObject(1, reportKey);
				st.setObject(2, i);
				if (st.executeUpdate() != 1) {
					String err = "addLinkToProcess_SQL.reportKey=" + reportKey + " failed";
					throw new MappingException(err);
				}
			}
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			if (se.getErrorCode() == -268) {
				throw new MappingException(se, "Данная связь присутствует!");
			}
			throw new MappingException(se, "SQLException addLinkToProcess code=" + se.getErrorCode());
		}
	}

	/**
	 * @param conn
	 * @param st
	 * @param reportKey
	 * @param processKey
	 * @throws MappingException
	 */
	private void deleteLinkToProcess(Connection conn, PreparedStatement st, Integer reportKey) throws MappingException {
		try {
			st = conn.prepareStatement(deleteLinkToProcess_SQL);
			st.setObject(1, reportKey);
			st.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException deleteLinkToProcess code=" + se.getErrorCode());
		}
	}

	private Integer[] getProcess(Connection conn, PreparedStatement st, Integer reportKey) throws MappingException {
		ArrayList<Integer> processKeys = new ArrayList<Integer>();
		try {
			st = conn.prepareStatement(getProcess_SQL);
			st.setObject(1, reportKey);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Integer processKey = Converter.toInteger((BigDecimal) rs.getObject(1));
				processKeys.add(processKey);
			}
			return (Integer[]) processKeys.toArray(new Integer[processKeys.size()]);
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException getProcess code=" + se.getErrorCode());
		}
	}

	private String getProcesNames(Connection conn, PreparedStatement st, Integer reportKey) throws MappingException {
		StringBuffer processNames = new StringBuffer();
		try {
			st = conn.prepareStatement(getProcesNames_SQL);
			st.setObject(1, reportKey);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (!processNames.toString().equals("")) {
					processNames.append(", ");
				}
				String processName = rs.getString(1);
				processNames.append(processName);
			}
			return processNames.toString();
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException getProcesNames code=" + se.getErrorCode());
		}
	}
}
