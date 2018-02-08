package ru.md.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vtb.exception.FactoryException;

import ru.masterdm.flexworkflow.logic.ejb.IFlexWorkflowIntegrationLocal;
import ru.masterdm.spo.integration.FilialTaskListFilter;
 
/**
 * тестовая страница для филиальных заявок.
 */
@Controller
public class FilialTaskListController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilialTaskListController.class.getName());

	@RequestMapping(value = "/filialTaskList.html")
    public String home(@ModelAttribute("model") ModelMap model) throws FactoryException {
        LOGGER.info("FilialTaskListController");
        IFlexWorkflowIntegrationLocal bean = com.vtb.util.EjbLocator.getInstance().getReference(IFlexWorkflowIntegrationLocal.class);
        FilialTaskListFilter filter = new FilialTaskListFilter();
        filter.setPageNum(0L);
        filter.setPageSize(15L);
        filter.setTaskNumber("203032");
        //filter.setSumFrom(2.0);
        //filter.setCur("EUR");
        //filter.setOrgName("ламак");
        filter.setHideClosed(false);
        model.addAttribute("res", bean.getFilialTaskList(filter));
        return "filialTaskList";
    }
}
