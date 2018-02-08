/**
 * 
 */
package org.uit.director.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.decider.BusinessProcessDecider;
import org.uit.director.decider.NextStagesInfo;

import ru.md.pup.dbobjects.AcceptJPA;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.WorkflowTaskInfo;

/**
 * Назначение акцепта пользователю
 * 
 * @author imatushak@masterdm.ru
 * 
 */
public class AssignAcceptAction extends Action {

    private static final Logger LOGGER = Logger.getLogger(AssignAcceptAction.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.struts.action.Action#execute(org.apache.struts.action.
     * ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

//        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
//
//        String acceptAssigned = request.getParameter("assigned");
//
//        Boolean acceptRequired = false;
//
//        if (request.getAttribute("taskId") != null && (acceptAssigned == null || acceptAssigned.trim().length() == 0)) {
//            Long taskId = (Long) request.getAttribute("taskId");
//            WorkflowTaskInfo taskInfo = getWorkflowTaskInfo(taskId);
//            Long idProcessType = taskInfo.getIdTypeProcess().longValue();
//            List<UserJPA> parentList = getParentList(wsc.getIdUser(), idProcessType, taskId);
//            acceptRequired = isAcceptRequired(taskInfo.getIdProcess(), "акцепт операции")
//                    && (parentList != null && !parentList.isEmpty()) && !hasDepartmentManagementRole(wsc.getIdUser());
//        }
//
//        if (!acceptRequired && (acceptAssigned == null || acceptAssigned.trim().length() == 0)) {
            return getCompleteDialog(mapping, request);
//        }
//
//        if (acceptAssigned == null || acceptAssigned.trim().length() == 0) {
//            Long taskId = (Long) request.getAttribute("taskId");
//            Long idProcessType = getWorkflowTaskInfo(taskId).getIdTypeProcess().longValue();
//            request.setAttribute("taskId", taskId);
//            request.setAttribute("parentUserList", getParentList(wsc.getIdUser(), idProcessType, taskId));
//            return mapping.findForward("assignAcceptPage");
//        }
//
//        String taskId = request.getParameter("taskId");
//        String userId = request.getParameter("userId");
//        String approved = request.getParameter("approved");
//
//        if (approved != null) {
//            updateAcceptDate(Long.parseLong(taskId), wsc.getIdUser());
//        }
//
//        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
//        TaskInfoJPA task = pupFacadeLocal.getTask(Long.parseLong(taskId));
//        task.setIdStatus(9);
//        pupFacadeLocal.merge(task);
//
//        UserJPA user = pupFacadeLocal.getUser(Long.parseLong(userId));
//
//        AcceptJPA accept = new AcceptJPA();
//        accept.setInitDate(Calendar.getInstance().getTime());
//        accept.setTaskInfo(task);
//        accept.setUser(user);
//
//        pupFacadeLocal.persist(accept);
//
//        ActionForward forward = new ActionForward();
//        forward.setRedirect(true);
//        forward.setPath("showTaskList.do?typeList=accept");
//        return forward;
    }

