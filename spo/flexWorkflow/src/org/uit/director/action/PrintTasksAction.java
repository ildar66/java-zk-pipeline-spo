/**
 *  Created by Struts Assistant.
 *  Date: 26.05.2006  Time: 08:57:28
 */

package org.uit.director.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.tasks.TaskInfo;
import org.uit.director.tasks.TaskList;

public class PrintTasksAction extends org.apache.struts.action.Action {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        String target = "textPage";

        String idTask = request.getParameter("p0");

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext()) return (mapping.findForward("start"));


        try {

            int i = 1;
            TaskList taskList = wsc.getTaskList();
            StringBuffer sb = new StringBuffer();
            sb.append("<SCRIPT LANGUAGE=\"JavaScript\">\n").
                    append("function varitext(text){\n").
                    append("text=document\n").
                    append("print(text)\n").
                    append("}\n").
                    append("</script>");
            sb.append("<div class=\"tabledata\">\n").
                    append("<table width=\"100%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            String lastHeadAttr = "";


            while (idTask != null) {
                TaskInfo taskInfo = taskList.findTaskInfo(Long.valueOf(idTask));
                if (taskInfo != null) {
                    List<BasicAttribute>  atrView = taskInfo.getAttributes().getAttrStructures();

                    sb.append("<tr>");
                    String headNameAttr = "";
                    String valuesAttr = "";

                    for (int j = 0; j < atrView.size(); j++) {
                        Attribute attribute =  atrView.get(j).getAttribute();
                        headNameAttr += "<th>" + attribute.getNameVariable() + "</th>";
                        valuesAttr += "<td>" + attribute.getValueAttributeString() + "</td>";
                    }

                    if (!headNameAttr.equals(lastHeadAttr)) {
                        sb.append(headNameAttr);
                        sb.append("</tr><tr>");
                        lastHeadAttr = headNameAttr;
                    }

                    sb.append(valuesAttr);


                    sb.append("</tr>");
                }
                idTask = request.getParameter("p" + i++);
            }

            sb.append("</table></div><br>\n").
                    append("\n").
                    append("<DIV ALIGN=\"CENTER\">\n").
                    append("<FORM>\n").
                    append("<INPUT NAME=\"print\" TYPE=\"button\" VALUE=\"Вывести на печать!\"\n").
                    append("ONCLICK=\"varitext()\">\n").
                    append("</FORM>\n").
                    append("</DIV>\n").
                    append("<br>");

            wsc.setPageData(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка выполнения операции.");
            target = "errorPage";

        }

        return (mapping.findForward(target));

    }
}