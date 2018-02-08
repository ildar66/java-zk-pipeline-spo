package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.DocumentGroup;
import com.vtb.domain.DocumentsType;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;

/**
 * изменил @author Какунин Константин Юрьевич
 * изменил @author Sergey Melnikov
 */
public class DocumentsTypeMapper extends JDBCMapper<DocumentsType> implements com.vtb.mapping.DocumentsTypeMapper {
    private static final String findByNameSqlString = "SELECT ID_DOCUMENT_TYPE,  NAME_DOCUMENT_TYPE as name,forcc FROM "
            + " DOCUMENTS_TYPE WHERE LOWER(NAME_DOCUMENT_TYPE) like LOWER(?)";

    private static final String SELECT_TYPE = "SELECT ID_DOCUMENT_TYPE, NAME_DOCUMENT_TYPE,forcc FROM " 
            + " DOCUMENTS_TYPE WHERE ID_DOCUMENT_TYPE = ?";

    //создает запись о типе документа с заданными названием и ID 
    private static final String INSERT_TYPE = "INSERT INTO " 
            + " DOCUMENTS_TYPE (ID_DOCUMENT_TYPE, NAME_DOCUMENT_TYPE,forcc) VALUES (?, ?,?)";
    
    private static final String SELECT_GROUP_IDS = "select id_document_group from " + "r_document_group where id_document_type=?";
    
    private static final String SELECT_GROUP = "select ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE from " 
            + " document_group d inner join " 
            + " r_document_group r on r.id_document_group = d.id_group where r.id_document_type = ?";
    /**
     * Добавление связи к группам документов 
     */
    //создает запись, связывающую тип документа и группу документа
    private static final String INSERT_R_DOCUMENT_GROUP = "INSERT INTO " 
    + " r_document_group (ID_DOCUMENT_TYPE, ID_DOCUMENT_GROUP) VALUES (?, ?)"; 

    private static final String DELETE_TYPE = "DELETE FROM " 
            + " DOCUMENTS_TYPE  WHERE ID_DOCUMENT_TYPE = ?";

    private static final String UPDATE_DOCUMENT_TYPE = "UPDATE " 
            + " DOCUMENTS_TYPE SET NAME_DOCUMENT_TYPE = ?,forcc=? WHERE ID_DOCUMENT_TYPE = ?";
   
   
    /**
     * Удаление связей с группами документов
     */
    private static final String DELETE_GROUP_IDS = "DELETE FROM " 
    + " r_document_group  WHERE ID_DOCUMENT_TYPE = ?";
    
    
    private static final String FLD_NAME_DOCUMENT_TYPE = "NAME_DOCUMENT_TYPE";
    
    
    private static final String SEL_CONTRACTOR_DOC_TYPES = "select ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE from DOCUMENT_GROUP WHERE GROUP_TYPE=0";
    private static final String SEL_PERSON_DOC_TYPES = "select ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE from DOCUMENT_GROUP WHERE GROUP_TYPE=2";
    private static final String SEL_OPPORTUNITY_DOC_TYPES = "select distinct ID_GROUP, NAME_DOCUMENT_GROUP, GROUP_TYPE from DOCUMENT_GROUP WHERE GROUP_TYPE=1";
    
    private static final String SEL_DOC_TYPES_BY_OWNERFORM = "select NAME_DOCUMENT_TYPE from DOCUMENTS_TYPE dt, LINK_OWNFORM_DOCTYPE link " +
                                                     "where link.id_document_type = dt.id_document_type and link.id_ownership_form_type=?";
    
    private static final String SELECT_TYPES_BY_GROUP_ID = "SELECT types.ID_DOCUMENT_TYPE, types.NAME_DOCUMENT_TYPE name,types.forcc  " + 
    	"FROM DOCUMENTS_TYPE types, r_document_group link " + 
    	" WHERE types.ID_DOCUMENT_TYPE = link.ID_DOCUMENT_TYPE AND link.id_document_group = ?";
    
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final DocumentGroupMapper groupMapper = new DocumentGroupMapper(); 
    
