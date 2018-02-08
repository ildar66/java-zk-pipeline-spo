package com.vtb.model;

import java.util.LinkedHashMap;
import java.util.List;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.SPOSettings;
import com.vtb.exception.MappingException;

/**
 * Interface predestine for: - working with file (without BLOB data); - working
 * with request on file;
 * 
 * @author Michel Tormozov
 */
public interface AttachmentActionProcessor {
	
	/**
	 * add file (attribute) to DB storage
	 * 
	 * @param file
	 *            - file that add to storage
	 * @return Attachemnt object with the UNID value
	 */
	public Attachment addAttachment(Attachment file) throws MappingException;

	/**
	 * update file (attribute) in DB storage
	 * 
	 * @param file
	 *            - file that updated in storage
	 * @return Attachemnt object with the UNID value
	 */
	public Attachment updateAttachment(Attachment file) throws MappingException;

	/**
	 * remove file from DB storage
	 * 
	 * @param file
	 *            - file that removed form storage (need contain only PK)
	 * @return Attachemnt object with the UNID value
	 */
	public boolean removeAttachment(Attachment domainObjectWithKeyValues) throws MappingException;

	/**
	 * mark file as accepted
	 * 
	 * @param file
	 *            - file that marked as accepted
	 * @return Attachemnt object with the UNID value
	 */
	public boolean acceptAttachment(Attachment domainObjectWithKeyValues) throws MappingException;

	/**
	 * find file in DB storage by PK
	 * 
	 * @param file
	 *            - file that found (only PK needed)
	 * @return Attachemnt object
	 */
	public Attachment findAttachemntByPK(Attachment domainObjectWithKeyValues) throws MappingException;

	/**
	 * find file in DB storage by ID OWNER (object that owns the file )
	 * 
	 * @param idOwner
	 *            - ID of APPLICATION or ID of CONTRACTOR (it depends from
	 *            parameter ID_TYPE)
	 * @param id_type
	 *            - ID_OWNER type (APPLICATION/CONTRACTOR)
	 * @return List of Attachemnt objects
	 */
	public List<Attachment> findAttachemntByOwnerAndType(String idOwner, Long ownerType) throws MappingException;

	@Deprecated
	public SPOSettings findSettingsForWAS() throws MappingException;

	/**
	 * This method update file data for the attached file
	 * 
	 * @param file
	 *            - request on file that need to be put to queue
	 * @throws MappingException
	 */
	public AttachmentFile updateAttachmentData(AttachmentFile file) throws MappingException;

	/**
	 * Find attachment data by UNID
	 * 
	 * @param file
	 *            - attachment object with unid
	 * @throws MappingException
	 */
	public AttachmentFile findAttachmentDataByPK(AttachmentFile file) throws MappingException;

	/**
     * Returns map of attachments (to use in drop-down lists) of the given owner (process id) and document type business key 
     * order by time of creation (latest first)  
     * @param ownerId owner process id
     * @param key document type business key
     * @return map of attachments (to use in drop-down lists) of the given owner (process id) and document type business key
     * @throws MappingException
     */
    public LinkedHashMap<String, String> findByOwnerAndKeyType(Long ownerId, String key) throws MappingException;
}
