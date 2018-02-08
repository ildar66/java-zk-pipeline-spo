package ru.md.servlet;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
/**
 * возвращает дочерние элементы (сублимиты и сделки) этой заявкм (сублимита или лимита).
 * @author Andrey Pavlenko
*/
public class AddSublimitAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.info("AddSublimitAjaxAction. create sublimits "+request.getParameter("id"));
        logger.info("org="+request.getParameter("org"));
        //создаем нашу модель данных
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        try {
            Task task = new Task(null);
            Task parent = processor.getTask(new Task(new Long(request.getParameter("id"))));
            task.setParent(parent.getId_task());
            task.getContractors().add(new TaskContractor(new Organization(request.getParameter("org")), 
            		new ArrayList<ContractorType>(), null));
            //по умолчанию для нового саблимита берем ежемесячные уплата процентов
            task.getHeader().setIdLimitType(1);
            task.getHeader().setStartDepartment(parent.getHeader().getStartDepartment());
            task.getHeader().setPlace(parent.getHeader().getPlace());
            task.getMain().setCurrency(parent.getMain().getCurrency2());
            task.setDeleted(false);//саблимит не будет живым, пока не нажмем сохранить
            // для сублимита значения наследуются из значений вышестоящего лимита
            if (parent.getMain().isRenewable()) {
                task.getMain().setRenewable(true);
                task.getMain().setMayBeRenewable(true);
            } else {
                task.getMain().setRenewable(false);
                task.getMain().setMayBeRenewable(false);    
            }
            task.getHeader().setNumber(pupFacadeLocal.getNextSublimitNumber(task.getParent()));
            // VTBSPO-204 версия сублимита берется из родительского лимита
            task.getHeader().setVersion(parent.getHeader().getVersion());
            task=processor.createTask(task);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        response.getWriter().write("OK");
        return null;
    }
}
