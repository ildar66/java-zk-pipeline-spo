package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.SpoHistory;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
/**
 * JDBCMapper "История прохождения заявки".
 * В данную таблицу система СПО записывает информацию о прохождении заявки
 * 
 * @author IShafigullin
 * 
 */
public class SpoHistoryMapper extends JDBCMapperCRM<SpoHistory> implements com.vtb.mapping.SpoHistoryMapper {
	
	protected static final String findByNameSqlString = "SELECT FB_SPO_HISTORYID id, SPOEXPERT spoExpert, FB_SPO_OPPORTUNITYID spoOpportunityID, SPOSTEP spostep, SPOSTAGE spostage, STEPCHDATE stepchDate FROM "
		+ "sysdba.FB_SPO_HISTORY WHERE LOWER(SPOEXPERT) like LOWER(?)";
	
	protected static final String _loadString = "SELECT FB_SPO_HISTORYID id, SPOEXPERT spoExpert, FB_SPO_OPPORTUNITYID spoOpportunityID, SPOSTEP spostep, SPOSTAGE spostage, STEPCHDATE stepchDate FROM "
		+ "sysdba.FB_SPO_HISTORY WHERE FB_SPO_HISTORYID = ?";

	protected static final String _createString = "INSERT INTO "
			+ "sysdba.FB_SPO_HISTORY (FB_SPO_HISTORYID, SPOEXPERT, FB_SPO_OPPORTUNITYID, SPOSTEP, STEPCHDATE, SPOSTAGE, SPOSTEPEXPERT) VALUES (?, ?, ?, ?, ?, ?, null) ";

	protected static final String _removeString = "DELETE FROM "
			+ "sysdba.FB_SPO_HISTORY  WHERE FB_SPO_HISTORYID = ?";

	protected static final String _storeString = "UPDATE "
			+ "sysdba.FB_SPO_HISTORY  SET SPOEXPERT = ?, FB_SPO_OPPORTUNITYID = ?, SPOSTEP = ?, STEPCHDATE = ?, SPOSTAGE = ? WHERE FB_SPO_HISTORYID = ?";

	@Override
	protected Object createImpl(Connection conn, SpoHistory domainObject)
			throws SQLException, MappingException {
		String id = null;
		String spoExpert = null;
		String spoOpportunityID = null;
		String spoStep = null;
		String spoStage = null;
		Timestamp stepchDate = null;
		if (domainObject instanceof SpoHistory) {
			id = domainObject.getId();
			spoExpert = domainObject.getSpoExpert();
			spoOpportunityID = domainObject.getSpoOpportunityID();
			spoStep = domainObject.getSpoStep();
			spoStage = domainObject.getSpoStage();
			stepchDate = domainObject.getStepchDate();
		} else {
			// update fails
			throw new DuplicateKeyException("Create Failed " + domainObject);
		}
		PreparedStatement ps = conn.prepareStatement(_createString);
		int i = 1;
		ps.setObject(i++, id);
		ps.setObject(i++, spoExpert);
		ps.setObject(i++, spoOpportunityID);
		ps.setObject(i++, spoStep);
		ps.setTimestamp(i++, stepchDate);
		ps.setObject(i++, spoStage);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return domainObject;
		else
			// failed
			throw new DuplicateKeyException("Create Failed; sql = " + ps.toString() + " " + domainObject);
	}

	@Override
	protected SpoHistory findByPrimaryKeyImpl(Connection conn,
			SpoHistory domainObjectWithKeyValues) throws SQLException,
			MappingException {
		SpoHistory domainObject = null;
		String aId = null;
		if (domainObjectWithKeyValues instanceof SpoHistory) {
			aId = ((SpoHistory) domainObjectWithKeyValues).getId();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, aId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			domainObject = activate(rs);
		}
		return domainObject;
	}

	@Override
	protected void removeImpl(Connection conn, SpoHistory domainObject)
			throws SQLException, MappingException {
		String aId = null;
		if (domainObject instanceof SpoHistory) {
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
	protected void updateImpl(Connection conn, SpoHistory domainObject)
			throws SQLException, MappingException {
		String id = null;
		String spoExpert = null;
		String spoOpportunityID = null;
		String spoStep = null;
		String spoStage = null;
		Timestamp stepchDate = null;
        id = domainObject.getId();
        spoExpert = domainObject.getSpoExpert();
        spoOpportunityID = domainObject.getSpoOpportunityID();
        spoStep = domainObject.getSpoStep();
        stepchDate = domainObject.getStepchDate();
        spoStage = domainObject.getSpoStage();
        PreparedStatement ps = conn.prepareStatement(_storeString);
		int i = 1;
		ps.setObject(i++, spoExpert);
		ps.setObject(i++, spoOpportunityID);
		ps.setObject(i++, spoStep);
		ps.setObject(i++, stepchDate);
		ps.setObject(i++, spoStage);
		ps.setObject(i++, id);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return;
		else
			// failed
			throw new MappingException("Update Failed " + domainObject);
	}

	public ArrayList<SpoHistory> findByName(String spoExpert, String orderBy)
			throws MappingException {
        ArrayList<SpoHistory> list = new ArrayList<SpoHistory>();
		SpoHistory domainObject = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer(findByNameSqlString);
		try {
			conn = getConnection();
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				sb.append(" order by " + orderBy);
			}
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, spoExpert);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				domainObject = activate(rs);
				list.add(domainObject);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName spoExpert="
					+ se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	protected SpoHistory activate(ResultSet rs) throws SQLException {
		int i = 1;
		SpoHistory domainObject = new SpoHistory(rs.getString(i++), rs.getString(i++), rs.getString(i++), rs.getString(i++), rs.getString(i++), rs.getTimestamp(i++));
		return domainObject;
	}

	public List<SpoHistory> findAll() throws MappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
