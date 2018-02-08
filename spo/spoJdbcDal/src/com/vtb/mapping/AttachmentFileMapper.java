package com.vtb.mapping;

import java.sql.Connection;

import com.vtb.domain.AttachmentFile;
import com.vtb.exception.MappingException;

public interface AttachmentFileMapper extends Mapper<AttachmentFile> {
    
	public AttachmentFile findByPrimaryKeyImpl(Connection conn, AttachmentFile file) throws MappingException;

	public void removeImpl(Connection conn, AttachmentFile domainObject) throws MappingException;
	
	public void updateImpl(Connection conn, AttachmentFile anObject) throws MappingException;
}
