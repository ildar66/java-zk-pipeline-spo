package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.custom.OperatorTO;
import com.vtb.domain.Operator;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchOperatorException;

public class OperatorMapper extends JDBCMapper<Operator>  implements com.vtb.mapping.OperatorMapper {
    protected static final String findByNameSqlString = "SELECT ID_OPERATOR id, LOGIN login, FA, IM, OT, ID_DEPARTMENT, EMAIL FROM "
            + " OPERATOR WHERE ID_DEPARTMENT = ? AND LOWER(LOGIN) like LOWER(?)";

    protected static final String _loadString = "SELECT ID_OPERATOR, LOGIN, FA, IM, OT, ID_DEPARTMENT, EMAIL FROM "
            + " OPERATOR WHERE ID_OPERATOR = ?";

    protected static final String _createString = "INSERT INTO " 
            + " users (id_user, LOGIN, surname, name, patronymic, ID_DEPARTMENT, mail_user, IS_ACTIVE) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";

    protected static final String _removeString = "DELETE FROM " 
            + " OPERATOR  WHERE ID_OPERATOR = ?";

    protected static final String _storeString = "UPDATE " 
            + " OPERATOR  SET LOGIN = ?, FA = ?, IM = ?, OT = ?, ID_DEPARTMENT = ?, EMAIL = ? WHERE ID_OPERATOR = ?";

    protected static final String findByLoginSqlString = "SELECT ID_OPERATOR id, LOGIN login, FA, IM, OT, ID_DEPARTMENT, EMAIL FROM "
            + " OPERATOR WHERE lower(LOGIN) = lower(?)";

    protected static final String findByFilterSqlString = "SELECT ID_OPERATOR id, LOGIN login, FA, IM, OT, ID_DEPARTMENT, EMAIL FROM "
            + " OPERATOR WHERE ID_DEPARTMENT = ? ";

    protected static final String findListByFilterSqlString = "SELECT o.ID_OPERATOR id, o.LOGIN login, o.FA, o.IM, o.OT, o.ID_DEPARTMENT, o.EMAIL, d.fullName depName FROM "
             + " OPERATOR o, DEPARTMENTS d WHERE o.ID_DEPARTMENT = d.ID_DEPARTMENT ";

    
    @Override
    protected Operator createImpl(Connection conn, Operator domainObject) throws SQLException, MappingException {
        String login = null;
        String fieldFA = null;
        String fieldIM = null;
        String fieldOT = null;
        Integer departmentID = null;
        String eMail = null;

        Integer id = domainObject.getId();
        login = domainObject.getLogin();
        fieldFA = domainObject.getFieldFA();
        fieldIM = domainObject.getFieldIM();
        fieldOT = domainObject.getFieldOT();
        departmentID = domainObject.getDepartmentID();
        eMail = domainObject.getEMail();

        PreparedStatement ps = conn.prepareStatement(_createString);
        int i = 1;
        ps.setObject(i++, id);
        ps.setObject(i++, login);
        ps.setObject(i++, fieldFA);
        ps.setObject(i++, fieldIM);
        ps.setObject(i++, fieldOT);
        ps.setObject(i++, departmentID);
        ps.setObject(i++, eMail);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return domainObject;
        else
            // failed
            throw new DuplicateKeyException("Create Failed " + domainObject);
    }

    @Override
    protected Operator findByPrimaryKeyImpl(Connection conn, Operator domainObjectWithKeyValues) throws SQLException,
            MappingException {
        Operator operator = null;
        Integer operatorId = domainObjectWithKeyValues.getId();
        PreparedStatement ps = conn.prepareStatement(_loadString);
        ps.setObject(1, operatorId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            operator = activate(rs);
        }
        return operator;
    }

