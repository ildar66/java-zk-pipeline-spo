package ru.md.controller;

import java.io.IOException;
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

import ru.masterdm.spo.integration.FilialTaskList;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.FactoryException;
 
/**
 * контролер обработки переадресации для филиального портала.
 */
@Controller
public class ReturnToPortalController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReturnToPortalController.class.getName());

	@RequestMapping(value = "/returnToPortal.html")
    public String home(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("pupid") String pupid,
    		HttpServletRequest request, HttpServletResponse responce) throws FactoryException, IOException {
        LOGGER.info("ReturnToPortalController. pupid="+pupid);
        //закрыть операцию
        PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        List<TaskInfoJPA> tasks = pup.getTaskInWork(Long.valueOf(pupid));
        for(TaskInfoJPA task : tasks){
        	try {
				pup.reacceptWork(task.getIdTask(), task.getExecutor().getIdUser());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
			}
        }
        
        responce.sendRedirect(FilialTaskList.portalUrl);
        return "redirect:" + FilialTaskList.portalUrl;
    }
}