    @Override
    protected DocumentsType createImpl(Connection conn, DocumentsType d) throws SQLException, MappingException {
        Integer id = d.getId();
        String name = d.getName();
        PreparedStatement ps = conn.prepareStatement(INSERT_TYPE);
        try {
            ps.setObject(1, id);
            ps.setObject(2, name);
            ps.setObject(3, d.getForCC()?"y":"n");
            int rows = ps.executeUpdate();
            if (rows == 1) {
                insertGroups(conn, d);
                return d;
            } else
                // failed
                throw new DuplicateKeyException("Create Failed " + d);
        } finally {
            close(ps);
        }
    }

    /**
     * Добавить групп для типа документа
     * @param conn соединение БД
     * @param d тип документа
     * @throws SQLException
     * @throws DuplicateKeyException
     */
    private void insertGroups(Connection conn, DocumentsType d) throws SQLException, DuplicateKeyException {
        PreparedStatement ps = conn.prepareStatement(INSERT_R_DOCUMENT_GROUP);
        if (d.getGroupID() != null)
            try {
                ps.setLong(1, d.getId());
                for (DocumentGroup groupId : d.getGroupID()) {
                    if (groupId != null)
                        ps.setLong(2, groupId.getId_document_group());
                    int count = ps.executeUpdate();
                    if (count!=1) throw new DuplicateKeyException("Ошибка по запросу: "+INSERT_R_DOCUMENT_GROUP);
                }

            } finally {
                close(ps);
            }
    }

    @Override
    protected DocumentsType findByPrimaryKeyImpl(Connection conn, DocumentsType domainObjectWithKeyValues) throws SQLException,
            MappingException {
    	   	
        DocumentsType documentsType = null;
        Integer documentsTypeId = null;
        if (domainObjectWithKeyValues instanceof DocumentsType) {
            documentsTypeId = ((DocumentsType) domainObjectWithKeyValues).getId();
        } else
            return null;
        PreparedStatement ps = conn.prepareStatement(SELECT_TYPE);
        try {
            ps.setObject(1, documentsTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID_DOCUMENT_TYPE");
                String name = rs.getString("NAME_DOCUMENT_TYPE");
                documentsType = new DocumentsType(id, name,rs.getString("forcc").equalsIgnoreCase("y"));

                PreparedStatement ps2 = null;
                try {
                    ps = conn.prepareStatement(SELECT_GROUP_IDS);
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    ArrayList<DocumentGroup> groupIds = new ArrayList<DocumentGroup>();
                    while (rs.next()) {
                        int groupId = rs.getInt("id_document_group");
                        //ids.add(groupId);
                        DocumentGroup group = new DocumentGroup(groupId);
                        groupMapper.findByPrimaryKey(group);
                        groupIds.add(group);
                    }
                    /*Integer[] groupIds = new Integer[ids.size()];
                    for (int i = 0; i<ids.size(); i++){
                        groupIds[i] = ids.get(i);
                    }*/
                    documentsType.setGroupID(groupIds);
                    return documentsType;
                } finally {
                    close(rs);
                    close(ps2);
                }
            }
        } finally {
            close(ps);
        }
        return documentsType;
    }

    @Override
    protected void removeImpl(Connection conn, DocumentsType d) throws SQLException, MappingException {
        Integer aId = null;
        if (d instanceof DocumentsType) {
            aId = d.getId();
        } else
            throw new MappingException("Removed Failed" + d);
        deleteGroupIds(conn, d);
        PreparedStatement ps = conn.prepareStatement(DELETE_TYPE);
        ps.setObject(1, aId);
        int rows = ps.executeUpdate();
        if (rows == 1)
            return;
        else
            // failed
            throw new MappingException("Remove Failed " + d);

    }

    @Override
    protected void updateImpl(Connection conn, DocumentsType d) throws SQLException, MappingException {
        PreparedStatement ps = conn.prepareStatement(UPDATE_DOCUMENT_TYPE);
        try {
            ps.setString(1, d.getName());
            ps.setString(2, d.getForCC()?"y":"n");
            ps.setInt(3, d.getId());
            int rows = ps.executeUpdate();
            if (rows == 1){
                updateGroup(conn, d);
                return;
            }
            else
                // failed
                throw new MappingException("Update Failed " + d);
        } finally {
            close(ps);
        }
    }

