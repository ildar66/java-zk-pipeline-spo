package org.uit.director.action;

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

import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

public class DownloadAction extends Action {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionErrors errors = new ActionErrors();

		try {
		    String id_template=request.getParameter("id_template");
			String id_mdtask=request.getParameter("id_mdtask");
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			byte[] data=processor.getResolution(new Long(id_template),new Long(id_mdtask));
			String fileName = "resolution.doc";
			String mimeType = "application/msword";
			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
			response.setContentLength(data.length);
			response.setContentType(mimeType);
			OutputStream os = response.getOutputStream();
			os.write(data);
			os.close();
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
