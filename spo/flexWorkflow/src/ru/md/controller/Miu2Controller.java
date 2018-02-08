package ru.md.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

import javax.servlet.http.HttpServletRequest;

@Controller
public class Miu2Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Miu2Controller.class);

	@RequestMapping(value = "/ajax/refuse_miu2.html")
    public String home(@ModelAttribute("model") ModelMap model,
					   @RequestParam("mdtaskid") Long mdTaskId,HttpServletRequest request) throws Exception {
		LOGGER.info("Miu2Controller");
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA taskJPA = taskFacadeLocal.getTask(mdTaskId);
		taskJPA.setMonitoringMode("Редактирование ставки");
		taskJPA.setMonitoringUserWorkId(null);
		taskFacadeLocal.merge(taskJPA);
		try {
			//при Акцепте и Отказе ты из этой таблицы по ID_MDTASK удаляешь записи
			taskFacadeLocal.clearKmDealPercentState(taskJPA.getId());
		} catch (Exception e){}

		model.addAttribute("msg", "OK");
        return "utf8";
    }
}
