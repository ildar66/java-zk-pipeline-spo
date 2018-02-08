package org.uit.director.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.crm.dbobjects.ProductQueueJPA;
import ru.md.spo.ejb.CrmFacadeLocal;

import com.vtb.exception.CantChooseProcessType;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

public class CRMLoadProductAction extends Action {
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception { 
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        Long userid = null;
        try {
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            CrmFacadeLocal crmFacade = com.vtb.util.EjbLocator.getInstance().getReference(
                    CrmFacadeLocal.class);
            String id = request.getParameter("id");
            ProductQueueJPA productQueue = crmFacade.getProductQueueById(id);
            if (productQueue.getAccept()!=null && productQueue.getAccept().equals("1")) {
                throw new Exception("Задача по загрузке сделки уже была выполнена " + 
                        Formatter.format(productQueue.getAcceptDate()));
            }
            if (request.getParameter("user")!=null && !request.getParameter("user").equals("null")) 
                userid = Long.valueOf(request.getParameter("user"));
            Long process = null;
            if (request.getParameter("process")!=null)
                process = Long.valueOf(request.getParameter("process"));
            wsc.setPageData("Сделка успешно загружена. Создана заявка номер "+
                    processor.productLoad(id,userid,process));
            return mapping.findForward("textPage");
        } catch (CantChooseProcessType e) {
            ActionForward forward = new ActionForward();
            forward.setPath("processtype.jsp?id="+request.getParameter("id")+
                    "&user="+userid);
            return forward;
        } catch (Exception e) {
            wsc.setErrorMessage(e.getMessage());
            return mapping.findForward("errorPage");
        }
    }
}
