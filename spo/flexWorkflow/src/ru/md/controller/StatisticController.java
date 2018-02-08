package ru.md.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.FactoryException;
 
/**
 * Выводит статистику по СПО.
 */
@Controller
public class StatisticController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class.getName());

	@RequestMapping(value = "/statistic.html")
    public String home(@ModelAttribute("model") ModelMap model) throws FactoryException {
        LOGGER.info("StatisticController");
        PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        model.addAttribute("statistic", pup.getStatistic());
        return "statistic";
    }
}
