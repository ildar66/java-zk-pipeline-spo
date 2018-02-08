package org.uit.director.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;


public class PluginAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        String target;


        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));

//        String className = (String) request.getAttribute("class");
        String className = (String) request.getParameter("class");

        if (className.equals("")) {
            target = "pageContextTask";

        } else
            try {

                Class classPlugin = Class.forName(className);
                PluginInterface pluginInterface;
                pluginInterface = (PluginInterface) classPlugin.newInstance();
                List par = new ArrayList();
                par.add(mapping);
                par.add(form);
                par.add(request);
                par.add(response);                
                pluginInterface.init(wsc, par);
                target = pluginInterface.execute();


            } catch (Exception e) {
                e.printStackTrace();
                wsc.setErrorMessage("Ошибка выполнения действия.");
                target = "errorPage";

            }
            
        if (target == null ) return null;    
        return (mapping.findForward(target));
    }
}
