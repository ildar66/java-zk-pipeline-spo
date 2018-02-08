package com.vtb.mapping.jdbc;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.masterdm.compendium.value.Page;

import com.vtb.domain.CRMLimit;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.util.ApplProperties;

/**
 * Маппер лимита из CRM
 * @author Andrey Pavlenko
 *
 */
public class CRMLimitMapper extends JDBCMapperCRM<CRMLimit> {
	private static final Logger LOGGER = Logger.getLogger(CRMLimitMapper.class.getName());
	private final static String LIMIT_DATE_LIMIT = ApplProperties.getCrmLimitDateFrom();
	private final static String QUERY = 
	    "from sysdba.v_spo_fb_limit l "+
		"inner join sysdba.v_spo_userinfo ui on ui.USERID=l.MANAGERID where " +
		"l.CREATEDATE>To_Date('" +LIMIT_DATE_LIMIT+"','dd.mm.yyyy') and " +
		"l.STATUS='Открытый'";
	
	private final static String FIELDS =
	    " l.code,l.fb_limitid,l.createdate,ui.USERCODE,l.limit_name,l.summa,l.curr, "+
	    "l.islimit,l.limitid,l.limit_vid,l.status,l.currs, ui.lastname, ui.firstname ";
	
	private final static String LOAD_QUERY="select "+FIELDS+QUERY;
	private final static String COUNT_QUERY="select count(fb_limitid) "+QUERY;
	
	@Override
	protected Object createImpl(Connection conn, CRMLimit domainObject) throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	protected CRMLimit findByPrimaryKeyImpl(Connection conn, CRMLimit domainObjectWithKeyValues) throws SQLException, MappingException {
		CRMLimit limit = null;
		StringBuffer sb = new StringBuffer(LOAD_QUERY);
		sb.append(" and l.fb_limitid=?");
		try {
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, domainObjectWithKeyValues.getLimitid());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				limit = activate(rs);
			}
			rs.close();
			ps.close();
			loadOrganisation(conn.prepareStatement("select la.ACCOUNTID from sysdba.v_spo_fb_limit_account la where la.FB_LIMITID=?"), 
					limit);
			return limit;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByUser code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	@Override
	protected void removeImpl(Connection conn, CRMLimit domainObject) throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	protected void updateImpl(Connection conn, CRMLimit anObject) throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	public List<CRMLimit> findAll() throws MappingException {
		throw new MappingException("not implemented");
	}