    /**
     * Обновление связей с группами документов
     * @param conn
     * @param d
     * @throws SQLException
     * @throws DuplicateKeyException 
     */
    private void updateGroup(Connection conn, DocumentsType d) throws SQLException, DuplicateKeyException {
        deleteGroupIds(conn, d);
        insertGroups(conn, d);
    }

    /**
     * Удаление принадлежности типа документа к группе
     * @param conn
     * @param d
     * @throws SQLException
     */
    private void deleteGroupIds(Connection conn, DocumentsType d) throws SQLException {
        PreparedStatement p = conn.prepareStatement(DELETE_GROUP_IDS);
        try{
            p.setLong(1, d.getId());
            p.executeUpdate();
        }finally{
            close(p);
        }
        
    }

    public ArrayList<DocumentsType> findByName(String name, String orderBy) throws MappingException {
    	ArrayList<DocumentsType> list = new ArrayList<DocumentsType>();
        DocumentsType documentsType = null;
        Connection conn = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder(findByNameSqlString);
        PreparedStatement ps = null;
        logger.info("[SQL] " + findByNameSqlString);
        try {
            conn = getConnection();
            if (orderBy != null && !orderBy.equals("")) {
                sb.append(" order by " + orderBy);
            }
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID_DOCUMENT_TYPE");
                String typeName = rs.getString("name");
                documentsType= new DocumentsType(id, typeName,rs.getString("forcc").equalsIgnoreCase("y"));
               
                documentsType.setGroupID(getGroupByDocumemtType(conn, id));
                
                //List<String> groups = getGroupByDocumemtType(conn, id);
                //documentTypeTO.setGroupName(groups);
                list.add(documentsType);
            }
            //return list;
        } catch (SQLException se) {
            logger.log(Level.WARNING, "Ошибка при запросе SQL:" + sb, se);
            throw new MappingException(se, "SQLException findByName code=" + se.getErrorCode());
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        
        return list;
    }

    /**
     * Получить наименования групп относящиеся к типу документа
     * @param conn соединение
     * @param typeId id тип документа
     * @return
     * @throws SQLException
     */
    private ArrayList<DocumentGroup> getGroupByDocumemtType(Connection conn, int typeId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null; 
        
        try {
            ps = conn.prepareStatement(SELECT_GROUP);
            ps.setInt(1, typeId);
            rs = ps.executeQuery();
            ArrayList<DocumentGroup> res = new ArrayList<DocumentGroup>();
            while (rs.next()) {
                DocumentGroup group = new DocumentGroup(rs.getLong("ID_GROUP"));
                group.setName_document_group(rs.getString("NAME_DOCUMENT_GROUP"));
                group.setType(rs.getInt("GROUP_TYPE"));
                
                int type = rs.getInt("GROUP_TYPE");
                //type.setType(type);
                group.setType(type);
                
                res.add(group);
            }
            
            return res;
        } finally {
            close(rs);
            close(ps);
        }
    }

    public ArrayList<DocumentsType> findAll() throws MappingException {
        return null;
    }
    
    
    public ArrayList<DocumentsType> getContractorDocTypes() { 	
    	//select .. where GROUP_TYPE = 0
    	return getDocumentTypes(SEL_CONTRACTOR_DOC_TYPES);
    }
    public ArrayList<DocumentsType> getPersonDocTypes() { 	
    	//select .. where GROUP_TYPE = 2
    	return getDocumentTypes(SEL_PERSON_DOC_TYPES);
    }

    public ArrayList<DocumentsType> getOpportunityDocTypes() {
    	//select .. where GROUP_TYPE = 1
        return getDocumentTypes(SEL_OPPORTUNITY_DOC_TYPES);
    }
    
