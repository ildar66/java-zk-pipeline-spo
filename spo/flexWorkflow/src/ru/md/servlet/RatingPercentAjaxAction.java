package ru.md.servlet;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryWso;
import ru.md.spo.util.Config;

import com.vtb.util.Formatter;

/**
 * возвращает расчетные стоимостные условия для периода.
 * @author Andrey Pavlenko
*/
public class RatingPercentAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setCharacterEncoding("UTF-8");
        String ans = "";
        Long idMdtask=Formatter.parseLong(request.getParameter("mdtaskid"));
        Date ratingDate = new Date();
        Long isRateTypeFixed = 1L;
        
        CalcHistoryWso calcHistory = null;
        if(Config.enableIntegration()){
        	RatingService ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
        	calcHistory = ratingService.getCalcHistoryBySdelkaId(idMdtask, isRateTypeFixed, ratingDate);
        }

        if (calcHistory == null) {
            {response.getWriter().write("error getting task for sdelkaId = '" + idMdtask
                    + "' rDate = '" + ratingDate + "' tipStavki = '" + isRateTypeFixed + "'");return null;}
        }

        response.getWriter().write(ans);
        logger.info("AJAX ratingPercent.do answer: "+ans);
        return null;
    }
}
