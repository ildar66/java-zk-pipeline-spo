package com.vtb.model.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.SPOSettings;
import com.vtb.ejb.AttachmentActionProcessorFacade;
import com.vtb.ejb.AttachmentActionProcessorFacadeLocal;
import com.vtb.exception.MappingException;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.util.EjbLocator;

public class AttachmentActionProcessorImpl implements AttachmentActionProcessor {

	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	private AttachmentActionProcessor modelFacade = null;
	
	public AttachmentActionProcessorImpl() throws Exception {
		try {
			getAttachmentFacadeLocal();
		} catch (Exception e) {
			try {
				getAttachmentFacade();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
				e1.printStackTrace();
				
				throw new Exception("AttachmentActionProcessorFacade can't found");
			}
		}
	}

	protected void getAttachmentFacade() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(AttachmentActionProcessorFacade.class); 
		} catch (Exception e) {
			throw e;
		}
	}
	
	protected void getAttachmentFacadeLocal() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(AttachmentActionProcessorFacadeLocal.class); 
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public boolean acceptAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		if (modelFacade != null)
			return modelFacade.acceptAttachment(domainObjectWithKeyValues);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public Attachment addAttachment(Attachment file) throws MappingException {
		if (modelFacade != null)
			return modelFacade.addAttachment(file);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public List<Attachment> findAttachemntByOwnerAndType(String id_owner, Long owner_type) throws MappingException {
		if (modelFacade != null)
			return modelFacade.findAttachemntByOwnerAndType(id_owner, owner_type);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public Attachment findAttachemntByPK(Attachment domainObjectWithKeyValues) throws MappingException {
		if (modelFacade != null)
			return modelFacade.findAttachemntByPK(domainObjectWithKeyValues);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public boolean removeAttachment(Attachment domainObjectWithKeyValues) throws MappingException {
		if (modelFacade != null)
			return modelFacade.removeAttachment(domainObjectWithKeyValues);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public Attachment updateAttachment(Attachment file) throws MappingException {
		if (modelFacade != null)
			return modelFacade.updateAttachment(file);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public AttachmentFile updateAttachmentData(AttachmentFile file) throws MappingException {
		if (modelFacade != null)
			return modelFacade.updateAttachmentData(file);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Override
	public AttachmentFile findAttachmentDataByPK(AttachmentFile file) throws MappingException {
		if (modelFacade != null)
			return modelFacade.findAttachmentDataByPK(file);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}

	@Deprecated
	@Override
	public SPOSettings findSettingsForWAS() throws MappingException {
		if (modelFacade != null)
			return modelFacade.findSettingsForWAS();
		else {
			throw new MappingException("AttachmentActionProcessorFacade can't found");
		}
	}

	@Override
	public LinkedHashMap<String, String> findByOwnerAndKeyType(Long ownerId, String key) throws MappingException {
		if (modelFacade != null)
			return modelFacade.findByOwnerAndKeyType(ownerId, key);
		else
			throw new MappingException("AttachmentActionProcessorFacade can't found");
	}
}
