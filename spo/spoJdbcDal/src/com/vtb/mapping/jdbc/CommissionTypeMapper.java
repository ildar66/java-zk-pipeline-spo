package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.masterdm.compendium.domain.Currency;

import com.vtb.domain.CommissionType;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

public class CommissionTypeMapper extends JDBCMapper<CommissionType> implements com.vtb.mapping.CommissionTypeMapper  {
    protected static final String findByNameSqlString = "SELECT ID_COMMISSION_TYPE id, NAME_COMMISSION_TYPE name FROM "
            + " COMMISSION_TYPE WHERE LOWER(NAME_COMMISSION_TYPE) like LOWER(?)";

    protected static final String _loadString = "SELECT ID_COMMISSION_TYPE id, NAME_COMMISSION_TYPE name FROM "
            + " COMMISSION_TYPE WHERE ID_COMMISSION_TYPE = ?";

    protected static final String _createString = "INSERT INTO " 
            + " COMMISSION_TYPE (ID_COMMISSION_TYPE, NAME_COMMISSION_TYPE) VALUES (?, ?)";

    protected static final String _removeString = "DELETE FROM " 
            + " COMMISSION_TYPE  WHERE ID_COMMISSION_TYPE = ?";

    protected static final String _storeString = "UPDATE " 
            + " COMMISSION_TYPE  SET NAME_COMMISSION_TYPE = ? WHERE ID_COMMISSION_TYPE = ?";

    @Override
    protected CommissionType createImpl(Connection conn, CommissionType domainObject) throws SQLException, MappingException {
        Integer id = null;
        String name = null;
        if (domainObject instanceof CommissionType) {
            id = domainObject.getId();
            name = domainObject.getName();
        } else {
            // update fails
            throw new DuplicateKeyException("Create Failed " + domainObject);
        }
        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, name);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return domainObject;
        else
            // failed
            throw new DuplicateKeyException("Create Failed " + domainObject);
    }

    @Override
    protected CommissionType findByPrimaryKeyImpl(Connection conn, CommissionType domainObjectWithKeyValues) throws SQLException, MappingException {
        CommissionType commissionType = null;
        Integer commissionTypeId = null;
        if (domainObjectWithKeyValues instanceof CommissionType) {
            commissionTypeId = ((CommissionType) domainObjectWithKeyValues).getId();
        } else
            return null;
        PreparedStatement ps = conn.prepareStatement(_loadString);
        ps.setObject(1, commissionTypeId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            commissionType = activate(rs);
        }
        return commissionType;
    }

    @Override
    protected void removeImpl(Connection conn, CommissionType domainObject) throws SQLException, MappingException {
        Integer aId = domainObject.getId();
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
    protected void updateImpl(Connection conn, CommissionType domainObject) throws SQLException, MappingException {
        Integer id = null;
        String name = null;
        id = domainObject.getId();
        name = domainObject.getName();
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, name);
        ps.setObject(2, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

    public ArrayList<CommissionType> findByName(String name, String orderBy) throws MappingException {
        ArrayList<CommissionType> list = new ArrayList<CommissionType>();
        CommissionType commissionType = null;
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
                commissionType = activate(rs);
                list.add(commissionType);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    protected CommissionType activate(ResultSet rs) throws SQLException {
        CommissionType commissionType = new CommissionType(((BigDecimal) rs.getObject(1)).intValue(), rs.getString(2));
        return commissionType;
    }

    public List<CommissionType> findAll() throws MappingException {
        return null;
    }


    public ArrayList<Currency> findParentCurrency(Long parentTaskId) throws MappingException {
        // загружаем все сохраненные валюты
        ArrayList<Currency> currencyList = new ArrayList<Currency>();
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement st = conn.prepareStatement(
                "SELECT id_currency FROM r_mdtask_currency where ID_MDTASK = ? order by id_currency");
            st.setObject(1, parentTaskId);
            ResultSet r = st.executeQuery();
            while (r.next()) {
                    currencyList.add(new Currency(r.getString("id_currency")));
            }
            r.close();
            st.close();
            return currencyList;
        }  catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException Currency.findParentCurrency");
        } finally {
            close(conn);
        }
    }
}
