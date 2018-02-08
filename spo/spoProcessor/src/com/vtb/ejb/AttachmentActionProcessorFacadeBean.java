package com.vtb.ejb;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.SPOSettings;
import com.vtb.exception.MappingException;
import com.vtb.mapping.Mapper;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.jdbc.AttachmentMapper;
import com.vtb.mapping.jdbc.SPOSettingsMapper;
import com.vtb.util.Converter;

/**
 * Bean implementation class for Enterprise Bean:
 * AttachmentActionProcessorFacade
 */
public class AttachmentActionProcessorFacadeBean implements AttachmentActionProcessorFacade, AttachmentActionProcessorFacadeLocal {

	private static final long serialVersionUID = 1L;
	
	private transient final Logger LOGGER = Logger.getLogger(AttachmentActionProcessorFacadeBean.class.getName());
	
	@Override
	public Attachment addAttachment(Attachment file) throws MappingException {
		Mapper<Attachment> mapper = null;
		try {
			String unid = file.getUnid();
			file.setDateOfAddition(Converter.toTimetamp(new java.util.Date()));
			if (unid == null || unid.equals("")) {
				file.setUnid(UUID.randomUUID().toString());
			}
			LOGGER.info("Generated new unid = " + file.getUnid());
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in insert: Can't generate ID value" + e.getMessage()));
		}
		mapper = MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
        if (mapper == null) {
        	LOGGER.info("Include ReserveMapper for Attachment");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);
        }
		mapper.insert(file);
		return file;
	}

	@Override
	public Attachment updateAttachment(Attachment file) throws MappingException {
		Mapper<Attachment> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);			
		}
		mapper.update(file);
		return file;
	}

	@Override
	public boolean removeAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		Mapper<Attachment> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);						
		}
		mapper.remove(domainObjectWithKeyValues);
		return true;
	}

	@Override
	public boolean acceptAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		Mapper<Attachment> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);									
		}

		Attachment file = mapper.findByPrimaryKey(new Attachment(domainObjectWithKeyValues.getUnid()));
		if (file != null && file.isAccepted() == Attachment.CONST_NOT_ACCEPTED) {
			file.setAccepted(Attachment.CONST_IS_ACCEPTED);
			file.setWhoAccept(domainObjectWithKeyValues.getWhoAccept());
			file.setDateOfAccept(new Date());
			mapper.update(file);
		} else if (file == null) {
			throw new MappingException("Exception caught in acceptAttachment: can't find object with unid["
					+ domainObjectWithKeyValues.getUnid() + "].");
		}
		else {
			LOGGER.info("Object with unid[" + domainObjectWithKeyValues.getUnid() + "] already accepted.");
		}
		return true;
	}

	@Override
	public Attachment findAttachemntByPK(Attachment domainObjectWithKeyValues) throws MappingException {
		Mapper<Attachment> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);									
		}
		return mapper.findByPrimaryKey(domainObjectWithKeyValues);
	}

	@Override
	public List<Attachment> findAttachemntByOwnerAndType(String idOwner, Long ownerType) throws MappingException {
		AttachmentMapper mapper;
		Attachment fileFind = new Attachment(idOwner, ownerType);
		mapper = (AttachmentMapper) MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = (AttachmentMapper) MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);									
		}
		try {
			return mapper.findByOwnerAndType(fileFind);
		} catch (Exception e) {
			throw new MappingException(e, e.getMessage());
		}
	}

	@Override
	public LinkedHashMap<String, String> findByOwnerAndKeyType(Long ownerId, String key) throws MappingException	{
		AttachmentMapper mapper = (AttachmentMapper) MapperFactory.getSystemMapperFactory().getMapper(Attachment.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for Attachment");
			mapper = (AttachmentMapper) MapperFactory.getReserveMapperFactory().getMapper(Attachment.class);									
		}
		try {
			return mapper.findByOwnerAndKeyType(ownerId, key);
		} catch (Exception e) {
			throw new MappingException(e, e.getMessage());
		}
	}

	@Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AttachmentFile updateAttachmentData(AttachmentFile file) throws MappingException {
		Mapper<AttachmentFile> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(AttachmentFile.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for AttachmentFile");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(AttachmentFile.class);
		}
		mapper.update(file);
		return file;
	}

	@Override
	public AttachmentFile findAttachmentDataByPK(AttachmentFile file) throws MappingException {
		Mapper<AttachmentFile> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(AttachmentFile.class);
		if (mapper == null) {
			LOGGER.info("Include ReserveMapper for AttachmentFile");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(AttachmentFile.class);			
		}
		return mapper.findByPrimaryKey(file);
	}

	@Deprecated
	@Override
	public SPOSettings findSettingsForWAS() throws MappingException {
		Mapper<SPOSettings> mapper = null;
		mapper = MapperFactory.getSystemMapperFactory().getMapper(SPOSettings.class);
        if (mapper == null) {
        	LOGGER.info("Include ReserveMapper for SPOSettings");
			mapper = MapperFactory.getReserveMapperFactory().getMapper(SPOSettings.class);
        }
		return ((SPOSettingsMapper) mapper).findMainSettings();		
	}
}
