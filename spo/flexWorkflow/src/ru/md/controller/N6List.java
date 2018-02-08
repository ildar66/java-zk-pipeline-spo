package ru.md.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.funding.FundingService;
import ru.masterdm.integration.funding.ws.FundingRequestFilter;
import ru.masterdm.integration.funding.ws.N6Request;
import ru.md.spo.util.Config;

import freemarker.template.Configuration;

/**
 * Контроллер для списка заявок на фондирования и на Н6.
 * @author drone
 *
 */
public class N6List extends Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(N6List.class.getName());
	/**
	 * имеет ли заявка связанные заявки на фондирование?
	 */
	public static boolean isHasFunds(Long mdtaskid){
		if(Config.getProperty("funding_integration_enable").equalsIgnoreCase("false"))//файл workflow.properties
			return false;
		long tstart = System.currentTimeMillis();
		try {
			FundingRequestFilter filter = new FundingRequestFilter();
			filter.setMdTaskId(mdtaskid);
			
			Long total_count = new Long(ru.masterdm.integration.ServiceFactory.getService(FundingService.class).getN6Requests(filter).size());
			Long loadTime = System.currentTimeMillis()-tstart;
			LOGGER.warn("*** total isHasFunds() time"+loadTime);
			return total_count > 0;
		} catch (Throwable e) {
			//LOGGER.error(e.getMessage(), e);
			Long loadTime = System.currentTimeMillis()-tstart;
			LOGGER.warn("*** total isHasFunds() time"+loadTime);
			return false;
		}
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        StringWriter ans = new StringWriter();
        Configuration cfg = new Configuration();//Freemarker configuration object
		cfg.setClassForTemplateLoading(this.getClass(),"/ftl/");
		Map<String, Object> data = gatData(Long.valueOf(request.getParameter("mdtaskid")));
		cfg.getTemplate("n6List.ftl","utf-8").process(data, ans);
        response.getWriter().write(ans.toString());
        return null;
    }

	private Map<String, Object> gatData(Long idmdtask) {
		Map<String, Object> data = new java.util.HashMap<String, Object>();
		List<N6Request> funds = new ArrayList<N6Request>();
		if(Config.getProperty("funding_integration_enable").equalsIgnoreCase("false")){//файл workflow.properties
			data.put("funds", funds);
			return data;
		}

		try {
			FundingRequestFilter filter = new FundingRequestFilter();
			filter.setMdTaskId(idmdtask);
			funds = ServiceFactory.getService(FundingService.class).getN6Requests(filter);
		} catch (Throwable e) {
			//LOGGER.error(e.getMessage(), e);
		}
		data.put("funds", funds);
		return data;
	}
}
