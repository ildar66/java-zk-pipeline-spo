package com.vtb.model.ejb;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.FileRequest;
import com.vtb.domain.SPOSettings;
import com.vtb.exception.MappingException;
import com.vtb.model.AttachmentActionProcessor;

public class AttachmentActionProcessorImpl implements AttachmentActionProcessor {

	/**
	 * {@inheritDoc}
	 */
	public void reinit() throws Exception {};
	
	public boolean acceptAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		return false;
	}

	public Attachment addAttachment(Attachment file) throws MappingException {
		return null;
	}

	public FileRequest addRequest(FileRequest request) throws MappingException {
		return null;
	}

	public SPOSettings addSpoSettings(SPOSettings settings) throws MappingException {
		return null;
	}

	public void completeRequest(FileRequest request, Integer status) throws MappingException {
	}

	public ArrayList<Attachment> findAttachemntByOwnerAndType(String id_owner, Long owner_type) throws MappingException {
		return null;
	}

	public Attachment findAttachemntByPK(Attachment domainObjectWithKeyValues) throws MappingException {
		return null;
	}

	public ArrayList<FileRequest> findNotCompletedRequest() throws MappingException {
		return null;
	}

	public SPOSettings findSpoSettings(long id_department) throws MappingException {
		return null;
	}

	public boolean removeAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		return false;
	}

	public boolean removeSpoSettings(SPOSettings domainObjectWithKeyValues) throws MappingException {
		return true;
	}

	public Attachment updateAttachment(Attachment file) throws MappingException {
		return null;
	}

	public SPOSettings updateSpoSettings(SPOSettings settings) throws MappingException {
		return null;
	}

	public boolean removeRequest(FileRequest request) throws MappingException {
		return false;
	}

	public FileRequest findNotCompletedRequestByUnidAndDep(String unid, long id_department) throws MappingException {
		return null;
	}

	public void putRequestedFileToMQ(FileRequest request) throws MappingException {
	}

	public AttachmentFile updateAttachmentData(AttachmentFile file) throws MappingException {
		return null;
	}

	public AttachmentFile findAttachmentDataByPK(AttachmentFile file) throws MappingException {
		return null;
	}

	public boolean getReceivedFileToDB() throws MappingException {
		return false;
	}

	public SPOSettings findSettingsForWAS() throws MappingException {
		return null;
	}

	@Override
	public LinkedHashMap<String, String> findByOwnerAndKeyType(Long ownerId, String key) throws MappingException {
		return null;
	}

}
