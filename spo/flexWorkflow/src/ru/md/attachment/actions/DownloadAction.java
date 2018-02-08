package ru.md.attachment.actions;

import java.io.OutputStream;
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

import ru.md.attachment.forms.FormUploadFile;

import com.vtb.domain.Attachment;
import com.vtb.domain.AttachmentFile;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;

/**
 * @version 1.0
 * @author
 */
public class DownloadAction extends Action

{

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionErrors errors = new ActionErrors();
		FormUploadFile formUploadFileFrame = (FormUploadFile) form;

		try {
			AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
			
			Attachment attachment = new Attachment(formUploadFileFrame.getUnid());
			attachment = processor.findAttachemntByPK(attachment);
		
			AttachmentFile file = new AttachmentFile(formUploadFileFrame.getUnid());
			file = processor.findAttachmentDataByPK(file);

			logger.info("start download");

			String fileName = new String(file.getFilename().getBytes("CP1251"), "CP1252");
			if(fileName!=null)
				fileName = fileName.replaceAll("\"", "").replaceAll("“", "").replaceAll("”", "");
			String mimeType = attachment.getContentType();

			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentLength(file.getFiledata().length);
			response.setContentType(mimeType);
			OutputStream os = response.getOutputStream();
			os.write(file.getFiledata());
			os.close();

			logger.info("download completed");

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
			
			errors.add("name", new ActionError("id"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		} else {}

		return (null);

	}

}
