package ru.md.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper;

import com.vtb.util.ApplProperties;

/**
 * url shortener.
 * @author Andrey Pavlenko
 */
public class UrlShortenerAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String report = request.getParameter("rp");
		if (report == null)
			report = "";
		if (report.equals("as")) {
			String newUrl = "reportPrintFormRenderAction.do?__format=html&notused=off&__report="
					+ "file:///" + ApplProperties.getReportsPath() + "Audit/active_stages.rptdesign"
					+ "&isDelinquency=-1&correspondingDeps=on&p_idDepartment=-1&id_ClaimFromList=" 
					+ request.getParameter("id");
			String mdtaskIdStr = request.getParameter("mdtaskId");
			if (mdtaskIdStr == null || mdtaskIdStr.equals(""))
				newUrl += "&p_idClaim=" + request.getParameter("id");
			else
				newUrl += "&mdtaskId=" + mdtaskIdStr;
			response.sendRedirect(newUrl);
			return null;
		}
		if (report.equals("hr")) {
			response
					.sendRedirect("report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1="
							+ request.getParameter("p") + "&menuOff=1");
			return null;
		}
		if (report.equals("sh")) {
			response.sendRedirect("plugin.action.do?class=" + ViewProcessWrapper.class.getName()
					+ "&idProcess=" + request.getParameter("id"));
			return null;
		}

		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		wsc.setErrorMessage("Не правильно заданы параметры. report=" + report);
		return (mapping.findForward("errorPage"));
	}
}
