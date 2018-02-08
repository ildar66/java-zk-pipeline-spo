package org.uit.director.action;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.servletutil.SessionListener;

import ru.masterdm.spo.utils.SBeanLocator;

import ru.md.domain.User;
import ru.md.persistence.UserMapper;

public class AbstractAction {

    private final static Logger logger = Logger.getLogger(AbstractAction.class.getName());
    public static final String DEFAULT_REMOTE_USER = "adminwf";

    public static synchronized WorkflowSessionContext getWorkflowSessionContext(HttpServletRequest request) {
        HttpSession session = request.getSession();

        WorkflowSessionContext wsc = (WorkflowSessionContext) session.getAttribute("workflowContext");

        if (wsc == null || !wsc.isCorrectContext()) {
            String userName = getUserLogin(request);

            logger.info("authentificated user: " + userName);

            wsc = new WorkflowSessionContext(userName);
            session.setAttribute("workflowContext", wsc);
            session.setAttribute("listener", new SessionListener());

        } else {
            wsc.setNewContext(false);
        }
        return wsc;
    }

    public static User getUser(HttpServletRequest request) {
        String remoteUser = request.getRemoteUser();
        if (remoteUser == null)
            remoteUser = DEFAULT_REMOTE_USER;
        UserMapper userMapper = (UserMapper) SBeanLocator.singleton().getBean("userMapper");
        User user = userMapper.getUserByLogin(remoteUser);
        return user;
    }

    public static Long getUserId(HttpServletRequest request) {
        User user = getUser(request);
        if (user == null)
            return null;
        return user.getId();
    }

    public static String getUserLogin(HttpServletRequest request) {
        User user = getUser(request);
        if (user == null)
            return DEFAULT_REMOTE_USER;
        String mainLogin = user.getLogin().toLowerCase();
        return mainLogin;
    }

    public static synchronized void resetWorkflowSessionContext(HttpServletRequest request) {
        request.getSession().removeAttribute("workflowContext");
    }
}
