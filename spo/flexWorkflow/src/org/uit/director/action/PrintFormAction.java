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
/**
 * Формирует печатную форму.
 * @author Andrey Pavlenko
 *
 */
public class PrintFormAction extends Action {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long mdtaskid=new Long(request.getParameter("mdtask"));
		String xml=request.getParameter("xml");
		logger.info("getting print version for mdtaskid="+mdtaskid);
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		Task task=processor.getTask(new Task(mdtaskid));
        //отчет
        String projResol = processor.getReport(task,xml!=null,100);
        response.setContentType("text/html;charset=UTF-8");
        OutputStream os = response.getOutputStream();
		os.write(projResol.getBytes("UTF-8"));
		os.close();
		return null;
	}
}
