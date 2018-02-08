package com.vtb.mapping.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.vtb.domain.AttachmentFile;
import com.vtb.exception.MappingException;

public class AttachmentFileMapper extends JDBCMapperExt<AttachmentFile> implements com.vtb.mapping.AttachmentFileMapper {
	private static final Logger LOGGER = Logger.getLogger(AttachmentFileMapper.class.getName());
	
    private final String TABLE = "Appfiles";
	private final String FLD_UNID = "unid";
	private final String FLD_FILENAME = "filename";
	private final String FLD_FILEDATA = "filedata";
	
	private final String CMD_UPDATE = "update "+TABLE+ " set "+
								FLD_FILENAME+"=?, "+
								FLD_FILEDATA+"=? "+ 
								" where "+ FLD_UNID+"=?";
	private final String CMD_REMOVE = "delete from "+TABLE+" where "+FLD_UNID+"=?";
	private final String CMD_FIND_BY_KEY = "select "+
									FLD_FILENAME+", "+
									FLD_FILEDATA+" "+
									" from "+TABLE+
									" where "+FLD_UNID+"=?";
	
	
	@Override
	protected List<AttachmentFile> findAllImpl(Connection conn) throws SQLException,
			MappingException {
		throw new MappingException("ERROR: incorrect using of AttacmentFile object");
	}

	@Override
	protected void insertImpl(Connection conn, AttachmentFile anObject)
			throws SQLException, MappingException {
		throw new MappingException("ERROR: incorrect using of AttacmentFile object");
	}

	@Override
	protected AttachmentFile createImpl(Connection conn, AttachmentFile domainObject)
			throws SQLException, MappingException {
		throw new MappingException("ERROR: incorrect using of AttacmentFile object");
	}

	@Override
	public AttachmentFile findByPrimaryKeyImpl(Connection conn, AttachmentFile file) throws MappingException {
		try {
			boolean isExist = false;
	        PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_KEY);
	        stmn.setString(1, file.getUnid());
	        ResultSet rs = stmn.executeQuery();
	        if (rs.next()) {
	            isExist = true;
	            file.setFilename(rs.getString(FLD_FILENAME));
	            Blob blobData = rs.getBlob(FLD_FILEDATA);
	            if (blobData != null) {
	                try {
	                    InputStream is = blobData.getBinaryStream();
	                    byte[] fileData = new byte[(int) blobData.length()];
	                    is.read(fileData);
	                    file.setFiledata(fileData);
	                } catch (Exception e) {
	
	                }
	            } else
	                file.setFiledata(null);
	
	        }
	        stmn.close();
	        return (isExist == true ? file : null);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new MappingException(e, e.getMessage());
		}
    }

	@Override
	public void removeImpl(Connection conn, AttachmentFile domainObject) throws MappingException {
		try {
	        AttachmentFile file = (AttachmentFile) domainObject;
	        PreparedStatement stmn = conn.prepareStatement(CMD_REMOVE);
	
	        stmn.setString(1, file.getUnid());
	
	        stmn.execute();
	        stmn.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new MappingException(e, e.getMessage());
		}
    }
	

	@Override
	public void updateImpl(Connection conn, AttachmentFile anObject) throws MappingException {
		try {
	        AttachmentFile file = (AttachmentFile) anObject;
	        PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);
	
	        stmn.setString(1, file.getFilename());
	        byte[] fileData = file.getFiledata();
	        stmn.setBinaryStream(2, new ByteArrayInputStream(fileData), fileData.length);
	        stmn.setString(3, file.getUnid());
	
	        stmn.executeUpdate();
	        stmn.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new MappingException(e, e.getMessage());
		}
    }
}