    /*private ArrayList<DocumentsType> getAllDocumentTypeByGroup(DocumentGroup group){
    	ArrayList <DocumentsType> result = new ArrayList<DocumentsType>();
    	
    	Connection conn = null;
    	ResultSet rs = null;
    	PreparedStatement ps = null;
    	try{
    		
    		conn = getConnection();
    		ps = conn.prepareStatement(SELECT_TYPES_BY_GROUP_ID);
    		ps.setInt(1, group.getGroupType().getType());
    		
    		rs = ps.executeQuery(SELECT_TYPES_BY_GROUP_ID);
    		
    		logger.info("[SQL]: " + SELECT_TYPES_BY_GROUP_ID);
    		
    		while (rs.next()){
    			DocumentsType docType = new DocumentsType(rs.getInt("ID_DOCUMENT_TYPE"));
    			docType.setName(rs.getString("NAME_DOCUMENT_TYPE"));
    			
    			docType.getGroupID().add(group);
    			
    			result.add(docType);
    		}
    		
    	} catch (Exception e){
    		logger.log(Level.SEVERE, e.getMessage(), e);
    		e.printStackTrace();
    		result = null;
    	} finally {
    		
    	}
    	
    	return result;
    }*/
    
    private ArrayList<DocumentsType> getDocumentTypes(String sql) {
        ArrayList<DocumentsType> list = null;

        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            int i = 0;
            //Statement stmn = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement(sql);
            rs = ps.executeQuery(sql);//stmn.executeQuery(sql);
            logger.info(sql);
            list = new ArrayList<DocumentsType>();
            while (rs.next()) {
                String value;
                logger.info("Count = " + (i++ + 1));
                
                DocumentGroup group = new DocumentGroup(rs.getInt("ID_GROUP"));
                group.setName_document_group(rs.getString("NAME_DOCUMENT_GROUP"));
                group.setType(rs.getInt("GROUP_TYPE"));

                
                int type = rs.getInt("GROUP_TYPE");
                group.setType(type);
                
                //groups.add(group);
                
                ArrayList<DocumentsType> types = getDocumentTypesByOwnformType((int)group.getId_document_group(), "");
                for (int t = 0; t < types.size(); ++t){
                	boolean exists = false;
                	for (int j = 0; j < list.size(); ++j){
                		if (list.get(j).getId().intValue() == types.get(t).getId().intValue()){
                			exists = true;
                			// МК: автор кода(ЛК) -- просто нехороший человек. Настолько извращенно и тупо!
                			// код должен быть простой как пистолет. Чтобы можно было его поменять легко.
                			// делаем финт ушами. Добавляем группу в уже существующую запись 
                			//(иначе в списке групп для документа будет только ОДНА группа)
                			list.get(j).getGroupID().add(group);
                		}
                	}
                	if (!exists) {
                		list.add(types.get(t));
                	}
                }
                
                /*value = (rs.getString(FLD_NAME_DOCUMENT_TYPE) != null) ? rs.getString(FLD_NAME_DOCUMENT_TYPE) : "";
                logger.info(FLD_NAME_DOCUMENT_TYPE + "=" + value);
                list.add(value);
                i++;*/
                
            }
            
            

        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка при запросе SQL: " + sql, e);
            list = null;
        } finally {
            close(rs);
            close(conn);

        }

        return list;
    }
        
    public ArrayList<DocumentsType> getDocumentTypesByOwnformType(int type, String orderBy) {
        ArrayList<DocumentsType> list = null;

        ResultSet rs = null;
        Connection conn = null;
        try {
            int i = 0;
            conn = getConnection();
            String sqlQuerry = SELECT_TYPES_BY_GROUP_ID;
            if (orderBy.length() > 0){
            	sqlQuerry += " ORDER BY " + orderBy;
            }
            PreparedStatement stmn = conn.prepareStatement(sqlQuerry);
            stmn.setInt(1, type);
            rs = stmn.executeQuery();

            //DocumentGroup group = new DocumentGroup(type);
            DocumentGroup group = groupMapper.findByPrimaryKey(new DocumentGroup(type));
            
            list = new ArrayList<DocumentsType>();
            while (rs.next()) {

                logger.info("Count = " + (i + 1));
    			DocumentsType docType = new DocumentsType(rs.getInt(1));
    			docType.setName(rs.getString(2));
    	
    			docType.getGroupID().add(group);
    			
    			list.add(docType);
                i++;
            }
            stmn.close();

        } catch (Exception e) {
        	logger.log(Level.SEVERE, e.getMessage(), e);
        	e.printStackTrace();
            //logger.log(Level.WARNING, "Ошибка при запросе SQL: " + SELECT_TYPES_BY_GROUP_ID, e);
            list = null;
        } finally {
            close(rs);
            close(conn);
        }

        return list;
    }
}
