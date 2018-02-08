package ru.md.controller;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vtb.util.Formatter;

import ru.md.helper.TaskHelper;
 
/**
 * контролер считает ставку фондирования.
 */
@Controller
public class FondRateController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FondRateController.class.getName());

	@RequestMapping(value = "/ajax/graphPaymentFondRate.html") @ResponseBody
    public String graphPaymentFondRate(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("date1") String date1,@RequestParam("date2") String date2,
    		@RequestParam("cur") String cur,@RequestParam("id") String id,
    		@RequestParam("rateTypeFixed") String rateTypeFixed, @RequestParam("indRate") String indRate,
    		HttpServletRequest request, HttpServletResponse responce) throws Exception {
		LOGGER.info("graphPaymentFondRate. date1="+date1);
		LOGGER.info("graphPaymentFondRate. date2="+date2);
		LOGGER.info("graphPaymentFondRate. cur="+cur);
		LOGGER.info("graphPaymentFondRate. indRate="+indRate);
        LOGGER.info("graphPaymentFondRate. rateTypeFixed="+rateTypeFixed);
        Integer interval = getInterval(date1, date2);
        String stavbase = "0.0";
        if(rateTypeFixed.equalsIgnoreCase("true")){
        	stavbase = interval.intValue()==0?"":Formatter.format(TaskHelper.dict().findStavbase(cur, interval));
        } else {
        	//Если по сделке используется плавающая процентная ставка, то Ставка фондирования для каждого периода платежа 
        	//должна заполняться из справочника «Ставки фондирования в виде маржи к плавающей ставке» по хитрым условиям.
        	stavbase = Formatter.format(TaskHelper.dict().findStavarsmargin(cur, interval, indRate));
        }
        return ("{'interval':'"+interval.toString()+"','fondrate':'"+
        		stavbase+"','id':'"+id+"'}").replaceAll("'", "\"");
    }
	
	@RequestMapping(value = "/ajax/dataInterval.html") @ResponseBody
    public String dataInterval(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("date1") String date1,@RequestParam("date2") String date2,
    		HttpServletRequest request, HttpServletResponse responce) throws Exception {
		LOGGER.info("dataInterval. date1="+date1);
        LOGGER.info("dataInterval. date2="+date2);
        Integer interval = getInterval(date1, date2);
        return interval.toString();
    }

	public Integer getInterval(String date1, String date2) {
        try {
        	DateTime d1 = new DateTime(Formatter.parseDate(date1));
        	DateTime d2 = new DateTime(Formatter.parseDate(date2));
			Days days = Days.daysBetween(d1, d2);
			return days.getDays();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
        return 0;
	}
}
