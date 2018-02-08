/**
 *  Created by Struts Assistant.
 *  Date: 28.07.2006  Time: 12:02:43
 */

package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

public class CloseAction extends org.apache.struts.action.Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String target = "ok";

		WorkflowSessionContext wsc = (WorkflowSessionContext) request
				.getSession().getAttribute("workflowContext");
		if (wsc != null) {

			try {

				wsc.release();
				wsc = null;
				// request.getSession().setMaxInactiveInterval(1);

			} catch (Exception e) {
				e.printStackTrace();
		

			} catch (Throwable throwable) {
				throwable.printStackTrace(); // To change body of catch
												// statement use File | Settings
												// | File Templates.
			}
		}

		return mapping.findForward(target);
	}
}