	/**
	 * Список заявок для пользователя
	 * @param login - логины пользователей в нижнем регистре через запятую
	 * @param start - начиная с 
	 * @param count - количество
	 * @return страница лимитов
	 * @throws MappingException
	 */
	@SuppressWarnings("unchecked")
	public Page findLimitByUser(String login,int start,int count) throws MappingException {
		ArrayList<CRMLimit> list = new ArrayList<CRMLimit>();
		int totalCount=0;
		CRMLimit limit = null;
		Connection conn = null;
		try {
			conn = getConnection();
			//общее количество
			String query=COUNT_QUERY;
			if(login!=null)query += " and lower(ui.USERCODE) in ("+login+")";
			query += " and fb_limitid not in (select crmid from sysdba.fb_spo_log_status where status=1) ";
			query += " and limitid is null ";
			LOGGER.info(query);
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				totalCount=rs.getInt(1);
			}
			ps.close();
			rs.close();
			if(totalCount==0) return Page.EMPTY_PAGE;
			//нужная страница
			if (count != 0){
				StringBuffer sb = new StringBuffer(LOAD_QUERY);
				if (login!=null){
					sb.append(" and lower(ui.USERCODE) in ("+login+")");
				}
				sb.append(" and fb_limitid not in (select crmid from sysdba.fb_spo_log_status where status=1) ");
				sb.append(" and limitid is null ");
				sb.append(" order by code desc");
				query = "select * from ( select  "+
				"a.*, ROWNUM rnum from ("+sb.toString()+") a "+
				"where ROWNUM <= "+Integer.toString(start+count-1)+" ) where rnum  >= "+Integer.toString(start);
				LOGGER.info(query);
				ps = conn.prepareStatement(query);
				rs = ps.executeQuery();
				while (rs.next()) {
					limit = activate(rs);
					list.add(limit);
				}
				ps.close();
				rs.close();
				//грузим организации
				ps =conn.prepareStatement("select la.ACCOUNTID from sysdba.v_spo_fb_limit_account la where la.FB_LIMITID=?");
				for (CRMLimit l: list){
					loadOrganisation(ps, l);
				}
				ps.close();
			}
			Page returnPage = new Page(list, start, (start + list.size()) < totalCount);
			returnPage.setTotalCount(totalCount);
			return returnPage;
		} catch (SQLException se) {
			LOGGER.log(Level.SEVERE, se.getMessage(), se);
			se.printStackTrace();
			throw new MappingException(se, "SQLException findByUser code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}

	/**
	 * @param conn
	 * @param l
	 * @throws SQLException
	 */
	private void loadOrganisation(PreparedStatement ps, CRMLimit l)
			throws SQLException {
		ps.setString(1, l.getLimitid());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			l.getOrglist().add(rs.getString("ACCOUNTID"));
		}
		rs.close();
	}

	private CRMLimit activate(ResultSet rs) throws SQLException {
		CRMLimit limit;
		limit = new CRMLimit(rs.getString("fb_limitid"));
		limit.setCreateDate(rs.getDate("createdate"));
		limit.setManagerlogin(rs.getString("USERCODE"));
		limit.setLimitname(rs.getString("limit_name"));
		limit.setSum(rs.getBigDecimal("summa"));
		limit.setCurrencycode(rs.getString("curr"));
		limit.setCurrencylist(rs.getString("currs"));
		limit.setLimit_vid(rs.getString("limit_vid"));
		limit.setStatus(rs.getString("status"));
		limit.setCode(rs.getString("code"));
		limit.setParentlimitid(rs.getString("limitid"));
		limit.setUserName(rs.getString("lastname")+" "+rs.getString("firstname"));
		return limit;
	}
	/**
     * Ищет саблимиты в CRM.
     * @param limitid - номер лимита
     * @return список лимитов из CRM
     * @throws ModelException
     * @throws RemoteException
     */
	public ArrayList<CRMLimit> findCRMSubLimit(String limitid) throws MappingException {
		ArrayList<CRMLimit> list = new ArrayList<CRMLimit>();
		CRMLimit limit = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer(LOAD_QUERY);
		sb.append(" and l.limitid=?");
		//постраничный вывод
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.setString(1, limitid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				limit = activate(rs);
				list.add(limit);
			}
			//грузим организации
			ps =conn.prepareStatement("select la.ACCOUNTID from sysdba.v_spo_fb_limit_account la where la.FB_LIMITID=?");
			for (CRMLimit l: list){
				loadOrganisation(ps, l);
			}
			ps.close();
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByUser code=" + se.getErrorCode());
		} finally {
			close(conn);
		}
	}
	
	/**
	 * сохраняет результат загрузки в журнал
	 * @param crmid - номер лимита или сделки
	 * @param i - код успешности. 1 - успешно, 2 - ошибка
	 * @param message - сообщение
	 * @throws ModelException
	 */
	public void crmlog(String crmid, int i,String message) throws MappingException {
		try {
			Connection conn = getConnection();
			//очищаем старые записи
			PreparedStatement stmn = conn.prepareStatement("DELETE FROM sysdba.fb_spo_log_status WHERE crmid=?");
	        stmn.setObject(1, crmid);
	        stmn.execute();
	        stmn.close();
	        //добавляем нашу запись
	        stmn = conn.prepareStatement("insert into sysdba.fb_spo_log_status (crmid, status, log) values (?,?,?)");
	        stmn.setObject(1, crmid);
	        stmn.setObject(2, i);
	        stmn.setObject(3, message);
	        stmn.execute();
	        stmn.close();
		} catch (SQLException e) {
			throw new MappingException(e, "SQLException crmlog " + e.getMessage());
		}
		
	}
}
