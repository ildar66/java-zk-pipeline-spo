package com.vtb.mapping;

import java.util.LinkedHashMap;
import java.util.List;

import com.vtb.domain.Attachment;
import com.vtb.exception.MappingException;

public interface AttachmentMapper extends Mapper<Attachment> {

    public List<Attachment> findByOwnerAndType(Attachment findObjects) throws MappingException;

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
