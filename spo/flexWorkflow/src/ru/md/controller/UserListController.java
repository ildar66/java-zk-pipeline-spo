package ru.md.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vtb.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import java.util.List;
import java.util.Set;

/**
 * контролер списков пользователей.
 */
@Controller
public class UserListController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserListController.class.getName());

	@RequestMapping(value = "/expertTeamList.html")
    public String expertTeam(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("mdtaskid") String mdtaskid,@RequestParam("expname") String expname,
    		HttpServletRequest request, HttpServletResponse responce) throws Exception {
		LOGGER.info("UserListController. mdtaskid="+mdtaskid);
        LOGGER.info("UserListController. expname="+expname);
        PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        model.addAttribute("users", pup.getUserExpertUser(expname, Long.valueOf(mdtaskid)));
        return "userList";
    }
	
	@RequestMapping(value = "/user_status.html")
    public String userStatus(@ModelAttribute("model") ModelMap model, 
    		HttpServletRequest request, HttpServletResponse responce) throws Exception {
		PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		UserJPA user = pup.getCurrentUser();
		model.addAttribute("msg", "{\"boss\":"+(user.isBoss(null)||user.isAdmin())+",\"worker\":"+user.isWorker()+"}");
		return "utf8";
	}

	public static final Set<String> expertteamRoles =
			CollectionUtils.set(
					"Руководитель подразделения по обеспечению безопасности",
					"Сотрудник подразделения по обеспечению безопасности",
					"Сотрудник юридического подразделения (банковские операции)",
					"Руководитель юридического подразделения (банковские операции)",
					"Сотрудник подразделения по анализу рисков",
					"Руководитель подразделения по анализу рисков",
					"Сотрудник подразделения внутреннего контроля",
					"Руководитель подразделения внутреннего контроля",
					"Сотрудник юридического подразделения (инвестиционные и финансовые операции)",
					"Руководитель юридического подразделения (инвестиционные и финансовые операции)",
					"Руководитель подразделения по анализу рыночных рисков",
					"Сотрудник подразделения по анализу рыночных рисков",
					"Сотрудник подразделения по работе с залогами",
					"Руководитель подразделения по работе с залогами",
					"Сотрудник Казначейства",
					"Руководитель Казначейства",
					"Руководитель подразделения целевых резервов",
					"Сотрудник подразделения целевых резервов",
					"Сотрудник Центральной бухгалтерии",
					"Руководитель Центральной бухгалтерии",
					"Работник мидл-офиса (тех. исполнимость)",
					"Руководитель мидл-офиса (тех. исполнимость)"
			);
	/**
	 * Можно ли показать список работа экспертного подразделения
	 * @param roles роли текущего пользователя во всех БП
	 */
	public static boolean isShowExpertteamList(List<String> roles){
		for(String role : roles)
			if(expertteamRoles.contains(role))
				return true;
		return false;
	}
}
