package org.uit.director.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;


public class DeleteRecordAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        String target = "ok";
        String table = request.getParameter("table");
        String id = request.getParameter("id");


        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));


        try {
            if (id != null && table != null) {

                String idTable = "";
                if (table.equalsIgnoreCase("STAGES")) idTable = "ID_STAGE";
                if (table.equalsIgnoreCase("VARIABLES")) idTable = "ID_VAR";
                String sql = "delete from DB2ADMIN." + table + " t where t." + idTable + "=" + id;
                wsc.getDbManager().getDbFlexDirector().execQuery(sql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (mapping.findForward(target));
    }
}