    /**
     * Устанавливает дату акцепта
     */
    private void updateAcceptDate(Long taskId, Long userId) throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        List<AcceptJPA> acceptList = pupFacadeLocal.getAcceptList(taskId, userId);
        if (acceptList != null) {
            for (AcceptJPA accept : acceptList) {
                accept.setAcceptDate(Calendar.getInstance().getTime());
                pupFacadeLocal.merge(accept);
            }
        }
    }

    /**
     * Возвращает {@link ActionForward} на окно ошибки или подтверждения
     */
    private ActionForward getCompleteDialog(ActionMapping mapping, HttpServletRequest request) throws Exception {
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);

        String taskIdString = request.getParameter("id0");
        Long taskId = Long.parseLong(taskIdString);
        String sign = request.getParameter("sign0");

        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        List<AcceptJPA> acceptList = pupFacadeLocal.getAcceptList(taskId, wsc.getIdUser());
        if (acceptList != null) {
            for (AcceptJPA accept : acceptList) {
                accept.setAcceptDate(Calendar.getInstance().getTime());
                pupFacadeLocal.merge(accept);
            }
        }

        List<NextStagesInfo> stagesNextInfo = new ArrayList<NextStagesInfo>();
        
        Object[] resDefinNextStages = getNextStages(wsc, taskId, sign);
        String messageForCommit = "";
        if (resDefinNextStages[0] != null && (Integer) resDefinNextStages[0] == 0) {
            messageForCommit += resDefinNextStages[1];
            stagesNextInfo.add((NextStagesInfo) resDefinNextStages[2]);
        } else {
            return (mapping.findForward("errorPage"));
        }
        messageForCommit += "<br />Подтвердить действие?";
        request.getSession().setAttribute("objectFromAction", stagesNextInfo);
        LOGGER.info("stagesNextInfo.size(): " + stagesNextInfo.size());
        request.setAttribute("actionFrom", "task.complete.do");
        request.setAttribute("message", messageForCommit);
        request.setAttribute("idTask", taskId.toString());
        return mapping.findForward("commitPage");
    }

    /**
     * Возвращает {@link WorkflowTaskInfo информацию} по {@link Long
     * идентификатору} {@link TaskInfoJPA задачи}
     * 
     * @param taskId
     *            {@link Long идентификатору} {@link TaskInfoJPA задачи}
     * @return {@link WorkflowTaskInfo информацию} по {@link Long
     *         идентификатору} {@link TaskInfoJPA задачи}
     * @throws Exception
     *             ошибка
     */
    private WorkflowTaskInfo getWorkflowTaskInfo(Long taskId) throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        return pupFacadeLocal.getTaskInfo(taskId);
    }

    /*
     * Метод возвращает значение аттрибута бизнес-процесса
     */
    private Boolean isAcceptRequired(Long processId, String name) throws Exception {
        Boolean acceptRequired = null;
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        String value = pupFacadeLocal.getPUPAttributeValue(processId, name);
        try {
            acceptRequired = Boolean.parseBoolean(value);
        } catch (Exception e) {
            acceptRequired = false;
        }
        LOGGER.info("Process with id '" + processId + "' has value '" + acceptRequired + "' for attribute name '"
                + name + "'");
        // return acceptRequired;
        return true;
    }

    /*
     * Метод возвращает список начальников пользователя, которые не акцептовали
     * операцию
     */
    private List<UserJPA> getParentList(Long childUserId, Long idProcessType, Long taskId) throws Exception {
        List<UserJPA> userList = new ArrayList<UserJPA>();
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        UserJPA childUser = pupFacadeLocal.getUser(childUserId);
        Set<UserJPA> parentUserList = pupFacadeLocal.getParentList(childUserId, idProcessType);
        if (parentUserList != null) {
            parentUserList.remove(childUser);
            List<AcceptJPA> acceptList = pupFacadeLocal.getApprovedAcceptList(taskId);
            for (UserJPA parentUser : parentUserList) {
                if (acceptList == null || !acceptList.contains(parentUser)) {
                    userList.add(parentUser);
                }
            }
        }
        return userList;
    }

    /**
     * Метод возвращает {@link Boolean true} если у пользователя есть роль,
     * которая начинается на 'Руководитель управления' или {@link Boolean false}
     * если у пользователя нет такой роли
     * 
     * @param userId
     *            {@link Long идентификатор} {@link UserJPA пользователя}
     * @return {@link Boolean true} или {@link Boolean false} в зависимости от
     *         существования ролей у пользователя, коротые начинаются на
     *         'Руководитель управления'
     * @throws Exception
     *             ошибка
     */
    private Boolean hasDepartmentManagementRole(Long userId) throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        UserJPA user = pupFacadeLocal.getUser(userId);
        List<RoleJPA> roleList = user.getRoles();
        if (roleList != null) {
            for (RoleJPA role : roleList) {
                if (role.getNameRole().toLowerCase().startsWith("Руководитель управления".toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Получить список операций, которые будут выполняться после завершения этой
     * операции (реально вызывается
     * BusinessProcessDecider.getNextStageAfterComplation)
     */
    private Object[] getNextStages(WorkflowSessionContext wsc, Long idTask, String sign) throws Exception {

        Object[] res = new Object[3];

        if (sign == null) {
            try {
                sign = new String(wsc.getSignum());
                wsc.setSignum(null);
            } catch (Exception e) {
                sign = null;
                LOGGER.info("wsc.getSignum(): NullPointerException");
            }

        }
        if (sign == null || sign.equals("")) {
            if (Config.getProperty("VALIDATE_SIGNUM").equalsIgnoreCase("true")) {
                wsc.setErrorMessage("Действие не подписано, операция не может быть выполнена");
                res[0] = 1;
                return res;
            } else {
                sign = "";
            }
        }

        NextStagesInfo nextStagesInfo = BusinessProcessDecider.getNextStageAfterComplation(wsc, idTask, sign);

        // Проверим условия, может ли завершиться сама операция. И заполним поля
        // возвращаемой структуры res
        // задание может завершиться
        if (nextStagesInfo.getResult() == 0) {
            // stagesNextInfo.add(nextStagesInfo);
            res[0] = 0;
            res[1] = nextStagesInfo.getMessage();
            res[2] = nextStagesInfo;
            // messageForCommit += nextStagesInfo.getMessage();

        } else {
            // задание не может завершиться
            wsc.setErrorMessage(nextStagesInfo.getMessage());
            res[0] = 1;
            return res;
        }
        return res;
    }

}
