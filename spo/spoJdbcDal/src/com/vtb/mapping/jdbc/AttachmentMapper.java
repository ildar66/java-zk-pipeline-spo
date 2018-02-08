package com.vtb.mapping.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.Attachment;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;

public class AttachmentMapper extends JDBCMapperExt<Attachment> implements com.vtb.mapping.AttachmentMapper {

    private final static String TABLE = "APPFILES";

    private final static String FLD_UNID = "UNID";
    private final static String FLD_FILEGROUP = "NAME_DOCUMENT_GROUP";
    private final static String FLD_FILETYPE = "FILETYPE";
    private final static String FLD_FILENAME = "FILENAME";
    private final static String FLD_SIGNATURE = "SIGNATURE";
    private final static String FLD_ID_GROUP = "ID_GROUP";
    private final static String FLD_ID_OWNER = "ID_OWNER";
    private final static String FLD_ID_TYPE = "ID_DOCUMENT_TYPE";
    private final static String FLD_OWNER_TYPE = "OWNER_TYPE";
    private final static String FLD_DATE_OF_EXPIRATION = "DATE_OF_EXPIRATION";
    private final static String FLD_DATE_OF_ADDITION = "DATE_OF_ADDITION";
    private final static String FLD_WHO_ADD = "WHO_ADD";
    private final static String FLD_ISACCEPTED = "ISACCEPTED";
    private final static String FLD_WHO_ACCEPT = "WHOACCEPTED";
    private final static String FLD_DATE_OF_ACCEPT = "DATE_OF_ACCEPT";
    private final static String FLD_FORCC = "FORCC";

    private final String CMD_FIND_BY_OWNER_AND_TYPE = "select " + FLD_UNID + ", " + FLD_FORCC + ", " +FLD_FILENAME + "," 
            + FLD_FILETYPE + "," + FLD_FILEGROUP + ", a." + FLD_ID_GROUP + "," + FLD_ID_OWNER + ","
            + FLD_OWNER_TYPE + "," + FLD_DATE_OF_EXPIRATION + "," + FLD_DATE_OF_ADDITION + "," + FLD_WHO_ADD + "," 
            + FLD_ISACCEPTED + "," + FLD_WHO_ACCEPT + "," + FLD_DATE_OF_ACCEPT + ", " + FLD_SIGNATURE + ", contentType, " + FLD_ID_TYPE 
            + " from APPFILES a left outer join DOCUMENT_GROUP g on a.ID_GROUP = g.ID_GROUP"
            + " where " + FLD_ID_OWNER + "=? and " + FLD_OWNER_TYPE + "=?"
            + " order by " + FLD_FILEGROUP + ", " + FLD_FILETYPE  + ", " + FLD_FILENAME;

    private final String CMD_FIND_BY_KEY = "select " + FLD_UNID + ", " + FLD_FORCC + ", " + FLD_FILENAME + "," 
            + FLD_FILETYPE + "," + FLD_FILEGROUP + ", a." + FLD_ID_GROUP + "," + FLD_ID_OWNER + "," + FLD_OWNER_TYPE + ","
            + FLD_DATE_OF_EXPIRATION + "," + FLD_DATE_OF_ADDITION + "," + FLD_WHO_ADD + "," + FLD_ISACCEPTED + "," 
            + FLD_WHO_ACCEPT + "," + FLD_DATE_OF_ACCEPT + ", " + FLD_SIGNATURE + ",contentType, " + FLD_ID_TYPE 
            + " from APPFILES a left outer join DOCUMENT_GROUP g on a.ID_GROUP = g.ID_GROUP"
            + " where " + FLD_UNID + "=?";

    private final String CMD_INSERT = "insert into " + TABLE + " (" + FLD_UNID + ", " + FLD_FILENAME + "," 
            + FLD_FILETYPE + "," + FLD_ID_GROUP + "," + FLD_ID_OWNER + "," + FLD_OWNER_TYPE + "," + FLD_DATE_OF_EXPIRATION + "," 
            + FLD_DATE_OF_ADDITION + "," + FLD_WHO_ADD + "," + FLD_ISACCEPTED + ","
            + FLD_WHO_ACCEPT + "," + FLD_DATE_OF_ACCEPT + "," + FLD_SIGNATURE + ", " +FLD_FORCC+ ",contentType, " + FLD_ID_TYPE   
            + ") VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?)";

    private final String CMD_UPDATE = "update " + TABLE + " set " + FLD_FILENAME + "=?, " + FLD_FILETYPE + "=?, " 
            + FLD_ID_GROUP + "=?, " + FLD_ID_OWNER + "=?, " + FLD_OWNER_TYPE
            + "=?, " + FLD_DATE_OF_EXPIRATION + "=?, " + FLD_DATE_OF_ADDITION + "=?, " + FLD_WHO_ADD + "=?, " + FLD_ISACCEPTED + "=?, "
            + FLD_WHO_ACCEPT + "=?, " + FLD_DATE_OF_ACCEPT + "=?, " + FLD_SIGNATURE + "=?, "+FLD_FORCC + "=? ,contentType=? " + FLD_ID_TYPE + "=? " 
            + " where " + FLD_UNID + "=?";

