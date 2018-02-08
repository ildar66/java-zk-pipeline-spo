package ru.md.attachment.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

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

import ru.md.attachment.forms.FormUploadFile;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;

public class ActionUploadFile extends Action {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			                     HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		ActionErrors errors = new ActionErrors();
	   	ActionForward forward = new ActionForward();
	   	FormUploadFile formUploadFileFrame = (FormUploadFile) form;

	   	try {
	   		Attachment attachment = formUploadFileFrame.getUploadedAttachment();
	   		if (attachment != null) {
	   			
	   			WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	   			attachment.setWhoAdd(wsc.getIdUser());
	   			if (attachment.getIdGroup()!= null && attachment.getIdGroup().equals("0"))
	   			    attachment.setIdGroup(null);
	   			
	   			AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");	   			
   				
   				
   				if (formUploadFileFrame.getAttachment() != null) {
   					attachment.setContentType(formUploadFileFrame.getAttachment().getContentType());
   				}
   				
   				attachment = processor.addAttachment(attachment);
   				
   				logger.info("Added file with unid = '" + attachment.getUnid() + "', name = '" + attachment.getFilename() + "'.");

   				if (formUploadFileFrame.getAttachment() != null) {
   					AttachmentFile file = new AttachmentFile(attachment.getUnid());
   					file.setFilename(attachment.getFilename());	   				
   					file.setFiledata(formUploadFileFrame.getAttachment().getFileData());

   					processor.updateAttachmentData(file);
   					
   					logger.info("File with unid = '" + attachment.getUnid()+ "' successfully stored.");
   				}
	   		}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			
			errors.add("Error while uploading file into database", new ActionError("error.message"));
		}
	
		if (!errors.isEmpty()) {
		    saveErrors(request, errors);
		    forward = mapping.findForward("failure");
		} else {	
		    forward = mapping.findForward("result");
		}
		return (forward);
	}
}
