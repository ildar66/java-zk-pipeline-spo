package ru.md.servlet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;
/**
 * возвращает дочерние элементы (сублимиты и сделки) этой заявкм (сублимита или лимита).
 * @author Andrey Pavlenko
*/
public class LimitTreeAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        TaskJPA task = taskFacade.getTask(Long.valueOf(request.getParameter("id")));
        String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>";
        String format = "<task><id>{0}</id><type>{1}</type><number>{2}</number>" +
        		"<org>{3}</org><sum>{4}</sum><period>{5}</period><hasChild>{6}</hasChild><hasSublimits>{7}</hasSublimits>" +
        		"<showDel>{8}</showDel><title>{9}</title></task>\n";
        List<TaskJPA> tasks = new ArrayList<TaskJPA>();
        if(request.getParameter("child").equals("true")){ tasks = task.getChilds();} else {tasks.add(task);} 
        String sublimits = "";
        for(TaskJPA child : tasks){
            if(child.isDeleted()) 
            	continue;
            if(child.isProduct()) {
            	if (child.getProcessId() == null) 
            		continue; 
            	/*Long lastSpoConfirmedCreditDealId = SBeanLocator.singleton().mdTaskMapper().getLastSpoConfirmedCreditDealId(child.getMdtask_number());
            	if (!child.getId().equals(lastSpoConfirmedCreditDealId))
            		continue;*/
            }
            
            boolean hasChild = false;boolean hasSublimits = false;
            for(TaskJPA c : child.getChilds()){if(!c.isDeleted())hasChild=true;}
            for(TaskJPA c : child.getChilds()){if(!c.isDeleted()&&!c.isProduct())hasSublimits=true;}
            sublimits = MessageFormat.format(format, child.getId().toString(),child.getType(),child.getNumberDisplay(),
        			child.getOrganisation(),child.getSumWithCurrency(),child.getPeriodFormated(),hasChild,
        			hasSublimits,child.getType().equals("Сублимит")&&!child.hasProductInHerarhy(),child.getTitle()) + sublimits;
        }
        ans += sublimits + "<parent>" + task.getId().toString() + "</parent></root>";
        response.getWriter().write(ans);
        logger.info("AJAX limittree.do answer: "+ans);
        return null;
    }
}
