package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.BaseRate;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class BaseRateMapper extends JDBCMapper<BaseRate> implements com.vtb.mapping.BaseRateMapper{
    protected static final String findByNameSqlString = "SELECT ID_BASE_RATE id, CODE code, RATE_NAME name, RATE_DESCRIPTION description FROM "
            + " BASE_RATE WHERE LOWER(RATE_NAME) like LOWER(?)";

    protected static final String _loadString = "SELECT ID_BASE_RATE id, CODE code, RATE_NAME name, RATE_DESCRIPTION description FROM "
            + " BASE_RATE WHERE ID_BASE_RATE = ?";

    protected static final String _createString = "INSERT INTO " 
            + " BASE_RATE (ID_BASE_RATE, CODE, RATE_NAME, RATE_DESCRIPTION) VALUES (?, ?, ?, ?)";

    protected static final String _removeString = "DELETE FROM " + " BASE_RATE  WHERE ID_BASE_RATE = ?";

    protected static final String _storeString = "UPDATE " 
            + " BASE_RATE  SET CODE = ?, RATE_NAME = ?, RATE_DESCRIPTION = ? WHERE ID_BASE_RATE = ?";

    @Override
    protected BaseRate createImpl(Connection conn, BaseRate domainObject) throws SQLException, MappingException {
        Integer id = null;
        Integer code = null;
        String name = null;
        String description = null;
        if (domainObject instanceof BaseRate) {
            id = ((BaseRate) domainObject).getId();
            code = ((BaseRate) domainObject).getCode();
            name = ((BaseRate) domainObject).getName();
            description = ((BaseRate) domainObject).getDescription();
        } else {
            // update fails
            throw new DuplicateKeyException("Create Failed " + domainObject);
        }
        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, code);
        ps.setObject(3, name);
        ps.setObject(4, description);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return domainObject;
        else
            // failed
            throw new DuplicateKeyException("Create Failed " + domainObject);
    }

    @Override
    protected BaseRate findByPrimaryKeyImpl(Connection conn, BaseRate domainObjectWithKeyValues) throws SQLException, MappingException {
        BaseRate baseRate = null;
        Integer baseRateId = null;
        baseRateId = ((BaseRate) domainObjectWithKeyValues).getId();
        PreparedStatement ps = conn.prepareStatement(_loadString);
        ps.setObject(1, baseRateId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            baseRate = activate(rs);
        }
        return baseRate;
    }

    @Override
    protected void removeImpl(Connection conn, BaseRate domainObject) throws SQLException, MappingException {
        Integer aId = null;
        if (domainObject instanceof BaseRate) {
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
    protected void updateImpl(Connection conn, BaseRate domainObject) throws SQLException, MappingException {
        Integer id = null;
        Integer code = null;
        String name = null;
        String description = null;
        id = domainObject.getId();
        code = domainObject.getCode();
        name = domainObject.getName();
        description = domainObject.getDescription();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, code);
        ps.setObject(2, name);
        ps.setObject(3, description);
        ps.setObject(4, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

    public ArrayList<BaseRate> findByName(String name, String orderBy) throws MappingException {
        ArrayList<BaseRate> list = new ArrayList<BaseRate>();
        BaseRate baseRate = null;
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
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                baseRate = activate(rs);
                list.add(baseRate);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    protected BaseRate activate(ResultSet rs) throws SQLException {
        BaseRate baseRate = new BaseRate(((BigDecimal) rs.getObject(1)).intValue(), rs.getString(3));
        baseRate.setCode(((BigDecimal) rs.getObject(2)).intValue());
        baseRate.setDescription(rs.getString(4));
        return baseRate;
    }

    public List<BaseRate> findAll() throws MappingException {
        return null;
    }

}
