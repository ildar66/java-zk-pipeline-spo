package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.spo.dbobjects.CdRiskpremiumJPA;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.util.Formatter;
/**
 * пересчитывает процентные ставки по периоду.
 * @author Andrey Pavlenko
*/
public class RecalculatePercentRateAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        
        FactPercentJPA per = new FactPercentJPA();
        TaskJPA task = new TaskJPA();
        
        task.setRate5(Formatter.parseDouble(request.getParameter("rate5")));
        task.setRate6(Formatter.parseDouble(request.getParameter("rate6")));
        task.setRate7(Formatter.parseDouble(request.getParameter("rate7")));
        task.setRate8(Formatter.parseDouble(request.getParameter("rate8")));
        task.setRate9(Formatter.parseDouble(request.getParameter("rate9")));
        task.setRate10(Formatter.parseDouble(request.getParameter("rate10")));
        per.setRiskStepupFactorValue(Formatter.parseDouble(request.getParameter("riskStepupFactor")));
        per.setTask(task);
        per.setRate4(Formatter.parseDouble(request.getParameter("rate4")));
        per.setFondrate(Formatter.parseDouble(request.getParameter("fondrate")));
        per.setRiskpremium(Formatter.parseDouble(request.getParameter("riskpremium")));
        per.setRate3(Formatter.parseDouble(request.getParameter("rate3")));
        per.setRiskpremium_change(Formatter.parseDouble(request.getParameter("riskpremium_change")));
        String riskpremium_type = request.getParameter("riskpremium_type");
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        for(CdRiskpremiumJPA to: taskFacadeLocal.findCdRiskpremium()){
            if(to.getId().toString().equals(riskpremium_type))
                per.setRiskpremiumtype(to);
        }
        
        String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        ans += "<rates><id>"+id+"</id><effRate>"+Formatter.format(per.getCalcRate3())+"</effRate><calcRateProtected>"+
            Formatter.format(per.getCalcRate2())+
            "</calcRateProtected><calcRate>"+Formatter.format(per.getCalcRate1())+"</calcRate></rates>";
        response.getWriter().write(ans);
        logger.info("AJAX recalculatePercentRate.do answer: "+ans);
        return null;
    }
}
