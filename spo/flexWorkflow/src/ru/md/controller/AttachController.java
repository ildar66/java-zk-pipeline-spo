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
import org.springframework.web.bind.annotation.ResponseBody;

import com.vtb.util.Formatter;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.compendium.CompendiumService;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
 
/**
 * контролер для манипулирования с прикрепленными файлами.
 */
@Controller
public class AttachController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

	@RequestMapping(value = "/ajax/editAttach.html") @ResponseBody
    public String editAttach(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("unid") String unid,@RequestParam("title") String title,@RequestParam("exp") String exp,
    		@RequestParam("doctype") String doctype,@RequestParam("group") String group,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        LOGGER.info("editAttach. unid="+unid);
        LOGGER.info("editAttach. title="+title);
		LOGGER.info("editAttach. doctype="+doctype);
		//TODO проверить права, когда Женя согласует требования
		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		AttachJPA a = pupFacadeLocal.getAttachemnt(unid);
		a.setTitle(title);
		a.setDATE_OF_EXPIRATION(Formatter.parseDate(exp));
		if(doctype!=null && !doctype.isEmpty())
			try {
				a.setDocumentType(pupFacadeLocal.getDocumentType(Long.valueOf(doctype)));
				try {
					a.setGroup(pupFacadeLocal.getDocumentGroup(Long.valueOf(group)));
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		pupFacadeLocal.merge(a);
        return "OK";
    }

	@RequestMapping(value = "/ajax/docEdcUp.html")
    public String docEdcUp(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("doctype") String doctype,@RequestParam("ownerid") String ownerid,@RequestParam("auto") String auto,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("doctype="+doctype);//
		try {
			Long userid = TaskHelper.getCurrentUser(request).getIdUser();
			if(doctype.equals("0")){
				TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
				TaskJPA task = ownerid.startsWith("mdtaskid")?taskFacade.getTask(Long.valueOf(ownerid.replaceAll("mdtaskid", ""))):taskFacade.getTaskByPupID(Long.valueOf(ownerid));
				OrgJPA org = task.getOrgList().get(0);
				if(org.getOgrn().isEmpty()){
					model.addAttribute("msg", "Нет ОГРН основного заемщика "+org.getOrganizationName());
					return "utf8";
				}
				Long pupid = task.getProcess()==null?null:task.getProcess().getId();
				LOGGER.info("I'm going to call CompendiumService.updateFileLinksForDeal("+task.getMdtask_number()+", "+pupid+", "+ 
						org.getOgrn()+", "+auto.equalsIgnoreCase("true")+");");
				ru.masterdm.integration.ServiceFactory.getService(CompendiumService.class).updateFileLinksForDeal(task.getMdtask_number(), pupid,
						org.getOgrn(), auto.equalsIgnoreCase("true"),userid);
			}
			if(doctype.equals("1")){
				OrgJPA org = TaskHelper.dict().getOrg(ownerid);
				if(org.getOgrn().isEmpty()){
					model.addAttribute("msg", "Нет ОГРН контрагента "+org.getOrganizationName());
					return "utf8";
				}
				ru.masterdm.integration.ServiceFactory.getService(CompendiumService.class).updateFileLinksForContractor(ownerid, org.getOgrn(),
						auto.equalsIgnoreCase("true"),userid);
			}
			if(doctype.equals("2")){
				model.addAttribute("msg", "не реализовано для физлиц");
				return "utf8";
			}
			model.addAttribute("msg", "OK");
		} catch (Exception e) {
			String err = "<b>ЭДК недоступно.</b>";
			err +=" время "+Formatter.formatDateTime(new java.util.Date());
			/*String err = "<b>Ошибка обновления ЭДК: "+e.getMessage()+"</b>";
			err +="<br />время "+Formatter.formatDateTime(new java.util.Date());*/
			/*for (StackTraceElement el : e.getStackTrace())
				err += "<br />"+el.toString();*/
			model.addAttribute("msg", err);
			LOGGER.error("docEdcUp error:"+e.getMessage(), e);
		}
		return "utf8";
	}
}
