package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.MQSchedulerSheet;
import com.vtb.exception.MappingException;
import com.vtb.util.Converter;

public class MQSchedulerSheetMapper extends JDBCMapperExt<MQSchedulerSheet> implements com.vtb.mapping.MQSchedulerSheetMapper {
	final String TABLE = "MQSchedulerSheet";
	final String FLD_ID = "ID";
	final String FLD_STARTTIME = "START_TIME";
	final String FLD_ENDTIME = "END_TIME";
	final String FLD_ID_DEPARTMENT = "ID_DEPARTMENT";
	final String FLD_DAYSOFWEEK = "DAYS_OF_WEEK";
	final String FLD_STATUS = "STATUS";
	
	final String CMD_INSERT = "insert into "+TABLE+" ("+
								FLD_ID+", "+
								FLD_STARTTIME+", "+
								FLD_ENDTIME+", "+
								FLD_ID_DEPARTMENT+", "+
								FLD_DAYSOFWEEK+", "+
								FLD_STATUS+
								") VALUES(?,?,?,?,?,?)";
	final String CMD_UPDATE = "update "+TABLE+" set "+
								FLD_STARTTIME+"=?, " +
								FLD_ENDTIME+"=?, " +
								FLD_ID_DEPARTMENT+"=?, " +
								FLD_DAYSOFWEEK+"=?, " +
								FLD_STATUS+"=? " +
								" where "+FLD_ID+"=?";
	final String CMD_REMOVE = "delete from "+TABLE+" where "+FLD_ID+"=?";
	final String CMD_FIND_BY_KEY = "select "+
									FLD_ID+", "+
									FLD_STARTTIME+", "+
									FLD_ENDTIME+", "+
									FLD_ID_DEPARTMENT+", "+
									FLD_DAYSOFWEEK+", "+
									FLD_STATUS+
									" from "+TABLE+
									" where "+FLD_ID+"=?";
	final String CMD_FIND_LIST = "select "+
									FLD_ID+", "+
									FLD_STARTTIME+", "+
									FLD_ENDTIME+", "+
									FLD_ID_DEPARTMENT+", "+
									FLD_DAYSOFWEEK+", "+
									FLD_STATUS+
									" from "+TABLE+
									" where "+FLD_ID_DEPARTMENT+"=?";	
	
	@Override
	protected List<MQSchedulerSheet> findAllImpl(Connection conn) throws SQLException,
			MappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    protected void insertImpl(Connection conn, MQSchedulerSheet anObject) throws SQLException, MappingException {

        MQSchedulerSheet sheet = (MQSchedulerSheet) anObject;

        if (sheet.getId().equals(new Long(MQSchedulerSheet.CONST_ID_UNKNOWN))) {
            //TODO INSERT ID GENERATION
            throw new MappingException("ERROR: error id generation");
        }

        PreparedStatement stmn = conn.prepareStatement(CMD_INSERT);

        stmn.setObject(1, sheet.getId());
        stmn.setTime(2, sheet.getStartTime());
        stmn.setTime(3, sheet.getEndTime());
        stmn.setObject(4, sheet.getId_department());
        stmn.setObject(5, sheet.getDaysOfWeek());
        stmn.setObject(6, sheet.getStatus());

        stmn.execute();
        stmn.close();

    }

	@Override
	protected MQSchedulerSheet createImpl(Connection conn, MQSchedulerSheet domainObject)
			throws SQLException, MappingException {
		return null;
	}

	@Override
    protected MQSchedulerSheet findByPrimaryKeyImpl(Connection conn, MQSchedulerSheet sheet) throws SQLException, MappingException {
        // TODO Auto-generated method stub
        boolean isExist = false;

        PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_KEY);

        stmn.setObject(1, sheet.getId());
        ResultSet rs = stmn.executeQuery();
        if (rs.next()) {
            isExist = true;
            mapTableRow(sheet, rs);
        }

        stmn.close();
        return (isExist == true ? sheet : null);
    }

    @Override
    protected void removeImpl(Connection conn, MQSchedulerSheet sheet) throws SQLException, MappingException {
        // TODO Auto-generated method stub
        PreparedStatement stmn = conn.prepareStatement(CMD_REMOVE);

        stmn.setObject(1, sheet.getId());

        stmn.execute();
        stmn.close();
    }

	@Override
    protected void updateImpl(Connection conn, MQSchedulerSheet sheet) throws SQLException, MappingException {
        PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);

        stmn.setTime(1, sheet.getStartTime());
        stmn.setTime(2, sheet.getEndTime());
        stmn.setObject(3, sheet.getId_department());
        stmn.setObject(4, sheet.getDaysOfWeek());
        stmn.setObject(5, sheet.getStatus());
        stmn.setObject(6, sheet.getId());

        stmn.execute();
        stmn.close();
    }
	
	protected void mapTableRow(MQSchedulerSheet sheet, ResultSet rs) throws SQLException {
		sheet.setId(Converter.toLong((BigDecimal)rs.getObject(FLD_ID)));
		sheet.setStartTime(rs.getTime(FLD_STARTTIME));
		sheet.setEndTime(rs.getTime(FLD_ENDTIME));
		sheet.setId_department(Converter.toLong((BigDecimal)rs.getObject(FLD_ID_DEPARTMENT)));
		sheet.setDaysOfWeek(Converter.toInteger((BigDecimal)rs.getObject(FLD_DAYSOFWEEK)));
		sheet.setStatus(Converter.toInteger((BigDecimal)rs.getObject(FLD_STATUS)));
		
	}


	public ArrayList<MQSchedulerSheet> findList(Integer departmentKey, String status, String orderBy) throws MappingException {
        ArrayList<MQSchedulerSheet> list = new ArrayList<MQSchedulerSheet>();
		MQSchedulerSheet mqSchedulerSheet = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer(CMD_FIND_LIST);
		try {
			conn = getConnection();
			PreparedStatement ps = null;
			if (orderBy != null && !orderBy.equals("")) {
				sb.append(" order by " + orderBy);
			}
			ps = conn.prepareStatement(sb.toString());
			ps.setObject(1, departmentKey);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				mqSchedulerSheet = new MQSchedulerSheet();
				mapTableRow(mqSchedulerSheet, rs);
				list.add(mqSchedulerSheet);
			}
			return list;
		} catch (SQLException se) {
			se.printStackTrace(System.out);
			throw new MappingException(se, "SQLException findByName code="
					+ se.getErrorCode());
		} finally {
			close(conn);
		}
	}

}
