package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ru.masterdm.compendium.value.Page;

import com.vtb.domain.SpoOpportunity;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

/**
 * Mapper "Cлужит для связи заявки, признаков отправке/получения, статусов
 * возврата между СПО и CRM"
 * 
 * @author IShafigullin
 * 
 */
public class SpoOpportunityMapper {
	
	protected static final String _loadString = "SELECT FB_SPO_OPPORTUNITYID id, SPOSEND, SPOSENDDATE, SPOACCEPT, SPOACCEPTDATE, SPOTYPE, OPPORTUNITYID, ACCOUNTID, CALLBACK, CALLBACKDATE FROM "
		+ "sysdba.FB_SPO_OPPORTUNITY WHERE FB_SPO_OPPORTUNITYID = ?";

	protected static final String _createString = "INSERT INTO "
			+ "sysdba.FB_SPO_OPPORTUNITY (FB_SPO_OPPORTUNITYID, SPOSEND, SPOSENDDATE, SPOACCEPT, SPOACCEPTDATE, SPOTYPE, OPPORTUNITYID, ACCOUNTID, CALLBACK, CALLBACKDATE ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	protected static final String _removeString = "DELETE FROM FB_SPO_OPPORTUNITY  WHERE FB_SPO_OPPORTUNITYID = ?";

	protected static final String _storeString = "UPDATE "
			+ "sysdba.FB_SPO_OPPORTUNITY  SET SPOACCEPT = ?, SPOACCEPTDATE = ?, SPOTYPE = ?, CALLBACK = ?, CALLBACKDATE = ?  WHERE FB_SPO_OPPORTUNITYID = ?";

	private Connection connection = null;
	public SpoOpportunityMapper(Connection c) {
		super();
		this.connection=c;
	}

	protected Object createImpl(Connection conn, SpoOpportunity domainObject) throws SQLException, MappingException {
		String id = null;
		String spoSend = null;
		Date spoSendDate = null;
		String spoAccept = null;
		Timestamp spoAcceptDate = null;
		String spoType = null;
		String opportunityID = null;
		String accountID = null;
        String callBack = null;
        Date callBackDate = null;

        id = domainObject.getId();
        spoSend = domainObject.getSpoSend();
        spoSendDate = domainObject.getSpoSendDate();
        spoAccept = domainObject.getSpoAccept();
        spoAcceptDate = domainObject.getSpoAcceptDate();
        spoType = domainObject.getSpoType();
        opportunityID = domainObject.getOpportunityID();
        accountID = domainObject.getAccountID();
        callBackDate = domainObject.getCallBackDate();

        PreparedStatement ps = conn.prepareStatement(_createString);
        int i = 1;
        ps.setObject(i++, id);
		ps.setObject(i++, spoSend);
		ps.setObject(i++, spoSendDate);
		ps.setObject(i++, spoAccept);
		ps.setObject(i++, spoAcceptDate);
		ps.setObject(i++, spoType);
		ps.setObject(i++, opportunityID);
		ps.setObject(i++, accountID);
		ps.setObject(i++, callBack);
		ps.setObject(i++, callBackDate);

		int rows = ps.executeUpdate();
		if (rows == 1)
			return domainObject;
		else
			// failed
			throw new DuplicateKeyException("Create Failed " + domainObject);
	}

	protected SpoOpportunity findByPrimaryKeyImpl(Connection conn, SpoOpportunity domainObjectWithKeyValues) throws SQLException,
			MappingException {
		SpoOpportunity spoOpportunity = null;
		String aId = null;
		if (domainObjectWithKeyValues instanceof SpoOpportunity) {
			aId = ((SpoOpportunity) domainObjectWithKeyValues).getId();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, aId);
		ResultSet rs = ps.executeQuery();
		rs.next();
		spoOpportunity = activate(rs);
		return spoOpportunity;
	}

