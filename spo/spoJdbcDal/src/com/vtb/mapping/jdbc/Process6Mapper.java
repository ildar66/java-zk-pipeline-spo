package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import ru.masterdm.compendium.value.Page;

import com.vtb.domain.AttachmentFile;
import com.vtb.domain.KeyValue;
import com.vtb.domain.Process6;
import com.vtb.exception.MappingException;
import com.vtb.system.AppService;
import com.vtb.util.ApplProperties;
import com.vtb.util.EJBClientHelper;

public class Process6Mapper extends JDBCMapper<Process6> {
	private static final Logger LOGGER = Logger.getLogger(Process6Mapper.class.getName());
	public static DataSource getDataSource6() {
		DataSource ds = null;
		try {
			InitialContext context = EJBClientHelper.getInitialContext();
			ds = (DataSource) context.lookup(REFERENCE_NAME_PREFIX + ApplProperties.getDatasourceJndiName6());
		} catch (javax.naming.NamingException ne) {
			MappingException e = new MappingException("NamingException: cannot find DataSource in initialContext");
			AppService.handle(e);
		}
		return ds;
	}

	@Override
	protected Process6 createImpl(Connection conn, Process6 domainObject)
			throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	protected Process6 findByPrimaryKeyImpl(Connection conn,
			Process6 domainObjectWithKeyValues) throws SQLException,
			MappingException {
		List<Process6> list = new ArrayList<Process6>();
		list.add(domainObjectWithKeyValues);
		try {
	        loadAttribute(list,true);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
			throw new MappingException(e.getMessage());
		}
        return list.get(0);
	}

	@Override
	protected void removeImpl(Connection conn, Process6 domainObject)
			throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	protected void updateImpl(Connection conn, Process6 anObject)
			throws SQLException, MappingException {
		throw new MappingException("not implemented");
	}

	@Override
	public List<Process6> findAll() throws MappingException {
		throw new MappingException("not implemented");
	}
	
	public Page findAll(int start,int count) throws MappingException {
		List<Process6> list = new ArrayList<Process6>();
		int totalCount=0;
		PreparedStatement ps;
		try {
			ps = getDataSource6().getConnection().prepareStatement(
					"select count(p.id_process) from processes p where p.id_type_process=151 and p.id_status=4");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				totalCount=rs.getInt(1);
			}
			if(totalCount==0) return Page.EMPTY_PAGE;
			String query="select p.id_process from processes p where p.id_type_process=151 and p.id_status=4";
			query = "select * from ( select /*+ FIRST_ROWS(n) */ "+
				"a.*, ROWNUM rnum from ("+query+") a "+
				"where ROWNUM <= "+Integer.toString(start+count-1)+" ) where rnum  >= "+Integer.toString(start);
			ps = getDataSource6().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			//получаем список процессов
	        while (rs.next()) {
	        	Process6 process=new Process6(rs.getLong("id_process"));
	            list.add(process);
	        }
	        close(getDataSource6().getConnection());
	        loadAttribute(list,false);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
			throw new MappingException(e.getMessage());
		}
		Page returnPage = new Page(list, start, (start + list.size()) < totalCount);
		returnPage.setTotalCount(totalCount);
		return returnPage;
	}

	/**
	 * Загружает аттрибуты процесса
	 * @param list
	 * @throws SQLException
	 */
	private void loadAttribute(List<Process6> list,boolean full) throws SQLException {
		PreparedStatement ps;
		ResultSet rs;
		//список аттрибутов
		LinkedHashMap<Long,String> variables = new LinkedHashMap<Long,String>();
		ps = getDataSource6().getConnection().prepareStatement(
			"select v.id_var,v.name_var from variables v where v.id_type_process=151");
		rs = ps.executeQuery();
		while (rs.next()) {
			String name=rs.getString("name_var");
			if(full||name.equals("Заявка №")||name.equals("Сумма лимита")||
					name.equals("Валюта")||name.equals("CRM_Контрагенты"))
				variables.put(rs.getLong("id_var"), name);
		}
		close(getDataSource6().getConnection());
		Set<Long> set = variables.keySet();
		Iterator<Long> iter = set.iterator();
		ps = getDataSource6().getConnection().prepareStatement(
			"select a.value_var from attributes a where a.id_var=? and a.id_process=?");
		while (iter.hasNext()) {
			Long idvar = iter.next();
			for (Process6 process:list){
				ArrayList<String> values=new ArrayList<String>();
				ps.setLong(1, idvar);
				ps.setLong(2, process.getIdprocess());
				rs = ps.executeQuery();
				while (rs.next()) {
					values.add(rs.getString("value_var"));
		        }
				process.getAttr().put(variables.get(idvar), values);
			}
		}
		//загрузить файлы
		if(full){
			ps=getDataSource6().getConnection().prepareStatement(
			"select a.unid,a.filename,a.filedata from appfiles a where a.owner_type=0 and a.id_owner=?");
			for (Process6 process:list){
				ps.setLong(1, process.getIdprocess());
				rs = ps.executeQuery();
				while (rs.next()) {
					AttachmentFile file=new AttachmentFile();
					file.setUnid(rs.getString("unid"));
					file.setFilename(rs.getString("filename"));
					file.setFiledata(rs.getBytes("filedata"));
					process.getFiles().add(file);
		        }
			}
		}
		close(getDataSource6().getConnection());
	}
}