    @Override
    protected void removeImpl(Connection conn, Operator domainObject) throws SQLException, MappingException {
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
    protected void updateImpl(Connection conn, Operator domainObject) throws SQLException, MappingException {
        String login = null;
        String fieldFA = null;
        String fieldIM = null;
        String fieldOT = null;
        Integer departmentID = null;
        String eMail = null;
        Integer id = domainObject.getId();
        login = domainObject.getLogin();
        fieldFA = domainObject.getFieldFA();
        fieldIM = domainObject.getFieldIM();
        fieldOT = domainObject.getFieldOT();
        departmentID = domainObject.getDepartmentID();
        eMail = domainObject.getEMail();
        PreparedStatement ps = conn.prepareStatement(_storeString);

        int i = 1;
        ps.setObject(i++, login);
        ps.setObject(i++, fieldFA);
        ps.setObject(i++, fieldIM);
        ps.setObject(i++, fieldOT);
        ps.setObject(i++, departmentID);
        ps.setObject(i++, eMail);

        ps.setObject(i++, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

    public Integer findOperatorsForMessageCount(Integer departmentId, String sNamePattern) throws MappingException {
        String sql =
        	  "select count(*)"
        	+ "  from operator"
        	+ " where lower(fa) like lower(?)";
        if (departmentId != null) {
        	sql += " and id_department = ?"; 
        }
        Connection connection = null;
        try {
        	connection = getConnection();
        	PreparedStatement st = connection.prepareStatement(sql);
        	st.setString(1, sNamePattern);
        	if (departmentId != null) {
        		st.setInt(2, departmentId);
        	}
        	ResultSet rs = st.executeQuery();
        	rs.next();
        	Integer ret = rs.getInt(1);
        	st.close();
        	return ret;
        } catch(SQLException e) {
        	throw new MappingException(e, e.getMessage());
        } finally {
        	close(connection);
        }
    }
    
    public ArrayList<Operator> findOperatorsForMessage(
    		Integer departmentId, String sNamePattern, Integer start, Integer end) throws MappingException {
    	ArrayList<Operator> ret = new ArrayList<Operator>();
    	String sql = 
    		  "select id, login, fa, im, ot, dep_id, email from ("
    		+ "    select rownum rn, op.id, op.login, op.fa, op.im, op.ot, op.dep_id, op.email from ("
    		+ "        select oper.id_operator as id, oper.login, oper.fa, oper.im, oper.ot, oper.id_department as dep_id, oper.email"
    		+ "          from operator oper"
            + "         where lower(oper.fa) like lower(?)";

    	if (departmentId != null) {
        	sql += "           and oper.id_department = ?";
        }
        
    	sql += "         order by oper.fa"
             + "        ) op"
             + "    )"
             + " where rn between ? and ?";
    	
        Connection connection = null;
        try {
        	connection = getConnection();
        	PreparedStatement ps = connection.prepareStatement(sql);
        	int i = 1;
        	ps.setString(i++, sNamePattern);
        	if (departmentId != null) {
        		ps.setInt(i++, departmentId);
        	}
        	ps.setInt(i++, start);
        	ps.setInt(i++, end);
        	ResultSet rs = ps.executeQuery();
        	while (rs.next()) {
        		ret.add(activate(rs));
        	}
        	ps.close();
        	return ret;
        } catch (SQLException e) {
        	throw new MappingException(e, e.getMessage());
        } finally {
        	close(connection);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.vtb.mapping.OperatorMapper#findByName(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public ArrayList<Operator> findByName(Integer departmentId, String likeName, String orderBy) throws MappingException {
        ArrayList<Operator> list = new ArrayList<Operator>();
        Operator operator = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findByNameSqlString);
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setObject(1, departmentId);
            ps.setString(2, likeName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                operator = activate(rs);
                list.add(operator);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vtb.mapping.OperatorMapper#findByFilter(java.lang.Integer, int,
     *      java.lang.String, java.lang.String)
     */
    public ArrayList<Operator> findByFilter(Integer departmentKey, int searchFilter, String searchStr, String orderBy)
            throws MappingException {
        ArrayList<Operator> list = new ArrayList<Operator>();
        Operator operator = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findByFilterSqlString);
        if (searchFilter == 1) {
            sb.append(" AND LOWER(LOGIN) like LOWER(?) ");
        } else if (searchFilter == 2) {
            sb.append(" AND LOWER(FA) like LOWER(?) ");
        }
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setObject(1, departmentKey);
            ps.setString(2, searchStr);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                operator = activate(rs);
                list.add(operator);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByFilter code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vtb.mapping.OperatorMapper#findListByFilter(int,
     *      java.lang.String, java.lang.String)
     */
    public ArrayList<OperatorTO> findListByFilter(int searchFilter, String searchStr, String orderBy) throws MappingException {
        ArrayList<OperatorTO> list = new ArrayList<OperatorTO>();
        OperatorTO to = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findListByFilterSqlString);
        if (searchFilter == 1) {
            sb.append(" AND LOWER(LOGIN) like LOWER(?) ");
        } else if (searchFilter == 2) {
            sb.append(" AND LOWER(FA) like LOWER(?) ");
        } else if (searchFilter == 0) {
            sb.append(" AND (LOWER(FA) like LOWER(?) OR LOWER(LOGIN) like LOWER(?)) ");
        }
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, searchStr);
            if (searchFilter == 0) {
                ps.setString(2, searchStr);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                to = activateTO(rs);
                list.add(to);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findListByFilter code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }
    
    protected OperatorTO activateTO(ResultSet rs) throws SQLException {
        int i = 1;
        OperatorTO to = new OperatorTO(rs.getInt(i++), rs.getString(i++));
        to.setFieldFA(rs.getString(i++));
        to.setFieldIM(rs.getString(i++));
        to.setFieldOT(rs.getString(i++));
        if (rs.getObject(i) != null) {
            to.setDepartmentID(rs.getInt(i++));
        }
        to.setEMail(rs.getString(i++));
        to.setDepName(rs.getString(i++));
        return to;
    }   

    protected Operator activate(ResultSet rs) throws SQLException {
        int i = 1;
        Operator operator = new Operator(rs.getInt(i++), rs.getString(i++));
        operator.setFieldFA(rs.getString(i++));
        operator.setFieldIM(rs.getString(i++));
        operator.setFieldOT(rs.getString(i++));
        if (rs.getObject(i) != null) {
            operator.setDepartmentID(((BigDecimal) rs.getObject(i++)).intValue());
        }
        operator.setEMail(rs.getString(i++));
        return operator;
    }

    public List<Operator> findAll() throws MappingException {
        // TODO Auto-generated method stub
        return null;
    }

    public Operator findOperatorByLogin(String aLogin) throws MappingException {
        Operator operator = null;
        Connection conn = null;
        StringBuffer sb = new StringBuffer(findByLoginSqlString);
        try {
            conn = getConnection();
            PreparedStatement ps = null;
            ps = conn.prepareStatement(sb.toString());
            ps.setObject(1, aLogin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                operator = activate(rs);
            }
            if (operator == null)
                throw new NoSuchOperatorException("No object found");
            return operator;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

}
