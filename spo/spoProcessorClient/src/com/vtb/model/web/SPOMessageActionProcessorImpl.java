package com.vtb.model.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.ejb.SPOMessageActionProcessorFacade;
import com.vtb.ejb.SPOMessageActionProcessorFacadeLocal;
import com.vtb.exception.MappingException;
import com.vtb.model.SPOMessageActionProcessor;
import com.vtb.util.EjbLocator;

public class SPOMessageActionProcessorImpl implements SPOMessageActionProcessor {

	SPOMessageActionProcessor modelFacade = null;

	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	public SPOMessageActionProcessorImpl() throws Exception {
		try {
			getSPOMessageFacadeLocal();
		} catch (Exception e) {
			try {
				getSPOMessageFacade();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
				e1.printStackTrace();
				
				throw new Exception("SPOMessageActionProcessorFacade can't found");
			}
		}
	}

	protected void getSPOMessageFacade() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(SPOMessageActionProcessorFacade.class);
		} catch (Exception e) {
			throw e;
		}
	}

	protected void getSPOMessageFacadeLocal() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(SPOMessageActionProcessorFacadeLocal.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override @Deprecated
	public void send(String senderMail, String senderName, String recipients, String subject, String body) throws MappingException {
		if (modelFacade != null)
			modelFacade.send(senderMail,senderName, recipients, subject, body);
		else
			throw new MappingException("SPOMessageActionProcessorFacade can't found");
		
	}
}