	protected void removeImpl(Connection conn, SpoOpportunity domainObject) throws SQLException, MappingException {
		String aId = null;
		if (domainObject instanceof SpoOpportunity) {
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

	protected void updateImpl(Connection conn, SpoOpportunity domainObject) throws SQLException, MappingException {
		String id = null;
		String spoAccept = null;
		Timestamp spoAcceptDate = null;
		String spoType = null;
		String callBack = null;
        Date callBackDate = null;

        id = domainObject.getId();
        spoAccept = domainObject.getSpoAccept();
        spoAcceptDate = domainObject.getSpoAcceptDate();
        spoType = domainObject.getSpoType();
        callBackDate = domainObject.getCallBackDate();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        int i = 1;
        ps.setObject(i++, spoAccept);
		ps.setObject(i++, spoAcceptDate);
		ps.setObject(i++, spoType);
		ps.setObject(i++, callBack);
		ps.setObject(i++, callBackDate);
		ps.setObject(i++, id);
		int rows = ps.executeUpdate();
		if (rows == 1)
			return;
		else
			// failed
			throw new MappingException("Update Failed " + domainObject);
	}

	@SuppressWarnings("unchecked")
	public Page findByFilter(String orderBy,int start,int count) throws MappingException {
        ArrayList<SpoOpportunity> list = new ArrayList<SpoOpportunity>();
		SpoOpportunity spoOpportunity = null;
		StringBuffer sb = new StringBuffer(" FROM "
				+ "sysdba.FB_SPO_OPPORTUNITY o " +
				" inner join sysdba.V_SPO_OPPORTUNITY opp on opp.OPPORTUNITYID=o.opportunityid "+
				" WHERE SPOSEND = 1 ");
		try {
			int totalCount=0;
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				sb.append(" order by " + orderBy);
			}
			ps = connection.prepareStatement("select count(FB_SPO_OPPORTUNITYID) "+sb.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				totalCount=rs.getInt(1);
			}
			ps.close();
			rs.close();
			if(totalCount==0) return Page.EMPTY_PAGE;
			
			if(count != 0){
				String query = "SELECT o.FB_SPO_OPPORTUNITYID id, o.SPOSEND, " +
					"o.SPOSENDDATE, o.SPOACCEPT, o.SPOACCEPTDATE, o.SPOTYPE, o.OPPORTUNITYID, " +
					"o.ACCOUNTID, o.CALLBACK, o.CALLBACKDATE"+sb.toString();
				query = "select * from ( select "+
					"a.*, ROWNUM rnum from ("+query+") a "+
					"where ROWNUM <= "+Integer.toString(start+count-1)+" ) where rnum  >= "+Integer.toString(start);
				ps = connection.prepareStatement(query);
				rs = ps.executeQuery();
				while (rs.next()) {
					spoOpportunity = activate(rs);
					list.add(spoOpportunity);
				}
				ps.close();
				rs.close();
			}
			Page returnPage = new Page(list, start, (start + list.size()) < totalCount);
			returnPage.setTotalCount(totalCount);
			return returnPage;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
		} finally {
		}
	}

	protected SpoOpportunity activate(ResultSet rs) throws SQLException {
		int i = 1;
		SpoOpportunity spoOpportunity = new SpoOpportunity(rs.getString(i++));
		spoOpportunity.setSpoSend(rs.getString(i++));
		spoOpportunity.setSpoSendDate(convertTimestampToDate(rs.getTimestamp(i++)));
		spoOpportunity.setSpoAccept(rs.getString(i++));
		spoOpportunity.setSpoAcceptDate(rs.getTimestamp(i++));
		spoOpportunity.setSpoType(rs.getString(i++));
		spoOpportunity.setOpportunityID(rs.getString(i++));
		spoOpportunity.setAccountID(rs.getString(i++));
		spoOpportunity.setCallBack(rs.getString(i++));
		spoOpportunity.setCallBackDate(convertTimestampToDate(rs.getTimestamp(i++)));
		return spoOpportunity;
	}

	private Date convertTimestampToDate(Timestamp t){
	    if (t==null) return null;
	    return new Date(t.getTime());
	}
	public List<SpoOpportunity> findAll() throws MappingException {
		return null;
	}

}
