package ru.md.attachment.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.attachment.forms.AttachmentsListForm;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.custom.AttachmentDisplay;
import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.InfoMessage;
import com.vtb.domain.Operator;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.util.ApplProperties;

public class AttachmentListAction extends Action {

	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionErrors errors = new ActionErrors();
        ActionForward forward = new ActionForward(); // return value
        AttachmentsListForm formData = (AttachmentsListForm) form;

        try {
            User operator = getCurrentOperator(request);

            if (formData.getAction().equals(AttachmentsListForm.ACTION_REMOVE)) {
                removeAttachments(formData, operator, request);
            }
            if (formData.getAction().equals(AttachmentsListForm.ACTION_ACCEPT)) {
                acceptAttachments(formData, operator);
            }

            List<Attachment> attachList = findAttachemnts(formData, operator);
            request.setAttribute("attachments", attachList);
        } catch (Exception e) {
            errors.add("name", new ActionError("id"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        forward = mapping.findForward("success");
        return (forward);
    }

    /**
     * Forms link for file download
     */
    private String getFileDownloadLink(String unid, String filename, User operator) {
        String url = "#";
        try {
            url = "/"+ApplProperties.getwebcontextFWF()+"/download.do?unid=" + unid;
        } catch (Exception e) {
        	url = "#";
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            e.printStackTrace();
        }
        return url;
    }

    private User getCurrentOperator(ServletRequest request) {
        User operator = null;
        try {
            WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext((HttpServletRequest) request);
            CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
            operator = compenduim.getUser(new User(wsc.getIdUser()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't find user. " + e.getMessage(), e);
            e.printStackTrace();
        }
        return operator;
    }

    private void removeAttachments(AttachmentsListForm formData, User operator, HttpServletRequest request) throws Exception {
    	AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
    	
        String[] unids = formData.getUnid();
        InfoMessage infoMessage = null;
        ArrayList<InfoMessage> infoMessages = new ArrayList<InfoMessage>(0);
        
        if (unids != null) {
            for (int i = 0; i < unids.length; i++) {
                try {
                    Attachment delAttachment = new Attachment(unids[i]);
                    delAttachment = processor.findAttachemntByPK(delAttachment);
                    
                    if (delAttachment.getWhoAdd().equals(operator.getId()) 
                    	|| delAttachment.getWhoAccept().equals((operator.getId())))
                    	processor.removeAttachment(delAttachment);
                    else {
                    	LOGGER.info("operator " + operator.getLogin() + " cannot remove file");
                    	
                    	infoMessage = new InfoMessage(0, "Вы не можете удалить документ '" + delAttachment.getFilename() + "', который Вы не прикрепляли");
                    	infoMessages.add(infoMessage);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Cannot remove file", e);
                }
            }
            
            if (infoMessages.size() > 0)
            	request.setAttribute("messages", infoMessages);
        }
    }

    private void acceptAttachments(AttachmentsListForm formData, User operator) throws Exception {
        String[] unids = formData.getUnid();
        AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
        if (unids != null) {
            for (int i = 0; i < unids.length; i++) {
                try {
                    Attachment attach = new Attachment(unids[i]);
                    attach.setWhoAccept(operator.getId());
                    processor.acceptAttachment(attach);
                } catch (Exception e) {
                	LOGGER.log(Level.WARNING, "Cannot accept file", e);
                }
            }
        }
    }

    private List<Attachment> findAttachemnts(AttachmentsListForm formData, User operator) throws Exception {
        long startTime = (new Date()).getTime();
        List<Attachment> outList = new ArrayList<Attachment>();
    	
    	try {
	    	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	    	
	        List<AttachJPA> attachList = pupFacadeLocal.findAttachemntByOwnerAndType(formData.getId(), Long.valueOf(formData.getType()));
	        
	        LOGGER.info("get list of attachments for id owner '" + formData.getId() + "' and type '" + formData.getType() + "'");
	        
	        if (attachList != null) {
	            for (AttachJPA attach : attachList) {
	                String url = getFileDownloadLink(attach.getUnid(), attach.getFILENAME(), operator);//долгая процедура
	                
	                LOGGER.info("url '" + url + "' for download file '" + attach.getFILENAME() + "'");
	                
	                AttachmentDisplay attachDisp = new AttachmentDisplay();
	                attachDisp.setUnid(attach.getUnid());

	                if (attach.getGroup()!=null){
    	                attachDisp.setFilegroup(attach.getGroup().getNAME_DOCUMENT_GROUP());
    	                attachDisp.setIdGroup(attach.getGroup().getId().toString());
	                }
	                attachDisp.setFiletype(attach.getFILETYPE());
	                attachDisp.setFilename(attach.getFILENAME());
	                attachDisp.setIdOwner(attach.getID_OWNER());
	                attachDisp.setOwnerType(attach.getOWNER_TYPE());
	                attachDisp.setSignature(attach.getSIGNATURE());
	                
	                attachDisp.setDateOfAddition(attach.getDATE_OF_ADDITION());
	                attachDisp.setDateOfExpiration(attach.getDATE_OF_EXPIRATION());
	                
	                attachDisp.setAccepted(attach.getISACCEPTED());
	                attachDisp.setDateOfAccept(attach.getDATE_OF_ACCEPT());
	                attachDisp.setIdType(attach.getDocumentType().getId());
	                attachDisp.setDownloadLink(url);
	                attachDisp.setForCC(attach.getFORCC().equalsIgnoreCase("y"));
	                
                    if (attach.getWhoAdd() != null) {
                        attachDisp.setWhoAdd(attach.getWhoAdd().getIdUser());
                        attachDisp.setWhoAddName(attach.getWhoAdd().getFullName());
                    }
                    
                    if (attach.getWhoAccepted() != null) {
                        attachDisp.setWhoAccept(attach.getWhoAccepted().getIdUser());
                        attachDisp.setWhoAcceptName(attach.getWhoAccepted().getFullName());
                    }
	                
	                outList.add(attachDisp);
	            }
	        }
	        
	    } catch (Exception e) {
	    	throw e;
	    }
	    
	    long ttl = (new Date()).getTime() - startTime;
        LOGGER.info("ttl: "+ttl);
        
	    return outList;
    }
}
