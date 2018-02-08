package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.SpoAccount;
import com.vtb.exception.MappingException;

public class SpoAccountMapper extends JDBCMapperCRM<SpoAccount> {
	private static final String _loadString = "SELECT acc.ACCOUNTID, acc.ACCOUNT, acc.INDUSTRY, acc.TERRITORY, acc.ADDRESSID, "
			+ " facc.OGRN, facc.OGRN_DATE , facc.OGRN_PLACE , facc.INN, facc.OKPO_CD, acc.REGION, facc.CORP_BLOCK, facc.CATEGORY "
			+ " FROM sysdba.V_SPO_ACCOUNT acc, " 
			+ " sysdba.V_SPO_FB_ACCOUNT facc "
			+ " WHERE acc.ACCOUNTID = facc.ACCOUNTID AND acc.ACCOUNTID = ? ";

	
	@Override
	protected Object createImpl(Connection conn, SpoAccount domainObject) throws SQLException, MappingException {
		throw new MappingException("Insert not valid for this type");
	}

	@Override
	protected SpoAccount findByPrimaryKeyImpl(Connection conn, SpoAccount domainObjectWithKeyValues) throws SQLException,
			MappingException {
		SpoAccount domainObject = null;
		String aId = null;
		if (domainObjectWithKeyValues instanceof SpoAccount) {
			aId = ((SpoAccount) domainObjectWithKeyValues).getAccountID();
		} else
			return null;
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, aId);
		ResultSet rs = ps.executeQuery();
		if (rs.next())
			domainObject = activate(conn, rs);
		
		return domainObject;
	}

	private SpoAccount activate(Connection conn, ResultSet rs)  throws SQLException, MappingException {
		int i = 1;
		SpoAccount domainObject = new SpoAccount(rs.getString(i++));
		domainObject.setAccount(rs.getString(i++));
		domainObject.setIndustry(rs.getString(i++));
		domainObject.setTerritory(rs.getString(i++));
		String addressId = rs.getString(i++);
		domainObject.setOgrn(rs.getString(i++));
		domainObject.setOgrnDate(rs.getDate(i++));
		domainObject.setOgrnPlace(rs.getString(i++));
		domainObject.setInn(rs.getString(i++));
		domainObject.setOkpo(rs.getString(i++));
		domainObject.setRegion(rs.getString(i++));
		domainObject.setCorp_block(rs.getString(i++));
		domainObject.setCategory(rs.getString(i++));
		return domainObject;
	}

	@Override
	protected void removeImpl(Connection conn, SpoAccount domainObject) throws SQLException, MappingException {
		throw new MappingException("Remote not valid for this type");
	}

	@Override
	protected void updateImpl(Connection conn, SpoAccount anObject) throws SQLException, MappingException {
		throw new MappingException("Update not valid for this type");
	}

	public ArrayList<SpoAccount> findAll() throws MappingException {
		throw new MappingException("findAll not valid for this type");
	}

}