    private final String CMD_REMOVE = "delete from " + TABLE + " where " + FLD_UNID + "=?";

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    protected void insertImpl(Connection conn, Attachment file) throws SQLException, MappingException {
        PreparedStatement stmn = conn.prepareStatement(CMD_INSERT);
        stmn.setString(1, file.getUnid());
        stmn.setString(2, file.getFilename());
        stmn.setString(3, file.getFiletype());
        stmn.setString(4, file.getIdGroup());
        stmn.setString(5, file.getIdOwner());
        stmn.setLong(6, file.getOwnerType());
        
        if (file.getDateOfExpiration() != null) stmn.setTimestamp(7, new Timestamp(file.getDateOfExpiration().getTime()));
        else stmn.setTimestamp(7, null);
        
        if (file.getDateOfAddition() != null) stmn.setTimestamp(8, new Timestamp(file.getDateOfAddition().getTime()));
        else stmn.setTimestamp(8, null);
        
        stmn.setObject(9, file.getWhoAdd());
        stmn.setObject(10, file.isAccepted());
        stmn.setObject(11, file.getWhoAccept());
        
        if (file.getDateOfAccept() != null) stmn.setTimestamp(12, new Timestamp(file.getDateOfAccept().getTime()));
        else stmn.setTimestamp(12, null);
        
        byte[] signature = file.getSignature().getBytes();
        if (signature != null && signature.length > 0) stmn.setBinaryStream(13, new ByteArrayInputStream(signature), signature.length);
        else stmn.setObject(13, null);
        
        stmn.setObject(14, file.getForCC()?"y":"n");
        stmn.setObject(15, file.getContentType());
        
        if ((file.getIdType() != null) && (file.getIdType().longValue() != 0)) stmn.setLong(16, file.getIdType());
        else stmn.setNull(16, java.sql.Types.NUMERIC);
        
        stmn.execute();
        stmn.close();
    }

    @Override
    protected Attachment createImpl(Connection conn, Attachment domainObject) throws SQLException, MappingException {
        return null;
    }

    @Override
    protected Attachment findByPrimaryKeyImpl(Connection conn, Attachment file) throws SQLException, MappingException {
        boolean isExist = false;
        PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_KEY);
        logger.info("[SQL] sql query = " + CMD_FIND_BY_KEY);
        logger.info("[SQL] File unid = " + file.getUnid());
        stmn.setString(1, file.getUnid());
        ResultSet rs = stmn.executeQuery();
        if (rs.next()) {
            isExist = true;
            mapTableRow(file, rs);
        }

