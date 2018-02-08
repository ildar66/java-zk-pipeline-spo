package org.uit.director.action;

import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

public class GetVersionAction extends Action {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long idversion = new Long(request.getParameter("versionid"));
		logger.info("read versionid " + idversion);
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		String report = processor.getVersion(idversion);
        response.setContentType("text/html;charset=UTF-8");
        OutputStream os = response.getOutputStream();
		os.write(report.getBytes("UTF-8"));
		os.close();
		return null;
	}
}
