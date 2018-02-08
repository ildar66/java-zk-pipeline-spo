package ru.md.controller;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.md.helper.TaskHelper;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.util.Formatter;
 
/**
 * контролер для старта экспертизы ПРР.
 */
@Controller
public class PRRController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PRRController.class);

	@RequestMapping(value = "/ajax/prrStart.html")
    public String prrStart(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("idProcess") Long idProcess,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("idProcess="+idProcess);
		try {
			Long userid = TaskHelper.getCurrentUser(request).getIdUser();
			PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			//model.addAttribute("msg","idProcess="+idProcess+". userid="+userid);
			model.addAttribute("msg",pupFacadeLocal.startPRR(userid, idProcess));
		} catch (Exception e) {
			String err = "Ошибка старта экспертизы ПРР: "+e.getMessage()+".";
			err +="время "+Formatter.formatDateTime(new java.util.Date());
			model.addAttribute("msg", err);
			LOGGER.error("prrStart error:"+e.getMessage(), e);
		}
		return "utf8";
	}
}