        stmn.close();
        return (isExist == true ? file : null);
    }

    @Override
    protected void removeImpl(Connection conn, Attachment domainObject) throws SQLException, MappingException {
        Attachment file = (Attachment) domainObject;
        file = findByPrimaryKey(file);
        if (file.isAccepted() == Attachment.CONST_IS_ACCEPTED) return;

        PreparedStatement stmn = conn.prepareStatement(CMD_REMOVE);
        stmn.setString(1, file.getUnid());
        stmn.execute();
        stmn.close();
    }

    @Override
    protected void updateImpl(Connection conn, Attachment file) throws SQLException, MappingException {
        PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);

        stmn.setString(1, file.getFilename());
        stmn.setString(2, file.getFiletype());
        stmn.setString(3, file.getIdGroup());
        stmn.setString(4, file.getIdOwner());
        stmn.setLong(5, file.getOwnerType());
        
        if (file.getDateOfExpiration() != null) stmn.setTimestamp(6, new Timestamp(file.getDateOfExpiration().getTime()));
        else stmn.setTimestamp(6, null);
        
        if (file.getDateOfAddition() != null) stmn.setTimestamp(7, new Timestamp(file.getDateOfAddition().getTime()));
        else stmn.setTimestamp(7, null);
        
        stmn.setObject(8, file.getWhoAdd());
        stmn.setObject(9, file.isAccepted());
        stmn.setObject(10, file.getWhoAccept());

        if (file.getDateOfAccept() != null) stmn.setTimestamp(11, new Timestamp(file.getDateOfAccept().getTime()));
        else stmn.setTimestamp(11, null);
        
        byte[] signature = file.getSignature() != null ? file.getSignature().getBytes() : null;
        if (signature != null && signature.length > 0) stmn.setBinaryStream(12, new ByteArrayInputStream(signature), signature.length);
        else stmn.setObject(12, null);

        stmn.setString(13, file.getForCC()?"y":"n");
        stmn.setString(14, file.getContentType());
        stmn.setString(15, file.getUnid());

        if ((file.getIdType() != null) && (file.getIdType().longValue() != 0)) stmn.setLong(16, file.getIdType());
        else stmn.setNull(16, java.sql.Types.NUMERIC);

        stmn.executeUpdate();
        stmn.close();
    }

    @Override
    protected List<Attachment> findAllImpl(Connection conn) throws SQLException, MappingException {
        return null;
    }

    @Override
    public List<Attachment> findByOwnerAndType(Attachment findObjects) throws MappingException {
        Connection conn = null;
        List<Attachment> objectList = new ArrayList<Attachment>();
        try {
            conn = getConnection();
            PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_OWNER_AND_TYPE);
            stmn.setString(1, findObjects.getIdOwner());
            stmn.setLong(2, findObjects.getOwnerType());
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                Attachment attach = new Attachment();
                mapTableRow(attach, rs);
                objectList.add(attach);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception caught in sql = " + CMD_FIND_BY_OWNER_AND_TYPE, e);
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in insert()");
        } finally {
            close(conn);
        }
        return objectList;
    }

    protected void mapTableRow(Attachment attach, ResultSet rs) throws SQLException {
        attach.setUnid(rs.getString(FLD_UNID));
        attach.setFilename(rs.getString(FLD_FILENAME));
        attach.setFiletype(rs.getString(FLD_FILETYPE));
        attach.setFilegroup(rs.getString(FLD_FILEGROUP));
        attach.setIdGroup(rs.getString(FLD_ID_GROUP));
        attach.setIdOwner(rs.getString(FLD_ID_OWNER));
        attach.setOwnerType(rs.getLong(FLD_OWNER_TYPE));
        attach.setWhoAdd(rs.getLong(FLD_WHO_ADD));
        attach.setDateOfAddition(rs.getTimestamp(FLD_DATE_OF_ADDITION));
        attach.setDateOfExpiration(rs.getTimestamp(FLD_DATE_OF_EXPIRATION));
        attach.setWhoAccept(rs.getLong(FLD_WHO_ACCEPT));
        attach.setDateOfAccept(rs.getTimestamp(FLD_DATE_OF_ACCEPT));
        attach.setAccepted(rs.getLong(FLD_ISACCEPTED));
        try {
            attach.setForCC(rs.getString(FLD_FORCC).equalsIgnoreCase("y"));
        } catch (Exception e) {
            //LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        attach.setContentType(rs.getString("contentType"));
        attach.setIdType(rs.getLong(FLD_ID_TYPE));
        Blob blobData = rs.getBlob(FLD_SIGNATURE);
        if (blobData != null) {
            try {
                //System.out.println("!!! SIGNATURE is NOT NULL");
                InputStream is = blobData.getBinaryStream();
                byte[] fileData = new byte[(int) blobData.length()];
                is.read(fileData);
                attach.setSignature(new String(fileData));
                //System.out.println("!!! SIGNATURE = "+attach.getSignature());
            } catch (Exception e) {

            }
        }
    }

    @Override
    public LinkedHashMap<String, String> findByOwnerAndKeyType(Long ownerId, String key) throws MappingException {
        Connection conn = null;
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        try {
            conn = getConnection();
            PreparedStatement stmn = conn.prepareStatement(
            		"select c.unid, c.filename, c.date_of_addition from appfiles c "
            	+	" inner join documents_type d on d.id_document_type = c.id_document_type "
            	+   " where c.id_owner = ? and d.key = ? and c.WHO_DEL is null and c.signature is not null " +
                            "and (upper(filename) like '%DOC' or upper(filename) like '%DOCX') "
            	+   " order by c.date_of_addition desc "
            );
            stmn.setString(1, String.valueOf(ownerId));
            stmn.setString(2, key);
            ResultSet rs = stmn.executeQuery();
            while (rs.next())
                map.put(rs.getString("unid"), 
                		convertFileName(rs.getString("filename"), rs.getTimestamp("date_of_addition")));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception caught in findByOwnerAndKeyType()", e);
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in insert()");
        } finally {
            close(conn);
        }
        return map;
    }
    
    /**
     * Removes the path to the file and extension. Leaves only filename 
     * adds date, if present
     * "C:\\Documents and Settings\\Администратор\\Рабочий стол\\2012_02_24_Пилот_Средний_бизнес_описание_v0.2 (котов).doc";
     * transforms to 2012_02_24_Пилот_Средний_бизнес_описание_v0.2 (котов) 18.10.2012 09:40:56
     * @param filename
     * @param dt
     * @return
     */
	private String convertFileName(String filename, Date dt) {
		if ((filename == null) || filename.trim().equals("")) return "";
		try {
			String cut = filename;
			int backslashPosition = filename.lastIndexOf("\\");
			int slashPosition = filename.lastIndexOf("/");
			int max = Math.max(backslashPosition, slashPosition);
			if (max > -1) cut = filename.substring(max + 1);
	
			// remove file extension .doc, .xls etc. 
			String withoutExtension = cut;
			int dotPosition = cut.lastIndexOf(".");
			if (dotPosition > -1) withoutExtension = cut.substring(0, dotPosition); 
			
			// add Date
			String result = withoutExtension.trim();
			if (dt != null) {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				result = withoutExtension.trim() + "  (" + df.format(dt) + ")";
			}
			return result;
		} catch (Exception e) {  
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "";
		}
	}
}
