package ru.md.controller;


import com.vtb.domain.ProcessSearchParam;
import com.vtb.exception.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * контролер перехода по кнопке "Вернуться к списку заявок" к странице с корректным номером страницы в пагинации.
 * По кнопке "Вернуться к списку заявок" переход должен быть к той странице в пагинации на которой теперь оказалась
 * заявка/операция. Если брали в работу из "Ожидающих в обработку", то возвращаемся в "Операции в работе" на страницу,
 * где эта операция.
 *
 * @author akirilchev@masterdm.ru
 */
@Controller
public class ReturnToTaskListController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnToTaskListController.class.getName());

    private static final String SEARCHPAGE_NUMBER_COOKIE_NAME = "searchpageNumber";
    private static final String TYPE_LIST_COOKIE_NAME = "typeList";

    @RequestMapping(value = "/returnToTaskList.html")
    public String goBack(@ModelAttribute("model") ModelMap model,
                         @RequestParam(value = "strutsAction", required = true) String strutsAction,
                         @RequestParam(value = "idPupProcess", required = true) Long idPupProcess,
                         @RequestParam(value = "typeList", required = true) String typeList,
                         @RequestParam(value = "idTask", required = false) Long idTask,
                         @RequestParam(value = "projectteam", required = false) String projectteam,
                         @RequestParam(value = "expertteam", required = false) String expertteam,
                         @RequestParam(value = "favorite", required = false) String favorite,
                         @RequestParam(value = "paused", required = false) String paused,
                         @RequestParam(value = "closed", required = false) String closed,
                         @RequestParam(value = "department", required = false) Long department,
                         @RequestParam(value = "idDepartment", required = false) Long idDepartment,
                         HttpServletRequest request,
                         HttpServletResponse response) throws FactoryException, IOException {
        try {
            String viewtypeParam = (projectteam != null && !projectteam.isEmpty()) ? "&projectteam=true" : "";
            String favoriteParam = (favorite != null && !favorite.isEmpty()) ? "&favorite=true" : "";
            String pausedParam = (paused != null && !paused.isEmpty()) ? "&paused=true" : "";
            String closedParam = (closed != null && !closed.isEmpty()) ? "&closed=true" : "";
            String expertteamParam = (expertteam != null && !expertteam.isEmpty()) ? "&expertteam=true" : "";
            String idDepartmentParam = (idDepartment != null) ? "&idDepartment=" + idDepartment : "";
            String departmentParam = (department != null) ? "&department=" + department : "";

            UserJPA currentUser = TaskHelper.getCurrentUser(request);
            Long idCurrentUser = currentUser.getIdUser();
            Long idCurrentUserDepartment = currentUser.getDepartment().getIdDepartment();

            LOGGER.trace("=========ReturnToTaskListController.goBack start idPupProcess '" + idPupProcess + "', idTask '"
                    + idTask + "', idCurrentUser '" + idCurrentUser + "', idCurrentUserDepartment '" + idCurrentUserDepartment
                    + "', typeList '" + typeList + "', viewtypeParam '" + viewtypeParam + "', pausedParam '" + pausedParam
                    + "', idDepartmentParam '" + idDepartmentParam + "', departmentParam '" + departmentParam + "', closedParam '" + closedParam + "'");

            ProcessSearchParam processSearchParam = new ProcessSearchParam(request, closed != null);
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);

            Long queryPageNumber = pupFacadeLocal.getQueryPageNumber(idCurrentUser, idCurrentUserDepartment.intValue(),
                    processSearchParam, idPupProcess, idTask, typeList);
            if (queryPageNumber == null)
                queryPageNumber = 0L;

            String typeListCookie = typeList;
            if (closed != null)
                typeListCookie = typeList + "closed";
            response.addCookie(TaskHelper.prepareCookie(SEARCHPAGE_NUMBER_COOKIE_NAME, String.valueOf(queryPageNumber)));
            response.addCookie(TaskHelper.prepareCookie(TYPE_LIST_COOKIE_NAME, typeListCookie));

            StringBuilder sb = new StringBuilder();
            sb.append(strutsAction).append(".do?typeList=").append(typeList).append(viewtypeParam).append(pausedParam)
                    .append(departmentParam).append(closedParam).append(favoriteParam)
                    .append(expertteamParam);
            if (expertteam == null || expertteam.trim().isEmpty())
                sb.append(idDepartmentParam);
            String path = sb.toString();

            LOGGER.trace("=========ReturnToTaskListController.goBack end idPupProcess '" + idPupProcess + "', idTask '"
                    + idTask + "', typeList '" + typeList + "', queryPageNumber '" + queryPageNumber + "', path '" + path + "'");

            return "redirect:" + path;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }
}
