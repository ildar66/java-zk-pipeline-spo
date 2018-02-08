package com.vtb.model.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.SpoHistory;
import com.vtb.ejb.CRMActionProcessorFacade;
import com.vtb.ejb.CRMActionProcessorFacadeLocal;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.model.CRMActionProcessor;
import com.vtb.util.EjbLocator;

/**
 * Процессор для исполнения заявок 
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-252
 */
public class CRMActionProcessorImpl implements CRMActionProcessor {

    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    
    CRMActionProcessor modelFacade = null;
	
	public CRMActionProcessorImpl() throws Exception {
		try {
			getFacadeLocal();
		} catch (Exception e) {
			try {
				getFacade();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
				e1.printStackTrace();
				
				throw new Exception("CRMActionProcessorFacade can't found");
			}
		}
	}
    
    protected void getFacade() throws Exception {
        try {      
        	modelFacade = EjbLocator.getInstance().getReference(CRMActionProcessorFacade.class); 
        } catch (Exception e) {
        	throw e;
        }
    }
    
    protected void getFacadeLocal() throws Exception {
        try {        
        	modelFacade = EjbLocator.getInstance().getReference(CRMActionProcessorFacadeLocal.class); 
        } catch (Exception e) {
        	throw e;
        }
    }

    public void insertSpoHistory(SpoHistory spoHistory) throws DuplicateKeyException, MappingException {
    	modelFacade.insertSpoHistory(spoHistory);
    }

    public SpoHistory findSpoHistoryByPrimaryKey(SpoHistory spoHistory) throws NoSuchObjectException, MappingException {
        return modelFacade.findSpoHistoryByPrimaryKey(spoHistory);
    }

    public void removeSpoHistory(SpoHistory spoHistory) throws MappingException {
    	modelFacade.removeSpoHistory(spoHistory);
    }
}
