package ru.md.servlet;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.domain.crm.Rating;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.flexworkflow.integration.list.ERatingType;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.funding.FundingService;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.md.spo.util.Config;

import com.vtb.domain.ApprovedRating;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;
/**
 * возвращает рейтинг организации.
 * @author Andrey Pavlenko
*/
public class RatingAjaxAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAjaxAction.class.getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        String org = request.getParameter("org");
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        Date ratingDate = new java.util.Date();
        RatingService ratingService = null;
        if(Config.enableIntegration())
        	ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
        CalcHistoryInput input = new CalcHistoryInput();
        input.setPartnerId(org);
        input.setRDate(ratingDate);
        CalcHistoryOutput output = null;
        try {
        	if(ratingService!=null)
        		output = ratingService.getKEKICalcHistory(input);
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}

        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
        format += "<ratingH><id>{4}</id><branch>{0}</branch><region>{1}</region>" +
        		"<ClientCategory>{2}</ClientCategory>{3}</ratingH>";
        String ratingFormat = "<rating><type>{0}</type><name>{1}</name><value>{2}</value><date>{3}</date></rating>";
        String branch = "";
        String region = "";
        
        String ans = "";
        if (output!=null) {
            branch = output.getBranch();
            region = output.getRegion();
            ans += MessageFormat.format(ratingFormat, "calculated", "Расчётный",
            		output.getRating()==null?"":output.getRating(),Formatter.format(output.getRDate()));
        }
        //утверждённый рейтинг коллегиального органа = getApprovedRating
        ApprovedRating ar = processor.getApprovedRating(ratingDate,org);
        if(ar!=null) {ans += MessageFormat.format(ratingFormat, "Approved","утверждённый",
                ar.getRating()==null?"":ar.getRating(),Formatter.format(ar.getDate()));}
        //экспертный рейтинг
        try {
        	 output = null;
        	if(ratingService!=null)
        		output = ratingService.getSEKZCalcHistory(input);
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
        if (output!=null){
            ans += MessageFormat.format(ratingFormat, "exp", "Экспертный",
            		output.getRating()==null?"":output.getRating(),Formatter.format(output.getRDate()));
        }
        
        ans = MessageFormat.format(format,branch==null?"":branch,region==null?"":region, 
        		compenduimCRM.findOrganization(org).getClientCategory(),ans,org);
        
        response.getWriter().write(ans);
        return null;
    }
}
