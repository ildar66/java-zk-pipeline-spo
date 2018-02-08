package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.Shareholder;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.system.AppService;
import com.vtb.system.TraceCapable;
import com.vtb.util.Converter;

public class ShareholderMapper extends JDBCMapperExt<Shareholder> implements com.vtb.mapping.ShareholderMapper {
    public final static String TABLE = "SHAREHOLDERS";

    final static String FLD_ID = "ID_SHAREHOLDER";
    final static String FLD_FIO = "FIO";
    final static String FLD_CATEGORY = "CATEGORY";
    final static String FLD_PART = "PART";
    final static String FLD_ID_CONTRACTOR = "ID_CONTRACTOR";

    final String CMD_FIND_BY_CONTRACTOR = "select " + FLD_ID + ", " + FLD_FIO + "," + FLD_CATEGORY + "," + FLD_PART
            + "," + FLD_ID_CONTRACTOR + " " + "from " + TABLE + " where " + FLD_ID_CONTRACTOR + "=?";
    final String CMD_FIND_BY_KEY = "select " + FLD_ID + ", " + FLD_FIO + "," + FLD_CATEGORY + "," + FLD_PART + ","
            + FLD_ID_CONTRACTOR + " " + "from " + TABLE + " where " + FLD_ID + "=?";

    final String CMD_INSERT = "insert into " + TABLE + " (" + FLD_ID + ", " + FLD_FIO + "," + FLD_CATEGORY + ","
            + FLD_PART + "," + FLD_ID_CONTRACTOR + " " + ") VALUES(?,?,?,?,?)";

    final String CMD_UPDATE = "update " + TABLE + " set " + FLD_FIO + "=?, " + FLD_CATEGORY + "=?, " + FLD_PART
            + "=?, " + FLD_ID_CONTRACTOR + "=? " + " where " + FLD_ID + "=?";

    final String CMD_REMOVE = "delete from " + TABLE + " where " + FLD_ID + "=?";

    protected static final String findByOrganizationSqlString = "SELECT ID_SHAREHOLDER id, FIO, PART, CATEGORY, ID_CONTRACTOR organizationID FROM "
            + " SHAREHOLDERS  WHERE ID_CONTRACTOR = ?";

    protected static final String findByOrganizationCrmSqlString = "SELECT null id, 'name' fio, 'PART', 'CATEGORY', null organizationID FROM "
            + " V_SHAREHOLDERS  WHERE CRMORG = ?";

    @Override
    protected ArrayList<Shareholder> findAllImpl(Connection conn) throws SQLException, MappingException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void insertImpl(Connection conn, Shareholder holder) throws SQLException, MappingException {

        PreparedStatement stmn = conn.prepareStatement(CMD_INSERT);

        stmn.setObject(1, holder.getId());
        stmn.setString(2, holder.getFio());
        stmn.setString(3, holder.getCategory());
        stmn.setString(4, holder.getPart());
        stmn.setObject(5, holder.getId_contractor());

        stmn.execute();
        stmn.close();

    }

    @Override
    protected Shareholder createImpl(Connection conn, Shareholder domainObject) throws SQLException, MappingException {
        return null;
    }

    @Override
    protected Shareholder findByPrimaryKeyImpl(Connection conn, Shareholder holder) throws SQLException, MappingException {
        boolean isExist = false;
        PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_KEY);
        stmn.setObject(1, holder.getId());
        ResultSet rs = stmn.executeQuery();
        if (rs.next()) {
            isExist = true;
            mapTableRow(holder, rs);
        }

        stmn.close();
        return (isExist == true ? holder : null);
    }

    @Override
    protected void removeImpl(Connection conn, Shareholder domainObject) throws SQLException, MappingException {
        if (domainObject instanceof Shareholder) {
            Shareholder holder = (Shareholder) domainObject;
            PreparedStatement stmn = conn.prepareStatement(CMD_REMOVE);

            stmn.setObject(1, holder.getId());

            stmn.execute();
            stmn.close();
        } else {
            throw new MappingException("ERROR: incorrect mapping to Shareholder object");
        }
    }

    @Override
    protected void updateImpl(Connection conn, Shareholder anObject) throws SQLException, MappingException {
        if (anObject instanceof Shareholder) {
            Shareholder holder = (Shareholder) anObject;
            PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);

            stmn.setString(1, holder.getFio());
            stmn.setString(2, holder.getCategory());
            stmn.setString(3, holder.getPart());
            stmn.setObject(4, holder.getId_contractor());

            stmn.setObject(5, holder.getId());

            stmn.execute();
            stmn.close();
        } else {
            throw new MappingException("ERROR: incorrect mapping to Shareholder object");
        }
    }

    public ArrayList findByContractor(Shareholder findObjects) throws SQLException, MappingException {
        Connection conn = null;
        ArrayList<Shareholder> objectList = new ArrayList<Shareholder>();
        try {
            // get a connection
            conn = getConnection();
            // single transaction.
            // conn.setAutoCommit(false);
            PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_CONTRACTOR);
            stmn.setLong(1, findObjects.getId_contractor());
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                Shareholder holder = new Shareholder();
                mapTableRow(holder, rs);
                objectList.add(holder);
            }

            // conn.commit();
        } catch (Exception e) {
            AppService.log(TraceCapable.ERROR_LEVEL, "Exception " + e + " caught in findByContractor()");
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in findByContractor()");
        } finally {
            close(conn);
        }
        return objectList;
    }

    protected void mapTableRow(Shareholder holder, ResultSet rs) throws SQLException {
        holder.setId(Converter.toLong((BigDecimal) rs.getObject(FLD_ID)));
        holder.setFio(rs.getString(FLD_FIO));
        holder.setCategory(rs.getString(FLD_CATEGORY));
        holder.setPart(rs.getString(FLD_PART));
        holder.setId_contractor(Converter.toLong((BigDecimal) rs.getObject(FLD_ID_CONTRACTOR)));
    }

    /**
     * 
     */
    public ArrayList<Shareholder> findListByOrganization(Integer organizationID, String orderBy) throws MappingException {
        ArrayList<Shareholder> list = new ArrayList<Shareholder>();
        Shareholder vo = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findByOrganizationSqlString);
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, organizationID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vo = activate(rs);
                list.add(vo);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findListByOrganization code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    public ArrayList<Shareholder> findListByOrganizationCRM(String orgCrmKey, String orderBy) throws MappingException {
        ArrayList<Shareholder> list = new ArrayList<Shareholder>();
        Shareholder vo = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findByOrganizationCrmSqlString);
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setObject(1, orgCrmKey);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vo = activate(rs);
                list.add(vo);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findListByOrganization code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    protected Shareholder activate(ResultSet rs) throws SQLException {
        int i = 1;
        Shareholder vo = new Shareholder(Converter.toLong((BigDecimal) rs.getObject(i++)), rs.getString(i++), rs
                .getString(i++), rs.getString(i++), Converter.toLong((BigDecimal) rs.getObject(i++)));
        return vo;
    }
}
