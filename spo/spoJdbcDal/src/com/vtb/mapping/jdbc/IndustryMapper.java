package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.Industry;
import com.vtb.exception.MappingException;

public class IndustryMapper extends JDBCMapper<Industry> implements com.vtb.mapping.IndustryMapper{
	
	protected static final String fieldsString="select fb_industryid id, industry name, industry_rating rating," +
			"INDUSTRY_TYPE type, corp_block corp from V_INDUSTRY ";
	
    protected static final String findByNameSqlString = fieldsString+" WHERE LOWER(INDUSTRY) like LOWER(?)";

    protected static final String _loadString = fieldsString+" WHERE fb_industryid= ?";

    protected static final String _storeString = "UPDATE "
            + " MY_INDUSTRY SET INDUSTRY_RATING = ?, INDUSTRY_TYPE = ? WHERE INDUSTRY_CRMID = ?";

    @Override
    protected Industry createImpl(Connection conn, Industry domainObject) throws SQLException, MappingException {
            throw new MappingException("Cannot create industry. Illegal operation for this object ");
    }

    @Override
    protected Industry findByPrimaryKeyImpl(Connection conn, Industry domainObjectWithKeyValues) throws SQLException, MappingException {
        Industry industry = null;
        String industryId = null;
        if (domainObjectWithKeyValues instanceof Industry) {
            industryId = ((Industry) domainObjectWithKeyValues).getId();
        } else
            return null;
        PreparedStatement ps = conn.prepareStatement(_loadString);
        ps.setObject(1, industryId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            industry = activate(rs);
        }
        return industry;
    }

    @Override
    protected void removeImpl(Connection conn, Industry domainObject) throws SQLException, MappingException {
    	throw new MappingException("Cannot remove industry. Illegal operation for this object ");

    }

    @Override
    protected void updateImpl(Connection conn, Industry domainObject) throws SQLException, MappingException {
    	String id = null;
        String rating = null;
        String type = null;
        if (domainObject instanceof Industry) {
            id = domainObject.getId();
            rating = domainObject.getRating();
            type = domainObject.getType();
        } else {
            // update fails
            throw new MappingException("Update Failed " + domainObject);
        }
        //пробуем создать запись в нашей таблице для этой отрасли
        try{
        	PreparedStatement p=conn.prepareStatement("insert into  my_industry(INDUSTRY_CRMID) values(?)");
        	p.setObject(1, id);
        	p.execute();
        } catch (SQLException se) {//если ошибка, то такая отрасль уже есть. Ничего делать не нужно
        }
        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setObject(1, rating);
        ps.setObject(2, type);
        ps.setObject(3, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

    public ArrayList<Industry> findByName(String name, String orderBy) throws MappingException {
        ArrayList<Industry> list = new ArrayList<Industry>();
        Industry industry = null;
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
                industry = activate(rs);
                list.add(industry);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    protected Industry activate(ResultSet rs) throws SQLException {
        Industry industry = new Industry( rs.getString("id"), rs.getString("name"));
        industry.setRating(rs.getString("rating"));
        industry.setType(rs.getString("type"));
        industry.setCorpBlock(rs.getString("corp"));
        return industry;
    }

    public ArrayList<Industry> findAll() throws MappingException {
        // TODO Auto-generated method stub
        return null;
    }

}
