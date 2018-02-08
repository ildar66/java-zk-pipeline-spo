package ru.md.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.ced.CedService;
import ru.masterdm.integration.ced.ws.CreditEnsuringDocFilter;
import ru.masterdm.integration.ced.ws.DealConclusion;
 
@Controller
public class CedController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CedController.class.getName());

	@RequestMapping(value = "/ajax/cedList.html")
    public String getCedList(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("mdtaskid") Long mdtaskid,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        LOGGER.info("getCedList. mdtaskid="+mdtaskid);
        CreditEnsuringDocFilter filter = new CreditEnsuringDocFilter();
        filter.setCountOnPage(30L);
        filter.setPageNumber(0L);
        filter.setCreditDealId(mdtaskid);
        try {
        	List<DealConclusion> list = ru.masterdm.integration.ServiceFactory.getService(CedService.class).getDealConclusions(filter);
        	if(list.size()==0){
        		model.addAttribute("msg", "По данной заявке нет запросов КОД ");
        		return "utf8";
        	}else{
	        	model.addAttribute("list", list);
	        	return "cedList";
        	}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			model.addAttribute("msg", "Ошибка получения запросов КОД: "+e.getMessage());
			return "utf8";
		}
    }
	
}
