package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vtb.domain.DocumentGroup;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

/**
 * изменил @author Какунин Константин Юрьевич
 * изменил @author Sergey Melnikov
 */

public class DocumentGroupMapper extends JDBCMapper<DocumentGroup> implements com.vtb.mapping.DocumentGroupMapper {
    protected static final String findByNameSqlString = "SELECT ID_GROUP id, NAME_DOCUMENT_GROUP name, GROUP_TYPE FROM " 
            + " DOCUMENT_GROUP WHERE LOWER(NAME_DOCUMENT_GROUP) like LOWER(?)";

    protected static final String _loadString = "SELECT ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE FROM " 
            + " DOCUMENT_GROUP WHERE ID_GROUP = ?";

    protected static final String _createString = "INSERT INTO " 
            + " DOCUMENT_GROUP (ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE) VALUES (?, ?, ?)";

    protected static final String _removeString = "DELETE FROM " + " DOCUMENT_GROUP  WHERE ID_GROUP = ?";

    protected static final String _storeString = "UPDATE " 
            + " DOCUMENT_GROUP  SET NAME_DOCUMENT_GROUP = ?, GROUP_TYPE = ? WHERE ID_GROUP = ?";

    protected static final String _removeLinkToDocTypes = "DELETE FROM " + "r_document_group WHERE ID_Document_Group = ?";
    
    @Override
    protected DocumentGroup createImpl(Connection conn, DocumentGroup domainObject) throws SQLException, MappingException {
        Object id = domainObject.getId_document_group();
        String name = domainObject.getName_document_group();
        int type = domainObject.getType();

        PreparedStatement ps = conn.prepareStatement(_createString);
        ps.setObject(1, id);
        ps.setObject(2, name);
        ps.setInt(3, type);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return domainObject;
        else
            // failed
            throw new DuplicateKeyException("Create Failed " + domainObject);
    }

    @Override
    protected DocumentGroup findByPrimaryKeyImpl(Connection conn, DocumentGroup domainObjectWithKeyValues) throws SQLException, MappingException {
        DocumentGroup documentGroup = null;
        Object documentGroupId = null;
        if (domainObjectWithKeyValues instanceof DocumentGroup) {
            documentGroupId = ((DocumentGroup) domainObjectWithKeyValues).getId_document_group();
        } else
            return null;
        PreparedStatement ps = conn.prepareStatement(_loadString);
        ps.setObject(1, documentGroupId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            documentGroup = activate(rs);
        }
        return documentGroup;
    }

    @Override
    protected void removeImpl(Connection conn, DocumentGroup domainObject) throws SQLException, MappingException {
        Object aId = null;
        if (domainObject instanceof DocumentGroup) {
            aId = domainObject.getId_document_group();
        } else
            throw new MappingException("Removed Failed" + domainObject);
        PreparedStatement ps = conn.prepareStatement(_removeString);
        ps.setObject(1, aId);
        int rows = ps.executeUpdate();
        if (rows == 1){
            PreparedStatement ps2 = conn.prepareStatement(_removeLinkToDocTypes);
            ps2.setObject(1, aId);
            ps2.executeUpdate();
        	return;
        }else
            // failed
            throw new MappingException("Remove Failed " + domainObject);

    }

    @Override
    protected void updateImpl(Connection conn, DocumentGroup domainObject) throws SQLException, MappingException {
        long id;
        String name = null;
        int type;
        
        id = domainObject.getId_document_group();
        name = domainObject.getName_document_group();
        type = domainObject.getType();

        PreparedStatement ps = conn.prepareStatement(_storeString);
        ps.setString(1, name);
        ps.setInt(2, type);
        ps.setLong(3, id);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Update Failed " + domainObject);
    }

    public ArrayList<DocumentGroup> findByName(String name, String orderBy) throws MappingException {
        ArrayList<DocumentGroup> list = new ArrayList<DocumentGroup>();
        DocumentGroup documentGroup = null;
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
                documentGroup = activate(rs);
                list.add(documentGroup);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace(System.out);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(conn);
        }
    }

    protected DocumentGroup activate(ResultSet rs) throws SQLException {
        DocumentGroup documentGroup = new DocumentGroup(((BigDecimal) rs.getObject(1)).intValue());
        documentGroup.setName_document_group(rs.getString(2));  

        documentGroup.setType(rs.getInt(3));

        return documentGroup;
    }

    public ArrayList<DocumentGroup> findAll() throws MappingException {
        // TODO Auto-generated method stub
        return null;
    }
}
